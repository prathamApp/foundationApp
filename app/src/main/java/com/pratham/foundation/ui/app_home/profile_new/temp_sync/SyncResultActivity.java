package com.pratham.foundation.ui.app_home.profile_new.temp_sync;


import static com.pratham.foundation.utility.FC_Utility.dpToPx;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.modalclasses.ApiResultClass;
import com.pratham.foundation.modalclasses.SyncLog;
import com.pratham.foundation.modalclasses.SyncStatusLog;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EActivity(R.layout.activity_sync_result)
public class SyncResultActivity extends BaseActivity implements SyncResultContract.SyncResultView,
        SyncResultContract.SyncResultItemClick {

    @Bean(SyncResultPresenter.class)
    SyncResultContract.SyncResultPresenter presenter;

    public List<SyncLog> syncLogList;
    /*    @ViewById(R.id.sync_list)
        ListView sync_list;*/
    @ViewById(R.id.recycler_view)
    RecyclerView recycler_view;

    public static String selectedItem;
    public SyncAdapter syncAdapter;
    public int position = 0;

//    result_list_item

    @AfterViews
    public void initiate() {

        presenter.setView(SyncResultActivity.this);
        syncLogList = new ArrayList<>();
        presenter.getSyncLogs();
/*        List<String> resList = AppDatabase.getDatabaseInstance(this).getLogsDao().getSyncedStatusLogs();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resList);
        sync_list.setAdapter(arrayAdapter);
        sync_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = (String) parent.getItemAtPosition(position);
                showLoader();
                getStatus(selectedItem);
            }
        });*/
    }

    @UiThread
    @Click(R.id.main_back)
    public void backButtonPressed() {
        finish();
    }


    @UiThread
    @Override
    public void notifyAdapter(List<SyncLog> syncLogLists) {
        syncLogList.clear();
        syncLogList.addAll(syncLogLists);
        if (syncAdapter == null) {
            try {
                syncAdapter = new SyncAdapter(this, syncLogList, this);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
                recycler_view.setLayoutManager(mLayoutManager);
                recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(this), true));
                recycler_view.setItemAnimator(new DefaultItemAnimator());
                recycler_view.setAdapter(syncAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            syncAdapter.notifyDataSetChanged();
        }
    }

    @UiThread
    @Override
    public void updateItemChanges(int itemPos) {
        syncAdapter.notifyItemChanged(itemPos, syncLogList.get(itemPos));
    }

    @Override
    public void checkDetails(int pos, SyncLog syncLog) {
        showLoader();
        position = pos;
        getStatus(FC_Constants.TEST_RESULT_API, "" + syncLog.getPushId());
    }

    @Override
    public void syncData(int pos, SyncLog syncLog) {
        showLoader();
        position = pos;
        presenter.syncItemData(FC_Constants.TEST_SYNC_API, "" + syncLog.getPushId());
    }

    @UiThread
    public void getStatus(String api, String selectedItem) {
        try {
            AndroidNetworking.get(api + selectedItem)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("PushData", "DATA PUSH " + response);
                            Gson gson = new Gson();
                            ApiResultClass pushResponse = gson.fromJson(response, ApiResultClass.class);

                            Log.d("PushData", "DATA PUSH SUCCESS");
                            ShowResponse(pushResponse, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("PushData", "Data push FAIL");
                            Log.d("PushData", "ERROR  " + anError);
                            setDataPushFailed();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CustomLodingDialog sttDialog;

    @UiThread
    public void ShowResponse(ApiResultClass pushResponse, String response) {
        try {
            dismissLoadingDialog();
            sttDialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
            sttDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(sttDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            sttDialog.setContentView(R.layout.sync_new_test_dialog);
            sttDialog.setCanceledOnTouchOutside(true);

            TextView txt_date = sttDialog.findViewById(R.id.txt_date);
            TextView dia_title = sttDialog.findViewById(R.id.dia_title);
            Button dia_btn_ok = sttDialog.findViewById(R.id.dia_btn_ok);

            txt_date.setText("Today's date : " + FC_Utility.getCurrentDateTime());

            String err = "";
            if (pushResponse.getIsError() != 0)
                err = "\n\nError : " + pushResponse.getErrorMsg();


//        dia_title.setText(Html.fromHtml("<b>PushID : </b>" + pushResponse.getPushId() +
            dia_title.setText("PushID : " + pushResponse.getPushId()
                    + "\n\n" + "SyncId : " + pushResponse.getSyncId()
                    + "\n\n" + "UUID : " + pushResponse.getUuid()
                    + "\n\n" + "Push Date : " + pushResponse.getDatePushed()
                    + "\n\n" + "Push Status : " + pushResponse.getPushStatus()
                    + "\n\n" + "Device ID : " + pushResponse.getDeviceId()
                    + "\n\n" + "Score Pushed : " + pushResponse.getScoreCount()
                    + "\n\n" + "Score Synced : " + pushResponse.getScorePushed()
                    + "\n\n" + "Attendance Pushed : " + pushResponse.getAttendanceCount()
                    + "\n\n" + "Attendance Synced : " + pushResponse.getAttendancePushed()
                    + "\n\n" + "LastChecked : " + pushResponse.getLastCheckedByApp()
                    + err);

            SyncStatusLog statusLog = new SyncStatusLog();
            statusLog.setPushId(Integer.parseInt(pushResponse.getPushId()));
            statusLog.setSyncId(pushResponse.getSyncId());
            statusLog.setUuid(pushResponse.getUuid());
            statusLog.setPushDate(pushResponse.getDatePushed());
            statusLog.setPushStatus(pushResponse.getPushStatus());
            statusLog.setDeviceId(pushResponse.getDeviceId());
            statusLog.setScorePushed(Integer.parseInt(pushResponse.getScorePushed()));
            statusLog.setScoreSynced(Integer.parseInt(pushResponse.getScoreCount()));
            statusLog.setLastChecked(pushResponse.getLastCheckedByApp());
            statusLog.setError(pushResponse.getErrorMsg());
            statusLog.setSentFlag(0);
            AppDatabase.getDatabaseInstance(this).getSyncStatusLogDao().insert(statusLog);
            AppDatabase.getDatabaseInstance(this).getSyncLogDao().updateStatus(Integer.parseInt(pushResponse.getPushId()),
                    pushResponse.getUuid(), pushResponse.getPushStatus());

            dia_btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Handler().postDelayed(() -> {
                        sttDialog.dismiss();
                    }, 100);

                }
            });
            sttDialog.show();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    CustomLodingDialog syncDialog;

    @UiThread
    public void ShowSyncResponse(SyncStatusLog statusLog, String response) {
        dismissLoadingDialog();
        syncDialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
        syncDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(syncDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        syncDialog.setContentView(R.layout.sync_new_test_dialog);
        syncDialog.setCanceledOnTouchOutside(false);
        syncDialog.setCancelable(false);

        AppDatabase.getDatabaseInstance(this).getSyncLogDao().updateStatus(statusLog.getPushId(),
                statusLog.getUuid(), statusLog.getPushStatus());
        AppDatabase.getDatabaseInstance(this).getSyncStatusLogDao().insert(statusLog);

        syncLogList.get(position).setStatus(statusLog.getPushStatus());
        TextView txt_date = syncDialog.findViewById(R.id.txt_date);
        TextView dia_title = syncDialog.findViewById(R.id.dia_title);
        Button dia_btn_ok = syncDialog.findViewById(R.id.dia_btn_ok);
        ScrollView scrollView = syncDialog.findViewById(R.id.scrollView);

        txt_date.setText("Today's date : " + FC_Utility.getCurrentDateTime());

        scrollView.setScrollbarFadingEnabled(false);

        String TotalError = "", ScoreErr = "", AttendanceErr = "", StudentErr = "", SessionErr = "",
                cpErr = "", logsErr = "", KeywordsErr = "", CourseEnrollmentErr = "", GroupesErr = "";

        if (!statusLog.getError().equalsIgnoreCase("") && !statusLog.getError().equalsIgnoreCase(" "))
            TotalError = "<br><br><font color='#FA1E1E'>Error : " + statusLog.getError() + "</font>";
        if (statusLog.getScoreError() != 0)
            ScoreErr = "<br><font color='#FA1E1E'>Score Error : " + statusLog.getScoreError() + "</font>";
        if (statusLog.getAttendanceError() != 0)
            AttendanceErr = "<br><font color='#FA1E1E'>Attendance Error : " + statusLog.getAttendanceError() + "</font>";
        if (statusLog.getStudentError() != 0)
            StudentErr = "<br><font color='#FA1E1E'>Student Error : " + statusLog.getStudentError() + "</font>";
        if (statusLog.getSessionError() != 0)
            SessionErr = "<br><font color='#FA1E1E'>Session Error : " + statusLog.getSessionError() + "</font>";
        if (statusLog.getCpError() != 0)
            cpErr = "<br><font color='#FA1E1E'>Content Progress Error : " + statusLog.getCpError() + "</font>";
        if (statusLog.getLogsError() != 0)
            logsErr = "<br><font color='#FA1E1E'>Logs Error : " + statusLog.getLogsError() + "</font>";
        if (statusLog.getKeywordsError() != 0)
            KeywordsErr = "<br><font color='#FA1E1E'>Keywords Error : " + statusLog.getKeywordsError() + "</font>";
        if (statusLog.getCourseEnrollmentError() != 0)
            CourseEnrollmentErr = "<br><font color='#FA1E1E'>Course Enrollment Error : " + statusLog.getCourseEnrollmentError() + "</font>";
        if (statusLog.getGroupsDataError() != 0)
            GroupesErr = "<br><font color='#FA1E1E'>Groups Error : " + statusLog.getGroupsDataError() + "</font>";

        String syncStatus = "";
        if (statusLog.getPushStatus().equalsIgnoreCase("completed"))
            syncStatus = "<font color='#05AF81'><b>" + statusLog.getPushStatus() + "</b></font>";
        else
            syncStatus = "<font color='#8e24aa'>" + statusLog.getPushStatus() + "</font>";

//        dia_title.setText(Html.fromHtml("<b>PushID : </b>" + statusLog.getPushId() +
        dia_title.setText(Html.fromHtml("PushID : " + statusLog.getPushId()
                + "<br>" + "SyncId : " + statusLog.getSyncId()
                + "<br>" + "UUID : " + statusLog.getUuid()
                + "<br>" + "Push Date : " + statusLog.getPushDate()
                + "<br>" + "Push Status : " + syncStatus
                + "<br>" + "Device ID : " + statusLog.getDeviceId()
                + "<br>" + "App Version : v" + FastSave.getInstance().getString(FC_Constants.APP_VERSION, "")
                + "<br><br><font color='#35469E'>" + "Score Pushed : " + statusLog.getScoreSynced() + "</font>"
                + "<br><font color='#05AF81'>" + "Score Synced : " + statusLog.getScorePushed() + "</font>"
                + ScoreErr + ""
                + "<br><br><font color='#35469E'>" + "Attendance Pushed : " + statusLog.getAttendancePushed() + "</font>"
                + "<br><font color='#05AF81'>" + "Attendance Synced : " + statusLog.getAttendanceSynced() + "</font>"
                + AttendanceErr + ""
                + "<br><br><font color='#35469E'>" + "Students Pushed : " + statusLog.getStudentPushed() + "</font>"
                + "<br><font color='#05AF81'>" + "Students Synced : " + statusLog.getStudentSynced() + "</font>"
                + StudentErr + ""
                + "<br><br><font color='#35469E'>" + "Sessions Pushed : " + statusLog.getSessionCount() + "</font>"
                + "<br><font color='#05AF81'>" + "Sessions Synced : " + statusLog.getSessionSynced() + "</font>"
                + SessionErr + ""
                + "<br><br><font color='#35469E'>" + "Content Progress Pushed : " + statusLog.getCpCount() + "</font>"
                + "<br><font color='#05AF81'>" + "Content Progress Synced : " + statusLog.getCpSynced() + "</font>"
                + cpErr + ""
                + "<br><br><font color='#35469E'>" + "Logs Pushed : " + statusLog.getLogsCount() + "</font>"
                + "<br><font color='#05AF81'>" + "Logs Synced : " + statusLog.getLogsSynced() + "</font>"
                + logsErr + ""
                + "<br><br><font color='#35469E'>" + "Keywords Pushed : " + statusLog.getKeywordsCount() + "</font>"
                + "<br><font color='#05AF81'>" + "Keywords Synced : " + statusLog.getKeywordsSynced() + "</font>"
                + KeywordsErr + ""
                + "<br><br><font color='#35469E'>" + "Course Enrollment Pushed : " + statusLog.getCourseEnrollmentCount() + "</font>"
                + "<br><font color='#05AF81'>" + "Course Enrollment Synced : " + statusLog.getCourseEnrollmentSynced() + "</font>"
                + CourseEnrollmentErr + ""
                + "<br><br><font color='#35469E'>" + "Groups Pushed : " + statusLog.getGroupsDataCount() + "</font>"
                + "<br><font color='#05AF81'>" + "Groups Synced : " + statusLog.getGroupsDataSynced() + "</font>"
                + "<br>" + GroupesErr
                + "<br><br><font color='#FFC700C7'>" + "LastChecked : " + statusLog.getLastChecked() + "</font>"
                + TotalError + "<br>"));

        statusLog.setSentFlag(0);
//        AppDatabase.getDatabaseInstance(this).getSyncLogDao().updateStatus(statusLog.getPushId(),
//                statusLog.getUuid(), statusLog.getPushStatus());

        dia_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(() -> {
                    syncDialog.dismiss();
                    updateItemChanges(position);
                }, 100);

            }
        });
        syncDialog.show();
    }

    public void setDataPushSuccessfull(ApiResultClass pushResponse) {

    }

    public void setDataPushFailed() {
        dismissLoadingDialog();
    }

    private boolean loaderVisible = false;
    private CustomLodingDialog myLoadingDialog;

    @UiThread
    public void showLoader() {
        if (!loaderVisible) {
            loaderVisible = true;
            myLoadingDialog = new CustomLodingDialog(this);
            myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(myLoadingDialog.getWindow()).
                    setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myLoadingDialog.setContentView(R.layout.loading_dialog);
            myLoadingDialog.setCanceledOnTouchOutside(false);
//        myLoadingDialog.setCancelable(false);
            myLoadingDialog.show();
            loaderVisible = true;
        }
    }

    @UiThread
    @Override
    public void showToast(String msg) {
        Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();
    }

    @UiThread
    @Override
    public void dismissLoadingDialog() {
        try {
            if (loaderVisible) {
                loaderVisible = false;
                new Handler().postDelayed(() -> {
                    if (myLoadingDialog != null && myLoadingDialog.isShowing())
                        myLoadingDialog.dismiss();
                }, 150);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
