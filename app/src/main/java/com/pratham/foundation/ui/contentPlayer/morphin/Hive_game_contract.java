package com.pratham.foundation.ui.contentPlayer.morphin;

import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;

import java.util.List;

public interface Hive_game_contract {
    interface Hive_game_view {
        void loadQuestion(List<ScienceQuestion> questionModel);
        void showResult();
        void onDataNotFound();
    }

    interface Hive_game_presenter {
        void setView(Hive_game_contract.Hive_game_view hive_game_view, String contentTitle, String resId);
        void getData(String readingContentPath);
        boolean checkIsAttempted(List<ScienceQuestionChoice> selectedAnsList);
        void addLearntWords(ScienceQuestion quetionModel, List<ScienceQuestionChoice> selectedAnsList);
        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label);
    }
}
