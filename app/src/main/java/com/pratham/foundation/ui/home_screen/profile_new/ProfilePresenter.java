package com.pratham.foundation.ui.home_screen.profile_new;

import android.content.Context;

import com.pratham.foundation.interfaces.API_Content_Result;

import org.androidannotations.annotations.EBean;


@EBean
public class ProfilePresenter implements ProfileContract.ProfilePresenter, API_Content_Result {

    Context mContext;
    ProfileContract.ProfileView PracticeView;

    public ProfilePresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(ProfileContract.ProfileView ProfileView) {
        this.PracticeView = ProfileView;
    }

    @Override
    public void receivedContent(String header, String response) {

    }

    @Override
    public void receivedError(String header) {

    }
}