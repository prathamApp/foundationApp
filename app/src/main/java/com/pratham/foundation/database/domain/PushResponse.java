package com.pratham.foundation.database.domain;

import com.google.gson.annotations.SerializedName;

public class PushResponse {


    @SerializedName("Error")
    private String Error;

    @SerializedName("Status")
    private String Status;

    public String getError() {
        return Error;
    }

    public void setError(String error) {
        Error = error;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}