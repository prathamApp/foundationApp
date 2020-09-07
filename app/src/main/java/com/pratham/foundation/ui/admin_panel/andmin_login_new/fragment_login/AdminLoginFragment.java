package com.pratham.foundation.ui.admin_panel.andmin_login_new.fragment_login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.pratham.foundation.R;
import com.pratham.foundation.async.PushDataToServer_New;
import com.pratham.foundation.ui.admin_panel.andmin_login_new.new_menu.New_Menu_Fragment;
import com.pratham.foundation.ui.admin_panel.andmin_login_new.new_menu.New_Menu_Fragment_;
import com.pratham.foundation.ui.admin_panel.fragment_admin_panel.PushOrAssign.PushOrAssignFragment;
import com.pratham.foundation.ui.admin_panel.fragment_admin_panel.PushOrAssign.PushOrAssignFragment_;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Objects;


/**
 * Created by PEF on 19/11/2018.
 */

@EFragment(R.layout.admin_panel_login)
public class AdminLoginFragment extends Fragment implements AdminLoginContract.AdminLoginView {

    @Bean(AdminLoginPresenter.class)
    AdminLoginContract.AdminLoginPresenter presenter;

    @ViewById(R.id.userName)
    TextInputEditText userNameET;

    @ViewById(R.id.password)
    TextInputEditText passwordET;

    @AfterViews
    public void initialize() {
        presenter.setView(AdminLoginFragment.this);
//        userNameET.setText("");
//        passwordET.setText("");
        userNameET.setText("Pratham1234");
        passwordET.setText("Pratham@1234");
/*          userNameET.setText("pravinthorat");
        passwordET.setText("pratham123");*/
    }

    @Click(R.id.btn_login)
    public void loginCheck() {
        final InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(Objects.requireNonNull(getView()).getWindowToken(), 0);
        presenter.checkLogin(getUserName(), getPassword());
        Objects.requireNonNull(userNameET.getText()).clear();
        Objects.requireNonNull(passwordET.getText()).clear();
    }

    @Bean(PushDataToServer_New.class)
    PushDataToServer_New pushDataToServer;

    @Override
    public String getUserName() {
        String userName = Objects.requireNonNull(userNameET.getText()).toString();
        return userName.trim();
    }

    @Override
    public String getPassword() {
        String password = Objects.requireNonNull(passwordET.getText()).toString();
        return password.trim();
    }

    @UiThread
    @Override
    public void openPullDataFragment() {
        FC_Utility.showFragment(getActivity(), new New_Menu_Fragment_(), R.id.frame_attendance,
                null, New_Menu_Fragment.class.getSimpleName());
//        FC_Utility.showFragment(getActivity(), new PullAndAssign_Fragment_(), R.id.frame_attendance,
//                null, PullDataFragment.class.getSimpleName());
    }

    @UiThread
    @Override
    public void onLoginFail() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Invalid Credentials");
        alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
        alertDialog.setButton("OK", (dialog, which) -> {
            userNameET.setText("");
            passwordET.setText("");
            userNameET.requestFocus();
        });
        alertDialog.show();
    }

    @UiThread
    @Override
    public void onLoginSuccess() {
        FC_Utility.showFragment(getActivity(), new PushOrAssignFragment_(), R.id.frame_attendance,
                null, PushOrAssignFragment.class.getSimpleName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Click(R.id.btn_back)
    public void pressedBackButton() {
        Objects.requireNonNull(getActivity()).onBackPressed();
    }


    @UiThread
    @Override
    public void onDataClearToast() {
        userNameET.setText("");
        passwordET.setText("");
        Toast.makeText(getActivity(), "Data cleared Successfully", Toast.LENGTH_SHORT).show();
    }
}
