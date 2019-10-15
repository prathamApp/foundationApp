
package com.pratham.foundation.modalclasses;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class ModalParaWord implements Serializable {


    @SerializedName("parentId")
    @Expose
    private String parentId;

    @SerializedName("nodeId")
    @Expose
    private String nodeId;

    @SerializedName("word")
    @Expose
    private String word;

    @SerializedName("wordId")
    @Expose
    private String wordId;

    @SerializedName("wordFrom")
    @Expose
    private String wordFrom;

    @SerializedName("wordTo")
    @Expose
    private String wordTo;

    @SerializedName("wordDuration")
    @Expose
    private String wordDuration;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public String getWordFrom() {
        return wordFrom;
    }

    public void setWordFrom(String wordFrom) {
        this.wordFrom = wordFrom;
    }

    public String getWordTo() {
        return wordTo;
    }

    public void setWordTo(String wordTo) {
        this.wordTo = wordTo;
    }

    public String getWordDuration() {
        return wordDuration;
    }

    public void setWordDuration(String wordDuration) {
        this.wordDuration = wordDuration;
    }
}