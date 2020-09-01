package com.pratham.foundation.database.domain;

import com.google.gson.annotations.SerializedName;

public class PushResponse {


    @SerializedName("TransId")
    private String TransId;

    @SerializedName("ErrorId")
    private String ErrorId;

    @SerializedName("ErrorDesc")
    private String ErrorDesc;


    public String getTransId() {
        return TransId;
    }

    public void setTransId(String transId) {
        TransId = transId;
    }

    public String getErrorId() {
        return ErrorId;
    }

    public void setErrorId(String errorId) {
        ErrorId = errorId;
    }

    public String getErrorDesc() {
        return ErrorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        ErrorDesc = errorDesc;
    }
}