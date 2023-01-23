package com.pratham.foundation.async;

import static com.pratham.foundation.ApplicationClass.BUILD_DATE;
import static com.pratham.foundation.utility.FC_Constants.IS_SERVICE_STOPED;
import static com.pratham.foundation.utility.FC_Constants.failed_ImageLength;
import static com.pratham.foundation.utility.FC_Constants.pushedScoreLength;
import static com.pratham.foundation.utility.FC_Constants.successful_ImageLength;
import static com.pratham.foundation.utility.FC_Constants.syncTime;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.gson.GsonBuilder;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Attendance;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.FilePushResponse;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.database.domain.PushResponse;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.database.domain.Session;
import com.pratham.foundation.database.domain.Status;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.modalclasses.Image_Upload;
import com.pratham.foundation.modalclasses.Model_CourseEnrollment;
import com.pratham.foundation.modalclasses.SyncLog;
import com.pratham.foundation.services.background_service.BackgroundPushService;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_RandomString;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@EBean
public class PushDataToServer_New_YN {

    private Context context;
    private JSONArray scoreData,attendanceData,studentData, sessionData,supervisorData,groupsData,
            assessmentData,contentProgressData, keyWordsData,courseEnrollmentData,logsData;
    private boolean pushSuccessfull = false;
    private boolean isConnectedToRasp = false;
    private final boolean isRaspberry = false;
    private boolean showUi = false;
    private final boolean pushImageSuccessfull = false;
    private int totalImages = 0, imageUploadCnt = 0, totalScoreCount = 0, enrollmentCount = 0;
    private final int BUFFER = 10000;
    private String actPhotoPath = "";
    private final String programID = "";
    private File[] imageFilesArray;
//    private final List<String> db_Media;
    private List<Image_Upload> imageUploadList;
    public Gson gson;
    CustomLodingDialog pushDialog;
    LottieAnimationView push_lottie;
    TextView txt_push_dialog_msg, txt_push_error, txt_date, dialog_file_name;
    RelativeLayout rl_btn;
    Button ok_btn, eject_btn;

    public PushDataToServer_New_YN(Context context) {
        this.context = context;
        scoreData = new JSONArray();
        attendanceData = new JSONArray();
        sessionData = new JSONArray();
        supervisorData = new JSONArray();
        groupsData = new JSONArray();
        logsData = new JSONArray();
        studentData = new JSONArray();
        assessmentData = new JSONArray();
        contentProgressData = new JSONArray();
        imageUploadList = new ArrayList<>();
//        db_Media = new ArrayList<>();
        courseEnrollmentData = new JSONArray();
        keyWordsData = new JSONArray();
        showUi = false;
    }

