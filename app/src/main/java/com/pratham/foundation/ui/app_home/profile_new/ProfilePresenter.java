package com.pratham.foundation.ui.app_home.profile_new;

import android.content.Context;
import android.util.Log;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.modalclasses.Modal_TotalDaysGroupsPlayed;
import com.pratham.foundation.modalclasses.Modal_TotalDaysStudentsPlayed;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;


@EBean
public class ProfilePresenter implements ProfileContract.ProfilePresenter, API_Content_Result {

    private final Context mContext;
    private ProfileContract.ProfileView profileView;

    public ProfilePresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(ProfileContract.ProfileView ProfileView) {
        this.profileView = ProfileView;
    }

    private ArrayList<String> codesText;

    @Background
    @Override
    public void getActiveData() {
        String studId = "" + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ?"NA"
                    :FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
        List<String> dateList = new ArrayList<>();
        if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, "").equalsIgnoreCase(FC_Constants.GROUP_MODE)) {
            List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds = new ArrayList<>();
            modal_totalDaysGroupsPlayeds = AppDatabase.getDatabaseInstance(mContext).getScoreDao().getTotalDaysGroupsPlayed();
            Log.d("getActiveGroups: ", modal_totalDaysGroupsPlayeds.size() + "   $$$$$$");
/*
            for (int i = 0; i < modal_totalDaysGroupsPlayeds.size(); i++) {
                if (i == 0)
                    dateList.add("" + modal_totalDaysGroupsPlayeds.get(i).dates);
                else {
                    if (!dateList.contains(modal_totalDaysGroupsPlayeds.get(i).dates))
                        dateList.add(modal_totalDaysGroupsPlayeds.get(i).dates);
                }
            }
*/
            Log.d("getActiveData: ", "2 : " + dateList.size());
            boolean foundEntry = false;
            for (int i = 0; i < modal_totalDaysGroupsPlayeds.size(); i++) {
                if (modal_totalDaysGroupsPlayeds.get(i).getGroupID().equalsIgnoreCase(studId)) {
                    foundEntry = true;
                    profileView.setDays(Integer.parseInt(modal_totalDaysGroupsPlayeds.get(i).getDates()));
                }
            }
            if(!foundEntry)
                profileView.setDays(0);

        } else {
            List<Modal_TotalDaysStudentsPlayed> modal_totalDaysStudentsPlayeds1 =
                    AppDatabase.getDatabaseInstance(mContext).getScoreDao().getTotalDaysByStudentID2(studId);
            for (int i = 0; i < modal_totalDaysStudentsPlayeds1.size(); i++) {
                if (i == 0)
                    dateList.add("" + modal_totalDaysStudentsPlayeds1.get(i).dates);
                else {
                    if (!dateList.contains(modal_totalDaysStudentsPlayeds1.get(i).dates))
                        dateList.add(modal_totalDaysStudentsPlayeds1.get(i).dates);
                }
            }
            Log.d("getActiveData: ", "2 : " + dateList.size());
            profileView.setDays(dateList.size());
        }
//        Log.d("getActiveData: ", "2 : "+modal_totalDaysStudentsPlayeds2.size());
//        tabUsageView.showTotalDaysPlayedByGroups(modal_totalDaysGroupsPlayeds);
    }


/*
    @Override
    public void getCertificateCount() {
        List<Assessment> assessmentList;
        List<ModalTopCertificates> modalTopCertificatesList = new ArrayList<>();
        String currStuID = ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ?"NA"
                    :FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "");
        if (FastSave.getInstance().getString(LOGIN_MODE,"").equalsIgnoreCase(FC_Constants.GROUP_MODE))
            assessmentList = AppDatabase.getDatabaseInstance(mContext).getAssessmentDao()
                    .getCertificatesGroups(currStuID, FC_Constants.CERTIFICATE_LBL);
        else
            assessmentList = AppDatabase.getDatabaseInstance(mContext).getAssessmentDao()
                    .getCertificates(currStuID, FC_Constants.CERTIFICATE_LBL);

        for (int i = 0; i < assessmentList.size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject(assessmentList.get(i).getResourceIDa());
                Iterator<String> iter = jsonObject.keys();
                codesText = new ArrayList<>();
                codesText.clear();
                int j=0;
                float totPerc=0f;
                while (iter.hasNext()) {
                    j++;
                    String key = iter.next();
                    float a = Float.parseFloat(jsonObject.getString(key));
                    if(a>=0)
                        totPerc += a;
                }
                float fullPerc = 0f;
                if(totPerc>0) {
                    fullPerc = (totPerc / j);
                    ModalTopCertificates modalTopCertificates = new ModalTopCertificates();
                    modalTopCertificates.setCertiName(FC_Utility.getSubjectNameFromNum(assessmentList.get(i).getQuestionIda()));
                    modalTopCertificates.setTotalPerc("" + fullPerc);
                    modalTopCertificatesList.add(modalTopCertificates);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(modalTopCertificatesList.size()>0)
            profileView.setCertificateCount(modalTopCertificatesList);
    }
*/

    @Override
    public void receivedContent_PI_SubLevel(String header, String response, int pos, int size) { }

    @Override
    public void receivedContent(String header, String response) { }

    @Override
    public void receivedError(String header) { }
}