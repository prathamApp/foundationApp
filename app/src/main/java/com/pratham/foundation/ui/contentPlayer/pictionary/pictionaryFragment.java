package com.pratham.foundation.ui.contentPlayer.pictionary;

import android.app.Dialog;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageButton;
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
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
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


    @BindView(R.id.btn_prev)
    ImageButton previous;
    @BindView(R.id.btn_submit)
    SansButton submitBtn;
    @BindView(R.id.btn_next)
    ImageButton next;

    @BindView(R.id.show_answer)
    SansButton show_answer;

    @BindView(R.id.image_container)
    RelativeLayout image_container;

    @BindView(R.id.iv_view_img)
    ImageView iv_view_img;

    private String readingContentPath, contentPath, contentTitle, StudentID, resId, resStartTime;
    private int totalWordCount, learntWordCount;
    List<ScienceQuestionChoice> options;
    private ArrayList<ScienceQuestion> selectedFive;
    private List<ScienceQuestion> dataList;


    private int imgCnt = 0, textCnt = 0, index = 0;
    private ScienceQuestion scienceQuestion;
    private boolean onSdCard;
    private float perc;
    private List<ScienceQuestionChoice> correctWordList, wrongWordList;
    private boolean showanswer = false;
    private Animation animFadein;
    View ansview;

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
                readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";

            EventBus.getDefault().register(this);
            animFadein = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                    R.anim.shake);
            resStartTime = FC_Utility.getCurrentDateTime();
            addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.SHOW_ME_ANDROID + " " + GameConstatnts.START);
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

    public void setCompletionPercentage() {
        try {
            totalWordCount = dataList.size();
            learntWordCount = getLearntWordsCount();
            String Label = "resourceProgress";
            if (learntWordCount > 0) {
                perc = ((float) learntWordCount / (float) totalWordCount) * 100;
                addContentProgress(perc, Label);
            } else {
                addContentProgress(0, Label);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addContentProgress(float perc, String label) {
        try {
            ContentProgress contentProgress = new ContentProgress();
            contentProgress.setProgressPercentage("" + perc);
            contentProgress.setResourceId("" + resId);
            contentProgress.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            contentProgress.setStudentId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
            contentProgress.setLabel("" + label);
            contentProgress.setSentFlag(0);
            appDatabase.getContentProgressDao().insert(contentProgress);
        } catch (Exception e) {
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
            for (ScienceQuestion scienceQuestion : selectedFive) {
                ArrayList<ScienceQuestionChoice> list = scienceQuestion.getLstquestionchoice();
                Collections.shuffle(list);
                scienceQuestion.setLstquestionchoice(list);
            }
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
        // count = appDatabase.getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
        count = appDatabase.getKeyWordDao().checkUniqueWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
        return count;
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = appDatabase.getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId, wordStr);
            return word != null;
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
        if (FC_Constants.isTest) {
            show_answer.setVisibility(View.INVISIBLE);
        }
        setMcqsQuestion();
    }

    public void setMcqsQuestion() {
        clerAnimation();
        if (selectedFive != null) {
            options = new ArrayList<>();
            question.setText(selectedFive.get(index).getQuestion());
            if (!selectedFive.get(index).getPhotourl().equalsIgnoreCase("")) {
                questionImage.setVisibility(View.VISIBLE);
                image_container.setVisibility(View.VISIBLE);
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

                image_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showZoomDialog(getActivity(), selectedFive.get(index).getPhotourl(), localPath);
                    }
                });
               /* image_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showZoomDialog(getActivity(), selectedFive.get(index).getPhotourl(), localPath);
                    }
                });*/
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
            } else {
                questionImage.setVisibility(View.GONE);
                image_container.setVisibility(View.GONE);
            }

            options.clear();
            options = selectedFive.get(index).getLstquestionchoice();
            imgCnt = 0;
            textCnt = 0;
            if (options != null) {
                radioGroupMcq.removeAllViews();
                gridMcq.removeAllViews();

                for (int r = 0; r < options.size(); r++) {
                    if (!options.get(r).getSubUrl().trim().equalsIgnoreCase("")) {
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
                        if (options.get(r).getSubUrl().trim().equalsIgnoreCase("")) {
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
                            radioButton.setTextColor(Color.BLACK);
                            radioButton.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));
                            radioButton.setText(options.get(r).getSubQues());
                            if (options.get(r).getCorrectAnswer().equalsIgnoreCase("true")) {
                                ansview = radioButton;
                            }
                            //   Log.d("tag111", "a" + selectedFive.get(index).getUserAnswer() + "  B" + options.get(r).getQid());
                            if (selectedFive.get(index).getUserAnswer().equalsIgnoreCase(options.get(r).getQid())) {
                                radioButton.setChecked(true);
                                radioButton.setTextColor(Color.WHITE);
                                radioButton.setBackground(getActivity().getResources().getDrawable(R.drawable.dialog_bg_blue));

                            } else {
                                radioButton.setChecked(false);
                                radioButton.setTextColor(Color.BLACK);
                                radioButton.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));

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
                        String fileName = options.get(r).getSubUrl().trim();
//                String localPath = Environment.getExternalStorageDirectory() + Assessment_Constants.STORE_DOWNLOADED_MEDIA_PATH + "/" + fileName;
                        String localPath = readingContentPath + fileName;

                        String path = options.get(r).getSubUrl().trim();

                        String[] imgPath = path.split("\\.");
                        int len;
                        if (imgPath.length > 0)
                            len = imgPath.length - 1;
                        else len = 0;
                  /*  final GifView gifView;
                    ImageView imageView = null;*/

                        final String imageUrl = options.get(r).getSubUrl().trim();
                        final View view;
                        final RelativeLayout rl_mcq;
                        View viewRoot;
                        final ImageView tick;
                        final ImageView zoomImg;

                        if (imgPath[len].equalsIgnoreCase("gif")) {
                            viewRoot = LayoutInflater.from(getActivity()).inflate(R.layout.layout_mcq_gif_item, gridMcq, false);
                            view = viewRoot.findViewById(R.id.mcq_gif);
                            rl_mcq = viewRoot.findViewById(R.id.rl_mcq);
                            tick = viewRoot.findViewById(R.id.iv_tick);
                            zoomImg = viewRoot.findViewById(R.id.iv_view_img);
                        /*  setImage(view, imageUrl, localPath);
                        gridMcq.addView(view);*/
                        } else {
                            viewRoot = LayoutInflater.from(getActivity()).inflate(R.layout.layout_mcq_card_image_item, gridMcq, false);
                            view = viewRoot.findViewById(R.id.mcq_img);
                            rl_mcq = viewRoot.findViewById(R.id.rl_mcq);
                            tick = viewRoot.findViewById(R.id.iv_tick);
                            zoomImg = viewRoot.findViewById(R.id.iv_view_img);
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
                        if (options.get(r).getCorrectAnswer().equalsIgnoreCase("true")) {
                            ansview = viewRoot;
                        }
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (int g = 0; g < gridMcq.getChildCount(); g++) {
                                    gridMcq.getChildAt(g).setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));
                                    ((CardView) ((RelativeLayout) gridMcq.getChildAt(g)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
                                    rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));
                                }
                                //  rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_edit_text));
                                rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_rectangle_stroke_bg));
                                tick.setVisibility(View.VISIBLE);
                                String fileName = options.get(finalR).getSubUrl().trim();
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
                        zoomImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String fileName = options.get(finalR).getSubUrl().trim();
                                String localPath = readingContentPath + fileName;
                                showZoomDialog(getActivity(), options.get(finalR).getSubUrl().trim(), localPath);

                            }
                        });
                        if (selectedFive.get(index).getUserAnswer().equalsIgnoreCase(options.get(r).getQid())) {
                            rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_rectangle_stroke_bg));
                            tick.setVisibility(View.VISIBLE);
                        } else {
                            rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));
                            tick.setVisibility(View.GONE);
                        }
                        //  view.setBackground((getActivity().getResources().getDrawable(R.drawable.rounded_rectangle_stroke_bg)));
                        setImage(view, imageUrl, localPath);

                        gridMcq.addView(viewRoot);

                    } else {
                        gridMcq.setColumnCount(2);
                        final int finalR1 = r;
                        if (!options.get(r).getSubUrl().trim().equalsIgnoreCase("")) {

//                        final String imageUrl = options.get(r).getChoiceurl();
                            final View view;
                            final RelativeLayout rl_mcq;
                            View viewRoot;
                            final ImageView tick;
                            final ImageView zoomImg;
                            String path = options.get(r).getSubUrl().trim();

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
                                zoomImg = viewRoot.findViewById(R.id.iv_view_img);

                        /*  setImage(view, imageUrl, localPath);
                        gridMcq.addView(view);*/
                            } else {
                                viewRoot = LayoutInflater.from(getActivity()).inflate(R.layout.layout_mcq_card_image_item, gridMcq, false);
                                view = viewRoot.findViewById(R.id.mcq_img);
                                rl_mcq = viewRoot.findViewById(R.id.rl_mcq);
                                tick = viewRoot.findViewById(R.id.iv_tick);
                                zoomImg = viewRoot.findViewById(R.id.iv_view_img);
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

                            String fileName = options.get(r).getSubUrl().trim();
//                String localPath = Environment.getExternalStorageDirectory() + Assessment_Constants.STORE_DOWNLOADED_MEDIA_PATH + "/" + fileName;
                            String localPath = readingContentPath + fileName;

//                        final ImageView imageView = (ImageView) view;
//                        if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
                            final String imageUrl = options.get(r).getSubUrl().trim();
                            //  view.setBackground((getActivity().getResources().getDrawable(R.drawable.rounded_rectangle_stroke_bg)));
                            setImage(view, imageUrl, localPath);

                            gridMcq.addView(viewRoot);

                            if (selectedFive.get(index).getUserAnswer().equalsIgnoreCase(options.get(r).getQid())) {
                                rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_edit_text));
                                tick.setVisibility(View.VISIBLE);

                            } else {
                                rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));
                                tick.setVisibility(View.GONE);
                            }
                            if (options.get(r).getCorrectAnswer().equalsIgnoreCase("true")) {
                                ansview = viewRoot;
                            }
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setOnclickOnItem(v, options.get(finalR1));
                                    String fileName = options.get(finalR1).getSubUrl().trim();
                                    String localPath = readingContentPath + fileName;
                                    tick.setVisibility(View.VISIBLE);

                                    rl_mcq.setBackground(getActivity().getResources().getDrawable(R.drawable.custom_edit_text));
