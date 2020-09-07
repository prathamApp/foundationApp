package com.pratham.foundation.database.domain;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Status")
public class Status {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "statusID")
    public int statusID;
    @ColumnInfo(name = "statusKey")
    public String statusKey;
    @NonNull
    @ColumnInfo(name = "value")
    public String value="";
    @ColumnInfo(name = "description")
    public String description;

    @Override
    public String toString() {
        return "Status{" +
                "statusID=" + statusID +
                ", statusKey='" + statusKey + '\'' +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public String getStatusKey() {
        return statusKey;
    }

    public void setStatusKey(String statusKey) {
        this.statusKey = statusKey;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    public void setValue(@NonNull String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
