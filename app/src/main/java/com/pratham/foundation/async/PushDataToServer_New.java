package com.pratham.foundation.async;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.Attendance;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.Crl;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.database.domain.Modal_RaspFacility;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.database.domain.Session;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.database.domain.SupervisorData;
import com.pratham.foundation.modalclasses.Image_Upload;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

@EBean
public class PushDataToServer_New {

    private Context context;
    private JSONArray scoreData;
    private JSONArray attendanceData;
    private JSONArray studentData;
    private JSONArray crlData;
    private JSONArray sessionData;
    private JSONArray supervisorData;
    private JSONArray groupsData;
    private JSONArray assessmentData;
    private JSONArray contentProgress;
    private JSONArray keyWordsData;
    private JSONArray logsData;
    private boolean pushSuccessfull = false, pushImageSuccessfull = false;
    private int totalImages, imageUploadCnt, scoreLen = 0;
    private String actPhotoPath = "";
    private File[] imageFilesArray;
    private List<Image_Upload> imageUploadList;
    private Boolean isConnectedToRasp = false;
    boolean isRaspberry = false;
    private String programID = "";
    public TextView dialog_file_name;
    CustomLodingDialog pushDialog;
    LottieAnimationView push_lottie;
    TextView txt_push_dialog_msg;
    TextView txt_push_error;
    RelativeLayout rl_btn;
    Button ok_btn, eject_btn;


    public PushDataToServer_New(Context context) {
        this.context = context;
        scoreData = new JSONArray();
        attendanceData = new JSONArray();
        crlData = new JSONArray();
        sessionData = new JSONArray();
        supervisorData = new JSONArray();
        groupsData = new JSONArray();
        logsData = new JSONArray();
        studentData = new JSONArray();
        assessmentData = new JSONArray();
        contentProgress = new JSONArray();
        imageUploadList = new ArrayList<>();
    }

