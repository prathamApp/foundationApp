package com.pratham.foundation.ui.student_profile;

import android.content.Context;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.EBean;

import static com.pratham.foundation.utility.FC_Constants.GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.INDIVIDUAL_MODE;
import static com.pratham.foundation.utility.FC_Constants.LOGIN_MODE;
import static com.pratham.foundation.utility.FC_Constants.QR_GROUP_MODE;

@EBean
public class Student_profile_presenterImpl implements Student_profile_contract.Student_profile_presenter {
    Context mContext;
    Student_profile_contract.Student_profile_view student_profile_view;

    public Student_profile_presenterImpl(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(Student_profile_contract.Student_profile_view view) {
        student_profile_view = view;
    }

    @Override
    public String getStudentProfileName() {
        String profileName = "QR Group";
        try {
            if (LOGIN_MODE.equalsIgnoreCase(GROUP_MODE))
                profileName = AppDatabase.getDatabaseInstance(mContext).getGroupsDao().getGroupNameByGrpID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            else if (!LOGIN_MODE.equalsIgnoreCase(QR_GROUP_MODE)) {
                profileName = AppDatabase.getDatabaseInstance(mContext).getStudentDao().getFullName(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            }

            if (LOGIN_MODE.equalsIgnoreCase(INDIVIDUAL_MODE))
                profileName = profileName.split(" ")[0];

        } catch (Exception e) {
            e.printStackTrace();
        }
        return profileName;
    }

    @Override
    public String getStudentProfileImage() {
        String sImage;
        if (!FC_Constants.GROUP_LOGIN)
            sImage = AppDatabase.getDatabaseInstance(mContext).getStudentDao().getStudentAvatar(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
        else
            sImage = "group_icon";
        return sImage;
    }

   /* @Override
    public void calculateStudentProgress() {
        int totalEntries = AppDatabase.getDatabaseInstance(mContext).getEnglishWordDao().getTotalEntries();
        int totalSentence = AppDatabase.getDatabaseInstance(mContext).getEnglishWordDao().getSentenceCount();
        int totalWords = totalEntries - totalSentence;

        int learntSentence = AppDatabase.getDatabaseInstance(mContext).getKeyWordDao().getLearntSentenceCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
        int sentenceProgress;
        if (totalSentence == 0) {
            sentenceProgress = 0;
        } else {
            sentenceProgress = (learntSentence / totalSentence) * 100;
        }

        int learntWords = AppDatabase.getDatabaseInstance(mContext).getKeyWordDao().getLearntWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
        int wordsProgress;
        if (totalWords == 0) {
            wordsProgress = 0;
        } else {
            wordsProgress = (learntWords / totalWords) * 100;
        }
      //  student_profile_view.displayStudentProgress(totalWords, learntWords, wordsProgress, totalSentence, learntSentence, sentenceProgress);
    }*/

    public String getTittleName(String id) {
        return "";//AppDatabase.getDatabaseInstance(mContext).getContentTableDao().getnodeTitle(id);
    }
}
