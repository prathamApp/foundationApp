package com.pratham.foundation.ui.app_home.learning_fragment;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.async.ZipDownloader;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.WordEnglish;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.modalclasses.Modal_DownloadAssessment;
import com.pratham.foundation.modalclasses.Modal_DownloadContent;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.pratham.foundation.ui.app_home.HomeActivity.sub_nodeId;
import static com.pratham.foundation.utility.FC_Constants.LOGIN_MODE;
import static com.pratham.foundation.utility.FC_Constants.QR_GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EBean
public class LearningPresenter implements LearningContract.LearningPresenter, API_Content_Result {

    Context mContext;
    LearningContract.LearningView learningView;
    public List<ContentTable> rootList, rootLevelList, dwParentList, childDwContentList;
    public List<ContentTable> contentParentList, contentDBList, contentApiList, childContentList;
    ArrayList<String> nodeIds;
    API_Content api_content;
    String downloadNodeId, fileName;
    Gson gson;
    ArrayList<String> codesText;
    int currentLevelNo;
    ContentTable contentDetail, testData;
    Modal_DownloadContent download_content;
    ArrayList<ContentTable> pos;
    public List<ContentTable> testList;
    List maxScore, maxScoreChild;
    @Bean(ZipDownloader.class)
    ZipDownloader zipDownloader;
    private String cosSection;

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

/*    @Background
    @Override
    public void displayProfileName() {
        String profileName;
        if (!GROUP_LOGIN)
            profileName = AppDatabase.getDatabaseInstance(mContext).getStudentDao().getFullName(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
        else
            profileName = AppDatabase.getDatabaseInstance(mContext).getGroupsDao().getGroupNameByGrpID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));

        learningView.setProfileName(profileName);
    }

    @Background
    @Override
    public void displayProfileImage() {
        String sImage;
        if (!GROUP_LOGIN)
            sImage = AppDatabase.getDatabaseInstance(mContext).getStudentDao().getStudentAvatar(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
        else
            sImage = "group_icon";
        learningView.setStudentProfileImage(sImage);
    }*/

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
        if (nodeIds.size() > 2) {
            nodeIds.remove(nodeIds.size() - 1);
            return true;
        } else
            return false;
    }

    @Background
    @Override
    public void getBottomNavId(int currentLevelNo, String cosSection) {
        this.currentLevelNo = currentLevelNo;
        this.cosSection = cosSection;
        String botID;
//        String rootID = FC_Utility.getRootNode(FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI));
        String rootID = sub_nodeId;
//        String rootID = "4030";
        botID = AppDatabase.appDatabase.getContentTableDao().getContentDataByTitle("" + rootID, cosSection);
        if (botID == null && !FC_Utility.isDataConnectionAvailable(mContext))
            learningView.showComingSoonDiaog();
        else if (botID != null && !FC_Utility.isDataConnectionAvailable(mContext))
            getLevelDataForList(currentLevelNo, botID);
        else
            getRootData(rootID);
    }

    @Background
    public void getRootData(String rootID) {
        if (FC_Utility.isDataConnectionAvailable(mContext))
            api_content.getAPIContent(FC_Constants.BOTTOM_NODE, FC_Constants.INTERNET_DOWNLOAD_NEW_API, rootID);
    }

    @Background
    @Override
    public void getLevelDataForList(int currentLevelNo, String bottomNavNodeId) {
        rootList = AppDatabase.appDatabase.getContentTableDao().getContentData("" + bottomNavNodeId);
        if (FC_Utility.isDataConnectionAvailable(mContext))
            getLevelDataFromApi(currentLevelNo, bottomNavNodeId);
        else
            learningView.setSelectedLevel(rootList);
    }

    public void getLevelDataFromApi(int currentLevelNo, String botNodeId) {
        api_content.getAPIContent(FC_Constants.INTERNET_LEVEL, FC_Constants.INTERNET_DOWNLOAD_NEW_API, botNodeId);
    }

    @Background
    @Override
    public void findMaxScore(String nodeId) {
        try {
            List<ContentTable> childList = AppDatabase.getDatabaseInstance(mContext).getContentTableDao().getChildsOfParent(nodeId);
            for (int childCnt = 0; childList.size() > childCnt; childCnt++) {
                if (childList.get(childCnt).getNodeType().equals("Resource")) {
                    double maxScoreTemp = 0.0;
                    List<ContentProgress> score = AppDatabase.getDatabaseInstance(mContext).getContentProgressDao().getProgressByStudIDAndResID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""),
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
        List<ContentTable> childList = AppDatabase.getDatabaseInstance(mContext).getContentTableDao().getChildsOfParent(nodeId);
        for (int childCnt = 0; childList.size() > childCnt; childCnt++) {
            if (childList.get(childCnt).getNodeType().equals("Resource")) {
                double maxScoreTemp = 0.0;
                List<ContentProgress> score = AppDatabase.getDatabaseInstance(mContext).getContentProgressDao().getProgressByStudIDAndResID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), childList.get(childCnt).getResourceId(), "resourceProgress");
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
            dwParentList = AppDatabase.appDatabase.getContentTableDao().getContentData("" + nodeIds.get(nodeIds.size() - 1));
            sortContentList(dwParentList);
            contentParentList.clear();
            ContentTable resContentTable = new ContentTable();
            List<ContentTable> resourceList = new ArrayList<>();
            List<ContentTable> tempList2 = new ArrayList<>();

            ContentTable contentTableRes = new ContentTable();
            contentTableRes.setNodeId("0");
            contentTableRes.setNodeType("Header");
            tempList2.add(contentTableRes);
            resContentTable.setNodelist(tempList2);
            resourceList.add(contentTableRes);
            contentParentList.add(contentTableRes);

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
                        contentTableRes.setContentType("" + dwParentList.get(j).getContentType());
                        contentTableRes.setIsDownloaded("" + dwParentList.get(j).getIsDownloaded());
                        contentTableRes.setOnSDCard(dwParentList.get(j).isOnSDCard());
                        contentTableRes.setNodelist(tempList2);
                        resourceList.add(contentTableRes);
                    } else {
                        List<ContentTable> tempList;
                        ContentTable contentTable = new ContentTable();
                        tempList = new ArrayList<>();
                        childDwContentList = AppDatabase.appDatabase.getContentTableDao().getContentData("" + dwParentList.get(j).getNodeId());
                        sortContentList(childDwContentList);
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
                        contentTable.setContentType(dwParentList.get(j).getContentType());
                        contentTable.setIsDownloaded("" + dwParentList.get(j).getIsDownloaded());
                        contentTable.setOnSDCard(dwParentList.get(j).isOnSDCard());

                        int childListSize = childDwContentList.size();
                        if (childDwContentList.size() > 0) {
                            ContentTable contentChild = new ContentTable();
                            contentChild.setNodeId("0");
                            contentChild.setNodeType("Header");
                            tempList.add(contentChild);
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
                                contentChild.setContentType(childDwContentList.get(i).getContentType());
                                contentChild.setIsDownloaded("" + childDwContentList.get(i).getIsDownloaded());
                                contentChild.setOnSDCard(childDwContentList.get(i).isOnSDCard());
                                contentChild.setNodelist(null);
                                maxScoreChild = new ArrayList();
                                if (!LOGIN_MODE.equalsIgnoreCase(QR_GROUP_MODE))
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
                                tempList.add(contentChild);
                            }
                            contentChild = new ContentTable();
                            contentChild.setNodeId("999999");
                            contentChild.setNodeType("Header");
                            tempList.add(contentChild);
                        }
                        sortAllList(tempList);
                        contentTable.setNodelist(tempList);
                        contentParentList.add(contentTable);
                    }
                }
                if (resourceList.size() > 1) {
                    contentTableRes = new ContentTable();
                    contentTableRes.setNodeId("999999");
                    contentTableRes.setNodeType("Header");
                    tempList2.add(contentTableRes);
                    resContentTable.setNodelist(tempList2);
                    resourceList.add(contentTableRes);

                    resContentTable.setNodelist(resourceList);
                    contentParentList.add(resContentTable);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateUI();
    }

    @UiThread
    public void updateUI() {
        maxScore.clear();
//        try {
//            findMaxScore("" + nodeIds.get(nodeIds.size() - 1));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //TODO add Use this code for API.
        if (FC_Utility.isDataConnectionAvailable(mContext)) {
            api_content.getAPIContent(FC_Constants.INTERNET_DOWNLOAD,
                    FC_Constants.INTERNET_DOWNLOAD_NEW_API, nodeIds.get(nodeIds.size() - 1));
        } else {
            if (contentParentList.size() == 0 && !FC_Utility.isDataConnectionAvailable(mContext)) {
                learningView.showNoDataDownloadedDialog();
            } else {
                learningView.addContentToViewList(contentParentList);
                learningView.notifyAdapter();
            }
        }
//        learningView.addContentToViewList(contentParentList);
//        learningView.notifyAdapter();

    }

    public void sortAllList(List<ContentTable> contentParentList) {
        Collections.sort(contentParentList, new Comparator<ContentTable>() {
            @Override
            public int compare(ContentTable o1, ContentTable o2) {
                return o1.getNodeId().compareTo(o2.getNodeId());
            }
        });
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
               /* if (person.getWord() == 0 ) {
                    System.out.println("Stream mode: " + person);
                }*/
                arrayList.add(person);
            }
            reader.close();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
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
    public void downloadResource(String downloadId) {
        downloadNodeId = downloadId;
        if (FC_Utility.isDataConnectionAvailable(mContext)) {
            api_content.getAPIContent(FC_Constants.INTERNET_DOWNLOAD_RESOURCE, FC_Constants.INTERNET_DOWNLOAD_RESOURCE_API, downloadNodeId);
        } else {
            learningView.showNoDataDownloadedDialog();
        }
//        getAPIContent(FC_Constants.INTERNET_DOWNLOAD_RESOURCE, FC_Constants.INTERNET_DOWNLOAD_RESOURCE_API);
    }

    @Background
    @Override
    public void updateDownloadJson(String folderPath) {
        String path = ApplicationClass.foundationPath + "" + gameFolderPath + folderPath;
        try {
            InputStream is = new FileInputStream(path + "/gameinfo.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONObject jsonObj = new JSONObject(jsonStr);
            Gson gson = new Gson();
            Modal_DownloadAssessment download_content = gson.fromJson(jsonObj.toString(), Modal_DownloadAssessment.class);
            List<ContentTable> contentTableList = new ArrayList<>();
            contentTableList = download_content.getNodelist();
            for (int i = 0; i < contentTableList.size(); i++) {
                API_Content.downloadImage(contentTableList.get(i).nodeServerImage, contentTableList.get(i).getNodeImage());
                contentTableList.get(i).setIsDownloaded("true");
            }
            AppDatabase.getDatabaseInstance(mContext).getContentTableDao().addContentList(contentTableList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BackupDatabase.backup(mContext);
        learningView.dismissDownloadDialog();
//        learningView.hideTestDownloadBtn();
//        learningView.displayCurrentDownloadedTest();
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
        Collections.sort(contentParentList, (o1, o2) -> o1.getNodeId().compareTo(o2.getNodeId()));
    }

    @Background
    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD)) {
            boolean parentFound = false, childFound = false;
            try {
                contentDBList.clear();
                Type listType = new TypeToken<ArrayList<ContentTable>>() {
                }.getType();
                List<ContentTable> serverContentList = gson.fromJson(response, listType);

                List<ContentTable> resourceSerList = new ArrayList<>();
                List<ContentTable> tempSerList2 = new ArrayList<>();
                ContentTable resContentTableSer = new ContentTable();

                ContentTable contentTableServerRes = new ContentTable();
                contentTableServerRes.setNodeId("0");
                contentTableServerRes.setNodeType("Header");
                tempSerList2.add(contentTableServerRes);
                resContentTableSer.setNodelist(tempSerList2);
                resourceSerList.add(contentTableServerRes);
                contentDBList.add(contentTableServerRes);

                for (int i = 0; i < serverContentList.size(); i++) {
                    parentFound = false;
                    List<ContentTable> tempList;
                    ContentTable contentTable = new ContentTable();
                    for (int j = 0; j < contentParentList.size(); j++) {
                        if (serverContentList.get(i).getNodeId().equalsIgnoreCase(
                                contentParentList.get(j).getNodeId())) {
                            parentFound = true;
                            tempList = new ArrayList<>();
                            contentDBList.add(contentParentList.get(j));
                            if (serverContentList.get(i).getNodelist().size() > 0)
                                for (int k = 0; k < serverContentList.get(i).getNodelist().size(); k++) {
                                    ContentTable contentTableTemp = new ContentTable();
                                    childFound = false;
                                    int listChild = 0;
                                    childContentList = new ArrayList<>();
                                    childContentList = contentParentList.get(j).getNodelist();
                                    if (childContentList.size() > 0) {
                                        for (int l = 0; l < childContentList.size(); l++) {
                                            if (serverContentList.get(i).getNodelist().get(k).getNodeId().equalsIgnoreCase(
                                                    childContentList.get(l).getNodeId())) {
                                                childFound = true;
//                                                contentDBList.get(j).getNodelist().add(childContentList.get(l));
                                                listChild = l;
                                                break;
                                            }
                                        }
                                        if (!childFound) {
                                            contentTableTemp = new ContentTable();
                                            contentTableTemp.setNodeId("" + serverContentList.get(i).getNodelist().get(k).getNodeId());
                                            contentTableTemp.setNodeType("" + serverContentList.get(i).getNodelist().get(k).getNodeType());
                                            contentTableTemp.setNodeTitle("" + serverContentList.get(i).getNodelist().get(k).getNodeTitle());
                                            contentTableTemp.setNodeKeywords("" + serverContentList.get(i).getNodelist().get(k).getNodeKeywords());
                                            contentTableTemp.setNodeAge("" + serverContentList.get(i).getNodelist().get(k).getNodeAge());
                                            contentTableTemp.setNodeDesc("" + serverContentList.get(i).getNodelist().get(k).getNodeDesc());
                                            contentTableTemp.setNodeServerImage("" + serverContentList.get(i).getNodelist().get(k).getNodeServerImage());
                                            contentTableTemp.setNodeImage("" + serverContentList.get(i).getNodelist().get(k).getNodeImage());
                                            contentTableTemp.setResourceId("" + serverContentList.get(i).getNodelist().get(k).getResourceId());
                                            contentTableTemp.setResourceType("" + serverContentList.get(i).getNodelist().get(k).getResourceType());
                                            contentTableTemp.setResourcePath("" + serverContentList.get(i).getNodelist().get(k).getResourcePath());
                                            contentTableTemp.setParentId("" + serverContentList.get(i).getNodelist().get(k).getParentId());
                                            contentTableTemp.setLevel("" + serverContentList.get(i).getNodelist().get(k).getLevel());
                                            contentTableTemp.setNodePercentage("0");
                                            contentTableTemp.setVersion("" + serverContentList.get(i).getNodelist().get(k).getVersion());
                                            contentTableTemp.setContentType("" + serverContentList.get(i).getNodelist().get(k).getContentType());
                                            contentTableTemp.setIsDownloaded("false");
                                            contentTableTemp.setOnSDCard(false);

                                            contentDBList.get(i).getNodelist().add(contentTableTemp);
                                            tempList.add(contentTableTemp);
                                            contentTable.setNodelist(tempList);
//                                        contentParentList.get(j).getNodelist().add(contentTable);
//                                        contentParentList.get(j).getNodelist().add();
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
                                            contentTableChildTemp.setResourceId("" + serverContentList.get(i).getNodelist().get(f).getResourceId());
                                            contentTableChildTemp.setResourceType("" + serverContentList.get(i).getNodelist().get(f).getResourceType());
                                            contentTableChildTemp.setResourcePath("" + serverContentList.get(i).getNodelist().get(f).getResourcePath());
                                            contentTableChildTemp.setParentId("" + serverContentList.get(i).getNodelist().get(f).getParentId());
                                            contentTableTemp.setNodePercentage("0");
                                            contentTableChildTemp.setLevel("" + serverContentList.get(i).getNodelist().get(f).getLevel());
                                            contentTableChildTemp.setVersion("" + serverContentList.get(i).getNodelist().get(f).getVersion());
                                            contentTableChildTemp.setContentType("" + serverContentList.get(i).getNodelist().get(f).getContentType());
                                            contentTableChildTemp.setIsDownloaded("false");
                                            contentTableChildTemp.setOnSDCard(false);
                                            tempList.add(contentTableChildTemp);
                                        }
//                                        contentTable.setNodelist(tempList);
                                        //Added whole child.
                                        contentTableTemp = new ContentTable();
                                        contentTableTemp.setNodeId("0");
                                        contentTableTemp.setNodeType("Header");
                                        tempList.add(contentTableTemp);

                                        contentTableTemp = new ContentTable();
                                        contentTableTemp.setNodeId("999999");
                                        contentTableTemp.setNodeType("Header");
                                        tempList.add(contentTableTemp);
                                        sortAllList(tempList);
                                        contentDBList.get(i).setNodelist(tempList);
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
                        contentTable.setResourceId("" + serverContentList.get(i).getResourceId());
                        contentTable.setResourceType("" + serverContentList.get(i).getResourceType());
                        contentTable.setResourcePath("" + serverContentList.get(i).getResourcePath());
                        contentTable.setParentId("" + serverContentList.get(i).getParentId());
                        contentTable.setNodePercentage("0");
                        contentTable.setLevel("" + serverContentList.get(i).getLevel());
                        contentTable.setVersion("" + serverContentList.get(i).getVersion());
                        contentTable.setContentType("" + serverContentList.get(i).getContentType());
                        contentTable.setIsDownloaded("false");
                        contentTable.setOnSDCard(false);

                        if (serverContentList.get(i).getNodelist().size() > 0) {
                            tempList = new ArrayList<>();
                            ContentTable contentTableRes = new ContentTable();
                            contentTableRes.setNodeId("0");
                            contentTableRes.setNodeType("Header");
                            tempList.add(contentTableRes);

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
                                contentTableTemp.setResourceId("" + serverContentList.get(i).getNodelist().get(f).getResourceId());
                                contentTableTemp.setResourceType("" + serverContentList.get(i).getNodelist().get(f).getResourceType());
                                contentTableTemp.setResourcePath("" + serverContentList.get(i).getNodelist().get(f).getResourcePath());
                                contentTableTemp.setParentId("" + serverContentList.get(i).getNodelist().get(f).getParentId());
                                contentTableTemp.setLevel("" + serverContentList.get(i).getNodelist().get(f).getLevel());
                                contentTableTemp.setVersion("" + serverContentList.get(i).getNodelist().get(f).getVersion());
                                contentTableTemp.setContentType("" + serverContentList.get(i).getNodelist().get(f).getContentType());
                                contentTableTemp.setIsDownloaded("false");
                                contentTableTemp.setOnSDCard(false);
                                tempList.add(contentTableTemp);
                            }
                            contentTableRes = new ContentTable();
                            contentTableRes.setNodeId("0");
                            contentTableRes.setNodeType("Header");
                            tempList.add(contentTableRes);

                            contentTableRes = new ContentTable();
                            contentTableRes.setNodeId("9999999");
                            contentTableRes.setNodeType("Header");
                            tempList.add(contentTableRes);
                            sortAllList(tempList);
                            contentTable.setNodelist(tempList);
                            contentDBList.add(contentTable);
                        }
                    }
                }
                //learningView.addContentToViewList(contentParentList);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            contentParentList = contentDBList;
            learningView.addContentToViewList(contentDBList);
            learningView.notifyAdapter();

        } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD_RESOURCE)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                contentDetail = download_content.getNodelist().get(download_content.getNodelist().size() - 1);
                pos.clear();

                for (int i = 0; i < download_content.getNodelist().size(); i++) {
                    ContentTable contentTableTemp = download_content.getNodelist().get(i);
                    pos.add(contentTableTemp);
                }

                fileName = download_content.getDownloadurl()
                        .substring(download_content.getDownloadurl().lastIndexOf('/') + 1);
                Log.d("HP", "doInBackground: fileName : " + fileName);
                Log.d("HP", "doInBackground: DW URL : " + download_content.getDownloadurl());
                zipDownloader.initialize(mContext, download_content.getDownloadurl(),
                        download_content.getFoldername(), fileName, contentDetail, pos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_LEVEL)) {
            try {
                rootLevelList = new ArrayList<>();
                Type listType = new TypeToken<ArrayList<ContentTable>>() {
                }.getType();
                List<ContentTable> serverContentList = gson.fromJson(response, listType);
                boolean itemFound = false;

                for (int i = 0; i < serverContentList.size(); i++) {
                    for (int j = 0; j < rootList.size(); j++) {
                        if (serverContentList.get(i).getNodeId().equalsIgnoreCase(rootList.get(j).getNodeId())) {
                            rootLevelList.add(rootList.get(j));
                            itemFound = true;
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
                        contentTableTemp.setResourceId("" + serverContentList.get(i).getResourceId());
                        contentTableTemp.setResourceType("" + serverContentList.get(i).getResourceType());
                        contentTableTemp.setResourcePath("" + serverContentList.get(i).getResourcePath());
                        contentTableTemp.setParentId("" + serverContentList.get(i).getParentId());
                        contentTableTemp.setLevel("" + serverContentList.get(i).getLevel());
                        contentTableTemp.setVersion("" + serverContentList.get(i).getVersion());
                        contentTableTemp.setContentType("" + serverContentList.get(i).getContentType());
                        contentTableTemp.setIsDownloaded("false");
                        contentTableTemp.setOnSDCard(false);
                        rootLevelList.add(contentTableTemp);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            sortContentList(rootLevelList);
            learningView.setSelectedLevel(rootLevelList);
        } else if (header.equalsIgnoreCase(FC_Constants.BOTTOM_NODE)) {
            try {
                Type listType = new TypeToken<ArrayList<ContentTable>>() {
                }.getType();
                List<ContentTable> serverContentList = gson.fromJson(response, listType);
                String botNodeId = serverContentList.get(0).getNodeId();
                for (int i = 0; i < serverContentList.size(); i++)
                    if (serverContentList.get(i).getNodeTitle().equalsIgnoreCase(cosSection))
                        botNodeId = serverContentList.get(i).getNodeId();
//                learningView.setBotNodeId(botNodeId);
                if (FC_Utility.isDataConnectionAvailable(mContext))
                    getLevelDataForList(currentLevelNo, botNodeId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Background
    @Override
    public void updateDownloads() {
        try {
            for (int i = 0; i < pos.size(); i++) {
                pos.get(i).setIsDownloaded("" + true);
                pos.get(i).setOnSDCard(false);
            }
            AppDatabase.appDatabase.getContentTableDao().addContentList(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receivedError(String header) {
        if (header.equalsIgnoreCase(FC_Constants.INTERNET_LEVEL)) {
            learningView.setSelectedLevel(rootList);
        } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD)) {
            learningView.addContentToViewList(contentParentList);
            learningView.notifyAdapter();
        }
    }
}