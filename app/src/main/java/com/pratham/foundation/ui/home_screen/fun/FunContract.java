package com.pratham.foundation.ui.home_screen.fun;

import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.ContentTableNew;

import java.util.List;

public interface FunContract {

    interface FunItemClicked {

        void onContentClicked(ContentTableNew singleItem);

        void onContentOpenClicked(ContentTableNew contentList);

        void onContentDownloadClicked(ContentTableNew contentList, int parentPos, int childPos, String downloadType);

        void onContentDeleteClicked(ContentTableNew contentList);

        void seeMore(String nodeId, String nodeTitle);
    }

    interface FunView {

        void notifyAdapter();

        void addContentToViewList(List<ContentTableNew> contentParentList);

        void setSelectedLevel(List<ContentTable> contentTable);

        void showNoDataDownloadedDialog();

        void showLoader();

        void dismissLoadingDialog();

        void setLevelprogress(int percent);

        void dismissDownloadDialog();

//        void setBotNodeId(String botID);
    }

    interface FunPresenter {

        void getDataForList();

        void insertNodeId(String nodeId);

        void updateDownloads();

        void downloadResource(String downloadNodeId);

        boolean removeLastNodeId();

        void clearNodeIds();

        void getLevelDataForList(int currentLevelNo, String bottomNavNodeId);

        void clearLevelList();

        void findMaxScore(String nodeId);

        void updateDownloadJson(String folderPath);

        void updateCurrentNode(ContentTableNew contentTableNew);

        String getcurrentNodeID();

        void setView(FunContract.FunView funView);

        void getBottomNavId(int currentLevelNo, String cosSection);

    }
}
