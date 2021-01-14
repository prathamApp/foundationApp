package com.pratham.foundation.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pratham.foundation.database.domain.Student;

import java.util.List;

@Dao
public interface StudentDao {

    @Insert
    long insert(Student student);

    /*@Insert
    long[] insertAll(Student... students);*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<Student> studentList);

    @Update
    int update(Student student);

    @Delete
    void delete(Student student);

    /*  @Delete
      void deleteAll(Student... students);
  */
    @Query("DELETE FROM Student")
    void deleteAll();

    @Query("select * from Student where StudentID = :studentID")
    Student getStudent(String studentID);

    @Query("select * from Student where StudentID = :studentID")
    Student addStudent(String studentID);

    @Query("select * from Student")
    List<Student> getAllStudents();

    @Query("select * from Student where GroupId='PS' OR StudentUID='PS' OR MiddleName='PS'")
    List<Student> getAllPSStudents();

    @Query("SELECT * FROM Student WHERE GroupId=:gID")
    List<Student> getGroupwiseStudents(String gID);

    /*@Query("select * from Student where newFlag = 1")
    List<Student> getAllNewStudents();*/

    @Query("select * from Student where newFlag = 0")
    List<Student> getAllNewStudents();

    @Query("update Student set newFlag=0 where newFlag = 1")
    void setNewStudentsToOld();

    @Query("update Student set newFlag=0 where StudentID = :studentID")
    void setFlagFalse(String studentID);

    @Query("select FirstName from Student where StudentID = :studentID")
    String getStudentName(String studentID);

    @Query("select FullName from Student where StudentID = :studentID")
    String getFullName(String studentID);

    @Query("select GroupName  from Student where GroupId = :groupID")
    String getGroupName(String groupID);

    @Query("select FirstName from Student where StudentID = :studentID")
    String checkStudent(String studentID);

    @Query("select avatarName from Student where StudentID = :studentID")
    String getStudentAvatar(String studentID);

    @Query("select Gender from Student where StudentID = :studentID")
    String getStudentGender(String studentID);

    @Query("DELETE FROM Student WHERE GroupID=:grpID")
    void deleteDeletedGrpsStdRecords(String grpID);

    @Query("DELETE FROM Student WHERE Gender='Deleted'")
    void deleteDeletedStdRecords();

    @Query("update Student set newFlag=1 where newFlag=0")
    void setSentFlag();

    @Query("DELETE FROM Student WHERE GroupID != 'PS'")
    void deletePrathamAll();
}
