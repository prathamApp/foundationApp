package com.pratham.foundation.ui.app_home.profile_new.temp_sync;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.async.ZipDownloader;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.modalclasses.SyncLog;
import com.pratham.foundation.modalclasses.SyncStatusLog;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.List;

@EBean
public class SyncResultPresenter implements SyncResultContract.SyncResultPresenter, API_Content_Result {

    public Context context;
    public SyncResultContract.SyncResultView syncResultView;
    public Gson gson;
    int itemPosition = 0;
    public API_Content api_content;
    public List<SyncLog> syncLogList;

    @Bean(ZipDownloader.class)
    ZipDownloader zipDownloader;

    public SyncResultPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(SyncResultContract.SyncResultView syncResultView) {
        this.syncResultView = syncResultView;
        gson = new Gson();
        api_content = new API_Content(context, SyncResultPresenter.this);
    }

    @Override
    @Background
    public void getSyncLogs(){
        syncLogList = AppDatabase.getDatabaseInstance(context).getSyncLogDao().getAllSyncLogs();
        syncResultView.notifyAdapter(syncLogList);
    }

    @Override
    @Background
    public void syncItemData(String api, String pushId){
        api_content.syncPushedItemDB(FC_Constants.SYNC_LBL, api, pushId);
    }

    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(FC_Constants.SYNC_LBL)) {
            try {
                Gson gson = new Gson();
                SyncStatusLog statusLog = gson.fromJson(response, SyncStatusLog.class);
                Log.d("PushData", "DATA PUSH SUCCESS");
                syncResultView.ShowSyncResponse(statusLog, response);

            } catch (Exception e){
                syncResultView.dismissLoadingDialog();
                syncResultView.showToast("Server Error");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void receivedContent_PI_SubLevel(String header, String response, int pos, int size) {

    }

    @Override
    public void receivedError(String header) {

    }
}