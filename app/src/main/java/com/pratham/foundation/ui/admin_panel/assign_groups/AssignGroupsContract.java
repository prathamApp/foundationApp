package com.pratham.foundation.ui.admin_panel.assign_groups;




import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.Village;

import java.util.List;

public interface AssignGroupsContract {

    interface AssignGroupsView {
        void showStateSpinner(List<String> statesList);

        void populateBlock(List<String> blocksList);

        void showProgramwiseSpinners(String programID);

        void populateVillage(List<Village> blocksVillagesList);

        void populateGroups(List<Groups> dbgroupList);

        void populateRIVillage(int vilID);

        void groupAssignSuccess();
    }

    interface AssignGroupsPresenter {
        void setView(AssignGroupsContract.AssignGroupsView assignGroupsView);

        void initializeStatesSpinner();

        void getBlockData(String selectedState);

        void getProgramWiseSpinners();

        void fetchVillageData(String selectedBlock);

        void getAllGroups(int vilID);

        void fetchRIVillage(String selectedBlock);

        void updateDBData(String group1, String group2, String group3, String group4, String group5, int vilID);
    }
}
