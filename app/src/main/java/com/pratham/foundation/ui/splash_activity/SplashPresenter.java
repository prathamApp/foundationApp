package com.pratham.foundation.ui.splash_activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.async.GetLatestVersion;
import com.pratham.foundation.async.PushDataToServer;
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
import com.pratham.foundation.services.AppExitService;
import com.pratham.foundation.services.LocationService;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;
import com.pratham.foundation.utility.SDCardUtil;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.pratham.foundation.ui.splash_activity.SplashActivity.exitDialogOpen;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_VERSION;

@EBean
public class SplashPresenter implements SplashContract.SplashPresenter {
    static String fpath, appname;
    Context context;
    SplashContract.SplashView splashView;

    @Bean(PushDataToServer.class)
    PushDataToServer pushDataToServer;

    public SplashPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(SplashContract.SplashView splashView) {
        this.splashView = splashView;
    }

    @Override
    public void checkVersion() {
        String currentVersion = FC_Utility.getCurrentVersion(context);
        String updatedVersion = FastSave.getInstance().getString(CURRENT_VERSION, "-1");
        if (updatedVersion.equalsIgnoreCase("-1")) {
            if (FC_Utility.isDataConnectionAvailable(context)) {
                try {
                    new GetLatestVersion(this).execute().get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else splashView.startApp();
        } else {
            if (updatedVersion != null && currentVersion != null && isCurrentVersionEqualsPlayStoreVersion(currentVersion, updatedVersion)) {
                splashView.showUpdateDialog();
            } else
                splashView.startApp();
        }
    }

    @Override
    public void versionObtained(String latestVersion) {
        if (latestVersion != null) {
            FastSave.getInstance().saveString(CURRENT_VERSION, latestVersion);
            checkVersion();
        } else {
            splashView.startApp();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void copyDataBase() {
        try {
            new AsyncTask<Void, Integer, Void>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    splashView.showProgressDialog();
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    File folder_file, db_file;
                    try {
//                        ArrayList<String> sdPath = FileUtils.getExtSdCardPaths(context);
                        SQLiteDatabase db = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PrathamBackups/foundation_db", null, SQLiteDatabase.OPEN_READONLY);
                        if (db != null) {
                            try {
                                Cursor content_cursor;
                                content_cursor = db.rawQuery("SELECT * FROM Score Where sentFlag=0", null);
                                List<Score> contents = new ArrayList<>();
                                if (content_cursor.moveToFirst()) {
                                    while (!content_cursor.isAfterLast()) {
                                        Score detail = new Score();
                                        detail.setScoreId(content_cursor.getInt(content_cursor.getColumnIndex("ScoreId")));
                                        detail.setSessionID(content_cursor.getString(content_cursor.getColumnIndex("SessionID")));
                                        detail.setStudentID(content_cursor.getString(content_cursor.getColumnIndex("StudentID")));
                                        detail.setDeviceID(content_cursor.getString(content_cursor.getColumnIndex("DeviceID")));
                                        detail.setResourceID(content_cursor.getString(content_cursor.getColumnIndex("ResourceID")));
                                        detail.setQuestionId(content_cursor.getInt(content_cursor.getColumnIndex("QuestionId")));
                                        detail.setScoredMarks(content_cursor.getInt(content_cursor.getColumnIndex("ScoredMarks")));
                                        detail.setTotalMarks(content_cursor.getInt(content_cursor.getColumnIndex("TotalMarks")));
                                        detail.setStartDateTime(content_cursor.getString(content_cursor.getColumnIndex("StartDateTime")));
                                        detail.setEndDateTime(content_cursor.getString(content_cursor.getColumnIndex("EndDateTime")));
                                        detail.setLevel(content_cursor.getInt(content_cursor.getColumnIndex("Level")));
                                        detail.setLabel(content_cursor.getString(content_cursor.getColumnIndex("Label")));
                                        detail.setSentFlag(content_cursor.getInt(content_cursor.getColumnIndex("sentFlag")));
                                        contents.add(detail);
                                        content_cursor.moveToNext();
                                    }
                                }
                                AppDatabase.appDatabase.getScoreDao().addScoreList(contents);
                                content_cursor.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                Cursor content_cursor;
                                content_cursor = db.rawQuery("SELECT * FROM Session Where sentFlag=0", null);
                                List<Session> contents = new ArrayList<>();
                                if (content_cursor.moveToFirst()) {
                                    while (!content_cursor.isAfterLast()) {
                                        Session detail = new Session();
                                        detail.setSessionID(content_cursor.getString(content_cursor.getColumnIndex("SessionID")));
                                        detail.setFromDate(content_cursor.getString(content_cursor.getColumnIndex("fromDate")));
                                        detail.setToDate(content_cursor.getString(content_cursor.getColumnIndex("toDate")));
                                        detail.setSentFlag(content_cursor.getInt(content_cursor.getColumnIndex("sentFlag")));
                                        contents.add(detail);
                                        content_cursor.moveToNext();
                                    }
                                }
                                AppDatabase.appDatabase.getSessionDao().addSessionList(contents);
                                content_cursor.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                Cursor content_cursor;
                                content_cursor = db.rawQuery("SELECT * FROM Attendance Where sentFlag=0", null);
                                List<Attendance> contents = new ArrayList<>();
                                if (content_cursor.moveToFirst()) {
                                    while (!content_cursor.isAfterLast()) {
                                        Attendance detail = new Attendance();
                                        detail.setAttendanceID(content_cursor.getInt(content_cursor.getColumnIndex("attendanceID")));
                                        detail.setSessionID(content_cursor.getString(content_cursor.getColumnIndex("SessionID")));
                                        detail.setDate(content_cursor.getString(content_cursor.getColumnIndex("Date")));
                                        detail.setGroupID(content_cursor.getString(content_cursor.getColumnIndex("GroupID")));
                                        detail.setSentFlag(content_cursor.getInt(content_cursor.getColumnIndex("sentFlag")));
                                        contents.add(detail);
                                        content_cursor.moveToNext();
                                    }
                                }
                                AppDatabase.appDatabase.getAttendanceDao().addAttendanceList(contents);
                                content_cursor.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                Cursor content_cursor;
                                content_cursor = db.rawQuery("SELECT * FROM Assessment Where sentFlag=0", null);
                                List<Assessment> contents = new ArrayList<>();
                                if (content_cursor.moveToFirst()) {
                                    while (!content_cursor.isAfterLast()) {
                                        Assessment detail = new Assessment();
                                        detail.setScoreIda(content_cursor.getInt(content_cursor.getColumnIndex("ScoreIda")));
                                        detail.setSessionIDm(content_cursor.getString(content_cursor.getColumnIndex("SessionIDm")));
                                        detail.setSessionIDa(content_cursor.getString(content_cursor.getColumnIndex("SessionIDa")));
                                        detail.setStudentIDa(content_cursor.getString(content_cursor.getColumnIndex("StudentIDa")));
                                        detail.setDeviceIDa(content_cursor.getString(content_cursor.getColumnIndex("DeviceIDa")));
                                        detail.setResourceIDa(content_cursor.getString(content_cursor.getColumnIndex("ResourceIDa")));
                                        detail.setQuestionIda(content_cursor.getInt(content_cursor.getColumnIndex("QuestionIda")));
                                        detail.setScoredMarksa(content_cursor.getInt(content_cursor.getColumnIndex("ScoredMarksa")));
                                        detail.setTotalMarksa(content_cursor.getInt(content_cursor.getColumnIndex("TotalMarksa")));
                                        detail.setStartDateTimea(content_cursor.getString(content_cursor.getColumnIndex("StartDateTimea")));
                                        detail.setEndDateTime(content_cursor.getString(content_cursor.getColumnIndex("EndDateTimea")));
                                        detail.setLevela(content_cursor.getInt(content_cursor.getColumnIndex("Levela")));
                                        detail.setLabel(content_cursor.getString(content_cursor.getColumnIndex("Labela")));
                                        detail.setSentFlag(content_cursor.getInt(content_cursor.getColumnIndex("sentFlag")));
                                        contents.add(detail);
                                        content_cursor.moveToNext();
                                    }
                                }
                                AppDatabase.appDatabase.getAssessmentDao().addAssessmentList(contents);
                                content_cursor.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                Cursor content_cursor;
                                content_cursor = db.rawQuery("SELECT * FROM Assessment Where Labela='" + FC_Constants.CERTIFICATE_LBL + "'", null);
                                List<Assessment> contents = new ArrayList<>();
                                if (content_cursor.moveToFirst()) {
                                    while (!content_cursor.isAfterLast()) {
                                        Assessment detail = new Assessment();
                                        detail.setScoreIda(content_cursor.getInt(content_cursor.getColumnIndex("ScoreIda")));
                                        detail.setSessionIDm(content_cursor.getString(content_cursor.getColumnIndex("SessionIDm")));
                                        detail.setSessionIDa(content_cursor.getString(content_cursor.getColumnIndex("SessionIDa")));
                                        detail.setStudentIDa(content_cursor.getString(content_cursor.getColumnIndex("StudentIDa")));
                                        detail.setDeviceIDa(content_cursor.getString(content_cursor.getColumnIndex("DeviceIDa")));
                                        detail.setResourceIDa(content_cursor.getString(content_cursor.getColumnIndex("ResourceIDa")));
                                        detail.setQuestionIda(content_cursor.getInt(content_cursor.getColumnIndex("QuestionIda")));
                                        detail.setScoredMarksa(content_cursor.getInt(content_cursor.getColumnIndex("ScoredMarksa")));
                                        detail.setTotalMarksa(content_cursor.getInt(content_cursor.getColumnIndex("TotalMarksa")));
                                        detail.setStartDateTimea(content_cursor.getString(content_cursor.getColumnIndex("StartDateTimea")));
                                        detail.setEndDateTime(content_cursor.getString(content_cursor.getColumnIndex("EndDateTimea")));
                                        detail.setLevela(content_cursor.getInt(content_cursor.getColumnIndex("Levela")));
                                        detail.setLabel(content_cursor.getString(content_cursor.getColumnIndex("Labela")));
                                        detail.setSentFlag(content_cursor.getInt(content_cursor.getColumnIndex("sentFlag")));
                                        contents.add(detail);
                                        content_cursor.moveToNext();
                                    }
                                }
                                AppDatabase.appDatabase.getAssessmentDao().addAssessmentList(contents);
                                content_cursor.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                Cursor content_cursor;
                                content_cursor = db.rawQuery("SELECT * FROM KeyWords", null);
                                List<KeyWords> contents = new ArrayList<>();
                                if (content_cursor.moveToFirst()) {
                                    while (!content_cursor.isAfterLast()) {
                                        KeyWords detail = new KeyWords();
                                        detail.setKeyWordId(content_cursor.getInt(content_cursor.getColumnIndex("learntWordId")));
                                        detail.setStudentId(content_cursor.getString(content_cursor.getColumnIndex("studentId")));
                                      //  detail.setSessionId(content_cursor.getString(content_cursor.getColumnIndex("sessionId")));
                                      //  detail.setSynId(content_cursor.getString(content_cursor.getColumnIndex("synId")));
                                        detail.setResourceId(content_cursor.getString(content_cursor.getColumnIndex("wordUUId")));
                                        detail.setKeyWord(content_cursor.getString(content_cursor.getColumnIndex("word")));
                                        detail.setWordType(content_cursor.getString(content_cursor.getColumnIndex("wordType")));
                                        detail.setSentFlag(content_cursor.getInt(content_cursor.getColumnIndex("sentFlag")));
                                        contents.add(detail);
                                        content_cursor.moveToNext();
                                    }
                                }
                                AppDatabase.appDatabase.getKeyWordDao().insertWord(contents);
                                content_cursor.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                Cursor content_cursor;
                                content_cursor = db.rawQuery("SELECT * FROM KeyWords", null);
                                List<KeyWords> contents = new ArrayList<>();
                                if (content_cursor.moveToFirst()) {
                                    while (!content_cursor.isAfterLast()) {
                                        KeyWords detail = new KeyWords();
                                        detail.setKeyWordId(content_cursor.getInt(content_cursor.getColumnIndex("learntWordId")));
                                        detail.setStudentId(content_cursor.getString(content_cursor.getColumnIndex("studentId")));
                                      //  detail.setSessionId(content_cursor.getString(content_cursor.getColumnIndex("sessionId")));
                                      //  detail.setSynId(content_cursor.getString(content_cursor.getColumnIndex("synId")));
                                        detail.setResourceId(content_cursor.getString(content_cursor.getColumnIndex("wordUUId")));
                                        detail.setKeyWord(content_cursor.getString(content_cursor.getColumnIndex("word")));
                                        detail.setWordType(content_cursor.getString(content_cursor.getColumnIndex("wordType")));
                                        detail.setSentFlag(content_cursor.getInt(content_cursor.getColumnIndex("sentFlag")));
                                        contents.add(detail);
                                        content_cursor.moveToNext();
                                    }
                                }
                                AppDatabase.appDatabase.getKeyWordDao().insertWord(contents);
                                content_cursor.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                Cursor content_cursor;
                                content_cursor = db.rawQuery("SELECT * FROM ContentProgress", null);
                                List<ContentProgress> contents = new ArrayList<>();
                                if (content_cursor.moveToFirst()) {
                                    while (!content_cursor.isAfterLast()) {
                                        ContentProgress detail = new ContentProgress();
                                        detail.setSessionId(content_cursor.getString(content_cursor.getColumnIndex("sessionId")));
                                        detail.setStudentId(content_cursor.getString(content_cursor.getColumnIndex("studentId")));
                                        detail.setResourceId(content_cursor.getString(content_cursor.getColumnIndex("resourceId")));
                                        detail.setUpdatedDateTime(content_cursor.getString(content_cursor.getColumnIndex("updatedDateTime")));
                                        detail.setProgressPercentage(content_cursor.getString(content_cursor.getColumnIndex("progressPercentage")));
                                        detail.setLabel(content_cursor.getString(content_cursor.getColumnIndex("label")));
                                        detail.setSentFlag(content_cursor.getInt(content_cursor.getColumnIndex("sentFlag")));
                                        contents.add(detail);
                                        content_cursor.moveToNext();
                                    }
                                }
                                AppDatabase.appDatabase.getContentProgressDao().addContentProgressList(contents);
                                content_cursor.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            BackupDatabase.backup(context);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    //addStartTime();
                    super.onPostExecute(aVoid);
                    splashView.dismissProgressDialog();
                    splashView.showButton();
                    BackupDatabase.backup(context);
                }

            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushData() {
        pushDataToServer.doInBackground(true);
    }

    @Override
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

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            String macAddress = wInfo.getMacAddress();
            status.setStatusKey("wifiMAC");
            status.setValue(macAddress);
            appDatabase.getStatusDao().insert(status);

            setAppName(status);
            setAppVersion(status);
            BackupDatabase.backup(context);
            addStartTime();
            requestLocation();
            FastSave.getInstance().saveBoolean(FC_Constants.INITIAL_ENTRIES, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void copyZipAndPopulateMenu() {
        new AsyncTask<Void, Integer, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                splashView.showProgressDialog();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (!FastSave.getInstance().getBoolean(FC_Constants.KEY_ASSET_COPIED, false)) {
                        File mydir = null;
                        mydir = new File(ApplicationClass.foundationPath + "/.FCA");
                        if (!mydir.exists()) mydir.mkdirs();

                        String path = ApplicationClass.foundationPath + "/.FCA/";
                        copyFile(context, path);

                        FastSave.getInstance().saveBoolean(FC_Constants.KEY_ASSET_COPIED, true);

                        if (!FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false))
                            doInitialEntries(AppDatabase.appDatabase);
                        if (!FastSave.getInstance().getBoolean(FC_Constants.KEY_MENU_COPIED, false))
                            populateMenu();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (!exitDialogOpen)
                    splashView.gotoNextActivity();
            }
        }.execute();
    }

    private void unzipFile(String source, String destination) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
            new File(source).delete();
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    private void copyFile(Context context, String path) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream in = assetManager.open("English.zip");
            OutputStream out = new FileOutputStream(path + "English.zip");
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);
            while (read != -1) {
                out.write(buffer, 0, read);
                read = in.read(buffer);
            }
            unzipFile(ApplicationClass.foundationPath + "/.FCA/English.zip", ApplicationClass.foundationPath + "/.FCA");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void copyDBFile() {
        try {
            File direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal");
            if (!direct.exists()) direct.mkdir();
            InputStream in = new FileInputStream(ApplicationClass.contentSDPath + "/.FCA/" + AppDatabase.DB_NAME);
            OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/" + AppDatabase.DB_NAME);
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);
            while (read != -1) {
                out.write(buffer, 0, read);
                read = in.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void populateSDCardMenu() {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (!FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false)) {
                    doInitialEntries(AppDatabase.appDatabase);
                    copyDBFile();
                    try {
                        File db_file;
                        db_file = new File(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/" + AppDatabase.DB_NAME);
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
                                            detail.setNodeId("" + content_cursor.getString(content_cursor.getColumnIndex("nodeId")));
                                            detail.setNodeType("" + content_cursor.getString(content_cursor.getColumnIndex("nodeType")));
                                            detail.setNodeTitle("" + content_cursor.getString(content_cursor.getColumnIndex("nodeTitle")));
                                            detail.setNodeKeywords("" + content_cursor.getString(content_cursor.getColumnIndex("nodeKeywords")));
                                            detail.setNodeAge("" + content_cursor.getString(content_cursor.getColumnIndex("nodeAge")));
                                            detail.setNodeDesc("" + content_cursor.getString(content_cursor.getColumnIndex("nodeDesc")));
                                            detail.setNodeServerImage("" + content_cursor.getString(content_cursor.getColumnIndex("nodeServerImage")));
                                            detail.setNodeImage("" + content_cursor.getString(content_cursor.getColumnIndex("nodeImage")));
                                            detail.setResourceId("" + content_cursor.getString(content_cursor.getColumnIndex("resourceId")));
                                            detail.setResourceType("" + content_cursor.getString(content_cursor.getColumnIndex("resourceType")));
                                            detail.setResourcePath("" + content_cursor.getString(content_cursor.getColumnIndex("resourcePath")));
                                            detail.setLevel("" + content_cursor.getInt(content_cursor.getColumnIndex("level")));
                                            detail.setContentLanguage("" + content_cursor.getString(content_cursor.getColumnIndex("contentLanguage")));
                                            detail.setParentId("" + content_cursor.getString(content_cursor.getColumnIndex("parentId")));
                                            detail.setContentType("" + content_cursor.getString(content_cursor.getColumnIndex("contentType")));
                                            detail.setIsDownloaded("" + content_cursor.getString(content_cursor.getColumnIndex("isDownloaded")));
                                            detail.setOnSDCard(true);
                                            contents.add(detail);
                                            content_cursor.moveToNext();
                                        }
                                    }
                                    AppDatabase.appDatabase.getContentTableDao().addContentList(contents);
                                    ApplicationClass.contentExistOnSD = true;
                                    content_cursor.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        BackupDatabase.backup(context);
                        if (!exitDialogOpen)
                            splashView.gotoNextActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                BackupDatabase.backup(context);
                if (!exitDialogOpen)
                    splashView.gotoNextActivity();
            }
        }.execute();
    }

    public void populateMenu() {
        try {
            File folder_file, db_file;
            if (!FastSave.getInstance().getBoolean(FC_Constants.KEY_MENU_COPIED, false)) {
                if (ApplicationClass.isTablet) {
                    folder_file = new File(ApplicationClass.contentSDPath);
                } else
                    folder_file = new File(ApplicationClass.foundationPath + "/.FCA/");
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
                                        detail.setIsDownloaded("" + content_cursor.getString(content_cursor.getColumnIndex("isDownloaded")));
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
            }
            FastSave.getInstance().saveBoolean(FC_Constants.KEY_MENU_COPIED, true);
            context.startService(new Intent(context, AppExitService.class));
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestLocation() {
        new LocationService(context).checkLocation();
    }

    private void setAppVersion(Status status) {
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

    @Override
    public void updateVersionApp() {
        if (AppDatabase.getDatabaseInstance(context).getStatusDao().getKey("apkVersion") != null) {
            PackageInfo pInfo = null;
            String verCode = "";
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                verCode = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            AppDatabase.appDatabase.getStatusDao().updateValue("apkVersion", verCode);
        }
    }

    private void setAppName(Status status) {
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
                e.printStackTrace();
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


    @SuppressLint("StaticFieldLeak")
    private void addStartTime() {
        new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    String appStartTime = FC_Utility.getCurrentDateTime();
                    StatusDao statusDao = AppDatabase.appDatabase.getStatusDao();
                    statusDao.updateValue("AppStartDateTime", appStartTime);
                    BackupDatabase.backup(context);
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }.execute();
    }


    private boolean isCurrentVersionEqualsPlayStoreVersion(String currentVersion, String
            playStoreVersion) {
        float cVersion, pVersion;
        try {
            cVersion = Float.parseFloat(currentVersion);
            pVersion = Float.parseFloat(playStoreVersion);
            return cVersion < pVersion;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean getSdCardPath() {
        ArrayList<String> base_path = SDCardUtil.getExtSdCardPaths(context);
        if (base_path.size() > 0) {
            String path = base_path.get(0).replace("[", "");
            path = path.replace("]", "");
            fpath = path;
        } else
            fpath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file;
        if (ApplicationClass.isTablet) {
            file = new File(fpath + "/.FCA/foundation_db");
            if (file.exists()) {
                ApplicationClass.contentSDPath = fpath;
                Log.d("getSD", "getSdCardPath: " + ApplicationClass.contentSDPath);
//            FC_Constants.SD_CARD_Content = true;
                return true;
            } else {
//            FC_Constants.SD_CARD_Content = false;
                return false;
            }
        } else {
            ApplicationClass.contentSDPath = fpath;
            return false;
        }
    }

    @Background
    @Override
    public void insertNewData() {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream in = assetManager.open("foundation_db");
            OutputStream out = new FileOutputStream(ApplicationClass.foundationPath + "/foundation_db");
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);
            while (read != -1) {
                out.write(buffer, 0, read);
                read = in.read(buffer);
            }

            File folder_file, db_file;
            folder_file = new File(ApplicationClass.foundationPath + "/.FCA");
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
                                    detail.setIsDownloaded("" + content_cursor.getString(content_cursor.getColumnIndex("isDownloaded")));
                                    detail.setOnSDCard(false);
                                    contents.add(detail);
                                    content_cursor.moveToNext();
                                }
                            }
                            AppDatabase.appDatabase.getContentTableDao().addNewContentList(contents);
                            content_cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            FastSave.getInstance().saveBoolean(FC_Constants.newDataLanguageInserted, true);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

