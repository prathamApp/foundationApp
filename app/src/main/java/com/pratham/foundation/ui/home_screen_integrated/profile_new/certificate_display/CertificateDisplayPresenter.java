package com.pratham.foundation.ui.home_screen_integrated.profile_new.certificate_display;

import android.content.Context;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.GROUP_LOGIN;

@EBean
public class CertificateDisplayPresenter implements CertificateDisplayContract.CertificatePresenter, API_Content_Result {

    Context mContext;
    CertificateDisplayContract.CertificateView certificateView;

    public CertificateDisplayPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(CertificateDisplayContract.CertificateView certificateView) {
        this.certificateView = certificateView;
    }

    @Background
    @Override
    public void showCertificates() {
        List<Assessment> assessmentList;
        if(GROUP_LOGIN)
            assessmentList = AppDatabase.getDatabaseInstance(mContext).getAssessmentDao()
                    .getCertificatesGroups(FC_Constants.currentGroup, FC_Constants.CERTIFICATE_LBL);
        else
            assessmentList = AppDatabase.getDatabaseInstance(mContext).getAssessmentDao()
                    .getCertificates(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), FC_Constants.CERTIFICATE_LBL);

        certificateView.addToAdapter(assessmentList);
    }

    @Override
    public void receivedContent(String header, String response) {

    }

    @Override
    public void receivedError(String header) {

    }
}