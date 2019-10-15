package com.pratham.foundation.ui.contentPlayer;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_fragment.FactRetrival_;
import com.pratham.foundation.ui.contentPlayer.sequenceLayout.SequenceLayout_;
import com.pratham.foundation.utility.FC_Utility;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;


public class ContentPlayerActivity extends BaseActivity{

    private String contentPath, contentTitle, StudentID, resId;
    boolean onSdCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_player);
        Log.d("Content_Player", "ContentPlayerActivity");

        Intent intent = getIntent();
        contentPath = intent.getStringExtra("contentPath");
        StudentID = intent.getStringExtra("StudentID");
        resId = intent.getStringExtra("resId");
        contentTitle = intent.getStringExtra("contentName");
        onSdCard = getIntent().getBooleanExtra("onSdCard", false);
        loadFragment();
    }

    public void loadFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("contentPath", contentPath);
        bundle.putString("StudentID", StudentID);
        bundle.putString("resId", resId);
        bundle.putString("contentName", contentTitle);
        bundle.putBoolean("onSdCard", onSdCard);
        FC_Utility.showFragment(ContentPlayerActivity.this, new SequenceLayout_(), R.id.RL_CPA,
                bundle, SequenceLayout_.class.getSimpleName());
    }


    @Override
    public void onBackPressed() {
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

        dia_btn_green.setText ("Yes");
        dia_btn_red.setText   ("No");
        dia_btn_yellow.setText(""+dialog_btn_cancel);
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
                finish();
                dialog.dismiss();
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

