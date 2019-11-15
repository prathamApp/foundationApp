package com.pratham.foundation.ui.admin_panel.group_selection;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pratham.foundation.R;
import com.pratham.foundation.ui.admin_panel.group_selection.fragment_select_group.FragmentSelectGroup;
import com.pratham.foundation.ui.admin_panel.group_selection.fragment_select_group.FragmentSelectGroup_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import java.util.Objects;


@EFragment(R.layout.fragment_menu)
public class MenuFragment extends Fragment {

    @AfterViews
    public void initialize() { }

    @Click({R.id.below_7})
    public void gotoQRActivity() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(FC_Constants.GROUP_AGE_BELOW_7, true);
        FC_Utility.showFragment(getActivity(), new FragmentSelectGroup_(), R.id.frame_group,
                bundle, FragmentSelectGroup.class.getSimpleName());
    }

    @Click({R.id.above_7})
    public void gotoGroupLogin() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(FC_Constants.GROUP_AGE_ABOVE_7, true);
        FC_Utility.showFragment(getActivity(), new FragmentSelectGroup_(), R.id.frame_group,
                bundle, FragmentSelectGroup.class.getSimpleName());
    }

    @Click(R.id.btn_back)
    public void pressedBackButton() {
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

}