    @Background
    public void startDataPush(Context context) {
        this.context = context;
        if (ApplicationClass.isTablet)
            showPushDialog(context);
        try {
            setMainTextToDialog("Collecting Data...");
            List<Score> scoreList = AppDatabase.getDatabaseInstance(context).getScoreDao().getAllPushScores();
            scoreData = fillScoreData(scoreList);
            List<Attendance> attendanceList = AppDatabase.getDatabaseInstance(context).getAttendanceDao().getAllPushAttendanceEntries();
            attendanceData = fillAttendanceData(attendanceList);
            List<Student> studentList = AppDatabase.getDatabaseInstance(context).getStudentDao().getAllStudents();
            studentData = fillStudentData(studentList);
            List<Crl> crlList = AppDatabase.getDatabaseInstance(context).getCrlDao().getAllCrls();
            crlData = fillCrlData(crlList);
            List<Session> sessionList = AppDatabase.getDatabaseInstance(context).getSessionDao().getAllNewSessions();
            sessionData = fillSessionData(sessionList);
            List<SupervisorData> supervisorDataList = AppDatabase.getDatabaseInstance(context).getSupervisorDataDao().getAllSupervisorData();
            supervisorData = fillSupervisorData(supervisorDataList);
            List<Modal_Log> logsList = AppDatabase.getDatabaseInstance(context).getLogsDao().getPushAllLogs();
            logsData = fillLogsData(logsList);
            List<Assessment> assessmentList = AppDatabase.getDatabaseInstance(context).getAssessmentDao().getAllAssessment();
            assessmentData = fillAssessmentData(assessmentList);
            List<Groups> groupsList = AppDatabase.getDatabaseInstance(context).getGroupsDao().getAllGroups();
            groupsData = fillGroupsData(groupsList);
            List<ContentProgress> contentProgressList = AppDatabase.getDatabaseInstance(context).getContentProgressDao().getAllContentNodeProgress();
            contentProgress = fillProgressData(contentProgressList);
            List<KeyWords> keyWordsList = AppDatabase.getDatabaseInstance(context).getKeyWordDao().getAllData();
            keyWordsData = fillkeyWordsData(keyWordsList);

            JSONObject pushDataJsonObject = generateRequestString(scoreData, attendanceData, sessionData, supervisorData, logsData, assessmentData, studentData, contentProgress, keyWordsData);
            pushSuccessfull = false;
            //iterate through all new sessions
            totalImages = AppDatabase.getDatabaseInstance(context).getScoreDao().getUnpushedImageCount();
            imageUploadCnt = 0;
            imageUploadList = new ArrayList<>();
            isConnectedToRasp = false;
            if (ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
                if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_KOLIBRI_HOTSPOT)) {
                    getFacilityId(pushDataJsonObject);
                } else {
                    isConnectedToRasp = false;
                    pushDataToServer(context, pushDataJsonObject, ApplicationClass.uploadDataUrl);
                }
            } else {
                isConnectedToRasp = false;
                pushDataToServer(context, pushDataJsonObject, ApplicationClass.uploadDataUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void setMainTextToDialog(String dialogMsg) {
        if (ApplicationClass.isTablet)
            txt_push_dialog_msg.setText("" + dialogMsg);
    }

    @UiThread
    public void setSubTextToDialog(String dialogMsg) {
        if (ApplicationClass.isTablet) {
            txt_push_error.setVisibility(View.VISIBLE);
            txt_push_error.setText("" + dialogMsg);
        }
    }

    @UiThread
    public void showPushDialog(Context context) {
        if (ApplicationClass.isTablet) {
            pushDialog = new CustomLodingDialog(context);
            pushDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pushDialog.setContentView(R.layout.app_send_success_dialog);
            Objects.requireNonNull(pushDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pushDialog.setCancelable(false);
            pushDialog.setCanceledOnTouchOutside(false);
            pushDialog.show();

            push_lottie = pushDialog.findViewById(R.id.push_lottie);
            txt_push_dialog_msg = pushDialog.findViewById(R.id.txt_push_dialog_msg);
            txt_push_error = pushDialog.findViewById(R.id.txt_push_error);
            rl_btn = pushDialog.findViewById(R.id.rl_btn);
            ok_btn = pushDialog.findViewById(R.id.ok_btn);
            eject_btn = pushDialog.findViewById(R.id.eject_btn);

            txt_push_error.setText("");
            txt_push_error.setVisibility(View.GONE);
            ok_btn.setVisibility(View.GONE);
            eject_btn.setVisibility(View.GONE);

            ok_btn.setOnClickListener(v -> {
                if (!isConnectedToRasp)
                    getImageList();
                else
                    pushDialog.dismiss();
            });

            eject_btn.setOnClickListener(v -> {
                pushDialog.dismiss();
            });
        }
    }

    public void pushDataToServer(Context context, JSONObject data, String url) {
        try {
            AndroidNetworking.post(url)
                    .addHeaders("Content-Type", "application/json")
                    .addJSONObjectBody(data)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equalsIgnoreCase("success")) {
                                Log.d("PushData", "Data pushed successfully");
                                pushSuccessfull = true;
                                setDataPushSuccessfull();
                            } else {
                                pushSuccessfull = false;
                                setDataPushFailed();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("PushData", "Data push failed");
                            pushSuccessfull = false;
                            setDataPushFailed();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void setDataPushSuccessfull() {
        if (ApplicationClass.isTablet) {
            setPushFlag();
            setMainTextToDialog("Data pushed successfully\n Score Count : " + scoreData.length() +
                    "\n\nNow Upload Media..");
            ok_btn.setText("OK");
            ok_btn.setVisibility(View.VISIBLE);
        } else {
            if (!isConnectedToRasp)
                getImageList();
        }
    }

    @UiThread
    public void hideOKBtn() {
        if (ApplicationClass.isTablet) {
            ok_btn.setText("OK");
            ok_btn.setVisibility(View.GONE);
        }
    }

    @UiThread
    public void setDataPushFailed() {
        if (ApplicationClass.isTablet) {
            setMainTextToDialog("OOPS...");
            setSubTextToDialog("Data pushed failed");
            push_lottie.setAnimation("error_cross.json");
            push_lottie.playAnimation();
            ok_btn.setText("OK");
            ok_btn.setVisibility(View.VISIBLE);
        } else {
            if (!isConnectedToRasp)
                getImageList();
        }
    }

    @Background
    public void setPushFlag() {
        AppDatabase.getDatabaseInstance(context).getLogsDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getSessionDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getAttendanceDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getScoreDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getAssessmentDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getSupervisorDataDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getStudentDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getKeyWordDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getContentProgressDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getLogsDao().deletePushedLogs();
    }

    @Background
    public void getImageList() {
        setMainTextToDialog("Collecting Media");
        hideOKBtn();
        actPhotoPath = Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/ActivityPhotos/";
        Log.d("PushData", "Path: " + actPhotoPath);
        File directory = new File(actPhotoPath);
        imageFilesArray = directory.listFiles();
        Log.d("PushData", "Size: " + imageFilesArray.length);

        for (int index = 0; index < imageFilesArray.length; index++) {
            if (imageFilesArray[index].exists() && imageFilesArray[index].isDirectory()) {
                Log.d("PushData", "FolderName:" + imageFilesArray[index].getName());
                File activityPhotosFile = new File(imageFilesArray[index].getAbsolutePath());
                File[] file = activityPhotosFile.listFiles();
                if (file.length > 0) {
                    for (int i = 0; i < file.length; i++) {
                        if (file[i].exists() && file[i].isFile()) {
                            String fName = file[i].getName();
                            File fPath = new File(file[i].getAbsolutePath());
                            Log.d("PushData", "FileName:" + fName);
                            if (AppDatabase.getDatabaseInstance(context).getScoreDao().getSentFlag(fName) == 0) {
                                Image_Upload image_upload = new Image_Upload();
                                image_upload.setFileName(fName);
                                image_upload.setFilePath(fPath);
                                image_upload.setUploadStatus(false);
                                imageUploadList.add(image_upload);
                            }
                        }
                    }
                }
            } else if (imageFilesArray[index].exists() && imageFilesArray[index].isFile()) {
                File fPath = new File(imageFilesArray[index].getAbsolutePath());
                String fName = imageFilesArray[index].getName();
                if (AppDatabase.getDatabaseInstance(context).getScoreDao().getSentFlag(fName) == 0) {
                    Log.d("PushData", "FileName:" + imageFilesArray[index].getName());
                    Image_Upload image_upload = new Image_Upload();
                    image_upload.setFileName(fName);
                    image_upload.setFilePath(fPath);
                    image_upload.setUploadStatus(false);
                    imageUploadList.add(image_upload);
                }
            }
        }

        imageUploadCnt = 0;
        totalImages = imageUploadList.size() - 1;
        Log.d("PushData", "Size: " + imageFilesArray.length);
        if (imageUploadList.size() > 0) {
            setMainTextToDialog("Uploading " + totalImages + " images.");
            pushImagesToServer(0);
        } else {
            showTotalImgStatus();
        }
    }

    @UiThread
    public void updateCntr(int imgCtr) {
        if (ApplicationClass.isTablet)
            dialog_file_name.setText("Uploading " + imgCtr + "/" + totalImages);
    }

    @UiThread
    public void pushImagesToServer(final int jsonIndex) {
        Log.d("PushData", "Image onResponse : " + jsonIndex);
        if (jsonIndex < imageUploadList.size()) {
            AndroidNetworking.upload(FC_Constants.PUSH_IMAGE_API)
                    .addMultipartFile(imageUploadList.get(jsonIndex).getFileName(),
                            imageUploadList.get(jsonIndex).getFilePath())
                    .addHeaders("Content-Type", "images")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("PushData", "Image onResponse : " + response);
                                if (response.equalsIgnoreCase("success")) {
                                    imageUploadCnt++;
                                    Log.d("PushData", "imageUploadCnt : " + imageUploadCnt);
                                    imageUploadList.get(jsonIndex).setUploadStatus(true);
                                    pushImagesToServer(jsonIndex + 1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            pushImagesToServer(jsonIndex + 1);
                        }
                    });
        } else {
            Log.d("PushData", "Before onPostImageExecute");
            showTotalImgStatus();
        }
    }

    @UiThread
    public void showTotalImgStatus() {
        int successfulCntr = 0, failedCntr = 0;

        for (int i = 0; i < imageUploadList.size(); i++) {
            if (imageUploadList.get(i).isUploadStatus()) {
                AppDatabase.getDatabaseInstance(context).getScoreDao().setImgSentFlag(imageUploadList.get(i).getFileName());
                successfulCntr++;
            } else
                failedCntr++;
        }

        if (ApplicationClass.isTablet) {
            ok_btn.setVisibility(View.GONE);
            eject_btn.setText("Close");
            eject_btn.setVisibility(View.VISIBLE);

            push_lottie.setAnimation("lottie_correct.json");
            push_lottie.playAnimation();
            setMainTextToDialog("Upload Complete");
            setSubTextToDialog("Score Count : " + scoreData.length() + "\nImages Successful : "
                    + successfulCntr + "\nImages Failed : " + failedCntr);
        }
    }

    @Background
    public void getFacilityId(JSONObject requestString) {
        try {
            JSONObject object = new JSONObject();
            object.put("username", "pratham");
            object.put("password", "pratham");
            AndroidNetworking.post(FC_Constants.RASP_IP + "/api/session/")
                    .addHeaders("Content-Type", "application/json")
                    .addJSONObjectBody(object)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            Modal_RaspFacility facility = gson.fromJson(response, Modal_RaspFacility.class);
                            FastSave.getInstance().saveString(FC_Constants.FACILITY_ID, facility.getFacilityId());
                            Log.d("pi", "onResponse: " + facility.getFacilityId());
                            isConnectedToRasp = true;
                            pushDataToRaspberry("" + FC_Constants.URL.DATASTORE_RASPBERY_URL.toString(),
                                    "" + requestString, programID, FC_Constants.USAGEDATA);
//                            try {
//                                getRaspImageList();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                        }

                        @Override
                        public void onError(ANError anError) {
//                            apiResult.notifyError(requestType/*, null*/);
                            setDataPushFailed();
                            isConnectedToRasp = false;
                            Log.d("Error::", anError.getErrorDetail());
                            Log.d("Error::", anError.getMessage());
                            Log.d("Error::", anError.getResponse().toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAuthHeader() {
        String encoded = Base64.encodeToString(("pratham" + ":" + "pratham").getBytes(), Base64.NO_WRAP);
        return "Basic " + encoded;
    }

    public void pushDataToRaspberry(String url, String data, String filter_name, String table_name) {
        String authHeader = getAuthHeader();
        AndroidNetworking.post(url)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", authHeader)
                .addBodyParameter("filter_name", filter_name)
                .addBodyParameter("table_name", table_name)
                .addBodyParameter("facility", FastSave.getInstance().getString(FC_Constants.FACILITY_ID, ""))
                .addBodyParameter("data", data)
                .setExecutor(Executors.newSingleThreadExecutor())
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Raspberry Success : ", "onResponse: " + response);
                        if (response.equalsIgnoreCase("success")) {
                            pushSuccessfull = true;
                            setPushFlag();
                            BackupDatabase.backup(ApplicationClass.getInstance());
                            setDataPushSuccessfull();
                        } else {
                            pushSuccessfull = false;
                            setDataPushFailed();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        pushSuccessfull = false;
                        setDataPushFailed();
                        Log.d("Raspberry Error::", anError.getErrorDetail());
                        Log.d("Raspberry Error::", anError.getMessage());
                        Log.d("Raspberry Error::", anError.getResponse().toString());
                    }
                });
    }

    private JSONObject generateRequestString(JSONArray scoreData, JSONArray attendanceData, JSONArray sessionData, JSONArray supervisorData, JSONArray logsData, JSONArray assessmentData, JSONArray studentData, JSONArray contentProgress, JSONArray keyWordsData) {
//        String requestString = "";
        JSONObject pushJsonObject = new JSONObject();

        try {
            JSONObject sessionObj = new JSONObject();
            JSONObject metaDataObj = new JSONObject();
            metaDataObj.put("ScoreCount", scoreData.length());

            metaDataObj.put("CRLID", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("CRLID"));
            metaDataObj.put("group1", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("group1"));
            metaDataObj.put("group2", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("group2"));
            metaDataObj.put("group3", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("group3"));
            metaDataObj.put("group4", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("group4"));
            metaDataObj.put("group5", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("group5"));
            metaDataObj.put("DeviceId", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceId"));
            metaDataObj.put("DeviceName", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceName"));
            metaDataObj.put("ActivatedDate", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("ActivatedDate"));
            metaDataObj.put("village", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("village"));
            metaDataObj.put("ActivatedForGroups", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("ActivatedForGroups"));
            metaDataObj.put("SerialID", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("SerialID"));
            metaDataObj.put("gpsFixDuration", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("gpsFixDuration"));
            metaDataObj.put("prathamCode", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("prathamCode"));
            metaDataObj.put("programId", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("programId"));
            metaDataObj.put("WifiMAC", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("wifiMAC"));
            if (ApplicationClass.isTablet)
                metaDataObj.put("apkType", "Tablet");
            else
                metaDataObj.put("apkType", "SmartPhone");
            metaDataObj.put("appName", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("appName"));
            metaDataObj.put("apkVersion", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("apkVersion"));
            metaDataObj.put("GPSDateTime", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("GPSDateTime"));
            metaDataObj.put("Latitude", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("Latitude"));
            metaDataObj.put("Longitude", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("Longitude"));
            metaDataObj.put("AndroidVersion", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("AndroidVersion"));
            metaDataObj.put("InternalAvailableStorage", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("InternalAvailableStorage"));
            metaDataObj.put("DeviceManufacturer", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceManufacturer"));
            metaDataObj.put("DeviceModel", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceModel"));
            metaDataObj.put("ScreenResolution", AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("ScreenResolution"));

            sessionObj.put("scoreData", scoreData);
            if (!ApplicationClass.isTablet)
                sessionObj.put("studentData", studentData);
            sessionObj.put("attendanceData", attendanceData);
            sessionObj.put("sessionsData", sessionData);
            sessionObj.put("keyWords", keyWordsData);
            sessionObj.put("contentProgressData", contentProgress);
            sessionObj.put("logsData", logsData);
            sessionObj.put("assessmentData", assessmentData);
            sessionObj.put("supervisor", supervisorData);

            pushJsonObject.put("session", sessionObj);
            pushJsonObject.put("metadata", metaDataObj);

//            requestString = "{ \"session\": " + sessionObj +
//                    ", \"metadata\": " + metaDataObj +
//                    "}";
            return pushJsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return pushJsonObject = new JSONObject();
        }
    }

    private JSONArray fillSessionData(List<Session> sessionList) {
        JSONArray newSessionsData = new JSONArray();
        JSONObject _sessionObj;
        try {
            for (int i = 0; i < sessionList.size(); i++) {
                _sessionObj = new JSONObject();
                _sessionObj.put("SessionID", sessionList.get(i).getSessionID());
                _sessionObj.put("fromDate", sessionList.get(i).getFromDate());
                _sessionObj.put("toDate", sessionList.get(i).getToDate());
                newSessionsData.put(_sessionObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newSessionsData;
    }

    private JSONArray fillCrlData(List<Crl> crlsList) {
        JSONArray crlsData = new JSONArray();
        JSONObject _crlObj;
        try {
            for (int i = 0; i < crlsList.size(); i++) {
                _crlObj = new JSONObject();
                _crlObj.put("CRLId", crlsList.get(i).getCRLId());
                _crlObj.put("FirstName", crlsList.get(i).getFirstName());
                _crlObj.put("LastName", crlsList.get(i).getLastName());
                _crlObj.put("UserName", crlsList.get(i).getUserName());
                _crlObj.put("UserName", crlsList.get(i).getUserName());
                _crlObj.put("Password", crlsList.get(i).getPassword());
                _crlObj.put("ProgramId", crlsList.get(i).getProgramId());
                _crlObj.put("Mobile", crlsList.get(i).getMobile());
                _crlObj.put("State", crlsList.get(i).getState());
                _crlObj.put("Email", crlsList.get(i).getEmail());
                _crlObj.put("CreatedBy", crlsList.get(i).getCreatedBy());
                _crlObj.put("newCrl", !crlsList.get(i).isNewCrl());
                crlsData.put(_crlObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return crlsData;
    }

    private JSONArray fillStudentData(List<Student> studentList) {
        JSONArray studentData = new JSONArray();
        JSONObject _studentObj;
        try {
            for (int i = 0; i < studentList.size(); i++) {
                _studentObj = new JSONObject();
                _studentObj.put("StudentID", studentList.get(i).getStudentID());
                _studentObj.put("StudentUID", studentList.get(i).getStudentUID());
                _studentObj.put("FirstName", studentList.get(i).getFirstName());
                _studentObj.put("MiddleName", studentList.get(i).getMiddleName());
                _studentObj.put("LastName", studentList.get(i).getLastName());
                _studentObj.put("FullName", studentList.get(i).getFullName());
                _studentObj.put("Gender", studentList.get(i).getGender());
                _studentObj.put("regDate", studentList.get(i).getRegDate());
                _studentObj.put("Age", studentList.get(i).getAge());
                _studentObj.put("villageName", studentList.get(i).getVillageName());
                _studentObj.put("newFlag", studentList.get(i).getNewFlag());
                _studentObj.put("DeviceId", studentList.get(i).getDeviceId());
                studentData.put(_studentObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return studentData;
    }

    private JSONArray fillAttendanceData(List<Attendance> attendanceList) {
        JSONArray attendanceData = new JSONArray();
        JSONObject _obj;
        try {
            for (int i = 0; i < attendanceList.size(); i++) {
                _obj = new JSONObject();
                Attendance _attendance = attendanceList.get(i);
                _obj.put("attendanceID", _attendance.getAttendanceID());
                _obj.put("SessionID", _attendance.getSessionID());
                _obj.put("StudentID", _attendance.getStudentID());
                attendanceData.put(_obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return attendanceData;
    }

    private JSONArray fillScoreData(List<Score> scoreList) {
        JSONArray scoreData = new JSONArray();
        JSONObject _obj;
        try {
            for (int i = 0; i < scoreList.size(); i++) {
                _obj = new JSONObject();
                Score _score = scoreList.get(i);
                _obj.put("ScoreId", _score.getScoreId());
                _obj.put("SessionID", _score.getSessionID());
                _obj.put("StudentID", _score.getStudentID());
                _obj.put("DeviceID", _score.getDeviceID());
                _obj.put("ResourceID", _score.getResourceID());
                _obj.put("QuestionId", _score.getQuestionId());
                _obj.put("ScoredMarks", _score.getScoredMarks());
                _obj.put("TotalMarks", _score.getTotalMarks());
                _obj.put("StartDateTime", _score.getStartDateTime());
                _obj.put("EndDateTime", _score.getEndDateTime());
                _obj.put("Level", _score.getLevel());
                _obj.put("Label", _score.getLabel());
                scoreData.put(_obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return scoreData;
    }

    private JSONArray fillSupervisorData(List<SupervisorData> supervisorDataList) {
        JSONArray supervisorData = new JSONArray();
        JSONObject _supervisorDataObj;
        try {
            for (int i = 0; i < supervisorDataList.size(); i++) {
                _supervisorDataObj = new JSONObject();
                SupervisorData supervisorDataTemp = supervisorDataList.get(i);
                _supervisorDataObj.put("sId", supervisorDataTemp.getsId());
                _supervisorDataObj.put("assessmentSessionId", supervisorDataTemp.getAssessmentSessionId());
                _supervisorDataObj.put("supervisorId", supervisorDataTemp.getSupervisorId());
                _supervisorDataObj.put("supervisorName", supervisorDataTemp.getSupervisorName());
                _supervisorDataObj.put("supervisorPhoto", supervisorDataTemp.getSupervisorPhoto());

                supervisorData.put(_supervisorDataObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return supervisorData;
    }

    private JSONArray fillLogsData(List<Modal_Log> logsList) {
        JSONArray logsData = new JSONArray();
        JSONObject _logsObj;
        try {
            for (int i = 0; i < logsList.size(); i++) {
                _logsObj = new JSONObject();
                Modal_Log modal_log = logsList.get(i);
                _logsObj.put("logId", modal_log.getLogId());
                _logsObj.put("deviceId", modal_log.getDeviceId());
                _logsObj.put("currentDateTime", modal_log.getCurrentDateTime());
                _logsObj.put("errorType", modal_log.getErrorType());
                _logsObj.put("exceptionMessage", modal_log.getExceptionMessage());
                _logsObj.put("exceptionStackTrace", modal_log.getExceptionStackTrace());
                _logsObj.put("groupId", modal_log.getGroupId());
                _logsObj.put("LogDetail", modal_log.getLogDetail());
                _logsObj.put("methodName", modal_log.getMethodName());

                logsData.put(_logsObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return logsData;
    }

    private JSONArray fillAssessmentData(List<Assessment> assessmentList) {
        JSONArray assessmentData = new JSONArray();
        JSONObject _assessmentobj;
        try {
            for (int i = 0; i < assessmentList.size(); i++) {
                _assessmentobj = new JSONObject();
                Assessment _Assessment = assessmentList.get(i);
                _assessmentobj.put("DeviceIDa", _Assessment.getDeviceIDa());
                _assessmentobj.put("EndDateTimea", _Assessment.getEndDateTime());
                _assessmentobj.put("Labela", _Assessment.getLabel());
                _assessmentobj.put("Levela", _Assessment.getLevela());
                _assessmentobj.put("QuestionIda", _Assessment.getQuestionIda());
                _assessmentobj.put("ResourceIDa", _Assessment.getResourceIDa());
                _assessmentobj.put("ScoredMarksa", _Assessment.getScoredMarksa());
                _assessmentobj.put("ScoreIda", _Assessment.getScoreIda());
                _assessmentobj.put("SessionIDa", _Assessment.getSessionIDa());
                _assessmentobj.put("SessionIDm", _Assessment.getSessionIDm());
                _assessmentobj.put("StartDateTimea", _Assessment.getStartDateTimea());
                _assessmentobj.put("StudentIDa", _Assessment.getStudentIDa());
                _assessmentobj.put("TotalMarksa", _Assessment.getTotalMarksa());

                assessmentData.put(_assessmentobj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return assessmentData;
    }

    private JSONArray fillGroupsData(List<Groups> groupsList) {
        JSONArray groupsData = new JSONArray();
        JSONObject _groupsObj;
        try {
            for (int i = 0; i < groupsList.size(); i++) {
                _groupsObj = new JSONObject();
                Groups group = groupsList.get(i);
                _groupsObj.put("GroupId", group.getGroupId());
                _groupsObj.put("DeviceId", group.getDeviceId());
                _groupsObj.put("GroupCode", group.getGroupCode());
                _groupsObj.put("GroupName", group.getGroupName());
                _groupsObj.put("ProgramId", group.getProgramId());
                _groupsObj.put("SchoolName", group.getSchoolName());
                _groupsObj.put("VillageId", group.getVillageId());
                _groupsObj.put("VIllageName", group.getVIllageName());

                groupsData.put(_groupsObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return groupsData;
    }

    private JSONArray fillProgressData(List<ContentProgress> contentProgressList) {
        JSONArray contentProgressArr = new JSONArray();
        JSONObject _progressObj;
        try {
            for (int i = 0; i < contentProgressList.size(); i++) {
                _progressObj = new JSONObject();
                ContentProgress contentProgress = contentProgressList.get(i);
                _progressObj.put("resourceId", contentProgress.getResourceId());
                _progressObj.put("studentId", contentProgress.getStudentId());
                _progressObj.put("sessionId", contentProgress.getSessionId());
                _progressObj.put("updatedDateTime", contentProgress.getUpdatedDateTime());
                _progressObj.put("progressPercentage", contentProgress.getProgressPercentage());
                _progressObj.put("label", contentProgress.getLabel());

                contentProgressArr.put(_progressObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return contentProgressArr;
    }

    private JSONArray fillkeyWordsData(List<KeyWords> keyWordsList) {
        JSONArray keyWordsArr = new JSONArray();
        JSONObject _keyWordsObj;
        try {
            for (int i = 0; i < keyWordsList.size(); i++) {
                _keyWordsObj = new JSONObject();
                KeyWords keyWords = keyWordsList.get(i);
                _keyWordsObj.put("resourceId", keyWords.getResourceId());
                _keyWordsObj.put("studentId", keyWords.getStudentId());
                _keyWordsObj.put("keyWord", keyWords.getKeyWord());
                _keyWordsObj.put("wordType", keyWords.getWordType());
                _keyWordsObj.put("topic", keyWords.getTopic());
                keyWordsArr.put(_keyWordsObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return keyWordsArr;
    }
}