package com.pratham.foundation.ui.selectSubject;

import android.content.Context;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.ContentTable;

import org.androidannotations.annotations.EBean;

import java.util.List;

@EBean
public class SelectSubjectPresenter implements SelectSubjectContract.Presenter {
    Context context;

    public SelectSubjectPresenter(Context context) {
        this.context = context;
    }

    @Override
    public List getSubjectList() {
        List<ContentTable> subjectList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getChildsOfParent("50001");
        return subjectList;
    }
}
