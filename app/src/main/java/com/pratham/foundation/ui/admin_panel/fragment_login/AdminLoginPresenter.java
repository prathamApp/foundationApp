package com.pratham.foundation.ui.admin_panel.fragment_login;

import android.content.Context;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

/**
 * Created by PEF on 19/11/2018.
 */

@EBean
public class AdminLoginPresenter implements AdminLoginContract.AdminLoginPresenter {
    AdminLoginContract.AdminLoginView adminLoginView;
    Context context;

    public AdminLoginPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(AdminLoginContract.AdminLoginView adminLoginView) {
        this.adminLoginView = adminLoginView;
    }

    @Background
    @Override
    public void checkLogin(String userName, String password) {

//        if (userName.equals("a") && password.equals("a"))
        if (userName.equals("Pratham1234") && password.equals("Pratham@1234"))
            adminLoginView.openPullDataFragment();
        else
            adminLoginView.onLoginFail();
    }

    @Background
    @Override
    public void clearData() {
        FastSave.getInstance().saveBoolean(FC_Constants.PRATHAM_LOGIN, false);
        AppDatabase.getDatabaseInstance(context).getVillageDao().deleteAllVillages();
        AppDatabase.getDatabaseInstance(context).getGroupsDao().deleteAllGroups();
        AppDatabase.getDatabaseInstance(context).getStudentDao().deletePrathamAll();
        AppDatabase.getDatabaseInstance(context).getCrlDao().deleteAll();
        adminLoginView.onDataClearToast();
    }
}
