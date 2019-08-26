package com.example.foundationapp.identifyKeywords;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foundationapp.R;
import com.example.foundationapp.custumView.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class IdentifyKeywordsActivity extends AppCompatActivity implements IdentifyKeywordsActivityController.View {
    private IdentifyKeywordsActivityPresenter identifyKeywordsActivityPresenter;
    private Context context;

    private List<String> selectedKeywords;

    @BindView(R.id.paragraph)
    FlowLayout paraghaph;

    @BindView(R.id.keywords)
    FlowLayout keywords;

    RelativeLayout.LayoutParams viewParam;
    HashMap<String, List<Integer>> positionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_keywords);
        ButterKnife.bind(this);
        context = IdentifyKeywordsActivity.this;
        selectedKeywords = new ArrayList<>();
        identifyKeywordsActivityPresenter = new IdentifyKeywordsActivityPresenter(context);
        positionMap = new HashMap<>();

        viewParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 5, 10, 5);

        identifyKeywordsActivityPresenter.getData();
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

            final TextView textView = new TextView(context);
            textView.setTextSize(30);
            textView.setPadding(5, 5, 5, 5);
            textView.setText(paragraphWords[i] + " ");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!selectedKeywords.contains(textView.getText().toString())) {
                        String to = textView.getText().toString();

                        List positions = positionMap.get(to.trim());
                        if (positions != null)
                            for (int i = 0; i < positions.size(); i++) {
                                TextView textViewTemp = (TextView) paraghaph.getChildAt((int) positions.get(i));
                                textViewTemp.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            }

                        RelativeLayout relativeLayout = new RelativeLayout(context);
                        viewParam.setMargins(10, 5, 10, 5);
                        relativeLayout.setLayoutParams(viewParam);
                        TextView textViewkey = new TextView(context);
                        textViewkey.setText(textView.getText());
                        relativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_border));
                        textViewkey.setPadding(10, 3, 10, 3);
                        textViewkey.setTextSize(30);

                        final ImageView imageView = new ImageView(context);
                        imageView.setId(1);
                        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close));
                        imageView.setPadding(10, 3, 10, 3);
                        imageView.setTag(textView.getText());
                        relativeLayout.addView(imageView);
                        viewParam.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
                        textViewkey.setLayoutParams(viewParam);
                        relativeLayout.addView(textViewkey);
                        selectedKeywords.add(textView.getText().toString());
                        keywords.addView(relativeLayout);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String temp = imageView.getTag().toString();
                                if (selectedKeywords.contains(temp)) {
                                    selectedKeywords.remove(temp);

                                    String to = textView.getText().toString();
                                    List positions = positionMap.get(to.trim());
                                    for (int i = 0; i < positions.size(); i++) {
                                        TextView textViewTemp = (TextView) paraghaph.getChildAt((int) positions.get(i));
                                        textViewTemp.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                                    }
                                    View view = (View) imageView.getParent();
                                    keywords.removeView(view);

                                } else {
                                    Toast.makeText(IdentifyKeywordsActivity.this, "Somthing went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
            paraghaph.addView(textView);
        }

    }
}
