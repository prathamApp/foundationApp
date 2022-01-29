package com.pratham.foundation.database;

import android.content.Context;

import com.pratham.foundation.ApplicationClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class BackupDatabase {

    public static void backup(Context mContext) {
        try {
            File sd = new File(ApplicationClass.getStoragePath()+"/PrathamBackups");
//            File sd = new File(""+mContext.getExternalFilesDir("/PrathamBackups"));
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
        }
    }
}
