package com.pratham.foundation.database.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StudentEnrollmentModel implements Comparable, Parcelable {

    @SerializedName("GroupId")
    private String GroupId;
    @SerializedName("GroupEnrollment")
    private String GroupEnrollment;
    @SerializedName("GroupName")
    private String GroupName;
    @SerializedName("GroupCode")
    private String GroupCode;
    @SerializedName("DeviceId")
    private String DeviceId;
    @SerializedName("VIllageName")
    private String VIllageName;
    @SerializedName("SchoolName")
    private String SchoolName;
    @SerializedName("EnrollmentType")
    private String EnrollmentType;
    @SerializedName("VillageId")
    private String VillageId;
    @SerializedName("ProgramId")
    private int ProgramId;
    @SerializedName("lstCourseEnroll")
    private List<LstCourseEnroll> lstCourseEnroll;
    @SerializedName("lstStudent")
    private List<LstStudent> lstStudent;

    public StudentEnrollmentModel(Parcel in) {
        GroupId = in.readString();
        GroupEnrollment = in.readString();
        GroupName = in.readString();
        GroupCode = in.readString();
        DeviceId = in.readString();
        VIllageName = in.readString();
        SchoolName = in.readString();
        EnrollmentType = in.readString();
        VillageId = in.readString();
        ProgramId = in.readInt();
        lstCourseEnroll = in.createTypedArrayList(LstCourseEnroll.CREATOR);
        lstStudent = in.createTypedArrayList(LstStudent.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(GroupId);
        dest.writeString(GroupEnrollment);
        dest.writeString(GroupName);
        dest.writeString(GroupCode);
        dest.writeString(DeviceId);
        dest.writeString(VIllageName);
        dest.writeString(SchoolName);
        dest.writeString(EnrollmentType);
        dest.writeString(VillageId);
        dest.writeInt(ProgramId);
        dest.writeTypedList(lstCourseEnroll);
        dest.writeTypedList(lstStudent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StudentEnrollmentModel> CREATOR = new Creator<StudentEnrollmentModel>() {
        @Override
        public StudentEnrollmentModel createFromParcel(Parcel in) {
            return new StudentEnrollmentModel(in);
        }

        @Override
        public StudentEnrollmentModel[] newArray(int size) {
            return new StudentEnrollmentModel[size];
        }
    };

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getGroupEnrollment() {
        return GroupEnrollment;
    }

    public void setGroupEnrollment(String groupEnrollment) {
        GroupEnrollment = groupEnrollment;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getGroupCode() {
        return GroupCode;
    }

    public void setGroupCode(String groupCode) {
        GroupCode = groupCode;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getVIllageName() {
        return VIllageName;
    }

    public void setVIllageName(String VIllageName) {
        this.VIllageName = VIllageName;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public String getEnrollmentType() {
        return EnrollmentType;
    }

    public void setEnrollmentType(String enrollmentType) {
        EnrollmentType = enrollmentType;
    }

    public String getVillageId() {
        return VillageId;
    }

    public void setVillageId(String villageId) {
        VillageId = villageId;
    }

    public int getProgramId() {
        return ProgramId;
    }

    public void setProgramId(int programId) {
        ProgramId = programId;
    }

    public List<LstCourseEnroll> getLstCourseEnroll() {
        return lstCourseEnroll;
    }

    public void setLstCourseEnroll(List<LstCourseEnroll> lstCourseEnroll) {
        this.lstCourseEnroll = lstCourseEnroll;
    }

    public List<LstStudent> getLstStudent() {
        return lstStudent;
    }

    public void setLstStudent(List<LstStudent> lstStudent) {
        this.lstStudent = lstStudent;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}