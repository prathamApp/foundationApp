package com.pratham.foundation.modalclasses;

import com.google.gson.annotations.SerializedName;


public class ParaSttQuestionListModel {

    @SerializedName("quesText")
    String quesText;

    @SerializedName("answerText")
    String answerText;


    public String getQuesText() {
        return quesText;
    }

    public void setQuesText(String quesText) {
        this.quesText = quesText;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}