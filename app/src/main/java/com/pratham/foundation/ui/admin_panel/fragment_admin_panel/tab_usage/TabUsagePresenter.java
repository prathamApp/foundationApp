package com.pratham.foundation.ui.admin_panel.fragment_admin_panel.tab_usage;

import android.content.Context;
import android.util.Log;

import com.pratham.foundation.modalclasses.Modal_ResourcePlayedByGroups;
import com.pratham.foundation.modalclasses.Modal_TotalDaysGroupsPlayed;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.pratham.foundation.database.AppDatabase.appDatabase;

/**
 * Created by Anki on 12/10/2018.
 */

@EBean
public class TabUsagePresenter implements TabUsageContract.TabUsagePresenter {

    Context context;
    TabUsageContract.TabUsageView tabUsageView;

    public TabUsagePresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(TabUsageContract.TabUsageView tabUsageView) {
        this.tabUsageView = tabUsageView;
        getDeviceActiveDays();
        getActiveGroups();
    }

    @Background
    public void getActiveGroups() {
        List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds = appDatabase.getScoreDao().getTotalDaysGroupsPlayed();
        Log.d("getActiveGroups: ", modal_totalDaysGroupsPlayeds.size() + "");
        tabUsageView.showTotalDaysPlayedByGroups(modal_totalDaysGroupsPlayeds);
    }

    @Background
    @Override
    public void getRecourcesPlayedByGroups(String groupId) {
        List<Modal_ResourcePlayedByGroups> modal_resourcePlayedByGroups = appDatabase.getScoreDao().getRecourcesPlayedByGroups(groupId);
        Log.d("getActiveGroups: ", modal_resourcePlayedByGroups.size() + "");
        HashMap<String, List<Modal_ResourcePlayedByGroups>> map = new HashMap<>();
        for (Modal_ResourcePlayedByGroups gr : modal_resourcePlayedByGroups) {
            if (map.containsKey(gr.getDates())) {
                map.get(gr.getDates()).add(gr);
            } else {
                List<Modal_ResourcePlayedByGroups> res = new ArrayList<>();
                res.add(gr);
                map.put(gr.getDates(), res);
            }
        }
        if (map.size() > 0) tabUsageView.showResourcesPlayedByGroups(map);
    }

    @Background
    @Override
    public void getDeviceActiveDays() {
/*        int days = appDatabase.getScoreDao().getTotalActiveDeviceDays();
        tabUsageView.showDeviceDays(days);*/
    }}
