package com.pratham.foundation.modalclasses;

import androidx.room.ColumnInfo;

public class Modal_TotalDaysGroupsPlayed {
    @ColumnInfo(name = "dates")
    public String dates;
    @ColumnInfo(name = "GroupID")
    public String GroupID;
    @ColumnInfo(name = "GroupName")
    public String GroupName;

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }
}
