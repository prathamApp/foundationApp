package com.pratham.foundation.modalclasses;

import android.arch.persistence.room.ColumnInfo;

public class Modal_ResourcePlayedByGroups {
    @ColumnInfo(name = "dates")
    public String dates;
    @ColumnInfo(name = "ResourceID")
    public String ResourceID;
    @ColumnInfo(name = "nodetitle")
    public String nodetitle;
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

    public String getResourceID() {
        return ResourceID;
    }

    public void setResourceID(String resourceID) {
        ResourceID = resourceID;
    }

    public String getNodetitle() {
        return nodetitle;
    }

    public void setNodetitle(String nodetitle) {
        this.nodetitle = nodetitle;
    }
}
