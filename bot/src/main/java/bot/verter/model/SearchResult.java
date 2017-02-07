/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package bot.verter.model;

public class SearchResult {
    private Double similarity;
    private String answer;

    public SearchResult(String answer, double similarity) {
        this.similarity = similarity;
        this.answer = answer;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public String getAnswer() {
        return answer;
    }
}
