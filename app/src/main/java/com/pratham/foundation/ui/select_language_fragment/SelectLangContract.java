package com.pratham.foundation.ui.select_language_fragment;


import com.pratham.foundation.database.domain.ContentTable;

import java.util.List;

/**
 * Created by Ameya on 23-Nov-17.
 */

public interface SelectLangContract {

    interface SelectLangView{
        void updateLangList(List<ContentTable> langList);

        void connectToInternetDialog();

        void notifyAdapter();

        void dismissLoadingDialog();

        void showLoader();

        void serverIssueDialog();
    }

    interface SelectLangPresenter {
        void setView(SelectLangContract.SelectLangView selectLangView);

        void getLanguage();
    }

    interface LangItemClicked {
        void itemClicked(ContentTable contentList, int pos);
    }

}
