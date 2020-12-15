package com.pratham.foundation.ui.admin_panel.andmin_login_new.new_menu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.fragment.app.Fragment;

import com.pratham.foundation.R;
import com.pratham.foundation.async.PushDataToServer_New;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.admin_panel.andmin_login_new.enrollmentid.AddEnrollmentId_;
import com.pratham.foundation.ui.admin_panel.andmin_login_new.pull_and_asign.PullAndAssign_Fragment;
import com.pratham.foundation.ui.admin_panel.andmin_login_new.pull_and_asign.PullAndAssign_Fragment_;
import com.pratham.foundation.ui.admin_panel.fragment_admin_panel.tab_usage.TabUsageActivity_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import java.util.Objects;


@EFragment(R.layout.fragment_new_menu)
public class New_Menu_Fragment extends Fragment {

    Context context;

    @AfterViews
    public void initialize() {
        context = getActivity();
    }

    @Click(R.id.btn_assign)
    public void onAssignClick() {
        FC_Utility.showFragment(getActivity(), new PullAndAssign_Fragment_(), R.id.frame_attendance,
                null, PullAndAssign_Fragment.class.getSimpleName());
    }

    @Bean(PushDataToServer_New.class)
    PushDataToServer_New pushDataToServer;

    @Click(R.id.btn_push)
    public void onPushClick() {
        pushDataToServer.startDataPush(getActivity(), true);
    }

    @Click(R.id.btn_Usage)
    public void viewTabUsage() {
        Intent intent = new Intent(getActivity(), TabUsageActivity_.class);
        startActivityForResult(intent, 1);
    }

    @Click(R.id.btn_Enrollment)
    public void addEnrollmentId() {
        Intent intent = new Intent(getActivity(), AddEnrollmentId_.class);
        startActivityForResult(intent, 1);
    }

    @Click(R.id.btn_clearData)
    public void clearDataClicked() {
        AlertDialog clearDataDialog = new AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle("Clear Data")
                .setMessage("Are you sure you want to clear everything ?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Delete", (dialog, whichButton) -> {
                    //your deleting code
                    clearData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
        clearDataDialog.show();
        clearDataDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);

    }

    @Background
    public void clearData() {
        FastSave.getInstance().saveBoolean(FC_Constants.PRATHAM_LOGIN, false);
        AppDatabase.getDatabaseInstance(context).getVillageDao().deleteAllVillages();
        AppDatabase.getDatabaseInstance(context).getGroupsDao().deleteAllGroups();
        AppDatabase.getDatabaseInstance(context).getStudentDao().deletePrathamAll();
        AppDatabase.getDatabaseInstance(context).getCrlDao().deleteAll();
        onDataClearToast();
    }

    @UiThread
    public void onDataClearToast() {
        AlertDialog clearDataDialog = new AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle("Data cleared Successfully")
                .setMessage("Please RESTART the application")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("OK", (dialog, whichButton) -> {
                    dialog.dismiss();
                })
                .create();
        clearDataDialog.show();
        clearDataDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    @Click(R.id.btn_back)
    public void backBtnClicked() {
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

}
