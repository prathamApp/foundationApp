package com.pratham.foundation.ui.splash_activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hanks.htextview.typer.TyperTextView;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.interfaces.Interface_copying;
import com.pratham.foundation.interfaces.PermissionResult;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.services.AppExitService;
import com.pratham.foundation.services.background_service.BackgroundPushService;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.admin_panel.MenuActivity_;
import com.pratham.foundation.ui.bottom_fragment.BottomStudentsFragment;
import com.pratham.foundation.ui.bottom_fragment.BottomStudentsFragment_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;
import com.pratham.foundation.utility.PermissionUtils;
import com.pratham.foundation.utility.SplashSupportActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import static com.pratham.foundation.utility.FC_Constants.BOTTOM_FRAGMENT_CLOSED;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_VERSION;
import static com.pratham.foundation.utility.FC_Constants.IS_SERVICE_STOPED;
import static com.pratham.foundation.utility.FC_Constants.SPLASH_OPEN;


@EActivity(R.layout.activity_splash)
public class SplashActivity extends SplashSupportActivity implements SplashContract.SplashView,
        PermissionResult, Interface_copying {

    @ViewById(R.id.splash_root)
    RelativeLayout splash_root;
    @ViewById(R.id.tv_typer)
    TyperTextView tv_typer;
    static String fpath, appname;
    public static MediaPlayer bgMusic;
    public ProgressDialog progressDialog;

    @Bean(SplashPresenter.class)
    SplashPresenter splashPresenter;

    @SuppressLint("StaticFieldLeak")
    static Context context;
    Dialog dialog;
    private int startCheck = 0;
    public static boolean firstPause = true, fragmentBottomOpenFlg = false,
            fragmentBottomPauseFlg = false, fragmentAddStudentPauseFlg = false,
            fragmentAddStudentOpenFlg = false, exitDialogOpen = false;

    @AfterViews
    public void init() {
//        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
//        bgMusic = MediaPlayer.create(this, R.raw.bg_sound);
//        bgMusic.setLooping(true);
//        bgMusic.start();
        startCheck = 0;
        new Handler().postDelayed(this::startTextAud, 500);
    }

    private void startTextAud() {
        //Set Animation for app title
        SPLASH_OPEN = true;
        final Typeface title_font = Typeface.createFromAsset(getAssets(), "fonts/GlacialIndifference-Bold.otf");
        tv_typer.setTypeface(title_font);
        tv_typer.setVisibility(View.VISIBLE);
        tv_typer.setTextColor(getResources().getColor(R.color.dark_blue));
        tv_typer.animateText("Foundation\nCourse");
        tv_typer.setAnimationListener(hTextView -> initiateApp());
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            bgMusic = MediaPlayer.create(this, R.raw.bg_sound);
            bgMusic.setLooping(true);
            bgMusic.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(() -> {
            if (!ApplicationClass.getAppMode()) {
                EventMessage message = new EventMessage();
                message.setMessage("reload");
                EventBus.getDefault().post(message);
            } else {
                if (startCheck > 0)
                    gotoNextActivity();
            }
        }, 300);
    }

    public void initiateApp() {
        //Setting the instance of view in the presenter.
        splashPresenter.setView(this);
        context = SplashActivity.this;
        dialog = new ProgressDialog(this);
        fpath = "";
        appname = "";

        FastSave.getInstance().saveString(FC_Constants.LOGIN_MODE, "NA");
        //Get Permissions required for app
        String[] permissionArray = new String[]{PermissionUtils.Manifest_CAMERA,
                PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_RECORD_AUDIO,
                PermissionUtils.Manifest_ACCESS_COARSE_LOCATION,
                PermissionUtils.Manifest_ACCESS_FINE_LOCATION
        };
        //Create Directory if not exists
        if (!new File(Environment.getExternalStorageDirectory() + "/PrathamBackups").exists())
            new File(Environment.getExternalStorageDirectory() + "/PrathamBackups").mkdir();
        if (!new File(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal").exists())
            new File(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal").mkdir();

        new Handler().postDelayed(() -> {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                if (!isPermissionsGranted(SplashActivity.this, permissionArray))
                    askCompactPermissions(permissionArray, SplashActivity.this);
                else
                    startApp();
            } else
                startApp();
        }, 500);
        // Setting the app local to ensure that the instruction and other strings come in the specified language.
        String a = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI);
        Log.d("INSTRUCTIONFRAG", "Splash Act: " + a);
        FC_Utility.setAppLocal(this, a);
    }

    @UiThread
    @Override
    public void showButton() {
        //App Exit service started
        context.startService(new Intent(context, AppExitService.class));
        if (!FastSave.getInstance().getBoolean(FC_Constants.KEY_MENU_COPIED, false)) {
            if (!ApplicationClass.getAppMode()) {
                ApplicationClass.contentExistOnSD = false;
                splashPresenter.copyZipAndPopulateMenu();
            } else {
                ApplicationClass.foundationPath = FC_Utility.getInternalPath(SplashActivity.this);
                Log.d("old_cos.pradigiPath", "old_cos.pradigiPath: " + ApplicationClass.foundationPath);
                splashPresenter.getSdCardPath();
                ApplicationClass.contentExistOnSD = true;
                splashPresenter.populateSDCardMenu();
            }
        } else
            gotoNextActivity();
    }

    @UiThread
    @Override
    public void showUpdateDialog() {
        //Update to latest version
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Upgrade to a better version !");
        builder.setCancelable(false);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Click button action
                dialog.dismiss();
                if (FC_Utility.isDataConnectionAvailable(SplashActivity.this)) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.pratham.foundation.database")));
                    finish();
                } else {
                    FC_Utility.showAlertDialogue(SplashActivity.this, "No internet connection! Try updating later.");
                    startApp();
                }
            }
        });
        builder.show();
    }

    @UiThread
    @Override
    public void startApp() {
        //Sets required resolution, language, database and continue the app flow
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Configuration config = context.getResources().getConfiguration();
        String strwidth = String.valueOf(width);
        String strheight = String.valueOf(height);
        String resolution = strwidth + "px x " + strheight + "px (" + config.densityDpi + " dpi)";
        FastSave.getInstance().saveString(FC_Constants.SCR_RES, "" + resolution);
        FastSave.getInstance().saveString(FC_Constants.LANGUAGE, FC_Constants.HINDI);
//        setAppLocal(this, FC_Constants.HINDI);
        FastSave.getInstance().saveBoolean(IS_SERVICE_STOPED, false);
        splashPresenter.createDatabase();
    }


    Intent mServiceIntent;
    private BackgroundPushService bgPushService;

    @Override
    protected void onDestroy() {
        try {
            stopService(mServiceIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("MAINACT", "isMyServiceRunning?  " + true);
                return true;
            }
        }
        Log.i("MAINACT", "isMyServiceRunning?  " + false);
        return false;
    }

    @Override
    @UiThread
    public void showProgressDialog() {
        try {
            progressDialog = new ProgressDialog(SplashActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(getResources().getString(R.string.loding_please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @UiThread
    public void dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fragmentBottomOpenFlg && fragmentBottomPauseFlg) {
            try {
                if (bgMusic != null && bgMusic.isPlaying()) {
                    bgMusic.setLooping(false);
                    bgMusic.stop();
                    bgMusic.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (firstPause) {
            try {
                if (bgMusic != null && bgMusic.isPlaying()) {
                    bgMusic.setLooping(false);
                    bgMusic.stop();
                    bgMusic.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!ApplicationClass.getAppMode())
            showExitDialog();
    }

    BlurPopupWindow exitDialog;
    Handler backHandler;

    @UiThread
    @SuppressLint("SetTextI18n")
    public void showExitDialog() {
        exitDialog = new BlurPopupWindow.Builder(this)
                .setContentView(R.layout.lottie_exit_dialog)
                .bindClickListener(v -> {
                    exitDialog.dismiss();
                    backHandler.removeCallbacksAndMessages(null);
                    new Handler().postDelayed(this::finishAffinity, 200);
                }, R.id.dia_btn_yes)
                .bindClickListener(v -> {
                    if (!ApplicationClass.getAppMode())
                        gotoNextActivity();
                    backHandler.removeCallbacksAndMessages(null);
                    exitDialogOpen = false;
                    new Handler().postDelayed(() -> exitDialog.dismiss(), 200);
                }, R.id.dia_btn_no)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(true)
                .setDismissOnClickBack(true)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();
        exitDialog.show();
        exitDialogOpen = true;
        backHandler = new Handler();
        backHandler.postDelayed(() -> {
            if (!ApplicationClass.getAppMode())
                gotoNextActivity();
            exitDialogOpen = false;
            exitDialog.dismiss();
        }, 5000);
    }

    @Override
    public void permissionGranted() {
        startApp();
    }

    @Override
    public void permissionDenied() {
    }

    @Override
    public void permissionForeverDenied() {
    }

    @Override
    @UiThread
    public void preShowBtn() {
        new Handler().postDelayed(this::showButton, 2000);
    }

    @UiThread
    @Override
    public void gotoNextActivity() {
        startCheck++;
        //Required folders are created if previously not exist
        File direct;
        direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal");
        if (!direct.exists())
            direct.mkdir();
        direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/ActivityPhotos");
        if (!direct.exists())
            direct.mkdir();
        direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/SupervisorImages");
        if (!direct.exists())
            direct.mkdir();

        splashPresenter.createNoMediaForFCInternal(new File(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal"));

        if (!FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false))
            splashPresenter.doInitialEntries(AppDatabase.getDatabaseInstance(context));
        splashPresenter.requestLocation();
        //        if(FastSave.getInstance().getBoolean(FC_Constants.newDataLanguageInserted, false))
//            splashPresenter.insertNewData();
        splashPresenter.updateVersionApp();
        if (!ApplicationClass.getAppMode()) {
//            Temporary
            bgPushService = new BackgroundPushService();
            mServiceIntent = new Intent(this, bgPushService.getClass());
            if (!isMyServiceRunning(bgPushService.getClass())) {
                startService(mServiceIntent);
            }
//            splashPresenter.pushData();
            dismissProgressDialog();
            Log.d("-CT-", "Before insert new  :::::CURRENT_VERSION::::: " + FastSave.getInstance().getString(CURRENT_VERSION, "NA"));
            Log.d("-CT-", "Before insert new  ::::getCurrentVersion:::: " + FC_Utility.getCurrentVersion(context));
            if (!FastSave.getInstance().getString(CURRENT_VERSION, "NA").equalsIgnoreCase(FC_Utility.getCurrentVersion(context))) {
                Log.d("-CT-", "insertNewData in IFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
                splashPresenter.copyZipAndPopulateMenu_New();
            } else {
                Log.d("-CT-", "Before insert new  :::::VOICES_DOWNLOAD_INTENT::::: " + FastSave.getInstance().getBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, false));
                if (!FastSave.getInstance().getBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, false))
                    show_STT_Dialog();
                else
                    showBottomFragment();
            }
        } else {
            bgPushService = new BackgroundPushService();
            mServiceIntent = new Intent(this, bgPushService.getClass());
            if (!isMyServiceRunning(bgPushService.getClass())) {
                startService(mServiceIntent);
            }
            dismissProgressDialog();
            startActivity(new Intent(this, MenuActivity_.class));
            finish();
        }
    }

    @UiThread
    @Override
    public void show_STT_Dialog() {
        //Allows to download language packages
        exitDialog = new BlurPopupWindow.Builder(this)
                .setContentView(R.layout.lottie_stt_dialog)
                .bindClickListener(v -> {
                    FastSave.getInstance().saveBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, true);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("com.google.android.googlequicksearchbox",
                            "com.google.android.voicesearch.greco3.languagepack.InstallActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    new Handler().postDelayed(() -> {
                        showBottomFragment();
                        exitDialog.dismiss();
                    }, 200);
                }, R.id.dia_btn_ok)
                .bindClickListener(v -> {
                    showBottomFragment();
                    new Handler().postDelayed(() -> exitDialog.dismiss(), 200);
                }, R.id.dia_btn_skip)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(false)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();
        exitDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    @Subscribe
    public void messageRecieved(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(BOTTOM_FRAGMENT_CLOSED))
                onBackPressed();
        }
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
    @Override
    public void showBottomFragment() {
        try {
            fragmentBottomOpenFlg = true;
            firstPause = false;
            BottomStudentsFragment_ bottomStudentsFragment = new BottomStudentsFragment_();
            bottomStudentsFragment.show(getSupportFragmentManager(), BottomStudentsFragment.class.getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent objEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyUp(keyCode, objEvent);
    }


    @Override
    public void copyingExisting() {
    }

    @Override
    public void successCopyingExisting(String path) {
    }

    @Override
    public void failedCopyingExisting() {
    }
}