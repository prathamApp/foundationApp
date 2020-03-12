package com.pratham.foundation.ui.selectSubject;

import android.content.Context;

import com.pratham.foundation.database.AppDatabase;

import org.androidannotations.annotations.EBean;

import java.util.List;

@EBean
public class SelectSubjectPresenter implements SelectSubjectContract.Presenter {

    Context context;

    SelectSubjectPresenter(Context context) {
        this.context = context;
    }

    @Override
    public List getSubjectList() {
        return AppDatabase.getDatabaseInstance(context).getContentTableDao().getChildsOfParent("50001");
    }
}
