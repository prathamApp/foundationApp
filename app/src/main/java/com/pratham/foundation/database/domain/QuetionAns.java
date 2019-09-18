package com.pratham.foundation.database.domain;

import android.arch.persistence.room.Ignore;

import com.google.gson.annotations.SerializedName;

public class QuetionAns {
    @SerializedName("que")
    private String quetion;
    @SerializedName("ans")
    private String answer;
    @Ignore
    private String userAns;
    @Ignore
    private int start;
    @Ignore
    private int end;

    public String getQuetion() {
        return quetion;
    }

    public void setQuetion(String quetion) {
        this.quetion = quetion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUserAns() {
        return userAns;
    }

    public void setUserAns(String userAns) {
        this.userAns = userAns;
    }

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
}
