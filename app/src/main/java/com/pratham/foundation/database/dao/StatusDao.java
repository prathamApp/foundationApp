package com.pratham.foundation.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pratham.foundation.database.domain.Status;

import java.util.List;

@Dao
public interface StatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Status status);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(Status... statuses);

    @Update
    int update(Status status);

    @Delete
    void delete(Status status);

    @Delete
    void deleteAll(Status... statuses);

    @Query("select * from Status")
    List<Status> getAllStatuses();

    @Query("Select statusKey from Status where statusKey = :key")
    String getKey(String key);

    @Query("Select value from Status where statusKey = :key")
    String getValue(String key);

    @Query("UPDATE Status set value =:value where statusKey =:key")
    void updateValue(String key, String value);

}