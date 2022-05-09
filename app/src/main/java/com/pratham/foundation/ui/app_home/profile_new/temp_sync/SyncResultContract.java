package com.pratham.foundation.ui.app_home.profile_new.temp_sync;


import com.pratham.foundation.modalclasses.SyncLog;
import com.pratham.foundation.modalclasses.SyncStatusLog;

import java.util.List;

public interface SyncResultContract {

    interface SyncResultItemClick{
        void checkDetails(int pos, SyncLog syncLog);
        void syncData(int pos, SyncLog syncLog);
    }

    interface SyncResultView{

        void notifyAdapter(List<SyncLog> syncLogList);

        void updateItemChanges(int itemPos);

        void ShowSyncResponse(SyncStatusLog statusLog, String response);

        void dismissLoadingDialog();

        void showToast(String msg);

    }

    interface SyncResultPresenter {

        void setView(SyncResultView syncResultView);

        void getSyncLogs();

        void syncItemData(String api, String selectedItem);

    }

}
