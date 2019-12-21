package com.pratham.foundation.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.Modal_ResourcePlayedByGroups;
import com.pratham.foundation.modalclasses.Modal_TotalDaysGroupsPlayed;
import com.pratham.foundation.modalclasses.Modal_TotalDaysStudentsPlayed;

import java.util.List;


@Dao
public interface ScoreDao {

    @Insert
    long insert(Score score);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Score> score);

    @Update
    int update(Score score);

    @Delete
    void delete(Score score);

    @Delete
    void deleteAll(Score... scores);

    @Query("select * from Score")
    List<Score> getAllScores();

    @Query("select * from Score where sentFlag=0")
    List<Score> getAllPushScores();


    @Query("DELETE FROM Score")
    void deleteAllScores();

    @Query("select * from Score where sentFlag = 0 AND SessionID=:s_id")
    List<Score> getAllNewScores(String s_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addScoreList(List<Score> contentList);

    @Query("update Score set sentFlag=1 where sentFlag=0")
    void setSentFlag();

    @Query("Select MAX(ScoredMarks) from Score where StudentID=:stdID AND Label='RC-sessionTotalScore '")
    int getRCHighScore(String stdID);

    @Query("select * from Score where StudentID=:stdID AND Label='RC-sessionTotalScore '")
    List<Score> getScoreByStdID(String stdID);

    @Query("select * from Score where Label='RC-sessionTotalScore '")
    List<Score> getScoreOfRCsessionTotalScore();

    @Query("select * from Score where StudentID=:currentStudentID AND ResourceID=:resourceId AND Label=:label")
    List<Score> getScoreByStudIDAndResID(String currentStudentID, String resourceId, String label);

    @Query("select * from Score where sentFlag = 0 ")
    List<Score> getAllNotSentScores();

    @Query("delete from Score where sentFlag = 1")
    void deletePushedScore();

    @Query("Select count(distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01','1'),'02','2'),'03','3'),'04','4'),'05','5'),'06','6'),'07','7'),'08','8'),'09','9')) as dates from Score sc where length(startdatetime)>5")
    int getTotalActiveDeviceDays();

/*    @Query("Select count(distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01','1'),'02','2'),'03','3'),'04','4'),'05','5'),'06','6'),'07','7'),'08','8'),'09','9')) as dates from Score sc where length(startdatetime)>5 AND StudentID:groupORstudentid")
    int getTotalActiveDeviceDaysById(String groupORstudentid);*/

    @Query("Select count(distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01','1'),'02','2'),'03','3'),'04','4'),'05','5'),'06','6'),'07','7'),'08','8'),'09','9')) as dates,at.groupid,g.groupname from Score sc inner join Attendance at on sc.sessionid=at.sessionid inner join Groups g on at.groupid=g.groupid where length(startdatetime)>5 group by at.groupid,g.groupname")
    List<Modal_TotalDaysGroupsPlayed> getTotalDaysGroupsPlayed();

    //    @Query("Select count(distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01','1'),'02','2'),'03','3'),'04','4'),'05','5'),'06','6'),'07','7'),'08','8'),'09','9')) as dates,at.studentid, st.fullname from Score sc inner join Attendance at on sc.sessionid=at.sessionid inner join Student st on at.studentid=st.studentid where length(startdatetime)>5 AND at.studentid=:stdid group by at.studentid, st.fullname ")
    @Query("Select count(distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01','1'),'02','2'),'03','3'),'04','4'),'05','5'),'06','6'),'07','7'),'08','8'),'09','9')) as dates,at.studentid,st.fullname from Score sc inner join Attendance at on sc.sessionid=at.sessionid inner join Student st on at.studentid=st.studentid where length(startdatetime)>5 and at.studentid=:stdid group by at.studentid,st.fullname")
    List<Modal_TotalDaysStudentsPlayed> getTotalDaysStudentPlayed(String stdid);

    @Query("Select distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01','1'),'02','2'),'03','3'),'04','4'),'05','5'),'06','6'),'07','7'),'08','8'),'09','9') as dates,sc.resourceid,tc.nodeTitle, at.studentid,st.fullname from Score sc inner join ContentTable tc on tc.resourceid=sc.resourceid inner join Attendance at on sc.sessionid=at.sessionid inner join Student st on at.studentid=st.studentid where length(startdatetime)>5 and at.studentid=:stdId")
    List<Modal_TotalDaysStudentsPlayed> getTotalDaysByStudentID(String stdId);

    @Query("Select distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01','1'),'02','2'),'03','3'),'04','4'),'05','5'),'06','6'),'07','7'),'08','8'),'09','9') as dates,sc.resourceid,tc.nodeTitle, at.groupid,g.groupname from Score sc inner join ContentTable tc on tc.resourceid=sc.resourceid inner join Attendance at on sc.sessionid=at.sessionid inner join Groups g on at.groupid=g.groupid where length(startdatetime)>5 and at.groupid=:grpId")
    List<Modal_ResourcePlayedByGroups> getRecourcesPlayedByGroups(String grpId);
}
