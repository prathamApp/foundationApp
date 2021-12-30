package com.pratham.foundation.async;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.ApplicationClass.BUILD_DATE;
import static com.pratham.foundation.utility.FC_Constants.FILE_DOWNLOAD_STARTED;
import static com.pratham.foundation.utility.FC_Constants.IS_DOWNLOADING;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.Modal_Download;
import com.pratham.foundation.modalclasses.Modal_FileDownloading;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;

@EBean
public class ContentDownloadingTask {

    private static final String TAG = ContentDownloadingTask.class.getSimpleName();
    String url;
    String dir_path;
    String f_name;
    String folder_name;
    ContentTable content;
    String downloadID;
    boolean unziping_error = false;
    Context context;
    //GamesDisplay gamesDisplay;
    //    DownloadService downloadService;
    ArrayList<ContentTable> levelContents;

    public ContentDownloadingTask(Context context) {
        this.context = context;
    }

    public void initialize(Modal_Download download) {
        this.url = download.getUrl();
        this.dir_path = download.getDir_path();
        this.f_name = download.getF_name();
        this.folder_name = download.getFolder_name();
        this.content = download.getContent();
        this.downloadID = download.getContent().getNodeId();
        this.levelContents = download.getLevelContents();
        //gamesDisplay = new GamesDisplay();
    }

    protected void afterInit() {
        Modal_FileDownloading modal_fileDownloading = new Modal_FileDownloading();
        modal_fileDownloading.setDownloadId(downloadID);
        modal_fileDownloading.setFilename(content.getNodeTitle());
        modal_fileDownloading.setProgress(0);
        modal_fileDownloading.setContentDetail(content);
        EventMessage eventMessage = new EventMessage();
        eventMessage.setModal_fileDownloading(modal_fileDownloading);
        eventMessage.setMessage(FILE_DOWNLOAD_STARTED);
        EventBus.getDefault().post(eventMessage);
        //gamesDisplay.fileDownloadStarted(downloadID, modal_fileDownloading);
    }

    @Background
    public void startContentDownload(Modal_Download download, boolean unzipFlg) {
        Log.d(TAG, "doInBackground: url: " + url);
        initialize(download);
        afterInit();
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            // String root = ApplicationClass.getStoragePath().toString();
            URL urlFormed = new URL(url);
            connection = (HttpURLConnection) urlFormed.openConnection();
            connection.setConnectTimeout(15000);
            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            Log.d(TAG, "doInBackground: urlFormed: " + urlFormed);
            Log.d(TAG, "doInBackground: getResponseCode: " + connection.getResponseCode());
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // getting file length
                dowloadImages();
                int lenghtOfFile = connection.getContentLength();
                if (lenghtOfFile < 0)
                    lenghtOfFile = (Integer.parseInt(content.getLevel()) > 0) ? Integer.parseInt(content.getLevel()) : 1;
                // input stream to read file - with 8k buffer
                input = connection.getInputStream();
                // Output stream to write file
                output = new FileOutputStream(dir_path + "/" + f_name);
                byte[] data = new byte[4096];
                long total = 0;
//                long download_percentage_old = 00;
                int count;
                while ((count = input.read(data)) != -1) {
/*                if (isCancelled()) {
                    input.close();
                    return false;
                }*/
                    total += count;
                    // writing data to file
                    output.write(data, 0, count);
                    long download_percentage_new = (100 * total) / lenghtOfFile;
                    updateProgress(download_percentage_new);
                }
                // flushing output AND closing streams
                output.close();
                input.close();

//                unziping_error = unzipFile(dir_path + "/" + f_name, dir_path);

                if (unzipFlg) {
                    unziping_error = unzipFile(dir_path + "/" + f_name, dir_path);
                    if (unziping_error)
                        unzipingError();
                    else
                        downloadCompleted();
                } else
                    downloadCompleted();

            } else {
                IS_DOWNLOADING = false;
                EventMessage eventMessage = new EventMessage();
                eventMessage.setMessage(FC_Constants.RESPONSE_CODE_ERROR);
                EventBus.getDefault().post(eventMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            IS_DOWNLOADING = false;
            EventMessage eventMessage = new EventMessage();
            eventMessage.setMessage(FC_Constants.FILE_DOWNLOAD_ERROR);
            EventBus.getDefault().post(eventMessage);
        }
    }

    private void unzipingError() {
        IS_DOWNLOADING = false;
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessage(FC_Constants.UNZIPPING_ERROR);
        EventBus.getDefault().post(eventMessage);
        onCompleteContentDownloadTase(false);
    }

    @Background
    public void downloadCompleted() {
        Log.d(TAG, "updateFileProgress: " + downloadID);
        ArrayList<ContentTable> temp = new ArrayList<>(levelContents);
        for(int j=0; j<levelContents.size(); j++){
            levelContents.get(j).setIsDownloaded("" + true);
            levelContents.get(j).setOnSDCard(false);
        }
/*
        for(int j=0; j<levelContents.size(); j++){
            if(content.getResourceId().equalsIgnoreCase(levelContents.get(j).getResourceId()))
                content = temp.get(j);
        }
        temp.add(content);
        for (int i = 0; i < temp.size(); i++) {
            temp.get(i).setIsDownloaded("" + true);
            temp.get(i).setOnSDCard(false);
            if (temp.get(i).getStudentId() != null)
                temp.get(i).setStudentId(temp.get(i).getStudentId() + "," + FastSave.getInstance().getString(CURRENT_STUDENT_ID, ""));
            else
                temp.get(i).setStudentId(FastSave.getInstance().getString(CURRENT_STUDENT_ID, ""));
        }
*/
        IS_DOWNLOADING = false;
        AppDatabase.getDatabaseInstance(context).getContentTableDao().addContentList(levelContents);
        onCompleteContentDownloadTase(true);
    }

