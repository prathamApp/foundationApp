package com.pratham.foundation.ui.app_home.profile_new.students_synced_data.sync_summary;

import android.content.Context;

import com.google.gson.Gson;
import com.pratham.foundation.R;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.modalclasses.SyncSummaryModal;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.EBean;

@EBean
public class SyncSummaryPresenter implements SyncSummaryContract.SyncSummaryPresenter, API_Content_Result {

    SyncSummaryContract.SyncSummaryView studentView;

    Context mContext;
    API_Content api_content;

    public SyncSummaryPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(SyncSummaryContract.SyncSummaryView studentView) {
        this.studentView = studentView;
        api_content = new API_Content(mContext, SyncSummaryPresenter.this);
    }

    @Override
    public void getDataFromServer(String startDate, String endDate) {
        api_content.getSyncedSummary(FC_Constants.SYNC_SUMMARY_DATA,
                FC_Constants.SYNC_SUMMARY_DATA_API, startDate,endDate);
    }

    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(FC_Constants.SYNC_SUMMARY_DATA)) {
            Gson gson = new Gson();
            SyncSummaryModal syncDetails = gson.fromJson(response, SyncSummaryModal.class);
            studentView.addSummary(syncDetails);
        }
    }

    @Override
    public void receivedContent_PI_SubLevel(String header, String response, int pos, int size) { }

    @Override
    public void receivedError(String header) {
        if (header.equalsIgnoreCase(FC_Constants.SYNC_SUMMARY_DATA)) {
            studentView.dismissLoadingDialog();
            studentView.showToast(""+ mContext.getResources().getString(R.string.server_error));
        }
    }
}