
package com.pratham.foundation.modalclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SyncLogDataModal {

    @SerializedName("sync_time")
    @Expose
    private String sync_time;
    @SerializedName("sync_course_enrollment_length")
    @Expose
    private String sync_course_enrollment_length;
    @SerializedName("sync_data_length")
    @Expose
    private String sync_data_length;
    @SerializedName("sync_media_length")
    @Expose
    private String sync_media_length;
    @SerializedName("ScoreTable")
    @Expose
    private String ScoreTable;
    @SerializedName("MediaCount")
    @Expose
    private String MediaCount;
    @SerializedName("CoursesCount")
    @Expose
    private String CoursesCount;

    public String getSync_time() {
        return sync_time;
    }

    public void setSync_time(String sync_time) {
        this.sync_time = sync_time;
    }

    public String getSync_course_enrollment_length() {
        return sync_course_enrollment_length;
    }

    public void setSync_course_enrollment_length(String sync_course_enrollment_length) {
        this.sync_course_enrollment_length = sync_course_enrollment_length;
    }

    public String getSync_data_length() {
        return sync_data_length;
    }

    public void setSync_data_length(String sync_data_length) {
        this.sync_data_length = sync_data_length;
    }

    public String getSync_media_length() {
        return sync_media_length;
    }

    public void setSync_media_length(String sync_media_length) {
        this.sync_media_length = sync_media_length;
    }

    public String getScoreTable() {
        return ScoreTable;
    }

    public void setScoreTable(String scoreTable) {
        ScoreTable = scoreTable;
    }

    public String getMediaCount() {
        return MediaCount;
    }

    public void setMediaCount(String mediaCount) {
        MediaCount = mediaCount;
    }

    public String getCoursesCount() {
        return CoursesCount;
    }

    public void setCoursesCount(String coursesCount) {
        CoursesCount = coursesCount;
    }
}
