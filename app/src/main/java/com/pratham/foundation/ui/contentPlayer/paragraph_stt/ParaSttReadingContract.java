package com.pratham.foundation.ui.contentPlayer.paragraph_stt;
import com.pratham.foundation.modalclasses.ModalParaSubMenu;
import com.pratham.foundation.modalclasses.ParaSttQuestionListModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ameya on 23-Nov-17.
 **/

public interface ParaSttReadingContract {

    interface ParaSttReadingView {
        void setListData(List<ModalParaSubMenu> paraDataList);

        void setCategoryTitle(String title);

        void setParaAudio(String paraAudio);

        void initializeContent(int pageNo);

        void allCorrectAnswer();

        void setCorrectViewColor();

        void dismissLoadingDialog();

        void showLoader();
    }

    interface ParaSttReadingPresenter {
        void fetchJsonData(String contentPath);

        void getDataList();

        void getPage(int pgNo);

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);

        void addExitScore(float perc, String Label);

        void setResId(String resId);

        void sttResultProcess(ArrayList<String> sttResult, List<String> splitWordsPunct, List<String> wordsResIdList);

        void addProgress();

        void setView(ParaSttReadingView readingView);

        void micStopped(List<String> splitWordsPunct, List<String> wordsResIdList);
    }


    interface STTQuestionsView {
        void setListData(List<ParaSttQuestionListModel> paraDataList);

        void setCategoryTitle(String title);

        void initializeContent(int pageNo);

        void dismissLoadingDialog();

        void showLoader();
    }

    interface STTQuestionsPresenter {

        void fetchJsonData(String contentPath, String jsonName);

        void getDataList();

        void getPage(int pgNo);

        void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label);

        void addExitScore(float perc, String Label);

        void setResId(String resId);

        void addProgress(String[] sttAnswers, String[] sttAnswersTime);

        void setView(STTQuestionsView readingView);
    }

}