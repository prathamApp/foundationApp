package com.pratham.foundation.ui.admin_panel.fragment_admin_panel.PushOrAssign;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.async.DownloadData;
import com.pratham.foundation.async.PushDataToServer;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.dao.StatusDao;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Status;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.services.AppExitService;
import com.pratham.foundation.services.LocationService;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.admin_panel.assign_groups.Activity_AssignGroups_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.pratham.foundation.database.AppDatabase.appDatabase;


@EFragment(R.layout.fragment_push_or_assign)
public class PushOrAssignFragment extends Fragment {

    @ViewById(R.id.btn_assign)
    Button assign;
    @ViewById(R.id.btn_push)
    Button push;
    @ViewById(R.id.btn_progress)
    Button btn_progress;
    Gson gson;
    int groupsSize = 0;
    public Dialog myLoadingDialog;
    ArrayList<String> present_groups;
    Dialog progress;

    @Bean(PushDataToServer.class)
    PushDataToServer pushDataToServer;

    public PushOrAssignFragment() {
        // Required empty public constructor
    }

    @AfterViews
    public void initialize() {
        gson = new Gson();
    }

    @Click(R.id.btn_assign)
    public void onAssignClick() {
        Intent intent = new Intent(getActivity(), Activity_AssignGroups_.class);
        startActivityForResult(intent, 1);
        getActivity().startActivity(intent);
    }

    // Delete Groups with Students
    private void deleteGroupsWithStudents() {
        List<Groups> deletedGroupsList = AppDatabase.getDatabaseInstance(getActivity()).getGroupsDao().GetAllDeletedGroups();
        for (int i = 0; i < deletedGroupsList.size(); i++) {
            AppDatabase.getDatabaseInstance(getActivity()).getStudentDao().deleteDeletedGrpsStdRecords(deletedGroupsList.get(i).GroupId);
            AppDatabase.getDatabaseInstance(getActivity()).getGroupsDao().deleteGroupByGrpID(deletedGroupsList.get(i).GroupId);
        }
        AppDatabase.getDatabaseInstance(getActivity()).getStudentDao().deleteDeletedStdRecords();
    }

    @Click(R.id.btn_push)
    public void onPushClick() {
        pushDataToServer.doInBackground(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    ProgressLayout progressLayout;
    ProgressBar roundProgress;
    TextView dialog_file_name;
    ImageView iv_file_trans;

    @Click(R.id.btn_download_all_data)
    public void onBtnDownload() {
   /*     Intent intent = new Intent(getActivity(), PushDataActivity.class);
        startActivityForResult(intent, 1);
        getActivity().startActivity(intent);*/
        if (ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_KOLIBRI_HOTSPOT)) {
                setProgressDailog();
                new DownloadData().doInBackground();
            } else {
                Toast.makeText(getActivity(), "Connect to Kolibri", Toast.LENGTH_SHORT).show();
                //callOnlineContentAPI(contentList, parentId);
            }
        }
    }

    private PowerManager.WakeLock wl;

    @SuppressLint("InvalidWakeLockTag")
    private void setProgressDailog() {
        progress = new Dialog(getActivity());
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setContentView(R.layout.dialog_file_downloading);
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
//        PowerManager pm = (PowerManager) ApplicationClass.getInstance().getSystemService(ApplicationClass.getInstance().POWER_SERVICE);
//        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNjfdhotDimScreen");
        progressLayout = progress.findViewById(R.id.dialog_progressLayout);
        roundProgress = progress.findViewById(R.id.dialog_roundProgress);
        dialog_file_name = progress.findViewById(R.id.dialog_file_name);
        iv_file_trans = progress.findViewById(R.id.iv_file_trans);
        Glide.with(this).load(R.drawable.splash_group).into(iv_file_trans);
        dialog_file_name.setText("Downloading please wait");
        progressLayout.setCurProgress(0);
        progress.show();
    }

