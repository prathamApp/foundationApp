package com.pratham.foundation.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

@Entity
public class ScienceQuestionChoice implements Serializable {

    @NonNull
    @PrimaryKey
    private String qcid;

    private String qid;
    private String subQues;
    private String correctAnswer;
    private String subUrl;

    @Ignore
    private String userAns = "";
    @Ignore
    private int position = -1;
    @Ignore
    private int start;
    @Ignore
    private int end;
    @Ignore
    private boolean isclicked = false;
    ;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @NonNull
    public String getQcid() {
        return qcid;
    }

    public void setQcid(@NonNull String qcid) {
        this.qcid = qcid;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getSubQues() {
        return subQues;
    }

    public void setSubQues(String subQues) {
        this.subQues = subQues;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getSubUrl() {
        return subUrl;
    }

    public void setSubUrl(String subUrl) {
        this.subUrl = subUrl;
    }

    public String getUserAns() {
        return userAns;
    }

    public void setUserAns(String userAns) {
        this.userAns = userAns;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isIsclicked() {
        return isclicked;
    }

    public void setIsclicked(boolean isclicked) {
        this.isclicked = isclicked;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ que :" + subQues + ", ans :" + userAns + " }";
    }
}