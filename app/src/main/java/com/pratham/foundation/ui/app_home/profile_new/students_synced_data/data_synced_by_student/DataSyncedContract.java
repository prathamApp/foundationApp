package com.pratham.foundation.ui.app_home.profile_new.students_synced_data.data_synced_by_student;

import com.pratham.foundation.modalclasses.StudentSyncUsageModal;

import java.util.List;

public interface DataSyncedContract {

    interface DataSyncedView {

        void notifyAdapter();

        void showNoData();

        void addStudentList(List<StudentSyncUsageModal> syncDetailsList);

        void dismissLoadingDialog();

        void showToast(String msg);

        void showLoader();
    }

    interface DataSyncedPresenter {

        void setView(DataSyncedView dataSyncedView);

        void getFinalList(String startDate, String endDate, String eId);

    }
}
