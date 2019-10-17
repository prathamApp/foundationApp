package com.pratham.foundation.ui.home_temp;


import com.pratham.foundation.database.domain.ContentTableNew;

public interface TempItemClicked {

    void onContentClicked(ContentTableNew singleItem);

    void onContentOpenClicked(ContentTableNew contentList);

    void onContentDownloadClicked(ContentTableNew contentList, int parentPos, int childPos, String downloadType);

    void onContentDeleteClicked(ContentTableNew contentList);

    void onContentTestOpenClicked(ContentTableNew singleItem);

    void seeMore(String nodeId, String nodeTitle);
}