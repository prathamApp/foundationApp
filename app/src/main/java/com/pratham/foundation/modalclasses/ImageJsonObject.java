package com.pratham.foundation.modalclasses;

public class ImageJsonObject {
    private String resId;
    private String gameName;
    private String qid;
    private String ansImageName;
    private String question;
    private String queImageName;

    public ImageJsonObject(String resId, String gameName, String qid, String ansImageName, String question, String queImageName) {
        this.resId = resId;
        this.gameName = gameName;
        this.qid = qid;
        this.ansImageName = ansImageName;
        this.question = question;
        this.queImageName = queImageName;
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
}
