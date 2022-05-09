package com.pratham.foundation.ui.app_home.learning_fragment;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.ui.app_home.HomeActivity.sub_nodeId;
import static com.pratham.foundation.utility.FC_Constants.BOTTOM_NODE_PI;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_STUDENT_ID;
import static com.pratham.foundation.utility.FC_Constants.INTERNET_LEVEL_PI;
import static com.pratham.foundation.utility.FC_Constants.PI_BROWSE;
import static com.pratham.foundation.utility.FC_Constants.PI_BROWSE_SUBLEVEL;
import static com.pratham.foundation.utility.FC_Constants.RASPBERRY_PI_BROWSE_API;
import static com.pratham.foundation.utility.FC_Constants.TYPE_FOOTER;
import static com.pratham.foundation.utility.FC_Constants.TYPE_HEADER;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.async.ZipDownloader;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.database.domain.WordEnglish;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.modalclasses.Modal_DownloadContent;
import com.pratham.foundation.modalclasses.RaspModel.ModalRaspContentNew;
import com.pratham.foundation.modalclasses.RaspModel.Modal_RaspResult;
import com.pratham.foundation.modalclasses.RaspModel.Modal_Rasp_JsonData;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@EBean
public class LearningPresenter implements LearningContract.LearningPresenter, API_Content_Result {

    LearningContract.LearningView learningView;

    @Bean(ZipDownloader.class)
    ZipDownloader zipDownloader;

    Context mContext;
    public List<ContentTable> rootList, rootLevelList, dwParentList, childDwContentList;
    public List<ContentTable> contentParentList, contentDBList, contentApiList, childContentList;
    ArrayList<String> nodeIds;
    API_Content api_content;
    String downloadNodeId, fileName, fileSize;
    Gson gson;
    ArrayList<String> codesText;
    int currentLevelNo;
    ContentTable contentDetail, testData, dwContent;
    Modal_DownloadContent download_content;
    ArrayList<ContentTable> pos;
    public List<ContentTable> testList;
    List maxScore, maxScoreChild;
    private String cosSection;
    String botID;

    public LearningPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(LearningContract.LearningView learningView) {
        this.learningView = learningView;
        nodeIds = new ArrayList<>();
        contentParentList = new ArrayList<>();
        contentDBList = new ArrayList<>();
        rootList = new ArrayList<>();
        contentApiList = new ArrayList<>();
        pos = new ArrayList<>();
        maxScore = new ArrayList();
        maxScoreChild = new ArrayList();
        gson = new Gson();
        api_content = new API_Content(mContext, LearningPresenter.this);
    }

    @Override
    public void insertNodeId(String nodeId) {
        nodeIds.add(nodeId);
    }

    @Override
    public void clearNodeIds() {
        nodeIds.clear();
    }

    @Override
    public void clearLevelList() {
        rootList.clear();
    }

    @Override
    public boolean removeLastNodeId() {
        if (nodeIds.size() > 1) {
            nodeIds.remove(nodeIds.size() - 1);
            return true;
        } else
            return false;
    }

