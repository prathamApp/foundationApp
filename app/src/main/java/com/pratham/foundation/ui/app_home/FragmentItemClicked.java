package com.pratham.foundation.ui.app_home;


import com.pratham.foundation.database.domain.ContentTable;

public interface FragmentItemClicked {

    void onContentClicked(ContentTable singleItem, String parentName);

    void onContentOpenClicked(ContentTable contentList);

    void onPreResOpenClicked(int position, String nId, String title, boolean onSDCard);

    void onContentDownloadClicked(ContentTable contentList, int parentPos, int childPos, String downloadType);

    void onContentDeleteClicked(int parentPos, int childPos, ContentTable contentList);

    void seeMore(String nodeId, String nodeTitle);

    void onTestContentClicked(int posi, ContentTable contentTable);
}
