
package com.pratham.foundation.modalclasses;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ModalReadingCardMenu implements Serializable {


    @SerializedName("convoTitle")
    @Expose
    private String convoTitle;

    @SerializedName("resourceId")
    @Expose
    private String resourceId;

    @SerializedName("audio")
    @Expose
    private String audio;

    @SerializedName("convoList")
    @Expose
    private List<ModalReadingCardSubMenu> convoList;

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

    public List<ModalReadingCardSubMenu> getConvoList() {
        return convoList;
    }

    public void setConvoList(List<ModalReadingCardSubMenu> convoList) {
        this.convoList = convoList;
    }
}