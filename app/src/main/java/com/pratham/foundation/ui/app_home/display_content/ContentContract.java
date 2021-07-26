package com.pratham.foundation.ui.app_home.display_content;


import com.pratham.foundation.database.domain.ContentTable;

import java.util.List;

/**
 * Created by Ameya on 23-Nov-17.
 */

public interface ContentContract {

    interface ContentView{
        void clearContentList();
        void addContentToViewList(List<ContentTable> contentTable);
        void notifyAdapter();

        void showNoDataDownloadedDialog();

        void notifyAdapterItem(int deletePos);

        void setHeaderProgress(int percent);

        void showLoader();

        void dismissLoadingDialog();

        void setStudentProfileImage(String sImage);

        void showToast(String msg);

        void showChangeDateDialog(String newDate, String sTime);

        void onTestAddedToDb(ContentTable testItem);
    }

    interface ContentPresenter {
        void getListData();

        void downloadResource(String downloadNodeId, ContentTable contentTable);

        void addNodeIdToList(String nodeId);

        boolean removeLastNodeId();

        void starContentEntry(String nodeId, String s);

        void deleteContent(int position, ContentTable contentList);

        void displayProfileImage();

        void getPerc(String nodeId);

        void findMaxScore(String nodeId);

        void setView(ContentContract.ContentView contentView);

        void getInternetTime();

        void addScoreToDB(String resId);

        void addAssessmentToDb(ContentTable itemContent);
    }

}
