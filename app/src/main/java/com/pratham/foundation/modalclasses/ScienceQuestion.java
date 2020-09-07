package com.pratham.foundation.modalclasses;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ScienceQuestion implements Serializable {

    private String ansdesc;

    private String updatedby;

    private String qlevel;

    private String addedby;

    private String languageid;

    private String active;
    private String instruction;
    private String instructionUrl;

    private String lessonid;
    @Embedded
    private ArrayList<ScienceQuestionChoice> lstquestionchoice;
    @Ignore
    List<ScienceQuestionChoice> matchingNameList = null;

    private String qtid;
    @NonNull
    @PrimaryKey
    private String qid;

    private String subjectid;

    private String addedtime;

    private String updatedtime;

    @SerializedName("resourceUrl")
    private String photourl;

    private String examtime;

    private String topicid;


    private String answer;

    private String outofmarks;

    private String question;

    private String hint;

    private boolean isSubmitted;

    private String pdid;
    private String startTime;
    @Ignore
    boolean isMediaDownloaded;

    private String endTime;


    private String marksPerQuestion = "0";

    private String paperid;

    private String userAnswerId = "";

    private String userAnswer = "";
    private String title = "";

    private boolean isAttempted;

    private boolean isCorrect;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserAnswer() {
        if (userAnswer == null)
            userAnswer = "";
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public boolean getIsAttempted() {
        return isAttempted;
    }

    public void setIsAttempted(boolean attempted) {
        isAttempted = attempted;
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean correct) {
        isCorrect = correct;
    }

    public String getUserAnswerId() {
        if (userAnswerId == null)
            userAnswerId = "";
        return userAnswerId;
    }

    public String getMarksPerQuestion() {
        if (marksPerQuestion != null)
            return marksPerQuestion;
        else return "0";
    }

    public void setMarksPerQuestion(String marksPerQuestion) {
        if (marksPerQuestion != null)
            this.marksPerQuestion = marksPerQuestion;
        else marksPerQuestion = "0";
    }

    public void setUserAnswerId(String userAnswerId) {
        this.userAnswerId = userAnswerId;
    }


    public String getAnsdesc() {
        return ansdesc;
    }

    public void setAnsdesc(String ansdesc) {
        this.ansdesc = ansdesc;
    }

    public String getUpdatedby() {
        return updatedby;
    }

    public void setUpdatedby(String updatedby) {
        this.updatedby = updatedby;
    }

    public String getQlevel() {
        return qlevel;
    }

    public void setQlevel(String qlevel) {
        this.qlevel = qlevel;
    }

    public String getAddedby() {
        return addedby;
    }

    public void setAddedby(String addedby) {
        this.addedby = addedby;
    }

    public String getLanguageid() {
        return languageid;
    }

    public void setLanguageid(String languageid) {
        this.languageid = languageid;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getLessonid() {
        return lessonid;
    }

    public void setLessonid(String lessonid) {
        this.lessonid = lessonid;
    }

    public ArrayList<ScienceQuestionChoice> getLstquestionchoice() {
        return lstquestionchoice;
    }

    public void setLstquestionchoice(ArrayList<ScienceQuestionChoice> lstquestionchoice) {
        this.lstquestionchoice = lstquestionchoice;
    }

    public String getQtid() {
        return qtid;
    }

    public void setQtid(String qtid) {
        this.qtid = qtid;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getSubjectid() {
        return subjectid;
    }

    public void setSubjectid(String subjectid) {
        this.subjectid = subjectid;
    }

    public String getAddedtime() {
        return addedtime;
    }

    public void setAddedtime(String addedtime) {
        this.addedtime = addedtime;
    }

    public String getUpdatedtime() {
        return updatedtime;
    }

    public void setUpdatedtime(String updatedtime) {
        this.updatedtime = updatedtime;
    }

    public String getPhotourl() {
        if (photourl == null)
            return "";
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getExamtime() {
        return examtime;
    }

    public void setExamtime(String examtime) {
        this.examtime = examtime;
    }

    public String getTopicid() {
        return topicid;
    }

    public void setTopicid(String topicid) {
        this.topicid = topicid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getOutofmarks() {
        return outofmarks;
    }

    public void setOutofmarks(String outofmarks) {
        this.outofmarks = outofmarks;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }



    public String getPdid() {
        return pdid;
    }

    public void setPdid(String pdid) {
        this.pdid = pdid;
    }

    public String getPaperid() {
        return paperid;
    }

    public void setPaperid(String paperid) {
        this.paperid = paperid;
    }

    public List<ScienceQuestionChoice> getMatchingNameList() {
        return matchingNameList;
    }

    public void setMatchingNameList(List<ScienceQuestionChoice> matchingNameList) {
        this.matchingNameList = matchingNameList;
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

    public boolean isMediaDownloaded() {
        return isMediaDownloaded;
    }

    public void setMediaDownloaded(boolean mediaDownloaded) {
        isMediaDownloaded = mediaDownloaded;
    }

    public String getInstruction() {
        if (instruction != null)
            return instruction;
        else
            return "";
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getInstructionUrl() {
        return instructionUrl;
    }

    public void setInstructionUrl(String instructionUrl) {
        this.instructionUrl = instructionUrl;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public void setSubmitted(boolean submitted) {
        isSubmitted = submitted;
    }
}
