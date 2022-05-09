package com.pratham.foundation.ui.app_home.profile_new.students_synced_data.students_synced_fragment;

import static com.pratham.foundation.utility.FC_Utility.dpToPx;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.modalclasses.StudentSyncDetails;
import com.pratham.foundation.ui.app_home.profile_new.students_synced_data.data_synced_by_student.DataSyncedFragment;
import com.pratham.foundation.ui.app_home.profile_new.students_synced_data.data_synced_by_student.DataSyncedFragment_;
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

@EFragment(R.layout.fragment_synced_student)
public class SyncedStudentFragment extends Fragment implements SyncedStudentContract.SyncedStudentView,
        SyncedStudentContract.StudentItemClicked {

    @Bean(SyncedStudentPresenter.class)
    SyncedStudentContract.SyncedStudentPresenter presenter;

    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;
    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.tv_date)
    TextView tv_date;
    @ViewById(R.id.tv_syncType)
    TextView tv_syncType;

    List<StudentSyncDetails> studentSyncDetailsList;
    public SyncedStudentAdapter studentAdapter;
    Context mContext;

    String endDate,startDate, syncType;

    public SyncedStudentFragment() {
        // Required empty public constructor
    }

    @AfterViews
    public void initialize() {
        mContext = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            startDate = bundle.getString("startDate");
            endDate = bundle.getString("endDate");
            syncType = bundle.getString("syncType");
            studentSyncDetailsList = new ArrayList<>();
//            studentUsageSyncDetailsList = new ArrayList<>();
            tv_date.setText(startDate + "    ->    "+endDate);
            studentAdapter = null;
//            Toast.makeText(getActivity(), "startDate"+startDate + "\nendDate: "+endDate, Toast.LENGTH_SHORT).show();
            presenter.setView(SyncedStudentFragment.this);
            showLoader();
            if(syncType.equalsIgnoreCase(FC_Constants.SYNC_ACCESSED_USERS))
                tv_syncType.setText(""+ getResources().getString(R.string.accessed_users));
            else
                tv_syncType.setText(""+getResources().getString(R.string.registered_users));

                presenter.getDataFromServer(syncType, startDate, endDate);
        }
    }

    @Override
    public void addStudentList(List<StudentSyncDetails> studentSyncDetailsLists) {
        studentSyncDetailsList.clear();
        studentSyncDetailsList.addAll(studentSyncDetailsLists);
    }

    @Override
    @UiThread
    public void showToast(String msg){
        Toast.makeText(mContext, ""+msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyAdapter() {
        if (studentAdapter == null) {
            try {
                Log.d("crashDetection", "notifyAdapter 3 : ");
                studentAdapter = new SyncedStudentAdapter(mContext, studentSyncDetailsList, this);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 1);
                my_recycler_view.setLayoutManager(mLayoutManager);
                my_recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(mContext), true));
                my_recycler_view.setItemAnimator(new DefaultItemAnimator());
                my_recycler_view.setAdapter(studentAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            studentAdapter.notifyDataSetChanged();
        }
        dismissLoadingDialog();
    }

    @Override
    public void showDetails(StudentSyncDetails studentItem, int pos) {
        Bundle bundle = new Bundle();
        bundle.putString("sName", studentItem.getName());
        bundle.putString("eID", studentItem.getEId());
        bundle.putString("studentID", studentItem.getStudentID());
        bundle.putString("tResources", studentItem.getTotalResources());
        bundle.putString("tCourses", studentItem.getTotalCourses());
        bundle.putString("startDate", startDate);
        bundle.putString("endDate", endDate);
        FC_Utility.showFragment(getActivity(), new DataSyncedFragment_(), R.id.frame_student,
                bundle, DataSyncedFragment.class.getSimpleName());
    }

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