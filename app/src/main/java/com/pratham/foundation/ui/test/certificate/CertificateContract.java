package com.pratham.foundation.ui.test.certificate;


import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.modalclasses.CertificateModelClass;

import org.json.JSONArray;

/**
 * Created by Ameya on 23-Nov-17.
 */

public interface CertificateContract {

    interface CertificateView{
        void setStudentName(String studName);

        void addContentToViewList(CertificateModelClass contentTable);

        void doubleQuestionCheck();

        void initializeTheIndex();

        void notifyAdapter();
    }

    interface CertificatePresenter {

        void setView(CertificateView certificateView);

        void fillAdapter(Assessment assessmentProfile, String certiTitle);

        JSONArray fetchAssessmentList(String level);

        float getStarRating(float perc);

        String[] getTestData(String jsonName);
    }

}
