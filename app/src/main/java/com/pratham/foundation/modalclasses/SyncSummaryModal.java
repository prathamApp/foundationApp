package com.pratham.foundation.modalclasses;

/**
 * Created by Anki on 12/11/2018.
 */

public class SyncSummaryModal {

    public String TotalAttendance;
    public String TotalCourses;
    public String TotalStudentsRegistered;
    public String TotalStudentsAccessed;
    public String TotalSessions;
    public String TotalGroups;
    public int IsError;
    public String ErrorMsg;


    public String getTotalAttendance() {
        return TotalAttendance;
    }

    public void setTotalAttendance(String totalAttendance) {
        TotalAttendance = totalAttendance;
    }

    public String getTotalCourses() {
        return TotalCourses;
    }

    public void setTotalCourses(String totalCourses) {
        TotalCourses = totalCourses;
    }

    public String getTotalStudentsRegistered() {
        return TotalStudentsRegistered;
    }

    public void setTotalStudentsRegistered(String totalStudentsRegistered) {
        TotalStudentsRegistered = totalStudentsRegistered;
    }

    public String getTotalStudentsAccessed() {
        return TotalStudentsAccessed;
    }

    public void setTotalStudentsAccessed(String totalStudentsAccessed) {
        TotalStudentsAccessed = totalStudentsAccessed;
    }

    public String getTotalSessions() {
        return TotalSessions;
    }

    public void setTotalSessions(String totalSessions) {
        TotalSessions = totalSessions;
    }

    public String getTotalGroups() {
        return TotalGroups;
    }

    public void setTotalGroups(String totalGroups) {
        TotalGroups = totalGroups;
    }

    public int getIsError() {
        return IsError;
    }

    public void setIsError(int isError) {
        IsError = isError;
    }

    public String getErrorMsg() {
        return ErrorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        ErrorMsg = errorMsg;
    }
}
