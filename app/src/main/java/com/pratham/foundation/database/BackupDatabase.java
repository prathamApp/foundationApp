package com.pratham.foundation.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.utility.FC_Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Objects;

public class BackupDatabase {

    public static void backup(Context mContext) {
        try {
            File sd = new File(ApplicationClass.getStoragePath().getAbsolutePath()+"/PrathamBackups");
            if(!sd.exists())
                sd.mkdirs();
            if (sd.canWrite()) {
                File currentDB = mContext.getDatabasePath(AppDatabase.DB_NAME);
                File parentPath = currentDB.getParentFile();
                Log.i("sizess",parentPath.listFiles().length+"--");
                for (File f : parentPath.listFiles()) {
                    Log.i("sizess1",f.getName());
                    File temp = new File(sd, f.getName());
                    if (!temp.exists())
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        {
                            ContentResolver resolver = mContext.getContentResolver();
                            ContentValues valuesvideos = new ContentValues();
                            valuesvideos.put(MediaStore.MediaColumns.DISPLAY_NAME, f.getName());
                            String fileMimeType = FC_Utility.getMimeType(f.getAbsolutePath());
                            valuesvideos.put(MediaStore.MediaColumns.MIME_TYPE, fileMimeType);
                            valuesvideos.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS
                                    + File.separator + "PrathamBackups" );
                            final Uri uriSavedVideo = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, valuesvideos);
                            FileChannel src = new FileInputStream(f).getChannel();
                            FileOutputStream  bos = (FileOutputStream) resolver.openOutputStream(Objects.requireNonNull(uriSavedVideo));
                            FileChannel dst = bos.getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                        }
                        else
                        {
                            temp.createNewFile();
                            FileChannel src = new FileInputStream(f).getChannel();
                            FileChannel dst = new  FileOutputStream (temp).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                        }
                    }
                    else
                    {
                        FileChannel src = new FileInputStream(f).getChannel();
                        FileChannel dst = new  FileOutputStream (temp).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*try {
            File sd = new File(ApplicationClass.getStoragePath()+"/PrathamBackups");
            if(!sd.exists())
                sd.mkdirs();
            if (sd.canWrite()) {
                File currentDB = mContext.getDatabasePath(AppDatabase.DB_NAME);
                File parentPath = currentDB.getParentFile();
                for (File f : parentPath.listFiles()) {
                    File temp = new File(sd, f.getName());
                    if (!temp.exists()) temp.createNewFile();
                    FileChannel src = new FileInputStream(f).getChannel();
                    FileChannel dst = new FileOutputStream(temp).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
