package com.pratham.foundation.ui.app_home.profile_new.students_synced_data.data_synced_by_student;

import android.content.Context;

import com.google.gson.Gson;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.modalclasses.StudentSyncUsageModal;
import com.pratham.foundation.modalclasses.SyncStudentUsageDetailsModal;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

@EBean
public class DataSyncedPresenter implements DataSyncedContract.DataSyncedPresenter, API_Content_Result {

    DataSyncedContract.DataSyncedView syncedView;

    Context mContext;
    List<StudentSyncUsageModal> syncDetailsList;
    public DataSyncedPresenter(Context mContext) {
        this.mContext = mContext;
    }
    API_Content api_content;

    @Override
    public void setView(DataSyncedContract.DataSyncedView studentView) {
        this.syncedView = studentView;
        api_content = new API_Content(mContext, DataSyncedPresenter.this);
        syncDetailsList = new ArrayList<>();
    }

    @Background
    @Override
    public void getFinalList(String startDate, String endDate, String Eid){
        api_content.getSyncedDetails(FC_Constants.SYNC_STUDENTS_DETAILS,
                FC_Constants.SYNC_STUDENTS_DETAILS_API, startDate,endDate,Eid);
 /*       syncDetailsList =new ArrayList<>();
        try {
            for(int i = 0 ; i< SyncedStudentFragment.studentUsageSyncDetailsList.size(); i++){
                if(SyncedStudentFragment.studentUsageSyncDetailsList.get(i).getEId()!=null
                        && SyncedStudentFragment.studentUsageSyncDetailsList.get(i).getEId().equalsIgnoreCase(Eid))
                    syncDetailsList.add(SyncedStudentFragment.studentUsageSyncDetailsList.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        syncedView.setJsonData(syncDetailsList);
        syncedView.notifyAdapter();*/
    }

    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(FC_Constants.SYNC_STUDENTS_DETAILS)) {
            Gson gson = new Gson();
            SyncStudentUsageDetailsModal detailsModal = gson.fromJson(response, SyncStudentUsageDetailsModal.class);

            syncDetailsList.clear();
            syncDetailsList.addAll(detailsModal.getStudentDetails());
            syncedView.addStudentList(syncDetailsList);
            syncedView.notifyAdapter();
        }
    }

    @Override
    public void receivedContent_PI_SubLevel(String header, String response, int pos, int size) {
    }

    @Override
    public void receivedError(String header) {
        syncedView.showToast("Server Error");
        syncedView.dismissLoadingDialog();
    }
}