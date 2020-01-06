package com.pratham.foundation.ui.home_screen;

import android.content.Context;

import org.androidannotations.annotations.EBean;

@EBean
public class HomePresenter implements HomeContract.HomePresenter{

    Context mContext;
    HomeContract.HomeView homeView;
    public HomePresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(HomeContract.HomeView homeView) {
        this.homeView = homeView;
    }

}