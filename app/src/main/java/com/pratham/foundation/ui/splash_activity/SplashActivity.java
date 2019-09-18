package com.pratham.foundation.ui.splash_activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.custom.shared_preferences.FastSave;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.interfaces.Interface_copying;
import com.pratham.foundation.interfaces.PermissionResult;
import com.pratham.foundation.services.AppExitService;
import com.pratham.foundation.ui.bottom_fragment.BottomStudentsFragment;
import com.pratham.foundation.ui.bottom_fragment.BottomStudentsFragment_;
import com.pratham.foundation.ui.bottom_fragment.add_student.MenuActivity_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;
import com.pratham.foundation.utility.PermissionUtils;
import com.pratham.foundation.utility.SplashSupportActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.annotation.Annotation;

import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_exit;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_restart;


@EActivity(R.layout.activity_splash)
public class SplashActivity extends SplashSupportActivity implements SplashContract.SplashView, PermissionResult, Interface_copying {

    @ViewById(R.id.splash_root)
    RelativeLayout splash_root;
    @ViewById(R.id.iv_splash)
    ImageView iv_splash;
    @ViewById(R.id.gifcomplete)
    ImageView gifcomplete;
    static String fpath, appname;
    public static MediaPlayer bgMusic;
    public ProgressDialog progressDialog;


    @Bean(SplashPresenter.class)
    SplashPresenter splashPresenter;

    static Context context;
    Dialog dialog;
    public static boolean firstPause = true, fragmentBottomOpenFlg = false,
            fragmentBottomPauseFlg = false, fragmentAddStudentPauseFlg = false,
            fragmentAddStudentOpenFlg = false, exitDialogOpen = false;


    @AfterViews
    public void init() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog = new ProgressDialog(this);
        fpath = "";
        appname = "";
        splashPresenter.setView(this);
        context = SplashActivity.this;
        bgMusic = MediaPlayer.create(this, R.raw.bg_sound);
        bgMusic.setLooping(true);
        /*Bundle b = getIntent().getExtras();
        if (b != null) {
            String myString = b.getString("KEY_DATA");
            Toast.makeText(this, "" + myString, Toast.LENGTH_SHORT).show();
        }*/
        ImageViewAnimatedChange(this,gifcomplete);
//        initiateApp();
    }

    public void initiateApp() {


        String[] permissionArray = new String[]{PermissionUtils.Manifest_CAMERA,
                PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_RECORD_AUDIO,
                PermissionUtils.Manifest_ACCESS_COARSE_LOCATION,
                PermissionUtils.Manifest_ACCESS_FINE_LOCATION
        };

        new Handler().postDelayed(() -> {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                if (!isPermissionsGranted(SplashActivity.this, permissionArray))
                    askCompactPermissions(permissionArray, SplashActivity.this);
                else
                    startApp();
            } else
                startApp();
        }, 1500);
    }

    public void ImageViewAnimatedChange(Context c, final ImageView iv_logo) {
        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.zoom_in_new);
        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                ImageViewAnimatedChangeSecond(c, iv_logo);
                iv_splash.setVisibility(View.VISIBLE);
            }
        });
        iv_logo.setAnimation(anim_in);
    }

    public void ImageViewAnimatedChangeSecond(Context c, final ImageView iv_logo) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out_new);
        iv_logo.setAnimation(anim_out);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                initiateApp();
//                pradigiAnimation(c, iv_logo_pradigi);
            }
        });
    }
