package com.pratham.foundation.utility;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.services.shared_preferences.FastSave;

import net.alhazmy13.catcho.library.Catcho;
import net.alhazmy13.catcho.library.error.CatchoError;

public class CatchoTransparentActivity extends BaseActivity {
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object[] objects) {
                CatchoError error = (CatchoError) getIntent().getSerializableExtra(Catcho.ERROR);
                Modal_Log log = new Modal_Log();
                log.setCurrentDateTime(FC_Utility.GetCurrentDateTime());
                log.setErrorType("ERROR");
                log.setExceptionMessage(error.toString());
                log.setExceptionStackTrace(error.getError());
                log.setMethodName("NO_METHOD");
                log.setGroupId(FastSave.getInstance().getString("", "no_group"));
                log.setDeviceId("");
                AppDatabase.getDatabaseInstance(CatchoTransparentActivity.this).getLogsDao().insertLog(log);
                new BackupDatabase().backup(CatchoTransparentActivity.this);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                finishAffinity();
            }
        }.execute();
    }
}
