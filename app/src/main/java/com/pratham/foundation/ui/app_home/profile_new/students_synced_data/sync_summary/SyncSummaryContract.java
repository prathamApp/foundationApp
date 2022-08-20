package com.pratham.foundation.ui.app_home.profile_new.students_synced_data.sync_summary;

import com.pratham.foundation.modalclasses.SyncSummaryModal;

public interface SyncSummaryContract {

    interface SyncSummaryView {

        void showNoData();

        void addSummary(SyncSummaryModal syncDetails);

        void dismissLoadingDialog();

        void showLoader();

        void showToast(String msg);
    }

    interface SyncSummaryPresenter {

        void setView(SyncSummaryView SyncSummaryView);

        void getDataFromServer(String startDate, String endDate);

    }
}
