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
        void showLoader();

        void dismissLoadingDialog();

        void initializeSubjectList(List<ContentTable> subjectList);

        void notifySubjAdapter();

        void serverIssueDialog();
    }

    interface SubjectPresenter {

        void  setView(SubjectView subjectView);
            
        void getSubjectList();

        void clearSubjList();
    }
}
