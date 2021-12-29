package com.pratham.foundation;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.isupatches.wisefy.WiseFy;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.modalclasses.Modal_FileDownloading;
import com.pratham.foundation.services.TTSService;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_RandomString;
import com.pratham.foundation.utility.FC_Utility;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ApplicationClass extends Application {

    // (HL Customised)
    public static final String BUILD_DATE = "29-Dec-2021";
    public static boolean isTablet = true;
    public static boolean isAssets = false;
    public static boolean contentExistOnSD = false, LocationFlg = false;
    public static String contentSDPath = "";
    public static String foundationPath = "";
    public static String App_Thumbs_Path = "/.FCA/App_Thumbs/";
    OkHttpClient okHttpClient;
    public static ArrayList<String> allContentsIDListStrings = new ArrayList<>();
    public static ArrayList<ContentTable> allContentsLists;
    public static WiseFy wiseF;
    public static ApplicationClass applicationClass;
    private static final DateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    public static String path;
    public static TTSService ttsService;
    public static AudioManager audioManager;
    public static MediaPlayer ButtonClickSound, BackBtnSound;
    public static List<Modal_FileDownloading> fileDownloadingList;
    public static FC_RandomString fc_randomString;


    @Override
    public void onCreate() {
        super.onCreate();
        //this way the VM ignores the file URI exposure. if commented, the camera crashes on open
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Fresco.initialize(this);
        if (applicationClass == null) {
            applicationClass = this;
        }
//        fc_randomString = new FC_RandomString(8, ThreadLocalRandom.current());
        fileDownloadingList = new ArrayList<>();
        FastSave.init(getApplicationContext());
        ButtonClickSound = MediaPlayer.create(this, R.raw.click2);
        BackBtnSound = MediaPlayer.create(this, R.raw.click);
        foundationPath = FC_Utility.getInternalPath(this);
        if (foundationPath != null) {
            File mydir = null;
            mydir = new File(foundationPath + "/.FCA");
            if (!mydir.exists())
                mydir.mkdirs();
        }
        Log.d("old_cos.foundationPath", "old_cos.foundationPath: " + foundationPath);

        wiseF = new WiseFy.Brains(getApplicationContext()).logging(true).getSmarts();
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);
    }

        public static File getStoragePath() {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)) {
                return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            } else {
                return Environment.getExternalStorageDirectory();
            }
        }

    public static boolean getAppMode() {
        isTablet = false;
//        isTablet = FastSave.getInstance().getBoolean(FC_Constants.PRATHAM_LOGIN, false);
        return isTablet;
    }

    private void createNotificationChannel() {
        String NOTIFICATION_CHANNEL_ID = "push.service";
        String channelName = "Sync Service";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    channelName, NotificationManager.IMPORTANCE_NONE);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static UUID getUniqueID() {
        return UUID.randomUUID();
    }

    public static ApplicationClass getInstance() {
        return applicationClass;
    }

    public static int getRandomNumber(int min, int max) {
        return min + (new Random().nextInt(max));
    }

}
