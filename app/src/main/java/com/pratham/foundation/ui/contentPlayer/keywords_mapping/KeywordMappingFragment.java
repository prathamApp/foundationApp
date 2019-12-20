package com.pratham.foundation.ui.contentPlayer.keywords_mapping;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
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


    @ViewById(R.id.btn_prev)
    ImageButton previous;

    @ViewById(R.id.btn_submit)
    SansButton submitBtn;

    @ViewById(R.id.btn_next)
    ImageButton next;

    @ViewById(R.id.showAnswer)
    Button showAnswer;
    private String contentPath, contentTitle, StudentID, resId, readingContentPath, resStartTime;
    private boolean onSdCard;
    private int index = 0;
    private List<ScienceQuestionChoice> optionList;
    private KeywordOptionAdapter keywordOptionAdapter;
    private List<ScienceQuestion> keywordmapping;
    private boolean isSubmitted = false;
    private Animation animFadein;
    private boolean showanswer = false;
    private GridLayoutManager gridLayoutManager;

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
        if (list != null) {
            keywordmapping = list;



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
            for (int i = 0; i < keywordmapping.size(); i++) {
                optionList = keywordmapping.get(i).getLstquestionchoice();
                Collections.shuffle(optionList);
            }
            LoadItemsToRecycler();
        }
    }

    private void LoadItemsToRecycler() {
        keyword.setText(keywordmapping.get(index).getQuestion());
        optionList = keywordmapping.get(index).getLstquestionchoice();
        //Collections.shuffle(optionList);
        //  List temp =
       /* for (int i = 0; i < temp.size(); i++) {
            optionList.add(new OptionKeyMap(temp.get(i).toString(), false));
        }*/
        keywordOptionAdapter = new KeywordOptionAdapter(getActivity(), optionList, getCorrectCnt(optionList), presenter);
        recycler_view.setAdapter(keywordOptionAdapter);
        recycler_view.setLayoutManager(gridLayoutManager);
        if (keywordmapping.get(index).isSubmitted()) {
            if (!FC_Constants.isTest) {
                showResult();
            }else {
                keywordOptionAdapter.setClickable(false);
                keywordOptionAdapter.setShowAnswer(true);
            }
            submitBtn.setVisibility(View.INVISIBLE);
            showAnswer.setVisibility(View.INVISIBLE);
        } else {
            submitBtn.setVisibility(View.VISIBLE);
            if (FC_Constants.isTest) {
                showAnswer.setVisibility(View.INVISIBLE);
            } else {
                showAnswer.setVisibility(View.VISIBLE);
            }
        }

        if (index == 0) {
            previous.setVisibility(View.INVISIBLE);
        } else {
            previous.setVisibility(View.VISIBLE);
        }
      /*  if (index == (keywordmapping.size() - 1)) {
           submitBtn.setVisibility(View.VISIBLE);
              next.setVisibility(View.INVISIBLE);
        } else {
             submitBtn.setVisibility(View.INVISIBLE);
            submitBtn.setVisibility(View.VISIBLE);
        }*/

    }

    @Click(R.id.btn_next)
    public void onNextClick() {
        if (keywordOptionAdapter != null)
            if (index < (keywordmapping.size() - 1)) {
                index++;
                LoadItemsToRecycler();
            } else {
                    GameConstatnts.playGameNext(getActivity(), GameConstatnts.TRUE, (OnGameClose) this);
            }
    }

    @Click(R.id.btn_prev)
    public void onPreviousClick() {
        if (keywordOptionAdapter != null)
            if (index > 0) {
                //  setAnswer();
                index--;
                LoadItemsToRecycler();
            }
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
    public void showResult() {

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

    @Click(R.id.btn_submit)
    public void submitClick() {
        if (keywordOptionAdapter != null && keywordmapping != null) {
            List<ScienceQuestionChoice> selectedoptionList = keywordOptionAdapter.getSelectedOptionList();
            if (selectedoptionList != null && selectedoptionList.size() > 0) {
                isSubmitted = true;
                presenter.addLearntWords(keywordmapping.get(index), selectedoptionList);
                showAnswer.setVisibility(View.INVISIBLE);
                keywordmapping.get(index).setSubmitted(true);
                // submitBtn.setText("Next");
                submitBtn.setVisibility(View.INVISIBLE);
                BaseActivity.correctSound.start();
                recycler_view.startAnimation(animFadein);
            }
        }
    }

    @Override
    public void gameClose() {
        presenter.returnScore();
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.KEYWORD_MAPPING + " " + GameConstatnts.END);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (!keywordmapping.get(index).getInstruction().isEmpty())
            GameConstatnts.showGameInfo(getActivity(), keywordmapping.get(index).getInstruction(), readingContentPath + keywordmapping.get(index).getInstructionUrl());
    }

    @Click(R.id.showAnswer)
    public void showAnswer() {
        if (showanswer) {
            //hide answer
           // showAnswer.setText("Hint");
           showAnswer.setText(getResources().getString(R.string.hint));
            showanswer = false;
            keywordOptionAdapter.setClickable(true);
            submitBtn.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            if (index == 0) {
                previous.setVisibility(View.INVISIBLE);
            } else {
                previous.setVisibility(View.VISIBLE);
            }

        } else {
            //show Answer
            //showAnswer.setText("Hide Hint");
            showAnswer.setText(getResources().getString(R.string.hide_hint));
            showanswer = true;
            keywordOptionAdapter.setClickable(false);
            submitBtn.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            previous.setVisibility(View.INVISIBLE);
        }
        keywordOptionAdapter.setShowAnswer(showanswer);
        keywordOptionAdapter.notifyDataSetChanged();
    }
}
