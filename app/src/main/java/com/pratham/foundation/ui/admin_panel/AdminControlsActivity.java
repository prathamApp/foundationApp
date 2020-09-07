package com.pratham.foundation.ui.admin_panel;

import android.content.Intent;

import androidx.fragment.app.FragmentManager;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.interfaces.DataPushListener;
import com.pratham.foundation.ui.admin_panel.fragment_admin_panel.AdminPanelFragment;
import com.pratham.foundation.ui.admin_panel.fragment_admin_panel.AdminPanelFragment_;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_admin_controls)
public class AdminControlsActivity extends BaseActivity implements DataPushListener {

    @AfterViews
    public void initialize() {
        FC_Utility.showFragment(this, new AdminPanelFragment_(), R.id.frame_attendance,
                null, AdminPanelFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            startActivity(new Intent(this, MenuActivity_.class));
            finish();
        }
    }

    @Override
    public void onResponseGet() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FC_Utility.showFragment(this, new AdminPanelFragment_(), R.id.frame_attendance,
                null, AdminPanelFragment.class.getSimpleName());

    }
}
