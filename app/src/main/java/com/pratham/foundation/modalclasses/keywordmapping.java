package com.pratham.foundation.modalclasses;

import java.util.List;

public class keywordmapping {
    private int id;
    private String keyword;
    private List<String> keywordOptionSet;
    private List<String> keywordAnsSet;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List getKeywordOptionSet() {
        return keywordOptionSet;
    }

    public void setKeywordOptionSet(List keywordOptionSet) {
        this.keywordOptionSet = keywordOptionSet;
    }

    public List getKeywordAnsSet() {
        return keywordAnsSet;
    }

    public void setKeywordAnsSet(List keywordAnsSet) {
        this.keywordAnsSet = keywordAnsSet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
