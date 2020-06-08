package com.pratham.foundation.ui.admin_panel.PullData;



import com.pratham.foundation.database.domain.ModalProgram;
import com.pratham.foundation.database.domain.ModalStates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PEF on 19/11/2018.
 */

public interface PullDataContract {
    interface PullDataView {
        void showStatesSpinner(List<ModalStates> Modalstates);

        void showProgressDialog(String msg);

        void shoConfermationDialog(int crlListCnt, int studentListcnt, int groupListCnt, int villageIDListCnt);

        void closeProgressDialog();

        void clearBlockSpinner();

        void clearStateSpinner();

        void showBlocksSpinner(List blocks);

        void showVillageDialog(List villageList);

        void disableSaveButton();

        void enableSaveButton();

        void showErrorToast();

        void openLoginActivity();


        void showNoConnectivity();

        void showProgram(List<ModalProgram> prgrmList);
    }

    interface PullDataPresenter {
        void loadSpinner(String selectedProgramId);

        void proccessVillageData(String respnce);

        void loadBlockSpinner(int pos, String selectedprogram);

        void downloadStudentAndGroup(ArrayList<String> villageIDList);

        void saveData();

        void clearLists();

        void onSaveClick();

        void checkConnectivity();

        void loadPrgramsSpinner();
    }
}
