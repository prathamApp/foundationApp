package com.pratham.foundation.database.domain;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity(tableName = "ContentProgress")
public class ContentProgress implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int progressId;

    @SerializedName("sessionId")
    private String sessionId;

    @SerializedName("studentId")
    private String studentId;

    @SerializedName("resourceId")
    private String resourceId;

    @SerializedName("updatedDateTime")
    private String updatedDateTime;

    @SerializedName("progressPercentage")
    private String progressPercentage;

    @SerializedName("label")
    private String label;

    private int sentFlag =0 ;

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }

    public int getProgressId() {
        return progressId;
    }

    public void setProgressId(int progressId) {
        this.progressId = progressId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(String updatedDateTime) {
        this.updatedDateTime = updatedDateTime; }

    public String getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(String progressPercentage) {
        this.progressPercentage = progressPercentage; }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}