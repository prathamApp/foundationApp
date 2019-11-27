package com.pratham.foundation.ui.home_screen.profile_new;

import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import static com.pratham.foundation.utility.FC_Utility.dpToPx;


@EFragment(R.layout.fragment_profile)
public class ProfileFragment extends Fragment implements ProfileContract.ProfileView{

    @Bean(ProfilePresenter.class)
    ProfileContract.ProfilePresenter presenter;

    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;

    String[] progressArray = {"Progress", "Share"};
    private ProfileOuterDataAdapter adapterParent;

    @AfterViews
    public void initialize() {
        presenter.setView(ProfileFragment.this);
        if (adapterParent == null) {
            adapterParent = new ProfileOuterDataAdapter(getActivity(), progressArray);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
            my_recycler_view.setLayoutManager(mLayoutManager);
            my_recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(getActivity()), true));
            my_recycler_view.setItemAnimator(new DefaultItemAnimator());
            my_recycler_view.setAdapter(adapterParent);
        }
    }
}