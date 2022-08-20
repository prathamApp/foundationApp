package com.pratham.foundation.modalclasses;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "SyncStatusLog")
public class SyncStatusLog implements Serializable, Comparable, Parcelable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int syncStatusId;
    public int SyncId;
    public String uuid;
    public int PushId;
    public String PushDate;
    public String PushStatus;
    public String DeviceId;
    public int ScorePushed;
    public int ScoreSynced;
    public int ScoreError;
    public int AttendancePushed;
    public int AttendanceSynced;
    public int AttendanceError;
    public int StudentPushed;
    public int StudentSynced;
    public int StudentError;
    public int SessionCount;
    public int SessionSynced;
    public int SessionError;
    public int cpCount;
    public int cpSynced;
    public int cpError;
    public int logsCount;
    public int logsSynced;
    public int logsError;
    public int KeywordsCount;
    public int KeywordsSynced;
    public int KeywordsError;
    public int CourseEnrollmentCount ;
    public int CourseEnrollmentSynced;
    public int CourseEnrollmentError;
    public int GroupsDataCount ;
    public int GroupsDataSynced;
    public int GroupsDataError;
    public String LastChecked;
    public String Error;
    public int sentFlag;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeInt(this.syncStatusId);
        parcel.writeInt(this.SyncId);
        parcel.writeString(this.uuid);
        parcel.writeInt(this.PushId);
        parcel.writeString(this.PushDate);
        parcel.writeString(this.PushStatus);
        parcel.writeString(this.DeviceId);
        parcel.writeInt(this.ScorePushed);
        parcel.writeInt(this.ScoreSynced);
        parcel.writeInt(this.ScoreError);
        parcel.writeInt(this.AttendancePushed);
        parcel.writeInt(this.AttendanceSynced);
        parcel.writeInt(this.AttendanceError);
        parcel.writeInt(this.StudentPushed);
        parcel.writeInt(this.StudentSynced);
        parcel.writeInt(this.StudentError);
        parcel.writeInt(this.SessionCount);
        parcel.writeInt(this.SessionSynced);
        parcel.writeInt(this.SessionError);
        parcel.writeInt(this.cpCount);
        parcel.writeInt(this.cpSynced);
        parcel.writeInt(this.cpError);
        parcel.writeInt(this.logsCount);
        parcel.writeInt(this.logsSynced);
        parcel.writeInt(this.logsError);
        parcel.writeInt(this.KeywordsCount);
        parcel.writeInt(this.KeywordsSynced);
        parcel.writeInt(this.KeywordsError);
        parcel.writeInt(this.CourseEnrollmentCount);
        parcel.writeInt(this.CourseEnrollmentSynced);
        parcel.writeInt(this.CourseEnrollmentError);
        parcel.writeInt(this.GroupsDataCount);
        parcel.writeInt(this.GroupsDataSynced);
        parcel.writeInt(this.GroupsDataError);
        parcel.writeString(this.LastChecked);
        parcel.writeString(this.Error);
        parcel.writeInt(this.sentFlag);

    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    public int getSyncStatusId() {
        return syncStatusId;
    }

    public void setSyncStatusId(int syncStatusId) {
        this.syncStatusId = syncStatusId;
    }

    public int getSyncId() {
        return SyncId;
    }

    public void setSyncId(int syncId) {
        SyncId = syncId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getPushId() {
        return PushId;
    }

    public void setPushId(int pushId) {
        PushId = pushId;
    }

    public String getPushDate() {
        return PushDate;
    }

    public void setPushDate(String pushDate) {
        PushDate = pushDate;
    }

    public String getPushStatus() {
        return PushStatus;
    }

    public void setPushStatus(String pushStatus) {
        PushStatus = pushStatus;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public int getScorePushed() {
        return ScorePushed;
    }

    public void setScorePushed(int scorePushed) {
        ScorePushed = scorePushed;
    }

    public int getScoreSynced() {
        return ScoreSynced;
    }

    public void setScoreSynced(int scoreSynced) {
        ScoreSynced = scoreSynced;
    }

    public int getScoreError() {
        return ScoreError;
    }

    public void setScoreError(int scoreError) {
        ScoreError = scoreError;
    }

    public int getAttendancePushed() {
        return AttendancePushed;
    }

    public void setAttendancePushed(int attendancePushed) {
        AttendancePushed = attendancePushed;
    }

    public int getAttendanceSynced() {
        return AttendanceSynced;
    }

    public void setAttendanceSynced(int attendanceSynced) {
        AttendanceSynced = attendanceSynced;
    }

    public int getAttendanceError() {
        return AttendanceError;
    }

    public void setAttendanceError(int attendanceError) {
        AttendanceError = attendanceError;
    }

    public int getStudentPushed() {
        return StudentPushed;
    }

    public void setStudentPushed(int studentPushed) {
        StudentPushed = studentPushed;
    }

    public int getStudentSynced() {
        return StudentSynced;
    }

    public void setStudentSynced(int studentSynced) {
        StudentSynced = studentSynced;
    }

    public int getStudentError() {
        return StudentError;
    }

    public void setStudentError(int studentError) {
        StudentError = studentError;
    }

    public int getSessionCount() {
        return SessionCount;
    }

    public void setSessionCount(int sessionCount) {
        SessionCount = sessionCount;
    }

    public int getSessionSynced() {
        return SessionSynced;
    }

    public void setSessionSynced(int sessionSynced) {
        SessionSynced = sessionSynced;
    }

    public int getSessionError() {
        return SessionError;
    }

    public void setSessionError(int sessionError) {
        SessionError = sessionError;
    }

    public int getCpCount() {
        return cpCount;
    }

    public void setCpCount(int cpCount) {
        this.cpCount = cpCount;
    }

    public int getCpSynced() {
        return cpSynced;
    }

    public void setCpSynced(int cpSynced) {
        this.cpSynced = cpSynced;
    }

    public int getCpError() {
        return cpError;
    }

    public void setCpError(int cpError) {
        this.cpError = cpError;
    }

    public int getLogsCount() {
        return logsCount;
    }

    public void setLogsCount(int logsCount) {
        this.logsCount = logsCount;
    }

    public int getLogsSynced() {
        return logsSynced;
    }

    public void setLogsSynced(int logsSynced) {
        this.logsSynced = logsSynced;
    }

    public int getLogsError() {
        return logsError;
    }

    public void setLogsError(int logsError) {
        this.logsError = logsError;
    }

    public int getKeywordsCount() {
        return KeywordsCount;
    }

    public void setKeywordsCount(int keywordsCount) {
        KeywordsCount = keywordsCount;
    }

    public int getKeywordsSynced() {
        return KeywordsSynced;
    }

    public void setKeywordsSynced(int keywordsSynced) {
        KeywordsSynced = keywordsSynced;
    }

    public int getKeywordsError() {
        return KeywordsError;
    }

    public void setKeywordsError(int keywordsError) {
        KeywordsError = keywordsError;
    }

    public int getCourseEnrollmentCount() {
        return CourseEnrollmentCount;
    }

    public void setCourseEnrollmentCount(int courseEnrollmentCount) {
        CourseEnrollmentCount = courseEnrollmentCount;
    }

    public int getCourseEnrollmentSynced() {
        return CourseEnrollmentSynced;
    }

    public void setCourseEnrollmentSynced(int courseEnrollmentSynced) {
        CourseEnrollmentSynced = courseEnrollmentSynced;
    }

    public int getCourseEnrollmentError() {
        return CourseEnrollmentError;
    }

    public void setCourseEnrollmentError(int courseEnrollmentError) {
        CourseEnrollmentError = courseEnrollmentError;
    }

    public int getGroupsDataCount() {
        return GroupsDataCount;
    }

    public void setGroupsDataCount(int groupsDataCount) {
        GroupsDataCount = groupsDataCount;
    }

    public int getGroupsDataSynced() {
        return GroupsDataSynced;
    }

    public void setGroupsDataSynced(int groupsDataSynced) {
        GroupsDataSynced = groupsDataSynced;
    }

    public int getGroupsDataError() {
        return GroupsDataError;
    }

    public void setGroupsDataError(int groupsDataError) {
        GroupsDataError = groupsDataError;
    }

    public String getLastChecked() {
        return LastChecked;
    }

    public void setLastChecked(String lastChecked) {
        LastChecked = lastChecked;
    }

    public String getError() {
        return Error;
    }

    public void setError(String error) {
        Error = error;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}