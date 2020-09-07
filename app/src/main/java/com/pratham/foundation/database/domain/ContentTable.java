package com.pratham.foundation.database.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Entity
public class ContentTable implements Serializable {

    @NonNull
    @PrimaryKey
    @SerializedName("nodeid")
    public String nodeId;
    @SerializedName("contentlevel")
    public String level;
    @SerializedName("resourceid")
    public String resourceId;
    @SerializedName("parentid")
    public String parentId;
    @SerializedName("nodedesc")
    public String nodeDesc;
    @SerializedName("nodetype")
    public String nodeType;
    @SerializedName("cont_engtitle")
    public String nodeEnglishTitle;
    @SerializedName("nodetitle")
    public String nodeTitle;
    @SerializedName("resourcepath")
    public String resourcePath;
    @SerializedName("resourcetype")
    public String resourceType;
    @SerializedName("nodeserverimage")
    public String nodeServerImage;
    @SerializedName("nodeimage")
    public String nodeImage;
    @SerializedName("nodeage")
    public String nodeAge;
    @SerializedName("nodelang")
    public String contentLanguage;
    @SerializedName("version")
    public String version;
    @SerializedName("orignodeversion")
    public String origNodeVersion;
    @SerializedName("subject")
    public String subject;
    @SerializedName("seq_no")
    public int seq_no;
    @SerializedName("nodeKeyword")
    public String nodeKeywords;
    public String isDownloaded;
    public String contentType;
    @Ignore
    public String nodePercentage;
    @Ignore
    public boolean nodeUpdate = false;
    private boolean onSDCard = false;

    @Nullable
    @Ignore
    public List<ContentTable> contentlist = null;


    @NonNull
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(@NonNull String nodeId) {
        this.nodeId = nodeId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getNodeDesc() {
        return nodeDesc;
    }

    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeEnglishTitle() {
        return nodeEnglishTitle;
    }

    public void setNodeEnglishTitle(String nodeEnglishTitle) {
        this.nodeEnglishTitle = nodeEnglishTitle;
    }

    public String getNodeTitle() {
        return nodeTitle;
    }

    public void setNodeTitle(String nodeTitle) {
        this.nodeTitle = nodeTitle;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getNodeServerImage() {
        return nodeServerImage;
    }

    public void setNodeServerImage(String nodeServerImage) {
        this.nodeServerImage = nodeServerImage;
    }

    public String getNodeImage() {
        return nodeImage;
    }

    public void setNodeImage(String nodeImage) {
        this.nodeImage = nodeImage;
    }

    public String getNodeAge() {
        return nodeAge;
    }

    public void setNodeAge(String nodeAge) {
        this.nodeAge = nodeAge;
    }

    public String getContentLanguage() {
        return contentLanguage;
    }

    public void setContentLanguage(String contentLanguage) {
        this.contentLanguage = contentLanguage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOrigNodeVersion() {
        return origNodeVersion;
    }

    public void setOrigNodeVersion(String origNodeVersion) { this.origNodeVersion = origNodeVersion; }

    public String getSubject() { return subject; }

    public void setSubject(String subject) { this.subject = subject; }

    public int getSeq_no() {
        return seq_no;
    }

    public void setSeq_no(int seq_no) {
        this.seq_no = seq_no;
    }

    public String getNodeKeywords() {
        return nodeKeywords;
    }

    public void setNodeKeywords(String nodeKeywords) {
        this.nodeKeywords = nodeKeywords;
    }

    public String getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(String isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getNodePercentage() {
        return nodePercentage;
    }

    public void setNodePercentage(String nodePercentage) {
        this.nodePercentage = nodePercentage;
    }

    public boolean isOnSDCard() {
        return onSDCard;
    }

    public void setOnSDCard(boolean onSDCard) {
        this.onSDCard = onSDCard;
    }

    public boolean isNodeUpdate() {
        return nodeUpdate;
    }

    public void setNodeUpdate(boolean nodeUpdate) {
        this.nodeUpdate = nodeUpdate;
    }

    @Nullable
    public List<ContentTable> getNodelist() {
        return contentlist;
    }

    public void setNodelist(@Nullable List<ContentTable> contentlist) {
        this.contentlist = contentlist;
    }
}