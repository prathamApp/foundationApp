package com.pratham.foundation.ui.selectSubject;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.APP_LANGUAGE;
import static com.pratham.foundation.utility.FC_Constants.HINDI;

@EBean
public class SelectSubjectPresenter implements SelectSubjectContract.SubjectPresenter, API_Content_Result {

    Context context;
    API_Content api_content;
    Gson gson;
    SelectSubjectContract.SubjectView subjectView;
    List<ContentTable> subjectList;

    SelectSubjectPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(SelectSubjectContract.SubjectView subjectView) {
        this.subjectView = subjectView;
        api_content = new API_Content(context, SelectSubjectPresenter.this);
        gson = new Gson();
    }

    @Background
    @Override
    public void getSubjectList() {
        subjectView.showLoader();
        String rootID = AppDatabase.getDatabaseInstance(context).getContentTableDao().getRootData("0",
                FastSave.getInstance().getString(APP_LANGUAGE, HINDI));
        if (rootID != null)
            subjectList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getChildsOfParent(rootID);

        if (FC_Utility.isDataConnectionAvailable(context))
            api_content.getAPIContent(FC_Constants.INTERNET_DOWNLOAD, FC_Constants.INTERNET_DOWNLOAD_NEW_API, rootID);
        else
            subjectView.initializeSubjectList(subjectList);
    }

    public void getLanguageFromApi() {
        if (FC_Utility.isDataConnectionAvailable(context))
            api_content.getAPILanguage(FC_Constants.APP_LANGUAGE_STRING, FC_Constants.INTERNET_DOWNLOAD_NEW_API);
//            api_content.getAPILanguage(FC_Constants.APP_LANGUAGE_STRING, FC_Constants.INTERNET_LANGUAGE_API);
    }

    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(FC_Constants.APP_LANGUAGE_STRING)) {
            try {
                Type listType = new TypeToken<ArrayList<ContentTable>>() {
                }.getType();

                List<ContentTable> serverContentList = gson.fromJson(response, listType);
                subjectView.showLanguageSelectionDialog(serverContentList);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (header.equalsIgnoreCase(FC_Constants.INTERNET_DOWNLOAD)) {
            try {
                Type listType = new TypeToken<ArrayList<ContentTable>>() {
                }.getType();

                List<ContentTable> serverContentList = gson.fromJson(response, listType);
                subjectView.showLanguageSelectionDialog(serverContentList);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void receivedError(String header) {

    }
}
