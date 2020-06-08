package com.pratham.foundation.ui.admin_panel.fragment_admin_panel;

import android.content.Context;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Crl;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

/**
 * Created by PEF on 19/11/2018.
 */

@EBean
public class AdminPanelPresenter implements AdminPanelContract.AdminPanelPresenter {
    AdminPanelContract.AdminPanelView adminPanelView;
    Context context;

    public AdminPanelPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(AdminPanelContract.AdminPanelView adminPanelView) {
        this.adminPanelView = adminPanelView;
    }

    @Background
    @Override
    public void checkLogin(String userName, String password) {

        if (userName.equals("admin") && password.equals("admin")) {
            adminPanelView.openPullDataFragment();
        } else {
            // assign push logic
            Crl loggedCrl = AppDatabase.getDatabaseInstance(context).getCrlDao().checkUserValidation(userName, password);
            if (loggedCrl != null) {
                adminPanelView.onLoginSuccess();
            } else {
                //userNAme and password may be wrong
                adminPanelView.onLoginFail();
            }
        }
    }

    @Background
    @Override
    public void clearData() {
        AppDatabase.getDatabaseInstance(context).getVillageDao().deleteAllVillages();
        AppDatabase.getDatabaseInstance(context).getGroupsDao().deleteAllGroups();
        AppDatabase.getDatabaseInstance(context).getStudentDao().deleteAll();
        AppDatabase.getDatabaseInstance(context).getCrlDao().deleteAll();
        adminPanelView.onDataClearToast();
    }
}
