package com.pratham.foundation.modalclasses;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
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
    private String english;
    private String isQuestion;
    private String englishURL;
    private boolean isPlaying=false;
    @Ignore
    private String ansInPassage;

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

    @Ignore
    private String startTime;

    @Ignore
    private String endTime;


    public List getStt_result() {
        return stt_result;
    }

    public void setStt_result(List stt_result) {
        this.stt_result = stt_result;
    }

    @Ignore
    private List stt_result=new ArrayList();

    @Ignore
    private boolean isTrue;

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


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ que :" + subQues + ", ans :" + userAns + " }";
    }

    public String getAnsInPassage() {
        return ansInPassage;
    }

    public void setAnsInPassage(String ansInPassage) {
        this.ansInPassage = ansInPassage;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean aTrue) {
        isTrue = aTrue;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getIsQuestion() {
        return isQuestion;
    }

    public void setIsQuestion(String isQuestion) {
        this.isQuestion = isQuestion;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getEnglishURL() {
        return englishURL;
    }

    public void setEnglishURL(String englishURL) {
        this.englishURL = englishURL;
    }
}