    @Override
    public void removeLastNodeId2() {
        try {
            if (nodeIds.size() > 0)
                nodeIds.remove(nodeIds.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void getBottomNavId(int currentLevelNo, String cosSection) {
        try {
            Log.d("crashDetection", "getBottomNavId : ");
            this.currentLevelNo = currentLevelNo;
            this.cosSection = cosSection;
            botID = AppDatabase.getDatabaseInstance(mContext).getContentTableDao().getContentDataByTitle("" + sub_nodeId, cosSection);
            if (botID == null && !FC_Utility.isDataConnectionAvailable(mContext))
                learningView.showNoDataLayout();
            else if (botID != null && !FC_Utility.isDataConnectionAvailable(mContext))
                getLevelDataForList(currentLevelNo, botID);
            else
                getRootData(sub_nodeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    public void getRootData(String rootID) {
        if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            //Checks if device is connected to raspberry pie
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                api_content.getAPIContent_PI(BOTTOM_NODE_PI, RASPBERRY_PI_BROWSE_API, rootID);
            } else {
                api_content.getAPIContent(FC_Constants.BOTTOM_NODE, FC_Constants.INTERNET_LANGUAGE_API, rootID);
            }
        }
    }

    @Background
    @Override
    public void getLevelDataForList(int currentLevelNo, String bottomNavNodeId) {
        rootList = AppDatabase.getDatabaseInstance(mContext).getContentTableDao().getContentData("" + bottomNavNodeId,
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%"/*,
                FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")*/);
        if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            //Checks if device is connected to raspberry pie
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                api_content.getAPIContent_PI(INTERNET_LEVEL_PI, RASPBERRY_PI_BROWSE_API, bottomNavNodeId);
            } else {
                api_content.getAPIContent(FC_Constants.INTERNET_LEVEL, FC_Constants.INTERNET_LANGUAGE_API, bottomNavNodeId);
            }
//            getLevelDataFromApi(currentLevelNo, bottomNavNodeId);
        } else {
            Log.d("crashDetection", "getLevelDataForList  currentLevelNo : " + currentLevelNo + "  :  bottomNavNodeId : " + bottomNavNodeId);
            learningView.setSelectedLevel(rootList);
        }
    }

/*
    public void getLevelDataFromApi(int currentLevelNo, String botNodeId) {
        api_content.getAPIContent(FC_Constants.INTERNET_LEVEL, FC_Constants.INTERNET_LANGUAGE_API, botNodeId);
    }
*/

    @Background
    @Override
    public void findMaxScore(String nodeId) {
        try {
            List<ContentTable> childList = AppDatabase.getDatabaseInstance(mContext).getContentTableDao().getChildsOfParent(nodeId,
                    "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%"
                    /*FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")*/);
            for (int childCnt = 0; childList.size() > childCnt; childCnt++) {
                if (childList.get(childCnt).getNodeType().equals("Resource")) {
                    double maxScoreTemp = 0.0;
                    List<ContentProgress> score = AppDatabase.getDatabaseInstance(mContext).getContentProgressDao().getProgressByStudIDAndResID(FastSave.getInstance().getString(CURRENT_STUDENT_ID, ""),
                            childList.get(childCnt).getResourceId(), "resourceProgress");
                    for (int cnt = 0; cnt < score.size(); cnt++) {
                        String d = score.get(cnt).getProgressPercentage();
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
            getWholePercentage(maxScore);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findMaxScoreNew(String nodeId) {
        List<ContentTable> childList = AppDatabase.getDatabaseInstance(mContext).getContentTableDao().getChildsOfParent(nodeId,
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%"/*,
                FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")*/);
        for (int childCnt = 0; childList.size() > childCnt; childCnt++) {
            if (childList.get(childCnt).getNodeType().equals("Resource")) {
                double maxScoreTemp = 0.0;
                List<ContentProgress> score = AppDatabase.getDatabaseInstance(mContext).getContentProgressDao().getProgressByStudIDAndResID(FastSave.getInstance().getString(CURRENT_STUDENT_ID, ""), childList.get(childCnt).getResourceId(), "resourceProgress");
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

    @Override
    public String getcurrentNodeID() {
        String nodeId = "NA";
        if (nodeIds.size() > 0)
            nodeId = nodeIds.get(nodeIds.size() - 1);
        return nodeId;
    }

    @Background
    public void getWholePercentage(List maxScore) {
        double totalScore = 0;
        try {
            if (maxScore.size() > 0) {
                for (int j = 0; maxScore.size() > j; j++) {
                    totalScore = totalScore + Double.parseDouble(maxScore.get(j).toString());
                }
                int percent = (int) (totalScore / maxScore.size());
                learningView.setLevelprogress(percent);
            } else {
                learningView.setLevelprogress(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            learningView.setLevelprogress(0);
        }
    }

    @Override
    @Background
    public void getDataForList() {
        learningView.showLoader();
        try {
            Log.d("crashDetection", "getDataForList : ");
            dwParentList = AppDatabase.getDatabaseInstance(mContext).getContentTableDao().getContentData("" + nodeIds.get(nodeIds.size() - 1),
                    "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%"/*,
                    FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")*/);
            contentParentList.clear();
            ContentTable resContentTable = new ContentTable();
            List<ContentTable> resourceList = new ArrayList<>();
            List<ContentTable> tempList2 = new ArrayList<>();
            ContentTable contentTableRes = new ContentTable();
            try {
                for (int j = 0; j < dwParentList.size(); j++) {
                    if (dwParentList.get(j).getNodeType().equalsIgnoreCase("Resource")) {
                        contentTableRes = new ContentTable();
                        tempList2 = new ArrayList<>();
                        contentTableRes.setNodeId("" + dwParentList.get(j).getNodeId());
                        contentTableRes.setNodeType("" + dwParentList.get(j).getNodeType());
                        contentTableRes.setNodeTitle("" + dwParentList.get(j).getNodeTitle());
                        contentTableRes.setNodeKeywords("" + dwParentList.get(j).getNodeKeywords());
                        contentTableRes.setNodeAge("" + dwParentList.get(j).getNodeAge());
                        contentTableRes.setNodeDesc("" + dwParentList.get(j).getNodeDesc());
                        contentTableRes.setNodeServerImage("" + dwParentList.get(j).getNodeServerImage());
                        contentTableRes.setNodeImage("" + dwParentList.get(j).getNodeImage());
                        contentTableRes.setResourceId("" + dwParentList.get(j).getResourceId());
                        contentTableRes.setResourceType("" + dwParentList.get(j).getResourceType());
                        contentTableRes.setResourcePath("" + dwParentList.get(j).getResourcePath());
                        contentTableRes.setParentId("" + dwParentList.get(j).getParentId());
                        contentTableRes.setLevel("" + dwParentList.get(j).getLevel());
                        contentTableRes.setSeq_no(dwParentList.get(j).getSeq_no());
                        contentTableRes.setContentType("" + dwParentList.get(j).getContentType());
                        contentTableRes.setContentLanguage("" + dwParentList.get(j).getContentLanguage());
                        contentTableRes.setVersion("" + dwParentList.get(j).getVersion());
                        contentTableRes.setIsDownloaded("" + dwParentList.get(j).getIsDownloaded());
                        contentTableRes.setOnSDCard(dwParentList.get(j).isOnSDCard());
                        contentTableRes.setNodelist(tempList2);
                        String resPath;
                        if (dwParentList.get(j).isOnSDCard())
                            resPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + dwParentList.get(j).getResourcePath();
                        else
                            resPath = ApplicationClass.foundationPath + gameFolderPath + "/" + dwParentList.get(j).getResourcePath();
                        if(!new File(resPath).exists()){
                            contentTableRes.setIsDownloaded("" + false);
                            contentTableRes.setOnSDCard(false);
                        }
                        contentTableRes.setNodeUpdate(false);
                        resourceList.add(contentTableRes);
                    } else {
                        List<ContentTable> tempList;
                        ContentTable contentTable = new ContentTable();
                        tempList = new ArrayList<>();
                        childDwContentList = AppDatabase.getDatabaseInstance(mContext).getContentTableDao().getContentData("" + dwParentList.get(j).getNodeId(),
                                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%"/*,
                                FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")*/);
                        contentTable.setNodeId("" + dwParentList.get(j).getNodeId());
                        contentTable.setNodeType("" + dwParentList.get(j).getNodeType());
                        contentTable.setNodeTitle("" + dwParentList.get(j).getNodeTitle());
                        contentTable.setNodeKeywords("" + dwParentList.get(j).getNodeKeywords());
                        contentTable.setNodeAge("" + dwParentList.get(j).getNodeAge());
                        contentTable.setNodeDesc("" + dwParentList.get(j).getNodeDesc());
                        contentTable.setNodeServerImage("" + dwParentList.get(j).getNodeServerImage());
                        contentTable.setNodeImage("" + dwParentList.get(j).getNodeImage());
                        contentTable.setResourceId("" + dwParentList.get(j).getResourceId());
                        contentTable.setResourceType("" + dwParentList.get(j).getResourceType());
                        contentTable.setResourcePath("" + dwParentList.get(j).getResourcePath());
                        contentTable.setParentId("" + dwParentList.get(j).getParentId());
                        contentTable.setLevel("" + dwParentList.get(j).getLevel());
                        contentTable.setSeq_no(dwParentList.get(j).getSeq_no());
                        contentTable.setContentType(dwParentList.get(j).getContentType());
                        contentTable.setContentLanguage(dwParentList.get(j).getContentLanguage());
                        contentTable.setVersion("" + dwParentList.get(j).getVersion());
                        contentTable.setNodeUpdate(false);
                        contentTable.setIsDownloaded("" + dwParentList.get(j).getIsDownloaded());
                        contentTable.setOnSDCard(dwParentList.get(j).isOnSDCard());
                        String resPath;
                        if (dwParentList.get(j).isOnSDCard())
                            resPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + dwParentList.get(j).getResourcePath();
                        else
                            resPath = ApplicationClass.foundationPath + gameFolderPath + "/" + dwParentList.get(j).getResourcePath();
                        if(!new File(resPath).exists()){
                            contentTable.setIsDownloaded("" + false);
                            contentTable.setOnSDCard(false);
                        }

                        int childListSize = childDwContentList.size();
                        if (childDwContentList.size() > 0) {
                            ContentTable contentChild = new ContentTable();
                            for (int i = 0; i < childListSize; i++) {
                                contentChild = new ContentTable();
                                contentChild.setNodeId("" + childDwContentList.get(i).getNodeId());
                                contentChild.setNodeType("" + childDwContentList.get(i).getNodeType());
                                contentChild.setNodeTitle("" + childDwContentList.get(i).getNodeTitle());
                                contentChild.setNodeKeywords("" + childDwContentList.get(i).getNodeKeywords());
                                contentChild.setNodeAge("" + childDwContentList.get(i).getNodeAge());
                                contentChild.setNodeDesc("" + childDwContentList.get(i).getNodeDesc());
                                contentChild.setNodeServerImage("" + childDwContentList.get(i).getNodeServerImage());
                                contentChild.setNodeImage("" + childDwContentList.get(i).getNodeImage());
                                contentChild.setResourceId("" + childDwContentList.get(i).getResourceId());
                                contentChild.setResourceType("" + childDwContentList.get(i).getResourceType());
                                contentChild.setResourcePath("" + childDwContentList.get(i).getResourcePath());
                                contentChild.setParentId("" + childDwContentList.get(i).getParentId());
                                contentChild.setLevel("" + childDwContentList.get(i).getLevel());
                                contentChild.setSeq_no(childDwContentList.get(i).getSeq_no());
                                contentChild.setContentType(childDwContentList.get(i).getContentType());
                                contentChild.setContentLanguage(childDwContentList.get(i).getContentLanguage());
                                contentChild.setIsDownloaded("" + childDwContentList.get(i).getIsDownloaded());
                                contentChild.setOnSDCard(childDwContentList.get(i).isOnSDCard());
                                contentChild.setVersion(childDwContentList.get(i).getVersion());
                                String childResPath;
                                if (childDwContentList.get(i).isOnSDCard())
                                    childResPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + childDwContentList.get(i).getResourcePath();
                                else
                                    childResPath = ApplicationClass.foundationPath + gameFolderPath + "/" + childDwContentList.get(i).getResourcePath();
                                if(!new File(childResPath).exists()){
                                    contentChild.setIsDownloaded("" + false);
                                    contentChild.setOnSDCard(false);
                                }
                                contentChild.setNodeUpdate(false);
                                contentChild.setNodelist(null);
                                maxScoreChild = new ArrayList();
                                float prog = 0;
                                if (childDwContentList.get(i).getNodeType() != null
                                        && childDwContentList.get(i).getNodeType().equalsIgnoreCase("resource")) {
                                    prog = AppDatabase.getDatabaseInstance(mContext).getContentProgressDao().getResPercentage
                                            (FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na"),
                                                    childDwContentList.get(i).getResourceId());
                                    contentChild.setNodePercentage("" + (int) prog);
                                    childDwContentList.get(i).setNodePercentage("" + (int) prog);
                                } else {
                                    findMaxScoreNew(childDwContentList.get(i).getNodeId());
                                    double totalScore = 0;
                                    for (int q = 0; maxScoreChild.size() > q; q++) {
                                        totalScore = totalScore + Double.parseDouble(maxScoreChild.get(q).toString());
                                    }
                                    if (maxScoreChild.size() > 0) {
                                        int percent = (int) (totalScore / maxScoreChild.size());
                                        contentChild.setNodePercentage("" + percent);
                                    } else {
                                        contentChild.setNodePercentage("0");
                                    }
                                }
                                tempList.add(contentChild);
                            }
                        }
                        contentTable.setNodelist(tempList);
                        contentParentList.add(contentTable);
                    }
                }
                if (resourceList.size() > 1) {
                    resContentTable.setNodelist(resourceList);
                    contentParentList.add(resContentTable);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("crashDetection", "updateUI 1 : ");
        updateUI();
    }

    public void updateUI() {
        try {
            Log.d("crashDetection", "updateUI 2 : ");
            maxScore.clear();
            try {
                findMaxScore("" + nodeIds.get(nodeIds.size() - 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
                //Checks if device is connected to raspberry pie
                if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                    api_content.getAPIContent_PI(PI_BROWSE, RASPBERRY_PI_BROWSE_API, nodeIds.get(nodeIds.size() - 1));
                } else {
                    api_content.getAPIContent(FC_Constants.INTERNET_BROWSE,
                            FC_Constants.INTERNET_BROWSE_API, nodeIds.get(nodeIds.size() - 1));
                }
            } else {
                Log.d("crashDetection", "updateUI 4 : ");
                addHeadersAndNotifyAdapter(contentParentList);
            }
        } catch (Exception e) {
            Log.d("crashDetection", "updateUI Exception : ");
            e.printStackTrace();
        }
//        learningView.addContentToViewList(contentParentList);
//        learningView.notifyAdapter();
    }

    private void insertEnglishWords(List<WordEnglish> wordGameDataEnglish) {
        List<WordEnglish> EnglishDbList = new ArrayList<>();
        for (int i = 0; i < wordGameDataEnglish.size(); i++) {
            WordEnglish word = new WordEnglish();
            word.setSize(wordGameDataEnglish.get(i).getSize());
            word.setWord(wordGameDataEnglish.get(i).getWord());
            word.setStart(wordGameDataEnglish.get(i).getStart());
            word.setType(wordGameDataEnglish.get(i).getType());
            word.setMeaning(wordGameDataEnglish.get(i).getMeaning());
            word.setUuid(wordGameDataEnglish.get(i).getUuid());
            word.setSynid(wordGameDataEnglish.get(i).getSynid());
            word.setVowelCnt(wordGameDataEnglish.get(i).getVowelCnt());
            word.setVowels(wordGameDataEnglish.get(i).getVowels());
            word.setVowelTogether(wordGameDataEnglish.get(i).getVowelTogether());
            word.setBlendCnt(wordGameDataEnglish.get(i).getBlendCnt());
            word.setLabel(wordGameDataEnglish.get(i).getLabel());
            word.setBlends(wordGameDataEnglish.get(i).getBlends());
            EnglishDbList.add(word);
        }
        AppDatabase.getDatabaseInstance(mContext).getEnglishWordDao().insertWordList(EnglishDbList);
    }

    private void insertEnglishSentences(JSONArray englishSentences) {
        List<WordEnglish> EnglishSentenceList = new ArrayList<>();
        try {
            for (int i = 0; i < englishSentences.length(); i++) {
                WordEnglish sentence = new WordEnglish();
                sentence.setWord(englishSentences.getJSONObject(i).getString("data"));
                sentence.setUuid(englishSentences.getJSONObject(i).getString("resourceId"));
                sentence.setSize(englishSentences.getJSONObject(i).getInt("wordCount"));
                sentence.setType("sentence");
                EnglishSentenceList.add(sentence);
            }
            AppDatabase.getDatabaseInstance(mContext).getEnglishWordDao().insertWordList(EnglishSentenceList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<WordEnglish> fetchWords(String jasonName) {
        ArrayList<WordEnglish> arrayList = new ArrayList<>();
        try {
            //InputStream is = new FileInputStream(ApplicationClass.pradigiPath + "/.FCA/"+FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI)+"/RC/" + jasonName);
            InputStream is = mContext.getAssets().open("" + jasonName);
            JsonReader reader = new JsonReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            Gson gson = new GsonBuilder().create();
            // Read file in stream mode
            reader.beginArray();
            while (reader.hasNext()) {
                // Read data into object model
                WordEnglish person = gson.fromJson(reader, WordEnglish.class);
/*                if (person.getWord() == 0 ) {
                    System.out.println("Stream mode: " + person);
                }*/
                arrayList.add(person);
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return arrayList;
    }

    public JSONArray fetchSentences(String jasonName) {
        JSONArray jsonArr = null;
        try {
            //InputStream is = new FileInputStream(ApplicationClass.pradigiPath + "/.FCA/"+FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI)+"/RC/" + jasonName);
            InputStream is = mContext.getAssets().open("" + jasonName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            jsonArr = new JSONArray(jsonStr);
            //returnStoryNavigate = jsonObj.getJSONArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArr;
    }

    @Override
    public void downloadResource(String downloadId, ContentTable dwContent) {
        downloadNodeId = downloadId;
        this.dwContent = dwContent;
        if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                api_content.getAPIContent_PI_V2(FC_Constants.INTERNET_DOWNLOAD_RESOURCE_PI, FC_Constants.INTERNET_DOWNLOAD_RESOURCE_API_PI, downloadNodeId);
            } else
                api_content.getAPIContent(FC_Constants.INTERNET_DOWNLOAD_RESOURCE, FC_Constants.INTERNET_DOWNLOAD_RESOURCE_API, downloadNodeId);
//            api_content.getAPIContentTemp(FC_Constants.INTERNET_DOWNLOAD_RESOURCE, FC_Constants.INTERNET_DOWNLOAD_RESOURCE_API, downloadNodeId);
        } else {
            learningView.showNoDataLayout();
        }
//        getAPIContent(FC_Constants.INTERNET_DOWNLOAD_RESOURCE, FC_Constants.INTERNET_DOWNLOAD_RESOURCE_API);
    }

    @Background
    @Override
    public void updateDownloadJson(String folderPath) {
//        String path = ApplicationClass.foundationPath + "" + gameFolderPath + folderPath;
//        try {
//            InputStream is = new FileInputStream(path + "/gameinfo.json");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            String jsonStr = new String(buffer);
//            JSONObject jsonObj = new JSONObject(jsonStr);
//            Gson gson = new Gson();
//            Modal_DownloadAssessment download_content = gson.fromJson(jsonObj.toString(), Modal_DownloadAssessment.class);
//            List<ContentTable> contentTableList = new ArrayList<>();
//            contentTableList = download_content.getNodelist();
//            for (int i = 0; i < contentTableList.size(); i++) {
//                API_Content.downloadImage(contentTableList.get(i).nodeServerImage, contentTableList.get(i).getNodeImage());
//                contentTableList.get(i).setIsDownloaded("true");
//            }
//            AppDatabase.getDatabaseInstance(mContext).getContentTableDao().addContentList(contentTableList);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        BackupDatabase.backup(mContext);
        learningView.dismissDownloadDialog();
    }

    @Background
    @Override
    public void updateCurrentNode(ContentTable contentTable) {
        try {
            contentTable.setIsDownloaded("true");
            contentTable.setOnSDCard(false);
            AppDatabase.getDatabaseInstance(mContext).getContentTableDao().insert(contentTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortContentList(List<ContentTable> contentParentList) {
        Collections.sort(contentParentList, (o1, o2) -> o1.getSeq_no() - (o2.getSeq_no()));
    }

    ContentTable testItem;

    @Background
    @Override
    public void addAssessmentToDb(ContentTable itemContent) {
        testItem = itemContent;
        if (FC_Utility.isDataConnectionAvailable(mContext))
            api_content.getAPIContent(FC_Constants.INTERNET_DOWNLOAD_ASSESSMENT_RESOURCE, FC_Constants.INTERNET_DOWNLOAD_RESOURCE_API, itemContent.getNodeId());
        else
            learningView.onTestAddedToDb(testItem);

//        itemContent.setIsDownloaded("true");
//        itemContent.setStudentId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
//        AppDatabase.getDatabaseInstance(mContext).getContentTableDao().insert(itemContent);
    }

    List<ContentTable> serverContentPIList, serverContentPIList2;

    @Background
    @Override
    public void receivedContent_PI_SubLevel(String header, String response, int pos, int size) {

        ModalRaspContentNew rasp_contents = gson.fromJson(response, ModalRaspContentNew.class);
        Type listType = new TypeToken<ArrayList<ContentTable>>() {
        }.getType();
        rasp_contents.getModalRaspResults();
        List<ContentTable> serverContentSubList = new ArrayList<>();
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
                raspResults.add(modalRaspResult);
                modal_rasp_jsonData = gson.fromJson(rasp_contents.getModalRaspResults().get(i).getJsonData(), Modal_Rasp_JsonData.class);
                ContentTable detail = modalRaspResult.setContentToConfigNodeStructure(modalRaspResult, modal_rasp_jsonData);
                serverContentSubList.add(detail);
            }
        }
        sortContentList(serverContentSubList);
        serverContentPIList.get(pos).setNodelist(serverContentSubList);
        if (pos == size - 1) {
            createFinalList(serverContentPIList);
        }else{
            subPos++;
            api_content.getAPIContent_SubLevel_PI(PI_BROWSE_SUBLEVEL, RASPBERRY_PI_BROWSE_API,
                    serverContentPIList2.get(subPos).getNodeId(), subPos, serverContentPIList2.size());

        }
    }

    @Background
    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(FC_Constants.INTERNET_BROWSE) || header.equalsIgnoreCase(FC_Constants.PI_BROWSE)) {
            try {
                contentDBList.clear();
                Type listType = new TypeToken<ArrayList<ContentTable>>() {
                }.getType();

                List<ContentTable> serverContentList = new ArrayList<>();
                Log.d("PI_CHECK", "receivedContent: " + response);

                if (header.equalsIgnoreCase(FC_Constants.PI_BROWSE)) {
                    ModalRaspContentNew rasp_contents = gson.fromJson(response, ModalRaspContentNew.class);
                    rasp_contents.getModalRaspResults();
                    Log.e("url raspResult : ", String.valueOf(rasp_contents.getModalRaspResults().size()));
                    Modal_Rasp_JsonData modal_rasp_jsonData;
//                    List<Modal_Rasp_JsonData> modal_rasp_jsonData = new ArrayList<>();
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
                            raspResults.add(modalRaspResult);
                            modal_rasp_jsonData = gson.fromJson(rasp_contents.getModalRaspResults().get(i).getJsonData(), Modal_Rasp_JsonData.class);
                            ContentTable detail = modalRaspResult.setContentToConfigNodeStructure(modalRaspResult, modal_rasp_jsonData);
                            serverContentList.add(detail);
//                            Type listTypes = new TypeToken<ArrayList<Modal_Rasp_JsonData>>() {
//                            }.getType();
//                            modal_rasp_jsonData = gson.fromJson(rasp_contents.getModalRaspResults().get(i).getJsonData(), listType);
//                            for (int l = 0; l < modal_rasp_jsonData.size(); l++) {
//                                ContentTable detail = modalRaspResult.setContentToConfigNodeStructure(modalRaspResult, modal_rasp_jsonData.get(l));
//                                serverContentList.add(detail);
//                            }
                        }
                    }
                    if (serverContentList.size() <= 0) {
                        createFinalList(serverContentList);
                    } else {
                        sortContentList(serverContentList);
                        getSubLevels_PI(serverContentList);
                    }
                } else {
                    serverContentList = gson.fromJson(response, listType);
                    sortContentList(serverContentList);
                    createFinalList(serverContentList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_RESOURCE) ||
                header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_RESOURCE_PI)) {
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
                            download_content = gson.fromJson(rasp_contents.getModalRaspResults().get(i).getJsonData(), Modal_DownloadContent.class);
                        }
                    }
                } else {
                    download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                }

                pos.addAll(download_content.getNodelist());

                for (int i = 0; i < pos.size(); i++) {
                    pos.get(i).setIsDownloaded("true");
                    String studID = AppDatabase.getDatabaseInstance(mContext).getContentTableDao().getEarlierStudentId(pos.get(i).getNodeId());
                    String currStudID = FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "");
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
                                pos.get(i).setStudentId(studID + "," + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
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
                        pos.get(i).setStudentId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                        if (FastSave.getInstance().getBoolean(FC_Constants.PRATHAM_STUDENT, false)) {
                            pos.get(i).setStudentId("pratham_group");
                        }
                    }
                }

                fileSize = "";
                if (!header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_RESOURCE_PI)) {
                    URL url = new URL(download_content.getDownloadurl());
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.connect();
                    fileSize = "" + FC_Utility.getFileSize(urlConnection.getContentLength());
                    learningView.setDownloadSize(" ("+fileSize+")");
                    Log.d("HP", "doInBackground: file SIZE : " + fileSize);
                }else
                    learningView.setDownloadSize("");
                fileName = download_content.getDownloadurl()
                        .substring(download_content.getDownloadurl().lastIndexOf('/') + 1);
                Log.d("HP", "doInBackground: fileName : " + fileName);
                Log.d("HP", "doInBackground: DW URL : " + download_content.getDownloadurl());

                if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_RESOURCE_PI)) {
                    if (fileName.contains(".zip") || fileName.contains(".rar")) {
                        String pi_url = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_URL + "zips/" + fileName;
                        zipDownloader.initialize(mContext, pi_url,
                                download_content.getFoldername(), fileName, dwContent, pos, true);
                    } else {
                        String pi_url = "na";
                        if (fileName.contains(".mp4"))
                            pi_url = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_URL + "videos/mp4/" + fileName;
                        else if (fileName.contains(".m4v"))
                            pi_url = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_URL + "videos/m4v/" + fileName;
                        else if (fileName.contains(".mp3"))
                            pi_url = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_URL + "audios/mp3/" + fileName;
                        else if (fileName.contains(".wav"))
                            pi_url = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_URL + "audios/wav/" + fileName;
                        else /*if (fileName.contains(".pdf"))*/
                            pi_url = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_URL + "docs/" + fileName;

                        Log.d("HP", "doInBackground: DW URL : " + pi_url);

                        zipDownloader.initialize(mContext, pi_url,
                                download_content.getFoldername(), fileName, dwContent, pos, false);
                    }
                } else {
                    zipDownloader.initialize(mContext, download_content.getDownloadurl(),
                            download_content.getFoldername(), fileName, dwContent, pos, fileName.contains(".zip") || fileName.contains(".rar"));

                }
/*
                if (fileName.contains(".zip") || fileName.contains(".rar"))
                    zipDownloader.initialize(mContext, download_content.getDownloadurl(),
                            download_content.getFoldername(), fileName, dwContent, pos, true);
                else zipDownloader.initialize(mContext, download_content.getDownloadurl(),
                        download_content.getFoldername(), fileName, dwContent, pos, false);
*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_ASSESSMENT_RESOURCE)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                pos.clear();
                pos.addAll(download_content.getNodelist());

                for (int i = 0; i < pos.size(); i++) {
                    pos.get(i).setIsDownloaded("true");
                    String studID = AppDatabase.getDatabaseInstance(mContext).getContentTableDao().getEarlierStudentId(pos.get(i).getNodeId());
                    String currStudID = FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "");
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
                                pos.get(i).setStudentId(studID + "," + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
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
                        pos.get(i).setStudentId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                        if (FastSave.getInstance().getBoolean(FC_Constants.PRATHAM_STUDENT, false)) {
                            pos.get(i).setStudentId("pratham_group");
                        }
                    }
                }

                AppDatabase.getDatabaseInstance(mContext).getContentTableDao().addContentList(pos);
                learningView.onTestAddedToDb(testItem);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_LEVEL) || header.equalsIgnoreCase(INTERNET_LEVEL_PI)) {
            try {
                rootLevelList = new ArrayList<>();
                Type listType = new TypeToken<ArrayList<ContentTable>>() {
                }.getType();
                List<ContentTable> serverContentList = new ArrayList<>();

                if (header.equalsIgnoreCase(INTERNET_LEVEL_PI)) {
                    try {
                        ModalRaspContentNew rasp_contents = gson.fromJson(response, ModalRaspContentNew.class);
                        rasp_contents.getModalRaspResults();
                        Log.e("url raspResult : ", String.valueOf(rasp_contents.getModalRaspResults().size()));
                        Modal_Rasp_JsonData modal_rasp_jsonData;
//                    List<Modal_Rasp_JsonData> modal_rasp_jsonData = new ArrayList<>();
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
                                raspResults.add(modalRaspResult);
                                modal_rasp_jsonData = gson.fromJson(rasp_contents.getModalRaspResults().get(i).getJsonData(), Modal_Rasp_JsonData.class);
                                ContentTable detail = modalRaspResult.setContentToConfigNodeStructure(modalRaspResult, modal_rasp_jsonData);
                                serverContentList.add(detail);
                            }
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                } else
                    serverContentList = gson.fromJson(response, listType);

                boolean itemFound = false;

                if (serverContentList.size() > 0) {
                    for (int i = 0; i < serverContentList.size(); i++) {
                        itemFound = false;
                        for (int j = 0; j < rootList.size(); j++) {
                            if (serverContentList.get(i).getNodeId().equalsIgnoreCase(rootList.get(j).getNodeId())) {
                                rootLevelList.add(rootList.get(j));
                                itemFound = true;
                                break;
                            }
                        }
                        if (!itemFound) {
                            ContentTable contentTableTemp = new ContentTable();
                            contentTableTemp.setNodeId("" + serverContentList.get(i).getNodeId());
                            contentTableTemp.setNodeType("" + serverContentList.get(i).getNodeType());
                            contentTableTemp.setNodeTitle("" + serverContentList.get(i).getNodeTitle());
                            contentTableTemp.setNodeKeywords("" + serverContentList.get(i).getNodeKeywords());
                            contentTableTemp.setNodeAge("" + serverContentList.get(i).getNodeAge());
                            contentTableTemp.setNodeDesc("" + serverContentList.get(i).getNodeDesc());
                            contentTableTemp.setNodeServerImage("" + serverContentList.get(i).getNodeServerImage());
                            contentTableTemp.setNodeImage("" + serverContentList.get(i).getNodeImage());
//                        contentTableTemp.setNodeImage("" + serverContentList.get(i).getNodeServerImage()
//                                .substring(serverContentList.get(i).getNodeServerImage().lastIndexOf('/') + 1));
                            contentTableTemp.setResourceId("" + serverContentList.get(i).getResourceId());
                            contentTableTemp.setResourceType("" + serverContentList.get(i).getResourceType());
                            contentTableTemp.setResourcePath("" + serverContentList.get(i).getResourcePath());
                            contentTableTemp.setParentId("" + serverContentList.get(i).getParentId());
                            contentTableTemp.setLevel("" + serverContentList.get(i).getLevel());
                            contentTableTemp.setVersion("" + serverContentList.get(i).getVersion());
                            contentTableTemp.setSeq_no(serverContentList.get(i).getSeq_no());
                            contentTableTemp.setContentType("" + serverContentList.get(i).getContentType());
                            contentTableTemp.setContentLanguage("" + serverContentList.get(i).getContentLanguage());
                            contentTableTemp.setIsDownloaded("false");
                            contentTableTemp.setOnSDCard(false);
                            rootLevelList.add(contentTableTemp);
                        }
                    }
                } else
                    learningView.setSelectedLevel(rootList);

            } catch (Exception e) {
                e.printStackTrace();
            }
            sortContentList(rootLevelList);
            learningView.setSelectedLevel(rootLevelList);
        } else if (header.equalsIgnoreCase(FC_Constants.BOTTOM_NODE) || header.equalsIgnoreCase(FC_Constants.BOTTOM_NODE_PI)) {
            try {
                Type listType = new TypeToken<ArrayList<ContentTable>>() {
                }.getType();
                List<ContentTable> serverContentList = new ArrayList<>();

                if (header.equalsIgnoreCase(FC_Constants.BOTTOM_NODE_PI)) {
                    try {
                        ModalRaspContentNew rasp_contents = gson.fromJson(response, ModalRaspContentNew.class);
                        rasp_contents.getModalRaspResults();
                        Log.e("url raspResult : ", String.valueOf(rasp_contents.getModalRaspResults().size()));
                        Modal_Rasp_JsonData modal_rasp_jsonData;
//                    List<Modal_Rasp_JsonData> modal_rasp_jsonData = new ArrayList<>();
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
                                //                            Type listTypes = new TypeToken<ArrayList<Modal_Rasp_JsonData>>() {
                                //                            }.getType();
                                //                            modal_rasp_jsonData = gson.fromJson(rasp_contents.getModalRaspResults().get(i).getJsonData(), listType);
                                //                            for (int l = 0; l < modal_rasp_jsonData.size(); l++) {
                                ContentTable detail = modalRaspResult.setContentToConfigNodeStructure(modalRaspResult, modal_rasp_jsonData/*.get(l)*/);
                                serverContentList.add(detail);
                                //                            }
                            }
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                } else
                    serverContentList = gson.fromJson(response, listType);

                String botNodeId = botID;
                if (serverContentList.size() > 0) {
                    botNodeId = serverContentList.get(0).getNodeId();
                    for (int i = 0; i < serverContentList.size(); i++)
                        if (serverContentList.get(i).getNodeTitle().equalsIgnoreCase(cosSection))
                            botNodeId = serverContentList.get(i).getNodeId();
//                learningView.setBotNodeId(botNodeId);
//                if (FC_Utility.isDataConnectionAvailable(mContext))
                    getLevelDataForList(currentLevelNo, botNodeId);
                } else {
                    getLevelDataForList(currentLevelNo, botID);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    int subPos = 0;
    public void getSubLevels_PI(List<ContentTable> serverContentList) {
        serverContentPIList = serverContentList;
        serverContentPIList2 = serverContentList;
        subPos = 0;
//        for (int i = 0; i < serverContentList.size(); i++) {
            api_content.getAPIContent_SubLevel_PI(PI_BROWSE_SUBLEVEL, RASPBERRY_PI_BROWSE_API,
                    serverContentPIList2.get(subPos).getNodeId(), subPos, serverContentList.size());
//        }
    }

    private void createFinalList(List<ContentTable> serverContentList) {
        try {
            boolean parentFound = false, childFound = false;
            contentDBList = new ArrayList<>();
            for (int i = 0; i < serverContentList.size(); i++) {
                parentFound = false;
                List<ContentTable> tempList;
                if (serverContentList.get(i).getNodelist() != null)
                    sortContentList(serverContentList.get(i).getNodelist());
                ContentTable contentTable = new ContentTable();
                for (int j = 0; j < contentParentList.size(); j++) {
                    if (serverContentList.get(i).getNodeId().equalsIgnoreCase(
                            contentParentList.get(j).getNodeId())) {
                        parentFound = true;
                        tempList = new ArrayList<>();
                        if (serverContentList.get(i).getVersion() != null &&
                                !serverContentList.get(i).getVersion().equalsIgnoreCase(contentParentList.get(j).getVersion()))
                            contentParentList.get(j).setNodeUpdate(true);
//                        contentDBList.add(contentParentList.get(j));
                        if (serverContentList.get(i).getNodelist().size() > 0) {
                            for (int k = 0; k < serverContentList.get(i).getNodelist().size(); k++) {
                                ContentTable contentTableTemp = new ContentTable();
                                childFound = false;
                                childContentList = new ArrayList<>();
                                childContentList = contentParentList.get(j).getNodelist();
                                if (childContentList.size() > 0) {
                                    for (int l = 0; l < childContentList.size(); l++) {
                                        if (serverContentList.get(i).getNodelist().get(k).getNodeId().equalsIgnoreCase(
                                                childContentList.get(l).getNodeId())) {
                                            if (serverContentList.get(i).getNodelist().get(k).getVersion() != null &&
                                                    !serverContentList.get(i).getNodelist().get(k).getVersion().equalsIgnoreCase(
                                                            childContentList.get(l).getVersion()))
                                                contentParentList.get(j).getNodelist().get(l).setNodeUpdate(true);
                                            childFound = true;
                                            break;
                                        }
                                    }
                                    if (!childFound) {
                                        try {
                                            contentTableTemp = new ContentTable();
                                            contentTableTemp.setNodeId("" + serverContentList.get(i).getNodelist().get(k).getNodeId());
                                            contentTableTemp.setNodeType("" + serverContentList.get(i).getNodelist().get(k).getNodeType());
                                            contentTableTemp.setNodeTitle("" + serverContentList.get(i).getNodelist().get(k).getNodeTitle());
                                            contentTableTemp.setNodeKeywords("" + serverContentList.get(i).getNodelist().get(k).getNodeKeywords());
                                            contentTableTemp.setNodeAge("" + serverContentList.get(i).getNodelist().get(k).getNodeAge());
                                            contentTableTemp.setNodeDesc("" + serverContentList.get(i).getNodelist().get(k).getNodeDesc());
                                            contentTableTemp.setNodeServerImage("" + serverContentList.get(i).getNodelist().get(k).getNodeServerImage());
                                            contentTableTemp.setNodeImage("" + serverContentList.get(i).getNodelist().get(k).getNodeImage());
//                                            contentTableTemp.setNodeImage("" + serverContentList.get(i).getNodelist().get(k).getNodeServerImage()
//                                                    .substring(serverContentList.get(i).getNodelist().get(k).getNodeServerImage().lastIndexOf('/') + 1));
                                            contentTableTemp.setResourceId("" + serverContentList.get(i).getNodelist().get(k).getResourceId());
                                            contentTableTemp.setResourceType("" + serverContentList.get(i).getNodelist().get(k).getResourceType());
                                            contentTableTemp.setResourcePath("" + serverContentList.get(i).getNodelist().get(k).getResourcePath());
                                            contentTableTemp.setParentId("" + serverContentList.get(i).getNodelist().get(k).getParentId());
                                            contentTableTemp.setLevel("" + serverContentList.get(i).getNodelist().get(k).getLevel());
                                            contentTableTemp.setSeq_no(serverContentList.get(i).getNodelist().get(k).getSeq_no());
                                            contentTableTemp.setNodePercentage("0");
                                            contentTableTemp.setVersion("" + serverContentList.get(i).getNodelist().get(k).getVersion());
                                            contentTableTemp.setContentType("" + serverContentList.get(i).getNodelist().get(k).getContentType());
                                            contentTableTemp.setContentLanguage("" + serverContentList.get(i).getNodelist().get(k).getContentLanguage());
                                            contentTableTemp.setIsDownloaded("false");
                                            contentTableTemp.setOnSDCard(false);
                                            contentTableTemp.setNodeUpdate(false);

                                            float prog = 0;
                                            if (serverContentList.get(i).getNodelist().get(k).getNodeType() != null
                                                    && serverContentList.get(i).getNodelist().get(k).getNodeType().equalsIgnoreCase("resource")) {
                                                prog = AppDatabase.getDatabaseInstance(mContext).getContentProgressDao().getResPercentage
                                                        (FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na"),
                                                                serverContentList.get(i).getNodelist().get(k).getResourceId());
                                                contentTableTemp.setNodePercentage("" + (int) prog);
                                            }
                                            try {
//                                                contentDBList.get(i).getNodelist().add(contentTableTemp);
                                                contentParentList.get(j).getNodelist().add(contentTableTemp);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            tempList.add(contentTableTemp);
                                            contentTable.setNodelist(tempList);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    for (int f = 0; f < serverContentList.get(i).getNodelist().size(); f++) {
                                        ContentTable contentTableChildTemp = new ContentTable();
                                        contentTableChildTemp.setNodeId("" + serverContentList.get(i).getNodelist().get(f).getNodeId());
                                        contentTableChildTemp.setNodeType("" + serverContentList.get(i).getNodelist().get(f).getNodeType());
                                        contentTableChildTemp.setNodeTitle("" + serverContentList.get(i).getNodelist().get(f).getNodeTitle());
                                        contentTableChildTemp.setNodeKeywords("" + serverContentList.get(i).getNodelist().get(f).getNodeKeywords());
                                        contentTableChildTemp.setNodeAge("" + serverContentList.get(i).getNodelist().get(f).getNodeAge());
                                        contentTableChildTemp.setNodeDesc("" + serverContentList.get(i).getNodelist().get(f).getNodeDesc());
                                        contentTableChildTemp.setNodeServerImage("" + serverContentList.get(i).getNodelist().get(f).getNodeServerImage());
                                        contentTableChildTemp.setNodeImage("" + serverContentList.get(i).getNodelist().get(f).getNodeImage());
//                                            contentTableChildTemp.setNodeImage("" +serverContentList.get(i).getNodelist().get(f).getNodeServerImage()
//                                                    .substring(serverContentList.get(i).getNodelist().get(f).getNodeServerImage().lastIndexOf('/') + 1));
                                        contentTableChildTemp.setResourceId("" + serverContentList.get(i).getNodelist().get(f).getResourceId());
                                        contentTableChildTemp.setResourceType("" + serverContentList.get(i).getNodelist().get(f).getResourceType());
                                        contentTableChildTemp.setResourcePath("" + serverContentList.get(i).getNodelist().get(f).getResourcePath());
                                        contentTableChildTemp.setParentId("" + serverContentList.get(i).getNodelist().get(f).getParentId());
                                        contentTableChildTemp.setSeq_no(serverContentList.get(i).getNodelist().get(f).getSeq_no());
                                        contentTableChildTemp.setNodePercentage("0");
                                        contentTableChildTemp.setLevel("" + serverContentList.get(i).getNodelist().get(f).getLevel());
                                        contentTableChildTemp.setVersion("" + serverContentList.get(i).getNodelist().get(f).getVersion());
                                        contentTableChildTemp.setContentType("" + serverContentList.get(i).getNodelist().get(f).getContentType());
                                        contentTableChildTemp.setContentLanguage("" + serverContentList.get(i).getNodelist().get(f).getContentLanguage());
                                        contentTableChildTemp.setIsDownloaded("false");
                                        contentTableChildTemp.setOnSDCard(false);
                                        contentTableChildTemp.setNodeUpdate(false);
                                        float prog = 0;
                                        if (serverContentList.get(i).getNodelist().get(f).getNodeType() != null
                                                && serverContentList.get(i).getNodelist().get(f).getNodeType().equalsIgnoreCase("resource")) {
                                            prog = AppDatabase.getDatabaseInstance(mContext).getContentProgressDao().getResPercentage
                                                    (FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na"),
                                                            serverContentList.get(i).getNodelist().get(f).getResourceId());
                                            contentTableChildTemp.setNodePercentage("" + (int) prog);
                                        }

                                        tempList.add(contentTableChildTemp);
                                    }
//                                    contentDBList.get(contentDBList.size() - 1).setNodelist(tempList);
                                    contentParentList.get(contentParentList.size() - 1).setNodelist(tempList);
                                }
                            }
                        }
                        break;
                    }
                }
                if (!parentFound) {
                    contentTable.setNodeId("" + serverContentList.get(i).getNodeId());
                    contentTable.setNodeType("" + serverContentList.get(i).getNodeType());
                    contentTable.setNodeTitle("" + serverContentList.get(i).getNodeTitle());
                    contentTable.setNodeKeywords("" + serverContentList.get(i).getNodeKeywords());
                    contentTable.setNodeAge("" + serverContentList.get(i).getNodeAge());
                    contentTable.setNodeDesc("" + serverContentList.get(i).getNodeDesc());
                    contentTable.setNodeServerImage("" + serverContentList.get(i).getNodeServerImage());
                    contentTable.setNodeImage("" + serverContentList.get(i).getNodeImage());
//                        contentTable.setNodeImage("" + serverContentList.get(i).getNodeServerImage()
//                                .substring(serverContentList.get(i).getNodeServerImage().lastIndexOf('/') + 1));
                    contentTable.setResourceId("" + serverContentList.get(i).getResourceId());
                    contentTable.setResourceType("" + serverContentList.get(i).getResourceType());
                    contentTable.setResourcePath("" + serverContentList.get(i).getResourcePath());
                    contentTable.setParentId("" + serverContentList.get(i).getParentId());
                    contentTable.setNodePercentage("0");
                    contentTable.setLevel("" + serverContentList.get(i).getLevel());
                    contentTable.setSeq_no(serverContentList.get(i).getSeq_no());
                    contentTable.setVersion("" + serverContentList.get(i).getVersion());
                    contentTable.setContentType("" + serverContentList.get(i).getContentType());
                    contentTable.setContentLanguage("" + serverContentList.get(i).getContentLanguage());
                    contentTable.setIsDownloaded("false");
                    contentTable.setOnSDCard(false);
                    contentTable.setNodeUpdate(false);

                    if (serverContentList.get(i).getNodelist() != null) {
                        if (serverContentList.get(i).getNodelist().size() > 0) {
                            tempList = new ArrayList<>();
                            ContentTable contentTableRes = new ContentTable();
                            for (int f = 0; f < serverContentList.get(i).getNodelist().size(); f++) {
                                ContentTable contentTableTemp = new ContentTable();
                                contentTableTemp.setNodeId("" + serverContentList.get(i).getNodelist().get(f).getNodeId());
                                contentTableTemp.setNodeType("" + serverContentList.get(i).getNodelist().get(f).getNodeType());
                                contentTableTemp.setNodeTitle("" + serverContentList.get(i).getNodelist().get(f).getNodeTitle());
                                contentTableTemp.setNodeKeywords("" + serverContentList.get(i).getNodelist().get(f).getNodeKeywords());
                                contentTableTemp.setNodeAge("" + serverContentList.get(i).getNodelist().get(f).getNodeAge());
                                contentTableTemp.setNodeDesc("" + serverContentList.get(i).getNodelist().get(f).getNodeDesc());
                                contentTableTemp.setNodeServerImage("" + serverContentList.get(i).getNodelist().get(f).getNodeServerImage());
                                contentTableTemp.setNodeImage("" + serverContentList.get(i).getNodelist().get(f).getNodeImage());
//                                contentTableTemp.setNodeImage("" +serverContentList.get(i).getNodelist().get(f).getNodeServerImage()
//                                        .substring(serverContentList.get(i).getNodelist().get(f).getNodeServerImage().lastIndexOf('/') + 1));
                                contentTableTemp.setResourceId("" + serverContentList.get(i).getNodelist().get(f).getResourceId());
                                contentTableTemp.setResourceType("" + serverContentList.get(i).getNodelist().get(f).getResourceType());
                                contentTableTemp.setResourcePath("" + serverContentList.get(i).getNodelist().get(f).getResourcePath());
                                contentTableTemp.setParentId("" + serverContentList.get(i).getNodelist().get(f).getParentId());
                                contentTableTemp.setLevel("" + serverContentList.get(i).getNodelist().get(f).getLevel());
                                contentTableTemp.setSeq_no(serverContentList.get(i).getNodelist().get(f).getSeq_no());
                                contentTableTemp.setNodePercentage("0");
                                contentTableTemp.setVersion("" + serverContentList.get(i).getNodelist().get(f).getVersion());
                                contentTableTemp.setContentType("" + serverContentList.get(i).getNodelist().get(f).getContentType());
                                contentTableTemp.setContentLanguage("" + serverContentList.get(i).getNodelist().get(f).getContentLanguage());
                                contentTableTemp.setIsDownloaded("false");
                                contentTableTemp.setOnSDCard(false);
                                contentTableTemp.setNodeUpdate(false);
                                float prog = 0;
                                if (serverContentList.get(i).getNodelist().get(f).getNodeType() != null
                                        && serverContentList.get(i).getNodelist().get(f).getNodeType().equalsIgnoreCase("resource")) {
                                    prog = AppDatabase.getDatabaseInstance(mContext).getContentProgressDao().getResPercentage
                                            (FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na"),
                                                    serverContentList.get(i).getNodelist().get(f).getResourceId());
                                    contentTableTemp.setNodePercentage("" + (int) prog);
                                }
                                tempList.add(contentTableTemp);
                            }
                            contentTable.setNodelist(tempList);
//                            contentDBList.add(contentTable);
                            contentParentList.add(contentTable);
                        }
                    } else {
//                        contentDBList.add(contentTable);
                        contentParentList.add(contentTable);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        addHeadersAndNotifyAdapter(contentDBList);
        addHeadersAndNotifyAdapter(contentParentList);
    }

    private void addHeadersAndNotifyAdapter(List<ContentTable> contentDBList) {
        Log.d("crashDetection", "addHeadersAndNotifyAdapter 1 : ");
        if (contentDBList.size() > 0) {
            ContentTable contentTableHeader = new ContentTable();
            contentTableHeader.setNodeId(TYPE_HEADER);
            contentTableHeader.setSeq_no(0);
            contentTableHeader.setNodeType(TYPE_HEADER);

            ContentTable contentTableFooter = new ContentTable();
            contentTableFooter.setNodeId(TYPE_FOOTER);
            contentTableFooter.setSeq_no(9999);
            contentTableFooter.setNodeType(TYPE_FOOTER);

            contentDBList.add(contentTableHeader);
            contentDBList.add(contentTableFooter);
            sortContentList(contentDBList);

            for (int i = 0; i < contentDBList.size(); i++) {
                if (!contentDBList.get(i).getNodeType().equalsIgnoreCase(TYPE_HEADER) &&
                        !contentDBList.get(i).getNodeType().equalsIgnoreCase(TYPE_FOOTER)) {
                    try {
                        contentDBList.get(i).getNodelist().add(contentTableHeader);
                        contentDBList.get(i).getNodelist().add(contentTableFooter);
                        sortContentList(contentDBList.get(i).getNodelist());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            learningView.showRecyclerLayout();
            Log.d("crashDetection", "addHeadersAndNotifyAdapter 2 : ");
            learningView.addContentToViewList(contentDBList);
            Log.d("crashDetection", "addHeadersAndNotifyAdapter 3 : ");
            learningView.notifyAdapter();
        } else
            learningView.showNoDataLayout();
    }

    @Background
    public void addScoreToDB(String resId) {
        try {
            String endTime = FC_Utility.getCurrentDateTime();
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setStudentID("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, ""));
            score.setDeviceID(FC_Utility.getDeviceID());
            score.setResourceID(resId);
            score.setQuestionId(0);
            score.setScoredMarks(0);
            score.setTotalMarks(0);
            score.setStartDateTime(endTime);
            score.setEndDateTime(endTime);
            score.setLevel(0);
            score.setLabel("Assessment Opened");
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(mContext).getScoreDao().insert(score);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void updateDownloads() {
        try {
            for (int i = 0; i < pos.size(); i++) {
                pos.get(i).setIsDownloaded("" + true);
                pos.get(i).setOnSDCard(false);
//                pos.get(i).setProgramid(FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na"));
            }
            AppDatabase.getDatabaseInstance(mContext).getContentTableDao().addContentList(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteContent(int parentPos, int childPos, ContentTable contentTableItem) {
        try {
            if (contentTableItem.getNodeType().equalsIgnoreCase("PreResource")) {
                List<ContentTable> contentTableList = AppDatabase.getDatabaseInstance(mContext).getContentTableDao()
                        .getChildsOfParent_forDelete(contentTableItem.getNodeId());
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
                checkAndDeleteParent(contentTableItem);
                Log.d("Delete_Clicked", "onClick: G_Presenter");
            } else {
                checkAndDeleteParent(contentTableItem);
                Log.d("Delete_Clicked", "onClick: G_Presenter");
                String foldername = contentTableItem.getResourcePath()/*.split("/")[0]*/;
                FC_Utility.deleteRecursive(new File(ApplicationClass.foundationPath
                        + gameFolderPath + "/" + foldername));
                FC_Utility.deleteRecursive(new File(ApplicationClass.foundationPath
                        + "" + App_Thumbs_Path + contentTableItem.getNodeImage()));
            }

            learningView.notifyAdapterItem(parentPos, childPos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAndDeleteParent(ContentTable contentItem) {
        AppDatabase.getDatabaseInstance(mContext).getContentTableDao().deleteContent(contentItem.getNodeId());
    }


    @Override
    public void receivedError(String header) {
        if (header.equalsIgnoreCase(FC_Constants.INTERNET_LEVEL)) {
//            if(rootList.size()>0)
//                learningView.serverIssueDialog();
            sortContentList(rootLevelList);
            learningView.setSelectedLevel(rootList);
        } else if (header.equalsIgnoreCase(FC_Constants.BOTTOM_NODE)) {
//            if(botID==null)
//                learningView.serverIssueDialog();
            getLevelDataForList(currentLevelNo, botID);
        } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_BROWSE)) {
//            if(contentParentList.size()>0)
//                learningView.serverIssueDialog();
            addHeadersAndNotifyAdapter(contentParentList);
//            learningView.addContentToViewList(contentParentList);
//            learningView.notifyAdapter();
        } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_RESOURCE)) {
            learningView.dismissLoadingDialog();
            learningView.showToast("Cannot connect.");
        }
    }

}