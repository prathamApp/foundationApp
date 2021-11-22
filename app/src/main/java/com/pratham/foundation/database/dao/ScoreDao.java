package com.pratham.foundation.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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

    @Query("Select COUNT(*) from Score")
    int getTotalScoreCount();

    @Query("Select COUNT(*) from Score WHERE sentFlag=1")
    int getTotalSuccessfullScorePush();

    @Query("Select COUNT(*) from Score WHERE Label='img_push_lbl'")
    int getTotalImageScorePush();

    @Query("Select COUNT(*) from Score WHERE sentFlag=1 AND Label='img_push_lbl'")
    int getTotalSuccessfullImageScorePush();

    @Query("select * from Score where sentFlag = 0 AND SessionID=:s_id")
    List<Score> getAllNewScores(String s_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addScoreList(List<Score> contentList);

    @Query("update Score set sentFlag=1 where sentFlag=0 AND Label!='img_push_lbl'")
    void setSentFlag();

    @Query("Select sentFlag from Score where StartDateTime=:imgName AND Label='img_push_lbl'")
    int getSentFlag(String imgName);

    @Query("Select ResourceID from Score where StartDateTime=:imgName AND sentFlag=0 AND Label='img_push_lbl'")
    String getImgResId(String imgName);

    @Query("Select COUNT(sentFlag) from Score where sentFlag=0 AND Label='img_push_lbl'")
    int getUnpushedImageCount();

    @Query("update Score set sentFlag=1 where StartDateTime=:imgName AND Label='img_push_lbl'")
    void setImgSentFlag(String imgName);

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

    @Query("select * from Score WHERE StudentID=:currentGroup AND Label LIKE :ImageQuesLbl ")
    List<Score> getImageQuesGroups(String currentGroup, String ImageQuesLbl);

    @Query("select * from Score WHERE StudentID=:stdID AND Label LIKE :COS_Lbl ")
    List<Score> getImageQues(String stdID, String COS_Lbl);


    @Query("Select count(distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01-','1-'),'02-','2-'),'03-','3-'),'04-','4-'),'05-','5-'),'06-','6-'),'07-','7-'),'08','8-'),'09-','9-')) as dates from Score sc where length(startdatetime)>5")
    int getTotalActiveDeviceDays();

/*    @Query("Select count(distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01-','1-'),'02-','2-'),'03-','3-'),'04-','4-'),'05-','5-'),'06-','6-'),'07-','7-'),'08','8-'),'09-','9-')) as dates from Score sc where length(startdatetime)>5 AND StudentID:groupORstudentid")
    int getTotalActiveDeviceDaysById(String groupORstudentid);*/

    @Query("Select count(distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01-','1-'),'02-','2-'),'03-','3-'),'04-','4-'),'05-','5-'),'06-','6-'),'07-','7-'),'08','8-'),'09-','9-')) as dates,at.groupid,g.groupname from Score sc inner join Attendance at on sc.sessionid=at.sessionid inner join Groups g on at.groupid=g.groupid where length(startdatetime)>5 group by at.groupid,g.groupname")
    List<Modal_TotalDaysGroupsPlayed> getTotalDaysGroupsPlayed();

    //    @Query("Select count(distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01-','1-'),'02-','2-'),'03-','3-'),'04-','4-'),'05-','5-'),'06-','6-'),'07-','7-'),'08','8-'),'09-','9-')) as dates,at.studentid, st.fullname from Score sc inner join Attendance at on sc.sessionid=at.sessionid inner join Student st on at.studentid=st.studentid where length(startdatetime)>5 AND at.studentid=:stdid group by at.studentid, st.fullname ")
    @Query("Select count(distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01-','1-'),'02-','2-'),'03-','3-'),'04-','4-'),'05-','5-'),'06-','6-'),'07-','7-'),'08','8-'),'09-','9-')) as dates,at.studentid,st.fullname from Score sc inner join Attendance at on sc.sessionid=at.sessionid inner join Student st on at.studentid=st.studentid where length(startdatetime)>5 and at.studentid=:stdid group by at.studentid,st.fullname")
    List<Modal_TotalDaysStudentsPlayed> getTotalDaysStudentPlayed(String stdid);

    @Query("Select distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01-','1-'),'02-','2-'),'03-','3-'),'04-','4-'),'05-','5-'),'06-','6-'),'07-','7-'),'08','8-'),'09-','9-') as dates,sc.resourceid,tc.nodeTitle, at.studentid,st.fullname from Score sc inner join ContentTable tc on tc.resourceid=sc.resourceid inner join Attendance at on sc.sessionid=at.sessionid inner join Student st on at.studentid=st.studentid where length(startdatetime)>5 and at.studentid=:stdId")
    List<Modal_ResourcePlayedByGroups> getTotalDaysByStudentID(String stdId);

    @Query("Select distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01-','1-'),'02-','2-'),'03-','3-'),'04-','4-'),'05-','5-'),'06-','6-'),'07-','7-'),'08','8-'),'09-','9-') as dates,sc.resourceid,tc.nodeTitle, at.studentid,st.fullname from Score sc inner join ContentTable tc on tc.resourceid=sc.resourceid inner join Attendance at on sc.sessionid=at.sessionid inner join Student st on at.studentid=st.studentid where length(startdatetime)>5 and at.studentid=:stdId")
    List<Modal_TotalDaysStudentsPlayed> getTotalDaysByStudentID2(String stdId);

    @Query("Select distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01-','1-'),'02-','2-'),'03-','3-'),'04-','4-'),'05-','5-'),'06-','6-'),'07-','7-'),'08','8-'),'09-','9-') as dates,sc.resourceid,tc.nodeTitle, at.groupid,g.groupname from Score sc inner join ContentTable tc on tc.resourceid=sc.resourceid inner join Attendance at on sc.sessionid=at.sessionid inner join Groups g on at.groupid=g.groupid where length(startdatetime)>5 and at.groupid=:grpId")
    List<Modal_ResourcePlayedByGroups> getRecourcesPlayedByGroups(String grpId);
}
