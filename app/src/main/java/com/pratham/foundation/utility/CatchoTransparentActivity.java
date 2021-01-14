package com.pratham.foundation.utility;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.ProcessPhoenix;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.services.shared_preferences.FastSave;

import net.alhazmy13.catcho.library.Catcho;
import net.alhazmy13.catcho.library.error.CatchoError;

public class CatchoTransparentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catcho);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        CatchoError error = (CatchoError) getIntent().getSerializableExtra(Catcho.ERROR);
        Modal_Log log = new Modal_Log();
        log.setCurrentDateTime(FC_Utility.getCurrentDateTime());
        log.setErrorType("ERROR");
        log.setExceptionMessage(error.toString());
        log.setExceptionStackTrace(error.getError());
        log.setMethodName("NO_METHOD");
        log.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "no_group"));
        log.setDeviceId("" + FC_Utility.getDeviceID());
        endSession();
        AppDatabase.getDatabaseInstance(CatchoTransparentActivity.this).getLogsDao().insertLog(log);
        BackupDatabase.backup(CatchoTransparentActivity.this);
        findViewById(R.id.catcho_button).setOnClickListener(v -> {
//            finishAffinity();
            ProcessPhoenix.triggerRebirth(CatchoTransparentActivity.this);
        });
    }

    public void endSession() {
        try {
            String curSession = AppDatabase.getDatabaseInstance(CatchoTransparentActivity.this).getStatusDao().getValue("CurrentSession");
            String toDateTemp = AppDatabase.getDatabaseInstance(CatchoTransparentActivity.this).getSessionDao().getToDate(curSession);
            if (toDateTemp.equalsIgnoreCase("na")) {
                AppDatabase.getDatabaseInstance(CatchoTransparentActivity.this).getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
            }
            BackupDatabase.backup(CatchoTransparentActivity.this);
        } catch (Exception e) {
            String curSession = AppDatabase.getDatabaseInstance(CatchoTransparentActivity.this).getStatusDao().getValue("CurrentSession");
            AppDatabase.getDatabaseInstance(CatchoTransparentActivity.this).getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
            e.printStackTrace();
        }
    }

}
