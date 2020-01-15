package com.pratham.foundation.ui.contentPlayer.doing;

import com.pratham.foundation.modalclasses.ScienceQuestion;

public interface DoingFragmentContract {
    interface DoingFragmentView {
        void setVideoQuestion(ScienceQuestion scienceQuestion);
    }

    interface DoingFragmentPresenter {
        void setView(DoingFragmentContract.DoingFragmentView doingFragmentView, String jsonName, String resId,String contentTitle);

        void getData(String readingContentPath);

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label,String resId, boolean addInAssessment);

        void addLearntWords(ScienceQuestion questionModel, String imageName);
    }
}
