package com.pratham.foundation.database.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class LstCourseEnroll implements Comparable, Parcelable {

    @SerializedName("CourseId")
    private String CourseId;
    @SerializedName("planFromDate")
    private String planFromDate;
    @SerializedName("planToDate")
    private String planToDate;
    @SerializedName("GroupId")
    private String GroupId;
    @SerializedName("language")
    private String Language;

    protected LstCourseEnroll(Parcel in) {
        CourseId = in.readString();
        planFromDate = in.readString();
        planToDate = in.readString();
        GroupId = in.readString();
        Language = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(CourseId);
        dest.writeString(planFromDate);
        dest.writeString(planToDate);
        dest.writeString(GroupId);
        dest.writeString(Language);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LstCourseEnroll> CREATOR = new Creator<LstCourseEnroll>() {
        @Override
        public LstCourseEnroll createFromParcel(Parcel in) {
            return new LstCourseEnroll(in);
        }

        @Override
        public LstCourseEnroll[] newArray(int size) {
            return new LstCourseEnroll[size];
        }
    };

    public String getCourseId() {
        return CourseId;
    }

    public void setCourseId(String courseId) {
        CourseId = courseId;
    }

    public String getPlanFromDate() {
        return planFromDate;
    }

    public void setPlanFromDate(String planFromDate) {
        this.planFromDate = planFromDate;
    }

    public String getPlanToDate() {
        return planToDate;
    }

    public void setPlanToDate(String planToDate) {
        this.planToDate = planToDate;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
