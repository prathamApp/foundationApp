package com.pratham.foundation.ui.app_home.profile_new;


public interface ProfileContract {

    interface ProfileView {
//        void setCertificateCount(List<ModalTopCertificates> modalTopCertificatesList);
        void setDays(int size);
    }

    interface ProfilePresenter {
        void setView(ProfileContract.ProfileView ProfileView);

//        void getCertificateCount();

        void getActiveData();
    }
}
