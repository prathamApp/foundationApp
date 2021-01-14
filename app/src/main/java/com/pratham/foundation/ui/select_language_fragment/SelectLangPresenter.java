package com.pratham.foundation.ui.select_language_fragment;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.APP_BOARD_STRING;
import static com.pratham.foundation.utility.FC_Constants.APP_LANGUAGE_STRING;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_STUDENT_ID;
import static com.pratham.foundation.utility.FC_Constants.INTERNET_LANGUAGE_API;
import static com.pratham.foundation.utility.FC_Constants.newRootParentId;

@EBean
public class SelectLangPresenter implements SelectLangContract.SelectLangPresenter, API_Content_Result {

    public Context context;
    public SelectLangContract.SelectLangView view;
    public Gson gson;
    public ContentTable contentDetail;
    public API_Content api_content;
    private List<ContentTable> boardList, langList;
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
        boardList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(newRootParentId,
                "%"+ FastSave.getInstance().getString(CURRENT_STUDENT_ID,"na")+"%");
//        langList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(rootParentId);
        //fetch language from API
        if (boardList.size() > 0)
            langList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(boardList.get(0).getNodeId(),
                "%"+ FastSave.getInstance().getString(CURRENT_STUDENT_ID,"na")+"%");
        if (FC_Utility.isDataConnectionAvailable(context))
            api_content.getBoardAPI(APP_BOARD_STRING, INTERNET_LANGUAGE_API);
        else {
            if (boardList.size() > 0) {
                langList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(boardList.get(0).getNodeId(),
                        "%"+ FastSave.getInstance().getString(CURRENT_STUDENT_ID,"na")+"%");
                view.updateLangList(langList);
                view.notifyAdapter();
            }else
                view.connectToInternetDialog();
        }

    }

    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(APP_BOARD_STRING)) {
            try {
                Type listType = new TypeToken<ArrayList<ContentTable>>() {}.getType();
                List<ContentTable> serverContentList = gson.fromJson(response, listType);
                if(serverContentList.size()>0)
                    api_content.getAPILanguage(APP_LANGUAGE_STRING, INTERNET_LANGUAGE_API,serverContentList.get(0).getNodeId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (header.equalsIgnoreCase(APP_LANGUAGE_STRING)) {
            try {
                Type listType = new TypeToken<ArrayList<ContentTable>>() {
                }.getType();
                List<ContentTable> serverContentList = gson.fromJson(response, listType);
                for (int i = 0; i < serverContentList.size(); i++) {
                    parentFound = false;
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
                //Set recieved language and notifyadapter
                view.updateLangList(langList);
                view.notifyAdapter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void receivedError(String header) {
        if(langList.size()>0) {
            view.updateLangList(langList);
            view.notifyAdapter();
        }else {
            view.serverIssueDialog();
        }
        view.dismissLoadingDialog();
    }
}