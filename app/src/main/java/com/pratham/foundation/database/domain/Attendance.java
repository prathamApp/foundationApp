package com.pratham.foundation.database.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Attendance")
public class Attendance {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "attendanceID")
    private int attendanceID;
    @ColumnInfo(name = "SessionID")
    private String SessionID;
    @ColumnInfo(name = "StudentID")
    private String StudentID;
    @SerializedName("Date")
    public String Date;
    @SerializedName("GroupID")
    public String GroupID;
    @SerializedName("sentFlag")
    public int sentFlag;
    public int getSentFlag() {
        return sentFlag;
    }
    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceID=" + attendanceID +
                ", SessionID='" + SessionID + '\'' +
                ", StudentID='" + StudentID + '\'' +
                ", Date='" + Date + '\'' +
                ", GroupID='" + GroupID + '\'' +
                ", sentFlag='" + sentFlag + '\'' +
                '}';
    }

    public int getAttendanceID() {
        return attendanceID;
    }

    public void setAttendanceID(int attendanceID) {
        this.attendanceID = attendanceID;
    }

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }
}
