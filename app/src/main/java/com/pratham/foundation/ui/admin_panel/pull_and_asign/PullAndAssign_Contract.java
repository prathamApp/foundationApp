package com.pratham.foundation.ui.admin_panel.pull_and_asign;




import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.ModalProgram;
import com.pratham.foundation.database.domain.ModalStates;
import com.pratham.foundation.database.domain.Village;

import java.util.ArrayList;
import java.util.List;

public interface PullAndAssign_Contract {

    interface PullAndAssignView {
        void showNoConnectivity();

        void showProgram(List<ModalProgram> prgrmList);

        void closeProgressDialog();

        void showStatesSpinner(List<ModalStates> modalStates);

        void showErrorToast();

        void showProgressDialog(String loading_blocks);

        void showBlocksSpinner(List<String> blockList);

        void showVillageDialog(List villageName);

        void showVillageSpinner(List<Village> blocksVillagesList);

        void populateGroups(List<Groups> dbgroupList);

        void groupAssignSuccess();
    }

    interface PullAndAssignPresenter {
        void setView(PullAndAssign_Contract.PullAndAssignView pullAndAssignView);

        void checkConnectivity();

        void clearLists();

        void loadStateSpinner(String selectedProgram);

        void loadBlockSpinner(int pos, String selectedProgram);

        void proccessVillageData(String block);

        void downloadStudentAndGroup(ArrayList<String> villageIDList);

        void getAllGroups(int vilID);

        void updateDBData(String group1, String group2, String group3, String group4, String group5, int vilID);
    }

    interface VillageSelectListener {
        void getSelectedItems(ArrayList<String> villageIDList);
    }

}
