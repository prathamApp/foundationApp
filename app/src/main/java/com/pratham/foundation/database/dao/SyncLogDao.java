package com.pratham.foundation.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratham.foundation.modalclasses.SyncLog;

import java.util.List;

@Dao
public interface SyncLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SyncLog syncLog);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSyncLog(SyncLog syncLog);

    @Query("select * from SyncLog where PushId !=0 ORDER BY PushId DESC")
    List<SyncLog> getAllSyncLogs();

    @Query("UPDATE SyncLog set Status =:status where PushId =:pId AND uuid =:uId")
    void updateStatus(int pId, String uId, String status);

}
