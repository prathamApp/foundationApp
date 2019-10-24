package com.pratham.foundation.ui.contentPlayer.opposites;

import com.pratham.foundation.modalclasses.ModalReadingVocabulary;

/**
 * Created by Ameya on 23-Nov-17.
 */

public interface OppositesContract {

    interface OppositesView {

        void showPrevious();

        void hidePrevious();

        void showWordNextDialog();

        void loadUi(ModalReadingVocabulary modalReadingVocabulary);
    }

    interface OppositesPresenter {

        void fetchJsonData(String readingContentPath);

        void showPrevious();

        void setCompletionPercentage();

        void showNext();

        void revise();

        void getDataList();

        void setView(OppositesContract.OppositesView oppositesView, String contentTitle, String resId);
    }
}