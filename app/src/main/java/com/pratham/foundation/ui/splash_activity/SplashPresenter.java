package com.pratham.foundation.ui.splash_activity;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.pratham.foundation.database.AppDatabase.DB_NAME;
import static com.pratham.foundation.database.AppDatabase.DB_VERSION;
import static com.pratham.foundation.ui.splash_activity.SplashActivity.exitDialogOpen;
import static com.pratham.foundation.ui.splash_activity.SplashActivity.fragmentBottomOpenFlg;
import static com.pratham.foundation.utility.FC_Constants.APP_LANGUAGE;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_VERSION;
import static com.pratham.foundation.utility.FC_Constants.HINDI;
import static com.pratham.foundation.utility.FC_Constants.apkSP;
import static com.pratham.foundation.utility.FC_Constants.apkTab;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.async.GetLatestVersion;
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
import com.pratham.foundation.modalclasses.Model_CourseEnrollment;
import com.pratham.foundation.services.AppExitService;
import com.pratham.foundation.services.LocationService;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;
import com.pratham.foundation.utility.SDCardUtil;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EBean
public class SplashPresenter implements SplashContract.SplashPresenter {
    static String fpath, appname;
    Context context;
    SplashContract.SplashView splashView;
    boolean copyDb = false;

//    @Bean(PushDataToServer_New.class)
//    PushDataToServer_New pushDataToServer;

    public SplashPresenter(Context context) {
        this.context = context;
    }

    //Sets View(UI)
    @Override
    public void setView(SplashContract.SplashView splashView) {
        this.splashView = splashView;
    }

