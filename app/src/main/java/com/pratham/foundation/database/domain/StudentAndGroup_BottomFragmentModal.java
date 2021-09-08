package com.pratham.foundation.database.domain;

import androidx.annotation.NonNull;

public class StudentAndGroup_BottomFragmentModal {

    @NonNull
    private String StudentID;
    public String FullName;
    public String Type;
    public String avatarName;
    public String gender;
    public String groupId;
    boolean isChecked;

    @NonNull
    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(@NonNull String studentID) {
        StudentID = studentID;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
