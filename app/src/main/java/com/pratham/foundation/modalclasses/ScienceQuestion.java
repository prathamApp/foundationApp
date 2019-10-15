package com.pratham.foundation.modalclasses;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

@Entity
public class ScienceQuestion implements Serializable {

    @NonNull
    @PrimaryKey
    private int qid;
    private String photourl;
    private String answer;
    private String title;
    private String qname;
    @Embedded
    private ArrayList<String> lstquestionchoice;
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

    public ArrayList<String> getLstquestionchoice() {
        return lstquestionchoice;
    }

    public void setLstquestionchoice(ArrayList<String> lstquestionchoice) {
        this.lstquestionchoice = lstquestionchoice;
    }

}
