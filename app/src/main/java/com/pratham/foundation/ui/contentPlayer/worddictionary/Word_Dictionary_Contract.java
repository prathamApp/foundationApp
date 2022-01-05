package com.pratham.foundation.ui.contentPlayer.worddictionary;

import com.pratham.foundation.modalclasses.ModalParaWord;
import com.pratham.foundation.modalclasses.WordDictionaryModel;

import java.util.ArrayList;
import java.util.List;


public interface Word_Dictionary_Contract {

    interface Word_Dictionary_View {
        void setListData(List<String> paraDataList);
         void setCharListData(List<WordDictionaryModel> paraDataList);
        void setLangListData(List<String> paraDataList);
        void nextbuttonaction();

        void initializeContent();
        void initializeLangContent();




    }

    interface Word_Dictionary_Presenter {
        void fetchJsonData(String contentPath, String resType);
        void fetchCharData(String contentPath, String resType);
        void fetchLangJsonData(String contentPath, String resType);
        void getDataList();
        void getLangDataList();
        void addCompletion(String values,String word);
          void getCahrDataList();
        void setResId(String resId);



        void exitDBEntry();




        void setView(Word_Dictionary_View readingView);


    }

}
