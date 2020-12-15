package com.pratham.foundation.ui.download_bottom_sheet;

import android.content.Context;

import com.google.gson.Gson;

import org.androidannotations.annotations.EBean;

@EBean
public class DownloadBottomSheetPresenter implements DownloadBottomSheetContract.DownloadBottomSheetPresenter {

    private DownloadBottomSheetContract.DownloadBottomSheetView myView;
    private Context context;
    Gson gson;

    public DownloadBottomSheetPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(DownloadBottomSheetContract.DownloadBottomSheetView viewBottomStudents) {
        this.myView = viewBottomStudents;
        gson = new Gson();
    }
}

