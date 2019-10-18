package com.pratham.foundation.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.utility.FC_Constants;


import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.concurrent.Executors;

public class DownloadData {


/*    public DownloadData(SplashContract.SplashPresenter splashPresenter) {
        this.splashPresenter = splashPresenter;
    }*/

    private String getAuthHeader() {
        String encoded = Base64.encodeToString(("pratham" + ":" + "pratham").getBytes(), Base64.NO_WRAP);
        return "Basic " + encoded;
    }

    public String doInBackground() {
        try {
            AndroidNetworking.download("http://192.168.4.1/data/FCA.zip", ApplicationClass.foundationPath,
                    "FCA.zip")
                    .setExecutor(Executors.newSingleThreadExecutor())
                    .setPriority(Priority.HIGH)
                    .build()
                    .setDownloadProgressListener((bytesDownloaded, totalBytes) -> {
                        int progress=(int) ((100 * bytesDownloaded) / totalBytes);
                        Log.d("doInBackground:", "" + progress);
                        EventMessage message=new EventMessage();
                        message.setMessage(FC_Constants.DATA_FILE_PROGRESS);
                        message.setProgress(progress);
                        EventBus.getDefault().post(message);
                    })
                    .startDownload(new DownloadListener() {
                        @SuppressLint("StaticFieldLeak")
                        @Override
                        public void onDownloadComplete() {
                            new AsyncTask<Object, Void, Object>() {
                                @Override
                                protected Object doInBackground(Object[] objects) {
                                    EventMessage message=new EventMessage();
                                    message.setMessage(FC_Constants.UNZIPPING_DATA_FILE);
                                    EventBus.getDefault().post(message);
                                    unzipFile(ApplicationClass.foundationPath + "/FCA.zip", ApplicationClass.foundationPath);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Object o) {
                                    super.onPostExecute(o);
                                    EventMessage message=new EventMessage();
                                    message.setMessage(FC_Constants.DATA_ZIP_COMPLETE);
                                    EventBus.getDefault().post(message);
                                }
                            }.execute();
                        }

                        @Override
                        public void onError(ANError anError) {
                            EventMessage message=new EventMessage();
                            message.setMessage(FC_Constants.DATA_DOWNLOAD_ERROR);
                            EventBus.getDefault().post(message);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void unzipFile(String source, String destination) {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
            Log.d("unzipFile:", ""+zipFile.getProgressMonitor().getResult());
            new File(source).delete();
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

}
