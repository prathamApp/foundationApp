package com.pratham.foundation.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.utility.FC_Constants;

import java.util.List;

@Dao
public interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLog(Modal_Log log);

    @Query("DELETE FROM Logs")
    void deleteLogs();

    @Query("select * from Logs")
    List<Modal_Log> getAllLogs();

    @Query("select * from Logs where sentFlag=0 AND (exceptionMessage NOT IN ('TEMP_SYNC_RESPONSE') OR exceptionMessage IS NULL)")
    List<Modal_Log> getPushAllLogs();

    @Query("select methodName from Logs where exceptionMessage='TEMP_SYNC_RESPONSE'")
    List<String> getSyncedStatusLogs();

    @Query("select * from Logs where sentFlag=0 AND sessionId=:s_id")
    List<Modal_Log> getAllLogs(String s_id);

    @Query("select * from Logs where exceptionMessage='"+ FC_Constants.APP_MANUAL_SYNC +
            "' OR exceptionMessage='"+ FC_Constants.APP_AUTO_SYNC +"'" +
            "OR exceptionMessage='"+ FC_Constants.DB_ZIP_PUSH +"' ORDER BY logId DESC")
    List<Modal_Log> getSyncedLogs();

    @Query("update Logs set sentFlag=1 where sentFlag=0")
    void setSentFlag();

    @Query("update Logs set errorType=:pushStatus where methodName=:pushID")
    void setPushStatus(String pushStatus, String pushID);

    @Query("update Logs set LogDetail=:pushData where methodName=:pushID")
    void setPushDataLog(String pushData, String pushID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllLogs(List<Modal_Log> log);

    @Query("delete from Logs where sentFlag = 1")
    void deletePushedLogs();

}