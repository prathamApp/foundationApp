package com.pratham.foundation.ui.app_home.profile_new.show_sync_log;

import static com.pratham.foundation.utility.FC_Utility.dpToPx;

import android.content.res.Configuration;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_show_sync_logs)
public class ShowSyncLogActivity extends BaseActivity implements
        ShowSyncLogContract.ShowSyncLogView{

    @Bean(ShowSyncLogPresenter.class)
    ShowSyncLogContract.ShowSyncLogPresenter presenter;

    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.sync_recycler_view)
    RecyclerView recycler_view;
    String sub_Name;
    ShowSyncLogAdapter syncLogAdapter;
    List<Modal_Log> modal_logList;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Configuration config = getResources().getConfiguration();
        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
        presenter.setView(ShowSyncLogActivity.this);
        presenter.getLogsData();
    }

    @UiThread
    @Override
    public void addToAdapter(List<Modal_Log> logList) {
        modal_logList = logList;
        if(syncLogAdapter==null) {
            syncLogAdapter = new ShowSyncLogAdapter(this, modal_logList);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.setNestedScrollingEnabled(false);
            recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(this), true));
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(syncLogAdapter);
        }else
            syncLogAdapter.notifyDataSetChanged();
    }

    @ViewById(R.id.rl_no_data)
    RelativeLayout rl_no_data;
    @UiThread
    @Override
    public void showNoData() {
        recycler_view.setVisibility(View.GONE);
        rl_no_data.setVisibility(View.VISIBLE);
    }

    @UiThread
    public void setStudentProfileImage(String sImage) {
    }

    @Click(R.id.main_back)
    public void pressedBack(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}