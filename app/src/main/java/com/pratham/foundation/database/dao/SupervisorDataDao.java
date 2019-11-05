package com.pratham.foundation.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.foundation.database.domain.SupervisorData;

import java.util.List;


@Dao
public interface SupervisorDataDao {

    @Insert
    long insert(SupervisorData supervisorData);

    @Query("DELETE FROM SupervisorData")
    void deleteAllSupervisorData();

    @Query("SELECT * FROM SupervisorData where sentFlag=0")
    List<SupervisorData> getAllSupervisorData();

    @Query("SELECT * FROM SupervisorData where supervisorId = :supervisorId")
    SupervisorData getSupervisorById(String supervisorId);

    @Query("SELECT * FROM SupervisorData where assessmentSessionId = :assessmentSessionId")
    List<SupervisorData> getSupervisorBySession(String assessmentSessionId);

    @Query("update SupervisorData set sentFlag=1 where sentFlag=0")
    void setSentFlag();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<SupervisorData> supervisorDataList);

    @Query("delete from SupervisorData where sentFlag = 1")
    void deletePushedSupervisorData();
}