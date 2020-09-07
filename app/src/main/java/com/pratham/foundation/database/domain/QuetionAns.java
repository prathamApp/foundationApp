package com.pratham.foundation.database.domain;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;

public class QuetionAns {
    int id;
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

    @NonNull
    @Override
    public String toString() {
        return "{ quetion : "+quetion+", answer: "+answer+"}";
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
