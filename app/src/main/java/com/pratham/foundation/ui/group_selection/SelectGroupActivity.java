package com.pratham.foundation.ui.group_selection;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.ui.group_selection.fragment_select_group.FragmentSelectGroup;
import com.pratham.foundation.ui.group_selection.fragment_select_group.FragmentSelectGroup_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import static com.pratham.foundation.ApplicationClass.BackBtnSound;

@EActivity(R.layout.activity_select_group)
public class SelectGroupActivity extends BaseActivity {

    @ViewById(R.id.main_layout)
    RelativeLayout main_layout;
    @ViewById(R.id.frame_group_frg)
    FrameLayout frame_group;
    public static int grpResumeCounter = 0;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Bundle bundle = new Bundle();
        bundle.putBoolean(FC_Constants.GROUP_AGE_ABOVE_7, true);
        FC_Utility.showFragment(this, new FragmentSelectGroup_(), R.id.frame_group_frg,
                bundle, FragmentSelectGroup.class.getSimpleName());
//        FC_Utility.showFragment(this, new MenuFragment_(), R.id.frame_group,
//                null, MenuFragment.class.getSimpleName());
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

    @Override
    public void onBackPressed() {
        try {
            BackBtnSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            EventMessage message = new EventMessage();
            message.setMessage("reload");
            EventBus.getDefault().post(message);
            exitDialog();
        }
    }
}