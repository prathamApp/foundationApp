package com.pratham.foundation.ui.app_home.display_content;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_STUDENT_ID;
import static com.pratham.foundation.utility.FC_Constants.PI_BROWSE;
import static com.pratham.foundation.utility.FC_Constants.RASPBERRY_PI_LANGUAGE_API;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.async.ZipDownloader;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.modalclasses.Modal_DownloadContent;
import com.pratham.foundation.modalclasses.Modal_InternetTime;
import com.pratham.foundation.modalclasses.RaspModel.ModalRaspContentNew;
import com.pratham.foundation.modalclasses.RaspModel.Modal_RaspResult;
import com.pratham.foundation.modalclasses.RaspModel.Modal_Rasp_JsonData;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EBean
public class ContentPresenter implements ContentContract.ContentPresenter, API_Content_Result {

    public Context context;
    public ContentContract.ContentView contentView;
    public String downloadNodeId, fileName;
    public Gson gson;
    public Modal_DownloadContent download_content;
    public ArrayList<ContentTable> pos;
    public ContentTable contentDetail, dwContent;
    public List<ContentTable> downloadedContentTableList, ListForContentTable1, ListForContentTable2;
    public ArrayList<String> nodeIds;
    public API_Content api_content;
    public List maxScore, maxScoreChild;
    public int percent = 0;

    @Bean(ZipDownloader.class)
    ZipDownloader zipDownloader;

    @Override
    public void setView(ContentContract.ContentView contentView) {
        this.contentView = contentView;
        nodeIds = new ArrayList<>();
        gson = new Gson();
        pos = new ArrayList<>();
        ListForContentTable1 = new ArrayList<ContentTable>();
        ListForContentTable2 = new ArrayList<ContentTable>();
        api_content = new API_Content(context, ContentPresenter.this);
    }

    private void sortAllList(List<ContentTable> contentParentList) {
        Collections.sort(contentParentList, (o1, o2) -> o1.getSeq_no() - o2.getSeq_no());
    }

