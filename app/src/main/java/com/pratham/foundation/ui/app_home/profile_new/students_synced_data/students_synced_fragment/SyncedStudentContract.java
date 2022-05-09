package com.pratham.foundation.ui.app_home.profile_new.students_synced_data.students_synced_fragment;

import com.pratham.foundation.modalclasses.StudentSyncDetails;

import java.util.List;

public interface SyncedStudentContract {

    interface StudentItemClicked {
        void showDetails(StudentSyncDetails studentItem, int pos);
    }

    interface SyncedStudentView {

        void notifyAdapter();

        void showNoData();

        void addStudentList(List<StudentSyncDetails> studentSyncDetailsList);

        void dismissLoadingDialog();

        void showLoader();

        void showToast(String msg);
    }

    interface SyncedStudentPresenter {

        void setView(SyncedStudentView syncedStudentView);

        void getDataFromServer(String syncType, String startDate, String endDate);
    }
}
