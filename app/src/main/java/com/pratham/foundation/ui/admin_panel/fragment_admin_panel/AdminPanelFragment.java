package com.pratham.foundation.ui.admin_panel.fragment_admin_panel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.pratham.foundation.R;
import com.pratham.foundation.ui.admin_panel.PullData.PullDataFragment;
import com.pratham.foundation.ui.admin_panel.fragment_admin_panel.PushOrAssign.PushOrAssignFragment;
import com.pratham.foundation.ui.admin_panel.fragment_admin_panel.PushOrAssign.PushOrAssignFragment_;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;


/**
 * Created by PEF on 19/11/2018.
 */

@EFragment(R.layout.admin_panel_login)
public class AdminPanelFragment extends Fragment implements AdminPanelContract.AdminPanelView {

    @Bean(AdminPanelPresenter.class)
    AdminPanelContract.AdminPanelPresenter presenter;

    @ViewById(R.id.userName)
    android.support.design.widget.TextInputEditText userNameET;

    @ViewById(R.id.password)
    android.support.design.widget.TextInputEditText passwordET;

    @AfterViews
    public void initialize() {
        presenter.setView(AdminPanelFragment.this);
        userNameET.setText("");
        passwordET.setText("");
/*          userNameET.setText("pravinthorat");
        passwordET.setText("pratham123");*/
    }

    @Click(R.id.btn_login)
    public void loginCheck() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        presenter.checkLogin(getUserName(), getPassword());
        userNameET.getText().clear();
        passwordET.getText().clear();
    }

    @Click(R.id.btn_clearData)
    public void clearData() {
        AlertDialog clearDataDialog = new AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle("Clear Data")
                .setMessage("Are you sure you want to clear everything ?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        presenter.clearData();
                        dialog.dismiss();
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        clearDataDialog.show();
        clearDataDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);

    }

    @Override
    public String getUserName() {
        String userName = userNameET.getText().toString();
        return userName.trim();
    }

    @Override
    public String getPassword() {
        String password = passwordET.getText().toString();
        return password.trim();
    }

    @UiThread
    @Override
    public void openPullDataFragment() {
        FC_Utility.showFragment(getActivity(), new PullDataFragment(), R.id.frame_attendance,
                null, PullDataFragment.class.getSimpleName());
    }

    @UiThread
    @Override
    public void onLoginFail() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Invalid Credentials");
        alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userNameET.setText("");
                passwordET.setText("");
                userNameET.requestFocus();
            }
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
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }

    @UiThread
    @Override
    public void onDataClearToast() {
        userNameET.setText("");
        passwordET.setText("");
        Toast.makeText(getActivity(), "Data cleared Successfully", Toast.LENGTH_SHORT).show();
    }
}
