package com.pratham.foundation.ui.selectSubject;

import static com.pratham.foundation.utility.FC_Constants.APP_LANGUAGE;
import static com.pratham.foundation.utility.FC_Constants.APP_LANGUAGE_NODE_ID;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_STUDENT_ID;
import static com.pratham.foundation.utility.FC_Constants.HINDI;
import static com.pratham.foundation.utility.FC_Constants.PI_BROWSE;
import static com.pratham.foundation.utility.FC_Constants.RASPBERRY_PI_LANGUAGE_API;
import static com.pratham.foundation.utility.FC_Constants.newRootParentId;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.modalclasses.RaspModel.ModalRaspContentNew;
import com.pratham.foundation.modalclasses.RaspModel.Modal_RaspResult;
import com.pratham.foundation.modalclasses.RaspModel.Modal_Rasp_JsonData;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EBean
public class SelectSubjectPresenter implements SelectSubjectContract.SubjectPresenter, API_Content_Result {

    Context context;
    API_Content api_content;
    Gson gson;
    SelectSubjectContract.SubjectView subjectView;
    List<ContentTable> subjectList, langList;
    boolean parentFound = false;

    SelectSubjectPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(SelectSubjectContract.SubjectView subjectView) {
        this.subjectView = subjectView;
        api_content = new API_Content(context, SelectSubjectPresenter.this);
        langList = new ArrayList<>();
        subjectList = new ArrayList<>();
        gson = new Gson();
    }

    @Override
    public void clearSubjList() {
        subjectList.clear();
    }

    private void sortAllList(List<ContentTable> contentParentList) {
        Collections.sort(contentParentList, (o1, o2) -> o1.getSeq_no() - o2.getSeq_no());
    }

    @Background
    @Override
    public void getSubjectList() {
        subjectView.showLoader();
        String currLang = FastSave.getInstance().getString(APP_LANGUAGE, HINDI);
        String currLangNodeId = FastSave.getInstance().getString(APP_LANGUAGE_NODE_ID, "");
        Log.d("currLang", "getSubjectList: " + currLang);
        String rootID = AppDatabase.getDatabaseInstance(context).getContentTableDao().getRootData(newRootParentId,
//                FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%");
        AppDatabase.getDatabaseInstance(context).getContentProgressDao().updateFullPercent();
        if (currLangNodeId != null)
            subjectList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getChildsOfParent(currLangNodeId,
                    "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%"
                    /*FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")*/);

        if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            //Checks if device is connected to raspberry pie
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                api_content.getAPIContent_PI(PI_BROWSE, RASPBERRY_PI_LANGUAGE_API, currLangNodeId);
            } else {
                api_content.getAPIContent(FC_Constants.INTERNET_BROWSE, FC_Constants.INTERNET_LANGUAGE_API, currLangNodeId);
            }
        } else {
            if (subjectList != null && subjectList.size() < 1)
                subjectView.noDataDialog();
            else {
                sortAllList(subjectList);
                subjectView.initializeSubjectList(subjectList);
                subjectView.notifySubjAdapter();
            }
            subjectView.dismissLoadingDialog();
        }
    }

    @Override
    public void receivedContent_PI_SubLevel(String header, String response, int pos, int size) {
    }

    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(FC_Constants.INTERNET_BROWSE) || header.equalsIgnoreCase(FC_Constants.PI_BROWSE)) {
            try {
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

                for (int i = 0; i < serverContentList.size(); i++) {
                    parentFound = false;
                    List<ContentTable> tempList;
                    ContentTable contentTable = new ContentTable();
                    serverContentList.get(i).setIsDownloaded("false");
                    for (int j = 0; j < subjectList.size(); j++) {
                        if (serverContentList.get(i).getNodeId().equalsIgnoreCase(
                                subjectList.get(j).getNodeId())) {
                            parentFound = true;
                        }
                    }
                    if (!parentFound) {
                        subjectList.add(serverContentList.get(i));
                    }
                }
                //Set recieved subject and notifyadapter
                if (subjectList != null && subjectList.size() < 1)
                    subjectView.noDataDialog();
                else {
                    sortAllList(subjectList);
                    subjectView.initializeSubjectList(subjectList);
                    subjectView.notifySubjAdapter();
                }
                subjectView.dismissLoadingDialog();
            } catch (Exception e) {
                e.printStackTrace();
                if (subjectList != null && subjectList.size() < 1)
                    subjectView.noDataDialog();
                else {
                    sortAllList(subjectList);
                    subjectView.initializeSubjectList(subjectList);
                    subjectView.notifySubjAdapter();
                }
                subjectView.dismissLoadingDialog();
            }
        }
    }

    @Override
    public void receivedError(String header) {
        if (subjectList.size() > 0) {
            sortAllList(subjectList);
            subjectView.initializeSubjectList(subjectList);
            subjectView.notifySubjAdapter();
        } else {
            subjectView.serverIssueDialog();
        }
        subjectView.dismissLoadingDialog();
    }
}
