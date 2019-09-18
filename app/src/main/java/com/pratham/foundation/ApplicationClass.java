package com.pratham.foundation;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.pratham.foundation.custom.shared_preferences.FastSave;
import com.pratham.foundation.utility.Utils;
import com.isupatches.wisefy.WiseFy;


import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ApplicationClass extends Application {

    public static String uploadDataUrl = "http://prodigi.openiscool.org/api/cosv2/pushdata";
    public static final boolean isTablet = false;
    public static boolean contentExistOnSD = false, LocationFlg = false;
    public static String contentSDPath = "";
    public static String foundationPath = "";
    OkHttpClient okHttpClient;
    public static WiseFy wiseF;
    public static ApplicationClass applicationClass;
    private static final DateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    public static String path;


    @Override
    public void onCreate() {
        super.onCreate();

        if (applicationClass == null) {
            applicationClass = this;
        }
        FastSave.init(getApplicationContext());

        foundationPath = Utils.getInternalPath(this);
        if (foundationPath != null) {
            File mydir = null;
            mydir = new File(applicationClass.foundationPath + "/.foundation");
            if (!mydir.exists())
                mydir.mkdirs();
            mydir = new File(applicationClass.foundationPath + "/.foundation/English");
            if (!mydir.exists())
                mydir.mkdirs();
            mydir = new File(applicationClass.foundationPath + "/.foundation/English/Game");
            if (!mydir.exists())
                mydir.mkdirs();
        }
        Log.d("COS.foundationPath", "COS.foundationPath: " + foundationPath);

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
