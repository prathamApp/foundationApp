package com.pratham.foundation.ui.qr_scan;

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
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.dao.StatusDao;
import com.pratham.foundation.database.domain.Attendance;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Session;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.modalclasses.PlayerModal;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.selectSubject.SelectSubject_;
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

import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.utility.FC_Constants.QR_GROUP_MODE;



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
    @ViewById(R.id.tv_stud_two)
    TextView tv_stud_two;
    @ViewById(R.id.tv_stud_three)
    TextView tv_stud_three;
    @ViewById(R.id.tv_stud_four)
    TextView tv_stud_four;
    @ViewById(R.id.tv_stud_five)
    TextView tv_stud_five;
    @ViewById(R.id.btn_start_game)
    Button btn_start_game;
    @ViewById(R.id.btn_reset_btn)
    Button btn_reset_btn;
    @ViewById(R.id.btn_get_progress)
    Button btn_get_progress;
    @ViewById(R.id.rl_student_list)
    RelativeLayout rl_student_list;

    PlayerModal playerModal;
    List<PlayerModal> playerModalList;
    CustomLodingDialog dialog;
    public ZXingScannerView mScannerView;
    Gson gson;

    @AfterViews
    public void initialize() {
        mScannerView = new ZXingScannerView(this);
        mScannerView.setResultHandler(this);
        content_frame.addView((mScannerView));
        gson = new Gson();
        playerModalList = new ArrayList<>();
        btn_get_progress.setVisibility(View.GONE);
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

    @Click(R.id.btn_back)
    public void pressedBackButton(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        mScannerView.stopCamera();
        super.onBackPressed();
    }

    public void initCamera() {
        mScannerView.startCamera();
        mScannerView.resumeCameraPreview(this);
    }

    @Click(R.id.btn_reset_btn)
    public void resetQrList() {
        playerModalList.clear();
        hideNameViews();
        hideButtonsAnimation(this, button_ll, tv_stud_one);
        ButtonClickSound.start();
        scanNextQRCode();
    }

    public void hideNameViews() {
        tv_stud_one.setText("");
        tv_stud_two.setText("");
        tv_stud_three.setText("");
        tv_stud_four.setText("");
        tv_stud_five.setText("");

        rl_student_list.setVisibility(View.GONE);
        tv_stud_one.setVisibility(View.GONE);
        tv_stud_two.setVisibility(View.GONE);
        tv_stud_three.setVisibility(View.GONE);
        tv_stud_four.setVisibility(View.GONE);
        tv_stud_five.setVisibility(View.GONE);
    }

    public CustomLodingDialog myLoadingDialog;

    @Click(R.id.btn_get_progress)
    public void fetchProgress() {
//        stdId;
        if (FC_Utility.isDataConnectionAvailable(this)) {
            showLoader();
//            getStudentData(FC_Constants.STUDENT_PROGRESS_INTERNET, FC_Constants.STUDENT_PROGRESS_API, stdId);
        } else {
            Toast.makeText(this, getResources().getString(R.string.No_Internet_Connection), Toast.LENGTH_SHORT).show();
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
//            getStudentData(FC_Constants.LEARNT_WORDS_INTERNET, FC_Constants.LEARNT_WORDS_API, stdId);
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
            return word != null;
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
        if(playerModalList.size()>0) {
            mScannerView.stopCamera();
            enterStudentData(playerModalList);
            startSession();
            if(FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(QR_GROUP_MODE)) {
                String currentStudName = "QR Students";
                FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_ID , "QR");
                FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_NAME , currentStudName);
            }else {
                String currentStudentID = ""+playerModalList.get(0).getStudentID();
                String currentStudName = ""+playerModalList.get(0).getStudentName();
                FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_ID , currentStudentID);
                FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_NAME , currentStudName);
            }
            ButtonClickSound.start();
            startActivity(new Intent(this, SelectSubject_.class));
            finish();
        }
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
        myLoadingDialog = new CustomLodingDialog(QRScanActivity.this);
        myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myLoadingDialog.setContentView(R.layout.loading_dialog);
        myLoadingDialog.setCanceledOnTouchOutside(false);
        myLoadingDialog.show();
    }

    public void dialogClick(int currentStudNo) {
        switch (currentStudNo) {
            case 0:
                rl_student_list.setVisibility(View.VISIBLE);
                tv_stud_one.setVisibility(View.VISIBLE);
                tv_stud_one.setText(playerModalList.get(currentStudNo).getStudentName());
                break;
            case 1:
                tv_stud_two.setVisibility(View.VISIBLE);
                tv_stud_two.setText(playerModalList.get(currentStudNo).getStudentName());
                break;
            case 2:
                tv_stud_three.setVisibility(View.VISIBLE);
                tv_stud_three.setText(playerModalList.get(currentStudNo).getStudentName());
                break;
            case 3:
                tv_stud_four.setVisibility(View.VISIBLE);
                tv_stud_four.setText(playerModalList.get(currentStudNo).getStudentName());
                break;
            case 4:
                tv_stud_five.setVisibility(View.VISIBLE);
                tv_stud_five.setText(playerModalList.get(currentStudNo).getStudentName());
                break;
        }
        scanNextQRCode();
    }

    @Override
    public void handleResult(Result result) {
        try {
            boolean dulicateQR = false;
            mScannerView.stopCamera();
            Log.d("RawResult:::", "****" + result.getText());
            JSONObject jsonobject = new JSONObject(result.getText());
            playerModal = new PlayerModal();
            playerModal.setStudentID(jsonobject.getString("stuId"));
            playerModal.setStudentName(jsonobject.getString("name"));

            if(FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(QR_GROUP_MODE)) {
                if (playerModalList.size() < 5) {
                    if (playerModalList.size() > 0) {
                        for (int i = 0; i < playerModalList.size(); i++)
                            if (playerModalList.get(i).getStudentID().equalsIgnoreCase(playerModal.studentID)) {
                                dulicateQR = true;
                                break;
                            }

                        if (dulicateQR)
                            showDuplicateDialog("Duplicate");
                        else {
                            playerModalList.add(playerModal);
                            dialogClick(playerModalList.size()-1);
                        }
                    } else {
                        playerModalList.add(playerModal);
                        dialogClick(playerModalList.size()-1);
                    }
                } else {
                    showDuplicateDialog("Complete");
                }
            }
            else {
                playerModalList.clear();
                playerModalList.add(playerModal);
                dialogClick(playerModalList.size()-1);
            }

        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.Invalid_QR_Code), Toast.LENGTH_SHORT).show();
            scanNextQRCode();
            BackupDatabase.backup(this);
            e.printStackTrace();
        }

    }

    private void showDuplicateDialog(String dialogMode) {
        final CustomLodingDialog dialog = new CustomLodingDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        if (dialogMode.equalsIgnoreCase("Complete"))
            title.setText(getResources().getString(R.string.Only_5_Students_Allowed));
        else
            title.setText(getResources().getString(R.string.Student_exists)+
                    "...\n"+getResources().getString(R.string.Scan_new_QR));
        dia_btn_green.setText(getResources().getString(R.string.Okay));
        dia_btn_red.setVisibility(View.GONE);
        dia_btn_yellow.setVisibility(View.GONE);

        dialog.show();
        dia_btn_green.setOnClickListener(v -> {
            scanNextQRCode();
            dialog.dismiss();
        });
    }

    @Background
    public void startSession() {
        String currentSession;
        try {
            StatusDao statusDao = appDatabase.getStatusDao();
            currentSession = "" + UUID.randomUUID().toString();
            FastSave.getInstance().saveString(FC_Constants.CURRENT_SESSION, currentSession);
            statusDao.updateValue("CurrentSession", "" + currentSession);

            Session startSesion = new Session();
            startSesion.setSessionID("" + currentSession);
            String timerTime = FC_Utility.getCurrentDateTime();

            Log.d("doInBackground", "--------------------------------------------doInBackground : " + timerTime);
            startSesion.setFromDate(timerTime);
            startSesion.setToDate("NA");
            startSesion.setSentFlag(0);
            appDatabase.getSessionDao().insert(startSesion);

            for (int i = 0; i < playerModalList.size(); i++) {
                Attendance attendance = new Attendance();
                attendance.setSessionID("" + currentSession);
                attendance.setDate(timerTime);
                attendance.setStudentID("" + playerModalList.get(i).getStudentID());
                attendance.setGroupID("QR");
                appDatabase.getAttendanceDao().insert(attendance);
            }

            BackupDatabase.backup(QRScanActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    public void enterStudentData(final List<PlayerModal> finalPlayerModalList) {
        try {

            for (int i = 0; i < finalPlayerModalList.size(); i++) {
                Student student = new Student();
                student.setStudentID("" + finalPlayerModalList.get(i).getStudentID());
                student.setFullName("" + finalPlayerModalList.get(i).getStudentName());
                student.setFirstName("" + finalPlayerModalList.get(i).getStudentName());
                student.setGender("NA");
                student.setAge(0);
                student.setAvatarName("NA");
                student.setSentFlag(0);
                student.setNewFlag(1);
                String studentName = appDatabase.getStudentDao().
                        checkStudent("" + finalPlayerModalList.get(i).getStudentID());

                if (studentName == null)
                    appDatabase.getStudentDao().insert(student);
            }
            BackupDatabase.backup(QRScanActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}