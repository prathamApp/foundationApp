package com.pratham.foundation.ui.contentPlayer.keywords_identification;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.flowlayout.FlowLayout;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
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
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EFragment(R.layout.fragment_keywords_identification)
public class KeywordsIdentificationFragment extends Fragment implements KeywordsIdentificationContract.KeywordsView, OnGameClose {

    @Bean(KeywordsIdentificationPresenter.class)
    KeywordsIdentificationContract.KeywordsPresenter presenter;

    @ViewById(R.id.paragraph)
    FlowLayout paraghaph;
    /* @ViewById(R.id.tittle)
     SansTextView tittle;*/
    @ViewById(R.id.keywords)
    FlowLayout keywords;
    @ViewById(R.id.btn_submit)
    SansButton btn_submit;
    @ViewById(R.id.keyword_selected)
    RelativeLayout keyword_selected;
    @ViewById(R.id.show_me_keywords)
    SansButton show_me_keywords;
    @ViewById(R.id.resultLayout)
    RelativeLayout resultLayout;

    RelativeLayout.LayoutParams viewParam;
    private HashMap<String, List<Integer>> positionMap;
    private List<ScienceQuestionChoice> selectedKeywords;
    private String contentPath, contentTitle, StudentID, resId, readingContentPath, resStartTime;
    private boolean onSdCard;
    private ScienceQuestion questionModel;
    private boolean isKeyWordShowing = false;
    private boolean issubmitted = false;
    private Animation animFadein;
    @AfterViews
    public void initiate() {
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
        presenter.setView(KeywordsIdentificationFragment.this, resId, readingContentPath);
        selectedKeywords = new ArrayList<>();
        positionMap = new HashMap<>();

        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.KEYWORD_IDENTIFICATION + " " + GameConstatnts.START);

        viewParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 5, 10, 5);
        if (FC_Constants.isTest) {
            show_me_keywords.setVisibility(View.GONE);
        }
        presenter.getData();
    }

    public void showAnswer() {
        if (!FC_Constants.isTest && !FC_Constants.isPractice) {
            show_me_keywords.performClick();
        }
    }
    @Override
    public void showParagraph(ScienceQuestion questionModel) {
        this.questionModel = questionModel;
        this.questionModel.setQuestion(questionModel.getQuestion().replace("\n", " "));
       /* if (questionModel.getInstruction() != null && !questionModel.getInstruction().isEmpty()) {
            tittle.setText(questionModel.getInstruction());
        }*/
        String[] paragraphWords = questionModel.getQuestion().split(" ");
        for (int i = 0; i < paragraphWords.length; i++) {
            if (positionMap.containsKey(paragraphWords[i].replaceAll("\\p{Punct}", "").trim())) {
                List temp = positionMap.get(paragraphWords[i].replaceAll("\\p{Punct}", "").trim());
                if (temp != null)
                    temp.add(i);
            } else {
                List<Integer> temp = new ArrayList<>();
                temp.add(i);
                positionMap.put(paragraphWords[i].replaceAll("\\p{Punct}", "").trim(), temp);
            }

            final SansTextView textView = new SansTextView(getActivity());
            textView.setTextSize(30);
            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorText));
            textView.setText(paragraphWords[i]);
            textView.setPadding(10, 5, 10, 5);
            final int temp_i = i;
            textView.setOnClickListener(v -> paragraphWordClicked("" + textView.getText().toString().replaceAll("\\p{Punct}", "").trim(), temp_i));
            paraghaph.addView(textView);

        }

    }

    @Override
    public void showResult(List correctWord, List wrongWord) {
        if ((correctWord != null && !correctWord.isEmpty()) || (wrongWord != null && !wrongWord.isEmpty())) {
            for (int i = 0; i < keywords.getChildCount(); i++) {
                LinearLayout linearLayout = (LinearLayout) keywords.getChildAt(i);
                String text = ((SansTextView) linearLayout.getChildAt(0)).getText().toString();
                if (correctWord.contains(text)) {
                    linearLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.convo_correct_bg));
                }

            }


            /*final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.show_result);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            SansTextViewBold correct_keywords = dialog.findViewById(R.id.correct_keywords);
            SansTextViewBold wrong_keywords = dialog.findViewById(R.id.wrong_keywords);
            SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);

            SansTextViewBold dia_titleCorrect = dialog.findViewById(R.id.dia_title);
            SansTextViewBold dia_title_wrong = dialog.findViewById(R.id.dia_title_wrong);

            dia_titleCorrect.setText("Existing Keywords");
            dia_title_wrong.setText("New Keywords");

            dia_btn_yellow.setText("OK");
            correct_keywords.setText(correctWord.toString().substring(1, correctWord.toString().length() - 1));
            wrong_keywords.setText(wrongWord.toString().substring(1, wrongWord.toString().length() - 1));
            dia_btn_yellow.setOnClickListener(v -> {
                        dialog.dismiss();
                        GameConstatnts.playGameNext(getActivity(), GameConstatnts.FALSE, this);
                    }
            );
            dialog.show();*/
        }
    }

    private void paragraphWordClicked(String paraText, int pos) {
        if (!issubmitted) {
            if (!isKeyWordShowing) {
                int correctCnt = getCorrectCnt(questionModel.getLstquestionchoice());

                // boolean flag = checkIsConins(paraText);
                // if (!selectedKeywords.contains(paraText)) {
                if (!checkIsConins(paraText)) {
                    List positions = positionMap.get(paraText.trim());
                    if (positions != null)
                        for (int i = 0; i < positions.size(); i++) {
                            SansTextView textViewTemp = (SansTextView) paraghaph.getChildAt((int) positions.get(i));
                            // textViewTemp.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGreenCorrect));
                            textViewTemp.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.tag_view));
                            textViewTemp.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                        }

                    LinearLayout linearLayout = new LinearLayout(getActivity());
                    viewParam.setMargins(5, 8, 5, 8);
                    linearLayout.setLayoutParams(viewParam);
                    linearLayout.setGravity(Gravity.CENTER);
                    linearLayout.setElevation(3);
                    linearLayout.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dialog_bg));

                    SansTextView textViewkey = new SansTextView(getActivity());
                    textViewkey.setText(paraText);
                    textViewkey.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorText));
                    textViewkey.setId(pos);
                    textViewkey.setPadding(10, 5, 5, 5);
                    textViewkey.setTextSize(25);
                    linearLayout.addView(textViewkey);

                    final ImageView imageView = new ImageView(getActivity());
                    imageView.setId(pos);
                    imageView.setTag(paraText);
                    imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_close_red_24dp));
                    imageView.setPadding(5, 3, 10, 3);

                    linearLayout.addView(imageView);

                    keywords.addView(linearLayout);

                    //  selectedKeywords.add(paraText);
                    ScienceQuestionChoice scienceQuestionChoice = new ScienceQuestionChoice();
                    scienceQuestionChoice.setSubQues(paraText);
                    scienceQuestionChoice.setStartTime(FC_Utility.getCurrentDateTime());
                    scienceQuestionChoice.setEndTime(FC_Utility.getCurrentDateTime());
                    selectedKeywords.add(scienceQuestionChoice);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!issubmitted) {
                                String temp = imageView.getTag().toString();
                                // if (selectedKeywords.contains(temp)) {
                                if (checkIsConins(temp)) {
                                    //selectedKeywords.remove(temp);
                                    removeItem(temp);
                                    List positions = positionMap.get(paraText.trim());
                                    for (int i = 0; i < positions.size(); i++) {
                                        SansTextView textViewTemp = (SansTextView) paraghaph.getChildAt((int) positions.get(i));
                                        textViewTemp.setBackground(null);
                                        textViewTemp.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorText));
                                    }
                                    View view = (View) imageView.getParent();
                                    keywords.removeView(view);

                                } else {
                                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            } else {
                ApplicationClass.ttsService.play(paraText);
            }
        }
    }

    private boolean checkIsConins(String paraText) {
        for (int i = 0; i < selectedKeywords.size(); i++) {
            if (selectedKeywords.get(i).getSubQues().equalsIgnoreCase(paraText)) {
                return true;
            }
        }
        return false;
    }

    private void removeItem(String paraText) {
        Iterator<ScienceQuestionChoice> iterator = selectedKeywords.iterator();
        while (iterator.hasNext()) {
            ScienceQuestionChoice scienceQuestionChoice = iterator.next();
            if (scienceQuestionChoice.getSubQues().equalsIgnoreCase(paraText)) {
                iterator.remove();
                break;
            }
        }
    }

    private int getCorrectCnt(ArrayList<ScienceQuestionChoice> lstquestionchoice) {
        /*int correctCnt = 0;
        for (int i = 0; i < lstquestionchoice.size(); i++) {
            if (lstquestionchoice.get(i).getCorrectAnswer().equalsIgnoreCase("true")) {
                correctCnt++;
            }
        }
        return correctCnt;*/
        return lstquestionchoice.size();
    }

    @Click(R.id.btn_submit)
    public void submitClicked() {
        if (selectedKeywords != null && selectedKeywords.size() > 0) {
            if (issubmitted) {
                GameConstatnts.playGameNext(getActivity(), GameConstatnts.FALSE, this);
            } else {
                issubmitted = true;
                BaseActivity.correctSound.start();
                btn_submit.setText("Next");
                show_me_keywords.setVisibility(View.INVISIBLE);
                presenter.addLearntWords(selectedKeywords);
                resultLayout.startAnimation(animFadein);
            }

        } else {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.TRUE, this);
        }
    }

    @Click(R.id.show_me_keywords)
    public void show_me_keywords() {
        if (questionModel != null) {
            List<ScienceQuestionChoice> keywordList = questionModel.getLstquestionchoice();
            positionMap.clear();
            selectedKeywords.clear();
            paraghaph.removeAllViews();
            keywords.removeAllViews();
            showParagraph(questionModel);
            if (!isKeyWordShowing) {
                isKeyWordShowing = true;
              //  show_me_keywords.setText("hide keywords");
               show_me_keywords.setText(getResources().getString(R.string.hide_hint));
                btn_submit.setVisibility(View.INVISIBLE);
                for (int keywordIndex = 0; keywordIndex < keywordList.size(); keywordIndex++) {
                    List positions = positionMap.get(keywordList.get(keywordIndex).getSubQues().trim());
                    if (positions != null)
                        for (int i = 0; i < positions.size(); i++) {
                            SansTextView textViewTemp = (SansTextView) paraghaph.getChildAt((int) positions.get(i));
                            textViewTemp.setBackgroundResource(R.drawable.dialog_bg_blue);
                        }
                }
            } else {
                isKeyWordShowing = false;
                //show_me_keywords.setText("show keywords");
                show_me_keywords.setText(getResources().getString(R.string.hint));
                btn_submit.setVisibility(View.VISIBLE);
                for (int keywordIndex = 0; keywordIndex < keywordList.size(); keywordIndex++) {
                    List positions = positionMap.get(keywordList.get(keywordIndex).getSubQues().trim());
                    if (positions != null)
                        for (int i = 0; i < positions.size(); i++) {
                            SansTextView textViewTemp = (SansTextView) paraghaph.getChildAt((int) positions.get(i));
                            textViewTemp.setBackground(null);
                        }
                }
                final Dialog dialog = new CustomLodingDialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.app_success_dialog);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
                TextView message = dialog.findViewById(R.id.message);
                message.setText(getResources().getString(R.string.usermessage));
                dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
            }
        }
    }


    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.KEYWORD_IDENTIFICATION + " " + GameConstatnts.END);
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        GameConstatnts.showGameInfo(getActivity(),questionModel.getInstruction(),readingContentPath+questionModel.getInstructionUrl());
    }
}