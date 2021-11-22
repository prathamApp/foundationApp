package com.pratham.foundation.ui.contentPlayer.vocabulary_qa;
import com.pratham.foundation.modalclasses.ModalVocabulary;

import java.util.ArrayList;
import java.util.List;

public interface ReadingVocabularyContract {

    interface ReadingCateogryClick {
        void setCategory(String name);
    }

    interface ReadingVocabularyView {
        void setListData(List<ModalVocabulary> wordsDataList);

        void sendClikChanger(int i);

        void setCategoryAdapter();

        void setCategoryList(List<String> categoryList);

        void setCorrectViewColor();

        void allCorrectAnswer();
    }

    interface ReadingVocabularyPresenter {
        void fetchJsonData(String contentPath, String vocabCategory);

        void getDataList();

        void getCategoryList(String selectedCategory);

        void getNextDataList();

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);

        void setResIdAndStartTime(String resId);

        void sttResultProcess(ArrayList<String> sttResults, ModalVocabulary modalVocabulary, String check, String ansCheck);

        void createWholeList(String vocabCategory);

        void setCompletionPercentage();

        void setView(ReadingVocabularyContract.ReadingVocabularyView readingView);
    }

}
