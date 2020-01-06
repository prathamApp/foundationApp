package com.pratham.foundation.ui.home_screen.Content_Fragment;

import com.pratham.foundation.database.domain.ContentTable;

import java.util.List;

public interface Content_Fragment_Contract {

    interface ContentItemClicked {

        void onContentClicked(ContentTable singleItem, String parentName);

        void onContentOpenClicked(ContentTable contentList);

        void onContentDownloadClicked(ContentTable contentList, int parentPos, int childPos, String downloadType);

        void onContentDeleteClicked(ContentTable contentList);

        void seeMore(String nodeId, String nodeTitle);
    }

    interface ContentFunView {

        void notifyAdapter();

        void addContentToViewList(List<ContentTable> contentParentList);

        void setSelectedLevel(List<ContentTable> contentTable);

        void showNoDataDownloadedDialog();

        void showLoader();

        void dismissLoadingDialog();

        void setLevelprogress(int percent);

        void dismissDownloadDialog();

        void showComingSoonDiaog();

//        void setBotNodeId(String botID);
    }

    interface ContentLearningView {

        void notifyAdapter();

        void addContentToViewList(List<ContentTable> contentParentList);

        void setSelectedLevel(List<ContentTable> contentTable);

        void showNoDataDownloadedDialog();

        void showLoader();

        void dismissLoadingDialog();

        void setLevelprogress(int percent);

        void dismissDownloadDialog();

        void showComingSoonDiaog();

//        void setBotNodeId(String botID);
    }

    interface ContentLearningPresenter {

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

        void setFunView(Content_Fragment_Contract.ContentFunView funView);

        void getBottomNavId(int currentLevelNo, String cosSection);

        void setLearningView(Content_Fragment_Contract.ContentLearningView learningView);
    }

    interface ContentFunPresenter {

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

        void setFunView(Content_Fragment_Contract.ContentFunView funView);

        void getBottomNavId(int currentLevelNo, String cosSection);
    }
}
