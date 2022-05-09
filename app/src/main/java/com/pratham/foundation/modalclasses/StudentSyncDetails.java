package com.pratham.foundation.modalclasses;

/**
 * Created by Anki on 12/11/2018.
 */

public class StudentSyncDetails {

    String Name;
    String EId;
    String StudentID;
    String TotalResources;
    String TotalCourses;
    String TotalAttendance;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEId() {
        return EId;
    }

    public void setEId(String EId) {
        this.EId = EId;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public String getTotalResources() {
        return TotalResources;
    }

    public void setTotalResources(String totalResources) {
        TotalResources = totalResources;
    }

    public String getTotalCourses() {
        return TotalCourses;
    }

    public void setTotalCourses(String totalCourses) {
        TotalCourses = totalCourses;
    }

    public String getTotalAttendance() {
        return TotalAttendance;
    }

    public void setTotalAttendance(String totalAttendance) {
        TotalAttendance = totalAttendance;
    }
}
