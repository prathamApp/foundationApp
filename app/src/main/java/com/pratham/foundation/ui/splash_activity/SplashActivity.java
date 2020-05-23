package com.pratham.foundation.ui.splash_activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
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
import static com.pratham.foundation.utility.FC_Constants.SPLASH_OPEN;
import static com.pratham.foundation.utility.FC_Utility.setAppLocal;


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
    public static boolean firstPause = true, fragmentBottomOpenFlg = false,
            fragmentBottomPauseFlg = false, fragmentAddStudentPauseFlg = false,
            fragmentAddStudentOpenFlg = false, exitDialogOpen = false;

    @AfterViews
    public void init() {
//        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
//        bgMusic = MediaPlayer.create(this, R.raw.bg_sound);
//        bgMusic.setLooping(true);
//        bgMusic.start();
        new Handler().postDelayed(this::startTextAud, 500);
    }

    private void startTextAud() {
        SPLASH_OPEN = true;
        final Typeface title_font = Typeface.createFromAsset(getAssets(), "fonts/Sarala_Bold.ttf");
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
            if (!ApplicationClass.isTablet) {
                EventMessage message = new EventMessage();
                message.setMessage("reload");
                EventBus.getDefault().post(message);
            }
        }, 300);
    }

    public void initiateApp() {
        splashPresenter.setView(this);
        context = SplashActivity.this;
        dialog = new ProgressDialog(this);
        fpath = "";
        appname = "";

        FastSave.getInstance().saveString(FC_Constants.LOGIN_MODE, "NA");
        String[] permissionArray = new String[]{PermissionUtils.Manifest_CAMERA,
                PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_RECORD_AUDIO,
                PermissionUtils.Manifest_ACCESS_COARSE_LOCATION,
                PermissionUtils.Manifest_ACCESS_FINE_LOCATION
        };
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
    }

    @UiThread
    @Override
    public void showButton() {
        context.startService(new Intent(context, AppExitService.class));
        if (!FastSave.getInstance().getBoolean(FC_Constants.KEY_MENU_COPIED, false)) {
            if (!ApplicationClass.isTablet) {
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
        Point size = new Point();
        int width = size.x;
        int height = size.y;
        String strwidth = String.valueOf(width);
        String strheight = String.valueOf(height);
        Configuration config = context.getResources().getConfiguration();
        String resolution = "W " + strwidth + " x H " + strheight + " pixels dpi: " + config.densityDpi;
        FastSave.getInstance().saveString(FC_Constants.SCR_RES, "" + resolution);
        FastSave.getInstance().saveString(FC_Constants.LANGUAGE, FC_Constants.HINDI);
        setAppLocal(this, FC_Constants.HINDI);
        splashPresenter.createDatabase();
/*        if (!FastSave.getInstance().getBoolean(FC_Constants.LANGUAGE_SPLASH_DIALOG, false))
            showLanguageSelectionDialog();
        else {
            setAppLocal(this, FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI));
            createDataBase();
        }*/
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
        if (!ApplicationClass.isTablet)
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
                    if (!ApplicationClass.isTablet)
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
            if (!ApplicationClass.isTablet)
                gotoNextActivity();
            exitDialogOpen = false;
            exitDialog.dismiss();
        }, 5000);
    }

/*    @UiThread
    public void showExitDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);
        exitDialogOpen = true;

        dia_btn_green.setText(getResources().getString(R.string.Restart));
        dia_btn_red.setText(getResources().getString(R.string.Exit));
        dia_btn_yellow.setText(getResources().getString(R.string.Cancel));
        dialog.show();

        Handler backHandler = new Handler();
        backHandler.postDelayed(() -> {
            if (!ApplicationClass.isTablet)
                gotoNextActivity();
            exitDialogOpen = false;
            dialog.dismiss();
        }, 5000);

        dia_btn_green.setOnClickListener(v -> {
            if (!ApplicationClass.isTablet)
                gotoNextActivity();
            backHandler.removeCallbacksAndMessages(null);
            exitDialogOpen = false;
            dialog.dismiss();
        });

        dia_btn_yellow.setOnClickListener(v -> {
            if (!ApplicationClass.isTablet)
                gotoNextActivity();
            backHandler.removeCallbacksAndMessages(null);
            exitDialogOpen = false;
            dialog.dismiss();
        });

        dia_btn_red.setOnClickListener(v -> {
            backHandler.removeCallbacksAndMessages(null);
            finishAffinity();
            dialog.dismiss();
        });
    }*/

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
            splashPresenter.doInitialEntries(AppDatabase.appDatabase);
        splashPresenter.requestLocation();
        //        if(FastSave.getInstance().getBoolean(FC_Constants.newDataLanguageInserted, false))
//            splashPresenter.insertNewData();
        splashPresenter.updateVersionApp();
        if (!ApplicationClass.isTablet) {
            splashPresenter.pushData();
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
            dismissProgressDialog();
            startActivity(new Intent(context, MenuActivity_.class));
            finish();
        }
    }

    @UiThread
    @Override
    public void show_STT_Dialog() {
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

/*        CustomLodingDialog dialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();


        dia_btn_green.setOnClickListener(v -> {
            showBottomFragment();
            FastSave.getInstance().saveBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, true);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName("com.google.android.googlequicksearchbox",
                    "com.google.android.voicesearch.greco3.languagepack.InstallActivity"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            dialog.dismiss();
        });

        dia_btn_red.setOnClickListener(v -> {
            showBottomFragment();
            dialog.dismiss();
        });*/
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

// unused code
/*
    @SuppressLint("SetTextI18n")
    private void showLanguageSelectionDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_language_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Spinner lang_spinner = dialog.findViewById(R.id.lang_spinner);
        dia_btn_green.setText("OK");
        dialog.show();

        dia_title.setText("Current Language : " + FastSave.getInstance().getString(FC_Constants.LANGUAGE, "Hindi"));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner,
                getResources().getStringArray(R.array.app_Language));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang_spinner.setAdapter(dataAdapter);

        lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                FC_Constants.currentSelectedLanguage = lang_spinner.getSelectedItem().toString();
//                FastSave.getInstance().saveString(FC_Constants.LANGUAGE, FC_Constants.currentSelectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dia_btn_green.setOnClickListener(v -> {
            FastSave.getInstance().saveBoolean(FC_Constants.LANGUAGE_SPLASH_DIALOG, true);
//                setAppLocal(this, FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI));
            createDataBase();
            dialog.dismiss();
        });
    }

//    private void showComingSoonToast() {
//        showLanguageSelectionDialog();
//    }
*/
