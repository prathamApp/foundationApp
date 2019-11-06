package com.pratham.foundation.ui.home_screen;


import com.pratham.foundation.database.domain.ContentTable;

public interface TempItemClicked {

    void onContentClicked(ContentTable singleItem);

    void onContentOpenClicked(ContentTable contentList);

    void onContentDownloadClicked(ContentTable contentList, int parentPos, int childPos, String downloadType);

    void onContentDeleteClicked(ContentTable contentList);

    void onContentTestOpenClicked(ContentTable singleItem);

    void seeMore(String nodeId, String nodeTitle);
}
