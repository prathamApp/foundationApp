package com.pratham.foundation.ui.app_home.profile_new;


import com.pratham.foundation.modalclasses.ModalTopCertificates;

import java.util.List;

public interface ProfileContract {

    interface ProfileItemClicked {
        void itemClicked(String usage);
    }

    interface ProfileView {
        void setCertificateCount(List<ModalTopCertificates> modalTopCertificatesList);

        void setDays(int size);
    }

    interface ProfilePresenter {
        void setView(ProfileContract.ProfileView ProfileView);

        void getCertificateCount();

        void getActiveData();
    }
}
