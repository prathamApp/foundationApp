package com.pratham.foundation.database.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class LstStudent implements Comparable, Parcelable {

    @SerializedName("GroupId")
    private String GroupId;
    @SerializedName("GroupName")
    private String GroupName;
    @SerializedName("FullName")
    private String FullName;
    @SerializedName("Class")
    private String studClass;
    @SerializedName("Age")
    private int Age;
    @SerializedName("Gender")
    private String Gender;
    @SerializedName("StudentId")
    private String StudentId;
    @SerializedName("StudentEnrollment")
    private String StudentEnrollment;

    protected LstStudent(Parcel in) {
        GroupId = in.readString();
        GroupName = in.readString();
        FullName = in.readString();
        studClass = in.readString();
        Age = in.readInt();
        Gender = in.readString();
        StudentId = in.readString();
        StudentEnrollment = in.readString();
    }

    public static final Creator<LstStudent> CREATOR = new Creator<LstStudent>() {
        @Override
        public LstStudent createFromParcel(Parcel in) {
            return new LstStudent(in);
        }

        @Override
        public LstStudent[] newArray(int size) {
            return new LstStudent[size];
        }
    };

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getStudClass() {
        return studClass;
    }

    public void setStudClass(String studClass) {
        this.studClass = studClass;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getStudentEnrollment() {
        return StudentEnrollment;
    }

    public void setStudentEnrollment(String studentEnrollment) {
        StudentEnrollment = studentEnrollment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(GroupId);
        dest.writeString(GroupName);
        dest.writeString(FullName);
        dest.writeString(studClass);
        dest.writeInt(Age);
        dest.writeString(Gender);
        dest.writeString(StudentId);
        dest.writeString(StudentEnrollment);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}