package com.pratham.foundation.ui.admin_panel;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.ui.admin_panel.fragment_login.AdminLoginFragment;
import com.pratham.foundation.ui.admin_panel.fragment_login.AdminLoginFragment_;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.greenrobot.eventbus.EventBus;


//@EActivity(R.layout.activity_admin_login)
@EActivity(R.layout.activity_admin_controls)
public class AdminConsoleActivityNew extends BaseActivity {

    @AfterViews
    public void initialize() {
        FC_Utility.showFragment(this, new AdminLoginFragment_(), R.id.frame_attendance,
                null, AdminLoginFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            EventMessage message = new EventMessage();
            message.setMessage("reload");
            EventBus.getDefault().post(message);
//            startActivity(new Intent(this, MenuActivity_.class));
            finish();
        }
    }

/*    @Override
    public void onResponseGet() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FC_Utility.showFragment(this, new AdminLoginFragment_(), R.id.frame_attendance,
                null, AdminLoginFragment.class.getSimpleName());
    }*/
}