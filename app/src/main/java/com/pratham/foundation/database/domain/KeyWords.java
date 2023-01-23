package com.pratham.foundation.database.domain;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Tankita on 10/8/2018.
 */

@Entity
public class KeyWords implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int keyWordId;
    @SerializedName("studentId")
    private String studentId;
    @SerializedName("resourceId")
    private String resourceId;
    @SerializedName("keyWord")
    private String keyWord;
    @SerializedName("wordType")
    private String wordType;
    private String topic;
    private int sentFlag;

    public int getKeyWordId() {
        return keyWordId;
    }

    public void setKeyWordId(int keyWordId) {
        this.keyWordId = keyWordId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

   /* public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }*/

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getWordType() {
        return wordType;
    }

    public void setWordType(String wordType) {
        this.wordType = wordType;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}