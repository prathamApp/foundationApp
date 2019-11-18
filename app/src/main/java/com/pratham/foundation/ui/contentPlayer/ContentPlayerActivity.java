package com.pratham.foundation.ui.contentPlayer;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.ui.contentPlayer.sequenceLayout.SequenceLayout_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.pratham.foundation.utility.FC_Constants.INFO_CLICKED;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;


@EActivity(R.layout.activity_content_player)
public class ContentPlayerActivity extends BaseActivity {

    private String nodeID;

    @ViewById(R.id.floating_back)
    FloatingActionButton floating_back;
    @ViewById(R.id.floating_info)
    FloatingActionButton floating_info;

    @AfterViews
    public void initialize() {
        Intent intent = getIntent();
        nodeID = intent.getStringExtra("nodeID");
/*
 contentPath = intent.getStringExtra("contentPath");
        StudentID = intent.getStringExtra("StudentID");
        resId = intent.getStringExtra("resId");
        contentTitle = intent.getStringExtra("contentName");
        onSdCard = getIntent().getBooleanExtra("onSdCard", false);
*/
        floating_back.setImageResource(R.drawable.ic_left_arrow_white);
        floating_info.setImageResource(R.drawable.ic_info_outline_white);
        loadFragment();
    }

    public void loadFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("nodeID", nodeID);
        FC_Utility.showFragment(ContentPlayerActivity.this, new SequenceLayout_(), R.id.RL_CPA,
                bundle, SequenceLayout_.class.getSimpleName());
    }

    @UiThread
    @Click(R.id.floating_back)
    public void pressedFloatingBackBtn(){
        onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @UiThread
    @Click(R.id.floating_info)
    public void pressedFloatingInfo(){
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessage(INFO_CLICKED);
        EventBus.getDefault().post(eventMessage);
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(FC_Constants.LEVEL_CHANGED)) {
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SequenceLayout_.class.getSimpleName());
        if (fragment != null && fragment.isVisible())
            finish();
        else
            showExitDialog();
    }

    private void showExitDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText("Yes");
        dia_btn_red.setText("No");
        dia_btn_yellow.setText("" + dialog_btn_cancel);
        dia_title.setText("Exit the game?");
        dialog.show();

        dia_btn_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!FC_Constants.isTest)
//                    addGameProgress();
//                if(FC_Constants.isTest){
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("cCode", cCode);
//                    returnIntent.putExtra("tMarks", tMarks);
//                    returnIntent.putExtra("sMarks", sMarks);
//                    setResult(Activity.RESULT_OK, returnIntent);
//                }
                //finish();

                dialog.dismiss();

                //if backpress when game list is shown then finish activity othewise popbackstack all fragmets Exclusive (sequence fdragment)
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(SequenceLayout_.class.getSimpleName());
                if (fragment != null && fragment.isVisible()) {
                    finish();
                } else {
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.RL_CPA);
                    if (f instanceof OnGameClose) {
                        ((OnGameClose) f).gameClose();
                    }
                    getSupportFragmentManager().popBackStack(SequenceLayout_.class.getSimpleName(), 0);
                }

            }
        });

        dia_btn_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dia_btn_yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}

