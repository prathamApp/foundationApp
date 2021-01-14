package com.pratham.foundation.ui.app_home.display_content;

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
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_STUDENT_ID;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

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
    public List maxScore;
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
                    "%"+ FastSave.getInstance().getString(CURRENT_STUDENT_ID,"na")+"%");
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
                        "%"+ FastSave.getInstance().getString(CURRENT_STUDENT_ID,"na")+"%");
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
                        contentTable.setNodeUpdate(false);
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
            if (FC_Utility.isDataConnectionAvailable(context))
                api_content.getAPIContent(FC_Constants.INTERNET_BROWSE, FC_Constants.INTERNET_BROWSE_API, nodeIds.get(nodeIds.size() - 1));
            else {
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

    @Override
    public void downloadResource(String downloadId, ContentTable contentTable) {
//        call download api
        downloadNodeId = downloadId;
        dwContent = contentTable;
        api_content.getAPIContent(FC_Constants.INTERNET_DOWNLOAD_RESOURCE, FC_Constants.INTERNET_DOWNLOAD_RESOURCE_API, downloadNodeId);
    }

    // API result
    @Override
    public void receivedContent(String header, String response) {
        try {
            if (header.equalsIgnoreCase(FC_Constants.INTERNET_BROWSE)) {
                boolean contentFound = false;
                try {
//                    compair response form server and downloaded data, then show all the data.
                    ListForContentTable2.clear();
                    Type listType = new TypeToken<ArrayList<ContentTable>>() {
                    }.getType();
                    List<ContentTable> serverContentList = gson.fromJson(response, listType);
                    sortAllList(serverContentList);
                    for (int i = 0; i < serverContentList.size(); i++) {
                        contentFound = false;
                        for (int j = 0; j < downloadedContentTableList.size(); j++) {
                            if (serverContentList.get(i).getNodeId().equalsIgnoreCase(downloadedContentTableList.get(j).getNodeId())) {
                                contentFound = true;
                                if (!serverContentList.get(i).getVersion().equalsIgnoreCase(downloadedContentTableList.get(j).getVersion())) {
                                    downloadedContentTableList.get(j).setNodeUpdate(true);
                                }
                                ListForContentTable2.add(downloadedContentTableList.get(j));
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
                            ListForContentTable2.add(contentTableTemp);
                        }
                    }
                } catch (Exception e) {
                    contentView.dismissLoadingDialog();
                    e.printStackTrace();
                }
//                contentView.addContentToViewList(ListForContentTable1);
//                add final list to view and then notify adapter
                contentView.addContentToViewList(ListForContentTable2);
                contentView.notifyAdapter();
                contentView.dismissLoadingDialog();
            } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_RESOURCE)) {
                try {
//                    get response from server and hit download url
                    JSONObject jsonObject = new JSONObject(response);
                    download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                    for (int i = 0; i < download_content.getNodelist().size(); i++) {
                        ContentTable contentTableTemp = download_content.getNodelist().get(i);
                        pos.add(contentTableTemp);
                    }
                    fileName = download_content.getDownloadurl()
                            .substring(download_content.getDownloadurl().lastIndexOf('/') + 1);
                    Log.d("HP", "doInBackground: fileName : " + fileName);
                    Log.d("HP", "doInBackground: folderName : " + download_content.getFoldername());
                    Log.d("HP", "doInBackground: DW URL : " + download_content.getDownloadurl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
/*
                if (download_content.getFoldername().equalsIgnoreCase("video"))
                    zipDownloader.initialize(context, "https://prathamopenschool.org/CourseContent/FCGames/OfftechVideos/5_Chatterbox.mp4",
                            download_content.getFoldername(), "5_Chatterbox.mp4", contentDetail, pos);
                else
*/
//                    start the download process
//                boolean downloadingItem = false;
//                Modal_FileDownloading modal_fileDownloading = new Modal_FileDownloading();
//                modal_fileDownloading.setContentDetail(dwContent);
//                modal_fileDownloading.setDownloadId(downloadNodeId);
//                modal_fileDownloading.setFilename(dwContent.getNodeTitle());
//                modal_fileDownloading.setProgress(0);
//                if (fileDownloadingList.size() < 4) {
//                    if (fileDownloadingList.size() > 0) {
//                        for (int x = 0; x < fileDownloadingList.size(); x++) {
//                            if (fileDownloadingList.get(x).getDownloadId().equalsIgnoreCase(downloadNodeId)) {
//                                downloadingItem = true;
//                            }
//                        }
//                    } else {
//                        fileDownloadingList.add(modal_fileDownloading);
//                    }
                if (fileName.contains(".zip") || fileName.contains(".rar"))
                    zipDownloader.initialize(context, download_content.getDownloadurl(),
                            download_content.getFoldername(), fileName, dwContent, pos, true);
                else zipDownloader.initialize(context, download_content.getDownloadurl(),
                        download_content.getFoldername(), fileName, dwContent, pos, false);
//                } else {
//                    contentView.showToast("Only 4 allowed");
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            contentView.dismissLoadingDialog();
        }
    }

    //    @Background
    @Override
    public void deleteContent(int deletePos, ContentTable contentItem) {
        checkAndDeleteParent(contentItem);
        Log.d("Delete_Clicked", "onClick: G_Presenter");

        String foldername = contentItem.getResourcePath()/*.split("/")[0]*/;
        FC_Utility.deleteRecursive(new File(ApplicationClass.foundationPath
                + gameFolderPath + "/" + foldername));

        FC_Utility.deleteRecursive(new File(ApplicationClass.foundationPath
                + "" + App_Thumbs_Path + contentItem.getNodeImage()));
        contentView.notifyAdapterItem(deletePos);
    }

    private void checkAndDeleteParent(ContentTable contentItem) {
        AppDatabase.getDatabaseInstance(context).getContentTableDao().deleteContent(contentItem.getNodeId());
    }

    @Override
    public void receivedError(String header) {
        contentView.showToast("Connection Error");
        contentView.addContentToViewList(ListForContentTable1);
        contentView.notifyAdapter();
        contentView.dismissLoadingDialog();
    }

    @Background
    @Override
    public void starContentEntry(final String contentID, final String Label) {
        try {
            String deviceId = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID("" + contentID);
            score.setQuestionId(0);
            score.setScoredMarks(0);
            score.setTotalMarks(0);
            score.setStudentID(FastSave.getInstance().getString(CURRENT_STUDENT_ID, ""));
            score.setStartDateTime(FC_Utility.getCurrentDateTime());
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(0);
            score.setLabel(Label);
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);
            BackupDatabase.backup(context);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}