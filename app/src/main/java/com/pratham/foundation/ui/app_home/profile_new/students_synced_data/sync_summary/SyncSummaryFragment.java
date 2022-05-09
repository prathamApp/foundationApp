package com.pratham.foundation.ui.app_home.profile_new.students_synced_data.sync_summary;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.modalclasses.StudentSyncDetails;
import com.pratham.foundation.modalclasses.StudentUsageSyncDetails;
import com.pratham.foundation.modalclasses.SyncSummaryModal;
import com.pratham.foundation.ui.app_home.profile_new.students_synced_data.students_synced_fragment.SyncedStudentAdapter;
import com.pratham.foundation.ui.app_home.profile_new.students_synced_data.students_synced_fragment.SyncedStudentFragment;
import com.pratham.foundation.ui.app_home.profile_new.students_synced_data.students_synced_fragment.SyncedStudentFragment_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_sync_summary)
public class SyncSummaryFragment extends Fragment implements SyncSummaryContract.SyncSummaryView {

    @Bean(SyncSummaryPresenter.class)
    SyncSummaryContract.SyncSummaryPresenter presenter;

    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;
    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.tv_date)
    TextView tv_date;
    @ViewById(R.id.tv_coursecount)
    TextView tv_coursecount;
    @ViewById(R.id.tv_attendancecount)
    TextView tv_attendancecount;
    @ViewById(R.id.tv_total_sessions)
    TextView tv_total_sessions;
    @ViewById(R.id.tv_total_stud_accessed)
    TextView tv_total_stud_accessed;
    @ViewById(R.id.tv_total_stud_registered)
    TextView tv_total_stud_registered;
    @ViewById(R.id.tv_total_groups)
    TextView tv_total_groups;
    @ViewById(R.id.tv_error_msg)
    TextView tv_error_msg;
    @ViewById(R.id.ll_error_msg)
    LinearLayout ll_error_msg;

    public static List<StudentUsageSyncDetails> studentUsageSyncDetailsList;
    List<StudentSyncDetails> studentSyncDetailsList;
    public SyncedStudentAdapter studentAdapter;
    Context mContext;

    String endDate, startDate;

    public SyncSummaryFragment() {
        // Required empty public constructor
    }

    @AfterViews
    public void initialize() {
        mContext = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            startDate = bundle.getString("startDate");
            endDate = bundle.getString("endDate");
            studentSyncDetailsList = new ArrayList<>();
            studentUsageSyncDetailsList = new ArrayList<>();
            tv_date.setText(startDate + "    ->    " + endDate);
            studentAdapter = null;
//            Toast.makeText(getActivity(), "startDate"+startDate + "\nendDate: "+endDate, Toast.LENGTH_SHORT).show();
            presenter.setView(SyncSummaryFragment.this);
            showLoader();
            presenter.getDataFromServer(startDate, endDate);
        }
    }

    @UiThread
    @Override
    public void showToast(String msg){
        Toast.makeText(mContext, ""+msg, Toast.LENGTH_SHORT).show();
    }

    @UiThread
    @Override
    public void addSummary(SyncSummaryModal syncDetails) {
        try {
            tv_attendancecount.setText("" + syncDetails.getTotalAttendance());
            tv_coursecount.setText("" + syncDetails.getTotalCourses());
            tv_total_sessions.setText("" + syncDetails.getTotalSessions());
            tv_total_stud_accessed.setText("" + syncDetails.getTotalStudentsAccessed());
            tv_total_stud_registered.setText("" + syncDetails.getTotalStudentsRegistered());
            tv_total_groups.setText("" + syncDetails.getTotalGroups());
            if (syncDetails.getIsError() == 1) {
                ll_error_msg.setVisibility(View.VISIBLE);
                tv_error_msg.setText("" + syncDetails.getErrorMsg());
            } else {
                ll_error_msg.setVisibility(View.GONE);
            }
            dismissLoadingDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.btn_total_stud_accessed)
    public void showAccessedData() {
        Bundle bundle = new Bundle();
        bundle.putString("startDate", startDate);
        bundle.putString("endDate", endDate);
        bundle.putString("syncType", FC_Constants.SYNC_ACCESSED_USERS);
        FC_Utility.showFragment(getActivity(), new SyncedStudentFragment_(), R.id.frame_student,
                bundle, SyncedStudentFragment.class.getSimpleName());
    }
    @Click(R.id.btn_total_stud_registered)
    public void showRegisteredData() {
        Bundle bundle = new Bundle();
        bundle.putString("startDate", startDate);
        bundle.putString("endDate", endDate);
        bundle.putString("syncType", FC_Constants.SYNC_REGISTERED_USERS);
        FC_Utility.showFragment(getActivity(), new SyncedStudentFragment_(), R.id.frame_student,
                bundle, SyncedStudentFragment.class.getSimpleName());
    }

/*    @Override
    public void showDetails(StudentSyncDetails studentItem, int pos) {
        Bundle bundle = new Bundle();
        bundle.putString("sName", studentItem.getName());
        bundle.putString("eID", studentItem.getEId());
        bundle.putString("tResources", studentItem.getTotalResources());
        bundle.putString("tCourses", studentItem.getTotalCourses());
        FC_Utility.showFragment(getActivity(), new DataSyncedFragment_(), R.id.frame_student,
                bundle, DataSyncedFragment.class.getSimpleName());
    }*/

    @Override
    public void showNoData() {

    }

    public CustomLodingDialog myLoadingDialog;

    @UiThread
    @Override
    public void showLoader() {
        try {
            if (myLoadingDialog == null) {
                myLoadingDialog = new CustomLodingDialog(mContext);
                myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myLoadingDialog.setContentView(R.layout.loading_dialog);
                myLoadingDialog.setCanceledOnTouchOutside(false);
                myLoadingDialog.show();
            } else if (!myLoadingDialog.isShowing())
                myLoadingDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void dismissLoadingDialog() {
        try {
            new Handler().postDelayed(() -> {
                if (myLoadingDialog != null && myLoadingDialog.isShowing() && !desFlag)
                    myLoadingDialog.dismiss();
            }, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean desFlag = false;

    @Override
    public void onDestroy() {
        desFlag = true;
        super.onDestroy();
    }

    @ViewById(R.id.main_back)
    ImageView main_back;

    @Click(R.id.main_back)
    @UiThread
    public void backPressed() {
        getActivity().onBackPressed();
    }
}