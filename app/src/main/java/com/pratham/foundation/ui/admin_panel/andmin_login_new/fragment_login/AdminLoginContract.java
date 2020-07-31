package com.pratham.foundation.ui.admin_panel.andmin_login_new.fragment_login;

/**
 * Created by PEF on 19/11/2018.
 */

public interface AdminLoginContract {

    interface AdminLoginView {
          String getUserName();
          String getPassword();
          void openPullDataFragment();
          void onLoginFail();
          void onLoginSuccess();
          void onDataClearToast();

    }

    interface AdminLoginPresenter {
        void checkLogin(String userName, String password);
        void clearData();

        void setView(AdminLoginContract.AdminLoginView adminLoginView);
    }
}
