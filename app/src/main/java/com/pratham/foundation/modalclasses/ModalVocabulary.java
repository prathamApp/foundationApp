
package com.pratham.foundation.modalclasses;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ModalVocabulary implements Serializable {


    @SerializedName("questionId")
    @Expose
    private String questionId;

    @SerializedName("question1Text")
    @Expose
    private String question1Text;

    @SerializedName("question1Aud")
    @Expose
    private String question1Aud;

    @SerializedName("answer1Text")
    @Expose
    private String answer1Text;

    @SerializedName("answer1Aud")
    @Expose
    private String answer1Aud;

    @SerializedName("check1Text")
    @Expose
    private String check1Text;

    @SerializedName("question2Text")
    @Expose
    private String question2Tex;

    @SerializedName("question2Aud")
    @Expose
    private String question2Aud;

    @SerializedName("answer2Text")
    @Expose
    private String answer2Text;

    @SerializedName("answer2Aud")
    @Expose
    private String answer2Aud;

    @SerializedName("check2Text")
    @Expose
    private String check2Text;

    @SerializedName("img")
    @Expose
    private String img;

    @SerializedName("value")
    @Expose
    private String value;

    @SerializedName("dataList")
    @Expose
    private List<ModalVocabulary> dataList;


    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion1Text() {
        return question1Text;
    }

    public void setQuestion1Text(String question1Text) {
        this.question1Text = question1Text;
    }

    public String getQuestion1Aud() {
        return question1Aud;
    }

    public void setQuestion1Aud(String question1Aud) {
        this.question1Aud = question1Aud;
    }

    public String getAnswer1Text() {
        return answer1Text;
    }

    public void setAnswer1Text(String answer1Text) {
        this.answer1Text = answer1Text;
    }

    public String getAnswer1Aud() {
        return answer1Aud;
    }

    public void setAnswer1Aud(String answer1Aud) {
        this.answer1Aud = answer1Aud;
    }

    public String getCheck1Text() {
        return check1Text;
    }

    public void setCheck1Text(String check1Text) {
        this.check1Text = check1Text;
    }

    public String getQuestion2Tex() {
        return question2Tex;
    }

    public void setQuestion2Tex(String question2Tex) {
        this.question2Tex = question2Tex;
    }

    public String getQuestion2Aud() {
        return question2Aud;
    }

    public void setQuestion2Aud(String question2Aud) {
        this.question2Aud = question2Aud;
    }

    public String getAnswer2Text() {
        return answer2Text;
    }

    public void setAnswer2Text(String answer2Text) {
        this.answer2Text = answer2Text;
    }

    public String getAnswer2Aud() {
        return answer2Aud;
    }

    public void setAnswer2Aud(String answer2Aud) {
        this.answer2Aud = answer2Aud;
    }

    public String getCheck2Text() {
        return check2Text;
    }

    public void setCheck2Text(String check2Text) {
        this.check2Text = check2Text;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<ModalVocabulary> getDataList() {
        return dataList;
    }

    public void setDataList(List<ModalVocabulary> dataList) {
        this.dataList = dataList;
    }
}