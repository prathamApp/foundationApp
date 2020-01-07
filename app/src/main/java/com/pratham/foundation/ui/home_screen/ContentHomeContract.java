package com.pratham.foundation.ui.home_screen;


import com.pratham.foundation.database.domain.ContentTable;

public interface ContentHomeContract {

    interface HomeView {

    }

    interface ContentLevelChanged{
        void setActualLevel(int levelCount);
    }

    interface ContentItemClicked {

        void onContentClicked(ContentTable singleItem, String parentName);

        void onContentOpenClicked(ContentTable contentList);

        void onContentDownloadClicked(ContentTable contentList, int parentPos, int childPos, String downloadType);

        void onContentDeleteClicked(ContentTable contentList);

        void seeMore(String nodeId, String nodeTitle);
    }


    interface HomePresenter {

        void setView(ContentHomeContract.HomeView homeView);

    }
}
