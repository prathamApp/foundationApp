package com.pratham.foundation.ui.home_screen.profile_new;


public interface ProfileContract {

    interface ProfileItemClicked {
    }

    interface ProfileView {
    }

    interface ProfilePresenter {
        void setView(ProfileContract.ProfileView ProfileView);
    }
}
