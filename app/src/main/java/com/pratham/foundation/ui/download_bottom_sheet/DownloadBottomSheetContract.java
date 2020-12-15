package com.pratham.foundation.ui.download_bottom_sheet;

public interface DownloadBottomSheetContract {

    interface DownloadBottomSheetView {
        void notifyStudentAdapter();
    }

    interface DownloadBottomSheetPresenter {
        void setView(DownloadBottomSheetContract.DownloadBottomSheetView viewBottomStudents);
    }

}
