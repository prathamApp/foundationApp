package com.pratham.foundation.ui.contentPlayer.keywords_mapping;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.TextView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.SansTextViewBold;
import com.pratham.foundation.modalclasses.OptionKeyMap;
import com.pratham.foundation.modalclasses.keywordmapping;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.paragraph_writing.ParagraphWritingFragment_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
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

    private String contentPath, contentTitle, StudentID, resId, readingContentPath;
    private boolean onSdCard;
    private int index = 0;
    private List<OptionKeyMap> optionList;
    private KeywordOptionAdapter keywordOptionAdapter;
    private keywordmapping keywordmapping;

    @AfterViews
    protected void initiate() {
        // super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_keyword_mapping2);
        Bundle bundle = getArguments();
        if (bundle != null) {
            contentPath = bundle.getString("contentPath");
            StudentID = bundle.getString("StudentID");
            resId = bundle.getString("resId");
            contentTitle = bundle.getString("contentName");
            onSdCard = bundle.getBoolean("onSdCard", false);
            if (onSdCard)
                readingContentPath = ApplicationClass.contentSDPath + "/.FCA/English/Game/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + "/.FCA/English/Game/" + contentPath + "/";

        }
        presenter.setView(KeywordMappingFragment.this, resId);
        presenter.getData();
    }

    @UiThread
    @Override
    public void loadUI(List<keywordmapping> list) {
        keywordmapping = list.get(index);
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
        keywordOptionAdapter = new KeywordOptionAdapter(getActivity(), optionList, keywordmapping.getKeywordAnsSet().size());
        recycler_view.setAdapter(keywordOptionAdapter);
    }

    @Override
    public void showResult(List correctWord, List wrongWord) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_result);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        SansTextViewBold correct_keywords = dialog.findViewById(R.id.correct_keywords);
        SansTextViewBold wrong_keywords = dialog.findViewById(R.id.wrong_keywords);
        SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        correct_keywords.setText(correctWord.toString().substring(1, correctWord.toString().length() - 1));
        wrong_keywords.setText(wrongWord.toString().substring(1, wrongWord.toString().length() - 1));
        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Click(R.id.submit)
    public void submitClick() {
        List selectedoptionList = keywordOptionAdapter.getSelectedOptionList();
        presenter.addLearntWords(keywordmapping, selectedoptionList);
        Bundle bundle = GameConstatnts.findGameData("104");
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new ParagraphWritingFragment_(), R.id.RL_CPA,
                    bundle, ParagraphWritingFragment_.class.getSimpleName());

        }
    }
}
