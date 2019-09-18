
package com.pratham.foundation.modalclasses;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModalReadingVocabulary implements Serializable {


    @SerializedName("topic")
    private String convoTitle;

    @SerializedName("id")
    private String resourceId;

    @SerializedName("audio")
    private String audio;

    @SerializedName("sentence")
    private Sentence sentence;

    @SerializedName("opposite")
    private Sentence opposite;

    public String getConvoTitle() {
        return convoTitle;
    }

    public void setConvoTitle(String convoTitle) {
        this.convoTitle = convoTitle;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
    }

    public Sentence getOpposite() {
        return opposite;
    }

    public void setOpposite(Sentence opposite) {
        this.opposite = opposite;
    }
}