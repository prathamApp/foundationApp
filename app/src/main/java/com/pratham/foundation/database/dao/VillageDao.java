package com.pratham.foundation.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratham.foundation.database.domain.Village;

import java.util.List;

@Dao
public interface VillageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllVillages(List<Village> villagesList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVillage(Village modal_village);

    @Query("DELETE FROM Village")
    void deleteAllVillages();

    @Query("SELECT * FROM Village")
    List<Village> getAllVillages();

    @Query("SELECT DISTINCT State FROM Village ORDER BY State ASC")
    List<String> getAllStates();

    @Query("SELECT DISTINCT Block FROM Village WHERE State=:st ORDER BY Block ASC")
    List<String> GetStatewiseBlock(String st);

    @Query("SELECT * FROM Village WHERE Block=:block  ORDER BY VillageName ASC")
    List<Village> GetVillages(String block);

    @Query("select VillageID from Village where Block=:block")
    int GetVillageIDByBlock(String block);
}