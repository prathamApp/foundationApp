package com.pratham.foundation.modalclasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ameya on 28-Mar-18.
 */

public class PlayerModal implements Parcelable {


    public String studentID,studentName,studentScore,studentAlias;

    public PlayerModal(String studentID, String studentName, String studentScore, String studentAlias) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.studentScore = studentScore;
        this.studentAlias = studentAlias;
    }

    protected PlayerModal(Parcel in) {
        studentID = in.readString();
        studentName = in.readString();
        studentScore = in.readString();
        studentAlias = in.readString();
    }

    public static final Creator<PlayerModal> CREATOR = new Creator<PlayerModal>() {
        @Override
        public PlayerModal createFromParcel(Parcel in) {
            return new PlayerModal(in);
        }

        @Override
        public PlayerModal[] newArray(int size) {
            return new PlayerModal[size];
        }
    };

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentScore() {
        return studentScore;
    }

    public void setStudentScore(String studentScore) {
        this.studentScore = studentScore;
    }

    public String getStudentAlias() {
        return studentAlias;
    }

    public void setStudentAlias(String studentAlias) {
        this.studentAlias = studentAlias;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(studentID);
        dest.writeString(studentName);
        dest.writeString(studentScore);
        dest.writeString(studentAlias);
    }
}
