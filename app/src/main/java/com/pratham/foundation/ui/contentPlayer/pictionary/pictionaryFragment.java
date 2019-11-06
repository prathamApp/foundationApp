package com.pratham.foundation.ui.contentPlayer.pictionary;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.utility.FC_Utility.showZoomDialog;


public class pictionaryFragment extends Fragment implements OnGameClose {

    @BindView(R.id.tv_question)
    TextView question;
    @BindView(R.id.iv_question_image)
    ImageView questionImage;
    @BindView(R.id.iv_question_gif)
    GifView questionGif;
    @BindView(R.id.rg_mcq)
    RadioGroup radioGroupMcq;
    @BindView(R.id.grid_mcq)
    GridLayout gridMcq;


    @BindView(R.id.previous)
    SansButton previous;
    @BindView(R.id.submitBtn)
    TextView submitBtn;
    @BindView(R.id.next)
    TextView next;

    private String readingContentPath, contentPath, contentTitle, StudentID, resId, resStartTime;
    private int totalWordCount, learntWordCount;
    List<ScienceQuestionChoice> options;
    private List<ScienceQuestion> selectedFive;
    private List<ScienceQuestion> dataList;
    private static final String POS = "pos";
    private static final String SCIENCE_QUESTION = "scienceQuestion";

    private int imgCnt = 0, textCnt = 0, index = 0;
    private ScienceQuestion scienceQuestion;
    private boolean onSdCard, isTest = false;
    private float perc;
    private List<ScienceQuestionChoice> correctWordList, wrongWordList;

    public pictionaryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            contentPath = getArguments().getString("contentPath");
            StudentID = getArguments().getString("StudentID");
            resId = getArguments().getString("resId");
            contentTitle = getArguments().getString("contentName");
            onSdCard = getArguments().getBoolean("onSdCard", false);
            if (onSdCard)
                readingContentPath = ApplicationClass.contentSDPath + "/.FCA/English/Game/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + "/.FCA/English/Game/" + contentPath + "/";

