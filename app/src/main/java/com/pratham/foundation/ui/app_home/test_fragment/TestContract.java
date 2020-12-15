package com.pratham.foundation.ui.app_home.test_fragment;




import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.modalclasses.CertificateModelClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface TestContract {
    
    interface LanguageSpinnerListner{
        void onSpinnerLanguageChanged(String language);
    }

    interface TestView {

        void notifyAdapter();

        void addContentToViewList(List<ContentTable> contentParentList);

        void setSelectedLevel(List<ContentTable> contentTable);

        void showNoDataDownloadedDialog();

        void showLoader();

        void dismissLoadingDialog();

        void setLevelprogress(int percent);

        void dismissDownloadDialog();

        void addContentToViewTestList(CertificateModelClass contentTable);

        void doubleQuestionCheck();

        void hideTestDownloadBtn();

        void initializeTheIndex();

        void displayCurrentDownloadedTest();

        void testOpenData(ContentTable testData);

        void clearTestList();

        void setQuesTranslateLang(String[] languagesArray);

        void showNoDataLayout();

        void showRecyclerLayout();

        void showToast(String s);

//        void setBotNodeId(String botID);
    }

    interface TestPresenter {

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

        void setView(TestContract.TestView TestView);

        void getBottomNavId(int currentLevelNo, String cosSection);

        JSONArray getTestData(String jsonName);

        void generateTestData(JSONArray testData, String nodeId, boolean isUpdate);

        void getTempData(String nodeId);

        void endTestSession();

        void recordTestData(JSONObject jsonObjectAssessment, String s);

        float getStarRating(float perc);

        ContentTable getRandomData(String resourceType, String nodeKeywords);

        void insertTestSession();

        void downloadTestJson(int level);
    }
}
