package com.pratham.foundation.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pratham.foundation.database.domain.KeyWords;

import java.util.List;

@Dao
public interface KeyWordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(KeyWords learntWord);

    @Insert
    long[] insertAll(KeyWords... LearntWord);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertWord(List<KeyWords> wordList);

    @Insert
    void insertAllWord(List<KeyWords> wordList);

    @Update
    int update(KeyWords word);

    @Delete
    void delete(KeyWords word);

    @Delete
    void deleteAll(KeyWords... words);

    @Query("select * from KeyWords where studentId= :studentId")
    KeyWords getWordOfStudent(String studentId);

/*    @Query("select * from KeyWords.java where studentId= :studentId and synId=:synId and learntWordId = :wordId")
    KeyWords.java checkWordOfStudent(String studentId, String synId, String wordId);*/

    @Query("select * from KeyWords where sentFlag=0")
    List<KeyWords> getAllData();

    @Query("select * from KeyWords where sentFlag=0")
    List<KeyWords> getAllDataShare();

    @Query("select * from KeyWords WHERE studentId=:stdID AND wordType='word'")
    List<KeyWords> getKeyWords(String stdID);

    @Query("select keyWord from KeyWords WHERE studentId=:stdID AND wordType='word' AND resourceId=:resId AND keyWord=:checkWord")
    String checkWord(String stdID, String resId, String checkWord);

    @Query("select keyWord from KeyWords WHERE studentId=:stdID AND wordType=:type AND resourceId=:resId AND keyWord=:checkWord")
    String checkEntry(String stdID, String resId, String checkWord, String type);

    @Query("select keyWord from KeyWords WHERE studentId=:stdID AND wordType=:wordType AND resourceId=:resId AND keyWord=:checkWord")
    String checkLearntData(String stdID, String resId, String checkWord, String wordType);

    @Query("select count() from KeyWords WHERE studentId=:stdID AND wordType='word' AND resourceId=:resId")
    int checkWordCount(String stdID, String resId);

    @Query("select count(DISTINCT keyWord ) from KeyWords WHERE studentId=:stdID AND wordType='word' AND resourceId=:resId")
    int checkUniqueWordCount(String stdID, String resId);

    @Query("select count() from KeyWords WHERE studentId=:stdID AND wordType='word' AND resourceId=:resId")
    int checkWordCountWithSession(String stdID, String resId);

    @Query("select count() from KeyWords WHERE studentId=:stdID AND resourceId=:resId")
    int checkWebWordCount(String stdID, String resId);

    @Query("select * from KeyWords WHERE studentId=:stdID AND wordType='sentence'")
    List<KeyWords> getLearntSentences(String stdID);

    @Query("select count(*) from KeyWords WHERE studentId=:stdID AND wordType='sentence'")
    int getLearntSentenceCount(String stdID);

    @Query("select count(*) from KeyWords WHERE studentId=:stdID AND wordType='word'")
    int getLearntWordCount(String stdID);

    @Query("update KeyWords set sentFlag=1 where sentFlag=0")
    void setSentFlag();

}
