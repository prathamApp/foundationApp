package com.pratham.foundation.ui.contentPlayer.keywords_mapping;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

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
    @ViewById(R.id.submit)
    Button submit;
    @ViewById(R.id.showAnswer)
    Button showAnswer;
    private String contentPath, contentTitle, StudentID, resId, readingContentPath, resStartTime;
    private boolean onSdCard;
    private int index = 0;
    private List<ScienceQuestionChoice> optionList;
    private KeywordOptionAdapter keywordOptionAdapter;
    private ScienceQuestion keywordmapping;
    private boolean isSubmitted = false;
    private Animation animFadein;
    private boolean showanswer = false;

    @AfterViews
    protected void initiate() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            contentPath = bundle.getString("contentPath");
            StudentID = bundle.getString("StudentID");
            resId = bundle.getString("resId");
            contentTitle = bundle.getString("contentName");
            onSdCard = bundle.getBoolean("onSdCard", false);
            if (onSdCard)
                readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";
        }
        animFadein = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.bounce_new);
        EventBus.getDefault().register(this);
        presenter.setView(KeywordMappingFragment.this, resId, readingContentPath);
        presenter.getData();
        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.KEYWORD_MAPPING + " " + GameConstatnts.START);
        if (FC_Constants.isTest) {
            showAnswer.setVisibility(View.INVISIBLE);
        }
    }


    @UiThread
    @Override
    public void loadUI(List<ScienceQuestion> list) {
        //show instruction dialog
        keywordmapping = list.get(index);
        keyword.setText(list.get(index).getQuestion());
        final GridLayoutManager gridLayoutManager;

       /* if (FC_Constants.TAB_LAYOUT) {
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        }*/
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int size = 1;
                if ((position + 1) % 3 == 0) {
                    size = 2;
                }
                return size;
            }
        });
        optionList = list.get(index).getLstquestionchoice();
        Collections.shuffle(optionList);
        //  List temp =
       /* for (int i = 0; i < temp.size(); i++) {
            optionList.add(new OptionKeyMap(temp.get(i).toString(), false));
        }*/
        keywordOptionAdapter = new KeywordOptionAdapter(getActivity(), optionList, getCorrectCnt(optionList), presenter);
        recycler_view.setAdapter(keywordOptionAdapter);
        recycler_view.setLayoutManager(gridLayoutManager);


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
    public void showResult(ScienceQuestion selectedAnsList) {

        keywordOptionAdapter.setClickable(false);
        keywordOptionAdapter.notifyDataSetChanged();
       /* for (int index = 0; index < recycler_view.getChildCount(); index++) {
            TextView textView = (TextView) recycler_view.getChildAt(index);
            if (!presenter.checkAnswerNew(selectedAnsList.getLstquestionchoice(), textView.getText().toString())) {
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textView.setBackgroundColor(getResources().getColor(R.color.level_1_color));
            } else {
                textView.setBackgroundColor(getResources().getColor(R.color.colorRed));
            }
        }*/
        //     textView.setPaintFlags(textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        //iews.setInt(R.id.widgetTitle, "setPaintFlags", Paint.ANTI_ALIAS_FLAG);
        /*if ((correctWord != null && !correctWord.isEmpty()) || (wrongWord != null && !wrongWord.isEmpty())) {
            final CustomLodingDialogdialog = new Dialog(getActivity());
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
        }*/
    }

    @Click(R.id.submit)
    public void submitClick() {
        if (keywordOptionAdapter != null && keywordmapping != null) {
            List<ScienceQuestionChoice> selectedoptionList = keywordOptionAdapter.getSelectedOptionList();
            if (selectedoptionList != null && selectedoptionList.size() > 0) {
                if (isSubmitted) {
                    GameConstatnts.playGameNext(getActivity(), GameConstatnts.FALSE, this);
                } else {
                    isSubmitted = true;
                    presenter.addLearntWords(keywordmapping, selectedoptionList);
                    showAnswer.setVisibility(View.INVISIBLE);
                    submit.setText("Next");
                    BaseActivity.correctSound.start();
                    recycler_view.startAnimation(animFadein);
                }
            } else {
                GameConstatnts.playGameNext(getActivity(), GameConstatnts.TRUE, (OnGameClose) this);
            }
        }
    }

    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.KEYWORD_MAPPING + " " + GameConstatnts.END);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (!keywordmapping.getInstruction().isEmpty())
            GameConstatnts.showGameInfo(getActivity(), keywordmapping.getInstruction(), readingContentPath + keywordmapping.getInstructionUrl());
    }

    @Click(R.id.showAnswer)
    public void showAnswer() {
        if (showanswer) {
            //hide answer
            showAnswer.setText("Hint");
            showanswer = false;
            keywordOptionAdapter.setClickable(true);
            submit.setVisibility(View.VISIBLE);

        } else {
            //show Answer
            showAnswer.setText("Hide Hint");
            showanswer = true;
            keywordOptionAdapter.setClickable(false);
            submit.setVisibility(View.INVISIBLE);
        }
        keywordOptionAdapter.setShowAnswer(showanswer);
        keywordOptionAdapter.notifyDataSetChanged();
    }
}
