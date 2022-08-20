package com.pratham.foundation.modalclasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "SyncLog")
public class SyncLog implements Serializable, Comparable, Parcelable {

    @NonNull
    @PrimaryKey
    public String uuid;
    public String PushDate;
    @NonNull
    public int PushId;
    public String Error;
    public String Status;
    public String PushType;
    public int sentFlag;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeString(this.uuid);
        parcel.writeString(this.PushDate);
        parcel.writeInt(this.PushId);
        parcel.writeString(this.Error);
        parcel.writeString(this.Status);
        parcel.writeString(this.PushType);
        parcel.writeInt(this.sentFlag);

    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    public String getPushDate() {
        return PushDate;
    }

    public void setPushDate(String pushDate) {
        PushDate = pushDate;
    }

    public int getPushId() {
        return PushId;
    }

    public void setPushId(int pushId) {
        PushId = pushId;
    }

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

    public String getPushType() {
        return PushType;
    }

    public void setPushType(String pushType) {
        PushType = pushType;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}