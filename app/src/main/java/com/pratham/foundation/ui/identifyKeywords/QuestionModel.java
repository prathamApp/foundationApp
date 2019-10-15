package com.pratham.foundation.ui.identifyKeywords;

import android.arch.persistence.room.Embedded;

import com.google.gson.annotations.SerializedName;
import com.pratham.foundation.database.domain.QuetionAns;

import java.util.List;

public class QuestionModel {
    @SerializedName("id")
    private int resourceId;
    private String paragraph;
    @Embedded
    private List<QuetionAns> keywords;
    @SerializedName("title")
    private String title;

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public List<QuetionAns> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<QuetionAns> keywords) {
        this.keywords = keywords;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
