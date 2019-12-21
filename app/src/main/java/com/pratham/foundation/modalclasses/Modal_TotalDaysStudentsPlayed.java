package com.pratham.foundation.modalclasses;

import android.arch.persistence.room.ColumnInfo;

public class Modal_TotalDaysStudentsPlayed {

    @ColumnInfo(name = "dates")
    public String dates;
    @ColumnInfo(name = "StudentID")
    public String StudentID;
    @ColumnInfo(name = "FullName")
    public String FullName;

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }
}
