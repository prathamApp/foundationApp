package com.pratham.foundation.ui.qr_scan;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.dao.StatusDao;
import com.pratham.foundation.database.domain.Attendance;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Session;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.modalclasses.PlayerModal;
import com.pratham.foundation.ui.selectSubject.SelectSubject_;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.utility.SplashSupportActivity.ButtonClickSound;


@EActivity(R.layout.activity_qrscan)
public class QRScanActivity extends BaseActivity implements
        ZXingScannerView.ResultHandler {

    @ViewById(R.id.content_frame)
    ViewGroup content_frame;
    @ViewById(R.id.button_rl)
    RelativeLayout button_ll;
    @ViewById(R.id.rl_qr_main)
    RelativeLayout rl_qr_main;
    @ViewById(R.id.tv_stud_one)
    TextView tv_stud_one;
    @ViewById(R.id.btn_start_game)
    Button btn_start_game;
    @ViewById(R.id.btn_reset_btn)
    Button btn_reset_btn;
    @ViewById(R.id.btn_get_progress)
    Button btn_get_progress;

    PlayerModal playerModal;
    int totalStudents = 0;
    String stdFirstName, stdId;
    Dialog dialog;
    Boolean setStud = false;
    public ZXingScannerView mScannerView;
    int crlCheck;
    Gson gson;

    @AfterViews
    public void initialize() {
        mScannerView = new ZXingScannerView(this);
        mScannerView.setResultHandler(this);
        content_frame.addView((mScannerView));
        gson = new Gson();
        Log.d("tag", "SD Path: " + ApplicationClass.contentSDPath);
        initCamera();
    }

    public void AnimateTextView(Context c, final TextView iv_logo) {
        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.zoom_in_new);
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out_new);
        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_logo.setAnimation(anim_out);
            }
        });
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showButton();
            }
        });
        //(holder.mTextView).setAnimation(anim_in);
        iv_logo.setAnimation(anim_in);
    }

    public void showButtonsAnimation(Context c, final RelativeLayout button_ll) {
        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
        //(holder.mTextView).setAnimation(anim_in);
        button_ll.setAnimation(anim_in);
    }

    public void hideButtonsAnimation(Context c, final RelativeLayout button_ll, final TextView iv_logo) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.full_zoom_out);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_stud_one.setText("");
                button_ll.setVisibility(View.GONE);
                iv_logo.setVisibility(View.GONE);
            }
        });
        //(holder.mTextView).setAnimation(anim_in);
        button_ll.setAnimation(anim_out);
        iv_logo.setAnimation(anim_out);
    }


    private void showButton() {
        button_ll.setVisibility(View.VISIBLE);
        showButtonsAnimation(this, button_ll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mScannerView.stopCamera();
    }

    public void initCamera() {
        mScannerView.startCamera();
        mScannerView.resumeCameraPreview(this);
    }

    @Click(R.id.btn_reset_btn)
    public void resetQrList() {
        stdFirstName = "";
        stdId = "";
        hideButtonsAnimation(this, button_ll, tv_stud_one);
        ButtonClickSound.start();
        scanNextQRCode();
    }

    public Dialog myLoadingDialog;
    @Click(R.id.btn_get_progress)
    public void fetchProgress() {
//        stdId;
        if (FC_Utility.isDataConnectionAvailable(this)) {
            showLoader();
            getStudentData(FC_Constants.STUDENT_PROGRESS_INTERNET, FC_Constants.STUDENT_PROGRESS_API, stdId);
        }
        else{
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getStudentData(final String requestType, String url, String studentID) {
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
    }

    @Background
    public void receivedContent(String requestType, String response) {
        if (requestType.equalsIgnoreCase(FC_Constants.STUDENT_PROGRESS_INTERNET)) {
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
                    BackupDatabase.backup(QRScanActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            getStudentData(FC_Constants.LEARNT_WORDS_INTERNET, FC_Constants.LEARNT_WORDS_API, stdId);
        } else if (requestType.equalsIgnoreCase(FC_Constants.LEARNT_WORDS_INTERNET)) {
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
                    BackupDatabase.backup(QRScanActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dismissLoader();
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

    private void dismissLoader() {
        if (myLoadingDialog != null) {
            myLoadingDialog.dismiss();
        }
    }

    @Click(R.id.btn_start_game)
    public void gotoGame() {
        mScannerView.stopCamera();
        enterStudentData(stdId, stdFirstName);
        startSession();
        FC_Constants.currentStudentID = stdId;
        ButtonClickSound.start();
        FC_Constants.GROUP_LOGIN = false;
        //todo remove#
       // startActivity(new Intent(this, HomeActivity_.class));
        startActivity(new Intent(this, SelectSubject_.class));
    }

    private String[] decodeStudentId(String text, String s) {
        return text.split(s);
    }

    public void scanNextQRCode() {
        if (mScannerView != null) {
            mScannerView.stopCamera();
            mScannerView.startCamera();
            mScannerView.resumeCameraPreview(this);
        }
    }

    private void showLoader() {
        myLoadingDialog = new Dialog(QRScanActivity.this);
        myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myLoadingDialog.setContentView(R.layout.loading_dialog);
        myLoadingDialog.setCanceledOnTouchOutside(false);
        myLoadingDialog.show();
    }

    public void dialogClick() {
        tv_stud_one.setVisibility(View.VISIBLE);
        tv_stud_one.setText("" + stdFirstName);
        AnimateTextView(this, tv_stud_one);
        scanNextQRCode();
    }

    @Override
    public void handleResult(Result result) {
        try {
            boolean dulicateQR = false;
            mScannerView.stopCamera();
            Log.d("RawResult:::", "****" + result.getText());

            JSONObject jsonobject = new JSONObject(result.getText());
            stdId = jsonobject.getString("stuId");
            stdFirstName = jsonobject.getString("name");

            dialogClick();

        } catch (Exception e) {
            Toast.makeText(this, "Invalid QR Code !!!", Toast.LENGTH_SHORT).show();
            scanNextQRCode();
            BackupDatabase.backup(this);
            e.printStackTrace();
        }

    }

    @Background
    public void startSession() {
        String currentSession;
        try {
            StatusDao statusDao = appDatabase.getStatusDao();
            currentSession = "" + UUID.randomUUID().toString();
            FC_Constants.currentSession = currentSession;
            statusDao.updateValue("CurrentSession", "" + currentSession);

            Session startSesion = new Session();
            startSesion.setSessionID("" + currentSession);
            String timerTime = FC_Utility.getCurrentDateTime();

            Log.d("doInBackground", "--------------------------------------------doInBackground : " + timerTime);
            startSesion.setFromDate(timerTime);
            startSesion.setToDate("NA");
            startSesion.setSentFlag(0);
            appDatabase.getSessionDao().insert(startSesion);

            Attendance attendance = new Attendance();
            attendance.setSessionID("" + currentSession);
            attendance.setDate(timerTime);
            attendance.setStudentID("" + stdId);
            attendance.setGroupID("QR");
            appDatabase.getAttendanceDao().insert(attendance);

            BackupDatabase.backup(QRScanActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Background
    public void enterStudentData(final String stdId, final String stdFirstName) {
        try {

            Student student = new Student();

            student.setStudentID("" + stdId);
            student.setFullName("" + stdFirstName);
            student.setNewFlag(1);
            String studentName = appDatabase.getStudentDao().checkStudent("" + stdId);

            if (studentName == null) {
                appDatabase.getStudentDao().insert(student);
            }

            BackupDatabase.backup(QRScanActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


/*

<ImageView
                android:id="@+id/iv_admin"
                android:layout_width="95dp"
                android:layout_height="95dp"
                android:src="@drawable/ic_admin"
                android:scaleType="fitXY"
                android:layout_centerInParent="true"
                android:layout_alignParentTop="true"
                android:layout_marginVertical="10dp"
                android:gravity="center"
                android:textSize="20sp"
                android:textStyle="bold" />

            <ImageView
                android:id="@+id/btn_stats"
                android:layout_width="95dp"
                android:layout_height="95dp"
                android:src="@drawable/ic_stats"
                android:scaleType="fitXY"
                android:layout_centerInParent="true"
                android:layout_marginVertical="10dp"
                android:gravity="center"
                android:textSize="20sp"
                android:textStyle="bold" />

* */