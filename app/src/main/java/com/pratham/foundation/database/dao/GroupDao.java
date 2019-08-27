package com.pratham.foundation.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.pratham.foundation.database.domain.Groups;
import java.util.List;

@Dao
public interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllGroups(List<Groups> groupsList);

    @Query("DELETE FROM Groups")
    public void deleteAllGroups();

    @Query("SELECT * FROM Groups ")
    public List<Groups> getAllGroups();

    @Query("SELECT * FROM Groups WHERE VillageID=:vID ORDER BY GroupName ASC")
    public List<Groups> GetGroups(int vID);

    @Query("DELETE FROM Groups WHERE GroupID=:grpID")
    public void deleteGroupByGrpID(String grpID);

    @Query("SELECT * FROM Groups WHERE GroupID=:grpID")
    public Groups getGroupByGrpID(String grpID);

    @Query("SELECT GroupName FROM Groups WHERE GroupID=:grpID")
    public String getGroupNameByGrpID(String grpID);

    @Query("select * from Groups WHERE DeviceID = 'deleted'")
    public List<Groups> GetAllDeletedGroups();

}