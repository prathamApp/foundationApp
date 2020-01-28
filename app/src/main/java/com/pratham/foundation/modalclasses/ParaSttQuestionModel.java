package com.pratham.foundation.modalclasses;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ParaSttQuestionModel {

    @SerializedName("paraTitle")
    String paraTitle;

    @SerializedName("quesList")
    private List<ParaSttQuestionListModel> quesList;

    public String getParaTitle() {
        return paraTitle;
    }

    public void setParaTitle(String paraTitle) {
        this.paraTitle = paraTitle;
    }

    public List<ParaSttQuestionListModel> getQuesList() {
        return quesList;
    }

    public void setQuesList(List<ParaSttQuestionListModel> quesList) {
        this.quesList = quesList;
    }
}
