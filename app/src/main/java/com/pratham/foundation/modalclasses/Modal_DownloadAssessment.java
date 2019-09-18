package com.pratham.foundation.modalclasses;

import com.google.gson.annotations.SerializedName;
import com.pratham.foundation.database.domain.ContentTable;

import java.util.List;

public class Modal_DownloadAssessment {
   
   @SerializedName("NodeList")
    List<ContentTable> nodelist;
    @SerializedName("GameType")
    String GameType;

    @Override
    public String toString() {
        return "Modal_DownloadContent{" +
                "nodelist=" + nodelist +
                ", GameType='" + GameType + '\'' +
                '}';
    }

    public List<ContentTable> getNodelist() {
        return nodelist;
    }

    public void setNodelist(List<ContentTable> nodelist) {
        this.nodelist = nodelist;
    }

    public String getGameType() {
        return GameType;
    }

    public void setGameType(String gameType) {
        GameType = gameType;
    }
}
