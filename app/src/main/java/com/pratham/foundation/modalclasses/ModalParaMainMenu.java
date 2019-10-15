
package com.pratham.foundation.modalclasses;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ModalParaMainMenu implements Serializable {


    @SerializedName("parentId")
    @Expose
    private String parentId;

    @SerializedName("nodeId")
    @Expose
    private String nodeId;

    @SerializedName("resourceId")
    @Expose
    private String resourceId;

    @SerializedName("nodeType")
    @Expose
    private String nodeType;

    @SerializedName("paraTitle")
    @Expose
    private String contentTitle;

    @SerializedName("pageImage")
    @Expose
    private String contentImage;

    @SerializedName("pageList")
    @Expose
    private List<ModalParaSubMenu> pageList;


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

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentImage() {
        return contentImage;
    }

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
    }

    public List<ModalParaSubMenu> getPageList() {
        return pageList;
    }

    public void setPageList(List<ModalParaSubMenu> pageList) {
        this.pageList = pageList;
    }
}