    @Override
    public void getInternetTime() {
        if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (!ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                api_content.getInternetTimeApi(FC_Constants.INTERNET_TIME, FC_Constants.INTERNET_TIME_API);
            }
        }
    }

    public ContentPresenter(Context context) {
        this.context = context;
    }

    @Background
    @Override
    public void displayProfileImage() {
        // old un used code
        String sImage;
        if (!FC_Constants.GROUP_LOGIN)
            sImage = AppDatabase.getDatabaseInstance(context).getStudentDao().getStudentAvatar(FastSave.getInstance().getString(CURRENT_STUDENT_ID, ""));
        else
            sImage = "group_icon";

        contentView.setStudentProfileImage(sImage);
    }

    @Background
    @Override
    public void getPerc(String nodeId) {
        maxScore = new ArrayList();
        findMaxScore(nodeId);
    }

    @Override
    public void findMaxScore(String nodeId) {
        try {
            List<ContentTable> childList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getChildsOfParent(nodeId,
                    "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%"
                    /*"%" +FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")+ "%"*/);
            for (int childCnt = 0; childList.size() > childCnt; childCnt++) {
                if (childList.get(childCnt).getNodeType().equals("Resource")) {
                    double maxScoreTemp = 0.0;
                    List<ContentProgress> contentProgressList = AppDatabase.getDatabaseInstance(context).getContentProgressDao().getContentNodeProgress(FastSave.getInstance().getString(CURRENT_STUDENT_ID, ""), childList.get(childCnt).getResourceId(), "resourceProgress");
                    for (int cnt = 0; cnt < contentProgressList.size(); cnt++) {
                        String d = contentProgressList.get(cnt).getProgressPercentage();
                        double scoreTemp = Double.parseDouble(d);
                        if (maxScoreTemp < scoreTemp) {
                            maxScoreTemp = scoreTemp;
                        }
                    }
                    maxScore.add(maxScoreTemp);
                } else {
                    findMaxScore(childList.get(childCnt).getNodeId());
                }
            }
            double totalScore = 0;
            for (int j = 0; maxScore.size() > j; j++) {
                totalScore = totalScore + Double.parseDouble(maxScore.get(j).toString());
            }
            if (maxScore.size() > 0) {
                percent = (int) (totalScore / maxScore.size());
                contentView.setHeaderProgress(percent);
            } else {
                contentView.setHeaderProgress(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void addNodeIdToList(String nodeId) {
        nodeIds.add(nodeId);
    }

    @Override
    public boolean removeLastNodeId() {
        if (nodeIds.size() > 1) {
            nodeIds.remove(nodeIds.size() - 1);
            return true;
        } else
            return false;
    }

    @Background
    @Override
    public void getListData() {
        try {
            contentView.showLoader();
            String nodeListIndex = "0";
            try {
                nodeListIndex = nodeIds.get(nodeIds.size() - 1);
            } catch (Exception e) {
                nodeListIndex = "0";
                e.printStackTrace();
            }
            if (nodeIds.size() < 1 || nodeListIndex.equalsIgnoreCase("0")) {
                updateUI();
            } else {
//            fetch downloaded data from DB
                downloadedContentTableList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getContentData("" + nodeListIndex,
                        "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%");
                Log.d("NODE_ID", "Node downloadedContentTableList :  " + downloadedContentTableList.size());
                sortAllList(downloadedContentTableList);
                contentView.clearContentList();
                ListForContentTable1.clear();
                try {
                    for (int j = 0; j < downloadedContentTableList.size(); j++) {
                        ContentTable contentTable = new ContentTable();
                        contentTable.setNodeId("" + downloadedContentTableList.get(j).getNodeId());
                        contentTable.setNodeType("" + downloadedContentTableList.get(j).getNodeType());
                        contentTable.setNodeTitle("" + downloadedContentTableList.get(j).getNodeTitle());
                        contentTable.setNodeKeywords("" + downloadedContentTableList.get(j).getNodeKeywords());
                        contentTable.setNodeAge("" + downloadedContentTableList.get(j).getNodeAge());
                        contentTable.setNodeDesc("" + downloadedContentTableList.get(j).getNodeDesc());
                        contentTable.setNodeServerImage("" + downloadedContentTableList.get(j).getNodeServerImage());
                        contentTable.setNodeImage("" + downloadedContentTableList.get(j).getNodeImage());
                        contentTable.setResourceId("" + downloadedContentTableList.get(j).getResourceId());
                        contentTable.setResourceType("" + downloadedContentTableList.get(j).getResourceType());
                        contentTable.setResourcePath("" + downloadedContentTableList.get(j).getResourcePath());
                        contentTable.setParentId("" + downloadedContentTableList.get(j).getParentId());
                        contentTable.setLevel("" + downloadedContentTableList.get(j).getLevel());
                        contentTable.setContentLanguage("" + downloadedContentTableList.get(j).getContentLanguage());
                        contentTable.setVersion("" + downloadedContentTableList.get(j).getVersion());
                        contentTable.setContentType(downloadedContentTableList.get(j).getContentType());
                        contentTable.setIsDownloaded("" + downloadedContentTableList.get(j).getIsDownloaded());
                        contentTable.setOnSDCard(downloadedContentTableList.get(j).isOnSDCard());
                        contentTable.setSeq_no(downloadedContentTableList.get(j).getSeq_no());
                        String resPath;
                        if (downloadedContentTableList.get(j).isOnSDCard())
                            resPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + downloadedContentTableList.get(j).getResourcePath();
                        else
                            resPath = ApplicationClass.foundationPath + gameFolderPath + "/" + downloadedContentTableList.get(j).getResourcePath();
                        if (!new File(resPath).exists()) {
                            contentTable.setIsDownloaded("" + false);
                            contentTable.setOnSDCard(false);
                        }

                        contentTable.setNodeUpdate(false);
                        float prog = 0;
                        maxScoreChild = new ArrayList();
                        if (downloadedContentTableList.get(j).getNodeType() != null
                                && downloadedContentTableList.get(j).getNodeType().equalsIgnoreCase("resource")) {
                            prog = AppDatabase.getDatabaseInstance(context).getContentProgressDao().getResPercentage
                                    (FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na"),
                                            downloadedContentTableList.get(j).getResourceId());
                            downloadedContentTableList.get(j).setNodePercentage("" + (int) prog);
                            contentTable.setNodePercentage("" + (int) prog);
                        } else {
                            findMaxScoreNew(downloadedContentTableList.get(j).getNodeId());
                            double totalScore = 0;
                            for (int q = 0; maxScoreChild.size() > q; q++) {
                                totalScore = totalScore + Double.parseDouble(maxScoreChild.get(q).toString());
                            }
                            if (maxScoreChild.size() > 0) {
                                int percent = (int) (totalScore / maxScoreChild.size());
                                contentTable.setNodePercentage("" + percent);
                                downloadedContentTableList.get(j).setNodePercentage("" + percent);
                            } else {
                                contentTable.setNodePercentage("0");
                            }
                        }
                        ListForContentTable1.add(contentTable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    BackupDatabase.backup(context);
                }
                updateUI();
            }
        } catch (Exception e) {
            e.printStackTrace();
            BackupDatabase.backup(context);
        }
    }

    @UiThread
    public void updateUI() {
        try {
//            check for connection and hit api for getting all data from server
//            if there no internet connection the display the downloaded data
            if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
                //Checks if device is connected to raspberry pie
                if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                    api_content.getAPIContent_PI(PI_BROWSE, RASPBERRY_PI_LANGUAGE_API, nodeIds.get(nodeIds.size() - 1));
                } else {
                    api_content.getAPIContent(FC_Constants.INTERNET_BROWSE, FC_Constants.INTERNET_BROWSE_API, nodeIds.get(nodeIds.size() - 1));
                }
            } else {
//                if downloaded list is empty show no downloaded data
                if (downloadedContentTableList.size() == 0) {
                    contentView.showNoDataDownloadedDialog();
                } else {
                    contentView.addContentToViewList(ListForContentTable1);
                    contentView.notifyAdapter();
                    contentView.dismissLoadingDialog();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findMaxScoreNew(String nodeId) {
        List<ContentTable> childList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getChildsOfParent(nodeId,
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%"/*,
                FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")*/);
        for (int childCnt = 0; childList.size() > childCnt; childCnt++) {
            if (childList.get(childCnt).getNodeType().equals("Resource")) {
                double maxScoreTemp = 0.0;
                List<ContentProgress> score = AppDatabase.getDatabaseInstance(context).getContentProgressDao().getProgressByStudIDAndResID(FastSave.getInstance().getString(CURRENT_STUDENT_ID, ""), childList.get(childCnt).getResourceId(), "resourceProgress");
                for (int cnt = 0; cnt < score.size(); cnt++) {
                    String d = score.get(cnt).getProgressPercentage();
                    double scoreTemp = Double.parseDouble(d);
                    if (maxScoreTemp < scoreTemp) {
                        maxScoreTemp = scoreTemp;
                    }
                }
                maxScoreChild.add(maxScoreTemp);
            } else {
                findMaxScoreNew(childList.get(childCnt).getNodeId());
            }
        }
    }

    ContentTable testItem;

    @Background
    @Override
    public void addAssessmentToDb(ContentTable itemContent) {
        testItem = itemContent;
        if (FC_Utility.isDataConnectionAvailable(context)) {
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                api_content.getAPIContent_PI_V2(FC_Constants.INTERNET_DOWNLOAD_ASSESSMENT_RESOURCE_PI, FC_Constants.INTERNET_DOWNLOAD_RESOURCE_API_PI, itemContent.getNodeId());
            } else
                api_content.getAPIContent(FC_Constants.INTERNET_DOWNLOAD_ASSESSMENT_RESOURCE, FC_Constants.INTERNET_DOWNLOAD_RESOURCE_API, itemContent.getNodeId());
        } else
            contentView.onTestAddedToDb(testItem);

//        itemContent.setIsDownloaded("true");
//        itemContent.setStudentId("" + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
//                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ?"NA"
//                    :FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "")));
//        AppDatabase.getDatabaseInstance(mContext).getContentTableDao().insert(itemContent);
    }

    @Override
    public void downloadResource(String downloadId, ContentTable contentTable) {
//        call download api
        downloadNodeId = downloadId;
        dwContent = contentTable;
        if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            //Checks if device is connected to raspberry pie
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                api_content.getAPIContent_PI_V2(FC_Constants.INTERNET_DOWNLOAD_RESOURCE_PI, FC_Constants.INTERNET_DOWNLOAD_RESOURCE_API_PI, downloadNodeId);
            } else {
                api_content.getAPIContent(FC_Constants.INTERNET_DOWNLOAD_RESOURCE, FC_Constants.INTERNET_DOWNLOAD_RESOURCE_API, downloadNodeId);
            }
        }
    }

    // API result
    @Override
    @Background
    public void receivedContent(String header, String response) {
        try {
            if (header.equalsIgnoreCase(FC_Constants.INTERNET_BROWSE) || header.equalsIgnoreCase(FC_Constants.PI_BROWSE)) {
                boolean contentFound = false;
                try {
//                    compair response form server and downloaded data, then show all the data.
                    ListForContentTable2.clear();
                    Type listType = new TypeToken<ArrayList<ContentTable>>() {
                    }.getType();
                    List<ContentTable> serverContentList = new ArrayList<>();

                    if (header.equalsIgnoreCase(FC_Constants.PI_BROWSE)) {
                        ModalRaspContentNew rasp_contents = gson.fromJson(response, ModalRaspContentNew.class);
                        rasp_contents.getModalRaspResults();
                        Log.e("url raspResult : ", String.valueOf(rasp_contents.getModalRaspResults().size()));
                        Modal_Rasp_JsonData modal_rasp_jsonData;
                        if (rasp_contents.getModalRaspResults() != null) {
                            List<Modal_RaspResult> raspResults = new ArrayList<>();
                            for (int i = 0; i < rasp_contents.getModalRaspResults().size(); i++) {
                                Modal_RaspResult modalRaspResult = new Modal_RaspResult();
                                modalRaspResult.setAppId(rasp_contents.getModalRaspResults().get(i).getAppId());
                                modalRaspResult.setNodeId(rasp_contents.getModalRaspResults().get(i).getNodeId());
                                modalRaspResult.setNodeType(rasp_contents.getModalRaspResults().get(i).getNodeType());
                                modalRaspResult.setNodeTitle(rasp_contents.getModalRaspResults().get(i).getNodeTitle());
                                modalRaspResult.setParentId(rasp_contents.getModalRaspResults().get(i).getParentId());
                                modalRaspResult.setJsonData(rasp_contents.getModalRaspResults().get(i).getJsonData());
                                //raspResults.add(modalRaspResult);
                                modal_rasp_jsonData = gson.fromJson(rasp_contents.getModalRaspResults().get(i).getJsonData(), Modal_Rasp_JsonData.class);
                                ContentTable detail = modalRaspResult.setContentToConfigNodeStructure(modalRaspResult, modal_rasp_jsonData);
                                serverContentList.add(detail);
                            }
                        }
                    } else
                        serverContentList = gson.fromJson(response, listType);

                    sortAllList(serverContentList);
                    for (int i = 0; i < serverContentList.size(); i++) {
                        contentFound = false;
                        for (int j = 0; j < downloadedContentTableList.size(); j++) {
                            if (serverContentList.get(i).getNodeId().equalsIgnoreCase(downloadedContentTableList.get(j).getNodeId())) {
                                contentFound = true;
                                if (!serverContentList.get(i).getVersion().equalsIgnoreCase(downloadedContentTableList.get(j).getVersion())) {
                                    downloadedContentTableList.get(j).setNodeUpdate(true);
                                }
//                                ListForContentTable2.add(downloadedContentTableList.get(j));
                                break;
                            }
                        }
                        if (!contentFound) {
                            ContentTable contentTableTemp = new ContentTable();
                            contentTableTemp.setNodeId("" + serverContentList.get(i).getNodeId());
                            contentTableTemp.setNodeType("" + serverContentList.get(i).getNodeType());
                            contentTableTemp.setNodeTitle("" + serverContentList.get(i).getNodeTitle());
                            contentTableTemp.setNodeKeywords("" + serverContentList.get(i).getNodeKeywords());
                            contentTableTemp.setNodeAge("" + serverContentList.get(i).getNodeAge());
                            contentTableTemp.setNodeDesc("" + serverContentList.get(i).getNodeDesc());
                            contentTableTemp.setNodeServerImage("" + serverContentList.get(i).getNodeServerImage());
                            contentTableTemp.setNodeImage("" + serverContentList.get(i).getNodeImage());
                            contentTableTemp.setResourceId("" + serverContentList.get(i).getResourceId());
                            contentTableTemp.setResourceType("" + serverContentList.get(i).getResourceType());
                            contentTableTemp.setResourcePath("" + serverContentList.get(i).getResourcePath());
                            contentTableTemp.setParentId("" + serverContentList.get(i).getParentId());
                            contentTableTemp.setLevel("" + serverContentList.get(i).getLevel());
                            contentTableTemp.setVersion("" + serverContentList.get(i).getVersion());
                            contentTableTemp.setNodePercentage("0");
                            contentTableTemp.setContentType("" + serverContentList.get(i).getContentType());
                            contentTableTemp.setContentLanguage("" + serverContentList.get(i).getContentLanguage());
                            contentTableTemp.setSeq_no(serverContentList.get(i).getSeq_no());
                            contentTableTemp.setIsDownloaded("false");
                            contentTableTemp.setOnSDCard(false);
                            contentTableTemp.setNodeUpdate(false);
                            maxScoreChild = new ArrayList();
                            float prog = 0;
                            if (serverContentList.get(i).getNodeType() != null
                                    && serverContentList.get(i).getNodeType().equalsIgnoreCase("resource")) {
                                prog = AppDatabase.getDatabaseInstance(context).getContentProgressDao().getResPercentage
                                        (FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na"),
                                                serverContentList.get(i).getResourceId());
                                contentTableTemp.setNodePercentage("" + (int) prog);
                            }
//                            ListForContentTable2.add(contentTableTemp);
                            downloadedContentTableList.add(contentTableTemp);
                        }
                    }
                } catch (Exception e) {
                    contentView.dismissLoadingDialog();
                    e.printStackTrace();
                }
//                contentView.addContentToViewList(ListForContentTable1);
//                add final list to view and then notify adapter
//                contentView.addContentToViewList(ListForContentTable2);
                contentView.addContentToViewList(downloadedContentTableList);
                contentView.notifyAdapter();
                contentView.dismissLoadingDialog();
            } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_RESOURCE) || header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_RESOURCE_PI)) {
                try {
//                    get response from server and hit download url
                    JSONObject jsonObject = new JSONObject(response);
                    pos.clear();
//                    List<ContentTable> serverContentList = new ArrayList<>();
                    if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_RESOURCE_PI)) {
                        ModalRaspContentNew rasp_contents = gson.fromJson(response, ModalRaspContentNew.class);
                        rasp_contents.getModalRaspResults();
                        Log.e("url raspResult : ", String.valueOf(rasp_contents.getModalRaspResults().size()));
                        Modal_Rasp_JsonData modal_rasp_jsonData;
                        if (rasp_contents.getModalRaspResults() != null) {
                            List<Modal_RaspResult> raspResults = new ArrayList<>();
                            for (int i = 0; i < rasp_contents.getModalRaspResults().size(); i++) {
                                Modal_RaspResult modalRaspResult = new Modal_RaspResult();
                                modalRaspResult.setAppId(rasp_contents.getModalRaspResults().get(i).getAppId());
                                modalRaspResult.setNodeId(rasp_contents.getModalRaspResults().get(i).getNodeId());
                                modalRaspResult.setNodeType(rasp_contents.getModalRaspResults().get(i).getNodeType());
                                modalRaspResult.setNodeTitle(rasp_contents.getModalRaspResults().get(i).getNodeTitle());
                                modalRaspResult.setParentId(rasp_contents.getModalRaspResults().get(i).getParentId());
                                modalRaspResult.setJsonData(rasp_contents.getModalRaspResults().get(i).getJsonData());
                                //raspResults.add(modalRaspResult);
                                download_content = gson.fromJson(rasp_contents.getModalRaspResults().get(i).getJsonData(), Modal_DownloadContent.class);
//                                ContentTable contentTable/*modal_rasp_jsonData*/ = gson.fromJson(rasp_contents.getModalRaspResults().get(i).getJsonData(), ContentTable.class);
//                                ContentTable detail = modalRaspResult.setContentToConfigNodeStructure(modalRaspResult, modal_rasp_jsonData);
//                                serverContentList.add(detail);
//                                pos.add(contentTable);
                            }
                        }
                    } else {
                        download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                    }

                    for (int i = 0; i < download_content.getNodelist().size(); i++) {
                        ContentTable contentTableTemp = download_content.getNodelist().get(i);
                        pos.add(contentTableTemp);
                    }

                    for (int i = 0; i < pos.size(); i++) {
                        pos.get(i).setIsDownloaded("true");
                        String studID = AppDatabase.getDatabaseInstance(context).getContentTableDao().getEarlierStudentId(pos.get(i).getNodeId());
                        String currStudID = ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                                || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ? "NA"
                                : FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                        boolean studFound = false, prathamGroupFound = false;
                        if (studID != null && !studID.equalsIgnoreCase("") && !studID.equalsIgnoreCase(" ")) {
                            String[] arrOfStdId = studID.split(",");
                            if (arrOfStdId != null && arrOfStdId.length > 0) {
                                for (int j = 0; j < arrOfStdId.length; j++) {
                                    if (arrOfStdId[j].equalsIgnoreCase(currStudID)) {
                                        studFound = true;
                                        break;
                                    }
                                    if (arrOfStdId[j].equalsIgnoreCase("pratham_group")) {
                                        prathamGroupFound = true;
                                        break;
                                    }
                                }

                                if (!studFound) {
                                    pos.get(i).setStudentId(studID + "," + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                                            || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ? "NA"
                                            : FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "")));
                                } else {
                                    pos.get(i).setStudentId(studID);
                                }
                                if (FastSave.getInstance().getBoolean(FC_Constants.PRATHAM_STUDENT, false)) {
                                    if (!prathamGroupFound) {
                                        pos.get(i).setStudentId(studID + ",pratham_group");
                                    } else {
                                        pos.get(i).setStudentId(studID);
                                    }
                                }
                            }
                        } else {
                            pos.get(i).setStudentId("" + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ? "NA"
                                    : FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "")));
                            if (FastSave.getInstance().getBoolean(FC_Constants.PRATHAM_STUDENT, false)) {
                                pos.get(i).setStudentId("pratham_group");
                            }
                        }
                    }

                    String fileSize = "";
                    if (!header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_RESOURCE_PI)) {
                        URL url = new URL(download_content.getDownloadurl());
                        URLConnection urlConnection = url.openConnection();
                        urlConnection.connect();
                        fileSize = "" + FC_Utility.getFileSize(urlConnection.getContentLength());
                        contentView.setDownloadSize(" (" + fileSize + ")");
                    } else
                        contentView.setDownloadSize(" ");
                    fileName = download_content.getDownloadurl()
                            .substring(download_content.getDownloadurl().lastIndexOf('/') + 1);
                    Log.d("HP", "doInBackground: fileName : " + fileName);
                    Log.d("HP", "doInBackground: folderName : " + download_content.getFoldername());
                    Log.d("HP", "doInBackground: DW URL : " + download_content.getDownloadurl());

                    if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_RESOURCE_PI)) {
                        if (fileName.contains(".zip") || fileName.contains(".rar")) {
                            String pi_url = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_URL + "zips/" + fileName;
                            zipDownloader.initialize(context, pi_url,
                                    download_content.getFoldername(), fileName, dwContent, pos, true);
                        } else {
                            String pi_url = "na";
                            if (fileName.contains(".mp4"))
                                pi_url = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_URL + "/videos/mp4/" + fileName;
                            else if (fileName.contains(".m4v"))
                                pi_url = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_URL + "/videos/m4v/" + fileName;
                            else if (fileName.contains(".mp3"))
                                pi_url = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_URL + "/audios/mp3/" + fileName;
                            else if (fileName.contains(".wav"))
                                pi_url = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_URL + "/audios/wav/" + fileName;
                            else /*if (fileName.contains(".pdf"))*/
                                pi_url = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_URL + "/docs/" + fileName;
                            zipDownloader.initialize(context, pi_url,
                                    download_content.getFoldername(), fileName, dwContent, pos, false);
                        }
                    } else {
                        zipDownloader.initialize(context, download_content.getDownloadurl(),
                                download_content.getFoldername(), fileName, dwContent, pos, fileName.contains(".zip") || fileName.contains(".rar"));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_ASSESSMENT_RESOURCE) ||
                    header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_ASSESSMENT_RESOURCE_PI)) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_ASSESSMENT_RESOURCE_PI)) {
                        ModalRaspContentNew rasp_contents = gson.fromJson(response, ModalRaspContentNew.class);
                        rasp_contents.getModalRaspResults();
                        Log.e("url raspResult : ", String.valueOf(rasp_contents.getModalRaspResults().size()));
                        Modal_Rasp_JsonData modal_rasp_jsonData;
                        if (rasp_contents.getModalRaspResults() != null) {
                            List<Modal_RaspResult> raspResults = new ArrayList<>();
                            for (int i = 0; i < rasp_contents.getModalRaspResults().size(); i++) {
                                Modal_RaspResult modalRaspResult = new Modal_RaspResult();
                                modalRaspResult.setAppId(rasp_contents.getModalRaspResults().get(i).getAppId());
                                modalRaspResult.setNodeId(rasp_contents.getModalRaspResults().get(i).getNodeId());
                                modalRaspResult.setNodeType(rasp_contents.getModalRaspResults().get(i).getNodeType());
                                modalRaspResult.setNodeTitle(rasp_contents.getModalRaspResults().get(i).getNodeTitle());
                                modalRaspResult.setParentId(rasp_contents.getModalRaspResults().get(i).getParentId());
                                modalRaspResult.setJsonData(rasp_contents.getModalRaspResults().get(i).getJsonData());
                                //raspResults.add(modalRaspResult);
                                download_content = gson.fromJson(rasp_contents.getModalRaspResults().get(i).getJsonData(), Modal_DownloadContent.class);
//                                ContentTable contentTable/*modal_rasp_jsonData*/ = gson.fromJson(rasp_contents.getModalRaspResults().get(i).getJsonData(), ContentTable.class);
//                                ContentTable detail = modalRaspResult.setContentToConfigNodeStructure(modalRaspResult, modal_rasp_jsonData);
//                                serverContentList.add(detail);
//                                pos.add(contentTable);
                            }
                        }
                    } else {
                        download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                    }

                    pos.clear();
                    pos.addAll(download_content.getNodelist());

                    for (int i = 0; i < pos.size(); i++) {
                        pos.get(i).setIsDownloaded("true");
                        String studID = AppDatabase.getDatabaseInstance(context).getContentTableDao().getEarlierStudentId(pos.get(i).getNodeId());
                        String currStudID = ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                                || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ? "NA"
                                : FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                        boolean studFound = false, prathamGroupFound = false;
                        if (studID != null && !studID.equalsIgnoreCase("") && !studID.equalsIgnoreCase(" ")) {
                            String[] arrOfStdId = studID.split(",");
                            if (arrOfStdId != null && arrOfStdId.length > 0) {
                                for (int j = 0; j < arrOfStdId.length; j++) {
                                    if (arrOfStdId[j].equalsIgnoreCase(currStudID)) {
                                        studFound = true;
                                        break;
                                    }
                                    if (arrOfStdId[j].equalsIgnoreCase("pratham_group")) {
                                        prathamGroupFound = true;
                                        break;
                                    }
                                }

                                if (!studFound) {
                                    pos.get(i).setStudentId(studID + "," + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                                            || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ? "NA"
                                            : FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "")));
                                } else {
                                    pos.get(i).setStudentId(studID);
                                }
                                if (FastSave.getInstance().getBoolean(FC_Constants.PRATHAM_STUDENT, false)) {
                                    if (!prathamGroupFound) {
                                        pos.get(i).setStudentId(studID + ",pratham_group");
                                    } else {
                                        pos.get(i).setStudentId(studID);
                                    }
                                }
                            }
                        } else {
                            pos.get(i).setStudentId("" + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ? "NA"
                                    : FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "")));
                            if (FastSave.getInstance().getBoolean(FC_Constants.PRATHAM_STUDENT, false)) {
                                pos.get(i).setStudentId("pratham_group");
                            }
                        }
                    }

                    AppDatabase.getDatabaseInstance(context).getContentTableDao().addContentList(pos);
                    contentView.onTestAddedToDb(testItem);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_TIME)) {
                try {
                    Type listType = new TypeToken<Modal_InternetTime>() {
                    }.getType();
                    Gson gson;
                    gson = new Gson();
                    Modal_InternetTime serverTime = gson.fromJson(response, listType);
                    String sDate = serverTime.getDatetime().split("T")[0];
                    Log.d("TAG", "$$$$    :    " + sDate);
                    String sTime = serverTime.getDatetime().split("T")[1].substring(0, 5);
//                String newDate = sDate.substring(5)+"-"+sDate.substring(0,4) + " "+ sTime;
//                2021-01-19
                    String newDate = String.format("%s-%s-%s", sDate.substring(8), sDate.substring(5, 7), sDate.substring(0, 4));
                    String newDateTime = String.format("%s-%s-%s %s", sDate.substring(8), sDate.substring(5, 7), sDate.substring(0, 4), sTime);
                    Log.d("TAG", "$$$$    :    " + newDate);
                    Log.d("TAG", "$$$$    :    " + newDateTime);

                    String fcDate = FC_Utility.getCurrentDate();
                    String fcTime = FC_Utility.getCurrentTime();
                    Log.d("TAG", "$$$$    :" + fcDate);
                    Log.d("TAG", "$$$$    :" + fcTime);
                    int t1 = Integer.parseInt(sTime.substring(0, 2));
                    int t1s = Integer.parseInt(sTime.substring(3, 5));
                    int t2 = Integer.parseInt(fcTime.substring(0, 2));
                    int t2s = Integer.parseInt(fcTime.substring(3, 5));
                    Log.d("TAG", "$$$$  T1  :" + t1 + "    " + t1s);
                    Log.d("TAG", "$$$$  T2  :" + t2 + "    " + t2s);
                    if (!fcDate.equalsIgnoreCase(newDate)) {
                        contentView.showChangeDateDialog(newDate, sTime);
                    } else {
/*
                    int t1 = Integer.parseInt(sTime.substring(0,2));
                    int t1s = Integer.parseInt(sTime.substring(4,6));
                    int t2 = Integer.parseInt(fcTime.substring(0,2));
                    int t2s = Integer.parseInt(sTime.substring(4,6));
                    Log.d("TAG", "$$$$  T1  :" +t1 + "    "+t1s);
                    Log.d("TAG", "$$$$  T2  :" +t2 + "    "+t2s);
*/
                        if (t1 > t2) {
                            if ((t1 - t2) > 1) {
                                Log.d("TAG", "$$$$  t1>t2  :" + t2 + "    " + t2s);
                                contentView.showChangeDateDialog(newDate, sTime);
                            }
                        } else if (t2 > t1) {
                            if ((t2 - t1) > 1) {
                                Log.d("TAG", "$$$$  t2>t1  :" + t2 + "    " + t2s);
                                contentView.showChangeDateDialog(newDate, sTime);
                            }
                        }
                    }
//                Fc
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            contentView.dismissLoadingDialog();
        }
    }

    @Override
    public void receivedContent_PI_SubLevel(String header, String response, int pos, int size) {
    }

    @Background
    public void addScoreToDB(String resId) {
        try {
            String endTime = FC_Utility.getCurrentDateTime();
            Score score = new Score();
            score.setSessionID("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setStudentID(((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ? "NA"
                    : FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA")));
            score.setGroupId(((FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "").equals(null)) ? "NA"
                    : FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "NA")));
            score.setDeviceID("" + FC_Utility.getDeviceID());
            score.setResourceID("" + resId);
            score.setQuestionId(0);
            score.setScoredMarks(0);
            score.setTotalMarks(0);
            score.setStartDateTime("" + endTime);
            score.setEndDateTime("" + endTime);
            score.setLevel(0);
            score.setLabel("Assessment Opened");
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    @Background
    @Override
    public void deleteContent(int deletePos, ContentTable contentItem) {
        try {
            if (contentItem.getNodeType().equalsIgnoreCase("PreResource")) {
                List<ContentTable> contentTableList = AppDatabase.getDatabaseInstance(context).getContentTableDao()
                        .getChildsOfParent_forDelete(contentItem.getNodeId());
                for (int i = 0; i < contentTableList.size(); i++) {
                    checkAndDeleteParent(contentTableList.get(i));
                    Log.d("Delete_Clicked", "onClick: G_Presenter");

                    String foldername = contentTableList.get(i).getResourcePath()/*.split("/")[0]*/;
                    FC_Utility.deleteRecursive(new File(ApplicationClass.foundationPath
                            + gameFolderPath + "/" + foldername));

                    FC_Utility.deleteRecursive(new File(ApplicationClass.foundationPath
                            + "" + App_Thumbs_Path + contentTableList.get(i).getNodeImage()));
//                learningView.notifyAdapterItem(parentPos,childPos);
                }
                checkAndDeleteParent(contentItem);
                Log.d("Delete_Clicked", "onClick: G_Presenter");
            } else {
                checkAndDeleteParent(contentItem);
                Log.d("Delete_Clicked", "onClick: G_Presenter");
                String foldername = contentItem.getResourcePath()/*.split("/")[0]*/;
                FC_Utility.deleteRecursive(new File(ApplicationClass.foundationPath
                        + gameFolderPath + "/" + foldername));
                FC_Utility.deleteRecursive(new File(ApplicationClass.foundationPath
                        + "" + App_Thumbs_Path + contentItem.getNodeImage()));
            }
            contentView.notifyAdapterItem(deletePos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAndDeleteParent(ContentTable contentItem) {
        AppDatabase.getDatabaseInstance(context).getContentTableDao().deleteContent(contentItem.getNodeId());
    }

    @Override
    public void receivedError(String header) {
        if (header.equalsIgnoreCase(FC_Constants.INTERNET_TIME))
            getInternetTime();
        else {
            contentView.showToast("Connection Error");
            contentView.addContentToViewList(ListForContentTable1);
            contentView.notifyAdapter();
            contentView.dismissLoadingDialog();
        }
    }

    @Background
    @Override
    public void starContentEntry(final String contentID, final String Label) {
        try {
            String deviceId = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, "").equals(null)
                    ? "NA" : FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(contentID.equals(null) ? "NA" : contentID);
            score.setQuestionId(0);
            score.setScoredMarks(0);
            score.setTotalMarks(0);
            score.setStudentID(((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ? "NA"
                    : FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA")));
            score.setGroupId(((FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "").equals(null)) ? "NA"
                    : FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "NA")));
            score.setStartDateTime("" + FC_Utility.getCurrentDateTime());
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(0);
            score.setLabel("" + (Label.equals(null) || Label.equals("") ? "NA" : Label));
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);
            BackupDatabase.backup(context);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}