package com.pratham.foundation.ui.contentPlayer.keywords_mapping;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@EFragment(R.layout.fragment_keyword_mapping)
public class KeywordMappingFragment extends Fragment implements KeywordMappingContract.KeywordMappingView, OnGameClose {

    @Bean(KeywordMappingPresenterImp.class)
    KeywordMappingContract.KeywordMappingPresenter presenter;

    /*  @ViewById(R.id.tittle)
      TextView tittle;*/
    @ViewById(R.id.keyword)
    TextView keyword;
    @ViewById(R.id.recycler_view)
    RecyclerView recycler_view;

    private String contentPath, contentTitle, StudentID, resId, readingContentPath, resStartTime;
    private boolean onSdCard;
    private int index = 0;
    private List<ScienceQuestionChoice> optionList;
    private KeywordOptionAdapter keywordOptionAdapter;
    private ScienceQuestion keywordmapping;

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

        presenter.setView(KeywordMappingFragment.this, resId, readingContentPath);
        presenter.getData();
        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime,FC_Utility.getCurrentDateTime(), GameConstatnts.KEYWORD_MAPPING + " " + GameConstatnts.START);

    }

    @UiThread
    @Override
    public void loadUI(List<ScienceQuestion> list) {
        keywordmapping = list.get(index);
        keyword.setText(list.get(index).getQuestion());
        final GridLayoutManager gridLayoutManager;

        /* RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());*/
        if (FC_Constants.TAB_LAYOUT) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        }

        optionList = list.get(index).getLstquestionchoice();
        Collections.shuffle(optionList);
        //  List temp =
       /* for (int i = 0; i < temp.size(); i++) {
            optionList.add(new OptionKeyMap(temp.get(i).toString(), false));
        }*/
        recycler_view.setLayoutManager(gridLayoutManager);
        keywordOptionAdapter = new KeywordOptionAdapter(getActivity(), optionList, getCorrectCnt(optionList));
        recycler_view.setAdapter(keywordOptionAdapter);
    }

    private int getCorrectCnt(List<ScienceQuestionChoice> lstquestionchoice) {
        int correctCnt = 0;
        for (int i = 0; i < lstquestionchoice.size(); i++) {
            if (lstquestionchoice.get(i).getCorrectAnswer().equalsIgnoreCase("true")) {
                correctCnt++;
            }
        }
        return correctCnt;
    }

    @Override
    public void showResult(List correctWord, List wrongWord) {
        if ((correctWord != null && !correctWord.isEmpty()) || (wrongWord != null && !wrongWord.isEmpty())) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.show_result);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            SansTextViewBold correct_keywords = dialog.findViewById(R.id.correct_keywords);
            SansTextViewBold wrong_keywords = dialog.findViewById(R.id.wrong_keywords);
            SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
            correct_keywords.setText(correctWord.toString().substring(1, correctWord.toString().length() - 1));
            wrong_keywords.setText(wrongWord.toString().substring(1, wrongWord.toString().length() - 1));
            dia_btn_yellow.setText("OK");
            dia_btn_yellow.setOnClickListener(v -> {
                dialog.dismiss();
                GameConstatnts.playGameNext(getActivity(), GameConstatnts.FALSE, this);
            });
            dialog.show();
        } else {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.TRUE, this);
        }
    }

    @Click(R.id.submit)
    public void submitClick() {
        if (keywordOptionAdapter != null && keywordmapping != null) {
            List<ScienceQuestionChoice> selectedoptionList = keywordOptionAdapter.getSelectedOptionList();
            presenter.addLearntWords(keywordmapping, selectedoptionList);
        }
        // GameConstatnts.playGameNext(getActivity());
       /* Bundle bundle = GameConstatnts.findGameData("104");
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new ParagraphWritingFragment_(), R.id.RL_CPA,
                    bundle, ParagraphWritingFragment_.class.getSimpleName());

        }*/
    }

    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime,FC_Utility.getCurrentDateTime(), GameConstatnts.KEYWORD_MAPPING + " " + GameConstatnts.END);
    }
}
