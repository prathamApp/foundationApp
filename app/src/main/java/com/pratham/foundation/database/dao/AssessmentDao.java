package com.pratham.foundation.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pratham.foundation.database.domain.Assessment;

import java.util.List;


@Dao
public interface AssessmentDao {

    @Insert
    long insert(Assessment assessment);

    @Insert
    long[] insertAll(Assessment... assessments);

    @Update
    int update(Assessment assessment);

    @Delete
    void delete(Assessment assessment);

    @Delete
    void deleteAll(Assessment... assessments);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAssessmentList(List<Assessment> contentList);

    @Query("select * from Assessment where sentFlag=0")
    List<Assessment> getAllAssessment();

    @Query("DELETE FROM Assessment")
    void deleteAllAssessment();

    @Query("select * from Assessment WHERE StudentIDa=:stdID AND Labela=:COS_Lbl ORDER BY EndDateTimea DESC")
    List<Assessment> getCertificates(String stdID, String COS_Lbl);

    @Query("select count(*) from Assessment WHERE StudentIDa=:stdID")
    int getAssessmentCount(String stdID);

    @Query("update Assessment set sentFlag=1 where sentFlag=0")
    void setSentFlag();

    @Query("select * from Assessment WHERE StartDateTimea=:currentGroup AND Labela=:certificateLbl ORDER BY EndDateTimea DESC")
    List<Assessment> getCertificatesGroups(String currentGroup, String certificateLbl);

    @Query("delete from Assessment where sentFlag = 1")
    void deletePushedAssessment();
}
