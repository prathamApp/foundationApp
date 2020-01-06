package com.pratham.foundation.ui.home_screen;


public interface HomeContract {

    interface HomeView {

    }

    interface HomePresenter {

        void setView(HomeContract.HomeView homeView);

    }
}
