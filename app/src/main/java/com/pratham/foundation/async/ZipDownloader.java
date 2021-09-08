package com.pratham.foundation.async;

import android.content.Context;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.modalclasses.Modal_Download;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FileUtils;

import org.androidannotations.annotations.EBean;

import java.io.File;
import java.util.ArrayList;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.ui.app_home.HomeActivity.contentDownloadingTask;
import static com.pratham.foundation.utility.FC_Constants.IS_DOWNLOADING;
import static com.pratham.foundation.utility.FC_Constants.currentSubjectFolder;

/**
 * Created by User on 16/11/15.
 */
@EBean
public class ZipDownloader {

    private static final String TAG = ZipDownloader.class.getSimpleName();
    String filename;
    Context context;
    boolean unzipFlg;

    public ZipDownloader(Context context) {
        this.context = context;
    }

    public void initialize(Context context, String url,
                           String foldername, String f_name, ContentTable contentDetail,
                           ArrayList<ContentTable> levelContents, boolean unzipFlg) {
        this.filename = f_name;
        this.unzipFlg = unzipFlg;
        createFolderAndStartDownload(url, foldername, f_name, contentDetail, context, levelContents);
    }

    /*Creating folder in internal.
     *That internal might be of android internal or sdcard internal (if available and writable)
     * */
    private void createFolderAndStartDownload(String url, String foldername, String f_name,
                                              ContentTable contentDetail,
                                              Context context,
                                              ArrayList<ContentTable> levelContents) {

        currentSubjectFolder = FastSave.getInstance().getString(FC_Constants.CURRENT_FOLDER_NAME, currentSubjectFolder);

        File mydir = null;
        mydir = new File(ApplicationClass.foundationPath + "/.FCA");
        if (!mydir.exists()) mydir.mkdirs();
        mydir = new File(ApplicationClass.foundationPath + "/.FCA/"+currentSubjectFolder+"/");
        if (!mydir.exists()) mydir.mkdirs();
        mydir = new File(ApplicationClass.foundationPath +  "" + App_Thumbs_Path );
        if (!mydir.exists()) mydir.mkdirs();
        mydir = new File(ApplicationClass.foundationPath + "/.FCA/"+currentSubjectFolder+"/Game");
        if (!mydir.exists()) mydir.mkdirs();

        if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
            if (foldername.equalsIgnoreCase(FC_Constants.GAME)) {
                f_name = f_name.substring(0, f_name.lastIndexOf("."));
                File temp_dir = new File(mydir.getAbsolutePath() + "/" + f_name);
                if (!temp_dir.exists()) temp_dir.mkdirs();
                mydir = temp_dir;
            }
        }

        Log.d("internal_file", mydir.getAbsolutePath());
        Modal_Download modal_download = new Modal_Download();
        modal_download.setUrl(url);
        modal_download.setDir_path(mydir.getAbsolutePath());
        modal_download.setF_name(filename);
        modal_download.setFolder_name(foldername);
        modal_download.setContent(contentDetail);
        modal_download.setLevelContents(levelContents);
        IS_DOWNLOADING = true;
        contentDownloadingTask.startContentDownload(modal_download, unzipFlg);
//        new DownloadingTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, modal_download);
    }


    private void createOverSdCardAndStartDownload(String url, String foldername, String f_name,
                                                  ContentTable contentDetail,
                                                  ArrayList<ContentTable> levelContents) {
        String path = FastSave.getInstance().getString(ApplicationClass.contentSDPath, "");
        if (path.isEmpty())
            return;
        DocumentFile documentFile = DocumentFile.fromFile(new File(path));
        if (documentFile.findFile("/.FCA/"+currentSubjectFolder+"/" + foldername) != null)
            documentFile = documentFile.findFile("/.FCA/"+currentSubjectFolder+"/" + foldername);
        else
            documentFile = documentFile.createDirectory("/.FCA/"+currentSubjectFolder+"/" + foldername);
        if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
            if (foldername.equalsIgnoreCase(FC_Constants.GAME)) {
                f_name = f_name.substring(0, f_name.lastIndexOf("."));
                if (documentFile.findFile(f_name) != null)
                    documentFile = documentFile.findFile(f_name);
                else
                    documentFile = documentFile.createDirectory(f_name);
            }
        }
        Modal_Download modal_download = new Modal_Download();
        modal_download.setUrl(url);
        modal_download.setDir_path(FileUtils.getPath(ApplicationClass.getInstance(), documentFile.getUri()));
        modal_download.setF_name(filename);
        modal_download.setFolder_name(foldername);
        modal_download.setContent(contentDetail);
        modal_download.setLevelContents(levelContents);
        IS_DOWNLOADING = true;
        contentDownloadingTask.startContentDownload(modal_download, unzipFlg);
//        new DownloadingTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, modal_download);
    }
}