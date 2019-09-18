package com.pratham.foundation.modalclasses;

import android.graphics.drawable.Drawable;
import android.location.Location;

import com.pratham.foundation.database.domain.ContentTable;

import java.util.ArrayList;

public class EventMessage {
	
    private String message;
    private int downlaodContentSize;
    private ContentTable contentDetail;
    private ArrayList<ContentTable> content;
    private Drawable connection_resource;
    private String connection_name;
    private String pushData;
    private String downloadId;
    private String file_name;
    private long progress;
    private Location location;
    private Modal_FileDownloading modal_fileDownloading;

    public Modal_FileDownloading getModal_fileDownloading() {
        return modal_fileDownloading;
    }

    public void setModal_fileDownloading(Modal_FileDownloading modal_fileDownloading) {
        this.modal_fileDownloading = modal_fileDownloading;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public ArrayList<ContentTable> getContent() {
        return content;
    }

    public void setContent(ArrayList<ContentTable> content) {
        this.content = content;
    }

    public Drawable getConnection_resource() {
        return connection_resource;
    }

    public void setConnection_resource(Drawable connection_resource) {
        this.connection_resource = connection_resource;
    }

    public String getConnection_name() {
        return connection_name;
    }

    public void setConnection_name(String connection_name) {
        this.connection_name = connection_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDownlaodContentSize() {
        return downlaodContentSize;
    }

    public void setDownlaodContentSize(int downlaodContentSize) {
        this.downlaodContentSize = downlaodContentSize;
    }

    public ContentTable getContentDetail() {
        return contentDetail;
    }

    public void setContentDetail(ContentTable contentDetail) {
        this.contentDetail = contentDetail;
    }

    public ArrayList<ContentTable> getContentList() {
        return content;
    }

    public void setContentList(ArrayList<ContentTable> content) {
        this.content = content;
    }

    public String getPushData() {
        return pushData;
    }

    public void setPushData(String pushData) {
        this.pushData = pushData;
    }
}
