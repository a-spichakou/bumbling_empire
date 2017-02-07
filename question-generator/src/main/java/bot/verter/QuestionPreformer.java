package bot.verter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.cmu.ark.AnalysisUtilities;
import edu.cmu.ark.GlobalProperties;
import edu.cmu.ark.InitialTransformationStep;
import edu.cmu.ark.Question;
import edu.cmu.ark.QuestionRanker;
import edu.cmu.ark.QuestionTransducer;
import edu.stanford.nlp.trees.Tree;

public class QuestionPreformer {
	private String modelPath = "models/linear-regression-ranker-reg500.ser.gz";
	
	public void preform(String givenArticle, String questionFile, String answerFile){
		Path questionFilePath = Paths.get(questionFile);
		Path answerFilePath = Paths.get(answerFile);
				
		//-------------------------
		
		QuestionTransducer qt = new QuestionTransducer();
		InitialTransformationStep trans = new InitialTransformationStep();
		QuestionRanker qr = null;
		
		
		qt.setAvoidPronounsAndDemonstratives(false);
		
		//pre-load
		AnalysisUtilities.getInstance();
		
		String buf;
		Tree parsed;
		boolean printVerbose = false;
		
		
		List<Question> outputQuestionList = new ArrayList<Question>();
		boolean preferWH = true;
		boolean doNonPronounNPC = false;
		boolean doPronounNPC = true;
		Integer maxLength = 30;
		boolean downweightPronouns = true;
		boolean avoidFreqWords = false;
		boolean dropPro = true;
		boolean justWH = false;
		
		
		qt.setAvoidPronounsAndDemonstratives(dropPro);
		trans.setDoPronounNPC(doPronounNPC);
		trans.setDoNonPronounNPC(doNonPronounNPC);
		
		if(modelPath != null){
			System.err.println("Loading question ranking models from "+modelPath+"...");
			qr = new QuestionRanker();
			qr.loadModel(modelPath);
		}
		
		try{
			int length = 6000;
			givenArticle = givenArticle.substring(0, Math.min(givenArticle.length(), length));
			Files.write(Paths.get(questionFile + ".trimmed"), givenArticle.getBytes());
			long startTime = System.currentTimeMillis();
				List<String> sentences = AnalysisUtilities.getSentences(givenArticle);
				
				//iterate over each segmented sentence and generate questions
				List<Tree> inputTrees = new ArrayList<Tree>();
				
				for(String sentence: sentences){
					if(GlobalProperties.getDebug()) System.err.println("Question Asker: sentence: "+sentence);
					
					parsed = AnalysisUtilities.getInstance().parseSentence(sentence).parse;
					inputTrees.add(parsed);
				}
				
				if(GlobalProperties.getDebug()) System.err.println("Seconds Elapsed Parsing:\t"+((System.currentTimeMillis()-startTime)/1000.0));
				
				//step 1 transformations
				List<Question> transformationOutput = trans.transform(inputTrees);
				
				//step 2 question transducer
				for(Question t: transformationOutput){
					if(GlobalProperties.getDebug()) System.err.println("Stage 2 Input: "+t.getIntermediateTree().yield().toString());
					qt.generateQuestionsFromParse(t);
					outputQuestionList.addAll(qt.getQuestions());
				}			
				
				//remove duplicates
				QuestionTransducer.removeDuplicateQuestions(outputQuestionList);
				
				//step 3 ranking
				if(qr != null){
					qr.scoreGivenQuestions(outputQuestionList);
					boolean doStemming = true;
					QuestionRanker.adjustScores(outputQuestionList, inputTrees, avoidFreqWords, preferWH, downweightPronouns, doStemming);
					QuestionRanker.sortQuestions(outputQuestionList, false);
				}

				//now print the questions
				//double featureValue;
				List<String> questions = new LinkedList<>();
				List<String> answers = new LinkedList<>();
				for(Question question: outputQuestionList){
					if(question.getTree().getLeaves().size() > maxLength){
						continue;
					}
					if(justWH && question.getFeatureValue("whQuestion") != 1.0){
						continue;
					}
					String questionString = question.yield();
					questions.add(questionString);
					System.out.print(questionString);
					String answerString = AnalysisUtilities.getCleanedUpYield(question.getSourceTree());
					answers.add(answerString);
					if(printVerbose) {
						System.out.print("\t"+ answerString);
					}
					Tree ansTree = question.getAnswerPhraseTree();
					if(printVerbose) System.out.print("\t");
					if(ansTree != null){
						if(printVerbose) System.out.print(AnalysisUtilities.getCleanedUpYield(question.getAnswerPhraseTree()));
					}
					if(printVerbose) System.out.print("\t"+question.getScore());
					//System.err.println("Answer depth: "+question.getFeatureValue("answerDepth"));
					
					System.out.println();
				}

				System.out.println("Seconds Elapsed Total:\t" + ((System.currentTimeMillis() - startTime) / 1000.0));
				//prompt for another piece of input text
				if(GlobalProperties.getDebug()) System.err.println("\nInput Text:");

				Files.write(questionFilePath, questions);
				Files.write(answerFilePath, answers);
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}

}
