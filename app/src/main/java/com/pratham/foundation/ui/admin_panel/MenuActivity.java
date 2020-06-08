package com.pratham.foundation.ui.admin_panel;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.admin_panel.group_selection.SelectGroupActivity_;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.BackBtnSound;
import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.utility.FC_Constants.GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.INDIVIDUAL_MODE;
import static com.pratham.foundation.utility.FC_Constants.StudentPhotoPath;


@EActivity(R.layout.activity_menu)
public class MenuActivity extends BaseActivity {

    //    @ViewById(R.id.mcv_qr)
//    ImageButton btn_qr;
//    @ViewById(R.id.mcv_group)
//    ImageButton btn_grp;
    @ViewById(R.id.btn_admin)
    ImageButton btn_admin;
    @ViewById(R.id.btn_back)
    ImageButton btn_back;
    @ViewById(R.id.rl_admin)
    RelativeLayout rl_admin;
    @ViewById(R.id.main_menu_layout)
    RelativeLayout main_layout;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        StudentPhotoPath = Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/StudentProfilePhotos/";
        File direct = new File(StudentPhotoPath);
        if (!direct.exists())
            direct.mkdir();
        if (!FastSave.getInstance().getBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, false))
            show_STT_Dialog();
    }


/*    private void showLoginDialog(String nextActivity) {
        Dialog dialog = new Dialog(MenuActivity.this);
    private void showLoginDialog(String nextActivity) {
        Dialog dialog = new CustomLodingDialog(MenuActivity.this, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_title.setTextSize(getResources().getDimension(R.dimen._10sdp));
        dia_title.setText(getResources().getString(R.string.login_mode));
        dia_btn_green.setVisibility(View.GONE);
        dia_btn_red.setText(getResources().getString(R.string.Group));
        dia_btn_yellow.setText(getResources().getString(R.string.Individual));

        dia_btn_red.setOnClickListener(v -> {
            if(nextActivity.equalsIgnoreCase("QRScan"))
            FC_Constants.LOGIN_MODE = FC_Constants.QR_GROUP_MODE;
            else
            FC_Constants.LOGIN_MODE = FC_Constants.GROUP_MODE;
            gotoNext(nextActivity);
            dialog.dismiss();
        });

        dia_btn_yellow.setOnClickListener(v -> {
            FC_Constants.LOGIN_MODE = FC_Constants.INDIVIDUAL_MODE;
            gotoNext(nextActivity);
            dialog.dismiss();
        });
    }*/

    CustomLodingDialog sttDialog;
    @UiThread
    public void show_STT_Dialog() {

        sttDialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
        sttDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(sttDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sttDialog.setContentView(R.layout.lottie_stt_dialog);
        sttDialog.setCanceledOnTouchOutside(false);

//        exitDialog = new BlurPopupWindow.Builder(this)
//                .setContentView(R.layout.fc_custom_dialog)
//                .bindClickListener(v -> {
//                    FastSave.getInstance().saveBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, true);
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.setComponent(new ComponentName("com.google.android.googlequicksearchbox",
//                            "com.google.android.voicesearch.greco3.languagepack.InstallActivity"));
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    new Handler().postDelayed(() -> {
//                        exitDialog.dismiss();
//                    }, 200);
//                }, R.id.dia_btn_green)
//                .bindClickListener(v -> {
//                    new Handler().postDelayed(() -> exitDialog.dismiss(), 200);
//                }, R.id.dia_btn_red)
//                .setGravity(Gravity.CENTER)
//                .setDismissOnTouchBackground(true)
//                .setDismissOnClickBack(true)
//                .setScaleRatio(0.2f)
//                .setBlurRadius(10)
//                .setTintColor(0x30000000)
//                .build();

        TextView dia_btn_skip = sttDialog.findViewById(R.id.dia_btn_skip);
        Button dia_btn_ok = sttDialog.findViewById(R.id.dia_btn_ok);

        dia_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastSave.getInstance().saveBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, true);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.google.android.googlequicksearchbox",
                        "com.google.android.voicesearch.greco3.languagepack.InstallActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                new Handler().postDelayed(() -> {
                    sttDialog.dismiss();
                }, 200);

            }
        });

        dia_btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(() -> {
                    sttDialog.dismiss();
                }, 200);

            }
        });

        sttDialog.show();
    }

    @Click(R.id.mcv_ind)
    public void gotoIndividualLogin() {
        ButtonClickSound.start();
        checkGroupAssigned(INDIVIDUAL_MODE);
//        showLoginDialog("QRScan");
    }

    @Click(R.id.mcv_group)
    public void gotoGroupLogin() {
        ButtonClickSound.start();
        checkGroupAssigned(GROUP_MODE);
//        showLoginDialog("SelectGroup");
    }

    public void checkGroupAssigned(String loginMode) {
        ArrayList<Groups> groups = new ArrayList<>();

//        Toast.makeText(getActivity(), "No groups assigned", Toast.LENGTH_SHORT).show();
        ArrayList<String> allGroups = new ArrayList<>();
        allGroups.add(AppDatabase.getDatabaseInstance(this).getStatusDao().getValue(FC_Constants.GROUPID1));
        allGroups.add(AppDatabase.getDatabaseInstance(this).getStatusDao().getValue(FC_Constants.GROUPID2));
        allGroups.add(AppDatabase.getDatabaseInstance(this).getStatusDao().getValue(FC_Constants.GROUPID3));
        allGroups.add(AppDatabase.getDatabaseInstance(this).getStatusDao().getValue(FC_Constants.GROUPID4));
        allGroups.add(AppDatabase.getDatabaseInstance(this).getStatusDao().getValue(FC_Constants.GROUPID5));

        for (String grID : allGroups) {
            // ArrayList<Student> students = (ArrayList<Student>) BaseActivity.studentDao.getGroupwiseStudents(grID);
            ArrayList<Student> students = (ArrayList<Student>) AppDatabase.getDatabaseInstance(this).getStudentDao().getGroupwiseStudents(grID);
            for (Student stu : students) {
                if (stu.getAge() >= 7) {
                    //Groups group = BaseActivity.groupDao.getGroupByGrpID(grID);
                    Groups group = AppDatabase.getDatabaseInstance(this).getGroupsDao().getGroupByGrpID(grID);
                    groups.add(group);
                    break;
                }
            }
        }
        if (groups.size() > 0) {
            if (loginMode.equalsIgnoreCase(INDIVIDUAL_MODE)) {
                FastSave.getInstance().saveString(FC_Constants.LOGIN_MODE, INDIVIDUAL_MODE);
                startActivity(new Intent(this, SelectGroupActivity_.class));
                finish();
            } else {
                FastSave.getInstance().saveString(FC_Constants.LOGIN_MODE, GROUP_MODE);
                startActivity(new Intent(this, SelectGroupActivity_.class));
                finish();
            }
        } else {
            Toast.makeText(this, "No groups assigned..", Toast.LENGTH_SHORT).show();
        }
    }


    private void gotoNext(String nextActivity) {
        startActivity(new Intent(this, SelectGroupActivity_.class));
        finish();
    }

    @Click({R.id.btn_admin, R.id.rl_admin})
    public void goto_btn_admin() {
        ButtonClickSound.start();
        startActivity(new Intent(this, AdminControlsActivity_.class));
    }
   /* @Click(R.id.btn_share_receive)
    public void goto_share_receive() {
        ButtonClickSound.start();
        startActivity(new Intent(this, ActivityShareReceive_.class));
    }*/

    @Click(R.id.btn_back)
    public void pressedBackButton() {
        try {
            BackBtnSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        exitDialog();
    }

    @Override
    public void onBackPressed() {
        btn_back.performClick();
    }


    BlurPopupWindow exitDialog;

    @UiThread
    @SuppressLint("SetTextI18n")
    public void exitDialog() {
        exitDialog = new BlurPopupWindow.Builder(this)
                .setContentView(R.layout.lottie_exit_dialog)
                .bindClickListener(v -> {
                    new Handler().postDelayed(() -> {
                        exitDialog.dismiss();
                        finishAffinity();
                    }, 200);
                }, R.id.dia_btn_yes)
                .bindClickListener(v -> new Handler().postDelayed(() -> {
                    exitDialog.dismiss();
                }, 200), R.id.dia_btn_no)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(true)
                .setDismissOnClickBack(true)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();
        exitDialog.show();
    }
}