package com.pratham.foundation.ui.home_screen.profile_new;


import com.pratham.foundation.modalclasses.ModalTopCertificates;

import java.util.List;

public interface ProfileContract {

    interface ProfileItemClicked {
        void itemClicked(String usage);
    }

    interface ProfileView {
        void setCertificateCount(List<ModalTopCertificates> modalTopCertificatesList);
    }

    interface ProfilePresenter {
        void setView(ProfileContract.ProfileView ProfileView);

        void getCertificateCount();
    }
}
