package com.pratham.foundation.ui.app_home.profile_new.certificate_display;


import com.pratham.foundation.database.domain.Assessment;

import java.util.List;

public interface CertificateDisplayContract {

    interface CertificateItemClicked {
        void gotoCertificate(Assessment assessment, String cTitle);
    }

    interface CertificateView {
        void addToAdapter(List<Assessment> assessmentList);

        void showNoData();
    }

    interface CertificatePresenter {
        void setView(CertificateDisplayContract.CertificateView certificateView);

        void showCertificates();
    }
}
