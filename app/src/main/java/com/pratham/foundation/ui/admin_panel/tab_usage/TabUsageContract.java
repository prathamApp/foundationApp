package com.pratham.foundation.ui.admin_panel.tab_usage;

import com.pratham.foundation.modalclasses.Modal_ResourcePlayedByGroups;
import com.pratham.foundation.modalclasses.Modal_TotalDaysGroupsPlayed;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Anki on 12/10/2018.
 */

public interface TabUsageContract {
    interface TabUsagePresenter {
        void setView(TabUsageContract.TabUsageView tabUsageView);

        void getDeviceActiveDays();

        void getRecourcesPlayedByGroups(String groupId);
    }

    interface TabUsageView {
        void showDeviceDays(int days);

        void showTotalDaysPlayedByGroups(List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds);

        void showResourcesPlayedByGroups(HashMap<String, List<Modal_ResourcePlayedByGroups>> modal_resourcePlayedByGroups);
    }
}
