package com.pratham.foundation;

import static com.pratham.foundation.ApplicationClass.audioManager;
import static com.pratham.foundation.ApplicationClass.ttsService;
import static com.pratham.foundation.utility.FC_Constants.transferredImages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.usb.UsbDevice;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Slide;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.pratham.foundation.async.CopyDbToOTG;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.services.TTSService;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.CatchoTransparentActivity;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;
import com.pratham.foundation.utility.MediaPlayerUtil;

import net.alhazmy13.catcho.library.Catcho;

import org.apache.commons.net.time.TimeTCPClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;


public class BaseActivity extends AppCompatActivity {

    //Add these Variables, declare it globally
    private AppUpdateManager appUpdateManager;
    private Task<AppUpdateInfo> appUpdateInfoTask;
    private final int APP_UPDATE_TYPE_SUPPORTED = AppUpdateType.FLEXIBLE;
    private final int REQUEST_UPDATE = 100;
    private final int CHECK_UPDATE_C = 101;
    private final int UPDATE_CONNECTION = 102;

    View mDecorView;
    public static boolean muteFlg = false;
    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final int SHOW_OTG_TRANSFER_DIALOG = 9;
    private static final int SHOW_OTG_SELECT_DIALOG = 11;
    private static final int HIDE_OTG_TRANSFER_DIALOG_SUCCESS = 12;
    private static final int HIDE_OTG_TRANSFER_DIALOG_FAILED = 13;
    private static final int SDCARD_LOCATION_CHOOSER = 10;
    public static UsbDevice myUsbDevice;
    LottieAnimationView push_lottie;
    TextView txt_push_dialog_msg,txt_push_dialog_msg2;
    TextView txt_push_error;
    RelativeLayout rl_btn;
    Button ok_btn, eject_btn;
    BlurPopupWindow pushDialog;
    CustomLodingDialog sd_builder;
    public static MediaPlayer correctSound;
    public static MediaPlayerUtil mediaPlayerUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        Window window = getWindow();
//        Slide slide = new Slide();
//        slide.setInterpolator(new LinearInterpolator());
//        slide.setSlideEdge(Gravity.LEFT);
//        window.setExitTransition(slide); // The Transition to use to move Views out of the scene when calling a new Activity.
//        window.setReenterTransition(slide); // The Transition to use to move Views into the scene when reentering from a previously-started Activity.

//         inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // set an exit transition
        getWindow().setExitTransition(new Slide());

        hideSystemUI();
        super.onCreate(savedInstanceState);
        mDecorView = getWindow().getDecorView();
        if (audioManager == null)
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        startSTT();
        correctSound = MediaPlayer.create(this, R.raw.correct_ans);
        muteFlg = false;

/*        Modal_Log log = new Modal_Log();
        log.setCurrentDateTime(FC_Utility.getCurrentDateTime());
        log.setErrorType(" ");
        log.setExceptionMessage("App_Start");
        log.setExceptionStackTrace("APK BUILD DATE : "+BUILD_DATE);
        log.setMethodName("onCreate()");
        log.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "no_group"));
        log.setDeviceId("" + FC_Utility.getDeviceID());
        AppDatabase.getDatabaseInstance(this).getLogsDao().insertLog(log);*/

        Catcho.Builder(this)
                .activity(CatchoTransparentActivity.class).build();
//                .recipients("ketan.inamdar@pratham.org").build();
    }

