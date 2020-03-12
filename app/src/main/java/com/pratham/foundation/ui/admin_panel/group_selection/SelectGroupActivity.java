package com.pratham.foundation.ui.admin_panel.group_selection;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.ui.admin_panel.group_selection.fragment_select_group.FragmentSelectGroup;
import com.pratham.foundation.ui.admin_panel.group_selection.fragment_select_group.FragmentSelectGroup_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_select_group)
public class SelectGroupActivity extends BaseActivity {

    @ViewById(R.id.main_layout)
    RelativeLayout main_layout;
    @ViewById(R.id.frame_group)
    FrameLayout frame_group;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Bundle bundle = new Bundle();
        bundle.putBoolean(FC_Constants.GROUP_AGE_ABOVE_7, true);
        FC_Utility.showFragment(this, new FragmentSelectGroup_(), R.id.frame_group,
                bundle, FragmentSelectGroup.class.getSimpleName());
//        FC_Utility.showFragment(this, new MenuFragment_(), R.id.frame_group,
//                null, MenuFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
//            showExitDialog();
        } else {
            if (fragments > 1) {
                // FragmentManager.BackStackEntry first = getSupportFragmentManager().getBackStackEntryAt(0);
                getSupportFragmentManager().popBackStack(0,0);
            } else {
                super.onBackPressed();
            }
        }
    }
}