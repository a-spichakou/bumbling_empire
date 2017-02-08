package bot.verter.service.impl;

import bot.verter.model.ConversationContext;
import bot.verter.model.SearchResult;
import bot.verter.service.SceneController;
import bot.verter.service.factory.ServiceFactory;
import bot.verter.storage.Index;
import bot.verter.storage.StorageFactory;
import it.uniroma1.lcl.adw.ADW;
import it.uniroma1.lcl.adw.DisambiguationMethod;
import it.uniroma1.lcl.adw.ItemType;
import it.uniroma1.lcl.adw.comparison.SignatureComparison;
import it.uniroma1.lcl.adw.comparison.WeightedOverlap;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ThemeQuestionAnswerController implements SceneController {

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
        SearchResult result;

        long start = System.nanoTime();

        if (context.getTheme() != null) {
            result = find(context.getTheme(), context.getSentence());
            if (result.getSimilarity() < MINIMAL_SIMILARITY_FOR_EXIT_THEME) {
                result = searchQuestionInAllTheme(context);
            }
        }
        else {
            result = searchQuestionInAllTheme(context);
        }

        long end = System.nanoTime();

       /* System.out.println("Time: " + (end - start) / 1000000L + " ms" +
                ". Theme: " + context.getTheme() +
                ". Similarity: " + result.getSimilarity());*/

        double minimalAcceptableSimilarity = 0.5;
        if (result.getSimilarity() < minimalAcceptableSimilarity) {
            return "Would you please rephrase your question? I want to make sure I understand you correctly.";
        }
        else {
            return result.getAnswer();
        }
    }

    private SearchResult searchQuestionInAllTheme(ConversationContext context) throws IOException {
        SearchResult result = new SearchResult("", -1);
        Map<String, List<Index>> storage = StorageFactory.getInstance().getStorage();
        String theme = "";
        for (Map.Entry<String, List<Index>> entry : storage.entrySet()) {
            theme = entry.getKey();
            SearchResult temp = find(theme, context.getSentence());
            if (temp.getSimilarity() > result.getSimilarity()) {
                result = temp;
            }
            if (result.getSimilarity() > SIMILARITY_FACTOR) {
                break;
            }
        }

        if (result.getSimilarity() > MINIMAL_SIMILARITY_FOR_JOIN_THEME) {
            context.setTheme(theme);
        }

        return result;
    }

    private SearchResult find(String theme, String sentence) {

        List<Index> indexes = StorageFactory.getInstance().retrieveIndexInTheme(theme);

        //indexes.stream() to disable concurrency
        return indexes.stream()
                .map(index -> new SearchResult(index.getAnswer(), calculateSimilarity(sentence, index.getQuestion())))
                .max(Comparator.comparing(SearchResult::getSimilarity)).orElse(new SearchResult("", -1));
    }

    private double calculateSimilarity(String sentence, String question) {
        //types of the two lexical items
        ItemType srcTextType = ItemType.SURFACE;
        ItemType trgTextType = ItemType.SURFACE;

        //if lexical items has to be disambiguated
        DisambiguationMethod disMethod = DisambiguationMethod.ALIGNMENT_BASED;

        //measure for comparing semantic signatures
        SignatureComparison measure = new WeightedOverlap();

        final ADW adw = new ADW();
        return adw.getPairSimilarity(sentence, question,
                disMethod, measure,
                srcTextType, trgTextType);
    }
}