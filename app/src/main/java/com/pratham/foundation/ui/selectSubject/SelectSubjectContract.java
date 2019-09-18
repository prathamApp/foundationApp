package com.pratham.foundation.ui.selectSubject;

import org.androidannotations.annotations.EBean;

import java.util.List;

public interface SelectSubjectContract {
    public interface View {

    }

    public interface Presenter {
        public List getSubjectList();
    }
}
