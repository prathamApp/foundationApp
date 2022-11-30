package com.pratham.foundation.services.background_service;

import static com.pratham.foundation.utility.FC_Constants.IS_SERVICE_STOPED;
import static com.pratham.foundation.utility.FC_Constants.NOTIFICATION_CHANNEL_ID;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.pratham.foundation.R;
import com.pratham.foundation.async.PushDataToServer_New_YN;
import com.pratham.foundation.services.shared_preferences.FastSave;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

@EBean
public class BackgroundPushService extends Service {

     /*
          Service Start Code
          Use this Code to start service from where you start the push data.

          BackgroundPushService mYourService;
          mYourService = new BackgroundPushService();
          Intent mServiceIntent;
          mServiceIntent = new Intent(context, mYourService.getClass());
          FastSave.getInstance().saveBoolean(IS_SERVICE_STOPED, true);
          Log.d("PushData", "End Service  IS_STOPPED : " + FastSave.getInstance().getBoolean(IS_SERVICE_STOPED, false));
          if (!isMyServiceRunning(mYourService.getClass())) context.startService(mServiceIntent);
     */

//    @Bean(PushDataToServer_New.class)
//    PushDataToServer_New pushDataToServer;
    @Bean(PushDataToServer_New_YN.class)
    PushDataToServer_New_YN pushDataToServer_new_yn;

    @Override
    public void onCreate() {
        super.onCreate();

//        Sync Data Initialize - Class for pushing data is initialized here.
//        pushDataToServer = new PushDataToServer_New(getApplicationContext());
        pushDataToServer_new_yn = new PushDataToServer_New_YN(getApplicationContext());

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
//        NOTIFICATION_CHANNEL_ID is kept in constants the value is as below-
//        public static final String NOTIFICATION_CHANNEL_ID = "foundation.data_sync";
        String channelName = "Data Sync Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName,
                NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(R.color.colorBtnGreenDark);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        chan.setName("Sync Service");
        chan.setDescription("Please Wait...");

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Data Sync")
                .setContentText("Please Wait...")
                .setColor(getApplication().getResources().getColor(R.color.colorGreen))
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
//        Start Data Sync
//        Start the pushing here by calling the push data function
//        pushDataToServer.startDataPush(getApplication(),false);
        pushDataToServer_new_yn.startDataPush(getApplication(),false);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        Stop Service
//        If the service is stopped this will restart the service. So I have used this flag to which marks the end in
//        the push data function.
//        This means that when the push is complete, IS_SERVICE_STOPED is made true and the service is stopped.
        /*
              Service Stop Code

              BackgroundPushService mYourService;
              mYourService = new BackgroundPushService();
              Intent mServiceIntent;
              mServiceIntent = new Intent(context, mYourService.getClass());
              FastSave.getInstance().saveBoolean(IS_SERVICE_STOPED, true);
              Log.d("PushData", "End Service  IS_STOPPED : " + FastSave.getInstance().getBoolean(IS_SERVICE_STOPED, false));
              if (isMyServiceRunning(mYourService.getClass())) context.stopService(mServiceIntent);
        */

        if(!FastSave.getInstance().getBoolean(IS_SERVICE_STOPED, false)) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, BackgroundServiceRestarter.class);
            this.sendBroadcast(broadcastIntent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }
}
