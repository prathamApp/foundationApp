package com.pratham.foundation.ui.contentPlayer.image_resource;

import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifViewZoom;
import com.pratham.foundation.customView.ZoomageView;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

@EActivity(R.layout.activity_display_image)
public class DisplayImageActivity extends BaseActivity{

    @ViewById(R.id.title_tv)
    TextView title_tv;
    @ViewById(R.id.close_ib)
    ImageButton close_ib;
    @ViewById(R.id.iv_zoom_img)
    ZoomageView iv_zoom_img;
    @ViewById(R.id.gifView)
    GifViewZoom gifView;
    
    String imgPath, dia,resId,contentName,resStartTime;
    boolean diaFlg = false, onSdCard;

    @AfterViews
    public void initiate() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dia = getIntent().getStringExtra("dia");
        if (dia.equalsIgnoreCase("dia"))
            diaFlg = true;
        if (!diaFlg) {
            resId = getIntent().getStringExtra("resId");
            imgPath = getIntent().getStringExtra("contentPath");
            contentName = getIntent().getStringExtra("contentName");
            onSdCard = getIntent().getBooleanExtra("onSdCard", false);

            if (onSdCard)
                imgPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + imgPath;
            else
                imgPath = ApplicationClass.foundationPath + gameFolderPath + "/" + imgPath;
//            imgPath = ApplicationClass.getStoragePath() + "/PrathamBackups/story.pdf";

            resStartTime = FC_Utility.getCurrentDateTime();

                    title_tv.setText(""+contentName);
            String a = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI);
            Log.d("INSTRUCTIONFRAG", "Select Subj: " + a);
            FC_Utility.setAppLocal(this, a);

        }else{
            imgPath = getIntent().getStringExtra("contentPath");
        }
        hideSystemUI();
        if (new File(imgPath).exists())
            displayImage(imgPath);
        else
            Toast.makeText(this, "problem Loading Image", Toast.LENGTH_SHORT).show();
    }

    public void displayImage(String path){
        if (path != null) {
            String[] imgPath = path.split("\\.");
            int len;
            if (imgPath.length > 0)
                len = imgPath.length - 1;
            else len = 0;
            if (imgPath[len].equalsIgnoreCase("gif")) {
                try {
                    InputStream gif = new FileInputStream(path);
                    iv_zoom_img.setVisibility(View.GONE);
                    gifView.setVisibility(View.VISIBLE);
                    gifView.setGifResource(gif);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    iv_zoom_img.setVisibility(View.VISIBLE);
                    Bitmap bmImg = BitmapFactory.decodeFile(path);
                    BitmapFactory.decodeStream(new FileInputStream(path));
                    iv_zoom_img.setImageBitmap(bmImg);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                gifView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Click(R.id.close_ib)
    public void closePDF() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    @UiThread
    public void showExitDialog() {
        CustomLodingDialog dialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_exit_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText(getResources().getString(R.string.yes));
        dia_btn_red.setText(getResources().getString(R.string.no));
        dia_title.setText(getResources().getString(R.string.exit_dialog_msg));
        dialog.show();
        dia_btn_green.setOnClickListener(v -> {
            if(!diaFlg){
                addScoreToDB();
                addGameProgress();
            }
            dialog.dismiss();
            new Handler().postDelayed(this::finish, 100);
        });
        dia_btn_red.setOnClickListener(v -> dialog.dismiss());
    }

    @Background
    public void addScoreToDB() {
        try {
            String endTime = FC_Utility.getCurrentDateTime();
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setStudentID("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setDeviceID(FC_Utility.getDeviceID());
            score.setResourceID(resId);
            score.setQuestionId(0);
            score.setScoredMarks(10);
            score.setTotalMarks(10);
            score.setStartDateTime(resStartTime);
            score.setEndDateTime(endTime);
            score.setLevel(0);
            score.setLabel("IMAGE_RES");
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(this).getScoreDao().insert(score);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @SuppressLint("StaticFieldLeak")
    public void addGameProgress() {
        try {
            ContentProgress contentProgress = new ContentProgress();
            contentProgress.setProgressPercentage("" + 100);
            contentProgress.setResourceId("" + resId);
            contentProgress.setSessionId("" + FastSave
                    .getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            contentProgress.setStudentId("" + FastSave
                    .getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("resourceProgress");
            contentProgress.setSentFlag(0);
            AppDatabase.getDatabaseInstance(DisplayImageActivity.this).getContentProgressDao().insert(contentProgress);
            BackupDatabase.backup(DisplayImageActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void hideSystemUI() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }
}