package com.pratham.foundation.ui.old_home;




import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.ContentTableNew;
import com.pratham.foundation.modalclasses.CertificateModelClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface HomeContract {

    interface HomeView {

        void notifyAdapter();

        void addContentToViewList(List<ContentTableNew> contentParentList);

        void addContentToViewTestList(CertificateModelClass test);

        void openRCGame(ContentTableNew contentList);

        void setSelectedLevel(List<ContentTable> contentTable);

        void showNoDataDownloadedDialog();

        void initializeTheIndex();

        void showLoader();

        void doubleQuestionCheck();

        void dismissLoadingDialog();

        void setLevelprogress(int percent);

        void testOpenData(ContentTable testData);

        void hideTestDownloadBtn();

        void notifyTestAdapter();

        void displayCurrentDownloadedTest();

        void dismissDownloadDialog();

        void setProfileName(String profileName);

        void setStudentProfileImage(String sImage);

        void setBotNodeId(String botID);
    }

    interface HomePresenter {

        void getDataForList();

        void insertNodeId(String nodeId);

        public void updateDownloads();

        void downloadResource(String downloadNodeId);

        void enterRCData(ContentTableNew contentList);

        boolean removeLastNodeId();

        void clearNodeIds();

        void getLevelDataForList(int currentLevelNo, String bottomNavNodeId);

        void clearLevelList();

        void displayProfileName();

        void displayProfileImage();

        void findMaxScore(String nodeId);

        void updateDownloadJson(String folderPath);

        void updateCurrentNode(ContentTableNew contentTableNew);

        JSONArray getTestData(String jsonName);

        float getStarRating(float perc);

        void generateTestData(JSONArray testData, String bottomNavNodeId);

        void recordTestData(JSONObject jsonObjectAssessment, String certificateTitle);

        void getTempData(String nodeId);

        ContentTable getRandomData(String resourceType, String nodeId);

        void insertTestSession();

        void endTestSession();

        String getcurrentNodeID();

        void setView(HomeContract.HomeView homeView);

        void getBottomNavId(int currentLevelNo, String cosSection);
    }
}
