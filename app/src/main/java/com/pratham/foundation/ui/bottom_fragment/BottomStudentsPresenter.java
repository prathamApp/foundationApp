package com.pratham.foundation.ui.bottom_fragment;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.dao.StatusDao;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.Attendance;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.database.domain.Session;
import com.pratham.foundation.database.domain.Status;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.services.AppExitService;
import com.pratham.foundation.services.LocationService;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.KeywordsIdentificationContract;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.QuestionModel;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.utility.FC_Constants.currentStudentID;

@EBean
public class BottomStudentsPresenter implements BottomStudentsContract.BottomStudentsPresenter {

    private BottomStudentsContract.BottomStudentsView myView;
    private Context context;
    Gson gson;

    public BottomStudentsPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(BottomStudentsContract.BottomStudentsView viewBottomStudents) {
        this.myView = viewBottomStudents;
        gson = new Gson();
        studentList = new ArrayList<>();
        studentDBList = new ArrayList<>();
    }

    private List<Student> studentDBList, studentList;
    @Background
    @Override
    public void showStudents() {
        try {
            studentList.clear();
            studentDBList = AppDatabase.appDatabase.getStudentDao().getAllStudents();
            if (studentDBList != null) {
                for (int i = 0; i < studentDBList.size(); i++) {
                    Student studentAvatar = new Student();
                    studentAvatar.setStudentID(studentDBList.get(i).getStudentID());
                    studentAvatar.setFullName(studentDBList.get(i).getFullName());
                    studentAvatar.setAvatarName(studentDBList.get(i).getAvatarName());
                    studentList.add(studentAvatar);
                }
            }
            BackupDatabase.backup(context);
            myView.setStudentList(studentList);
            myView.notifyStudentAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void updateStudentData() {
        AppDatabase.appDatabase.getStatusDao().updateValue("CurrentSession", "" + FC_Constants.currentSession);
        Attendance attendance = new Attendance();
        attendance.setSessionID(FC_Constants.currentSession);
        attendance.setStudentID(currentStudentID);
        attendance.setDate(FC_Utility.getCurrentDateTime());
        attendance.setGroupID("SP");
        attendance.setSentFlag(0);
        AppDatabase.appDatabase.getAttendanceDao().insert(attendance);

        Session startSesion = new Session();
        startSesion.setSessionID("" + FC_Constants.currentSession);
        startSesion.setFromDate("" + FC_Utility.getCurrentDateTime());
        startSesion.setToDate("NA");
        startSesion.setSentFlag(0);
        AppDatabase.appDatabase.getSessionDao().insert(startSesion);

        myView.gotoNext();
        if (FC_Utility.isDataConnectionAvailable(context))
            getStudentData(FC_Constants.STUDENT_PROGRESS_INTERNET, FC_Constants.STUDENT_PROGRESS_API, currentStudentID);

    }

    private void getStudentData(String requestType, String url, String studentID) {
        try {
            String url_id;
            url_id = url + studentID;
            AndroidNetworking.get(url_id)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            receivedContent(requestType, response);
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
//        startActivity(new Intent(getActivity(), HomeActivity.class));
//        getActivity().finish();
    }

    @SuppressLint("StaticFieldLeak")
    private void receivedContent(String requestType, String response) {
        if (requestType.equalsIgnoreCase(FC_Constants.STUDENT_PROGRESS_INTERNET)) {
//            new AsyncTask<Object, Void, Object>() {
//                @Override
//                protected Object doInBackground(Object[] objects) {
            try {
                Type listType = new TypeToken<ArrayList<ContentProgress>>() {
                }.getType();
                List<ContentProgress> contentProgressList = gson.fromJson(response, listType);
                if (contentProgressList != null && contentProgressList.size() > 0) {
                    for (int i = 0; i < contentProgressList.size(); i++) {
                        contentProgressList.get(i).setSentFlag(1);
                        contentProgressList.get(i).setLabel("" + FC_Constants.RESOURCE_PROGRESS);
                    }
                    AppDatabase.appDatabase.getContentProgressDao().addContentProgressList(contentProgressList);
                    BackupDatabase.backup(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//                    return null;
//                }
//
//                @Override
//                protected void onPostExecute(Object o) {
//                    super.onPostExecute(o);
            getStudentData(FC_Constants.LEARNT_WORDS_INTERNET, FC_Constants.LEARNT_WORDS_API, FC_Constants.currentStudentID);
//                }
//            }.execute();
        } else if (requestType.equalsIgnoreCase(FC_Constants.LEARNT_WORDS_INTERNET)) {
//            new AsyncTask<Object, Void, Object>() {
//                @Override
//                protected Object doInBackground(Object[] objects) {
//
            try {
                Type listType = new TypeToken<ArrayList<KeyWords>>() {
                }.getType();
                List<KeyWords> learntWordsList = gson.fromJson(response, listType);
                if (learntWordsList != null && learntWordsList.size() > 0) {
                    for (int i = 0; i < learntWordsList.size(); i++) {
                        learntWordsList.get(i).setSentFlag(1);
                        learntWordsList.get(i).setKeyWord("" + learntWordsList.get(i).getKeyWord().toLowerCase());
                        if (!checkWord(learntWordsList.get(i).getStudentId(), learntWordsList.get(i).getResourceId(), learntWordsList.get(i).getKeyWord(), learntWordsList.get(i).getWordType()))
                            AppDatabase.appDatabase.getKeyWordDao().insert(learntWordsList.get(i));
                    }
                    BackupDatabase.backup(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//                    return null;
//                }
//
//                @Override
//                protected void onPostExecute(Object o) {
//                    super.onPostExecute(o);
//                    startActivity(new Intent(getActivity(), HomeActivity.class));
//                    getActivity().finish();
//                }
//            }.execute();
        }
    }

    @Background
    @Override
    public void populateDB() {
        try {
            if (!FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false))
                doInitialEntries(AppDatabase.appDatabase);
            populateMenu();

            new Handler().postDelayed(() -> {
                    myView.dismissProgressDialog2();
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkWord(String studentId, String wordUUId, String wordCheck, String wordType) {
        try {
            String word = AppDatabase.appDatabase.getKeyWordDao().checkLearntData(studentId, "" + wordUUId, wordCheck.toLowerCase(), wordType);
            if (word != null)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
                                        detail.setLevel("" + content_cursor.getInt(content_cursor.getColumnIndex("level")));
                                        detail.setContentLanguage(content_cursor.getString(content_cursor.getColumnIndex("contentLanguage")));
                                        detail.setParentId(content_cursor.getString(content_cursor.getColumnIndex("parentId")));
                                        detail.setContentType(content_cursor.getString(content_cursor.getColumnIndex("contentType")));
                                        detail.setIsDownloaded("true");
                                        detail.setOnSDCard(false);
                                        contents.add(detail);
                                        content_cursor.moveToNext();
                                    }
                                }
                                AppDatabase.appDatabase.getContentTableDao().addContentList(contents);
                                content_cursor.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                context.startService(new Intent(context, AppExitService.class));
                BackupDatabase.backup(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void doInitialEntries(AppDatabase appDatabase) {
            try {
                com.pratham.foundation.database.domain.Status status;
                status = new com.pratham.foundation.database.domain.Status();
                status.setStatusKey("DeviceId");
                status.setValue("" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
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

                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                String macAddress = wInfo.getMacAddress();
                status.setStatusKey("wifiMAC");
                status.setValue(macAddress);
                appDatabase.getStatusDao().insert(status);

                setAppName(status);
                setAppVersion(status);
                BackupDatabase.backup(context);

                addStartTime();
//            getSdCardPath();
                requestLocation();

                FastSave.getInstance().saveBoolean(FC_Constants.INITIAL_ENTRIES, true);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setAppName(Status status) {
            String appname = "";
            if (AppDatabase.getDatabaseInstance(context).getStatusDao().getKey("appName") == null) {
                CharSequence c = "";
                ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                List l = am.getRunningAppProcesses();
                Iterator i = l.iterator();
                PackageManager pm = context.getPackageManager();
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
                AppDatabase.appDatabase.getStatusDao().insert(status);

            } else {
                CharSequence c = "";
                ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                List l = am.getRunningAppProcesses();
                Iterator i = l.iterator();
                PackageManager pm = context.getPackageManager();
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
                AppDatabase.appDatabase.getStatusDao().insert(status);
            }
        }

        public void requestLocation() {
            new LocationService(context).checkLocation();
        }

        public void setAppVersion(Status status) {
            if (AppDatabase.getDatabaseInstance(context).getStatusDao().getKey("apkVersion") == null) {
                status = new Status();

                status.setStatusKey("apkVersion");
                PackageInfo pInfo = null;
                String verCode = "";
                try {
                    pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    verCode = pInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                status.setValue(verCode);
                AppDatabase.appDatabase.getStatusDao().insert(status);

            } else {
                status.setStatusKey("apkVersion");

                PackageInfo pInfo = null;
                String verCode = "";
                try {
                    pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    verCode = pInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                status.setValue(verCode);
                AppDatabase.appDatabase.getStatusDao().insert(status);

            }
        }

        @Background
        public void addStartTime() {
            try {
                String appStartTime = FC_Utility.getCurrentDateTime();
                StatusDao statusDao = AppDatabase.appDatabase.getStatusDao();
                statusDao.updateValue("AppStartDateTime", appStartTime);
                BackupDatabase.backup(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}

