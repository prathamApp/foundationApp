package com.pratham.foundation.ui.student_profile;

import android.content.Context;


import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.utility.FC_Constants;

public class Student_profile_presenterImpl implements Student_profile_contract.Student_profile_presenter {
    Context mContext;
    Student_profile_contract.Student_profile_view student_profile_view;

    public Student_profile_presenterImpl(Context mContext) {
        this.mContext = mContext;
        student_profile_view = (Student_profile_contract.Student_profile_view) mContext;
    }

    @Override
    public String getStudentProfileName() {
        String profileName;
        if (!FC_Constants.GROUP_LOGIN)
            profileName = AppDatabase.getDatabaseInstance(mContext).getStudentDao().getFullName(FC_Constants.currentStudentID);
        else
            profileName = AppDatabase.getDatabaseInstance(mContext).getGroupsDao().getGroupNameByGrpID(FC_Constants.currentStudentID);
        return profileName;
    }

    @Override
    public String getStudentProfileImage() {
        String sImage;
        if (!FC_Constants.GROUP_LOGIN)
            sImage = AppDatabase.getDatabaseInstance(mContext).getStudentDao().getStudentAvatar(FC_Constants.currentStudentID);
        else
            sImage = "group_icon";
        return sImage;
    }

   /* @Override
    public void calculateStudentProgress() {
        int totalEntries = AppDatabase.getDatabaseInstance(mContext).getEnglishWordDao().getTotalEntries();
        int totalSentence = AppDatabase.getDatabaseInstance(mContext).getEnglishWordDao().getSentenceCount();
        int totalWords = totalEntries - totalSentence;

        int learntSentence = AppDatabase.getDatabaseInstance(mContext).getKeyWordDao().getLearntSentenceCount(FC_Constants.currentStudentID);
        int sentenceProgress;
        if (totalSentence == 0) {
            sentenceProgress = 0;
        } else {
            sentenceProgress = (learntSentence / totalSentence) * 100;
        }

        int learntWords = AppDatabase.getDatabaseInstance(mContext).getKeyWordDao().getLearntWordCount(FC_Constants.currentStudentID);
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
