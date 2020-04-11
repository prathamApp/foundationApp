package com.pratham.foundation.ui.selectSubject;

import com.pratham.foundation.database.domain.ContentTable;

import java.util.List;

public interface SelectSubjectContract {

    interface ItemClicked {
        void onItemClicked(ContentTable contentTableObj);
    }

    interface AppLanguageSpinnerListner{
        void onAppSpinnerLanguageChanged(String language);
    }

    interface SubjectView {
        void showLanguageSelectionDialog(List<ContentTable> serverContentList);

        void showLoader();

        void dismissLoadingDialog();

        void initializeSubjectList(List<ContentTable> subjectList);
    }

    interface SubjectPresenter {

        void  setView(SelectSubjectContract.SubjectView subjectView);
            
        void getSubjectList();
        
        void getLanguageFromApi();
    }
}
