package com.pratham.foundation.ui.app_home.profile_new;

import android.content.Context;
import android.util.Log;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.modalclasses.ModalTopCertificates;
import com.pratham.foundation.modalclasses.Modal_TotalDaysStudentsPlayed;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.LOGIN_MODE;


@EBean
public class ProfilePresenter implements ProfileContract.ProfilePresenter, API_Content_Result {

    private Context mContext;
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
        String studId = ""+FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID,"");
        List<Modal_TotalDaysStudentsPlayed> modal_totalDaysStudentsPlayeds1 =
                AppDatabase.getDatabaseInstance(mContext).getScoreDao().getTotalDaysByStudentID(studId);
//        List<Modal_TotalDaysStudentsPlayed> modal_totalDaysStudentsPlayeds2 = appDatabase.getScoreDao().getTotalDaysStudentPlayed();
        Log.d("getActiveData: ", "1 : "+modal_totalDaysStudentsPlayeds1.size());
        List <String> dateList = new ArrayList<>();
        for(int i =0 ; i<modal_totalDaysStudentsPlayeds1.size(); i++){
            if(i==0)
                dateList.add(""+ modal_totalDaysStudentsPlayeds1.get(i).dates);
            else{
                if(!dateList.contains(modal_totalDaysStudentsPlayeds1.get(i).dates))
                    dateList.add(modal_totalDaysStudentsPlayeds1.get(i).dates);
            }
        }
        Log.d("getActiveData: ", "2 : "+dateList.size());
        profileView.setDays(dateList.size());
//        Log.d("getActiveData: ", "2 : "+modal_totalDaysStudentsPlayeds2.size());
//        tabUsageView.showTotalDaysPlayedByGroups(modal_totalDaysGroupsPlayeds);
    }


    @Override
    public void getCertificateCount() {
        List<Assessment> assessmentList;
        List<ModalTopCertificates> modalTopCertificatesList = new ArrayList<>();
        String currStuID = FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "");
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

    @Override
    public void receivedContent(String header, String response) {

    }

    @Override
    public void receivedError(String header) {

    }
}