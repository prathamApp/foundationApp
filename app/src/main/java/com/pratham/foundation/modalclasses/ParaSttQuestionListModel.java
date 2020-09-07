package com.pratham.foundation.modalclasses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class ParaSttQuestionListModel implements Serializable {

    @SerializedName("quesText")
    String quesText;

    @SerializedName("answerText")
    String answerText;

    @Nullable
    @SerializedName("studentText")
    String studentText;


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

    public String getStudentText() {
        return studentText;
    }

    public void setStudentText(String studentText) {
        this.studentText = studentText;
    }
}