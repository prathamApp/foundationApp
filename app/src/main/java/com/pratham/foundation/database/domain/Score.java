package com.pratham.foundation.database.domain;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Score")
public class Score implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ScoreId")
    private int ScoreId;
    @ColumnInfo(name = "SessionID")
    private String SessionID;
    @ColumnInfo(name = "StudentID")
    private String StudentID;
    @ColumnInfo(name = "DeviceID")
    private String DeviceID;
    @ColumnInfo(name = "ResourceID")
    private String ResourceID;
    @ColumnInfo(name = "QuestionId")
    private int QuestionId;
    @ColumnInfo(name = "ScoredMarks")
    private int ScoredMarks;
    @ColumnInfo(name = "TotalMarks")
    private int TotalMarks;
    @ColumnInfo(name = "StartDateTime")
    private String StartDateTime;
    @ColumnInfo(name = "EndDateTime")
    private String EndDateTime;
    @ColumnInfo(name = "Level")
    private int Level;
    @ColumnInfo(name = "Label")
    private String Label;
    @ColumnInfo(name = "GroupId")
    private String GroupId;
    @ColumnInfo(name = "sentFlag")
    private int sentFlag;


    @Override
    public String toString() {
        return "Score{" +
                "ScoreId='" + ScoreId + '\'' +
                ", SessionID='" + SessionID + '\'' +
                ", StudentID='" + StudentID + '\'' +
                ", DeviceId='" + DeviceID + '\'' +
                ", ResourceID='" + ResourceID + '\'' +
                ", QuestionId=" + QuestionId +
                ", ScoredMarks=" + ScoredMarks +
                ", TotalMarks=" + TotalMarks +
                ", StartDateTime='" + StartDateTime + '\'' +
                ", EndDateTime='" + EndDateTime + '\'' +
                ", GroupId='" + GroupId + '\'' +
                ", Level=" + Level +
                '}';
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }

    @NonNull
    public int getScoreId() {
        return ScoreId;
    }

    public void setScoreId(@NonNull int scoreId) {
        ScoreId = scoreId;
    }

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getResourceID() {
        return ResourceID;
    }

    public void setResourceID(String resourceID) {
        ResourceID = resourceID;
    }

    public int getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(int questionId) {
        QuestionId = questionId;
    }

    public int getScoredMarks() {
        return ScoredMarks;
    }

    public void setScoredMarks(int scoredMarks) {
        ScoredMarks = scoredMarks;
    }

    public int getTotalMarks() {
        return TotalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        TotalMarks = totalMarks;
    }

    public String getStartDateTime() {
        return StartDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        StartDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return EndDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        EndDateTime = endDateTime;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }
}
