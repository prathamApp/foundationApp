package com.pratham.foundation.ui.contentPlayer.keywords_identification;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.nex3z.flowlayout.FlowLayout;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.customView.SansTextViewBold;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.ContentPlayerActivity;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;
import com.pratham.foundation.ui.contentPlayer.keywords_mapping.KeywordMappingFragment_;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@EFragment(R.layout.fragment_keywords_identification)
public class KeywordsIdentificationFragment extends Fragment implements KeywordsIdentificationContract.KeywordsView {

    @Bean(KeywordsIdentificationPresenter.class)
    KeywordsIdentificationContract.KeywordsPresenter presenter;

    @ViewById(R.id.paragraph)
    FlowLayout paraghaph;
    @ViewById(R.id.keywords)
    FlowLayout keywords;
    @ViewById(R.id.keyword_selected)
    RelativeLayout keyword_selected;
    @ViewById(R.id.show_me_keywords)
    com.pratham.foundation.customView.fonts.SansButton show_me_keywords;

    RelativeLayout.LayoutParams viewParam;
    private HashMap<String, List<Integer>> positionMap;
    private List<String> selectedKeywords;
    private String contentPath, contentTitle, StudentID, resId,readingContentPath;
    private boolean onSdCard;
    private ScienceQuestion questionModel;
    private boolean isKeyWordShowing = false;


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
                readingContentPath = ApplicationClass.contentSDPath + "/.FCA/English/Game/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + "/.FCA/English/Game/" + contentPath + "/";
        }
        presenter.setView(KeywordsIdentificationFragment.this, resId,readingContentPath);
        selectedKeywords = new ArrayList<>();
        positionMap = new HashMap<>();

        viewParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 5, 10, 5);

        presenter.getData();
    }

    @Override
    public void showParagraph(ScienceQuestion questionModel) {
        this.questionModel = questionModel;
        String[] paragraphWords = questionModel.getQuestion().split(" ");
        for (int i = 0; i < paragraphWords.length; i++) {
            if (positionMap.containsKey(paragraphWords[i].replaceAll("[^a-zA-Z0-9]", ""))) {
                List temp = positionMap.get(paragraphWords[i]);
                temp.add(i);
            } else {
                List<Integer> temp = new ArrayList<>();
                temp.add(i);
                positionMap.put(paragraphWords[i].replaceAll("[^a-zA-Z0-9]", ""), temp);
            }

            final SansTextView textView = new SansTextView(getActivity());
            textView.setTextSize(30);
            textView.setText(paragraphWords[i]);
            final int temp_i = i;
            textView.setOnClickListener(v -> paragraphWordClicked("" + textView.getText().toString().replaceAll("[^a-zA-Z0-9]", ""), temp_i));
            paraghaph.addView(textView);
        }

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

    private void paragraphWordClicked(String paraText, int pos) {
        if (!isKeyWordShowing) {
           int correctCnt= getCorrectCnt(questionModel.getLstquestionchoice());
            if (selectedKeywords.size() <= correctCnt) {
                if (!selectedKeywords.contains(paraText)) {
                    List positions = positionMap.get(paraText.trim());
                    if (positions != null)
                        for (int i = 0; i < positions.size(); i++) {
                            SansTextView textViewTemp = (SansTextView) paraghaph.getChildAt((int) positions.get(i));
                            textViewTemp.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGreenCorrect));
                        }

                    LinearLayout linearLayout = new LinearLayout(getActivity());
                    viewParam.setMargins(5, 8, 5, 8);
                    linearLayout.setLayoutParams(viewParam);
                    linearLayout.setGravity(Gravity.CENTER);
                    linearLayout.setElevation(3);
                    linearLayout.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dialog_bg));

                    SansTextView textViewkey = new SansTextView(getActivity());
                    textViewkey.setText(paraText);
                    textViewkey.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                    textViewkey.setId(pos);
                    textViewkey.setPadding(10, 5, 5, 5);
                    textViewkey.setTextSize(25);
                    linearLayout.addView(textViewkey);

                    final ImageView imageView = new ImageView(getActivity());
                    imageView.setId(pos);
                    imageView.setTag(paraText);
                    imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_close));
                    imageView.setPadding(5, 3, 10, 3);

                    linearLayout.addView(imageView);

                    keywords.addView(linearLayout);

                    selectedKeywords.add(paraText);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String temp = imageView.getTag().toString();
                            if (selectedKeywords.contains(temp)) {
                                selectedKeywords.remove(temp);
                                List positions = positionMap.get(paraText.trim());
                                for (int i = 0; i < positions.size(); i++) {
                                    SansTextView textViewTemp = (SansTextView) paraghaph.getChildAt((int) positions.get(i));
                                    textViewTemp.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                                }
                                View view = (View) imageView.getParent();
                                keywords.removeView(view);

                            } else {
                                Toast.makeText(getActivity(), "Somthing went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } else {
                Toast.makeText(getActivity(), "Upper limit reached", Toast.LENGTH_SHORT).show();
            }
        } else {
            ((ContentPlayerActivity) getActivity()).ttsService.play(paraText);
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
        presenter.addLearntWords(selectedKeywords);
        Bundle bundle = GameConstatnts.findGameData("103");
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new KeywordMappingFragment_(), R.id.RL_CPA,
                    bundle, KeywordMappingFragment_.class.getSimpleName());
        }
    }

    @Click(R.id.show_me_keywords)
    public void show_me_keywords() {
        List<ScienceQuestionChoice> keywordList = questionModel.getLstquestionchoice();
        positionMap.clear();
        selectedKeywords.clear();
        paraghaph.removeAllViews();
        keywords.removeAllViews();
        showParagraph(questionModel);
        if (!isKeyWordShowing) {
            isKeyWordShowing = true;
            show_me_keywords.setText("hide keywords");
            for (int keywordIndex = 0; keywordIndex < keywordList.size(); keywordIndex++) {
                List positions = positionMap.get(keywordList.get(keywordIndex).getSubQues().trim());
                if (positions != null)
                    for (int i = 0; i < positions.size(); i++) {
                        SansTextView textViewTemp = (SansTextView) paraghaph.getChildAt((int) positions.get(i));
                        textViewTemp.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGreenCorrect));
                    }
            }
        } else {
            isKeyWordShowing = false;
            show_me_keywords.setText("show keywords");
            for (int keywordIndex = 0; keywordIndex < keywordList.size(); keywordIndex++) {
                List positions = positionMap.get(keywordList.get(keywordIndex).getSubQues().trim());
                if (positions != null)
                    for (int i = 0; i < positions.size(); i++) {
                        SansTextView textViewTemp = (SansTextView) paraghaph.getChildAt((int) positions.get(i));
                        textViewTemp.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                    }
            }
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.app_success_dialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
            TextView message = dialog.findViewById(R.id.message);
            message.setText("Now its your turn to select keyword");
            dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }
    }
}