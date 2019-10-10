package com.pratham.foundation.ui.home_temp.learning_fragment;




import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.ContentTableNew;
import com.pratham.foundation.modalclasses.CertificateModelClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface LearningContract {

    interface LearningItemClicked {

        void onContentClicked(ContentTableNew singleItem);

        void onContentOpenClicked(ContentTableNew contentList);

        void onContentDownloadClicked(ContentTableNew contentList, int parentPos, int childPos, String downloadType);

        void onContentDeleteClicked(ContentTableNew contentList);

        void seeMore(String nodeId, String nodeTitle);
    }

    interface LearningView {

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

        void updateCurrentNode(ContentTableNew contentTableNew);

        String getcurrentNodeID();

        void setView(LearningContract.LearningView learningView);

        void getBottomNavId(int currentLevelNo, String cosSection);

    }
}
