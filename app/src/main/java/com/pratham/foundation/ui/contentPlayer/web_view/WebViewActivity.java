package com.pratham.foundation.ui.contentPlayer.web_view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.interfaces.WebViewInterface;
import com.pratham.foundation.modalclasses.CertificateModelClass;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

//TODO ADD ANNOTATIONS
@EActivity(R.layout.activity_web_view)
public class WebViewActivity extends BaseActivity implements WebViewInterface {

    public static int tMarks, sMarks;
    @ViewById(R.id.loadPage)
    WebView webView;
    String gamePath, currentGameName, webViewLang = "NA", resStartTime;
    public static String webResId, gameLevel, mode, cCode, gameType, gameCategory, gameName;
    TextToSpeechCustom tts;
    public static int gameCounter = 0, arraySize, dataTotalLength;
    int position;
    public static List<CertificateModelClass> certificateModelClassList;
    public static List<KeyWords> learntWordsList;

    @AfterViews
    public void initialize(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        webResId = getIntent().getStringExtra("resId");
        gamePath = getIntent().getStringExtra("resPath");
        mode = getIntent().getStringExtra("mode");
        gameLevel = getIntent().getStringExtra("gameLevel");
        gameType = getIntent().getStringExtra("gameType");
        gameName = getIntent().getStringExtra("gameName");
        gameCategory = getIntent().getStringExtra("gameCategory");

        startWebViewAct();
        /*
        CertificateModelClass certificateModelClass=new CertificateModelClass();
        certificateModelClass.setScoredMarks(50);
        certificateModelClass.setTotalMarks(100);
        certificateModelClass.setCertiCode("2");
        certificateModelClassList.add(certificateModelClass);
        certificateModelClass=new CertificateModelClass();
        certificateModelClass.setScoredMarks(5);
        certificateModelClass.setTotalMarks(10);
        certificateModelClass.setCertiCode("4");
        certificateModelClassList.add(certificateModelClass);
        certificateModelClass=new CertificateModelClass();
        certificateModelClass.setScoredMarks(3);
        certificateModelClass.setTotalMarks(10);
        certificateModelClass.setCertiCode("4");
        certificateModelClassList.add(certificateModelClass);
        certificateModelClass=new CertificateModelClass();
        certificateModelClass.setScoredMarks(9);
        certificateModelClass.setTotalMarks(10);
        certificateModelClass.setCertiCode("3");
        certificateModelClassList.add(certificateModelClass);
*/
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
        sMarks = 0;
        tMarks = 0;
        cCode = "NA";
        Log.d("WevViewLevel", "onCreate: " + gameLevel);

        try {
            tts = new TextToSpeechCustom(this, 0.6f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        gameCounter = 0;
        resStartTime = FC_Utility.getCurrentDateTime();
        createWebView(gamePath);
        learntWordsList = new ArrayList<>();
        certificateModelClassList = new ArrayList<>();
    }

    @UiThread
    @SuppressLint("JavascriptInterface")
    public void createWebView(String GamePath) {

        String myPath = GamePath;
        webView.loadUrl(myPath);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new JSInterface(this, webView, tts,
                this, WebViewActivity.this), "Android");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.clearCache(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        hideSystemUI();
    }


    @Override
    public void onBackPressed() {
/*        super.onBackPressed();
        webView.loadUrl("about:blank");*/
        showExitDialog();
    }

    @UiThread
    public void showExitDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText("" + getResources().getString(R.string.yes));
        dia_btn_red.setText("" + getResources().getString(R.string.no));
        dia_btn_yellow.setVisibility(View.GONE);
//        setText("" + getResources().getString(R.string.Cancel));
        dia_title.setText("" + getResources().getString(R.string.exit_dialog_msg));
        dialog.show();

        dia_btn_green.setOnClickListener(v -> {
            if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test)) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("cCode", cCode);
                returnIntent.putExtra("tMarks", tMarks);
                returnIntent.putExtra("sMarks", sMarks);
                setResult(Activity.RESULT_OK, returnIntent);
            } else
                addGameProgress();
            dialog.dismiss();
//            this.finishAfterTransition();
            new Handler().postDelayed(() -> finish(),150);
        });

        dia_btn_red.setOnClickListener(v -> dialog.dismiss());

        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
    }

    @Background
    @SuppressLint("StaticFieldLeak")
    public void addGameProgress() {
         try {
                    if (learntWordsList.size() > 0) {
                        for (int i = 0; i < learntWordsList.size(); i++) {
                            boolean wordPresent = checkWord(learntWordsList.get(i).getKeyWord().toLowerCase());
                            if (!wordPresent) {
                                KeyWords learntWords = new KeyWords();
                                learntWords.setResourceId(webResId);
                                learntWords.setSentFlag(0);
                                learntWords.setStudentId(FastSave
                                        .getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                                learntWords.setKeyWord(learntWordsList.get(i).getKeyWord().toLowerCase());
                                learntWords.setWordType("" + learntWordsList.get(i).getWordType());
                                AppDatabase.getDatabaseInstance(WebViewActivity.this).getKeyWordDao().insert(learntWords);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int scoredMarks = AppDatabase.getDatabaseInstance(WebViewActivity.this).getKeyWordDao().checkWebWordCount(FastSave.getInstance()
                        .getString(FC_Constants.CURRENT_STUDENT_ID, ""), "" + webResId);
                float perc = 0f;
                try {
                    if (scoredMarks > 0 && dataTotalLength > 0) {
                        perc = ((float) scoredMarks / (float) dataTotalLength) * 100f;
                    } else
                        perc = 0f;
                } catch (Exception e) {
                    perc = 0f;
                }
                try {
                    ContentProgress contentProgress = new ContentProgress();
                    contentProgress.setProgressPercentage("" + perc);
                    contentProgress.setResourceId("" + webResId);
                    contentProgress.setSessionId("" + FastSave
                            .getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                    contentProgress.setStudentId("" + FastSave
                            .getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                    contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
                    contentProgress.setLabel("resourceProgress");
                    contentProgress.setSentFlag(0);
                    AppDatabase.getDatabaseInstance(WebViewActivity.this).getContentProgressDao().insert(contentProgress);
                    BackupDatabase.backup(WebViewActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

    private boolean checkWord(String checkWord) {
        try {
            String word = AppDatabase.getDatabaseInstance(WebViewActivity.this).getKeyWordDao().checkWord(FastSave.getInstance()
                    .getString(FC_Constants.CURRENT_STUDENT_ID, ""), "" + webResId, checkWord);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @UiThread
    @Override
    public void onNextGame(final WebView w) {
        showExitDialog();
        //        webView.loadUrl("about:blank");
//        finish();

        /*gameCounter += 1;
        if (gameCounter < arraySize) {
            System.out.println(" gameCounter :::::::::::::::::::::::::::::::::: " + gameCounter);
            gamePath = gameTestWebViewList.get(gameCounter).getResourcePath();//
            webResId = gameTestWebViewList.get(gameCounter).getResourceId();
            System.out.println("gamePath :::::: " + gamePath + " :::::: " + webResId);
            w.post(new Runnable() {
                @Override
                public void run() {
                    w.loadUrl(gamePath);
                }
            });
        } else {
            startActivity(new Intent(this, CertificateActivity.class));
            super.onBackPressed();
        }*/
    }

    @Override
    protected void onPause() {
        if (webView != null) {
            webView.loadUrl("");
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.loadUrl(gamePath);
        }
    }
}