            resStartTime = FC_Utility.getCurrentDateTime();
            addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.READINGGAME + " " + GameConstatnts.START);

            getData();
        }
    }

    private void getData() {
        try {
            InputStream is = new FileInputStream(readingContentPath + "ShowMeAndroid.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONArray jsonObj = new JSONArray(jsonStr);

            // List instrumentNames = new ArrayList<>();
            Gson gson = new Gson();
            Type type = new TypeToken<List<ScienceQuestion>>() {
            }.getType();
            dataList = gson.fromJson(jsonObj.toString(), type);
            getDataList();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDataList() {
        try {
            selectedFive = new ArrayList<ScienceQuestion>();
            perc = getPercentage();
            Collections.shuffle(dataList);
            for (int i = 0; i < dataList.size(); i++) {
                if (perc < 95) {
                    if (!checkWord("" + dataList.get(i).getAnswer()))
                        selectedFive.add(dataList.get(i));
                } else {
                    selectedFive.add(dataList.get(i));
                }
                if (selectedFive.size() >= 5) {
                    break;
                }
            }
            Collections.shuffle(selectedFive);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getPercentage() {
        float perc = 0f;
        try {
            totalWordCount = dataList.size();
            learntWordCount = getLearntWordsCount();
            if (learntWordCount > 0) {
                perc = ((float) learntWordCount / (float) totalWordCount) * 100;
                return perc;
            } else
                return 0f;
        } catch (Exception e) {
            return 0f;
        }
    }

    private int getLearntWordsCount() {
        int count = 0;
        count = appDatabase.getKeyWordDao().checkWordCount(FC_Constants.currentStudentID, resId);
        return count;
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = appDatabase.getKeyWordDao().checkWord(FC_Constants.currentStudentID, resId, wordStr);
            if (word != null)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_mcq_fill_in_the_blanks_with_options_row, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setMcqsQuestion();
    }

    public void setMcqsQuestion() {
        if (selectedFive != null) {
            options = new ArrayList<>();
            question.setText(selectedFive.get(index).getQuestion());
            if (!selectedFive.get(index).getPhotourl().equalsIgnoreCase("")) {
                questionImage.setVisibility(View.VISIBLE);
//            if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {

                String fileName = selectedFive.get(index).getPhotourl();
                final String localPath = readingContentPath + "/" + fileName;


                String path = selectedFive.get(index).getPhotourl();
                String[] imgPath = path.split("\\.");
                int len;
                if (imgPath.length > 0)
                    len = imgPath.length - 1;
                else len = 0;
                if (imgPath[len].equalsIgnoreCase("gif")) {
                    try {
                        InputStream gif;
                   /* if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
                        Glide.with(getActivity()).asGif()
                                .load(path)
                                .apply(new RequestOptions()
                                        .placeholder(Drawable.createFromPath(localPath)))
                                .into(questionImage);
                    } else {*/
                        gif = new FileInputStream(localPath);
                        questionImage.setVisibility(View.GONE);
                        questionGif.setVisibility(View.VISIBLE);
                        questionGif.setGifResource(gif);
                        //  }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

           /*     Glide.with(getActivity()).asGif()
                        .load(path)
                        .apply(new RequestOptions()
                                .placeholder(Drawable.createFromPath(localPath)))
                        .into(questionImage);*/
//                    zoomImg.setVisibility(View.VISIBLE);
                } else {
                    Glide.with(getActivity())
                            .load(path)
                            .apply(new RequestOptions()
                                    .placeholder(Drawable.createFromPath(localPath)))
                            .into(questionImage);
                }

                questionImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showZoomDialog(getActivity(), selectedFive.get(index).getPhotourl(), localPath);
                    }
                });
                questionGif.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showZoomDialog(getActivity(), selectedFive.get(index).getPhotourl(), localPath);
                    }
                });
           /* } else {
                String fileName = Assessment_Utility.getFileName(scienceQuestion.getQid(), scienceQuestion.getPhotourl());
                final String localPath = AssessmentApplication.assessPath + Assessment_Constants.STORE_DOWNLOADED_MEDIA_PATH + "/" + fileName;
                setImage(questionImage, localPath);
                questionImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showZoomDialog(localPath);
                    }
                });
            }*/
            } else questionImage.setVisibility(View.GONE);

            options.clear();
            options = selectedFive.get(index).getLstquestionchoice();
            imgCnt = 0;
            textCnt = 0;
            if (options != null) {
                radioGroupMcq.removeAllViews();
                gridMcq.removeAllViews();

                for (int r = 0; r < options.size(); r++) {
                    if (!options.get(r).getSubUrl().equalsIgnoreCase("")) {
                        imgCnt++;
                    }
                    if (!options.get(r).getSubQues().equalsIgnoreCase("")) {
                        textCnt++;
                    }

                }
                for (int r = 0; r < options.size(); r++) {

                    String ans = "$";
                    if (!selectedFive.get(index).getUserAnswer().equalsIgnoreCase(""))
                        ans = selectedFive.get(index).getUserAnswer();
                    String ansId = selectedFive.get(index).getUserAnswer();

                    if (textCnt == options.size()) {
                        if (options.get(r).getSubUrl().equalsIgnoreCase("")) {
                            radioGroupMcq.setVisibility(View.VISIBLE);
                            gridMcq.setVisibility(View.GONE);

                            final View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_mcq_radio_item, radioGroupMcq, false);
                            final RadioButton radioButton = (RadioButton) view;
                            // radioButton.setButtonTintList(Assessment_Utility.colorStateList);
                            radioButton.setId(r);
                            radioButton.setElevation(3);

                       /* if (!options.get(r).getChoiceurl().equalsIgnoreCase("")) {
                            final String path = options.get(r).getChoiceurl();
                            radioButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showZoomDialog(path);
                                }
                            });
                            radioGroupMcq.addView(radioButton);
//                            radioButton.setText("View Option " + (r + 1));
//                    radioButton.setText(options.get(r).getChoicename());
                            if (scienceQuestion.getUserAnswerId().equalsIgnoreCase(options.get(r).getQcid())) {
                                radioButton.setChecked(true);
                                radioButton.setTextColor(Assessment_Utility.selectedColor);
                            } else {
                                radioButton.setChecked(false);
                                radioButton.setTextColor(Color.WHITE);
                            }
                        } else {*/
                            radioButton.setText(options.get(r).getSubQues());
                            //   Log.d("tag111", "a" + selectedFive.get(index).getUserAnswer() + "  B" + options.get(r).getQid());
                            if (selectedFive.get(index).getUserAnswer().equalsIgnoreCase(options.get(r).getQid())) {

                                radioButton.setChecked(true);
                                // radioButton.setTextColor(Assessment_Utility.selectedColor);
                            } else {
                                radioButton.setChecked(false);
                                // radioButton.setTextColor(Color.WHITE);
                            }
                            radioGroupMcq.addView(radioButton);
                            if (ans.equals(options.get(r).getSubQues())) {
                                radioButton.setChecked(true);
                            } else {
                                radioButton.setChecked(false);
                            }
//                        }
                        }
                    } else if (imgCnt == options.size()) {
                        radioGroupMcq.setVisibility(View.GONE);
                        gridMcq.setVisibility(View.VISIBLE);
                        String fileName = options.get(r).getSubUrl();
//                String localPath = Environment.getExternalStorageDirectory() + Assessment_Constants.STORE_DOWNLOADED_MEDIA_PATH + "/" + fileName;
                        String localPath = readingContentPath + fileName;

                        String path = options.get(r).getSubUrl();

                        String[] imgPath = path.split("\\.");
                        int len;
                        if (imgPath.length > 0)
                            len = imgPath.length - 1;
                        else len = 0;
                  /*  final GifView gifView;
                    ImageView imageView = null;*/

                        final String imageUrl = options.get(r).getSubUrl();
                        final View view;
                        final RelativeLayout rl_mcq;
                        View viewRoot;
                        final ImageView tick;


                        if (imgPath[len].equalsIgnoreCase("gif")) {
                            viewRoot = LayoutInflater.from(getActivity()).inflate(R.layout.layout_mcq_gif_item, gridMcq, false);
                            view = viewRoot.findViewById(R.id.mcq_gif);
                            rl_mcq = viewRoot.findViewById(R.id.rl_mcq);
                            tick = viewRoot.findViewById(R.id.iv_tick);
                        /*  setImage(view, imageUrl, localPath);
                        gridMcq.addView(view);*/
                        } else {
                            viewRoot = LayoutInflater.from(getActivity()).inflate(R.layout.layout_mcq_card_image_item, gridMcq, false);
                            view = viewRoot.findViewById(R.id.mcq_img);
                            rl_mcq = viewRoot.findViewById(R.id.rl_mcq);
                            tick = viewRoot.findViewById(R.id.iv_tick);
/*setImage(view, imageUrl, localPath);
                        gridMcq.addView(view);*/
//                        if (scienceQuestion.getUserAnswerId().equalsIgnoreCase(options.get(r).getQcid())) {
//                            view.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_edit_text));
//                        } else {
//                            view.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));
//
//                        }

                        }
                        final int finalR = r;
//                    final ImageView finalImageView = imageView;
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (int g = 0; g < gridMcq.getChildCount(); g++) {
                                    gridMcq.getChildAt(g).setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));
                                    ((CardView) ((RelativeLayout) gridMcq.getChildAt(g)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
                                }
                                rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_edit_text));
                                tick.setVisibility(View.VISIBLE);
                                String fileName = options.get(finalR).getSubUrl();
                                String localPath = readingContentPath + fileName;


//                            if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
                                // showZoomDialog(getActivity(), options.get(finalR).getSubUrl(), localPath);
                            /*} else {
                                 showZoomDialog(localPath);
                            }*/
                                List<ScienceQuestionChoice> ans = new ArrayList<>();
                                ans.add(options.get(finalR));
                                //todo
                                selectedFive.get(index).setUserAnswer(options.get(finalR).getQid());
                                selectedFive.get(index).setStartTime(FC_Utility.getCurrentDateTime());
                                selectedFive.get(index).setEndTime(FC_Utility.getCurrentDateTime());
                            }
                        });

                        if (selectedFive.get(index).getUserAnswer().equalsIgnoreCase(options.get(r).getQid())) {
                            rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_edit_text));
                            tick.setVisibility(View.VISIBLE);

                        } else {
                            rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));
                            tick.setVisibility(View.GONE);

                        }
                        setImage(view, imageUrl, localPath);
                        gridMcq.addView(viewRoot);

                    } else {
                        gridMcq.setColumnCount(2);
                        final int finalR1 = r;
                        if (!options.get(r).getSubUrl().equalsIgnoreCase("")) {

//                        final String imageUrl = options.get(r).getChoiceurl();
                            final View view;
                            final RelativeLayout rl_mcq;
                            View viewRoot;
                            final ImageView tick;

                            String path = options.get(r).getSubUrl();

                            String[] imgPath = path.split("\\.");
                            int len;
                            if (imgPath.length > 0)
                                len = imgPath.length - 1;
                            else len = 0;


                            if (imgPath[len].equalsIgnoreCase("gif")) {
                                viewRoot = LayoutInflater.from(getActivity()).inflate(R.layout.layout_mcq_gif_item, gridMcq, false);
                                view = viewRoot.findViewById(R.id.mcq_gif);
                                rl_mcq = viewRoot.findViewById(R.id.rl_mcq);
                                tick = viewRoot.findViewById(R.id.iv_tick);

                        /*  setImage(view, imageUrl, localPath);
                        gridMcq.addView(view);*/
                            } else {
                                viewRoot = LayoutInflater.from(getActivity()).inflate(R.layout.layout_mcq_card_image_item, gridMcq, false);
                                view = viewRoot.findViewById(R.id.mcq_img);
                                rl_mcq = viewRoot.findViewById(R.id.rl_mcq);
                                tick = viewRoot.findViewById(R.id.iv_tick);
/*setImage(view, imageUrl, localPath);
                        gridMcq.addView(view);*/
//                        if (scienceQuestion.getUserAnswerId().equalsIgnoreCase(options.get(r).getQcid())) {
//                            view.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_edit_text));
//                        } else {
//                            view.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));
//
//                        }

                            }


//                        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_mcq_image_item, gridMcq, false);

                            String fileName = options.get(r).getSubUrl();
//                String localPath = Environment.getExternalStorageDirectory() + Assessment_Constants.STORE_DOWNLOADED_MEDIA_PATH + "/" + fileName;
                            String localPath = readingContentPath + "/images/" + fileName;

//                        final ImageView imageView = (ImageView) view;
//                        if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
                            final String imageUrl = options.get(r).getSubUrl();
                            setImage(view, imageUrl, localPath);

                            gridMcq.addView(viewRoot);

                            if (selectedFive.get(index).getUserAnswer().equalsIgnoreCase(options.get(r).getQid())) {
                                rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_edit_text));
                                tick.setVisibility(View.VISIBLE);

                            } else {
                                rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));
                                tick.setVisibility(View.GONE);
                            }

                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setOnclickOnItem(v, options.get(finalR1));
                                    String fileName = options.get(finalR1).getSubUrl();
                                    String localPath = readingContentPath + "/images/" + fileName;
                                    tick.setVisibility(View.VISIBLE);

                                    rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_edit_text));
