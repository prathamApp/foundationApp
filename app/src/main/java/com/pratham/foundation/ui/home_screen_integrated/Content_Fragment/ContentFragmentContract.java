package com.pratham.foundation.ui.home_screen_integrated.Content_Fragment;

import com.pratham.foundation.database.domain.ContentTable;

import java.util.List;

public interface ContentFragmentContract {

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

    interface ContentView {

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

    interface ContentPracticeView {

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


    interface ContentFragmentPresenter {

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

        void getBottomNavId(int currentLevelNo, String cosSection);

/*        void setFunView(ContentFragmentContract.ContentFunView funView);

        void setLearningView(ContentFragmentContract.ContentLearningView learningView);

        void setPracticeView(ContentFragmentContract.ContentPracticeView PracticeView);*/

        void setView(ContentFragmentContract.ContentView contentView);

    }
}
