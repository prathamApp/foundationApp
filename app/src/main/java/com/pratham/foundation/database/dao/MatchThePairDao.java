package com.pratham.foundation.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import com.pratham.foundation.modalclasses.MatchThePair;

import java.util.List;

@Dao
public interface MatchThePairDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllParas(List<MatchThePair> modalClasses);

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    public void insertVillage(Village modal_village);

    @Query("DELETE FROM MatchThePair")
    public void deleteAllQuestions();

    @Query("SELECT * FROM MatchThePair where paraLang=:lang")
    public List<MatchThePair> getQuestions(String lang);

    @Query("SELECT * FROM MatchThePair where paraLang=:lang order by random() limit 5")
        public List<MatchThePair> getRandomQuestions(String lang);

 /*   @Query("SELECT DISTINCT Block FROM Village WHERE State=:st ORDER BY Block ASC")
    public List<String> GetStatewiseBlock(String st);

    @Query("SELECT * FROM Village WHERE Block=:block  ORDER BY VillageName ASC")
    public List<Village> GetVillages(String block);*/

    @Query("select * from MatchThePair where id=:id and paraLang=:lang")
    public List<MatchThePair> GetQuestionById(String id, String lang);
}