//                                if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
                                    showZoomDialog(getActivity(), options.get(finalR1).getSubUrl(), localPath);

                               /* } else {
                                    showZoomDialog(localPath);
                                }*/
                                }
                            });
                        } else {
                            final View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_mcq_single_text_item, gridMcq, false);
                            final TextView textView = (TextView) view;
                            textView.setElevation(3);
                            textView.setText(options.get(r).getSubQues());
                            gridMcq.addView(textView);
                            if (selectedFive.get(index).getUserAnswer().equalsIgnoreCase(options.get(r).getQid())) {
                                // textView.setTextColor(Assessment_Utility.selectedColor);
                                textView.setBackground(getActivity().getResources().getDrawable(R.drawable.gradient_selector));
                            } else {
                                // textView.setTextColor(Color.WHITE);
                                textView.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));

                            }
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setOnclickOnItem(v, options.get(finalR1));

                                    textView.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_edit_text));
                                    //  textView.setTextColor(Assessment_Utility.selectedColor);
                                }
                            });
                        }


                    }

                }
            }
            radioGroupMcq.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    //((RadioButton) radioGroupMcq.getChildAt(checkedId)).setChecked(true);
                    RadioButton rb = group.findViewById(checkedId);
                    if (rb != null) {
                        rb.setChecked(true);
                        //   rb.setTextColor(Assessment_Utility.selectedColor);
                    }

                    for (int i = 0; i < group.getChildCount(); i++) {
                        if ((group.getChildAt(i)).getId() == checkedId) {
                            //  ((RadioButton) group.getChildAt(i)).setTextColor(Assessment_Utility.selectedColor);

                            List<ScienceQuestionChoice> ans = new ArrayList<>();
                            ans.add(options.get(i));
                            //todo
                            selectedFive.get(index).setUserAnswer(options.get(i).getQid());
                            selectedFive.get(index).setStartTime(FC_Utility.getCurrentDateTime());
                            selectedFive.get(index).setEndTime(FC_Utility.getCurrentDateTime());
                        } else {
                            // ((RadioButton) group.getChildAt(i)).setTextColor(getActivity().getResources().getColor(R.color.white));
                        }
                    }
                }
            });
            if (index == 0) {
                previous.setVisibility(View.INVISIBLE);
            } else {
                previous.setVisibility(View.VISIBLE);
            }
            if (index == (selectedFive.size() - 1)) {
                submitBtn.setVisibility(View.VISIBLE);
                next.setVisibility(View.INVISIBLE);
            } else {
                submitBtn.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
            }


        } else {
            Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
        }


    }


    private void setOnclickOnItem(View v, ScienceQuestionChoice scienceQuestionChoice) {
        /*for (int g = 0; g < gridMcq.getChildCount(); g++) {
          //  gridMcq.getChildAt(g).setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));
            //View view = gridMcq.getChildAt(g);
           *//* if (view instanceof TextView)
                ((TextView) view).setTextColor(Color.WHITE);*//*
        }*/

        List<ScienceQuestionChoice> ans = new ArrayList<>();
        ans.add(scienceQuestionChoice);
        //todo
        selectedFive.get(index).setUserAnswer(scienceQuestionChoice.getQid());
        selectedFive.get(index).setStartTime(FC_Utility.getCurrentDateTime());
        selectedFive.get(index).setEndTime(FC_Utility.getCurrentDateTime());

    }

    private void setImage(View view, final String choiceurl, String placeholderTemp) {
//        if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
        String path = choiceurl.replace(" ", "");
        String placeholder = placeholderTemp.replace(" ", "");
        String[] imgPath = path.split("\\.");
        int len;
        if (imgPath.length > 0)
            len = imgPath.length - 1;
        else len = 0;


        if (imgPath[len].equalsIgnoreCase("gif")) {

            try {
                GifView gifView = (GifView) view;
                InputStream gif = new FileInputStream(placeholder);
                gifView.setGifResource(gif);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    /*        Glide.with(getActivity()).asGif()
                    .load(path)
                    .apply(new RequestOptions()
                            .placeholder(Drawable.createFromPath(placeholder)))
                    .into(imageView);*/
        } else {
            ImageView imageView = (ImageView) view;
            Glide.with(getActivity())
                    .load(path)
                    .apply(new RequestOptions()
                            .placeholder(Drawable.createFromPath(placeholder)))
                    .into(imageView);
        }


//        }
    }

    @OnClick(R.id.previous)
    public void onPreviousClick() {
        if (selectedFive != null)
            if (index > 0) {
                index--;
                setMcqsQuestion();
            }
    }

    @OnClick(R.id.next)
    public void onNextClick() {
        if (selectedFive != null)
            if (index < (selectedFive.size() - 1)) {
                index++;
                setMcqsQuestion();
            }
    }

    @OnClick(R.id.submitBtn)
    public void onsubmitBtnClick() {
        if (selectedFive != null)
            addLearntWords(selectedFive);
        //  GameConstatnts.playGameNext(getActivity());
        /*Bundle bundle = GameConstatnts.findGameData("110");
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new FillInTheBlanksFragment(), R.id.RL_CPA,
                    bundle, FillInTheBlanksFragment.class.getSimpleName());
        }*/

    }

    public void addLearntWords(List<ScienceQuestion> selectedAnsList) {
        int correctCnt = 0;
        correctWordList = new ArrayList<>();
        wrongWordList = new ArrayList<>();
        if (selectedAnsList != null && !selectedAnsList.isEmpty()) {
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (checkAnswer(selectedAnsList.get(i))) {
                    correctCnt++;
                    KeyWords keyWords = new KeyWords();
                    keyWords.setResourceId(resId);
                    keyWords.setSentFlag(0);
                    keyWords.setStudentId(FC_Constants.currentStudentID);
                    String key = selectedAnsList.get(i).getUserAnswer();
                    keyWords.setKeyWord(key);
                    keyWords.setWordType("word");
                    appDatabase.getKeyWordDao().insert(keyWords);
                    List<ScienceQuestionChoice> tempOptionList = selectedAnsList.get(i).getLstquestionchoice();
                    for (int k = 0; k < tempOptionList.size(); k++) {
                        if (tempOptionList.get(k).getQid().equalsIgnoreCase(selectedAnsList.get(i).getUserAnswer())) {
                            correctWordList.add(tempOptionList.get(k));
                        }
                    }

                    addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid().trim()), GameConstatnts.READINGGAME, 10, 10, selectedAnsList.get(i).getStartTime(), selectedAnsList.get(i).getEndTime(), selectedAnsList.get(i).getUserAnswer());
                } else {
                    if (selectedAnsList.get(i).getUserAnswer() != null && !selectedAnsList.get(i).getUserAnswer().trim().equalsIgnoreCase("")) {
                        List<ScienceQuestionChoice> tempOptionList = selectedAnsList.get(i).getLstquestionchoice();
                        for (int k = 0; k < tempOptionList.size(); k++) {
                            if (tempOptionList.get(k).getQid().equalsIgnoreCase(selectedAnsList.get(i).getUserAnswer())) {
                                wrongWordList.add(tempOptionList.get(k));
                            }
                        }
                        addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid().trim()), GameConstatnts.READINGGAME, 0, 10, selectedAnsList.get(i).getStartTime(), selectedAnsList.get(i).getEndTime(), selectedAnsList.get(i).getUserAnswer());
                    }
                }
            }
            if (!FC_Constants.isTest) {
                showResult(correctWordList, wrongWordList);
            }
        } else {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.TRUE, (OnGameClose) this);
        }
        BackupDatabase.backup(getActivity());
    }

    private boolean checkAnswer(ScienceQuestion scienceQuestion) {
        List<ScienceQuestionChoice> optionListlist = scienceQuestion.getLstquestionchoice();
        for (int i = 0; i < optionListlist.size(); i++) {
            if (optionListlist.get(i).getQid().equalsIgnoreCase(scienceQuestion.getUserAnswer()) && optionListlist.get(i).getCorrectAnswer().equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }


    private void showResult(List<ScienceQuestionChoice> correctWord, List<ScienceQuestionChoice> wrongWord) {
        if ((correctWord != null && !correctWord.isEmpty()) || (wrongWord != null && !wrongWord.isEmpty())) {

            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.show_result_pictionary);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            resultAdapter resultAdapter = new resultAdapter(correctWord, getContext(), readingContentPath);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            RecyclerView correct_keywords = dialog.findViewById(R.id.correct_keywords);
            correct_keywords.setLayoutManager(linearLayoutManager);
            correct_keywords.setAdapter(resultAdapter);
            LinearLayoutManager wrongLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            resultAdapter wrongResultAdapter = new resultAdapter(wrongWord, getContext(), readingContentPath);
            RecyclerView wrong_keywords = dialog.findViewById(R.id.wrong_keywords);
            wrong_keywords.setLayoutManager(wrongLayoutManager);
            wrong_keywords.setAdapter(wrongResultAdapter);
            SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);

            // correct_keywords.setText(correctWord.toString().substring(1, correctWord.toString().length() - 1));
            // wrong_keywords.setText(wrongWord.toString().substring(1, wrongWord.toString().length() - 1));
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

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String resEndTime, String Label) {
        try {
            String deviceId = appDatabase.getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FC_Constants.currentSession);
            score.setResourceID(resId);
            score.setQuestionId(wID);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FC_Constants.currentStudentID);
            score.setStartDateTime(resStartTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(resEndTime);
            score.setLevel(FC_Constants.currentLevel);
            score.setLabel(Word + " - " + Label);
            score.setSentFlag(0);
            appDatabase.getScoreDao().insert(score);

            if (FC_Constants.isTest) {
                Assessment assessment = new Assessment();
                assessment.setResourceIDa(resId);
                assessment.setSessionIDa(FC_Constants.assessmentSession);
                assessment.setSessionIDm(FC_Constants.currentSession);
                assessment.setQuestionIda(wID);
                assessment.setScoredMarksa(scoredMarks);
                assessment.setTotalMarksa(totalMarks);
                assessment.setStudentIDa(FC_Constants.currentAssessmentStudentID);
                assessment.setStartDateTimea(resStartTime);
                assessment.setDeviceIDa(deviceId.equals(null) ? "0000" : deviceId);
                assessment.setEndDateTime(resEndTime);
                assessment.setLevela(FC_Constants.currentLevel);
                assessment.setLabel("test: " + Label);
                assessment.setSentFlag(0);
                appDatabase.getAssessmentDao().insert(assessment);
            }
            BackupDatabase.backup(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gameClose() {
        addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.READINGGAME + " " + GameConstatnts.END);
    }
}

