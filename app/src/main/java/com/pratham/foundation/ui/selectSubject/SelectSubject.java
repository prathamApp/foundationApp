package com.pratham.foundation.ui.selectSubject;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_select_subject)
public class SelectSubject extends AppCompatActivity implements SelectSubjectContract.View {
    @Bean(SelectSubjectPresenter.class)
    SelectSubjectContract.Presenter presenter;

    @ViewById(R.id.recycler)
    RecyclerView recyclerView;
    private Context context;

    @AfterViews
    protected void initiate() {
        // super.onCreate(savedInstanceState);
        //setContentView();
        context = SelectSubject.this;
        final GridLayoutManager gridLayoutManager;
        if (getScreenWidthDp() >= 1200) {
            gridLayoutManager = new GridLayoutManager(context, 3);
        } else {
            gridLayoutManager = new GridLayoutManager(context, 2);
        }
        recyclerView.setLayoutManager(gridLayoutManager);
        List<ContentTable> subjectList = presenter.getSubjectList();
        SelectSubjectAdapter selectSubjectAdapter = new SelectSubjectAdapter(context, subjectList);
        recyclerView.setAdapter(selectSubjectAdapter);
    }

    private int getScreenWidthDp() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }
}
