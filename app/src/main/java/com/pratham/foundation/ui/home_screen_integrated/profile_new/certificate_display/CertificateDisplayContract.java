package com.pratham.foundation.ui.home_screen_integrated.profile_new.certificate_display;


import com.pratham.foundation.database.domain.Assessment;

import java.util.List;

public interface CertificateDisplayContract {

    interface CertificateItemClicked {
        void gotoCertificate(Assessment assessment);
    }

    interface CertificateView {
        void addToAdapter(List<Assessment> assessmentList);
    }

    interface CertificatePresenter {
        void setView(CertificateDisplayContract.CertificateView certificateView);

        void showCertificates();
    }
}
