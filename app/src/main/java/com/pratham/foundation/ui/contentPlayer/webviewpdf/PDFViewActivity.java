package com.pratham.foundation.ui.contentPlayer.webviewpdf;

import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
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

import java.io.File;
import java.util.Objects;

@EActivity(R.layout.activity_pdf_view)
public class PDFViewActivity extends BaseActivity {

    private String contentName, pdf_Path, resStartTime, dia;
    private String resId;
    private final boolean isScoreAdded = false;
    private boolean onSdCard = false;
    private boolean diaFlg = false;

    @AfterViews
    public void initialize() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dia = getIntent().getStringExtra("dia");
        if (dia.equalsIgnoreCase("dia"))
            diaFlg = true;
        if (!diaFlg) {
            resId = getIntent().getStringExtra("resId");
            pdf_Path = getIntent().getStringExtra("contentPath");
            contentName = getIntent().getStringExtra("contentName");
            onSdCard = getIntent().getBooleanExtra("onSdCard", false);

            if (onSdCard)
                pdf_Path = ApplicationClass.contentSDPath + gameFolderPath + "/" + pdf_Path;
            else
                pdf_Path = ApplicationClass.foundationPath + gameFolderPath + "/" + pdf_Path;
//            pdf_Path = FC_Utility.getStoragePath() + "/PrathamBackups/story.pdf";


            String a = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI);
            Log.d("INSTRUCTIONFRAG", "Select Subj: " + a);
            FC_Utility.setAppLocal(this, a);

        }else{
            pdf_Path = getIntent().getStringExtra("contentPath");
        }
        if (new File(pdf_Path).exists())
            startWebViewAct();
        else
            Toast.makeText(this, "PDF not found", Toast.LENGTH_SHORT).show();
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

    private void startWebViewAct() {
        resStartTime = FC_Utility.getCurrentDateTime();
        PDFView pdfView = findViewById(R.id.pdfView);
        pdfView.fromFile(new File(pdf_Path))
                .enableSwipe(true)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(false)
                .enableSwipe(true)
                .enableAnnotationRendering(false)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true)
                .spacing(5)
                .pageFitPolicy(FitPolicy.WIDTH)
                .load();
        hideSystemUI();
    }

    @Click(R.id.close)
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
            score.setLabel("PDF");
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
            AppDatabase.getDatabaseInstance(PDFViewActivity.this).getContentProgressDao().insert(contentProgress);
            BackupDatabase.backup(PDFViewActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

