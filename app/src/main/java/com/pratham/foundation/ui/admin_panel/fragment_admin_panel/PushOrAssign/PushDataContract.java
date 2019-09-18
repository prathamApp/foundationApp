package com.pratham.foundation.ui.admin_panel.fragment_admin_panel.PushOrAssign;

/**
 * Created by Anki on 12/10/2018.
 */

public interface PushDataContract {
    interface PushDataPresenter {
        void createJsonForTransfer();
    }

    interface PushDataView {
        void finishActivity();
        void showDialog(boolean statusFlg);

        void showLoaderDialog();

        void dismissLoaderDialog();
    }
}
