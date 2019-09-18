package com.pratham.foundation.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.services.TTSService;

import java.util.Locale;


public class BaseActivity extends AppCompatActivity {

    public static boolean muteFlg = false;
    public static TTSService ttsService;
    private static AudioManager audioManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        startSTT();
        muteFlg = false;

    }

    private void startSTT() {
        ttsService = new TTSService(getApplication());
        ttsService.setActivity(this);
        ttsService.setSpeechRate(0.7f);
        ttsService.setLanguage(new Locale("en", "IN"));
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
                            AppDatabase.appDatabase.getSessionDao().UpdateToDate(curSession, Utils.getCurrentDateTime());
                        }
                        BackupDatabase.backup(context);
                    } catch (Exception e) {
                        String curSession = AppDatabase.appDatabase.getStatusDao().getValue("CurrentSession");
                        AppDatabase.appDatabase.getSessionDao().UpdateToDate(curSession, Utils.getCurrentDateTime());
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}