/*
    @Override
    public void onAttachedToWindow() {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }
*/

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
/*        Modal_Log log = new Modal_Log();
        log.setCurrentDateTime(FC_Utility.getCurrentDateTime());
        log.setErrorType("");
        log.setExceptionMessage("App_End");
        log.setExceptionStackTrace("APK BUILD DATE : "+BUILD_DATE);
        log.setMethodName("onDestroy()");
        log.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "no_group"));
        log.setDeviceId("" + FC_Utility.getDeviceID());
        AppDatabase.getDatabaseInstance(this).getLogsDao().insertLog(log);*/
        super.onDestroy();
    }

    public String getScreenResolution(Context context) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        String strwidth = String.valueOf(width);
        String strheight = String.valueOf(height);
        Configuration config = context.getResources().getConfiguration();
        String resolution = "W " + strwidth + " x H " + strheight + " pixels dpi: " + config.densityDpi;
        return "" + resolution;
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideSystemUI();
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

    private void startSTT() {
        if (ttsService == null) {
            ttsService = new TTSService(getApplication());
            ttsService.setActivity(this);
            ttsService.setSpeechRate(0.7f);
            ttsService.setLanguage(new Locale("en", "IN"));
        }
    }

    public static void setMute(int m) {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            if (m == 1 && !muteFlg) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
                muteFlg = true;
            } else if (m == 0 && muteFlg) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
                muteFlg = false;
            }
        } else {

            if (m == 1 && !muteFlg) {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                muteFlg = true;
            } else if (m == 0 && muteFlg) {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                muteFlg = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ActivityResumed();
        String a = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI);
        Log.d("INSTRUCTIONFRAG", "Select Subj: " + a);
        FC_Utility.setAppLocal(this, a);
        BackupDatabase.backup(this);
    }

    public void getTimeFormServer(){
        try {
            TimeTCPClient client = new TimeTCPClient();
            try {
                // Set timeout of 60 seconds
                client.setDefaultTimeout(60000);
                // Connecting to time server
                // Other time servers can be found at : http://tf.nist.gov/tf-cgi/servers.cgi#
                // Make sure that your program NEVER queries a server more frequently than once every 4 seconds
                client.connect("time.nist.gov");
                System.out.println(client.getDate());
            } finally {
                client.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void updateFlagsWhenPushed(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(FC_Constants.OTG_INSERTED)) {
                mHandler.sendEmptyMessage(SHOW_OTG_TRANSFER_DIALOG);
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.BACKUP_DB_COPIED)) {
                mHandler.sendEmptyMessage(HIDE_OTG_TRANSFER_DIALOG_SUCCESS);
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.BACKUP_DB_NOT_COPIED)) {
                mHandler.sendEmptyMessage(HIDE_OTG_TRANSFER_DIALOG_FAILED);
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.CHECK_UPDATE)) {
                mHandler.sendEmptyMessage(CHECK_UPDATE_C);
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.START_UPDATE)) {
                startUpdate();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @SuppressLint({"MissingPermission", "SetTextI18n"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHECK_UPDATE_C:
                    checkForUpdate();
                    break;
                case SHOW_OTG_TRANSFER_DIALOG:
                    showSDBuilderDialog();
                    break;
                case SHOW_OTG_SELECT_DIALOG:
                    new Handler().postDelayed(() -> {
                        ShowOTGPushDialog();
                        rl_btn.setVisibility(View.GONE);
                    }, 300);
                    break;
                case HIDE_OTG_TRANSFER_DIALOG_SUCCESS:
                    push_lottie.setAnimation("lottie_correct.json");
                    push_lottie.playAnimation();
                    int days = AppDatabase.getDatabaseInstance(ApplicationClass.getInstance()).getScoreDao().getTotalActiveDeviceDays();
                    txt_push_dialog_msg.setText("Data of " + days + " days and\n"
                            + transferredImages + " Images\nCopied Successfully!!");
                    rl_btn.setVisibility(View.VISIBLE);
                    break;
                case HIDE_OTG_TRANSFER_DIALOG_FAILED:
                    push_lottie.setAnimation("error_cross.json");
                    push_lottie.playAnimation();
                    txt_push_dialog_msg.setText("Data Copying Failed!! Please re-insert the OTG");
                    txt_push_dialog_msg.setTextColor(getResources().getColor(R.color.red));
                    txt_push_error.setVisibility(View.GONE);
                    rl_btn.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @SuppressLint("CutPasteId")
    @UiThread
    public void ShowOTGPushDialog() {
        pushDialog = new BlurPopupWindow.Builder(this)
                .setContentView(R.layout.app_send_success_dialog)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(false)
                .setScaleRatio(0.2f)
                .bindClickListener(v -> {
                    new Handler().postDelayed(() -> {
                        pushDialog.dismiss();
                    }, 200);
                }, R.id.ok_btn)
                .bindClickListener(v -> {
                    new Handler().postDelayed(() -> {
                        ejectOTG();
                        pushDialog.dismiss();
                    }, 200);
                }, R.id.eject_btn)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();

        push_lottie = pushDialog.findViewById(R.id.push_lottie);
        txt_push_dialog_msg = pushDialog.findViewById(R.id.txt_push_dialog_msg);
        txt_push_error = pushDialog.findViewById(R.id.txt_push_error);
        rl_btn = pushDialog.findViewById(R.id.rl_btn);
        ok_btn = pushDialog.findViewById(R.id.ok_btn);
        eject_btn = pushDialog.findViewById(R.id.eject_btn);
        pushDialog.show();
    }

    private void ejectOTG() {
        Intent i = new Intent(android.provider.Settings.ACTION_MEMORY_CARD_SETTINGS);
        startActivity(i);
    }

    BlurPopupWindow sdBuilderDialog;

    private void showSDBuilderDialog() {
        sdBuilderDialog = new BlurPopupWindow.Builder(this)
                .setContentView(R.layout.dialog_alert_sd_card)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(true)
                .setScaleRatio(0.2f)
                .bindClickListener(v -> {
                    sdBuilderDialog.dismiss();
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        startActivityForResult(intent, SDCARD_LOCATION_CHOOSER);
                    }, 200);
                }, R.id.txt_choose_sd_card)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();
        sdBuilderDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    public void endSession(Context context) {
        try {
            new AsyncTask<Object, Void, Object>() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        String curSession = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("CurrentSession");
                        String toDateTemp = AppDatabase.getDatabaseInstance(context).getSessionDao().getToDate(curSession);
                        if (toDateTemp.equalsIgnoreCase("na")) {
                            AppDatabase.getDatabaseInstance(context).getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
                        }
                        BackupDatabase.backup(context);
                    } catch (Exception e) {
                        String curSession = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("CurrentSession");
                        AppDatabase.getDatabaseInstance(context).getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // UPDATE APP CODE
    //Flexible Update
    private void checkForUpdate() {
/*
        if (BuildConfig.DEBUG) {
            appUpdateManager = new FakeAppUpdateManager(this);
            ((FakeAppUpdateManager) appUpdateManager).setUpdateAvailable(0);
            Log.d("##########  ->", "Fake");
        } else {
*/
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Log.d("##########  ->", "Original");
//        }
        // Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(installStateUpdatedListener);

        appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        Log.d("########## 1 ->", String.valueOf(appUpdateInfoTask));
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            Log.d("########## 2 ->", "SuccessListener");
            if ((appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                //send message if update is available
                EventMessage updateAvailable = new EventMessage();
                updateAvailable.setMessage(FC_Constants.UPDATE_AVAILABLE);
                EventBus.getDefault().post(updateAvailable);
            } else {
                Log.d("########## 5 ->", "No Update available");
            }
        });
    }
    //Listener for checking Install Status
    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED){
                        //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                        //send message if update is downloaded
                        Log.d("#", "InstallStateUpdated: state: " + state.installStatus());
                        appUpdateManager.completeUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED){
                        Log.d("#", "InstallStateInstalled: state: " + state.installStatus());
                        if (appUpdateManager != null){
                            appUpdateManager.unregisterListener(installStateUpdatedListener);
                        }

                    } else {
                        Log.d("#", "InstallStateUpdatedListener: state: " + state.installStatus());
                    }
                }
            };

    public void startUpdate(){
        // Start an update.
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfoTask.getResult(),
                    AppUpdateType.FLEXIBLE,
                    this,
                    REQUEST_UPDATE);
            Log.d("########## 3 ->", "All Condition true");
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
/*        if (BuildConfig.DEBUG) {
            FakeAppUpdateManager fakeAppUpdate = (FakeAppUpdateManager) appUpdateManager;
            if (fakeAppUpdate.isConfirmationDialogVisible()) {
                fakeAppUpdate.userAcceptsUpdate();
                fakeAppUpdate.downloadStarts();
                fakeAppUpdate.downloadCompletes();
                fakeAppUpdate.completeUpdate();
                fakeAppUpdate.installCompletes();
            }
        }*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SDCARD_LOCATION_CHOOSER) {
            if (data != null && data.getData() != null) {
                Uri treeUri = data.getData();
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                ApplicationClass.getInstance().getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
                mHandler.sendEmptyMessage(SHOW_OTG_SELECT_DIALOG);
                new Handler().postDelayed(() -> {
                    new CopyDbToOTG().execute(treeUri);
                }, 500);
            }
        } else if (requestCode == REQUEST_UPDATE) {
            Log.d("########## 4 ->", "Activity Result");
            switch (requestCode) {
                case RESULT_OK:
                    if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.FLEXIBLE) {
                        Log.d("#", "App Updated Successfully");
                    } else {
                        Log.d("#", "Update Started");
                    }
                case RESULT_CANCELED:
                    Log.d("#", "Update Cancelled");
                case ActivityResult.RESULT_IN_APP_UPDATE_FAILED:
                    Log.d("#", "Update Failed");

            }
        }
    }

    public void endSession() {
        try {
            String curSession = FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, "");
            AppDatabase.getDatabaseInstance(ApplicationClass.getInstance()).getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
            BackupDatabase.backup(ApplicationClass.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        endSession();
        super.onPause();
    }

}