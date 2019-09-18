package com.pratham.foundation.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.foundation.database.domain.ContentTable;

import java.util.List;

/**
 * Created by Anki on 12/18/2018.
 */

@Dao
public interface ContentTableDao {

    @Insert
    long[] insertAll(List<ContentTable> contentTableList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ContentTable contentTableList);

    @Query("select * from ContentTable where parentId= :parentId")
    public List<ContentTable> getContentData(String parentId);

    @Query("select nodeId from ContentTable where parentId= :parentId and nodeTitle=:nodetitle")
    public String getContentDataByTitle(String parentId, String nodetitle);

    @Query("select * from ContentTable where nodeId= :nodeId")
    public List<ContentTable> getNodeData(String nodeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addContentList(List<ContentTable> contentList);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void addNewContentList(List<ContentTable> contentList);

    @Query("SELECT * FROM ContentTable WHERE parentid ISNULL or parentid = 0 or parentid=''and contentLanguage=:language")
    public List<ContentTable> getParentsHeaders(String language);

    @Query("SELECT * FROM ContentTable WHERE parentid=:id")
    public List<ContentTable> getChildsOfParent(String id);

    @Query("SELECT * FROM ContentTable WHERE parentid=:id AND nodeId!='4033'")
    public List<ContentTable> getChildsOfParent2(String id);

    @Query("Delete from ContentTable WHERE nodeid=:nodeId")
    void deleteContent(String nodeId);

    @Query("SELECT COUNT(*) from ContentTable WHERE parentId=:nodeId" /*and contentLanguage=:language*/)
    int getChildCountOfParent(String nodeId/*, String language*/);

    @Query("SELECT * FROM ContentTable WHERE nodeId=:id" /*and content_language=:language"*/)
    public ContentTable getContent(String id/*, String language*/);

    @Query("SELECT * FROM ContentTable WHERE parentid=:id and resourceType=:resType")
    public List<ContentTable> getListByResType(String id, String resType);

    @Query("SELECT nodeId FROM ContentTable WHERE nodeTitle=:cosSection and contentLanguage=:currentLanguage")
    String getBottomNavigationId(String currentLanguage, String cosSection);
//
//    @Query("SELECT * FROM ContentTable WHERE parentid ISNULL or parentid = 0 or parentid=''and contentLanguage=:language")
//    public List<ContentTableNew> getParentsHeadersNew(String language);

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    public void addContentListNew(List<ContentTable> contentList);
}
