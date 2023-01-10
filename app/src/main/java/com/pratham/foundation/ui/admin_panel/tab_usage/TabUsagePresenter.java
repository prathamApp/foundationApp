package com.pratham.foundation.ui.admin_panel.tab_usage;

import android.content.Context;
import android.util.Log;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.modalclasses.Modal_ResourcePlayedByGroups;
import com.pratham.foundation.modalclasses.Modal_TotalDaysGroupsPlayed;
import com.pratham.foundation.modalclasses.Modal_TotalDaysStudentsPlayed;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds = new ArrayList<>();
        if(FastSave.getInstance().getString(FC_Constants.LOGIN_MODE,"").equalsIgnoreCase(FC_Constants.GROUP_MODE)) {
            modal_totalDaysGroupsPlayeds = AppDatabase.getDatabaseInstance(context).getScoreDao().getTotalDaysGroupsPlayed();
            Log.d("getActiveGroups: ", modal_totalDaysGroupsPlayeds.size() + "");
            tabUsageView.showTotalDaysPlayedByGroups(modal_totalDaysGroupsPlayeds);
        }else {
            List<Modal_TotalDaysStudentsPlayed> modal_totalDaysStudentsPlayeds = AppDatabase.getDatabaseInstance(context).getScoreDao().getTotalDaysStudentPlayed(
                    "" + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ?"NA"
                    :FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "")));
            Log.d("getActiveGroups: ", modal_totalDaysStudentsPlayeds.size() + "");
            for(int i =0; i<modal_totalDaysStudentsPlayeds.size();i++){
                Modal_TotalDaysGroupsPlayed modal_totalDaysGroupsPlayed = new Modal_TotalDaysGroupsPlayed();
                modal_totalDaysGroupsPlayed.setDates(""+modal_totalDaysStudentsPlayeds.get(i).getDates());
                modal_totalDaysGroupsPlayed.setGroupID(""+modal_totalDaysStudentsPlayeds.get(i).getStudentID());
                modal_totalDaysGroupsPlayed.setGroupName(""+modal_totalDaysStudentsPlayeds.get(i).getFullName());
                modal_totalDaysGroupsPlayeds.add(modal_totalDaysGroupsPlayed);
            }
            tabUsageView.showTotalDaysPlayedByGroups(modal_totalDaysGroupsPlayeds);
        }
    }

    @Background
    @Override
    public void getRecourcesPlayedByGroups(String groupId) {

        if(FastSave.getInstance().getString(FC_Constants.LOGIN_MODE,"").equalsIgnoreCase(FC_Constants.GROUP_MODE)) {
            List<Modal_ResourcePlayedByGroups> modal_resourcePlayedByGroups = AppDatabase.getDatabaseInstance(context)
                    .getScoreDao().getRecourcesPlayedByGroups(groupId);
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
        List<Modal_ResourcePlayedByGroups> modal_resourcePlayedByStudents = AppDatabase.getDatabaseInstance(context)
                .getScoreDao().getTotalDaysByStudentID(groupId);
        Log.d("getActiveGroups: ", modal_resourcePlayedByStudents.size() + "");


        HashMap<String, List<Modal_ResourcePlayedByGroups>> map = new HashMap<>();
        for (Modal_ResourcePlayedByGroups gr : modal_resourcePlayedByStudents) {
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
        int days = AppDatabase.getDatabaseInstance(context).getScoreDao().getTotalActiveDeviceDays();
        tabUsageView.showDeviceDays(days);
    }}