    /**
     * This method begins the process of pushing data to server.
     * Locally stored data is collected and added to its respective JsonArray defined globally above.
     *
     * @param showUi is used to show the push Dialog.
     */
    @Background
    public void startDataPush(Context context, boolean showUi) {
        if (FC_Utility.isDataConnectionAvailable(context)) {
            this.context = context;
            this.showUi = showUi;
            scoreData = new JSONArray();
            attendanceData = new JSONArray();
            sessionData = new JSONArray();
            supervisorData = new JSONArray();
            groupsData = new JSONArray();
            logsData = new JSONArray();
            studentData = new JSONArray();
            assessmentData = new JSONArray();
            contentProgressData = new JSONArray();
            imageUploadList = new ArrayList<>();
//            db_Media = new ArrayList<>();
            courseEnrollmentData = new JSONArray();
            keyWordsData = new JSONArray();

            gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create();

            FastSave.getInstance().saveString(FC_Constants.PUSH_ID_LOGS, "" + FC_Utility.getUUID());
            //Show Dialog
            if (showUi)
                showPushDialog(context);
            //Here data is fetched from local database and added to a list and then passed to JsonArray.
            syncTime = FC_Utility.getCurrentDateTime();
            try {
                setGreenColorMainTextToDialog();
                setMainTextToDialog(context.getResources().getString(R.string.Collecting_Data));

                List<Score> scoreList = AppDatabase.getDatabaseInstance(context).getScoreDao().getAllPushScores();
                for (final Score score : scoreList)
                    scoreData.put(new JSONObject(gson.toJson(score)));

                List<Attendance> attendanceList = AppDatabase.getDatabaseInstance(context).getAttendanceDao().getAllPushAttendanceEntries();
                for (final Attendance attendance : attendanceList)
                    attendanceData.put(new JSONObject(gson.toJson(attendance)));

                List<Student> studentList = AppDatabase.getDatabaseInstance(context).getStudentDao().getAllSyncStudents();
                for (final Student student : studentList)
                    studentData.put(new JSONObject(gson.toJson(student)));

                List<Session> sessionList = AppDatabase.getDatabaseInstance(context).getSessionDao().getAllNewSessions();
                for (final Session session : sessionList)
                    sessionData.put(new JSONObject(gson.toJson(session)));

//                List<SupervisorData> supervisorDataList = AppDatabase.getDatabaseInstance(context).getSupervisorDataDao().getAllSupervisorData();
//                for (final SupervisorData supervisorData1 : supervisorDataList)
//                    supervisorData.put(new JSONObject(gson.toJson(supervisorData1)));
//
//                List<Assessment> assessmentList = AppDatabase.getDatabaseInstance(context).getAssessmentDao().getAllAssessment();
//                for (final Assessment assessment : assessmentList)
//                    assessmentData.put(new JSONObject(gson.toJson(assessment)));

                List<Modal_Log> logsList = AppDatabase.getDatabaseInstance(context).getLogsDao().getPushAllLogs();
                for (final Modal_Log modal_log : logsList)
                    logsData.put(new JSONObject(gson.toJson(modal_log)));

                List<Groups> groupsList = AppDatabase.getDatabaseInstance(context).getGroupsDao().getAllNewGroups();
                for (final Groups groups : groupsList)
                    groupsData.put(new JSONObject(gson.toJson(groups)));

                List<ContentProgress> contentProgressList = AppDatabase.getDatabaseInstance(context).getContentProgressDao().getAllContentNodeProgress();
                for (final ContentProgress contentProgress : contentProgressList)
                    contentProgressData.put(new JSONObject(gson.toJson(contentProgress)));

                List<KeyWords> keyWordsList = AppDatabase.getDatabaseInstance(context).getKeyWordDao().getAllData();
                for (final KeyWords keyWords : keyWordsList)
                    keyWordsData.put(new JSONObject(gson.toJson(keyWords)));

                List<Model_CourseEnrollment> courseEnrollList = AppDatabase.getDatabaseInstance(context).getCourseDao().getAllData();
                for (final Model_CourseEnrollment model_courseEnrollment : courseEnrollList)
                    courseEnrollmentData.put(new JSONObject(gson.toJson(model_courseEnrollment)));

                JSONObject pushDataJsonObject = generateRequestString(scoreData, attendanceData, sessionData,
                        /*supervisorData,*/ logsData, /*assessmentData,*/ studentData, contentProgressData, keyWordsData,
                        courseEnrollmentData, groupsData);

                pushSuccessfull = false;
                //iterate through all new sessions
                totalImages = AppDatabase.getDatabaseInstance(context).getScoreDao().getUnpushedImageCount();
                enrollmentCount = courseEnrollList.size();
                totalScoreCount = scoreData.length();
//                certiCount = AppDatabase.getDatabaseInstance(context).getAssessmentDao().getUnpushedCertiCount(CERTIFICATE_LBL);
                imageUploadCnt = 0;
                imageUploadList = new ArrayList<>();
                isConnectedToRasp = false;
                //Checks if device is connected to wifi
                if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
                    isConnectedToRasp = false;
                    setYellowColorMainTextToDialog();
                    setMainTextToDialog(context.getResources().getString(R.string.Please_wait_pushing_Data));
                    setSyncLottieToDialog();
                    pushDataToServer(context, pushDataJsonObject, FC_Constants.uploadDataUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            BackgroundPushService mYourService;
            mYourService = new BackgroundPushService();
            Intent mServiceIntent;
            mServiceIntent = new Intent(context, mYourService.getClass());
            FastSave.getInstance().saveBoolean(IS_SERVICE_STOPED, true);
            Log.d("PushData", "End Service  IS_STOPPED : " + FastSave.getInstance().getBoolean(IS_SERVICE_STOPED, false));
            if (isMyServiceRunning(mYourService.getClass()))
                context.stopService(mServiceIntent);

        }
    }

    //Set heading text of Dialog
    @SuppressLint("SetTextI18n")
    @UiThread
    public void setMainTextToDialog(String dialogMsg) {
        if (showUi)
            txt_push_dialog_msg.setText("" + dialogMsg);
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    public void setRedColorMainTextToDialog() {
        if (showUi)
            txt_push_dialog_msg.setTextColor(context.getResources().getColor(R.color.colorBtnRedDark));
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    public void setGreenColorMainTextToDialog() {
        if (showUi)
            txt_push_dialog_msg.setTextColor(context.getResources().getColor(R.color.colorBtnGreenDark));
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    public void setYellowColorMainTextToDialog() {
        if (showUi)
            txt_push_dialog_msg.setTextColor(context.getResources().getColor(R.color.colorGame));
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    public void setSyncLottieToDialog() {
        if (showUi) {
            push_lottie.setAnimation("cloud_sync.json");
            push_lottie.playAnimation();
        }
    }

    //Set sub text of dialog
    @SuppressLint("SetTextI18n")
    @UiThread
    public void setSubTextToDialog(String dialogMsg) {
        if (showUi) {
            txt_push_error.setVisibility(View.VISIBLE);
            txt_push_error.setText("" + dialogMsg);
        }
    }

    //Custom loading dialog is shown
    @UiThread
    public void showPushDialog(Context context) {
        if (showUi) {
            pushDialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
            pushDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pushDialog.setContentView(R.layout.app_send_success_dialog);
            Objects.requireNonNull(pushDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pushDialog.setCancelable(false);
            pushDialog.setCanceledOnTouchOutside(false);
            pushDialog.show();

            push_lottie = pushDialog.findViewById(R.id.push_lottie);
            txt_push_dialog_msg = pushDialog.findViewById(R.id.txt_push_dialog_msg);
            txt_push_error = pushDialog.findViewById(R.id.txt_push_error);
            txt_date = pushDialog.findViewById(R.id.txt_date);
            rl_btn = pushDialog.findViewById(R.id.rl_btn);
            ok_btn = pushDialog.findViewById(R.id.ok_btn);
            eject_btn = pushDialog.findViewById(R.id.eject_btn);

            txt_date.setText("Today's date : " + FC_Utility.getCurrentDateTime());
            txt_push_error.setText("");
            txt_push_error.setVisibility(View.GONE);
            ok_btn.setVisibility(View.GONE);
            eject_btn.setVisibility(View.GONE);

            ok_btn.setOnClickListener(v -> {
                if(pushSuccessfull)
                    setPushFlag();
                getImageList();
//                else
//                    pushDialog.dismiss();
            });

            eject_btn.setOnClickListener(v -> {
                pushDialog.dismiss();
                try {
                    Intent intent = new Intent("com.pratham.assessment.async.SyncDataActivity_");
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void zip(String[] _files, String zipFileName, File filepath) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte[] data = new byte[BUFFER];
            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
//            filepath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String filePathStr = "NA";

    // Call only this method, do the data collection before
    public void pushDataToServer(Context context, JSONObject data, String... url) {
        try {
//            String fielName = ""+FC_Utility.getUUID();
            String fielName = "FC_" + FC_RandomString.unique();
            filePathStr = ApplicationClass.getStoragePath().toString()
                    + "/FCAInternal/PushJsons/" + fielName; // file path to save
            File filepath = new File(filePathStr + ".json"); // file path to save
//            if (filepath.exists())
//                filepath.delete();
            FileWriter writer = new FileWriter(filepath);
            writer.write(String.valueOf(data));
            writer.flush();
            writer.close();

            String[] s = new String[1];
            // Type the path of the files in here
            s[0] = filePathStr + ".json";
            Log.d("FC_RandomString", "ZIP NAME " + s[0]);
            zip(s, filePathStr + ".zip", filepath);
            String URL_Final = url[0];
//            AndroidNetworking.upload("http://devprodigi.openiscool.org/api/FCAPP/PushFiles"/*url[0]*/)

            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                URL_Final = FC_Constants.uploadDataUrl_PI;
                Log.d("PushData", "RPI Push API : " + URL_Final);
                AndroidNetworking.upload(URL_Final)
                        .addHeaders("Content-Type", "file/zip")
                        .addMultipartFile("uploaded_file", new File(filePathStr + ".zip"))
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("PushData", "DATA PUSH " + response);
                                Gson gson = new Gson();
                                FilePushResponse pushResponse = gson.fromJson(response, FilePushResponse.class);

//                                new File(filePathStr + ".zip").delete();
                                Log.d("PushData", "DATA PUSH SUCCESS");
                                pushSuccessfull = true;
                                setDataPushSuccessfull();
                            }

                            @Override
                            public void onError(ANError anError) {
                                //Fail - Show dialog with failure message.
                                Log.d("PushData", "Data push FAIL");
                                Log.d("PushData", "ERROR  " + anError);
//                                new File(filePathStr + ".zip").delete();
                                pushSuccessfull = false;
                                setDataPushFailed();
                            }
                        });
            } else {
//                pushSuccessfull = false;
//                setDataPushFailed();
                Log.d("PushData", "First Push API : " + FC_Constants.NEW_PUSH_API);
                AndroidNetworking.upload(FC_Constants.NEW_PUSH_API)
                        .addHeaders("Content-Type", "file/zip")
                        .addMultipartFile("fileJson", new File(filePathStr + ".zip"))
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("PushData", "DATA PUSH " + response);
                                Gson gson = new Gson();
                                SyncLog pushResponse = gson.fromJson(response, SyncLog.class);

//                                new File(filePathStr + ".zip").delete();
                                if (pushResponse.getPushId() != 0) {
                                    Log.d("PushData", "DATA PUSH SUCCESS");
                                    pushSuccessfull = true;
                                    setNewDataPushSuccessfull(pushResponse, response);
                                } else {
                                    Log.d("PushData", "Failed DATA PUSH");
                                    pushSuccessfull = false;
                                    setDataPushFailed();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("PushData", "Data push FAIL");
                                Log.d("PushData", "ERROR  " + anError);
//                                new File(filePathStr + ".zip").delete();
                                pushSuccessfull = false;
                                setDataPushFailed();
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method shows success dialog
    @UiThread
    public void setDataPushSuccessfull() {
        setPushFlag();
        if (showUi) {
            setGreenColorMainTextToDialog();
            setMainTextToDialog(context.getResources().getString(R.string.data_pushed_successfully) + "\n" +
                    context.getResources().getString(R.string.Score_Count) + " " + scoreData.length());
            ok_btn.setText(context.getResources().getString(R.string.Okay));
            ok_btn.setVisibility(View.VISIBLE);
        } else {
//            setPushFlag();
            getImageList();
        }
    }

    @UiThread
    public void setNewDataPushSuccessfull(SyncLog pushResponse, String strResponse) {
//        this.pushResponse = pushResponse;
        setPushFlag();
        if (showUi)
            pushResponse.setPushType(FC_Constants.APP_MANUAL_SYNC);
        else
            pushResponse.setPushType(FC_Constants.APP_AUTO_SYNC);

        AppDatabase.getDatabaseInstance(context).getSyncLogDao().insert(pushResponse);
        addResponse(pushResponse, strResponse);

        String err = "";
        if (!pushResponse.getError().equalsIgnoreCase("") && !pushResponse.getError().equalsIgnoreCase(" "))
            err = "ERROR : " + pushResponse.getError();
//        AppDatabase.getDatabaseInstance(context).getLogsDao().setPushStatus(FC_Constants.SUCCESSFULLYPUSHED,
//                FastSave.getInstance().getString(FC_Constants.PUSH_ID_LOGS, ""));
        if (showUi) {
            push_lottie.setAnimation("cloud_sync.json");
            push_lottie.playAnimation();
            setGreenColorMainTextToDialog();
            setMainTextToDialog("PushID : " + pushResponse.getPushId() + "\n"
                    + "UUID : " + pushResponse.getUuid() + "\n"
                    + "Push Date : " + pushResponse.getPushDate() + "\n"
                    + "Push Status : " + pushResponse.getStatus() + "\n"
                    + err + "\n\n" + context.getResources().getString(R.string.Now_Upload_Media));
            txt_push_dialog_msg.setVisibility(View.VISIBLE);
            ok_btn.setText(context.getResources().getString(R.string.Okay));
            ok_btn.setVisibility(View.VISIBLE);
        } else {
            getImageList();
        }
    }

    @UiThread
    public void addResponse(SyncLog pushResponse, String response) {
        Modal_Log log = new Modal_Log();

        log.setCurrentDateTime(pushResponse.getPushDate());
        log.setErrorType("");
        log.setExceptionMessage("TEMP_SYNC_RESPONSE");
        log.setMethodName("" + pushResponse.getPushId());
        log.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
        log.setGroupId("");
        log.setLogDetail("" + response);
        log.setExceptionStackTrace("APK BUILD DATE : " + BUILD_DATE);
        log.setDeviceId("" + FC_Utility.getDeviceID());
        log.setSentFlag(1);

        Log.d("PushData", "pushStatusJson JSON : " + pushResponse);
        AppDatabase.getDatabaseInstance(context).getLogsDao().insertLog(log);
        BackupDatabase.backup(context);
    }

    @UiThread
    public void hideOKBtn() {
        if (showUi) {
            ok_btn.setText(context.getResources().getString(R.string.Okay));
            ok_btn.setVisibility(View.GONE);
        }
    }

    //Method shows failure dialog
    @UiThread
    public void setDataPushFailed() {
//        AppDatabase.getDatabaseInstance(context).getLogsDao().setPushStatus(FC_Constants.PUSHFAILED,
//                FastSave.getInstance().getString(FC_Constants.PUSH_ID_LOGS, ""));
        if (showUi) {
            setRedColorMainTextToDialog();
            setMainTextToDialog(context.getResources().getString(R.string.OOPS));
            setSubTextToDialog(context.getResources().getString(R.string.Data_pushed_failed));
            push_lottie.setAnimation("error_cross.json");
            push_lottie.playAnimation();
            ok_btn.setText(context.getResources().getString(R.string.Okay));
            ok_btn.setVisibility(View.VISIBLE);
        } else
            getImageList();
    }

    //This method sets setFlag value to 1 in local database if data is successfully pushed to server.
    @Background
    public void setPushFlag() {
        AppDatabase.getDatabaseInstance(context).getLogsDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getSessionDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getAttendanceDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getScoreDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getAssessmentDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getSupervisorDataDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getKeyWordDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getContentProgressDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getCourseDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getStudentDao().setSentFlag();
        AppDatabase.getDatabaseInstance(context).getGroupsDao().setSentFlag();
//        AppDatabase.getDatabaseInstance(context).getLogsDao().deletePushedLogs();
        BackupDatabase.backup(context);
    }

    @Background
    public void getImageList() {
        setMainTextToDialog(context.getResources().getString(R.string.Collecting_Media));
        setGreenColorMainTextToDialog();
        hideOKBtn();
        actPhotoPath = ApplicationClass.getStoragePath().toString() + "/FCAInternal/ActivityPhotos/";
//        Log.d("PushData", "Path: " + actPhotoPath);
        File directory = new File(actPhotoPath);
        imageFilesArray = directory.listFiles();
//        db_Media =
//        Log.d("PushData", "Size: " + imageFilesArray.length);

        if (imageFilesArray != null)
            for (int index = 0; index < imageFilesArray.length; index++) {
                if (imageFilesArray[index].exists() && imageFilesArray[index].isDirectory()) {
//                Log.d("PushData", "FolderName:" + imageFilesArray[index].getName());
                    File activityPhotosFile = new File(imageFilesArray[index].getAbsolutePath());
                    File[] file = activityPhotosFile.listFiles();
                    if (Objects.requireNonNull(file).length > 0) {
                        for (int i = 0; i < file.length; i++) {
                            if (file[i].exists() && file[i].isFile()
                                    && !file[i].getName().equalsIgnoreCase(".nomedia")) {
                                String fName = file[i].getName();
                                File fPath = new File(file[i].getAbsolutePath());
//                            Log.d("PushData", "FileName:" + fName);
                                String f_Name = "NA";
                                f_Name = AppDatabase.getDatabaseInstance(context).getScoreDao().getImgResId(fName);
                                if (f_Name != null) {
                                    Image_Upload image_upload = new Image_Upload();
                                    image_upload.setFileName(fName);
                                    image_upload.setFilePath(fPath);
                                    image_upload.setUploadStatus(false);
                                    imageUploadList.add(image_upload);
                                }
                            }
                        }
                    }
                } else if (imageFilesArray[index].exists() && imageFilesArray[index].isFile()
                        && !imageFilesArray[index].getName().equalsIgnoreCase(".nomedia")) {
                    File fPath = new File(imageFilesArray[index].getAbsolutePath());
                    String fName = imageFilesArray[index].getName();
                    String f_Name = "NA";
                    f_Name = AppDatabase.getDatabaseInstance(context).getScoreDao().getImgResId(fName);
                    if (f_Name != null) {
//                    Log.d("PushData", "FileName:" + imageFilesArray[index].getName());
                        Image_Upload image_upload = new Image_Upload();
                        image_upload.setFileName(fName);
                        image_upload.setFilePath(fPath);
                        image_upload.setUploadStatus(false);
                        imageUploadList.add(image_upload);
                    }
                }
            }

        imageUploadCnt = 0;
        totalImages = imageUploadList.size();
//        Log.d("PushData", "Size: " + imageUploadList.size());
        if (imageUploadList.size() > 0) {
            setGreenColorMainTextToDialog();
            setMainTextToDialog(context.getResources().getString(R.string.Uploading) + " "
                    + totalImages + " " + context.getResources().getString(R.string.images));

//            String fielName = "FCI_" + FC_RandomString.unique();
//            String filePathStr = ApplicationClass.getStoragePath().toString()
//                    + "/FCAInternal/ActivityPhotos/" + fielName;
//            img_zip(imageUploadList, filePathStr + ".zip");

            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI))
                pushImagesToServer_PI(0);
            else
                pushImagesToServer_Internet(0);
        } else {
            showTotalImgStatus();
        }
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    public void updateCntr(int imgCtr) {
        if (showUi)
            dialog_file_name.setText(context.getResources().getString(R.string.Uploading) + " "
                    + imgCtr + "/" + totalImages);
    }

    /**
     * This method is to push the list of the image to server.
     *
     * @param jsonIndex is always passed zero and incremented on success push.
     */
    @UiThread
    public void pushImagesToServer_Internet(final int jsonIndex) {
        String img_api= ""+FC_Constants.PUSH_IMAGE_API+FC_Constants.APK_VERSION_STR+FC_Utility.getAppVerison();
        Log.d("PushData", "Image jsonIndex : " + img_api);
        if (jsonIndex < imageUploadList.size()) {
            AndroidNetworking.upload(img_api)
                    .addMultipartFile("images",
                            imageUploadList.get(jsonIndex).getFilePath())
//                    .addMultipartFile(imageUploadList.get(jsonIndex).getFileName(),
//                            imageUploadList.get(jsonIndex).getFilePath())
                    .addHeaders("Content-Type", "images")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("PushData", "Image onResponse : " + response);
//                                Gson gson = new Gson();
                                PushResponse pushResponse = gson.fromJson(response, PushResponse.class);
//                                if (response.contains("UPLOADED")/*contains(",\"ErrorId\":\"1\",")*/) {
                                if (pushResponse.getStatus().equalsIgnoreCase("UPLOADED")/*contains(",\"ErrorId\":\"1\",")*/) {
                                    imageUploadCnt++;
                                    Log.d("PushData", "imageUploadCnt : " + imageUploadCnt);
                                    Log.d("PushData", "imageUploadName : " + imageUploadList.get(jsonIndex).getFileName());
                                    imageUploadList.get(jsonIndex).setUploadStatus(true);
                                    pushImagesToServer_Internet(jsonIndex + 1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("PushData", "IMAGE onError : " + imageUploadList.get(jsonIndex).getFileName());
                            pushImagesToServer_Internet(jsonIndex + 1);
                        }
                    });
        } else {
            Log.d("PushData", "IMAGES COMPLETE");
            showTotalImgStatus();
        }
    }

    @UiThread
    public void pushImagesToServer_PI(final int jsonIndex) {
//        Log.d("PushData", "Image jsonIndex : " + jsonIndex);
        if (jsonIndex < imageUploadList.size()) {

            AndroidNetworking.upload(FC_Constants.PUSH_IMAGE_API_PI)
                    .addMultipartFile("uploaded_file", imageUploadList.get(jsonIndex).getFilePath())
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d("PushData", "Image onResponse_PI : " + response);
//                                if (response.equalsIgnoreCase("success")) {
                                imageUploadCnt++;
                                Log.d("PushData", "imageUploadCnt _PI: " + imageUploadCnt);
                                imageUploadList.get(jsonIndex).setUploadStatus(true);
                                pushImagesToServer_PI(jsonIndex + 1);
//                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("PushData", "IMAGE onError _PI: " + imageUploadList.get(jsonIndex).getFileName());
                            Log.d("PushData", "onError _PI: " + anError.getMessage());
                            Log.d("PushData", "onError _PI: " + anError.getErrorBody());
                            Log.d("PushData", "onError _PI: " + anError.getErrorDetail());
                            pushImagesToServer_PI(jsonIndex + 1);
                        }
                    });
        } else {
            Log.d("PushData", "IMAGES COMPLETE");
            showTotalImgStatus();
        }
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    public void showTotalImgStatus() {
        int successfulCntr = 0, failedCntr = 0;
        Log.d("PushData", "IMAGES COMPLETE");
        if (showUi) {
            ok_btn.setVisibility(View.GONE);
            eject_btn.setText(context.getResources().getString(R.string.close));
            eject_btn.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < imageUploadList.size(); i++) {
            if (imageUploadList.get(i).isUploadStatus()) {
                AppDatabase.getDatabaseInstance(context).getScoreDao().setImgSentFlag(imageUploadList.get(i).getFileName());
                successfulCntr++;
            } else
                failedCntr++;
        }

        if (pushSuccessfull)
            pushedScoreLength = "" + scoreData.length();
        else
            pushedScoreLength = "0";
        successful_ImageLength = "" + successfulCntr;
        failed_ImageLength = "" + failedCntr;
//        syncTime = FC_Utility.getCurrentDateTime();

        FastSave.getInstance().saveString(FC_Constants.SYNC_TIME, syncTime);
        FastSave.getInstance().saveString(FC_Constants.SYNC_COURSE_ENROLLMENT_LENGTH, "" + enrollmentCount);
        FastSave.getInstance().saveString(FC_Constants.SYNC_DATA_LENGTH, pushedScoreLength);
        FastSave.getInstance().saveString(FC_Constants.SYNC_MEDIA_LENGTH, successful_ImageLength);

        int totalScoreCount, totalSuccessfullScorePush, totalImgCount, totalSuccessfulImgCount, totalCourses, totalCoursesSuccessful;

        totalSuccessfullScorePush = AppDatabase.getDatabaseInstance(context).getScoreDao().getTotalSuccessfullScorePush();
        totalScoreCount = AppDatabase.getDatabaseInstance(context).getScoreDao().getTotalScoreCount();

        totalSuccessfulImgCount = AppDatabase.getDatabaseInstance(context).getScoreDao().getTotalSuccessfullImageScorePush();
        totalImgCount = AppDatabase.getDatabaseInstance(context).getScoreDao().getTotalImageScorePush();

        totalCoursesSuccessful = AppDatabase.getDatabaseInstance(context).getCourseDao().getAllSuccessfulCourses();
        totalCourses = AppDatabase.getDatabaseInstance(context).getCourseDao().getAllCourses();

        try {
            JSONObject pushStatusJson = null;
            pushStatusJson = new JSONObject();
            pushStatusJson.put(FC_Constants.SYNC_TIME, syncTime);
            pushStatusJson.put(FC_Constants.SYNC_COURSE_ENROLLMENT_LENGTH, enrollmentCount);
            pushStatusJson.put(FC_Constants.SYNC_DATA_LENGTH, pushedScoreLength);
            pushStatusJson.put(FC_Constants.SYNC_MEDIA_LENGTH, successful_ImageLength);
            pushStatusJson.put("ScoreTable", totalSuccessfullScorePush + "/" + totalScoreCount);
            pushStatusJson.put("MediaCount", totalSuccessfulImgCount + "/" + totalImgCount);
            pushStatusJson.put("CoursesCount", totalCoursesSuccessful + "/" + totalCourses);

            Modal_Log log = new Modal_Log();
            if (!showUi)
                log.setExceptionMessage(FC_Constants.APP_AUTO_SYNC);
            else
                log.setExceptionMessage(FC_Constants.APP_MANUAL_SYNC);
            log.setMethodName("" + FastSave.getInstance().getString(FC_Constants.PUSH_ID_LOGS, "na"));
            log.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            log.setGroupId("");
            log.setExceptionStackTrace("APK BUILD DATE : " + BUILD_DATE);
            if (pushSuccessfull)
                log.setErrorType("" + FC_Constants.SUCCESSFULLYPUSHED);
            else
                log.setErrorType("" + FC_Constants.PUSHFAILED);
            log.setLogDetail("" + pushStatusJson);
            log.setDeviceId("" + FC_Utility.getDeviceID());
            log.setCurrentDateTime("" + syncTime);
            AppDatabase.getDatabaseInstance(context).getLogsDao().insertLog(log);

            Log.d("PushData", "pushStatusJson JSON : " + pushStatusJson);
//            AppDatabase.getDatabaseInstance(context).getLogsDao().setPushDataLog(,
//                    FastSave.getInstance().getString(FC_Constants.PUSH_ID_LOGS, ""));
            BackupDatabase.backup(context);

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (showUi) {
            if (pushSuccessfull) {
                setGreenColorMainTextToDialog();
                setMainTextToDialog(context.getResources().getString(R.string.Upload_Complete));
            } else {
                setRedColorMainTextToDialog();
                setMainTextToDialog(context.getResources().getString(R.string.Data_pushed_failed));
            }
            //Show total groups pushed
            push_lottie.setAnimation("lottie_correct.json");
            push_lottie.playAnimation();
            setSubTextToDialog(context.getResources().getString(R.string.Data_synced) + " " + scoreData.length()
                    + "\n" + context.getResources().getString(R.string.Enrollment_synced) + " " + enrollmentCount
                    + "\n" + context.getResources().getString(R.string.Media_synced) + " " + successfulCntr
                    + "\n" + context.getResources().getString(R.string.Media_failed) + " " + failedCntr);
        }

        BackgroundPushService mYourService;
        mYourService = new BackgroundPushService();
        Intent mServiceIntent;
        mServiceIntent = new Intent(context, mYourService.getClass());
        FastSave.getInstance().saveBoolean(IS_SERVICE_STOPED, true);
        Log.d("PushData", "End Service  IS_STOPPED : " + FastSave.getInstance().getBoolean(IS_SERVICE_STOPED, false));
        if (isMyServiceRunning(mYourService.getClass()))
            context.stopService(mServiceIntent);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("MAINACT", "isMyServiceRunning?  " + true);
                return true;
            }
        }
        Log.i("MAINACT", "isMyServiceRunning?  " + false);
        return false;
    }

    //In this method data from json array and from local database is fetched and passed to jsonobject.
    private JSONObject generateRequestString(JSONArray scoreData, JSONArray attendanceData, JSONArray sessionData,
                                             /*JSONArray supervisorData,*/ JSONArray logsData, /*JSONArray assessmentData,*/
                                             JSONArray studentData, JSONArray contentProgress, JSONArray keyWordsData,
                                             JSONArray courseEnrollmentData, JSONArray groupsData) {

        JSONObject pushJsonObject = new JSONObject();

        try {
            final JSONObject metadataJson = new JSONObject();

            metadataJson.put(FC_Constants.SCORECOUNT, scoreData.length());
            metadataJson.put(FC_Constants.ATTENDANCECOUNT, attendanceData.length());
            metadataJson.put(FC_Constants.SESSIONCOUNT, sessionData.length());
            metadataJson.put(FC_Constants.LOGSCOUNT, logsData.length());
            metadataJson.put(FC_Constants.STUDENTCOUNT, studentData.length());
            metadataJson.put(FC_Constants.CONTENTPROGRESSCOUNT, contentProgress.length());
            metadataJson.put(FC_Constants.KEYWORDSCOUNT, keyWordsData.length());
            metadataJson.put(FC_Constants.COURSEENROLLMENTCOUNT, courseEnrollmentData.length());
            metadataJson.put(FC_Constants.GROUPSCOUNT, groupsData.length());
            if (showUi)
                metadataJson.put("PushType", FC_Constants.APP_MANUAL_SYNC);
            else
                metadataJson.put("PushType", FC_Constants.APP_AUTO_SYNC);

            final List<Status> statuses = AppDatabase.getDatabaseInstance(context).getStatusDao().getAllStatuses();
            for (final Status status : statuses) {
                metadataJson.put(status.getStatusKey(), status.getValue());
            }
            metadataJson.put("apkType", "APP Build Date : " + ApplicationClass.BUILD_DATE);
            metadataJson.put("DeviceDataSyncTime", FC_Utility.getCurrentDateTime());

            JSONObject sessionObj = new JSONObject();

            sessionObj.put(FC_Constants.SCOREDATA, scoreData);
            sessionObj.put(FC_Constants.STUDENTDATA, studentData);
            sessionObj.put(FC_Constants.ATTENDANCEDATA, attendanceData);
            sessionObj.put(FC_Constants.SESSIONSDATA, sessionData);
            sessionObj.put(FC_Constants.KEYWORDS, keyWordsData);
            sessionObj.put(FC_Constants.COURSEENROLLMENT, courseEnrollmentData);
            sessionObj.put(FC_Constants.CONTENTPROGRESSDATA, contentProgress);
            sessionObj.put(FC_Constants.LOGSDATA, logsData);
            sessionObj.put(FC_Constants.GROUPSDATA, groupsData);
//            sessionObj.put(FC_Constants.ASSESSMENTDATA, assessmentData);
//            sessionObj.put(FC_Constants.SUPERVISOR, supervisorData);
            pushJsonObject.put("session", sessionObj);
            pushJsonObject.put("metadata", metadataJson);

            return pushJsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return pushJsonObject = new JSONObject();
        }
    }
}