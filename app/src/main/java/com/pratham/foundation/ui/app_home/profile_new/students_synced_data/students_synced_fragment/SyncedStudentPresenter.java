package com.pratham.foundation.ui.app_home.profile_new.students_synced_data.students_synced_fragment;

import android.content.Context;

import com.google.gson.Gson;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.modalclasses.MainSyncDetails;
import com.pratham.foundation.modalclasses.StudentSyncDetails;
import com.pratham.foundation.modalclasses.SyncAccessedModal;
import com.pratham.foundation.modalclasses.SyncRegisteredModal;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

@EBean
public class SyncedStudentPresenter implements SyncedStudentContract.SyncedStudentPresenter, API_Content_Result {


    SyncedStudentContract.SyncedStudentView studentView;

    public List<MainSyncDetails> syncDetails = null;
    public List<StudentSyncDetails> studentSyncDetailsList = null;

    Context mContext;
    String selectedLangName = "na";
    private List<ContentTable> boardList, langList, subjList, setTabList, setLevelList;
    API_Content api_content;

    public SyncedStudentPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(SyncedStudentContract.SyncedStudentView studentView) {
        this.studentView = studentView;
        syncDetails = new ArrayList<>();
        studentSyncDetailsList = new ArrayList<>();
        api_content = new API_Content(mContext, SyncedStudentPresenter.this);
    }

    @Override
    public void getDataFromServer(String syncType, String startDate, String endDate) {
        if(syncType.equalsIgnoreCase(FC_Constants.SYNC_ACCESSED_USERS))
            api_content.getSyncedSummary(FC_Constants.SYNC_ACCESSED_USERS,
                    FC_Constants.SYNC_ACCESSED_USERS_API, startDate,endDate);
        else
            api_content.getSyncedSummary(FC_Constants.SYNC_REGISTERED_USERS,
                    FC_Constants.SYNC_REGISTERED_USERS_API, startDate,endDate);
    }


    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(FC_Constants.SYNC_ACCESSED_USERS)) {
            Gson gson = new Gson();
            SyncAccessedModal syncAccessedModal = gson.fromJson(response, SyncAccessedModal.class);

            studentSyncDetailsList.clear();
            studentSyncDetailsList.addAll(syncAccessedModal.getStudentAccessed());
            studentView.addStudentList(studentSyncDetailsList);
            studentView.notifyAdapter();
        }
        else if (header.equalsIgnoreCase(FC_Constants.SYNC_REGISTERED_USERS)) {
            Gson gson = new Gson();
            SyncRegisteredModal syncRegisteredModal = gson.fromJson(response, SyncRegisteredModal.class);

            studentSyncDetailsList.clear();
            studentSyncDetailsList.addAll(syncRegisteredModal.getStudentRegistered());
            studentView.addStudentList(studentSyncDetailsList);
            studentView.notifyAdapter();
        }
    }

    @Override
    public void receivedContent_PI_SubLevel(String header, String response, int pos, int size) {

    }

    @Override
    public void receivedError(String header) {
        studentView.dismissLoadingDialog();
        studentView.showToast("Server Error");
    }
}