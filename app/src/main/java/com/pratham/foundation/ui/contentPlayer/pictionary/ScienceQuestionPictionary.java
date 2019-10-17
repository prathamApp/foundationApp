package com.pratham.foundation.ui.contentPlayer.pictionary;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.pratham.foundation.modalclasses.ScienceQuestionChoice;

import java.util.ArrayList;

public class ScienceQuestionPictionary {
    @NonNull
    @PrimaryKey
    private int qid;
    private String photourl;
    private String answer;
    private String title;
    private String qname;
    @Embedded
    private ArrayList<ScienceQuestionChoice> lstquestionchoice;
    private String userAnswer = "";

    public String getUserAnswer() {
        if (userAnswer == null)
            userAnswer = "";
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public String getPhotourl() {
        if (photourl == null) {
            photourl = "";
        }
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQname() {
        return qname;
    }

    public void setQname(String qname) {
        this.qname = qname;
    }

    public ArrayList<ScienceQuestionChoice> getLstquestionchoice() {
        return lstquestionchoice;
    }

    public void setLstquestionchoice(ArrayList<ScienceQuestionChoice> lstquestionchoice) {
        this.lstquestionchoice = lstquestionchoice;
    }
}
