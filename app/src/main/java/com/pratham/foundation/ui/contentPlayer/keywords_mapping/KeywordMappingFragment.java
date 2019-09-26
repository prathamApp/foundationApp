package com.pratham.foundation.ui.contentPlayer.keywords_mapping;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.OptionKeyMap;
import com.pratham.foundation.modalclasses.keywordmapping;
import com.pratham.foundation.ui.contentPlayer.paragraph_writing.ParagraphWritingFragment_;
import com.pratham.foundation.ui.writingParagraph.WritingParagraph_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_keyword_mapping)
public class KeywordMappingFragment extends Fragment implements KeywordMappingContract.KeywordMappingView {

    @Bean(KeywordMappingPresenterImp.class)
    KeywordMappingContract.KeywordMappingPresenter presenter;

    @ViewById(R.id.tittle)
    TextView tittle;
    @ViewById(R.id.keyword)
    TextView keyword;
    @ViewById(R.id.recycler_view)
    RecyclerView recycler_view;

    private String contentPath, contentTitle, StudentID, resId;
    private boolean onSdCard;
    private int index = 0;
    private List<OptionKeyMap> optionList;
    private KeywordOptionAdapter keywordOptionAdapter;

    @AfterViews
    protected void initiate() {
        // super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_keyword_mapping2);
        presenter.setView(KeywordMappingFragment.this);
        presenter.getData();
    }

    @UiThread
    @Override
    public void loadUI(List<keywordmapping> list) {

        keyword.setText(list.get(index).getKeyword());
        final GridLayoutManager gridLayoutManager;

        /* RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());*/
        if (FC_Constants.TAB_LAYOUT) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        }

        optionList = new ArrayList<>();
        List temp = list.get(index).getKeywordOptionSet();
        for (int i = 0; i < temp.size(); i++) {
            optionList.add(new OptionKeyMap(temp.get(i).toString(), false));
        }
        recycler_view.setLayoutManager(gridLayoutManager);
        keywordOptionAdapter = new KeywordOptionAdapter(getActivity(), optionList);
        recycler_view.setAdapter(keywordOptionAdapter);
    }

//    private int getScreenWidthDp() {
//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//        return (int) (displayMetrics.widthPixels / displayMetrics.density);
//    }

    @Click(R.id.submit)
    public void submitClick() {
        FC_Utility.showFragment(getActivity(), new ParagraphWritingFragment_(), R.id.RL_CPA,
                null, ParagraphWritingFragment_.class.getSimpleName());

       /* List selectedoptionList = keywordOptionAdapter.getSelectedOptionList();
        Log.d("tag", selectedoptionList.toString());*/
/*        Intent intent = new Intent(getActivity(), WritingParagraph_.class);
        startActivity(intent);*/
    }
}
