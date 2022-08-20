package com.pratham.foundation.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratham.foundation.modalclasses.SyncStatusLog;

import java.util.List;

@Dao
public interface SyncStatusLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SyncStatusLog syncStatusLog);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSyncStatusLog(SyncStatusLog syncStatusLog);

    @Query("select * from SyncStatusLog where PushId !=0")
    List<SyncStatusLog> getAllSyncStatusLogs();

}
