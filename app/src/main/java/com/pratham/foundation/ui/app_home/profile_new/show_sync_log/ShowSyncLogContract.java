package com.pratham.foundation.ui.app_home.profile_new.show_sync_log;

import com.pratham.foundation.database.domain.Modal_Log;

import java.util.List;

public interface ShowSyncLogContract {

    interface ShowSyncLogView {
        void addToAdapter(List<Modal_Log> logList);
        void showNoData();
    }

    interface ShowSyncLogPresenter {
        void setView(ShowSyncLogContract.ShowSyncLogView ShowSyncLogView);

        void getLogsData();
    }
}
