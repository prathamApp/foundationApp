package com.pratham.foundation.ui.bottom_fragment.add_student;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.admin_panel.AdminControlsActivity_;
import com.pratham.foundation.ui.admin_panel.group_selection.SelectGroupActivity_;
import com.pratham.foundation.ui.qr_scan.QRScanActivity_;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import static com.pratham.foundation.utility.SplashSupportActivity.ButtonClickSound;


@EActivity(R.layout.activity_menu)
public class MenuActivity extends BaseActivity {

    @ViewById(R.id.btn_qr)
    ImageButton btn_qr;
    @ViewById(R.id.btn_grp)
    ImageButton btn_grp;
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
        if (!FastSave.getInstance().getBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, false))
            show_STT_Dialog();
    }

    private void showLoginDialog(String nextActivity) {
        Dialog dialog = new Dialog(MenuActivity.this);
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
    }

    private void show_STT_Dialog() {
        Dialog dialog = new Dialog(MenuActivity.this);
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
        dia_title.setText(getResources().getString(R.string.Stt_Dialog_Msg));
        dia_btn_green.setText(getResources().getString(R.string.Okay));
        dia_btn_red.setText(getResources().getString(R.string.Skip));
        dia_btn_yellow.setVisibility(View.GONE);

        dia_btn_green.setOnClickListener(v -> {
            FastSave.getInstance().saveBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, true);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName("com.google.android.googlequicksearchbox",
                    "com.google.android.voicesearch.greco3.languagepack.InstallActivity"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            dialog.dismiss();
        });

        dia_btn_red.setOnClickListener(v -> dialog.dismiss());

    }

    @Click(R.id.btn_qr)
    public void gotoQRActivity() {
        ButtonClickSound.start();
        showLoginDialog("QRScan");
    }

    @Click(R.id.btn_grp)
    public void gotoGroupLogin() {
        ButtonClickSound.start();
        showLoginDialog("SelectGroup");
    }

    private void gotoNext(String nextActivity) {
        if(nextActivity.equalsIgnoreCase("SelectGroup")) {
            startActivity(new Intent(this, SelectGroupActivity_.class));
            finish();
        }else {
            startActivity(new Intent(this, QRScanActivity_.class));
            finish();
        }
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
    public void pressedBackButton(){
        showExitDialog();
    }

    @Override
    public void onBackPressed() {
        btn_back.performClick();
    }

    @SuppressLint("SetTextI18n")
    private void showExitDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText (getResources().getString(R.string.Restart));
        dia_btn_red.setText   (getResources().getString(R.string.Exit));
        dia_btn_yellow.setText(getResources().getString(R.string.Cancel));
        dia_btn_green.setVisibility(View.GONE);
        dialog.show();

        dia_btn_red.setOnClickListener(v -> {
            finishAffinity();
            dialog.dismiss();
        });
        dia_btn_green.setOnClickListener(v -> dialog.dismiss());

        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
    }

}