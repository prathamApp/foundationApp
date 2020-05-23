package com.pratham.foundation.modalclasses;

import com.google.gson.annotations.SerializedName;
import com.pratham.foundation.database.domain.ContentTable;

import java.util.List;

public class Modal_DownloadContent {

    @SerializedName("contentlist")
    List<ContentTable> nodelist;
    @SerializedName("downloadurl")
    String downloadurl;
    @SerializedName("resourcetype")
    String foldername;

    @Override
    public String toString() {
        return "Modal_DownloadContent{" +
                "nodelist=" + nodelist +
                ", downloadurl='" + downloadurl + '\'' +
                ", foldername='" + foldername + '\'' +
                '}';
    }

    public List<ContentTable> getNodelist() {
        return nodelist;
    }

    public void setNodelist(List<ContentTable> nodelist) {
        this.nodelist = nodelist;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getFoldername() {
        return foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }
}
