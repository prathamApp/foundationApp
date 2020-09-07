package com.pratham.foundation.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pratham.foundation.database.domain.Crl;

import java.util.List;


@Dao
public interface CrlDao {

    @Insert
    long insert(Crl crl);

    @Insert
    long[] insertAll(Crl... crls);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<Crl> crls);

    @Update
    int update(Crl crl);

    @Delete
    void delete(Crl crl);

    /*@Delete
    void deleteAll(Crl... crls);
*/
    @Query("DELETE FROM Crl")
    void deleteAll();

    @Query("select * from Crl where CRLId = :crlID")
    Crl getCrl(String crlID);

    @Query("select * from Crl")
    List<Crl> getAllCrls();

    @Query("update Crl set newCrl = 0 where newCrl = 1")
    void setNewCrlToOld();

    @Query("select * from Crl where newCrl = 1")
    List<Crl> getAllNewCrls();

    @Query("select FirstName from Crl where UserName = :uName and Password = :uPass")
    String checkCrls(String uName, String uPass);

    @Query("SELECT * FROM CRL WHERE UserName=:user AND Password=:pass")
    Crl checkUserValidation(String user, String pass);

    @Query("SELECT * FROM CRL")
    List<Crl> getAllCRLs();
}
