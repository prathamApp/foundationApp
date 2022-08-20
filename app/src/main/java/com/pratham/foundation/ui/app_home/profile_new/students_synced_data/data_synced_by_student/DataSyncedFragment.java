package com.pratham.foundation.ui.app_home.profile_new.students_synced_data.data_synced_by_student;

import static com.pratham.foundation.utility.FC_Utility.dpToPx;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
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
import com.pratham.foundation.modalclasses.StudentSyncUsageModal;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_data_synced)
public class DataSyncedFragment extends Fragment implements DataSyncedContract.DataSyncedView {

    @Bean(DataSyncedPresenter.class)
    DataSyncedContract.DataSyncedPresenter presenter;

    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;
    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.tv_date)
    TextView tv_date;
    @ViewById(R.id.tv_Resources)
    TextView tv_Resources;
    Context mContext;
    StudentSyncDetails studentItem;
    List<StudentSyncUsageModal> syncDetailsList;
    String sName, eID, tResources, tCourses,startDate,endDate,studentID;
    DataSyncedAdapter syncedAdapter;

    public DataSyncedFragment() {
        // Required empty public constructor
    }

    @AfterViews
    public void initialize() {
        mContext = getActivity();
        Bundle bundle = getArguments();
        syncDetailsList = new ArrayList<>();
        if (bundle != null) {
            sName = bundle.getString("sName");
            eID = bundle.getString("eID");
            studentID = bundle.getString("studentID");
            tResources = bundle.getString("tResources");
            tCourses = bundle.getString("tCourses");
            startDate = bundle.getString("startDate");
            endDate = bundle.getString("endDate");
            presenter.setView(DataSyncedFragment.this);

            tv_Topic.setText(sName);
            tv_date.setText(eID);
            tv_Resources.setText(getResources().getString(R.string.total_resources)
                    +" : "+ tResources+"\n"
                    + getResources().getString(R.string.total_courses) + " : " + tCourses);
            showLoader();
            presenter.getFinalList(startDate,endDate,studentID);
        }
    }

    @Override
    public void addStudentList(List<StudentSyncUsageModal> detailsList){
        syncDetailsList.clear();
        syncDetailsList.addAll(detailsList);
    }

    @Override
    public void notifyAdapter() {
        if (syncedAdapter == null) {
            try {
                Log.d("crashDetection", "notifyAdapter 3 : ");
                syncedAdapter = new DataSyncedAdapter(mContext, syncDetailsList);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
                my_recycler_view.setLayoutManager(mLayoutManager);
                my_recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(mContext), true));
                my_recycler_view.setItemAnimator(new DefaultItemAnimator());
                my_recycler_view.setAdapter(syncedAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            syncedAdapter.notifyDataSetChanged();
        }
        dismissLoadingDialog();
    }

    @Override
    @UiThread
    public void showToast(String msg){
        Toast.makeText(mContext, ""+msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoData() { }

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

    @Click(R.id.main_back)
    @UiThread
    public void backPressed() {
        getActivity().onBackPressed();
    }

}
