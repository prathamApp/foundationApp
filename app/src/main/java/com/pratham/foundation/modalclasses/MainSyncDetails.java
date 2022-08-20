package com.pratham.foundation.modalclasses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Anki on 12/11/2018.
 */

public class MainSyncDetails {

    @SerializedName("Summary")
    public SyncSummaryModal SyncSummary;
    public List<StudentSyncDetails> StudentData;
    public List<StudentUsageSyncDetails> Details;

    public SyncSummaryModal getSyncSummary() {
        return SyncSummary;
    }

    public void setSyncSummary(SyncSummaryModal syncSummary) {
        SyncSummary = syncSummary;
    }

    public List<StudentSyncDetails> getStudentData() {
        return StudentData;
    }

    public void setStudentData(List<StudentSyncDetails> studentData) {
        StudentData = studentData;
    }

    public List<StudentUsageSyncDetails> getDetails() {
        return Details;
    }

    public void setDetails(List<StudentUsageSyncDetails> details) {
        Details = details;
    }
}
