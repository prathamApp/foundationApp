package com.pratham.foundation.ui.home_screen.profile_new.certificate_display;

import android.content.Context;

import com.pratham.foundation.interfaces.API_Content_Result;

import org.androidannotations.annotations.EBean;

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

    @Override
    public void receivedContent(String header, String response) {

    }

    @Override
    public void receivedError(String header) {

    }
}