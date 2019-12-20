package com.pratham.foundation.ui.admin_panel.fragment_admin_panel.tab_usage;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.Modal_NavigationMenu;
import com.pratham.foundation.modalclasses.Modal_ResourcePlayedByGroups;
import com.pratham.foundation.modalclasses.Modal_TotalDaysGroupsPlayed;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.List;

@EActivity(R.layout.activity_tab_usage)
public class TabUsageActivity extends BaseActivity implements TabUsageContract.TabUsageView, ContractOptions{

    @Bean(TabUsagePresenter.class)
    TabUsageContract.TabUsagePresenter presenter;

    @ViewById(R.id.txt_active)
    TextView txt_active;
    @ViewById(R.id.rv_stat_group)
    DiscreteScrollView rv_stat_group;
    @ViewById(R.id.rv_daily_stat)
    RecyclerView rv_daily_stat;
    @ViewById(R.id.rl_no_data)
    View rl_no_data;

    GroupAdapter groupAdapter;
    GroupResourcesAdapter groupResourcesAdapter;


    boolean isConnectedToRasp = false;

    @AfterViews
    public void initialize() {
        presenter.setView(TabUsageActivity.this);
    }
    @SuppressLint("SetTextI18n")
    @UiThread
    @Override
    public void showDeviceDays(int days) {
        txt_active.setText("This device was active for " + days + " days");
    }

    @Override
    public void showTotalDaysPlayedByGroups(List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds) {
        initializeGroupAdapter(modal_totalDaysGroupsPlayeds);
    }

    @UiThread
    public void initializeGroupAdapter(List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds) {
        if (groupAdapter == null) {
            groupAdapter = new GroupAdapter(TabUsageActivity.this, modal_totalDaysGroupsPlayeds, this);
            rv_stat_group.setOrientation(DSVOrientation.HORIZONTAL);
            rv_stat_group.setAdapter(groupAdapter);
            rv_stat_group.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
                @Override
                public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                    presenter.getRecourcesPlayedByGroups(groupAdapter.getItems().get(adapterPosition).getGroupID());
                }
            });
            rv_stat_group.setItemTransitionTimeMillis(150);
            rv_stat_group.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(0.9f)
                    .setMaxScale(1.05f)
                    .build());
        } else
            groupAdapter.updateItems(modal_totalDaysGroupsPlayeds);
        if (modal_totalDaysGroupsPlayeds.size() > 0) rv_daily_stat.smoothScrollToPosition(0);
        else rl_no_data.setVisibility(View.VISIBLE);
    }

    @Override
    public void showResourcesPlayedByGroups(HashMap<String, List<Modal_ResourcePlayedByGroups>> modal_resourcePlayedByGroups) {
        initializeResourcesAdapter(modal_resourcePlayedByGroups);
    }

    @UiThread
    public void initializeResourcesAdapter(HashMap<String, List<Modal_ResourcePlayedByGroups>> modal_resourcePlayedByGroups) {
        if (groupResourcesAdapter == null) {
            groupResourcesAdapter = new GroupResourcesAdapter(this, modal_resourcePlayedByGroups);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rv_daily_stat.setHasFixedSize(true);
            rv_daily_stat.setLayoutManager(linearLayoutManager);
            rv_daily_stat.setAdapter(groupResourcesAdapter);
        } else
            groupResourcesAdapter.updateData(modal_resourcePlayedByGroups);
        if (modal_resourcePlayedByGroups.size() > 0) rv_daily_stat.smoothScrollToPosition(0);
    }

    @Click(R.id.main_back)
    public void setStatBack() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void menuClicked(int position, Modal_NavigationMenu modal_navigationMenu, View view) {
        rv_stat_group.smoothScrollToPosition(position);
    }

    @Override
    public void toggleMenuIcon() {

    }
}
