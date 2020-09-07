package com.pratham.foundation.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pratham.foundation.database.domain.Attendance;

import java.util.List;

@Dao
public interface AttendanceDao {

    @Insert
    long insert(Attendance attendance);

    @Insert
    long[] insertAll(Attendance... attendances);

    @Insert
    long[] insertAll(List<Attendance> attendances);

    @Update
    int update(Attendance attendance);

    @Delete
    void delete(Attendance attendance);

    @Delete
    void deleteAll(Attendance... attendances);

    @Query("select * from Attendance")
    List<Attendance> getAllAttendanceEntries();

    @Query("select * from Attendance where sentFlag=0")
        List<Attendance> getAllPushAttendanceEntries();

    @Query("select StudentID from Attendance where SessionID = :sessionId")
    String getStudentBySession(String sessionId);

    @Query("SELECT * FROM Attendance WHERE sentFlag=0 AND SessionID=:s_id")
    List<Attendance> getNewAttendances(String s_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAttendanceList(List<Attendance> contentList);

    @Query("update Attendance set sentFlag=1 where sentFlag=0")
    void setSentFlag();

    @Query("SELECT * FROM Attendance WHERE sentFlag=0")
    List<Attendance> getAllUnSentAttendances();
}
