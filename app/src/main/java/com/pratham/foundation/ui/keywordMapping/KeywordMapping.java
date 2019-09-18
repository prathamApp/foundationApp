package com.pratham.foundation.ui.keywordMapping;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.OptionKeyMap;
import com.pratham.foundation.modalclasses.keywordmapping;
import com.pratham.foundation.ui.writingParagraph.WritingParagraph;
import com.pratham.foundation.ui.writingParagraph.WritingParagraph_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_keyword_mapping2)
public class KeywordMapping extends AppCompatActivity implements KeywordMappingContract.View {
    @Bean(KeywordMappingPresenterImp.class)
    KeywordMappingContract.Presenter presenter;

    @ViewById(R.id.tittle)
    TextView tittle;
    @ViewById(R.id.keyword)
    TextView keyword;
    @ViewById(R.id.recycler_view)
    RecyclerView recycler_view;

    private String contentPath, contentTitle, StudentID, resId;
    private boolean onSdCard;
    private int index = 0;
    private Context context;
    private List<OptionKeyMap> optionList;
    private KeywordOptionAdapter keywordOptionAdapter;

    @AfterViews
    protected void initiate() {
        // super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_keyword_mapping2);
        Intent intent = getIntent();
        contentPath = intent.getStringExtra("contentPath");
        StudentID = intent.getStringExtra("StudentID");
        resId = intent.getStringExtra("resId");
        contentTitle = intent.getStringExtra("contentName");
        onSdCard = getIntent().getBooleanExtra("onSdCard", false);
        context = KeywordMapping.this;
        presenter.getData();
    }

    @Override
    public void loadUI(List<keywordmapping> list) {

        keyword.setText(list.get(index).getKeyword());
        final GridLayoutManager gridLayoutManager;

        /* RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());*/
        if (getScreenWidthDp() >= 1200) {
            gridLayoutManager = new GridLayoutManager(context, 3);
        } else {
            gridLayoutManager = new GridLayoutManager(context, 2);
        }

        optionList = new ArrayList<>();
        List temp = list.get(index).getKeywordOptionSet();
        for (int i = 0; i < temp.size(); i++) {
            optionList.add(new OptionKeyMap(temp.get(i).toString(), false));
        }
        recycler_view.setLayoutManager(gridLayoutManager);
        keywordOptionAdapter = new KeywordOptionAdapter(context, optionList);
        recycler_view.setAdapter(keywordOptionAdapter);
    }

    private int getScreenWidthDp() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    @Click(R.id.submit)
    public void submitClick() {
       /* List selectedoptionList = keywordOptionAdapter.getSelectedOptionList();
        Log.d("tag", selectedoptionList.toString());*/
        Intent intent = new Intent(context, WritingParagraph_.class);
        startActivity(intent);
    }
}
