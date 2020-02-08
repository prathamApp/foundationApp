package com.pratham.foundation.async;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.utility.FC_Constants;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.pratham.foundation.database.AppDatabase.DB_NAME;
import static com.pratham.foundation.database.AppDatabase.appDatabase;


public class CopyDbToOTG extends AsyncTask {

    String actPhotoPath;
    DocumentFile mediaFolder;
    int totalActivityFolders;
    File[] files;

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            actPhotoPath = Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/ActivityPhotos/";
            FC_Constants.TransferedImages = 0;
            totalActivityFolders = 0;

            Uri treeUri = (Uri) objects[0];
            DocumentFile rootFile = DocumentFile.fromTreeUri(ApplicationClass.getInstance(), treeUri);

            DocumentFile fca_backup_file = rootFile.findFile("FCA_DBs");
            if (fca_backup_file == null)
                fca_backup_file = rootFile.createDirectory("FCA_DBs");

            String thisdeviceFolderName = "DeviceId_" + appDatabase.getStatusDao().getValue("DeviceId");
            DocumentFile thisTabletFolder = fca_backup_file.findFile(thisdeviceFolderName);
            if (thisTabletFolder == null)
                thisTabletFolder = fca_backup_file.createDirectory(thisdeviceFolderName);

            String media_Folder = "FC_Media";
            mediaFolder = thisTabletFolder.findFile(media_Folder);
            if (mediaFolder == null)
                mediaFolder = thisTabletFolder.createDirectory(media_Folder);

            File currentDB = ApplicationClass.getInstance().getDatabasePath(DB_NAME);
            File parentPath = currentDB.getParentFile();

            for (File f : parentPath.listFiles()) {
                DocumentFile file = thisTabletFolder.findFile(f.getName());
                if (file != null) file.delete();
                file = thisTabletFolder.createFile("image", f.getName());
                OutputStream out = ApplicationClass.getInstance().getContentResolver().openOutputStream(file.getUri());
                FileInputStream in = new FileInputStream(f.getAbsolutePath());
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                out.flush();
                out.close();
            }
            File activityPhotosFile = new File(actPhotoPath);
            files = activityPhotosFile.listFiles();
            totalActivityFolders = files.length;
            if(totalActivityFolders>0)
                CopyPhotos( 0);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void CopyPhotos(int no) {
        try {
            if(no<totalActivityFolders) {
                String actPhotoPath = files[no].toString();
                String actPhotoName = files[no].getName();
                File activityPhotosFile = new File(actPhotoPath);

                if (activityPhotosFile.exists() && activityPhotosFile.isDirectory()) {
                    DocumentFile studentFolder = mediaFolder.findFile(actPhotoName);
                    if (studentFolder == null)
                        studentFolder = mediaFolder.createDirectory(actPhotoName);
                    File[] files = activityPhotosFile.listFiles();
                    Log.d("Files", "FolderName: " + actPhotoPath);
                    for (int i = 0; i < files.length; i++) {
                        Log.d("Files", "FileName: " + files[i].getName());
                        DocumentFile file = studentFolder.createFile("image", files[i].getName());
                        OutputStream out = ApplicationClass.getInstance().getContentResolver().openOutputStream(file.getUri());
                        FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        out.flush();
                        out.close();
                        FC_Constants.TransferedImages++;
                    }
                }
                else if(activityPhotosFile.exists() && activityPhotosFile.isFile()){
                    Log.d("Files", "FileName: " + files[no].getName());
                    DocumentFile file = mediaFolder.createFile("image", files[no].getName());
                    OutputStream out = ApplicationClass.getInstance().getContentResolver().openOutputStream(file.getUri());
                    FileInputStream in = new FileInputStream(files[no].getAbsolutePath());
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    out.flush();
                    out.close();
                    FC_Constants.TransferedImages++;
                }
                no++;
                CopyPhotos(no);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        EventMessage message = new EventMessage();
        if ((boolean) o) message.setMessage(FC_Constants.BACKUP_DB_COPIED);
        else message.setMessage(FC_Constants.BACKUP_DB_NOT_COPIED);
        EventBus.getDefault().post(message);
    }
}
