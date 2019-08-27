package com.pratham.foundation.ui.identifyKeywords;

import java.util.List;

public class QuestionModel {
    private String paragraph;
    private List<String> keywords;

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
