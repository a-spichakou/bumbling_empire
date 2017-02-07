package bot.verter.service.impl;

import bot.verter.model.ConversationContext;
import bot.verter.model.Question;
import bot.verter.service.SceneController;
import bot.verter.service.factory.ServiceFactory;
import it.uniroma1.lcl.adw.ADW;
import it.uniroma1.lcl.adw.DisambiguationMethod;
import it.uniroma1.lcl.adw.ItemType;
import it.uniroma1.lcl.adw.comparison.SignatureComparison;
import it.uniroma1.lcl.adw.comparison.WeightedOverlap;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ThemeQuestionAnswerController implements SceneController {

    private static final String PATH_TO_QA = "d:/hackathon/bot/resources/qa/";

    private static final String QUESTIONS_SUFFIX = ".questions";

    private static final String ANSWERS_SUFFIX = ".answers";

    private static final double SIMILARITY_FACTOR = 0.9999999;

    private static final double MINIMAL_SIMILARITY_FOR_JOIN_THEME = 0.85;

    private static final double MINIMAL_SIMILARITY_FOR_EXIT_THEME = 0.75;

    @Override
    public boolean enter(ConversationContext context) {
        return true;
    }

    @Override
    public SceneController getNextController() {
        return null;
    }

    @Override
    public String talkBack(ConversationContext context) throws IOException {
        return searchAnswer(context);
    }

    @Override
    public int confidence() {
        return 0;
    }

    private String searchAnswer(ConversationContext context) throws IOException {
        Question question;

        if (context.getTheme() != null) {
            question = searchQuestion(context.getTheme(), context.getSentence());
            if (question.getSimilarity() < MINIMAL_SIMILARITY_FOR_EXIT_THEME) {
                question = searchQuestionInAllTheme(context);
            }
        }
        else {
            question = searchQuestionInAllTheme(context);
        }

        System.out.println("Theme: " + question.getTheme() +
                ". Similarity: " + question.getSimilarity() +
                ". Idx: " + question.getIndex() +
                ". Question: " + question.getText());

        return readAnswer(question);
    }

    private Question searchQuestionInAllTheme(ConversationContext context) throws IOException {
        Question question = new Question();

        File path = new File(PATH_TO_QA);
        for (File f : path.listFiles()) {
            if (f.isFile() && f.getCanonicalPath().endsWith(QUESTIONS_SUFFIX)) {
                String theme = FilenameUtils.getBaseName(f.getName());
                Question questionCandidate = searchQuestion(theme, context.getSentence());
                if (questionCandidate.getSimilarity() > question.getSimilarity()) {
                    question = questionCandidate;
                }
            }
            if (question.getSimilarity() >= SIMILARITY_FACTOR) {
                break;
            }
        }

        if (question.getSimilarity() > MINIMAL_SIMILARITY_FOR_JOIN_THEME) {
            context.setTheme(question.getTheme());
        }

        return question;
    }

    private Question searchQuestion(String theme, String sentence) throws IOException {
        Question question = new Question();
        question.setTheme(theme);

        int idx = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(PATH_TO_QA + theme + QUESTIONS_SUFFIX))) {
            String line = br.readLine();

            while (line != null) {
                double similarity = calculateSimilarity(sentence, line);
                if (similarity > question.getSimilarity()) {
                    question.setSimilarity(similarity);
                    question.setIndex(idx);
                    question.setText(line);
                    if (similarity >= SIMILARITY_FACTOR) {
                        break;
                    }
                }
                idx++;
                line = br.readLine();
            }
        }
        return question;
    }

    private String readAnswer(Question question) throws IOException {
        String line;
        int idx = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(PATH_TO_QA + question.getTheme() + ANSWERS_SUFFIX))) {
            line = br.readLine();

            while (line != null) {
                if (idx == question.getIndex()) {
                    return line;
                }
                idx++;
                line = br.readLine();
            }
        }
        return null;
    }

    private double calculateSimilarity(String sentence, String question) {
        //types of the two lexical items
        ItemType srcTextType = ItemType.SURFACE;
        ItemType trgTextType = ItemType.SURFACE;

        //if lexical items has to be disambiguated
        DisambiguationMethod disMethod = DisambiguationMethod.ALIGNMENT_BASED;

        //measure for comparing semantic signatures
        SignatureComparison measure = new WeightedOverlap();

        final ADW adw = ServiceFactory.getInstance().getADW();
        return adw.getPairSimilarity(sentence, question,
                disMethod, measure,
                srcTextType, trgTextType);
    }
}