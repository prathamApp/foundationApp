package com.pratham.foundation.database.domain;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "Session")
public class Session {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "SessionID")
    private String SessionID;
    @ColumnInfo(name = "fromDate")
    private String fromDate;
    @ColumnInfo(name = "toDate")
    private String toDate;
    @ColumnInfo(name = "sentFlag")
    private int sentFlag;

    @Override
    public String toString() {
        return "Session{" +
                "SessionID='" + SessionID + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                ", sentFlag='" + sentFlag + '\'' +
                '}';
    }

    @NonNull
    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(@NonNull String sessionID) {
        SessionID = sessionID;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}
