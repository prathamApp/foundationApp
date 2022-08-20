package com.pratham.foundation.ui.app_home.profile_new.students_synced_data;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.SyncLog;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.app_home.profile_new.students_synced_data.sync_summary.SyncSummaryFragment;
import com.pratham.foundation.ui.app_home.profile_new.students_synced_data.sync_summary.SyncSummaryFragment_;
import com.pratham.foundation.ui.app_home.profile_new.temp_sync.SyncAdapter;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.List;

@EActivity(R.layout.activity_synced_student_datat)
public class SyncedStudentDataActivity extends BaseActivity {


    @ViewById(R.id.rl_calender)
    RelativeLayout rl_calender;
    @ViewById(R.id.frame_student)
    FrameLayout frame_student;

    public List<SyncLog> syncLogList;
    public static String selectedItem;
    public SyncAdapter syncAdapter;
    public int position = 0;

//    result_list_item

    @AfterViews
    public void initialize() {
        rl_calender.setVisibility(View.VISIBLE);
        frame_student.setVisibility(View.GONE);
        initializeCalendar();
    }

    @ViewById(R.id.sync_date_picker)
    DateRangeCalendarView sync_date_picker;
    private Calendar startDate;
    private Calendar endDate;

    private void initializeCalendar() {
        String a = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI);
        Log.d("INSTRUCTIONFRAG", "Select Subj: " + a);
        FC_Utility.setAppLocal(this, a);

        Calendar startSelectionDate = Calendar.getInstance();
//        startSelectionDate.add(Calendar.MONTH, -1);
        Calendar endSelectionDate = (Calendar) startSelectionDate.clone();
//        endSelectionDate.add(Calendar.DATE, 4);
//        sync_date_picker.setSelectedDateRange(startSelectionDate, endSelectionDate);
        sync_date_picker.setCalendarListener(new DateRangeCalendarView.CalendarListener() {
            @Override
            public void onFirstDateSelected(Calendar stDate) {
                startDate = stDate;
                Log.d("onDate", "START DATE : " + FC_Utility.getConvertedDate(startDate.getTimeInMillis()));
                endDate = null;
            }

            @Override
            public void onDateRangeSelected(Calendar s_Date, Calendar e_Date) {
                startDate = s_Date;
                endDate = e_Date;
                Log.d("onDate", "START DATE : " + FC_Utility.getConvertedDate(startDate.getTimeInMillis()));
                Log.d("onDate", "END DATE : " + FC_Utility.getConvertedDate(endDate.getTimeInMillis()));
            }
        });
    }

    @UiThread
    @Click(R.id.btn_sync_date_range)
    public void onSyncDateSelected() {
        if (FC_Utility.isDataConnectionAvailable(this)) {
            if (startDate == null) {
                Toast.makeText(this, ""+ getResources().getString(R.string.pls_select_date), Toast.LENGTH_SHORT).show();
            } else {
                if (endDate == null)
                    endDate = startDate;
                String sDate = FC_Utility.getConvertedDate(startDate.getTimeInMillis());
                String eDate = FC_Utility.getConvertedDate(endDate.getTimeInMillis());
                Log.d("onDate", "START DATE : " + sDate);
                Log.d("onDate", "END DATE : " + eDate);
                rl_calender.setVisibility(View.GONE);
                frame_student.setVisibility(View.VISIBLE);
                Bundle bundle = new Bundle();
                bundle.putString("startDate", sDate);
                bundle.putString("endDate", eDate);
//                FC_Utility.showFragment(this, new SyncedStudentFragment_(), R.id.frame_student,
//                        bundle, SyncedStudentFragment.class.getSimpleName());
                FC_Utility.showFragment(this, new SyncSummaryFragment_(), R.id.frame_student,
                        bundle, SyncSummaryFragment.class.getSimpleName());
            }
        }else {
            Toast.makeText(this, ""+ getResources().getString(R.string.connect_to_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @UiThread
    @Override
    public void onBackPressed() {
        Log.e("KIX : ", String.valueOf(this.getSupportFragmentManager().getBackStackEntryCount()));
        if (this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            if (this.getSupportFragmentManager().getBackStackEntryCount() == 1) {
                rl_calender.setVisibility(View.VISIBLE);
                frame_student.setVisibility(View.GONE);
            }
            this.getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Click(R.id.main_back)
    @UiThread
    public void backPressed() {
        onBackPressed();
    }
}
