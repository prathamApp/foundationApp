package com.pratham.foundation.ui.app_home.profile_new.show_sync_log;

import android.content.Context;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.interfaces.API_Content_Result;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.List;

@EBean
public class ShowSyncLogPresenter implements ShowSyncLogContract.ShowSyncLogPresenter , API_Content_Result {

    Context mContext;
    ShowSyncLogContract.ShowSyncLogView showSyncLogView;

    public ShowSyncLogPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(ShowSyncLogContract.ShowSyncLogView showSyncLogView) {
        this.showSyncLogView = showSyncLogView;
    }

    @Background
    @Override
    public void getLogsData() {
        List<Modal_Log> logList;
        logList = AppDatabase.getDatabaseInstance(mContext).getLogsDao().getSyncedLogs();

        if(logList!=null && logList.size()>0)
            showSyncLogView.addToAdapter(logList);
        else
            showSyncLogView.showNoData();
    }

    @Override
    public void receivedContent_PI_SubLevel(String header, String response, int pos, int size) {
    }

    @Override
    public void receivedContent(String header, String response) {
    }

    @Override
    public void receivedError(String header) {
    }
}