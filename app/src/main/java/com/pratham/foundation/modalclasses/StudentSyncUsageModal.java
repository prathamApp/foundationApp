package com.pratham.foundation.modalclasses;

/**
 * Created by Anki on 12/11/2018.
 */

public class StudentSyncUsageModal {

    String Name;
    String EId;
    String SId;
    String Courses;
    String Resources;
    String AttendanceCount;
    String Date;

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

    public String getSId() {
        return SId;
    }

    public void setSId(String SId) {
        this.SId = SId;
    }

    public String getCourses() {
        return Courses;
    }

    public void setCourses(String courses) {
        Courses = courses;
    }

    public String getResources() {
        return Resources;
    }

    public void setResources(String resources) {
        Resources = resources;
    }

    public String getAttendanceCount() {
        return AttendanceCount;
    }

    public void setAttendanceCount(String attendanceCount) {
        AttendanceCount = attendanceCount;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
