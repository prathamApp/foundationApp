package com.pratham.foundation.ui.contentPlayer.pictionary;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
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
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
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
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Utility.showZoomDialog;

@EFragment(R.layout.layout_mcq_fill_in_the_blanks_with_options_row)
public class pictionaryFragment extends Fragment implements OnGameClose, PictionaryContract.PictionaryView {

    @ViewById(R.id.tv_question)
    TextView question;
    @ViewById(R.id.iv_question_image)
    ImageView questionImage;
    @ViewById(R.id.iv_question_gif)
    GifView questionGif;
    @ViewById(R.id.rg_mcq)
    RadioGroup radioGroupMcq;
    @ViewById(R.id.grid_mcq)
    GridLayout gridMcq;


    @ViewById(R.id.btn_prev)
    ImageButton previous;
    @ViewById(R.id.btn_submit)
    SansButton submitBtn;
    @ViewById(R.id.btn_next)
    ImageButton next;

    @ViewById(R.id.show_answer)
    SansButton show_answer;

    @ViewById(R.id.image_container)
    MaterialCardView image_container;

    @ViewById(R.id.iv_view_img)
    ImageView iv_view_img;

    @Bean(PictionaryPresenter.class)
    PictionaryContract.PictionaryPresenter presenter;
    private String readingContentPath, contentPath, contentTitle, StudentID, resId, resStartTime;

    List<ScienceQuestionChoice> options;
    private ArrayList<ScienceQuestion> selectedFive;
    // private List<ScienceQuestion> dataList;


    private int imgCnt = 0, textCnt = 0, index = 0;
    private ScienceQuestion scienceQuestion;
    private boolean onSdCard;

    private boolean showanswer = false;
    private Animation animFadein;
    View ansview;

    public pictionaryFragment() {
        // Required empty public constructor
    }


    @AfterViews
    public void initiate() {
        // super.onCreate(savedInstanceState);
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
            if (FC_Constants.isTest) {
                show_answer.setVisibility(View.INVISIBLE);
            }
            animFadein = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.shake);
            resStartTime = FC_Utility.getCurrentDateTime();
            presenter.setView(this, resId);
            presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.SHOW_ME_ANDROID + " " + GameConstatnts.START);
            presenter.getData(readingContentPath);

        }
    }











   /* private boolean checkWord(String wordStr) {
        try {
            String word = appDatabase.getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId, wordStr);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }*/

   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_mcq_fill_in_the_blanks_with_options_row, container, false);
    }*/

   /* @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      //  ButterKnife.bind(this, view);
        if (FC_Constants.isTest) {
            show_answer.setVisibility(View.INVISIBLE);
        }
        setMcqsQuestion();
    }*/

    public void setData(ArrayList<ScienceQuestion> selectedFive) {
        if (selectedFive != null) {
            this.selectedFive = selectedFive;
            setMcqsQuestion();
        }
    }

    private void setMcqsQuestion() {
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
            if (!FC_Constants.isTest && !FC_Constants.isPractice) {
                if (selectedFive.get(index).getUserAnswer() != null && selectedFive.get(index).getUserAnswer().isEmpty()) {
                    show_answer.performClick();
                }
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

    @Click(R.id.btn_prev)
    public void onPreviousClick() {
        if (selectedFive != null)
            if (index > 0) {
                index--;
                setMcqsQuestion();
            }
    }

    @Click(R.id.btn_next)
    public void onNextClick() {
        if (selectedFive != null)
            if (index < (selectedFive.size() - 1)) {
                index++;
                setMcqsQuestion();
            }
    }

    @Click(R.id.btn_submit)
    public void onsubmitBtnClick() {
        if (selectedFive != null)
            presenter.addLearntWords(selectedFive);

        //  GameConstatnts.playGameNext(getActivity());
        /*Bundle bundle = GameConstatnts.findGameData("110");
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new FillInTheBlanksFragment(), R.id.RL_CPA,
                    bundle, FillInTheBlanksFragment.class.getSimpleName());
        }*/

    }


    public void showResult() {
        Intent intent = new Intent(getActivity(), PictionaryResult.class);
        intent.putExtra("selectlist", selectedFive);
        intent.putExtra("readingContentPath", readingContentPath);
        intent.putExtra("resourceType", GameConstatnts.SHOW_ME_ANDROID);
        startActivityForResult(intent, 111);


       /* if ((correctWord != null && !correctWord.isEmpty()) || (wrongWord != null && !wrongWord.isEmpty())) {

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
        }*/
    }


    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.SHOW_ME_ANDROID + " " + GameConstatnts.END);
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
        if (!selectedFive.get(index).getInstruction().isEmpty())
            GameConstatnts.showGameInfo(getActivity(),selectedFive.get(index).getInstruction(), readingContentPath +selectedFive.get(index).getInstruction());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDilogCloseEvent(String msg) {
       if(msg.equalsIgnoreCase(FC_Constants.DIALOG_CLOSED)){
           setMcqsQuestion();
       }
    }
    @Click(R.id.show_answer)
    public void showAnswer() {
        if (showanswer) {
            //hide answer
            clerAnimation();

        } else {
            //show Answer
            showanswer = true;
           // show_answer.setText("Hide Hint");
            show_answer.setText(getResources().getString(R.string.hide_hint));
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
        //show_answer.setText("Hint");
        show_answer.setText(getResources().getString(R.string.hint));
    }
}

