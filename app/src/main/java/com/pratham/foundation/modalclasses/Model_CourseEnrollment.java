package com.pratham.foundation.modalclasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.pratham.foundation.database.domain.ContentTable;

@Entity(tableName = "CourseEnrolled")
public class Model_CourseEnrollment implements Parcelable/*, Comparable */{
    @PrimaryKey(autoGenerate = true)
    @SerializedName("c_autoID")
    private int c_autoID;
    @SerializedName("courseId")
    private String courseId;
    @SerializedName("groupId")
    private String groupId;
    @SerializedName("planFromDate")
    private String planFromDate;
    @SerializedName("planToDate")
    private String planToDate;
    @SerializedName("coachVerified")
    private int coachVerified;
    @SerializedName("coachVerificationDate")
    private String coachVerificationDate;
    @SerializedName("courseExperience")
    private String courseExperience;
    @SerializedName("courseCompleted")
    private boolean courseCompleted;
    @SerializedName("coachImage")
    private String coachImage;
    @SerializedName("language")
    private String language;
    @SerializedName("sentFlag")
    private int sentFlag;
    @Ignore
    private boolean isProgressCompleted = false;
    @Ignore
    private ContentTable courseDetail;
    @Ignore
    private String course_status;

    public Model_CourseEnrollment() {
    }

    public static final Creator<Model_CourseEnrollment> CREATOR = new Creator<Model_CourseEnrollment>() {
        @Override
        public Model_CourseEnrollment createFromParcel(Parcel in) {
            return new Model_CourseEnrollment(in);
        }

        @Override
        public Model_CourseEnrollment[] newArray(int size) {
            return new Model_CourseEnrollment[size];
        }
    };

    protected Model_CourseEnrollment(Parcel in) {
        c_autoID = in.readInt();
        courseId = in.readString();
        groupId = in.readString();
        planFromDate = in.readString();
        planToDate = in.readString();
        coachVerified = in.readInt();
        coachVerificationDate = in.readString();
        courseExperience = in.readString();
        sentFlag = in.readInt();
        courseDetail = in.readParcelable(ContentTable.class.getClassLoader());
        courseCompleted = in.readByte() != 0;
        coachImage = in.readString();
        isProgressCompleted = in.readByte() != 0;
        language = in.readString();
        course_status = in.readString();
    }

    public String getCourse_status() {
        return course_status;
    }

    public void setCourse_status(String course_status) {
        this.course_status = course_status;
    }

    public int getC_autoID() {
        return c_autoID;
    }

    public void setC_autoID(int c_autoID) {
        this.c_autoID = c_autoID;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public int getCoachVerified() {
        return coachVerified;
    }

    public void setCoachVerified(int coachVerified) {
        this.coachVerified = coachVerified;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }

    public String getCoachVerificationDate() {
        return coachVerificationDate;
    }

    public void setCoachVerificationDate(String coachVerificationDate) {
        this.coachVerificationDate = coachVerificationDate;
    }

    public String getCourseExperience() {
        return courseExperience;
    }

    public void setCourseExperience(String courseExperience) {
        this.courseExperience = courseExperience;
    }

    public ContentTable getCourseDetail() {
        return courseDetail;
    }

    public void setCourseDetail(ContentTable courseDetail) {
        this.courseDetail = courseDetail;
    }

    public boolean isCourseCompleted() {
        return courseCompleted;
    }

    public void setCourseCompleted(boolean courseCompleted) {
        this.courseCompleted = courseCompleted;
    }

    public String getCoachImage() {
        return coachImage;
    }

    public void setCoachImage(String coachImage) {
        this.coachImage = coachImage;
    }

    public boolean isProgressCompleted() {
        return isProgressCompleted;
    }

    public void setProgressCompleted(boolean progressCompleted) {
        isProgressCompleted = progressCompleted;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(c_autoID);
        dest.writeString(courseId);
        dest.writeString(groupId);
        dest.writeString(planFromDate);
        dest.writeString(planToDate);
        dest.writeInt(coachVerified);
        dest.writeString(coachVerificationDate);
        dest.writeString(courseExperience);
        dest.writeInt(sentFlag);
        dest.writeByte((byte) (courseCompleted ? 1 : 0));
        dest.writeString(coachImage);
        dest.writeByte((byte) (isProgressCompleted ? 1 : 0));
        dest.writeString(language);
        dest.writeString(course_status);
    }

/*    @Override
    public int compareTo(Object o) {
        Model_CourseEnrollment compare = (Model_CourseEnrollment) o;
        if (compare.getCourseId() != null) {
            if (compare.getCourseId().equalsIgnoreCase(this.courseId) && compare.isCoachVerified() == this.isCoachVerified())
                return 0;
            else return 1;
        } else {
            return 0;
        }
    }*/
}
