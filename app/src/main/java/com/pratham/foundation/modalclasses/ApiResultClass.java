package com.pratham.foundation.modalclasses;

/**
 * Created by Anki on 12/11/2018.
 */

public class ApiResultClass {

    String uuid;
    int SyncId;
    String PushId;
    String DeviceId;
    String ScoreCount;
    String ScorePushed;
    String AttendanceCount;
    String AttendancePushed;
    String DatePushed;
    String LastCheckedByApp;
    String PushStatus;
    int isError;
    String errorMsg;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getSyncId() {
        return SyncId;
    }

    public void setSyncId(int syncId) {
        SyncId = syncId;
    }

    public String getPushId() {
        return PushId;
    }

    public void setPushId(String pushId) {
        PushId = pushId;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getScoreCount() {
        return ScoreCount;
    }

    public void setScoreCount(String scoreCount) {
        ScoreCount = scoreCount;
    }

    public String getScorePushed() {
        return ScorePushed;
    }

    public void setScorePushed(String scorePushed) {
        ScorePushed = scorePushed;
    }

    public String getAttendanceCount() {
        return AttendanceCount;
    }

    public void setAttendanceCount(String attendanceCount) {
        AttendanceCount = attendanceCount;
    }

    public String getAttendancePushed() {
        return AttendancePushed;
    }

    public void setAttendancePushed(String attendancePushed) {
        AttendancePushed = attendancePushed;
    }

    public String getDatePushed() {
        return DatePushed;
    }

    public void setDatePushed(String datePushed) {
        DatePushed = datePushed;
    }

    public String getLastCheckedByApp() {
        return LastCheckedByApp;
    }

    public void setLastCheckedByApp(String lastCheckedByApp) {
        LastCheckedByApp = lastCheckedByApp;
    }

    public String getPushStatus() {
        return PushStatus;
    }

    public void setPushStatus(String pushStatus) {
        PushStatus = pushStatus;
    }

    public int getIsError() {
        return isError;
    }

    public void setIsError(int isError) {
        this.isError = isError;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
