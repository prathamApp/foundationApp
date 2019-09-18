package com.pratham.foundation.ui.display_content;


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
    }

    interface ContentPresenter {
        void getListData();

        void downloadResource(String downloadNodeId);

        void addNodeIdToList(String nodeId);

        boolean removeLastNodeId();

        void starContentEntry(String nodeId, String s);

        void deleteContent(int position, ContentTable contentList);

        void displayProfileImage();

        void getPerc(String nodeId);

        void findMaxScore(String nodeId);

        void setView(ContentContract.ContentView contentView);
    }

}
