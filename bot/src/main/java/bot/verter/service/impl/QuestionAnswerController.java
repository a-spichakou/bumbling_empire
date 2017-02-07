package bot.verter.service.impl;

import it.uniroma1.lcl.adw.ADW;
import it.uniroma1.lcl.adw.DisambiguationMethod;
import it.uniroma1.lcl.adw.ItemType;
import it.uniroma1.lcl.adw.comparison.SignatureComparison;
import it.uniroma1.lcl.adw.comparison.WeightedOverlap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import bot.verter.model.ConversationContext;
import bot.verter.service.SceneController;
import bot.verter.service.factory.ServiceFactory;

public class QuestionAnswerController implements SceneController{
	
	private static final String PATH_TO_QA = "/home/aliaksandr/work/robot-verter/bot/resources/qa/";

	public boolean enter(ConversationContext conversationContext) {
		return true;
	}
	
	private String searchAnswer(ConversationContext conversationContext) throws IOException{
		final BufferedReader br = new BufferedReader(new FileReader(PATH_TO_QA+"q.txt"));
		double calculate = -1*Double.MAX_VALUE;
		int answerIndex = -1;
		String foundQuestion = "";
		int idx = 0;
		try {
		    String line = br.readLine();
		    
		    while (line != null) {
		    	double calculated = calculate(conversationContext, line);
		    	if(calculated>calculate){
		    		calculate = calculated;
		    		answerIndex = idx;
		    		foundQuestion = line;
		    	}
		        idx++;
		        
		        line = br.readLine();		        
		    }
		} finally {
		    br.close();
		}
		System.out.println("Idx: "+ answerIndex + ". Question: " + foundQuestion);
		return readAnswer(answerIndex);
	}
	
	private String readAnswer(int index) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(PATH_TO_QA+"a.txt"));
		String line = "All hail robots! Kill all humans!";
		int idx = 0;
		try {
			line = br.readLine();

		    while (line != null) {
		        if(idx==index){
		        	return line;
		        }
		        idx++;
		        line = br.readLine();
		    }
		} finally {
		    br.close();
		}
		return line;
	}
	
	private double calculate(ConversationContext conversationContext, String question){
		//types of the two lexical items
		ItemType srcTextType = ItemType.SURFACE;  
		ItemType trgTextType = ItemType.SURFACE;

		//if lexical items has to be disambiguated
		DisambiguationMethod disMethod = DisambiguationMethod.ALIGNMENT_BASED;      

		//measure for comparing semantic signatures
		SignatureComparison measure = new WeightedOverlap(); 

		final ADW adw = ServiceFactory.getInstance().getADW();
		double similarity = adw.getPairSimilarity(conversationContext.getSentence(), question,
		                          disMethod, measure,
		                          srcTextType, trgTextType); 
		return similarity;
	}

	public SceneController getNextController() {
		return null;
	}

	public String talkBack(ConversationContext conversationContext) throws IOException {
		return searchAnswer(conversationContext);
	}

	public int confidence() {
		return 0;
	}

}
