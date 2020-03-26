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
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hanks.htextview.typer.TyperTextView;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
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

import java.io.File;
import java.util.Objects;

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
        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(() -> {
            startTextAud();
        }, 300);
    }

    private void startTextAud() {
        final Typeface title_font = Typeface.createFromAsset(getAssets(), "fonts/Sarala_Bold.ttf");
        tv_typer.setTypeface(title_font);
        tv_typer.setVisibility(View.VISIBLE);
        tv_typer.animateText("Foundation\nCourse");
        tv_typer.setAnimationListener(hTextView -> initiateApp());
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> {
//            try {
//                if (bgMusic == null || !bgMusic.isPlaying()) {
//                    bgMusic = MediaPlayer.create(this, R.raw.bg_sound);
//                    bgMusic.setLooping(true);
//                    bgMusic.start();
//                }
//                else if(bgMusic!=null){
//                    bgMusic.start();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
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
//                } else {
//                    ApplicationClass.contentExistOnSD = false;
//                    splashPresenter.copyZipAndPopulateMenu();
//                }
            }
        } else
            gotoNextActivity();
    }

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
        createDataBase();
/*        if (!FastSave.getInstance().getBoolean(FC_Constants.LANGUAGE_SPLASH_DIALOG, false))
            showLanguageSelectionDialog();
        else {
            setAppLocal(this, FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI));
            createDataBase();
        }*/
    }

    @SuppressLint("SetTextI18n")
    private void showLanguageSelectionDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(context);
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

    @Override
    @UiThread
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(SplashActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getResources().getString(R.string.loding_please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
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
        if ((fragmentBottomOpenFlg && fragmentBottomPauseFlg) ||
                (fragmentBottomOpenFlg && fragmentAddStudentOpenFlg && fragmentBottomPauseFlg && fragmentAddStudentPauseFlg)) {
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

    public void showExitDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(context);
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

    boolean copyDb = false;

    @SuppressLint("StaticFieldLeak")
    public void createDataBase() {
        try {
            boolean dbExist = checkDataBase();
            if (!dbExist) {
                new AsyncTask<Void, Integer, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            AppDatabase.getDatabaseInstance(SplashActivity.this);
                            if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PrathamBackups/foundation_db").exists())
                                copyDb = true;
                            else
                                splashPresenter.getSdCardPath();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (copyDb)
                            splashPresenter.copyDataBase();
                        else
                            new Handler().postDelayed(() -> showButton(), 2000);
                    }
                }.execute();
            } else {
                AppDatabase.getDatabaseInstance(SplashActivity.this);
                splashPresenter.getSdCardPath();
                new Handler().postDelayed(this::showButton, 2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void preShowBtn() {
        new Handler().postDelayed(this::showButton, 2000);
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(getDatabasePath(AppDatabase.DB_NAME).getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

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

        createNoMediaForFCInternal(new File(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal"));

        if (!FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false))
            splashPresenter.doInitialEntries(AppDatabase.appDatabase);
        splashPresenter.requestLocation();
        //        if(FastSave.getInstance().getBoolean(FC_Constants.newDataLanguageInserted, false))
//            splashPresenter.insertNewData();
        splashPresenter.updateVersionApp();
        if (!ApplicationClass.isTablet) {
            splashPresenter.pushData();
            dismissProgressDialog();
            if (!FastSave.getInstance().getBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, false))
                show_STT_Dialog();
            else
                showBottomFragment();
        } else {
            dismissProgressDialog();
            startActivity(new Intent(context, MenuActivity_.class));
            finish();
        }
    }

    private void createNoMediaForFCInternal(File myFile) {
        try {
            File[] files = myFile.listFiles();
            try {
                File direct = new File(myFile.getPath() + "/.nomedia");
                if (!direct.exists()) {
                    Log.d("Files", "\nFirst Directory : " + myFile.getName());//CanonicalPath());
                    direct.createNewFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    createNoMediaForFCInternal(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void show_STT_Dialog() {
        CustomLodingDialog dialog = new CustomLodingDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fc_custom_dialog);
/*        Bitmap map=FC_Utility.takeScreenShot(HomeActivity.this);
        Bitmap fast=FC_Utility.fastblur(map, 20);
        final Drawable draw=new BitmapDrawable(getResources(),fast);
        dialog.getWindow().setBackgroundDrawable(draw);*/
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();

        int dp = 12;
        if (FC_Constants.TAB_LAYOUT)
            dp = 20;

        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_title.setTextSize(dp);
        dia_title.setText(getResources().getString(R.string.Stt_Dialog_Msg));
        dia_btn_green.setText(getResources().getString(R.string.Okay));
        dia_btn_red.setText(getResources().getString(R.string.Skip));
        dia_btn_yellow.setVisibility(View.GONE);

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
        });
    }

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