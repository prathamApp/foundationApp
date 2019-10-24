package com.pratham.foundation.ui.contentPlayer.reading_rhyming;
import com.pratham.foundation.modalclasses.ModalRhymingWords;

import java.util.List;

/**
 * Created by Ameya on 23-Nov-17.
 */

public interface ReadingRhymesContract {

    interface ReadingRhymesView {
        void setListData(List<ModalRhymingWords> wordsDataList);

        void noNextButton();

        void setCorrectViewColor(int i);

//        void _getLearntWordsCount();
    }

    interface ReadingRhymesPresenter {
        void fetchJsonData(String contentPath);

        void getDataList();

        void getNextDataList();

        void setResIdAndStartTime(String resId);

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);

//        void sttResultProcess(ArrayList<String> sttResults, List<ModalRhymingWords> rhymingWordsList);

        void addLearntWords(int pos, ModalRhymingWords rhymingWords);

        void setCompletionPercentage();

        void setView(ReadingRhymesContract.ReadingRhymesView readingView);

        void setLearntWordsCount(int learntWordCount);

        int getTotalCount();
    }

}
