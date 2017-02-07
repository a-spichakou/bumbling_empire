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
                result = searchQuestionInAllTheme(context, context.getTheme());
            }
        }
        else {
            result = searchQuestionInAllTheme(context, null);
        }

        long end = System.nanoTime();
        System.out.println("Took : " + (end - start) / 1000000L + ", ms");

        return result.getAnswer();
    }

    private SearchResult searchQuestionInAllTheme(ConversationContext context, String skip) throws IOException {
        SearchResult result = new SearchResult("", -1);
        Map<String, List<Index>> storage = StorageFactory.getInstance().getStorage();
        String theme = "";
        for (Map.Entry<String, List<Index>> entry : storage.entrySet()) {
            if (entry.getKey().equals(skip)) {
                continue;
            }
            theme = entry.getKey();
            SearchResult temp = find(theme, context.getSentence());
            if (temp.getSimilarity() > SIMILARITY_FACTOR) {
                result = temp;
                break;
            }
        }

        if (result.getSimilarity() > MINIMAL_SIMILARITY_FOR_JOIN_THEME) {
            context.setTheme(theme);
        }

        return result;
    }

    private SearchResult find(String theme, String sentence) {
        Map<String, List<Index>> storage = StorageFactory.getInstance().getStorage();

        List<Index> indexes = storage.get(theme);

        //indexes.stream() to disable concurrency
        return indexes.parallelStream()
                .map(index -> new SearchResult(index.getAnswer(), calculateSimilarity(sentence, index.getQuestion())))
                .filter(a1 -> a1.getSimilarity() >= SIMILARITY_FACTOR)
                .max((a1, a2) -> a1.getSimilarity().compareTo(a2.getSimilarity())).orElse(new SearchResult("", -1));
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