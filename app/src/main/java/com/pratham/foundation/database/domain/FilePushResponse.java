package com.pratham.foundation.database.domain;

import com.google.gson.annotations.SerializedName;

public class FilePushResponse {


    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("Status")
    private String Status;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}