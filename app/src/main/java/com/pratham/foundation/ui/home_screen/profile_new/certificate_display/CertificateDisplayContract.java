package com.pratham.foundation.ui.home_screen.profile_new.certificate_display;


public interface CertificateDisplayContract {

    interface CertificateItemClicked {
    }

    interface CertificateView {
    }

    interface CertificatePresenter {
        void setView(CertificateDisplayContract.CertificateView certificateView);
    }
}