    @Subscribe
    public void messageRecieved(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(FC_Constants.DATA_FILE_PROGRESS)) {
                if (progress != null)
                    progressLayout.setCurProgress((int) message.getProgress());
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.UNZIPPING_DATA_FILE)) {
                if (progress != null) {
                    dialog_file_name.setText("Unzipping please wait..");
                    roundProgress.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                }
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.DATA_ZIP_COMPLETE)) {
//                wl.release();
                Log.d("pushorassign", "Zipping Completed.. ");
                if (progress != null)
                    dialog_file_name.setText("Zipping Completed..");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog_file_name.setText("Loading Data..");
                        Log.d("pushorassign", "Loading Data..");
                        populateDB();
                    }
                }, 1000);
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.DATA_DOWNLOAD_ERROR)) {
//                wl.release();
                if (progress != null)
                    dialog_file_name.setText("Download Error..");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                }, 1000);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void populateDB() {
        new AsyncTask<Void, Integer, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (!FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false))
                        doInitialEntries(appDatabase);
                    if (!FastSave.getInstance().getBoolean(FC_Constants.KEY_MENU_COPIED, false))
                        populateMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progress != null)
                            progress.dismiss();
                    }
                }, 1000);
            }
        }.execute();
    }

    public void populateMenu() {
        try {
            File folder_file, db_file;
            folder_file = new File(ApplicationClass.foundationPath + "/.FCA/English/");
            if (folder_file.exists()) {
                Log.d("-CT-", "doInBackground ApplicationClass.contentSDPath: " + ApplicationClass.contentSDPath);
                db_file = new File(folder_file + "/" + AppDatabase.DB_NAME);
//                    db_file = new File(folder_file.getAbsolutePath() + "/" + AppDatabase.DB_NAME);
                if (db_file.exists()) {
                    SQLiteDatabase db = SQLiteDatabase.openDatabase(db_file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
                    if (db != null) {
                        Cursor content_cursor;
                        try {
                            content_cursor = db.rawQuery("SELECT * FROM ContentTable", null);
                            //populate contents
                            List<ContentTable> contents = new ArrayList<>();
                            if (content_cursor.moveToFirst()) {
                                while (!content_cursor.isAfterLast()) {
                                    ContentTable detail = new ContentTable();
                                    detail.setNodeId(content_cursor.getString(content_cursor.getColumnIndex("nodeId")));
                                    detail.setNodeType(content_cursor.getString(content_cursor.getColumnIndex("nodeType")));
                                    detail.setNodeTitle(content_cursor.getString(content_cursor.getColumnIndex("nodeTitle")));
                                    detail.setNodeKeywords(content_cursor.getString(content_cursor.getColumnIndex("nodeKeywords")));
                                    detail.setNodeAge(content_cursor.getString(content_cursor.getColumnIndex("nodeAge")));
                                    detail.setNodeDesc(content_cursor.getString(content_cursor.getColumnIndex("nodeDesc")));
                                    detail.setNodeServerImage(content_cursor.getString(content_cursor.getColumnIndex("nodeServerImage")));
                                    detail.setNodeImage(content_cursor.getString(content_cursor.getColumnIndex("nodeImage")));
                                    detail.setResourceId(content_cursor.getString(content_cursor.getColumnIndex("resourceId")));
                                    detail.setResourceType(content_cursor.getString(content_cursor.getColumnIndex("resourceType")));
                                    detail.setResourcePath(content_cursor.getString(content_cursor.getColumnIndex("resourcePath")));
                                    detail.setVersion(content_cursor.getString(content_cursor.getColumnIndex("version")));
                                    detail.setLevel("" + content_cursor.getInt(content_cursor.getColumnIndex("level")));
                                    detail.setContentLanguage(content_cursor.getString(content_cursor.getColumnIndex("contentLanguage")));
                                    detail.setParentId(content_cursor.getString(content_cursor.getColumnIndex("parentId")));
                                    detail.setContentType(content_cursor.getString(content_cursor.getColumnIndex("contentType")));
                                    detail.setIsDownloaded("" + content_cursor.getString(content_cursor.getColumnIndex("isDownloaded")));
                                    detail.setOnSDCard(false);
                                    contents.add(detail);
                                    content_cursor.moveToNext();
                                }
                            }
                            appDatabase.getContentTableDao().addContentList(contents);
                            content_cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            getActivity().startService(new Intent(getActivity(), AppExitService.class));
            BackupDatabase.backup(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doInitialEntries(AppDatabase appDatabase) {
        try {
            com.pratham.foundation.database.domain.Status status;
            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("DeviceId");
            status.setValue("" + Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
            status.setDescription("" + Build.SERIAL);
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("CRLID");
            status.setValue("default");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("DeviceName");
            status.setValue(FC_Utility.getDeviceName());
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("gpsFixDuration");
            status.setValue("");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("prathamCode");
            status.setValue("");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("apkType");
            status.setValue("");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("Latitude");
            status.setValue("");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("Longitude");
            status.setValue("");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("GPSDateTime");
            status.setValue("");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("CurrentSession");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("SdCardPath");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("AppLang");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("AppStartDateTime");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            //new Entries
            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("ActivatedForGroups");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("programId");
            status.setValue("1");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("group1");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("group2");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("group3");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("group4");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("group5");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("village");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("ActivatedDate");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("AssessmentSession");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("AndroidID");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("DBVersion");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new com.pratham.foundation.database.domain.Status();
            status.setStatusKey("SerialID");
            status.setValue(FC_Utility.getDeviceSerialID());
            appDatabase.getStatusDao().insert(status);

            WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            String macAddress = wInfo.getMacAddress();
            status.setStatusKey("wifiMAC");
            status.setValue(macAddress);
            appDatabase.getStatusDao().insert(status);

            setAppName(status);
            setAppVersion(status);
            BackupDatabase.backup(getActivity());

            addStartTime();
//            getSdCardPath();
            requestLocation();

            FastSave.getInstance().saveBoolean(FC_Constants.INITIAL_ENTRIES, true);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAppName(Status status) {
        String appname = "";
        if (AppDatabase.getDatabaseInstance(getActivity()).getStatusDao().getKey("appName") == null) {
            CharSequence c = "";
            ActivityManager am = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
            List l = am.getRunningAppProcesses();
            Iterator i = l.iterator();
            PackageManager pm = getActivity().getPackageManager();
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                appname = c.toString();
                Log.w("LABEL", c.toString());
            } catch (Exception e) {
            }

            status = new Status();
            status.setStatusKey("appName");
            status.setValue(appname);
            appDatabase.getStatusDao().insert(status);

        } else {
            CharSequence c = "";
            ActivityManager am = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
            List l = am.getRunningAppProcesses();
            Iterator i = l.iterator();
            PackageManager pm = getActivity().getPackageManager();
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                appname = c.toString();
                Log.w("LABEL", c.toString());
            } catch (Exception e) {
            }
            status = new Status();
            status.setStatusKey("appName");
            status.setValue(appname);
            appDatabase.getStatusDao().insert(status);
        }
    }

    private void requestLocation() {
        new LocationService(getActivity()).checkLocation();
    }

    private void setAppVersion(Status status) {
        if (AppDatabase.getDatabaseInstance(getActivity()).getStatusDao().getKey("apkVersion") == null) {
            status = new Status();

            status.setStatusKey("apkVersion");
            PackageInfo pInfo = null;
            String verCode = "";
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                verCode = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            status.setValue(verCode);
            appDatabase.getStatusDao().insert(status);

        } else {
            status.setStatusKey("apkVersion");

            PackageInfo pInfo = null;
            String verCode = "";
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                verCode = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            status.setValue(verCode);
            appDatabase.getStatusDao().insert(status);

        }
    }

    private void addStartTime() {
        new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    String appStartTime = FC_Utility.getCurrentDateTime();
                    StatusDao statusDao = appDatabase.getStatusDao();
                    statusDao.updateValue("AppStartDateTime", appStartTime);
                    BackupDatabase.backup(getActivity());
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }.execute();
    }


    @Click(R.id.btn_progress)
    public void fetchStudentProgress() {
        checkAssignedGroups();
    }

    private void checkAssignedGroups() {
        present_groups = new ArrayList<>();
        // String groupId1 = BaseActivity.statusDao.getValue(PD_Constant.GROUPID1);
        String groupId1 = AppDatabase.getDatabaseInstance(getActivity()).getStatusDao().getValue(FC_Constants.GROUPID1);

        if (!groupId1.equalsIgnoreCase("0") && !groupId1.equalsIgnoreCase("NA"))
            present_groups.add(groupId1);
        String groupId2 = AppDatabase.getDatabaseInstance(getActivity()).getStatusDao().getValue(FC_Constants.GROUPID2);
        //       String groupId2 = BaseActivity.statusDao.getValue(PD_Constant.GROUPID2);

        if (!groupId2.equalsIgnoreCase("0") && !groupId2.equalsIgnoreCase("NA"))
            present_groups.add(groupId2);
        String groupId3 = AppDatabase.getDatabaseInstance(getActivity()).getStatusDao().getValue(FC_Constants.GROUPID3);

        //       String groupId3 = BaseActivity.statusDao.getValue(PD_Constant.GROUPID3);

        if (!groupId3.equalsIgnoreCase("0") && !groupId3.equalsIgnoreCase("NA"))
            present_groups.add(groupId3);
        String groupId4 = AppDatabase.getDatabaseInstance(getActivity()).getStatusDao().getValue(FC_Constants.GROUPID4);

//        String groupId4 = BaseActivity.statusDao.getValue(PD_Constant.GROUPID4);

        if (!groupId4.equalsIgnoreCase("0") && !groupId4.equalsIgnoreCase("NA"))
            present_groups.add(groupId4);
        String groupId5 = AppDatabase.getDatabaseInstance(getActivity()).getStatusDao().getValue(FC_Constants.GROUPID5);

        //       String groupId5 = BaseActivity.statusDao.getValue(PD_Constant.GROUPID5);

        if (!groupId5.equalsIgnoreCase("0") && !groupId5.equalsIgnoreCase("NA"))
            present_groups.add(groupId5);

        if (present_groups.size() > 0) {
            showLoader();
            groupsSize = present_groups.size() - 1;
            for (int i = 0; i < present_groups.size(); i++) {
                getStudentData(FC_Constants.STUDENT_PROGRESS_INTERNET, FC_Constants.STUDENT_PROGRESS_API, present_groups.get(i), i);
            }
        } else {
            Toast.makeText(getActivity(), "ASIGN GROUPS FIRST", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoader() {
        myLoadingDialog = new Dialog(getActivity());
        myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myLoadingDialog.setContentView(R.layout.loading_dialog);
        myLoadingDialog.setCanceledOnTouchOutside(false);
        myLoadingDialog.show();
    }

    private void getStudentData(final String requestType, String url, String studentID, int pos) {
        try {
            String url_id;
            url_id = url + studentID;
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            receivedContent(requestType, response, pos);
                        }

                        @Override
                        public void onError(ANError anError) {
                            try {
                                Log.d("Error:", anError.getErrorDetail());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            receivedError();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receivedError() {
    }

    @SuppressLint("StaticFieldLeak")
    private void receivedContent(String requestType, String response, int pos) {
        if (requestType.equalsIgnoreCase(FC_Constants.STUDENT_PROGRESS_INTERNET)) {
            new AsyncTask<Object, Void, Object>() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        Type listType = new TypeToken<ArrayList<ContentProgress>>() {
                        }.getType();
                        List<ContentProgress> contentProgressList = gson.fromJson(response, listType);
                        if (contentProgressList != null && contentProgressList.size() > 0) {
                            for (int i = 0; i < contentProgressList.size(); i++) {
                                contentProgressList.get(i).setSentFlag(1);
                                contentProgressList.get(i).setLabel("" + FC_Constants.RESOURCE_PROGRESS);
                            }
                            appDatabase.getContentProgressDao().addContentProgressList(contentProgressList);
                            BackupDatabase.backup(getActivity());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    if (pos == groupsSize)
                        getNextData();
                }
            }.execute();
        } else if (requestType.equalsIgnoreCase(FC_Constants.LEARNT_WORDS_INTERNET)) {
            new AsyncTask<Object, Void, Object>() {
                @Override
                protected Object doInBackground(Object[] objects) {

                    try {
                        Type listType = new TypeToken<ArrayList<KeyWords>>() {
                        }.getType();
                        List<KeyWords> learntWordsList = gson.fromJson(response, listType);
                        if (learntWordsList != null && learntWordsList.size() > 0) {
                            for (int i = 0; i < learntWordsList.size(); i++) {
                                learntWordsList.get(i).setSentFlag(1);
                                learntWordsList.get(i).setKeyWord("" + learntWordsList.get(i).getKeyWord().toLowerCase());
                                if (!checkWord(learntWordsList.get(i).getStudentId(), learntWordsList.get(i).getResourceId(), learntWordsList.get(i).getKeyWord(), learntWordsList.get(i).getWordType()))
                                    appDatabase.getKeyWordDao().insert(learntWordsList.get(i));
                            }
                            BackupDatabase.backup(getActivity());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    if (pos == groupsSize)
                        dismissLoader();
                }
            }.execute();
        }
    }

    private boolean checkWord(String studentId, String wordUUId, String wordCheck, String wordType) {
        try {
            String word = appDatabase.getKeyWordDao().checkLearntData(studentId, "" + wordUUId, wordCheck.toLowerCase(), wordType);
            if (word != null)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void getNextData() {
        if (present_groups.size() > 0) {
            groupsSize = present_groups.size() - 1;
            for (int i = 0; i < present_groups.size(); i++)
                getStudentData(FC_Constants.LEARNT_WORDS_INTERNET, FC_Constants.LEARNT_WORDS_API, present_groups.get(i), i);
        }
    }

    private void dismissLoader() {
        if (myLoadingDialog != null) {
            myLoadingDialog.dismiss();
        }
    }

}
