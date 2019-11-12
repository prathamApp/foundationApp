package com.pratham.foundation;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.isupatches.wisefy.WiseFy;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Utility;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ApplicationClass extends Application {

//    public static String uploadDataUrl = "http://prodigi.openiscool.org/api/cosv2/pushdata";
    public static String uploadDataUrl = "http://devprodigi.openiscool.org/api/Foundation/PushData";
    public static final boolean isTablet = true;
    public static boolean contentExistOnSD = false, LocationFlg = false;
    public static String contentSDPath = "";
    public static String foundationPath = "";
    public static String App_Thumbs_Path = "/.FCA/App_Thumbs/";
    OkHttpClient okHttpClient;
    public static WiseFy wiseF;
    public static ApplicationClass applicationClass;
    private static final DateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    public static String path;


    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        if (applicationClass == null) {
            applicationClass = this;
        }
        FastSave.init(getApplicationContext());

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
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);

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