//                                if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
                                    //   showZoomDialog(getActivity(), options.get(finalR1).getSubUrl(), localPath);

                               /* } else {
                                    showZoomDialog(localPath);
                                }*/
                                }
                            });
                            zoomImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String fileName = options.get(finalR1).getSubUrl().trim();
                                    String localPath = readingContentPath + fileName;
                                    showZoomDialog(getActivity(), options.get(finalR1).getSubUrl().trim(), localPath);
                                }
                            });
                        } else {
                            final View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_mcq_single_text_item, gridMcq, false);
                            final TextView textView = (TextView) view;
                            textView.setElevation(3);
                            textView.setText(options.get(r).getSubQues());
                            if (options.get(r).getCorrectAnswer().equalsIgnoreCase("true")) {
                                ansview = view;
                            }
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
                        rb.setTextColor(Color.WHITE);
                        rb.setBackground(getActivity().getResources().getDrawable(R.drawable.dialog_bg_blue));

                    }

                    for (int i = 0; i < group.getChildCount(); i++) {
                        if ((group.getChildAt(i)).getId() == checkedId) {
                            ((RadioButton) group.getChildAt(i)).setTextColor(Color.WHITE);
                            group.getChildAt(i).setBackground(getActivity().getResources().getDrawable(R.drawable.dialog_bg_blue));

                            List<ScienceQuestionChoice> ans = new ArrayList<>();
                            ans.add(options.get(i));
                            //todo
                            selectedFive.get(index).setUserAnswer(options.get(i).getQid());
                            selectedFive.get(index).setStartTime(FC_Utility.getCurrentDateTime());
                            selectedFive.get(index).setEndTime(FC_Utility.getCurrentDateTime());
                        } else {
                            ((RadioButton) group.getChildAt(i)).setTextColor(Color.BLACK);
                            group.getChildAt(i).setBackground(getActivity().getResources().getDrawable(R.drawable.custom_radio_button));
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

    @OnClick(R.id.btn_prev)
    public void onPreviousClick() {
        if (selectedFive != null)
            if (index > 0) {
                index--;
                setMcqsQuestion();
            }
    }

    @OnClick(R.id.btn_next)
    public void onNextClick() {
        if (selectedFive != null)
            if (index < (selectedFive.size() - 1)) {
                index++;
                setMcqsQuestion();
            }
    }

    @OnClick(R.id.btn_submit)
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

    private boolean checkAttemptedornot(List<ScienceQuestion> selectedAnsList) {
        if (selectedAnsList != null) {
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (selectedAnsList.get(i).getUserAnswer() != null && !selectedAnsList.get(i).getUserAnswer().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addLearntWords(ArrayList<ScienceQuestion> selectedAnsList) {
        int correctCnt = 0;
        correctWordList = new ArrayList<>();
        wrongWordList = new ArrayList<>();
        if (selectedAnsList != null && checkAttemptedornot(selectedAnsList)) {
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (checkAnswer(selectedAnsList.get(i))) {
                    correctCnt++;
                    KeyWords keyWords = new KeyWords();
                    keyWords.setResourceId(resId);
                    keyWords.setSentFlag(0);
                    keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                    String key = selectedAnsList.get(i).getQuestion();
                    keyWords.setKeyWord(key);
                    keyWords.setWordType("word");
                    appDatabase.getKeyWordDao().insert(keyWords);
                    List<ScienceQuestionChoice> tempOptionList = selectedAnsList.get(i).getLstquestionchoice();
                    for (int k = 0; k < tempOptionList.size(); k++) {
                        if (tempOptionList.get(k).getQid().equalsIgnoreCase(selectedAnsList.get(i).getUserAnswer())) {
                            correctWordList.add(tempOptionList.get(k));
                        }
                    }

                    addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid().trim()), GameConstatnts.SHOW_ME_ANDROID, 10, 10, selectedAnsList.get(i).getStartTime(), selectedAnsList.get(i).getEndTime(), selectedAnsList.get(i).getUserAnswer());
                } else {
                    if (selectedAnsList.get(i).getUserAnswer() != null && !selectedAnsList.get(i).getUserAnswer().trim().equalsIgnoreCase("")) {
                        List<ScienceQuestionChoice> tempOptionList = selectedAnsList.get(i).getLstquestionchoice();
                        for (int k = 0; k < tempOptionList.size(); k++) {
                            if (tempOptionList.get(k).getQid().equalsIgnoreCase(selectedAnsList.get(i).getUserAnswer())) {
                                wrongWordList.add(tempOptionList.get(k));
                            }
                        }
                        addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid().trim()), GameConstatnts.SHOW_ME_ANDROID, 0, 10, selectedAnsList.get(i).getStartTime(), selectedAnsList.get(i).getEndTime(), selectedAnsList.get(i).getUserAnswer());
                    }
                }
            }
            GameConstatnts.postScoreEvent(selectedAnsList.size(), correctCnt);
            BaseActivity.correctSound.start();
            setCompletionPercentage();
            if (!FC_Constants.isTest) {
                // showResult(correctWordList, wrongWordList);
                Intent intent = new Intent(getActivity(), PictionaryResult.class);
                intent.putExtra("selectlist", selectedAnsList);
                intent.putExtra("readingContentPath", readingContentPath);
                intent.putExtra("resourceType", GameConstatnts.SHOW_ME_ANDROID);
                startActivityForResult(intent, 111);
            } else {
                GameConstatnts.playGameNext(getActivity(), GameConstatnts.FALSE, this);
            }
        } else {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.TRUE, this);
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

            final Dialog dialog = new CustomLodingDialog(getActivity());
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
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(resId);
            score.setQuestionId(wID);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
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
                assessment.setSessionIDa(FastSave.getInstance().getString(FC_Constants.ASSESSMENT_SESSION, ""));
                assessment.setSessionIDm(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                assessment.setQuestionIda(wID);
                assessment.setScoredMarksa(scoredMarks);
                assessment.setTotalMarksa(totalMarks);
                assessment.setStudentIDa(FastSave.getInstance().getString(FC_Constants.CURRENT_ASSESSMENT_STUDENT_ID, ""));
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
        addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.SHOW_ME_ANDROID + " " + GameConstatnts.END);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.FALSE, this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (!scienceQuestion.getInstruction().isEmpty())
            GameConstatnts.showGameInfo(getActivity(), scienceQuestion.getInstruction(), readingContentPath + scienceQuestion.getInstructionUrl());
    }

    @OnClick(R.id.show_answer)
    public void showAnswer() {
        if (showanswer) {
            //hide answer
            clerAnimation();

        } else {
            //show Answer
            showanswer = true;
            show_answer.setText("Hide Hint");
            if (ansview != null) {
                ansview.startAnimation(animFadein);
                animFadein.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        clerAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }
    }

    private void clerAnimation() {
        showanswer = false;
        show_answer.setText("Hint");
    }
}

