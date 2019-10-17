package com.pratham.foundation.ui.selectSubject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.ui.home_screen.TempHomeActivity_;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import static com.pratham.foundation.utility.FC_Utility.dpToPx;

@EActivity(R.layout.activity_select_subject)
public class SelectSubject extends BaseActivity implements
        SelectSubjectContract.View, SelectSubjectContract.itemClicked {

    @Bean(SelectSubjectPresenter.class)
    SelectSubjectContract.Presenter presenter;

    @ViewById(R.id.subject_recycler)
    RecyclerView subject_recycler;
    @ViewById(R.id.name)
    TextView name;
    private Context context;
    SelectSubjectAdapter subjectAdapter;
    String studName;

    @AfterViews
    protected void initiate() {
        // super.onCreate(savedInstanceState);
        //setContentView();
        studName = getIntent().getStringExtra("studName");
        context = SelectSubject.this;
        Configuration config = getResources().getConfiguration();
        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
        List<ContentTable> subjectList = presenter.getSubjectList();

        name.setText("Welcome "+studName+".");

        subjectAdapter = new SelectSubjectAdapter(this, subjectList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        subject_recycler.setLayoutManager(mLayoutManager);
        int dp = 12;
        if (FC_Constants.TAB_LAYOUT)
            dp = 20;
        subject_recycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(this,dp), true));
        subject_recycler.setItemAnimator(new DefaultItemAnimator());
        subject_recycler.setAdapter(subjectAdapter);
    }

    private int getScreenWidthDp() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    @Override
    public void onItemClicked(ContentTable contentTableObj) {
        Intent intent = new Intent(context, TempHomeActivity_.class);
        intent.putExtra("nodeId", contentTableObj.getNodeId());
        context.startActivity(intent);
    }
}