    //Checks the current version of app and gets the latest version for app
    @Override
    public void checkVersion() {
        //get current version of app
        String currentVersion = FC_Utility.getCurrentVersion(context);
        String updatedVersion = FastSave.getInstance().getString(CURRENT_VERSION, "-1");
        if (updatedVersion.equalsIgnoreCase("-1")) {
            if (FC_Utility.isDataConnectionAvailable(context)) {
                //checks if new version is avalible on playstore
                try {
                    new GetLatestVersion(this).execute().get();//method returns latest version
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
            checkVersion();
        } else {
            splashView.startApp();
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(context.getDatabasePath(AppDatabase.DB_NAME).getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    @Background
    @Override
    @SuppressLint("StaticFieldLeak")
    public void createDatabase() {
        try {
            boolean dbExist = checkDataBase();
            if (!dbExist) {
                //check for specific folder and create database
                try {
                    AppDatabase.getDatabaseInstance(context);
                    if (new File(ApplicationClass.getStoragePath().getAbsolutePath() + "/PrathamBackups/" + DB_NAME).exists())
                        copyDb = true;
                    getSdCardPath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callCopyDB();
            } else {
                AppDatabase.getDatabaseInstance(context);
                getSdCardPath();
                splashView.preShowBtn();
            }
            if (FastSave.getInstance().getString(APP_LANGUAGE, "").equalsIgnoreCase(""))
                FastSave.getInstance().saveString(APP_LANGUAGE, HINDI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void callCopyDB() {
        if (copyDb)
            copyDataBase();
        else
            new Handler().postDelayed(() -> splashView.showButton(), 2000);
    }

    @Background
    @SuppressLint("StaticFieldLeak")
    @Override
    public void copyDataBase() {
        splashView.showProgressDialog();
        try {
//                        ArrayList<String> sdPath = FileUtils.getExtSdCardPaths(context);
            SQLiteDatabase db = SQLiteDatabase.openDatabase(ApplicationClass.getStoragePath()
                    .getAbsolutePath() + "/PrathamBackups/foundation_db", null, SQLiteDatabase.OPEN_READONLY);
            if (db != null) {
                //Get all data and insert it in database
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
                    AppDatabase.getDatabaseInstance(context).getScoreDao().addScoreList(contents);
                    content_cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Cursor content_cursor;
//                            appDatabase.getContentTableDao().clearDB();
                    content_cursor = db.rawQuery("SELECT * FROM CourseEnrolled Where sentFlag=0", null);
                    //populate contents
                    List<Model_CourseEnrollment> courseEnrollmentList = new ArrayList<>();
                    if (content_cursor.moveToFirst()) {
                        while (!content_cursor.isAfterLast()) {
                            Model_CourseEnrollment detail = new Model_CourseEnrollment();
                            detail.setCourseId("" + content_cursor.getString(content_cursor.getColumnIndex("courseId")));
                            detail.setGroupId("" + content_cursor.getString(content_cursor.getColumnIndex("groupId")));
                            detail.setPlanFromDate("" + content_cursor.getString(content_cursor.getColumnIndex("planFromDate")));
                            detail.setPlanToDate("" + content_cursor.getString(content_cursor.getColumnIndex("planToDate")));
                            detail.setCoachVerified(+content_cursor.getInt(content_cursor.getColumnIndex("coachVerified")));
                            detail.setCoachVerificationDate("" + content_cursor.getString(content_cursor.getColumnIndex("coachVerificationDate")));
                            detail.setCourseExperience("" + content_cursor.getString(content_cursor.getColumnIndex("courseExperience")));
                            detail.setCourse_status("" + content_cursor.getString(content_cursor.getColumnIndex("courseCompleted")));
                            detail.setCoachImage("" + content_cursor.getString(content_cursor.getColumnIndex("coachImage")));
                            detail.setLanguage("" + content_cursor.getString(content_cursor.getColumnIndex("language")));
                            detail.setSentFlag(content_cursor.getInt(content_cursor.getColumnIndex("sentFlag")));
                            courseEnrollmentList.add(detail);
                            content_cursor.moveToNext();
                        }
                    }
                    AppDatabase.getDatabaseInstance(context).getCourseDao().insertListCourse(courseEnrollmentList);
                    FastSave.getInstance().saveString(CURRENT_VERSION, "" + FC_Utility.getCurrentVersion(context));
                    Log.d("-CT-", "populateMenu_New END");
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
                    AppDatabase.getDatabaseInstance(context).getSessionDao().addSessionList(contents);
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
                    AppDatabase.getDatabaseInstance(context).getAttendanceDao().addAttendanceList(contents);
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
                    AppDatabase.getDatabaseInstance(context).getAssessmentDao().addAssessmentList(contents);
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
                    AppDatabase.getDatabaseInstance(context).getAssessmentDao().addAssessmentList(contents);
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
                    AppDatabase.getDatabaseInstance(context).getKeyWordDao().insertWord(contents);
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
                    AppDatabase.getDatabaseInstance(context).getContentProgressDao().addContentProgressList(contents);
                    content_cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                BackupDatabase.backup(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        splashView.dismissProgressDialog();
        splashView.showButton();
        BackupDatabase.backup(context);
    }

    @Override
    public void pushData() {
//        pushDataToServer.startDataPush(context, false);
    }

    @Override
    public void doInitialEntries(AppDatabase appDatabase) {
        //General info is inserted to database like deviceid, device name etc
        try {
            Log.d("pushorassign", "Splash doInitialEntries : KEY_MENU_COPIED: " + FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false));
            Status status;
            status = new Status();
            status.setStatusKey("DeviceId");
            status.setValue("" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            status.setDescription("" + Build.SERIAL);
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("CRLID");
            status.setValue("default");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("DeviceName");
            status.setValue(FC_Utility.getDeviceName());
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("gpsFixDuration");
            status.setValue("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("prathamCode");
            status.setValue("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("apkType");
            if (ApplicationClass.getAppMode())
                status.setValue("" + apkTab);
            else
                status.setValue("" + apkSP);
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("Latitude");
            status.setValue("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("Longitude");
            status.setValue("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("GPSDateTime");
            status.setValue("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("CurrentSession");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("SdCardPath");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("AppLang");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("AppStartDateTime");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            //new Entries
            status = new Status();
            status.setStatusKey("ActivatedForGroups");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("AndroidVersion");
            status.setValue(FC_Utility.getAndroidOSVersion());
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("InternalAvailableStorage");
            status.setValue(FC_Utility.getInternalStorageStatus());
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("DeviceManufacturer");
            status.setValue(FC_Utility.getDeviceManufacturer());
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("DeviceModel");
            status.setValue(FC_Utility.getDeviceModel());
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("ScreenResolution");
            status.setValue(FastSave.getInstance().getString(FC_Constants.SCR_RES, ""));
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("programId");
            status.setValue("1");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("group1");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("group2");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("group3");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("group4");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("group5");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("village");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("ActivatedDate");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("AssessmentSession");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("AndroidID");
            status.setValue("NA");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("DBVersion");
            status.setValue(DB_VERSION);
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("SerialID");
            status.setValue(FC_Utility.getDeviceSerialID());
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("AppBuild Date");
            status.setValue(ApplicationClass.BUILD_DATE);
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
            Log.d("pushorassign", "Splash 2 doInitialEntries : KEY_MENU_COPIED: " + FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false));
            FastSave.getInstance().saveBoolean(FC_Constants.INITIAL_ENTRIES, true);
            Log.d("pushorassign", "Splash 3 doInitialEntries : KEY_MENU_COPIED: " + FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false));
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
                        Log.d("pushorassign", "doInBackground: ");
                        if (new File(path).exists()) {
                            copyFile(context, path);
                            if (!FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false))
                                doInitialEntries(AppDatabase.getDatabaseInstance(context));
//                            if (!FastSave.getInstance().getBoolean(FC_Constants.KEY_MENU_COPIED, false))
//                                populateMenu();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                splashView.dismissProgressDialog();
                if (!exitDialogOpen)
                    splashView.gotoNextActivity();
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void copyZipAndPopulateMenu_New() {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                splashView.showProgressDialog();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                //check if data is present in folder and copy
                try {
                    File mydir = null;
                    mydir = new File(ApplicationClass.foundationPath + "/.FCA");
                    if (!mydir.exists()) mydir.mkdirs();

                    String path = ApplicationClass.foundationPath + "/.FCA/";
                    Log.d("-CT-", "Copy File in IFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
                    if (new File(path).exists()) {
                        copyFile(context, path);
                        populateMenu_New();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.d("-CT-", "insertNewData ENDDDDDDDDDDDDD");
                Log.d("-CT-", "insert END  :::::CURRENT_VERSION::::: " + FastSave.getInstance().getString(CURRENT_VERSION, "NA"));
                Log.d("-CT-", "insert END  ::::getCurrentVersion:::: " + FC_Utility.getCurrentVersion(context));
                Log.d("-CT-", "Before insert new  :::::VOICES_DOWNLOAD_INTENT::::: " + FastSave.getInstance().getBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, false));
                splashView.dismissProgressDialog();
                if (!FastSave.getInstance().getBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, false))
                    splashView.show_STT_Dialog();
                else {
                    if (!fragmentBottomOpenFlg &&
                            FastSave.getInstance().getBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, false))
                        splashView.showBottomFragment();
                }
            }
        }.execute();
    }

    @Override
    public void createNoMediaForFCInternal(File myFile) {
        try {
            File[] files = myFile.listFiles();
            try {
                File direct = new File(myFile.getPath() + "/.nomedia");
                if (!direct.exists()) {
                    Log.d("Files", "\nFirst Directory : " + myFile.getName());//CanonicalPath());
                    direct.createNewFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    createNoMediaForFCInternal(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateMenu_New() {
        //Get all data from the SD_Card and populate the menu
        try {
            Log.d("-CT-", "populateMenu_New in IFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
            File folder_file, db_file;
            folder_file = new File(ApplicationClass.foundationPath + "/.FCA/");
            if (folder_file.exists()) {
                db_file = new File(folder_file + "/" + DB_NAME);
                if (db_file.exists()) {
                    SQLiteDatabase db = SQLiteDatabase.openDatabase(db_file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
                    if (db != null) {
                        try {
                            Cursor content_cursor;
//                            appDatabase.getContentTableDao().clearDB();
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
                                    detail.setVersion("" + content_cursor.getString(content_cursor.getColumnIndex("version")));
//                                    detail.setProgramid("" + content_cursor.getString(content_cursor.getColumnIndex("programid")));
                                    try {
                                        String origNodeId = "" + content_cursor.getString(content_cursor.getColumnIndex("origNodeVersion"));
                                        detail.setOrigNodeVersion("" + origNodeId);
                                    } catch (Exception e) {
                                        detail.setOrigNodeVersion("");
                                        e.printStackTrace();
                                    }
                                    detail.setSubject("" + content_cursor.getString(content_cursor.getColumnIndex("subject")));
                                    detail.setSeq_no(content_cursor.getInt(content_cursor.getColumnIndex("seq_no")));
                                    detail.setStudentId("" + content_cursor.getString(content_cursor.getColumnIndex("studentId")));
                                    detail.setOnSDCard(false);
                                    contents.add(detail);
                                    content_cursor.moveToNext();
                                }
                            }
                            AppDatabase.getDatabaseInstance(context).getContentTableDao().addContentList(contents);
                            FastSave.getInstance().saveString(CURRENT_VERSION, "" + FC_Utility.getCurrentVersion(context));
                            Log.d("-CT-", "populateMenu_New END");
                            content_cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Cursor content_cursor;
                            content_cursor = db.rawQuery("SELECT * FROM CourseEnrolled Where sentFlag=0", null);
                            //populate contents
                            List<Model_CourseEnrollment> courseEnrollmentList = new ArrayList<>();
                            if (content_cursor.moveToFirst()) {
                                while (!content_cursor.isAfterLast()) {
                                    Model_CourseEnrollment detail = new Model_CourseEnrollment();
                                    detail.setCourseId("" + content_cursor.getString(content_cursor.getColumnIndex("courseId")));
                                    detail.setGroupId("" + content_cursor.getString(content_cursor.getColumnIndex("groupId")));
                                    detail.setPlanFromDate("" + content_cursor.getString(content_cursor.getColumnIndex("planFromDate")));
                                    detail.setPlanToDate("" + content_cursor.getString(content_cursor.getColumnIndex("planToDate")));
                                    detail.setCoachVerified(content_cursor.getInt(content_cursor.getColumnIndex("coachVerified")));
                                    detail.setCoachVerificationDate("" + content_cursor.getString(content_cursor.getColumnIndex("coachVerificationDate")));
                                    detail.setCourseExperience("" + content_cursor.getString(content_cursor.getColumnIndex("courseExperience")));
                                    detail.setCourse_status("" + content_cursor.getString(content_cursor.getColumnIndex("courseCompleted")));
                                    detail.setCoachImage("" + content_cursor.getString(content_cursor.getColumnIndex("coachImage")));
                                    detail.setLanguage("" + content_cursor.getString(content_cursor.getColumnIndex("language")));
                                    detail.setSentFlag(content_cursor.getInt(content_cursor.getColumnIndex("sentFlag")));
                                    courseEnrollmentList.add(detail);
                                    content_cursor.moveToNext();
                                }
                            }
                            AppDatabase.getDatabaseInstance(context).getCourseDao().insertListCourse(courseEnrollmentList);
                            FastSave.getInstance().saveString(CURRENT_VERSION, "" + FC_Utility.getCurrentVersion(context));
                            Log.d("-CT-", "populateMenu_New END");
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

    //Extract data from the zip file
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

    //copy and unzip the file
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
            FastSave.getInstance().saveBoolean(FC_Constants.KEY_ASSET_COPIED, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //copy DB From The Assets And Populate Menu
    private void copyAssetsFile(Context context, String path) {
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
            e.printStackTrace();
        }
    }

    @UiThread
    public void copyDBFile() {

        try {
            File internalDB = new File(ApplicationClass.getStoragePath().toString() + "/FCAInternal/" + DB_NAME);
            if (internalDB.exists()) {
                Log.d("copyDBFile", "copyDBFile: ");
                internalDB.delete();
                Log.d("copyDBFile", "delete: ");
            }
        } catch (Exception e) {
            Log.d("copyDBFile", "Exception : ");
            e.printStackTrace();
        }

        try {
            File direct = new File(ApplicationClass.getStoragePath().toString() + "/FCAInternal");
            if (!direct.exists()) direct.mkdirs();
            InputStream in = new FileInputStream(ApplicationClass.contentSDPath + "/.FCA/" + DB_NAME);
            OutputStream out = new FileOutputStream(ApplicationClass.getStoragePath().toString() + "/FCAInternal/" + DB_NAME);
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);
            while (read != -1) {
                out.write(buffer, 0, read);
                read = in.read(buffer);
            }
            Log.d("copyDBFile", "CopyDone : ");
        } catch (Exception e) {
            Log.d("copyDBFile", "Exception : ");
            e.printStackTrace();
        }
    }

    @UiThread
    public void copyTestJsons(int no) {
/*        if (new File(ApplicationClass.contentSDPath + "/.FCA/TestJsons/Test_" + no + ".json").exists()) {
            try {
                File internalTestJson = new File(ApplicationClass.getStoragePath().toString()
                        + "/FCAInternal/TestJsons/Test_" + no + ".json");
                if (new File(ApplicationClass.getStoragePath().toString()
                        + "/FCAInternal/TestJsons/Test_" + no + ".json").exists())
                    new File(ApplicationClass.getStoragePath().toString()
                            + "/FCAInternal/TestJsons/Test_" + no + ".json").delete();
            } catch (Exception e) {
                Log.d("copyDBFile", "Exception : ");
                e.printStackTrace();
            }

            try {
                File direct = new File(ApplicationClass.getStoragePath().toString() +
                        "/FCAInternal/TestJsons");
                if (!direct.exists()) direct.mkdirs();
                InputStream in = new FileInputStream(ApplicationClass.contentSDPath + "/.FCA/TestJsons/Test_" + no + ".json");
                OutputStream out = new FileOutputStream(ApplicationClass.getStoragePath().toString()
                        + "/FCAInternal/TestJsons/" + "Test_" + no + ".json");
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);
                while (read != -1) {
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }
                Log.d("copyDBFile", "CopyDone : ");
                if (no == 5)
                    FastSave.getInstance().saveBoolean(FC_Constants.TEST_JSON_COPY, true);
                if (no < 5) {
                    no += 1;
                    copyTestJsons(no);
                }
            } catch (Exception e) {
                Log.d("copyDBFile", "Exception : ");
                e.printStackTrace();
            }
        } else {
            if (no == 5)
                FastSave.getInstance().saveBoolean(FC_Constants.TEST_JSON_COPY, true);
            if (no < 5) {
                no += 1;
                copyTestJsons(no);
            }
        }*/
    }

    @UiThread
    public void copyInternalTestJsons(int no) {
/*        if (new File(ApplicationClass.foundationPath + "/.FCA/Test_" + no + ".json").exists()) {
            try {
                File internalTestJson = new File(ApplicationClass.getStoragePath().toString()
                        + "/FCAInternal/TestJsons/Test_" + no + ".json");
                if (new File(ApplicationClass.getStoragePath().toString()
                        + "/FCAInternal/TestJsons/Test_" + no + ".json").exists())
                    new File(ApplicationClass.getStoragePath().toString()
                            + "/FCAInternal/TestJsons/Test_" + no + ".json").delete();
            } catch (Exception e) {
                Log.d("copyDBFile", "Exception : ");
                e.printStackTrace();
            }

            try {
                File direct = new File(ApplicationClass.getStoragePath().toString() +
                        "/FCAInternal/TestJsons");
                if (!direct.exists()) direct.mkdirs();
                InputStream in = new FileInputStream(ApplicationClass.foundationPath + "/.FCA/Test_" + no + ".json");
                OutputStream out = new FileOutputStream(ApplicationClass.getStoragePath().toString()
                        + "/FCAInternal/TestJsons/" + "Test_" + no + ".json");
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);
                while (read != -1) {
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }
                Log.d("copyDBFile", "CopyDone : ");
                if (no == 5) {
                    new File(ApplicationClass.foundationPath + "/.FCA/Test_" + no + ".json").delete();
                    FastSave.getInstance().saveBoolean(FC_Constants.TEST_JSON_COPY2, true);
                }
                if (no < 5) {
                    new File(ApplicationClass.foundationPath + "/.FCA/Test_" + no + ".json").delete();
                    no += 1;
                    copyInternalTestJsons(no);
                }
            } catch (Exception e) {
                Log.d("copyDBFile", "Exception : ");
                e.printStackTrace();
            }
        } else {
            if (no == 5)
                FastSave.getInstance().saveBoolean(FC_Constants.TEST_JSON_COPY2, true);
            if (no < 5) {
                no += 1;
                copyInternalTestJsons(no);
            }
        }*/
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void populateSDCardMenu() {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                splashView.showProgressDialog();
                Log.d("copyDBFile", "Before Copy : ");
                copyDBFile();
                Log.d("copyDBFile", "Aft Copy : ");
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (!FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false)) {
                    doInitialEntries(AppDatabase.getDatabaseInstance(context));
                }
                if (!FastSave.getInstance().getBoolean(FC_Constants.INITIAL_SD_COPIED, false))
                    try {
                        File db_file;
                        db_file = new File(ApplicationClass.getStoragePath().toString() + "/FCAInternal/" + DB_NAME);
                        if (db_file.exists()) {
                            SQLiteDatabase db = SQLiteDatabase.openDatabase(db_file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
                            if (db != null) {
                                Cursor content_cursor;
                                try {
                                    Log.d("copyDBFile", "Content Data Copy Start: ");
                                    content_cursor = db.rawQuery("SELECT * FROM ContentTable", null);
                                    //populate contents
                                    Log.d("copyDBFile", "Content Data Copy Start: " + content_cursor.getCount());
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
                                            detail.setVersion(content_cursor.getString(content_cursor.getColumnIndex("version")));
//                                            detail.setProgramid(content_cursor.getString(content_cursor.getColumnIndex("programid")));
                                            detail.setOrigNodeVersion(content_cursor.getString(content_cursor.getColumnIndex("origNodeVersion")));
                                            detail.setSubject(content_cursor.getString(content_cursor.getColumnIndex("subject")));
                                            detail.setSeq_no(content_cursor.getInt(content_cursor.getColumnIndex("seq_no")));
                                            detail.setStudentId("" + content_cursor.getString(content_cursor.getColumnIndex("studentId")));
                                            detail.setOnSDCard(true);
                                            contents.add(detail);
                                            content_cursor.moveToNext();
                                        }
                                    }
                                    AppDatabase.getDatabaseInstance(context).getContentTableDao().addContentList(contents);
                                    ApplicationClass.contentExistOnSD = true;
                                    content_cursor.close();
//                            appDatabase.getContentTableDao().clearDB();
                                    Cursor content_cursor2;
                                    content_cursor2 = db.rawQuery("SELECT * FROM CourseEnrolled Where sentFlag=0", null);
                                    //populate contents
                                    List<Model_CourseEnrollment> courseEnrollmentList = new ArrayList<>();
                                    if (content_cursor2.moveToFirst()) {
                                        while (!content_cursor2.isAfterLast()) {
                                            Model_CourseEnrollment detail = new Model_CourseEnrollment();
                                            detail.setCourseId("" + content_cursor2.getString(content_cursor2.getColumnIndex("courseId")));
                                            detail.setGroupId("" + content_cursor2.getString(content_cursor2.getColumnIndex("groupId")));
                                            detail.setPlanFromDate("" + content_cursor2.getString(content_cursor2.getColumnIndex("planFromDate")));
                                            detail.setPlanToDate("" + content_cursor2.getString(content_cursor2.getColumnIndex("planToDate")));
                                            detail.setCoachVerified(content_cursor2.getInt(content_cursor2.getColumnIndex("coachVerified")));
                                            detail.setCoachVerificationDate("" + content_cursor2.getString(content_cursor2.getColumnIndex("coachVerificationDate")));
                                            detail.setCourseExperience("" + content_cursor2.getString(content_cursor2.getColumnIndex("courseExperience")));
                                            detail.setCourse_status("" + content_cursor2.getString(content_cursor2.getColumnIndex("courseCompleted")));
                                            detail.setCoachImage("" + content_cursor2.getString(content_cursor2.getColumnIndex("coachImage")));
                                            detail.setLanguage("" + content_cursor2.getString(content_cursor2.getColumnIndex("language")));
                                            detail.setSentFlag(content_cursor2.getInt(content_cursor2.getColumnIndex("sentFlag")));
                                            courseEnrollmentList.add(detail);
                                            content_cursor2.moveToNext();
                                        }
                                    }
                                    AppDatabase.getDatabaseInstance(context).getCourseDao().insertListCourse(courseEnrollmentList);
                                    FastSave.getInstance().saveString(CURRENT_VERSION, "" + FC_Utility.getCurrentVersion(context));
                                    Log.d("-CT-", "populateMenu_New END");
                                    content_cursor2.close();
                                    FastSave.getInstance().saveBoolean(FC_Constants.KEY_MENU_COPIED, true);
                                    Log.d("copyDBFile", "Content Data Copy complete: ");
                                } catch (Exception e) {
                                    Log.d("copyDBFile", "Excep: ");
                                    e.printStackTrace();
                                }

                            }
                        } else
                            FastSave.getInstance().saveBoolean(FC_Constants.INITIAL_SD_COPIED, true);
                        BackupDatabase.backup(context);
//                        if (!exitDialogOpen)
//                            splashView.gotoNextActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                splashView.dismissProgressDialog();
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
                Log.d("copyDBFile", "Populatemenu: ");
                if (ApplicationClass.getAppMode()) {
                    Log.d("copyDBFile", "Exception : ");
                    copyDBFile();
                    folder_file = new File(ApplicationClass.getStoragePath().toString() + "/FCAInternal");
                } else
                    folder_file = new File(ApplicationClass.foundationPath + "/.FCA/");
                if (folder_file.exists()) {
                    Log.d("-CT-", "doInBackground ApplicationClass.contentSDPath: " + ApplicationClass.contentSDPath);
                    db_file = new File(folder_file + "/" + DB_NAME);
//                    db_file = new File(folder_file.getAbsolutePath() + "/" + AppDatabase.DB_NAME);
                    if (db_file.exists()) {
                        SQLiteDatabase db = SQLiteDatabase.openDatabase(db_file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
                        if (db != null) {
                            Cursor content_cursor;
                            try {
                                Log.d("copyDBFile", "Content Data Copy Start: ");
                                content_cursor = db.rawQuery("SELECT * FROM ContentTable", null);
                                //populate contents
                                Log.d("copyDBFile", "Content Data Copy Start: " + content_cursor.getCount());
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
                                        detail.setVersion(content_cursor.getString(content_cursor.getColumnIndex("version")));
//                                        detail.setProgramid(content_cursor.getString(content_cursor.getColumnIndex("programid")));
                                        detail.setOrigNodeVersion(content_cursor.getString(content_cursor.getColumnIndex("origNodeVersion")));
                                        detail.setSubject(content_cursor.getString(content_cursor.getColumnIndex("subject")));
                                        detail.setSeq_no(content_cursor.getInt(content_cursor.getColumnIndex("seq_no")));
                                        detail.setStudentId("" + content_cursor.getString(content_cursor.getColumnIndex("studentId")));
                                        detail.setOnSDCard(false);
                                        contents.add(detail);
                                        content_cursor.moveToNext();
                                    }
                                }
                                AppDatabase.getDatabaseInstance(context).getContentTableDao().addContentList(contents);
                                content_cursor.close();
                                Cursor content_cursor2;
                                content_cursor2 = db.rawQuery("SELECT * FROM CourseEnrolled Where sentFlag=0", null);
                                //populate contents
                                List<Model_CourseEnrollment> courseEnrollmentList = new ArrayList<>();
                                if (content_cursor2.moveToFirst()) {
                                    while (!content_cursor2.isAfterLast()) {
                                        Model_CourseEnrollment detail = new Model_CourseEnrollment();
                                        detail.setCourseId("" + content_cursor2.getString(content_cursor2.getColumnIndex("courseId")));
                                        detail.setGroupId("" + content_cursor2.getString(content_cursor2.getColumnIndex("groupId")));
                                        detail.setPlanFromDate("" + content_cursor2.getString(content_cursor2.getColumnIndex("planFromDate")));
                                        detail.setPlanToDate("" + content_cursor2.getString(content_cursor2.getColumnIndex("planToDate")));
                                        detail.setCoachVerified(content_cursor2.getInt(content_cursor2.getColumnIndex("coachVerified")));
                                        detail.setCoachVerificationDate("" + content_cursor2.getString(content_cursor2.getColumnIndex("coachVerificationDate")));
                                        detail.setCourseExperience("" + content_cursor2.getString(content_cursor2.getColumnIndex("courseExperience")));
                                        detail.setCourse_status("" + content_cursor2.getString(content_cursor2.getColumnIndex("courseCompleted")));
                                        detail.setCoachImage("" + content_cursor2.getString(content_cursor2.getColumnIndex("coachImage")));
                                        detail.setLanguage("" + content_cursor2.getString(content_cursor2.getColumnIndex("language")));
                                        detail.setSentFlag(content_cursor2.getInt(content_cursor2.getColumnIndex("sentFlag")));
                                        courseEnrollmentList.add(detail);
                                        content_cursor2.moveToNext();
                                    }
                                }
                                AppDatabase.getDatabaseInstance(context).getCourseDao().insertListCourse(courseEnrollmentList);
                                FastSave.getInstance().saveString(CURRENT_VERSION, "" + FC_Utility.getCurrentVersion(context));
                                Log.d("-CT-", "populateMenu_New END");
                                content_cursor2.close();
                                FastSave.getInstance().saveBoolean(FC_Constants.KEY_MENU_COPIED, true);
                                FastSave.getInstance().saveString(CURRENT_VERSION, "" + FC_Utility.getCurrentVersion(context));
                                Log.d("copyDBFile", "Content Data Copy complete: ");
                            } catch (Exception e) {
                                Log.d("copyDBFile", "Content excep: ");
                                e.printStackTrace();
                            }
                        }
                    }
                } else
                    FastSave.getInstance().saveBoolean(FC_Constants.KEY_MENU_COPIED, true);
            }
            context.startService(new Intent(context, AppExitService.class));
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get current location
    @Override
    public void requestLocation() {
        new LocationService(context).checkLocation();
    }

    private void setAppVersion(Status status) {
        if (AppDatabase.getDatabaseInstance(context).getStatusDao().getKey("apkVersion") == null) {
            status = new Status();

            status.setStatusKey("apkVersion");
            String verCode = FC_Utility.getAppVerison();
            status.setValue(verCode);
            AppDatabase.getDatabaseInstance(context).getStatusDao().insert(status);

        } else {
            status.setStatusKey("apkVersion");

            String verCode = FC_Utility.getAppVerison();
            status.setValue(verCode);
            AppDatabase.getDatabaseInstance(context).getStatusDao().insert(status);

        }
    }

    @Override
    public void updateVersionApp() {
        try {
            if (AppDatabase.getDatabaseInstance(context).getStatusDao().getKey("apkVersion") != null) {
                String verCode = FC_Utility.getAppVerison();
                AppDatabase.getDatabaseInstance(context).getStatusDao().updateValue("apkVersion", verCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            AppDatabase.getDatabaseInstance(context).getStatusDao().insert(status);
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
            AppDatabase.getDatabaseInstance(context).getStatusDao().insert(status);
        }
    }


    //add start time of app
    @SuppressLint("StaticFieldLeak")
    private void addStartTime() {
        new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    String appStartTime = FC_Utility.getCurrentDateTime();
                    StatusDao statusDao = AppDatabase.getDatabaseInstance(context).getStatusDao();
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


    //checks current version and playstore version
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
            fpath = ApplicationClass.getStoragePath().getAbsolutePath();
        FC_Constants.SD_DB = false;

        File file;
        if (ApplicationClass.getAppMode()) {
            file = new File(fpath + "/.FCA/foundation_db");
            if (file.exists()) {
                FC_Constants.SD_DB = true;
                ApplicationClass.contentSDPath = fpath;
                ApplicationClass.contentExistOnSD = true;
                Log.d("getSD", "getSdCardPath: " + ApplicationClass.contentSDPath);
//            FC_Constants.SD_CARD_Content = true;
                return true;
            } else {
                ApplicationClass.contentExistOnSD = false;
//            FC_Constants.SD_CARD_Content = false;
                return false;
            }
        } else {
            ApplicationClass.contentSDPath = fpath;
            return false;
        }
    }

    @Override
    public void populateAssetsMenu() {
        try {
            FastSave.getInstance().saveBoolean(FC_Constants.NEW_ASSET_DB, true);
//            ApplicationClass.foundationPath + "/.FCA/"
            AssetManager assetManager = context.getAssets();
            InputStream in = assetManager.open("foundation_db_new");
            OutputStream out = new FileOutputStream(ApplicationClass.foundationPath + "/.FCA/foundation_db_new");
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
                db_file = new File(folder_file + "/foundation_db_new");
//                    db_file = new File(folder_file.getAbsolutePath() + "/" + AppDatabase.DB_NAME);
                if (db_file.exists()) {
                    SQLiteDatabase db = SQLiteDatabase.openDatabase(db_file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
                    if (db != null) {
                        Cursor newContent_cursor;
                        try {
                            newContent_cursor = db.rawQuery("SELECT * FROM ContentTable", null);
                            //populate contents
                            List<ContentTable> contents = new ArrayList<>();
                            if (newContent_cursor.moveToFirst()) {
                                while (!newContent_cursor.isAfterLast()) {
                                    ContentTable detail = new ContentTable();
                                    detail.setNodeId(newContent_cursor.getString(newContent_cursor.getColumnIndex("nodeId")));
                                    detail.setNodeType(newContent_cursor.getString(newContent_cursor.getColumnIndex("nodeType")));
                                    detail.setNodeTitle(newContent_cursor.getString(newContent_cursor.getColumnIndex("nodeTitle")));
                                    detail.setNodeKeywords(newContent_cursor.getString(newContent_cursor.getColumnIndex("nodeKeywords")));
                                    detail.setNodeAge(newContent_cursor.getString(newContent_cursor.getColumnIndex("nodeAge")));
                                    detail.setNodeDesc(newContent_cursor.getString(newContent_cursor.getColumnIndex("nodeDesc")));
                                    detail.setNodeServerImage(newContent_cursor.getString(newContent_cursor.getColumnIndex("nodeServerImage")));
                                    detail.setNodeImage(newContent_cursor.getString(newContent_cursor.getColumnIndex("nodeImage")));
                                    detail.setResourceId(newContent_cursor.getString(newContent_cursor.getColumnIndex("resourceId")));
                                    detail.setResourceType(newContent_cursor.getString(newContent_cursor.getColumnIndex("resourceType")));
                                    detail.setResourcePath(newContent_cursor.getString(newContent_cursor.getColumnIndex("resourcePath")));
                                    detail.setLevel("" + newContent_cursor.getString(newContent_cursor.getColumnIndex("level")));
                                    detail.setContentLanguage(newContent_cursor.getString(newContent_cursor.getColumnIndex("contentLanguage")));
                                    detail.setParentId(newContent_cursor.getString(newContent_cursor.getColumnIndex("parentId")));
                                    detail.setContentType(newContent_cursor.getString(newContent_cursor.getColumnIndex("contentType")));
                                    detail.setIsDownloaded("" + newContent_cursor.getString(newContent_cursor.getColumnIndex("isDownloaded")));
                                    detail.setVersion(newContent_cursor.getString(newContent_cursor.getColumnIndex("version")));
//                                    detail.setProgramid(newContent_cursor.getString(newContent_cursor.getColumnIndex("programid")));
                                    detail.setOrigNodeVersion(newContent_cursor.getString(newContent_cursor.getColumnIndex("origNodeVersion")));
                                    detail.setSubject(newContent_cursor.getString(newContent_cursor.getColumnIndex("subject")));
                                    detail.setSeq_no(newContent_cursor.getInt(newContent_cursor.getColumnIndex("seq_no")));
                                    detail.setStudentId("" + newContent_cursor.getString(newContent_cursor.getColumnIndex("studentId")));
                                    detail.setOnSDCard(true);
                                    contents.add(detail);
                                    newContent_cursor.moveToNext();
                                }
                            }
                            AppDatabase.getDatabaseInstance(context).getContentTableDao().deleteAll();
                            AppDatabase.getDatabaseInstance(context).getContentTableDao().addContentList(contents);
                            newContent_cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            FastSave.getInstance().saveBoolean(FC_Constants.INITIAL_SD_COPIED, true);
            FastSave.getInstance().saveBoolean(FC_Constants.NEW_ASSET_DB, true);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

