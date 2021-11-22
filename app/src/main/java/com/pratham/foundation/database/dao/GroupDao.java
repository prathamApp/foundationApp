package com.pratham.foundation.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratham.foundation.database.domain.Groups;

import java.util.List;

@Dao
public interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllGroups(List<Groups> groupsList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Groups groups);

    @Query("DELETE FROM Groups")
    void deleteAllGroups();

    @Query("SELECT * FROM Groups ")
    List<Groups> getAllGroups();

    @Query("SELECT * FROM Groups WHERE VillageID=:vID ORDER BY GroupName ASC")
    List<Groups> GetGroups(int vID);

    @Query("SELECT VIllageName FROM Groups WHERE GroupId=:gID")
    String getVillagebyId(String gID);

    @Query("SELECT * FROM Groups WHERE GroupId=:gID")
    List<Groups> GetStudentsByGroupId(String gID);

    @Query("DELETE FROM Groups WHERE GroupID=:grpID")
    void deleteGroupByGrpID(String grpID);

    @Query("SELECT * FROM Groups WHERE GroupID=:grpID")
    Groups getGroupByGrpID(String grpID);

    @Query("SELECT GroupName FROM Groups WHERE GroupID=:grpID")
    String getGroupNameByGrpID(String grpID);

    @Query("select * from Groups WHERE DeviceID = 'deleted'")
    List<Groups> GetAllDeletedGroups();

}