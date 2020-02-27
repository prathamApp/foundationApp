package com.pratham.foundation.modalclasses;

import com.pratham.foundation.services.shared_preferences.FastSave;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

public class ImageJsonObject {
    private String resId;
    private String gameName;
    private String qid;
    private String ansImageName;
    private String question;
    private String queImageName;
    private boolean isTest;


    public ImageJsonObject(String resId, String gameName, String qid, String ansImageName, String question, String queImageName) {
        this.resId = resId;
        this.gameName = gameName;
        this.qid = qid;
        this.ansImageName = ansImageName;
        this.question = question;
        this.queImageName = queImageName;
        this.isTest= FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test);
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getAnsImageName() {
        return ansImageName;
    }

    public void setAnsImageName(String ansImageName) {
        this.ansImageName = ansImageName;
    }

    public String getQueImageName() {
        return queImageName;
    }

    public void setQueImageName(String queImageName) {
        this.queImageName = queImageName;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }
}
