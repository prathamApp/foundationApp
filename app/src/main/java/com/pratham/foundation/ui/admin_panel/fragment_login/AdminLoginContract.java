package com.pratham.foundation.ui.admin_panel.fragment_login;

/**
 * Created by PEF on 19/11/2018.
 */

public interface AdminLoginContract {

    interface AdminLoginView {
          String getUserName();
          String getPassword();
          void openPullDataFragment();
          void onLoginFail();
          void onDataClearToast();

    }

    interface AdminLoginPresenter {
        void checkLogin(String userName, String password);
        void clearData();

        void setView(AdminLoginView adminLoginView);
    }
}
