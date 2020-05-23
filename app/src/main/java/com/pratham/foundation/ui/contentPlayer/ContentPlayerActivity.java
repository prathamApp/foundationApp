package com.pratham.foundation.ui.contentPlayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.interfaces.ShowInstruction;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ScoreEvent;
import com.pratham.foundation.services.shared_preferences.FastSave;
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

import static com.pratham.foundation.ApplicationClass.BackBtnSound;
import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.INFO_CLICKED;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

@EActivity(R.layout.activity_content_player)
public class ContentPlayerActivity extends BaseActivity implements ShowInstruction {

    private String nodeID, title, cCode;
    private Intent returnIntent;
    boolean onSdCard;

    @ViewById(R.id.floating_back)
    public static FloatingActionButton floating_back;
    @ViewById(R.id.floating_info)
    public static FloatingActionButton floating_info;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Intent intent = getIntent();
        nodeID = intent.getStringExtra("nodeID");
        title = intent.getStringExtra("title");
        onSdCard = intent.getBooleanExtra("onSdCard", false);
        String testcall = intent.getStringExtra("testcall");

        floating_back.setImageResource(R.drawable.ic_left_arrow_white);
        floating_info.setImageResource(R.drawable.ic_info_outline_white);

        if (testcall == null) {
            loadFragment();
        } else {
            ContentTable testData = (ContentTable) intent.getSerializableExtra("testData");
            cCode = testData.getNodeDesc();
            //  GameConstatnts.gameList = contentTableList;
            // GameConstatnts.currentGameAdapterposition=myViewHolder.getAdapterPosition();
            GameConstatnts.playInsequence = false;
            GameConstatnts.gameSelector(this, testData,onSdCard);
            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                returnIntent = new Intent();
                returnIntent.putExtra("cCode", cCode);
                returnIntent.putExtra("tMarks", 0);
                returnIntent.putExtra("sMarks", 0);
                setResult(Activity.RESULT_OK, returnIntent);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void loadFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("nodeID", nodeID);
        bundle.putString("title", title);
        bundle.putBoolean("onSdCard", onSdCard);
        FC_Utility.showFragment(ContentPlayerActivity.this, new SequenceLayout_(), R.id.RL_CPA,
                bundle, SequenceLayout_.class.getSimpleName());
    }

    @UiThread
    @Click(R.id.floating_back)
    public void pressedFloatingBackBtn() {
        onBackPressed();
    }

/*    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }*/

    @UiThread
    @Click(R.id.floating_info)
    public void pressedFloatingInfo() {
        try {
            BackBtnSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
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

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scoreEventReceived(ScoreEvent scoreEvent) {
        if (scoreEvent != null) {
            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                if (scoreEvent.getMessage().equalsIgnoreCase(FC_Constants.RETURNSCORE)) {
                    returnIntent.putExtra("tMarks", scoreEvent.getTotalCount());
                    returnIntent.putExtra("sMarks", scoreEvent.getScoredMarks());
                    // Toast.makeText(this, ""+scoreEvent.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        try {
            BackBtnSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SequenceLayout_.class.getSimpleName());
        if (fragment != null && fragment.isVisible())
            finish();
        else
            showExitDialog();
    }

    private void showExitDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText(getResources().getString(R.string.yes));
        //dia_btn_red.setText("No");
        dia_btn_red.setVisibility(View.GONE);
        dia_btn_yellow.setText(getResources().getString(R.string.Cancel));
        dia_title.setText(getResources().getString(R.string.exit_dialog_msg));
        dialog.show();

        dia_btn_green.setOnClickListener(v -> {
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
                //if game opened from test
                if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                    if (f.getActivity() instanceof ContentPlayerActivity) {
                        finish();
                    } else {
                        getSupportFragmentManager().popBackStack(SequenceLayout_.class.getSimpleName(), 0);
                    }
                }else {
                    getSupportFragmentManager().popBackStack(SequenceLayout_.class.getSimpleName(), 0);
                }
            }

        });

        dia_btn_red.setOnClickListener(v -> dialog.dismiss());

        dia_btn_yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void hideFloating_info() {
        floating_info.hide();
    }

    public void showFloating_info() {
        floating_info.show();
    }

    @Override
    public void play(Context context) {

    }

    @Override
    public void exit() {

    }
}

