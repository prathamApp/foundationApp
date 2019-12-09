package com.pratham.foundation.ui.home_screen.profile_new;


public interface ProfileContract {

    interface ProfileItemClicked {
        void itemClicked(String usage);
    }

    interface ProfileView {
    }

    interface ProfilePresenter {
        void setView(ProfileContract.ProfileView ProfileView);
    }
}
