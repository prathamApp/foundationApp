package com.pratham.foundation.ui.selectSubject;

import com.pratham.foundation.database.domain.ContentTable;

import org.androidannotations.annotations.EBean;

import java.util.List;

public interface SelectSubjectContract {

    interface itemClicked {
        void onItemClicked(ContentTable contentTableObj);
    }

    public interface View {

    }

    public interface Presenter {
        public List getSubjectList();
    }
}
