package com.pratham.foundation.ui.home_screen.learning_fragment;




import com.pratham.foundation.database.domain.ContentTable;

import java.util.List;

public interface LearningContract {

    interface LearningItemClicked {

        void onContentClicked(ContentTable singleItem);

        void onContentOpenClicked(ContentTable contentList);

        void onContentDownloadClicked(ContentTable contentList, int parentPos, int childPos, String downloadType);

        void onContentDeleteClicked(ContentTable contentList);

        void seeMore(String nodeId, String nodeTitle);
    }

    interface LearningView {

        void notifyAdapter();

        void addContentToViewList(List<ContentTable> contentParentList);

        void setSelectedLevel(List<ContentTable> contentTable);

        void showNoDataDownloadedDialog();

        void showLoader();

        void dismissLoadingDialog();

        void setLevelprogress(int percent);

        void dismissDownloadDialog();

//        void setBotNodeId(String botID);
    }

    interface LearningPresenter {

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

        void updateCurrentNode(ContentTable contentTable);

        String getcurrentNodeID();

        void setView(LearningContract.LearningView learningView);

        void getBottomNavId(int currentLevelNo, String cosSection);

    }
}