    private void updateProgress(long download_percentage_new) {
        if (downloadID != null) {
            Modal_FileDownloading modal_fileDownloading = new Modal_FileDownloading();
            modal_fileDownloading.setDownloadId(downloadID);
            modal_fileDownloading.setFilename(content.getNodeTitle());
            modal_fileDownloading.setProgress((int) download_percentage_new);
            modal_fileDownloading.setContentDetail(content);
            onProgressUpdate(modal_fileDownloading);
        }
    }

    private void dowloadImages() {
        for (ContentTable detail : levelContents) {
            if (detail.getNodeServerImage() != null) {
                String thumbPath = detail.getNodeServerImage();
                String f_name = detail.getNodeServerImage()
                        .substring(detail.getNodeServerImage().lastIndexOf('/') + 1);
                if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
                    if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                        f_name = detail.getNodeServerImage()
                                .substring(detail.getNodeServerImage().lastIndexOf('/') + 1);
                        thumbPath = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_IMAGES + f_name;
                    }
                }
                downloadImage(thumbPath, f_name);
            }
        }
        if (content.getNodeServerImage() != null) {
            String f_name = content.getNodeServerImage()
                    .substring(content.getNodeServerImage().lastIndexOf('/') + 1);
            String thumbPath = content.getNodeServerImage();
            if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
                if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                    f_name = content.getNodeServerImage()
                            .substring(content.getNodeServerImage().lastIndexOf('/') + 1);
                    thumbPath = FC_Constants.RASP_IP + FC_Constants.RASP_LOCAL_IMAGES + f_name;
                }
            }
            downloadImage(thumbPath, f_name);
        }
    }

    public static void downloadImage(String url, String filename) {
        File dir = new File(ApplicationClass.foundationPath + "" + App_Thumbs_Path); //Creating an internal dir;
        if (!dir.exists()) dir.mkdirs();
        AndroidNetworking.download(url, dir.getAbsolutePath(), filename)
                .setPriority(Priority.HIGH)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.d("image::", "DownloadComplete");
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("image::", "Not Downloaded");
                    }
                });
    }

    private boolean unzipFile(String source, String destination) {
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessage(FC_Constants.UNZIPPING_DATA_FILE);
        EventBus.getDefault().post(eventMessage);
        ZipFile zipFile = null;
        IS_DOWNLOADING = false;
        try {
            zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
            new File(source).delete();
            return false;
        } catch (ZipException e) {
            e.printStackTrace();
//            if (folder_name.equalsIgnoreCase(VIDEO)||folder_name.equalsIgnoreCase(PDF))
//                unziping_error = false;
//            else
//                return true;
        }
        return false;
    }

    public void onProgressUpdate(Modal_FileDownloading modal_fileDownloading) {
//        Modal_FileDownloading mfd = (Modal_FileDownloading) values[0];
        Log.d(TAG, "onProgressUpdate: " + downloadID + ":::" + f_name + ":::" + modal_fileDownloading.getProgress());
        EventMessage eventMessage = new EventMessage();
        eventMessage.setModal_fileDownloading(modal_fileDownloading);
        eventMessage.setMessage(FC_Constants.FILE_DOWNLOAD_UPDATE);
        EventBus.getDefault().post(eventMessage);
    }

    protected void onCompleteContentDownloadTase(boolean success) {
        Log.d(TAG, "onPostExecute");
        IS_DOWNLOADING = false;
        if (success) {
            //update data dowanlod chanle
            Modal_Log modal_log = new Modal_Log();
            modal_log.setErrorType("DOWNLOAD");
            modal_log.setExceptionMessage(content.nodeTitle);
            modal_log.setMethodName(content.nodeId);
            modal_log.setCurrentDateTime(FC_Utility.getCurrentDateTime());
            modal_log.setSessionId(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            modal_log.setExceptionStackTrace("APK BUILD DATE : "+BUILD_DATE);
            modal_log.setDeviceId("" + FC_Utility.getDeviceID());
            modal_log.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "no_group"));
            if(url.contains(FC_Constants.RASP_IP))
                modal_log.setLogDetail("PI#"+url);
            else
                modal_log.setLogDetail("INTERNET#"+url);

            AppDatabase.getDatabaseInstance(context).getLogsDao().insertLog(modal_log);

            EventMessage eventMessage = new EventMessage();
            eventMessage.setMessage(FC_Constants.FILE_DOWNLOAD_COMPLETE);
            EventBus.getDefault().post(eventMessage);
//            gamesDisplay.onDownloadCompleted(downloadID, content);
        } else {
            EventMessage eventMessage = new EventMessage();
            eventMessage.setMessage(FC_Constants.FILE_DOWNLOAD_ERROR);
            EventBus.getDefault().post(eventMessage);
//            gamesDisplay.ondownloadError(downloadID);
        }
    }
}
