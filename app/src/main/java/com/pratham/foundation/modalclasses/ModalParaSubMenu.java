
package com.pratham.foundation.modalclasses;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ModalParaSubMenu implements Serializable {


    @SerializedName("parentId")
    @Expose
    private String parentId;

    @SerializedName("nodeId")
    @Expose
    private String nodeId;

    @SerializedName("pageText")
    @Expose
    private String pageText;

    @SerializedName("pageTitle")
    @Expose
    private String pageTitle;

    @SerializedName("readPageAudio")
    @Expose
    private String readPageAudio;

    @SerializedName("pageImage")
    @Expose
    private String pageImage;

    @SerializedName("readList")
    @Expose
    private List<ModalParaWord> readList;

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

    public String getPageText() {
        return pageText;
    }

    public void setPageText(String paraText) {
        this.pageText = paraText;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getReadPageAudio() {
        return readPageAudio;
    }

    public void setReadPageAudio(String readPageAudio) {
        this.readPageAudio = readPageAudio;
    }

    public List<ModalParaWord> getReadList() {
        return readList;
    }

    public void setReadList(List<ModalParaWord> readList) {
        this.readList = readList;
    }

    public String getPageImage() {
        return pageImage;
    }

    public void setPageImage(String pageImage) {
        this.pageImage = pageImage;
    }
}