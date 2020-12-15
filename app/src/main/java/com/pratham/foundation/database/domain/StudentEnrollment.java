package com.pratham.foundation.database.domain;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Student")
public class StudentEnrollment implements Comparable, Parcelable {

    @PrimaryKey()
    @NonNull
    @SerializedName("StudentId")
    private String StudentID;
    @SerializedName("FullName")
    public String FullName;
    @SerializedName("Gender")
    private String Gender;
    @SerializedName("Age")
    private int Age;
    @SerializedName("Class")
    public String Stud_Class;
    @SerializedName("GroupId")
    public String GroupId;
    @SerializedName("GroupName")
    public String GroupName;
    @SerializedName("villageid")
    public int villageId;
    @SerializedName("VillageName")
    public String VillageName;


    protected StudentEnrollment(Parcel in) {
        StudentID = in.readString();
        FullName = in.readString();
        Gender = in.readString();
        Age = in.readInt();
        Stud_Class = in.readString();
        GroupId = in.readString();
        GroupName = in.readString();
        villageId = in.readInt();
        VillageName = in.readString();
    }

    public static final Creator<StudentEnrollment> CREATOR = new Creator<StudentEnrollment>() {
        @Override
        public StudentEnrollment createFromParcel(Parcel in) {
            return new StudentEnrollment(in);
        }

        @Override
        public StudentEnrollment[] newArray(int size) {
            return new StudentEnrollment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(StudentID);
        dest.writeString(FullName);
        dest.writeString(Gender);
        dest.writeInt(Age);
        dest.writeString(Stud_Class);
        dest.writeString(GroupId);
        dest.writeString(GroupName);
        dest.writeInt(villageId);
        dest.writeString(VillageName);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

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

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public String getStud_Class() {
        return Stud_Class;
    }

    public void setStud_Class(String stud_Class) {
        Stud_Class = stud_Class;
    }

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

    public int getVillageId() {
        return villageId;
    }

    public void setVillageId(int villageId) {
        this.villageId = villageId;
    }

    public String getVillageName() {
        return VillageName;
    }

    public void setVillageName(String villageName) {
        VillageName = villageName;
    }

    public static Creator<StudentEnrollment> getCREATOR() {
        return CREATOR;
    }
}
