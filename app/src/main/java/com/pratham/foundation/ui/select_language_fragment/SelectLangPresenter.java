package com.pratham.foundation.ui.select_language_fragment;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.APP_LANGUAGE_STRING;
import static com.pratham.foundation.utility.FC_Constants.INTERNET_LANGUAGE_API;
import static com.pratham.foundation.utility.FC_Constants.rootParentId;

@EBean
public class SelectLangPresenter implements SelectLangContract.SelectLangPresenter, API_Content_Result {

    public Context context;
    public SelectLangContract.SelectLangView view;
    public Gson gson;
    public ContentTable contentDetail;
    public API_Content api_content;
    private List<ContentTable> langList;
    private boolean parentFound = false;

    @Override
    public void setView(SelectLangContract.SelectLangView view) {
        this.view = view;
        gson = new Gson();
        langList = new ArrayList<ContentTable>();
        api_content = new API_Content(context, SelectLangPresenter.this);
    }

    private void sortAllList(List<ContentTable> contentParentList) {
        Collections.sort(contentParentList, (o1, o2) -> o1.getSeq_no() - o2.getSeq_no());
//        Collections.sort(contentParentList, (o1, o2) -> o1.getNodeId().compareTo(o2.getNodeId()));
    }

    public SelectLangPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void getLanguage() {
        langList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(rootParentId);
        if (FC_Utility.isDataConnectionAvailable(context))
            api_content.getAPILanguage(APP_LANGUAGE_STRING, INTERNET_LANGUAGE_API);
        else {
            if (langList.size() > 0)
                view.updateLangList(langList);
            else
                view.connectToInternetDialog();
        }

    }

    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(APP_LANGUAGE_STRING)) {
            try {
                Type listType = new TypeToken<ArrayList<ContentTable>>() {
                }.getType();

                List<ContentTable> serverContentList = gson.fromJson(response, listType);

                for (int i = 0; i < serverContentList.size(); i++) {
                    parentFound = false;
                    List<ContentTable> tempList;
                    ContentTable contentTable = new ContentTable();
                    for (int j = 0; j < langList.size(); j++) {
                        if (serverContentList.get(i).getNodeId().equalsIgnoreCase(
                                langList.get(j).getNodeId())) {
                            parentFound = true;
                        }
                    }
                    if (!parentFound) {
                        langList.add(serverContentList.get(i));
                    }
                }
                view.updateLangList(langList);
                view.notifyAdapter();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void receivedError(String header) {
        view.updateLangList(langList);
        view.notifyAdapter();
        view.dismissLoadingDialog();
    }

    /*    @Background
    @Override
    public void displayProfileImage() {
        String sImage;
        if (!FC_Constants.GROUP_LOGIN)
            sImage = AppDatabase.getDatabaseInstance(context).getStudentDao().getStudentAvatar(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
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
            List<ContentTable> childList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getChildsOfParent(nodeId);
            for (int childCnt = 0; childList.size() > childCnt; childCnt++) {
                if (childList.get(childCnt).getNodeType().equals("Resource")) {
                    double maxScoreTemp = 0.0;
                    List<ContentProgress> contentProgressList = AppDatabase.getDatabaseInstance(context).getContentProgressDao().getContentNodeProgress(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), childList.get(childCnt).getResourceId(), "resourceProgress");
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
            downloadedContentTableList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getContentData("" + nodeIds.get(nodeIds.size() - 1));
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
                    contentTable.setNodeUpdate(false);
                    ListForContentTable1.add(contentTable);
                }
            } catch (Exception e) {
                e.printStackTrace();
                BackupDatabase.backup(context);
            }
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
            BackupDatabase.backup(context);
        }
    }

    @UiThread
    public void updateUI() {
        try {
            if (FC_Utility.isDataConnectionAvailable(context))
                api_content.getAPIContent(FC_Constants.INTERNET_BROWSE, FC_Constants.INTERNET_BROWSE_API, nodeIds.get(nodeIds.size() - 1));
            else {
//                if (downloadedContentTableList.size() == 0 && !FC_Utility.isDataConnectionAvailable(context)) {
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
    public void downloadResource(String downloadId) {
        downloadNodeId = downloadId;
        api_content.getAPIContent(FC_Constants.INTERNET_DOWNLOAD_RESOURCE, FC_Constants.INTERNET_DOWNLOAD_RESOURCE_API, downloadNodeId);
    }

    @Override
    public void receivedContent(String header, String response) {
        try {
            if (header.equalsIgnoreCase(FC_Constants.INTERNET_BROWSE)) {
                boolean contentFound = false;
                try {
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
                contentView.addContentToViewList(ListForContentTable2);
                contentView.notifyAdapter();
                contentView.dismissLoadingDialog();
            } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_RESOURCE)) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                    contentDetail = download_content.getNodelist().get(download_content.getNodelist().size() - 1);
                    for (int i = 0; i < download_content.getNodelist().size(); i++) {
//                        download_content.getNodelist().get(i).setNodeImage(download_content.getNodelist().get(i).getNodeServerImage()
//                                .substring( download_content.getNodelist().get(i).getNodeServerImage().lastIndexOf('/') + 1));
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
//                if (download_content.getFoldername().equalsIgnoreCase("video"))
//                    zipDownloader.initialize(context, "https://prathamopenschool.org/CourseContent/FCGames/OfftechVideos/5_Chatterbox.mp4",
//                            download_content.getFoldername(), "5_Chatterbox.mp4", contentDetail, pos);
//                else
                zipDownloader.initialize(context, download_content.getDownloadurl(),
                        download_content.getFoldername(), fileName, contentDetail, pos);
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

        String foldername = contentItem.getResourcePath()*//*.split("/")[0]*//*;
        FC_Utility.deleteRecursive(new File(ApplicationClass.foundationPath
                + gameFolderPath + "/" + foldername));

        FC_Utility.deleteRecursive(new File(ApplicationClass.foundationPath
                + "" + App_Thumbs_Path + contentItem.getNodeImage()));
        contentView.notifyAdapterItem(deletePos);
    }

    private void checkAndDeleteParent(ContentTable contentItem) {
        AppDatabase.getDatabaseInstance(context).getContentTableDao().deleteContent(contentItem.getNodeId());
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
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
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
    }*/

}