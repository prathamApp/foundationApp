package com.pratham.foundation.ui.admin_panel.assign_groups;

import android.content.Context;
import android.provider.Settings;


import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.Village;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.List;

@EBean
public class AssignGroupsPresenter implements AssignGroupsContract.AssignGroupsPresenter{

    Context mContext;
    AssignGroupsContract.AssignGroupsView assignGroupsView;

    public AssignGroupsPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(AssignGroupsContract.AssignGroupsView assignGroupsView) {
        this.assignGroupsView = assignGroupsView;
    }

    @Background
    @Override
    public void initializeStatesSpinner() {
        try {
            List<String> statesList = AppDatabase.appDatabase.getVillageDao().getAllStates();
            assignGroupsView.showStateSpinner(statesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void getProgramWiseSpinners() {
        try {
            String programID = AppDatabase.appDatabase.getStatusDao().getValue("programId");
            assignGroupsView.showProgramwiseSpinners(programID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void getBlockData(String selectedState) {
        try {
            List<String> blocksList =  AppDatabase.appDatabase.getVillageDao().GetStatewiseBlock(selectedState);
            assignGroupsView.populateBlock(blocksList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void fetchVillageData(String selectedBlock) {
        try {
            List<Village> blocksVillagesList = AppDatabase.appDatabase.getVillageDao().GetVillages(selectedBlock);
            assignGroupsView.populateVillage(blocksVillagesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void getAllGroups(int vilID) {
        try {
            List<Groups> dbgroupList = AppDatabase.appDatabase.getGroupsDao().GetGroups(vilID);
            assignGroupsView.populateGroups(dbgroupList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void fetchRIVillage(String selectedBlock) {
        try {
            int vilID = AppDatabase.appDatabase.getVillageDao().GetVillageIDByBlock(selectedBlock);
            assignGroupsView.populateRIVillage(vilID);
            getAllGroups(vilID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void updateDBData(String group1, String group2, String group3, String group4, String group5, int vilID) {
        try {
            AppDatabase.appDatabase.getStudentDao().deleteDeletedStdRecords();
            AppDatabase.appDatabase.getStatusDao().updateValue(FC_Constants.GROUPID1, group1);
            AppDatabase.appDatabase.getStatusDao().updateValue(FC_Constants.GROUPID2, group2);
            AppDatabase.appDatabase.getStatusDao().updateValue(FC_Constants.GROUPID3, group3);
            AppDatabase.appDatabase.getStatusDao().updateValue(FC_Constants.GROUPID4, group4);
            AppDatabase.appDatabase.getStatusDao().updateValue(FC_Constants.GROUPID5, group5);

            AppDatabase.appDatabase.getStatusDao().updateValue("village", Integer.toString(vilID));
            AppDatabase.appDatabase.getStatusDao().updateValue("DeviceId", "" + Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
            AppDatabase.appDatabase.getStatusDao().updateValue("ActivatedDate", FC_Utility.getCurrentDateTime());
            AppDatabase.appDatabase.getStatusDao().updateValue("ActivatedForGroups", group1 + "," + group2 + "," + group3 + "," + group4 + "," + group5);

            assignGroupsView.groupAssignSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}