package com.pratham.foundation.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratham.foundation.database.domain.ContentProgress;

import java.util.List;


@Dao
public interface ContentProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ContentProgress contentProgress);

    @Query("select * from ContentProgress where studentId=:currentStudentID AND resourceId=:resourceId AND label=:label")
    List<ContentProgress> getContentNodeProgress(String currentStudentID, String resourceId, String label);

    @Query("select * from ContentProgress where sentFlag=0")
    List<ContentProgress> getAllContentNodeProgress();

    @Query("update ContentProgress set sentFlag=1 where sentFlag=0")
    void setSentFlag();

    @Query("update ContentProgress set progressPercentage='100' where CAST(progressPercentage as float)>100")
    void updateFullPercent();

    @Query("select MAX(CAST(progressPercentage as float)) from ContentProgress where studentId=:sId AND resourceId=:resId")
    float getResPercentage(String sId, String resId);

    @Insert
    void addContentProgressList(List<ContentProgress> contentProgressList);

    @Query("select * from ContentProgress where studentId=:currentStudentID AND resourceId=:resourceId AND label=:resourceProgress")
    List<ContentProgress> getProgressByStudIDAndResID(String currentStudentID, String resourceId, String resourceProgress);
}