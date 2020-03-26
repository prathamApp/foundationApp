package com.pratham.foundation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbDevice;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.foundation.async.CopyDbToOTG;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.services.TTSService;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;
import com.pratham.foundation.utility.MediaPlayerUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.audioManager;
import static com.pratham.foundation.ApplicationClass.ttsService;
import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.utility.FC_Constants.TransferedImages;


public class BaseActivity extends AppCompatActivity {

    public static boolean muteFlg = false;
    View mDecorView;
    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final int SHOW_OTG_TRANSFER_DIALOG = 9;
    private static final int SHOW_OTG_SELECT_DIALOG = 11;
    private static final int HIDE_OTG_TRANSFER_DIALOG_SUCCESS = 12;
    private static final int HIDE_OTG_TRANSFER_DIALOG_FAILED = 13;
    private static final int SDCARD_LOCATION_CHOOSER = 10;
    public static UsbDevice myUsbDevice;
    LottieAnimationView push_lottie;
    TextView txt_push_dialog_msg;
    TextView txt_push_error;
    RelativeLayout rl_btn;
    Button ok_btn, eject_btn;
    CustomLodingDialog pushDialog;
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

//        Catcho.Builder(this)
//                .activity(CatchoTransparentActivity.class).build();
        //   .recipients("abc@domain.com").build();
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
    protected void onPause() {
        super.onPause();
//        ActivityOnPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ActivityResumed();
        BackupDatabase.backup(this);
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
                case SHOW_OTG_TRANSFER_DIALOG:
                    showSDBuilderDialog();
                    break;
                case SHOW_OTG_SELECT_DIALOG:
                    ShowOTGPushDialog();
                    rl_btn.setVisibility(View.GONE);
                    break;
                case HIDE_OTG_TRANSFER_DIALOG_SUCCESS:
                    push_lottie.setAnimation("lottie_correct.json");
                    push_lottie.playAnimation();
                    int days = appDatabase.getScoreDao().getTotalActiveDeviceDays();
                    txt_push_dialog_msg.setText("Data of " + days + " days and\n" + TransferedImages + " Images\nCopied Successfully!!");
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
        pushDialog = new CustomLodingDialog(this);
        pushDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pushDialog.setContentView(R.layout.app_send_success_dialog);
        Objects.requireNonNull(pushDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pushDialog.setCancelable(false);
        pushDialog.setCanceledOnTouchOutside(false);
        pushDialog.show();

        push_lottie = pushDialog.findViewById(R.id.push_lottie);
        txt_push_dialog_msg = pushDialog.findViewById(R.id.txt_push_dialog_msg);
        txt_push_error = pushDialog.findViewById(R.id.txt_push_error);
        rl_btn = pushDialog.findViewById(R.id.rl_btn);
        ok_btn = pushDialog.findViewById(R.id.ok_btn);
        eject_btn = pushDialog.findViewById(R.id.eject_btn);

        ok_btn.setOnClickListener(v -> {
            pushDialog.dismiss();
        });
        eject_btn.setOnClickListener(v -> {
            ejectOTG();
            pushDialog.dismiss();
        });
    }

    private void ejectOTG() {
        Intent i = new Intent(android.provider.Settings.ACTION_MEMORY_CARD_SETTINGS);
        startActivity(i);
    }

    private void showSDBuilderDialog() {
        CustomLodingDialog dialog = new CustomLodingDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert_sd_card);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button txt_choose_sd_card = dialog.findViewById(R.id.txt_choose_sd_card);
        txt_choose_sd_card.setOnClickListener(v -> {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, SDCARD_LOCATION_CHOOSER);
            }, 200);
            dialog.dismiss();
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void endSession(Context context) {
        try {
            new AsyncTask<Object, Void, Object>() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        String curSession = AppDatabase.appDatabase.getStatusDao().getValue("CurrentSession");
                        String toDateTemp = AppDatabase.appDatabase.getSessionDao().getToDate(curSession);
                        if (toDateTemp.equalsIgnoreCase("na")) {
                            AppDatabase.appDatabase.getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
                        }
                        BackupDatabase.backup(context);
                    } catch (Exception e) {
                        String curSession = AppDatabase.appDatabase.getStatusDao().getValue("CurrentSession");
                        AppDatabase.appDatabase.getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                new CopyDbToOTG().execute(treeUri);
            }
        }
    }
}