package com.pratham.foundation.ui.contentPlayer.keywords_identification;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pratham.foundation.R;
import com.nex3z.flowlayout.FlowLayout;
import com.pratham.foundation.custumView.SansTextView;
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

    RelativeLayout.LayoutParams viewParam;
    HashMap<String, List<Integer>> positionMap;
    private List<String> selectedKeywords;

    @AfterViews
    public void initiate() {

        presenter.setView(KeywordsIdentificationFragment.this);
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
    public void showParagraph(QuestionModel questionModel) {
        String[] paragraphWords = questionModel.getParagraph().split(" ");
        for (int i = 0; i < paragraphWords.length; i++) {
            if (positionMap.containsKey(paragraphWords[i])) {
                List temp = positionMap.get(paragraphWords[i]);
                temp.add(i);
            } else {
                List<Integer> temp = new ArrayList<>();
                temp.add(i);
                positionMap.put(paragraphWords[i], temp);
            }

            final SansTextView textView = new SansTextView(getActivity());
            textView.setTextSize(30);
            textView.setText(paragraphWords[i]);
            final int temp_i = i;
            textView.setOnClickListener(v -> paragraphWordClicked("" + textView.getText(), temp_i));
            paraghaph.addView(textView);
        }

    }

    private void paragraphWordClicked(String paraText, int pos) {
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
    }

    @Click(R.id.btn_submit)
    public void submitClicked(){
        FC_Utility.showFragment(getActivity(), new KeywordMappingFragment_(), R.id.RL_CPA,
                null, KeywordMappingFragment_.class.getSimpleName());

    }
}
