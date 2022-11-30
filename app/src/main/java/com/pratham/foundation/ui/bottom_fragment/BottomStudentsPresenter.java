package com.pratham.foundation.ui.bottom_fragment;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.pratham.foundation.database.AppDatabase.DB_VERSION;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import com.pratham.foundation.database.domain.Attendance;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Session;
import com.pratham.foundation.database.domain.Status;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.database.domain.StudentAndGroup_BottomFragmentModal;
import com.pratham.foundation.services.AppExitService;
import com.pratham.foundation.services.LocationService;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EBean
public class BottomStudentsPresenter implements BottomStudentsContract.BottomStudentsPresenter {

    private BottomStudentsContract.BottomStudentsView myView;
    private final Context context;
    Gson gson;
    private List<Student> studentDBList;
    private List<Groups> groupDBList, groupList;
    private List<StudentAndGroup_BottomFragmentModal> fragmentModalsList;

    public BottomStudentsPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(BottomStudentsContract.BottomStudentsView viewBottomStudents) {
        this.myView = viewBottomStudents;
        gson = new Gson();
        fragmentModalsList = new ArrayList<>();
        studentDBList = new ArrayList<>();
        groupList = new ArrayList<>();
    }

    @Background
    @Override
    public void showStudents() {
        try {

            fragmentModalsList.clear();
            groupList.clear();
            myView.clearList();
            studentDBList = AppDatabase.getDatabaseInstance(context).getStudentDao().getAllPSStudents();
            groupDBList = AppDatabase.getDatabaseInstance(context).getGroupsDao().getAllGroups();

            if (studentDBList != null || groupDBList != null) {
                StudentAndGroup_BottomFragmentModal studentHeader = new StudentAndGroup_BottomFragmentModal();
                studentHeader.setStudentID("#####");
                studentHeader.setFullName("");
                studentHeader.setAvatarName("");
                studentHeader.setGroupId("");
                studentHeader.setEnrollmentID("");
                studentHeader.setType(FC_Constants.TYPE_HEADER);
                fragmentModalsList.add(studentHeader);
            }

            if (studentDBList != null) {
                for (int i = 0; i < studentDBList.size(); i++) {
                    StudentAndGroup_BottomFragmentModal studentAvatar = new StudentAndGroup_BottomFragmentModal();
                    studentAvatar.setStudentID(studentDBList.get(i).getStudentID());
                    studentAvatar.setFullName(studentDBList.get(i).getFullName());
                    studentAvatar.setAvatarName(studentDBList.get(i).getAvatarName());
                    studentAvatar.setGender(studentDBList.get(i).getGender());
                    studentAvatar.setGroupId(studentDBList.get(i).getGroupId());
//                    studentAvatar.setProgramID(studentDBList.get(i).getProgramId());
                    studentAvatar.setChecked(false);
                    studentAvatar.setEnrollmentID(studentDBList.get(i).getLastName());
                    studentAvatar.setType(FC_Constants.STUDENTS);
                    fragmentModalsList.add(studentAvatar);
                }
            }

            if (groupDBList != null) {
                for (int i = 0; i < groupDBList.size(); i++) {
                    StudentAndGroup_BottomFragmentModal studentAvatar = new StudentAndGroup_BottomFragmentModal();
                    if(groupDBList.get(i).getVIllageName()!=null && !groupDBList.get(i).getVIllageName().equalsIgnoreCase(""))
                        studentAvatar.setStudentID(groupDBList.get(i).getVIllageName());
                    else
                        studentAvatar.setStudentID(groupDBList.get(i).getGroupId());
                    studentAvatar.setFullName(groupDBList.get(i).getGroupName());
                    studentAvatar.setGroupId(groupDBList.get(i).getGroupId());
                    studentAvatar.setAvatarName("NA");
                    studentAvatar.setEnrollmentID(groupDBList.get(i).getVIllageName());
//                    studentAvatar.setProgramID(""+groupDBList.get(i).getProgramId());
                    studentAvatar.setChecked(false);
                    studentAvatar.setType(FC_Constants.GROUP_MODE);
                    fragmentModalsList.add(studentAvatar);
                }
            }

            if (fragmentModalsList != null) {
                StudentAndGroup_BottomFragmentModal studentHeader = new StudentAndGroup_BottomFragmentModal();
                studentHeader.setStudentID("#####");
                studentHeader.setFullName("");
                studentHeader.setAvatarName("");
                studentHeader.setGroupId("");
                studentHeader.setEnrollmentID("");
                studentHeader.setType(FC_Constants.TYPE_HEADER);
                fragmentModalsList.add(studentHeader);
            }
            myView.setStudentList(fragmentModalsList);
            myView.notifyStudentAdapter();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getStudentsFromGroup(String studentId) {

        fragmentModalsList.clear();
        studentDBList = AppDatabase.getDatabaseInstance(context).getStudentDao().getGroupwiseStudents(studentId);
        myView.clearList();
        if (studentDBList != null) {
            StudentAndGroup_BottomFragmentModal studentHeader = new StudentAndGroup_BottomFragmentModal();
            studentHeader.setStudentID("#####");
            studentHeader.setFullName("");
            studentHeader.setAvatarName("");
            studentHeader.setEnrollmentID("");
            studentHeader.setType(FC_Constants.TYPE_HEADER);
            fragmentModalsList.add(studentHeader);

            for (int i = 0; i < studentDBList.size(); i++) {
                StudentAndGroup_BottomFragmentModal studentAvatar = new StudentAndGroup_BottomFragmentModal();
                studentAvatar.setStudentID(studentDBList.get(i).getStudentID());
                studentAvatar.setFullName(studentDBList.get(i).getFullName());
                studentAvatar.setAvatarName(studentDBList.get(i).getAvatarName());
                studentAvatar.setGender(studentDBList.get(i).getGender());
                studentAvatar.setChecked(false);
                studentAvatar.setEnrollmentID(studentDBList.get(i).getLastName());
                studentAvatar.setType(FC_Constants.STUDENTS);
                fragmentModalsList.add(studentAvatar);
            }
        }
        StudentAndGroup_BottomFragmentModal studentHeader = new StudentAndGroup_BottomFragmentModal();
        studentHeader.setStudentID("#####");
        studentHeader.setFullName("");
        studentHeader.setAvatarName("");
        studentHeader.setEnrollmentID("");
        studentHeader.setType(FC_Constants.TYPE_FOOTER);
        fragmentModalsList.add(studentHeader);

        myView.setStudentList(fragmentModalsList);
        myView.notifyStudentAdapter();
    }

    @Background
    @Override
    public void updateStudentData() {
        try {
            AppDatabase.getDatabaseInstance(context).getStatusDao().updateValue("CurrentSession", "" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            Attendance attendance = new Attendance();
            attendance.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            attendance.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            attendance.setDate(FC_Utility.getCurrentDateTime());
            attendance.setGroupID(""+FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_GRP_ID, "NA"));
            attendance.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getAttendanceDao().insert(attendance);

            Session startSesion = new Session();
            startSesion.setSessionID("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            startSesion.setFromDate("" + FC_Utility.getCurrentDateTime());
            startSesion.setToDate("NA");
            startSesion.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getSessionDao().insert(startSesion);

            myView.gotoNext();
            if (FC_Utility.isDataConnectionAvailable(context))
                getStudentData(FC_Constants.STUDENT_PROGRESS_INTERNET, FC_Constants.STUDENT_PROGRESS_API, FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
        } catch (Exception e) {
            myView.showToast("Problem Marking Attendance");
            e.printStackTrace();
        }

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
                    AppDatabase.getDatabaseInstance(context).getContentProgressDao().addContentProgressList(contentProgressList);
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
            getStudentData(FC_Constants.LEARNT_WORDS_INTERNET, FC_Constants.LEARNT_WORDS_API, FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
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
                            AppDatabase.getDatabaseInstance(context).getKeyWordDao().insert(learntWordsList.get(i));
                    }
                    BackupDatabase.backup(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Background
    @Override
    public void populateDB() {
        try {
            Log.d("pushorassign", "populateDB() FC_Constants.INITIAL_ENTRIES : " + FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false));
            if (!FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false))
                doInitialEntries(AppDatabase.getDatabaseInstance(context));
            populateMenu();

            myView.dismissProgressDialog2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkWord(String studentId, String wordUUId, String wordCheck, String wordType) {
        try {
            String word = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkLearntData(studentId, "" + wordUUId, wordCheck.toLowerCase(), wordType);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void populateMenu() {
        try {
            File folder_file, db_file;
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
                                    detail.setIsDownloaded("true");
                                    detail.setOnSDCard(false);
                                    contents.add(detail);
                                    content_cursor.moveToNext();
                                }
                            }
                            AppDatabase.getDatabaseInstance(context).getContentTableDao().addContentList(contents);
                            content_cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            context.startService(new Intent(context, AppExitService.class));
            Log.d("pushorassign", "populateDB() FC_Constants.KEY_MENU_COPIED : " + FastSave.getInstance().getBoolean(FC_Constants.KEY_MENU_COPIED, false));
            FastSave.getInstance().saveBoolean(FC_Constants.KEY_MENU_COPIED, true);
            Log.d("pushorassign", "populateDB() FC_Constants.KEY_MENU_COPIED : " + FastSave.getInstance().getBoolean(FC_Constants.KEY_MENU_COPIED, false));
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doInitialEntries(AppDatabase appDatabase) {
        try {
            Status status;
            status = new Status();
            status.setStatusKey("DeviceId");
            status.setValue("" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            status.setDescription("" + FC_Utility.getDeviceSerialID());
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("CRLID");
            status.setValue("default");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("DeviceName");
            status.setValue(FC_Utility.getDeviceName());
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("gpsFixDuration");
            status.setValue("");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("prathamCode");
            status.setValue("");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("apkType");
            status.setValue("");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("Latitude");
            status.setValue("");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("Longitude");
            status.setValue("");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("GPSDateTime");
            status.setValue("");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("CurrentSession");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("SdCardPath");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("AppLang");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("AppStartDateTime");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            //new Entries
            status = new Status();
            status.setStatusKey("ActivatedForGroups");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("AndroidVersion");
            status.setValue(FC_Utility.getAndroidOSVersion());
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("InternalAvailableStorage");
            status.setValue(FC_Utility.getInternalStorageStatus());
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("DeviceManufacturer");
            status.setValue(FC_Utility.getDeviceManufacturer());
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("DeviceModel");
            status.setValue(FC_Utility.getDeviceModel());
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("ScreenResolution");
            status.setValue(FastSave.getInstance().getString(FC_Constants.SCR_RES, ""));
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("programId");
            status.setValue("1");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("group1");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("group2");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("group3");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("group4");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("group5");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("village");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("ActivatedDate");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("AssessmentSession");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("AndroidID");
            status.setValue("NA");
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("DBVersion");
            status.setValue(DB_VERSION);
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("SerialID");
            status.setValue(FC_Utility.getDeviceSerialID());
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            status = new Status();
            status.setStatusKey("AppBuild Date");
            status.setValue(ApplicationClass.BUILD_DATE);
            status.setDescription("");
            appDatabase.getStatusDao().insert(status);

            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            String macAddress = wInfo.getMacAddress();
            status.setStatusKey("WifiMAC");
            status.setValue(macAddress);
            status.setDescription("");
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
            status.setDescription("");
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
            status.setDescription("");
            AppDatabase.getDatabaseInstance(context).getStatusDao().insert(status);
        }
    }

    public void requestLocation() {
        new LocationService(context).checkLocation();
    }

    public void setAppVersion(Status status) {
        if (AppDatabase.getDatabaseInstance(context).getStatusDao().getKey("apkVersion") == null) {
            status = new Status();

            status.setStatusKey("apkVersion");
            String verCode = FC_Utility.getAppVerison();
            status.setValue(verCode);
            status.setDescription("");
            AppDatabase.getDatabaseInstance(context).getStatusDao().insert(status);

        } else {
            status.setStatusKey("apkVersion");

            String verCode = FC_Utility.getAppVerison();
            status.setValue(verCode);
            status.setDescription("");
            AppDatabase.getDatabaseInstance(context).getStatusDao().insert(status);

        }
    }

    @Background
    public void addStartTime() {
        try {
            String appStartTime = FC_Utility.getCurrentDateTime();
            StatusDao statusDao = AppDatabase.getDatabaseInstance(context).getStatusDao();
            statusDao.updateValue("AppStartDateTime", appStartTime);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

