package com.pratham.foundation.ui.home_screen.profile_new.certificate_display;


import com.pratham.foundation.database.domain.Assessment;

public interface CertificateDisplayContract {

    interface CertificateItemClicked {
        void gotoCertificate(Assessment assessment);
    }

    interface CertificateView {
    }

    interface CertificatePresenter {
        void setView(CertificateDisplayContract.CertificateView certificateView);
    }
}
