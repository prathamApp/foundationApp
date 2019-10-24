package com.pratham.foundation.ui.contentPlayer.old_cos.reading_cards;

import com.pratham.foundation.modalclasses.ModalReadingCardSubMenu;

import java.util.List;

/**
 * Created by Ameya on 23-Nov-17.
 */

public interface ReadingCardContract {

    interface ReadingCardView{
        void setCardAudio(String cardAudio);

        void setListData(List<ModalReadingCardSubMenu> modalReadingCardSubMenuList);

        void initializeContent();

        void setContentTitle(String contentTitle);

        void showLoader();

        void dismissLoadingDialog();
    }

    interface ReadingCardPresenter {
        void fetchJsonData(String readingContentPath);

        void setResId(String resId);

        void addCompletion();

        void setView(ReadingCardView readingView);
    }

}
