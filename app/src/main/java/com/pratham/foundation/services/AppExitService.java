package com.pratham.foundation.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.splash_activity.SplashActivity;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;


public class AppExitService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
            new AsyncTask<Object, Void, Object>() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        Log.d("AppExitService:  ", "1]  In Try");
                        Log.d("AppExitService:  ", "2]  toDateTemp : "+ FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                        String toDateTemp = appDatabase.getSessionDao().getToDate(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                        Log.d("AppExitService:  ", "3]  toDateTemp : "+toDateTemp);
                        if (toDateTemp.equalsIgnoreCase("na")) {
                            Log.d("AppExitService:  ", "4]  toDateTemp If NA: "+toDateTemp);
                            appDatabase.getSessionDao().UpdateToDate(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""), FC_Utility.getCurrentDateTime());
                        }
                        if(FC_Constants.assessmentFlag || FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                            Log.d("AppExitService:  ", "5]  Assessment Flg: ");
                            String toDateAssessment = appDatabase.getSessionDao().getToDate(FastSave.getInstance().getString(FC_Constants.ASSESSMENT_SESSION, ""));
                            Log.d("AppExitService:  ", "6]  Assessment toDate: "+toDateAssessment);
                            if (toDateAssessment.equalsIgnoreCase("na")) {
                                Log.d("AppExitService:  ", "7]  Assessment toDate If Na: "+toDateAssessment);
                                appDatabase.getSessionDao().UpdateToDate(FastSave.getInstance().getString(FC_Constants.ASSESSMENT_SESSION, ""), FC_Utility.getCurrentDateTime());
                            }
                        }
                        Log.d("AppExitService:  ", "8]  Outside : ");
                        BackupDatabase.backup(AppExitService.this);
                        stopService(new Intent(AppExitService.this, SplashActivity.class));
                        stopSelf();
                        Log.d("AppExitService:  ", "9]  Last: ");
                    } catch (Exception e) {
                        Log.d("AppExitService:  ", "10]  Inner Exception");
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
            Thread.sleep(2000);
        } catch (Exception e) {
            Log.d("AppExitService:  ", "11]  Outer Exception");
            e.printStackTrace();
        }
    }
}