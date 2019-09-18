package com.pratham.foundation.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.pratham.foundation.database.domain.WordEnglish;

import java.util.List;

@Dao
public interface EnglishWordDao {

    @Insert
    long insert(WordEnglish word);

    @Insert
    long[] insertAll(WordEnglish... words);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertWordList(List<WordEnglish> wordList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertEnglishWordList(List<WordEnglish> wordList);

    @Update
    int update(WordEnglish word);

    @Delete
    void delete(WordEnglish word);

    @Delete
    void deleteAll(WordEnglish... words);

    @Query("select * from WordEnglish where uuid = :uuid")
    WordEnglish getWord(String uuid);

    @Query("select * from WordEnglish")
    List<WordEnglish> getAllWords();

    @Query("select count(*) from WordEnglish")
    int getWordCount();

/*
    @Query("select * from ContentWord where vowelCnt_matraCnt=:matraCnt and size=:size and blendCnt_jodAksharCnt=0 ORDER BY RANDOM() LIMIT 5")
    List<ContentWord> getLevelWiseWordsWithoutJodakshar(int matraCnt, int size);

    @Query("select * from ContentWord where vowelCnt_matraCnt=:matraCnt and size=:size and blendCnt_jodAksharCnt>0 ORDER BY RANDOM() LIMIT 5")
    List<ContentWord> getLevelWiseWordsWithJodakshar(int matraCnt, int size);
*/
/*

    @Query("select * from WordEnglish where size=:size and vowelCnt=:vowelCnt and word like :vowel ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelOneWords(int size, int vowelCnt, String vowel);

    @Query("select * from WordEnglish where (size=:size1 or size=:size2)  and vowelCnt=:vowelCnt and word like :vowel  ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelTwoWords(int size1, int size2, int vowelCnt, String vowel);

    @Query("select * from WordEnglish where size=:size and vowelCnt!=0 and blendCnt=:blendCnt and word like :vowel ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelThreeWords(int size, int blendCnt, String vowel);

    @Query("select * from WordEnglish where size=:size and blendCnt=:blendCnt and word like :vowel  ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelFourWords(int size, int blendCnt, String vowel);
*/


    @Query("select * from WordEnglish where (size=2 or size=3)  and vowelCnt=1  ORDER BY RANDOM() ")
    List<WordEnglish> getRandomWords();

   /* @Query("select * from WordEnglish where label='rc_w_simple_l1' and word like :vowel ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelOneWords(String vowel);

    @Query("select * from WordEnglish where label='rc_w_simple_l2' and word like :vowel ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelTwoWords(String vowel);

    @Query("select * from WordEnglish where label='rc_w_simple_l3' and word like :vowel ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelThreeWords(String vowel);

    @Query("select * from WordEnglish where label='rc_w_complex_l1' and word like :vowel ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelFourWords(String vowel);

    @Query("select * from WordEnglish where label='rc_w_complex_l2' and word like :vowel ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelFiveWords(String vowel);

    @Query("select * from WordEnglish where label='rc_w_complex_l3' and word like :vowel ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelSixWords(String vowel);
    */


     @Query("select * from WordEnglish where label=:label  ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelOneWords(String label);

   /* @Query("select * from WordEnglish where label=:label  ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelTwoWords(String label);

    @Query("select * from WordEnglish where label=:label ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelThreeWords(String label);

    @Query("select * from WordEnglish where label=:label  ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelFourWords(String label);

    @Query("select * from WordEnglish where label='rc_w_complex_l2'  ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelFiveWords(String label);

    @Query("select * from WordEnglish where label='rc_w_complex_l3'  ORDER BY RANDOM() LIMIT 5")
    List<WordEnglish> getEnglishLevelSixWords(String label);*/

    @Query("select * from WordEnglish where size between :size1 and :size2 and type='sentence' ORDER BY RANDOM() LIMIT 10")
    List<WordEnglish> getSentencesBetweenSizes(int size1, int size2);

   /* @Query("select * from WordEnglish where type='sentence' ORDER BY RANDOM() LIMIT 2")
    List<WordEnglish> getSentencesBetweenSizes();
*/

   /* @Query("select * from WordEnglish where size between 6 and 8 and type=:type ORDER BY RANDOM() LIMIT 10")
    List<WordEnglish> getLevelTwoSentences(String type, int size);
*/



    /*@Query("update ContentWord set newCrl = 0 where newCrl = 1")
    void setNewCrlToOld();*/

   /* @Query("select * from Crl where newCrl = 1")
    List<Crl> getAllNewCrls();*/

    @Query("select count(*) from WordEnglish")
    int getTotalEntries();

    @Query("select count(*) from WordEnglish WHERE type='sentence'")
    int getSentenceCount();

}
