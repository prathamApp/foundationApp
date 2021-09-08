
package com.pratham.foundation.modalclasses.RaspModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LstFileList {

    @SerializedName("FileId")
    @Expose
    private Integer fileId;
    @SerializedName("NodeId")
    @Expose
    private String nodeId;
    @SerializedName("FileType")
    @Expose
    private String fileType;
    @SerializedName("FileUrl")
    @Expose
    private String fileUrl;
    @SerializedName("DateUpdated")
    @Expose
    private String dateUpdated;
    @SerializedName("localUrl")
    @Expose
    private String localUrl;

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

}
