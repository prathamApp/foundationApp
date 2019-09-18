package com.pratham.foundation.ui.test.certificate;


import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.modalclasses.CertificateModelClass;

import org.json.JSONArray;
import org.json.JSONObject;

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

        void setSupervisorData(String sName, String sImage);
    }

    interface CertificatePresenter {
        void getStudentName(String certiMode);

        void proceed(JSONArray certiData, String nodeId);

        void getSupervisorData(String certiMode);

        void fillAdapter(Assessment assessmentProfile, JSONArray certiData);

        JSONArray fetchAssessmentList(String level);

        float getStarRating(float perc);

        void recordTestData(JSONObject jsonObjectAssessment, String certiTitle);
    }

}
