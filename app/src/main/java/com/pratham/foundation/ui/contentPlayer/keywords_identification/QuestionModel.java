package com.pratham.foundation.ui.contentPlayer.keywords_identification;

import android.arch.persistence.room.Embedded;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionModel {
    @SerializedName("id")
    private int resourceId;
    private String paragraph;
    @Embedded
    private List keywords;
    @SerializedName("topic")
    private String title;

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public List getKeywords() {
        return keywords;
    }

    public void setKeywords(List keywords) {
        this.keywords = keywords;
    }


    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