/*

    public void pradigiAnimation(Context c, final ImageView iv_logo_pradigi) {
        final Animation anim_in = AnimationUtils.loadAnimation(c, R.anim.item_animation_from_bottom);
        iv_logo_pradigi.setVisibility(View.VISIBLE);

        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String[] permissionArray = new String[]{PermissionUtils.Manifest_CAMERA,
                        PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                        PermissionUtils.Manifest_RECORD_AUDIO,
                        PermissionUtils.Manifest_ACCESS_COARSE_LOCATION,
                        PermissionUtils.Manifest_ACCESS_FINE_LOCATION
                };

                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                    if (!isPermissionsGranted(SplashActivity.this, permissionArray)) {
                        askCompactPermissions(permissionArray, SplashActivity.this);
                    } else {
                        startApp();
//                        splashPresenter.checkVersion();
                    }
                } else {
                    startApp();
//                    splashPresenter.checkVersion();
                }
            }
        });
        iv_logo_pradigi.setAnimation(anim_in);
    }
*/

    @Override
    public void showButton() {
        context.startService(new Intent(context, AppExitService.class));
        if (!FastSave.getInstance().getBoolean(FC_Constants.KEY_MENU_COPIED, false)) {
            if (!ApplicationClass.isTablet) {
                ApplicationClass.contentExistOnSD = false;
                splashPresenter.copyZipAndPopulateMenu();
            } else {
                ApplicationClass.foundationPath = FC_Utility.getInternalPath(SplashActivity.this);
                Log.d("COS.pradigiPath", "COS.pradigiPath: " + ApplicationClass.foundationPath);
                if (splashPresenter.getSdCardPath()) {
                    ApplicationClass.contentExistOnSD = true;
                    splashPresenter.populateSDCardMenu();
                } else {
                    ApplicationClass.contentExistOnSD = false;
                    splashPresenter.copyZipAndPopulateMenu();
                }
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
        if (!FastSave.getInstance().getBoolean(FC_Constants.LANGUAGE_SPLASH_DIALOG, false))
            showLanguageSelectionDialog();
        else
            createDataBase();
    }

    @SuppressLint("SetTextI18n")
    private void showLanguageSelectionDialog() {
        final Dialog dialog = new Dialog(context);
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

        dia_title.setText("Current Language : "+FastSave.getInstance().getString(FC_Constants.LANGUAGE,"Hindi"));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner,
                getResources().getStringArray(R.array.app_Language));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang_spinner.setAdapter(dataAdapter);

        lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FC_Constants.currentSelectedLanguage = lang_spinner.getSelectedItem().toString();
                FastSave.getInstance().saveString(FC_Constants.LANGUAGE, FC_Constants.currentSelectedLanguage);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dia_btn_green.setOnClickListener(v -> {
            FastSave.getInstance().saveBoolean(FC_Constants.LANGUAGE_SPLASH_DIALOG, true);
            createDataBase();
            dialog.dismiss();
        });
    }

    @Override
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(SplashActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading... Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        try {
            if (progressDialog != null)
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
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);
        exitDialogOpen = true;

        dia_btn_green.setText("" + dialog_btn_restart);
        dia_btn_red.setText("" + dialog_btn_exit);
        dia_btn_yellow.setText("" + dialog_btn_cancel);

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
            if (backHandler != null)
                backHandler.removeCallbacksAndMessages(null);
            exitDialogOpen = false;
            dialog.dismiss();
        });

        dia_btn_yellow.setOnClickListener(v -> {
            if (!ApplicationClass.isTablet)
                gotoNextActivity();
            if (backHandler != null)
                backHandler.removeCallbacksAndMessages(null);
            exitDialogOpen = false;
            dialog.dismiss();
        });

        dia_btn_red.setOnClickListener(v -> {
            if (backHandler != null)
                backHandler.removeCallbacksAndMessages(null);
            finishAffinity();
            dialog.dismiss();
        });
    }

    @Override
    public void permissionGranted() {
        Log.d("Splash", "permissionGranted: HAHAHAHAHAHA");
        startApp();
//        splashPresenter.checkVersion();
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
                           //todo remove#
                            // ApplicationClass.contentProgressDao = AppDatabase.appDatabase.getContentProgressDao();
                            if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/foundation_db").exists()) {
                                try {
                                    copyDb = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                splashPresenter.getSdCardPath();
                            }
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
                //todo remove#
               // ApplicationClass.contentProgressDao = AppDatabase.appDatabase.getContentProgressDao();
                splashPresenter.getSdCardPath();
                new Handler().postDelayed(() -> showButton(), 2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(getDatabasePath(AppDatabase.DB_NAME).getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    @Override
    public void gotoNextActivity() {
        if (!FastSave.getInstance().getBoolean(FC_Constants.INITIAL_ENTRIES, false))
            splashPresenter.doInitialEntries(AppDatabase.appDatabase);
//        if(FastSave.getInstance().getBoolean(FC_Constants.newDataLanguageInserted, false))
//            splashPresenter.insertNewData();
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
        }
    }

    private void show_STT_Dialog() {
        Dialog dialog = new Dialog(this);
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

        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_title.setTextSize(14);
        dia_title.setText("Please download language packs offline for better performance");
        dia_btn_green.setText("OK");
        dia_btn_red.setText("SKIP");
        dia_btn_yellow.setVisibility(View.GONE);

        dia_btn_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomFragment();
                FastSave.getInstance().saveBoolean(FC_Constants.VOICES_DOWNLOAD_INTENT, true);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.google.android.googlequicksearchbox",
                        "com.google.android.voicesearch.greco3.languagepack.InstallActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dia_btn_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomFragment();
                dialog.dismiss();
            }
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
    protected void onResume() {
        super.onResume();
        try {
            if (!bgMusic.isPlaying()) {
                bgMusic = MediaPlayer.create(this, R.raw.bg_sound);
                bgMusic.setLooping(true);
                bgMusic.start();
            }
        } catch (Exception e) {
        }
        try {
            if (bgMusic.equals(null)) {
                bgMusic = MediaPlayer.create(this, R.raw.bg_sound);
                bgMusic.setLooping(true);
                bgMusic.start();
            }
        } catch (Exception e) {
        }
        EventBus.getDefault().post("reload");
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

/*<!--    <com.cunoraz.gifview.library.GifView
        android:id="@+id/gif1"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentTop="true"
        android:layout_below="@+id/iv_pratham_logo"
        android:layout_centerHorizontal="true"
        custom:gif="@drawable/splash_gif_animation"
        android:scaleType="centerInside"/>-->*/
