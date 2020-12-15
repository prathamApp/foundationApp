package com.pratham.foundation.ui.contentPlayer.morphin;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.fontsview.SansButton;
import com.pratham.foundation.customView.hive.HiveLayoutManager;
import com.pratham.foundation.interfaces.MediaCallbacks;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Utility;
import com.pratham.foundation.utility.MediaPlayerUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EFragment(R.layout.activity_main_morphin)
public class Hive_game extends Fragment implements Hive_game_contract.Hive_game_view,
        OptionRecyclerInterface, OnGameClose, MediaCallbacks {
    @Bean(Hive_game_presenter.class)
    Hive_game_contract.Hive_game_presenter presenter;
    @ViewById(R.id.btn_prev)
    ImageButton previous;
    @ViewById(R.id.btn_submit)
    SansButton submitBtn;
    @ViewById(R.id.btn_next)
    ImageButton next;
    @ViewById(R.id.showAnswer)
    Button showAnswer;
    @ViewById(R.id.optionList)
    RecyclerView optionListRecycler;
    @ViewById(R.id.hive_recycler)
    RecyclerView hive_recycler;
    @ViewById(R.id.bottom_control_container)
    RecyclerView bottom_control_container;

   /* @ViewById(R.id.add)
    Button add;*/

    /*  public static final int[] resIds = new int[]{
              R.drawable.img_0
              , R.drawable.img_1
              , R.drawable.img_2
              , R.drawable.img_3
              , R.drawable.img_4
              , R.drawable.img_5
              , R.drawable.img_6
              , R.drawable.img_7
              , R.drawable.img_8
              , R.drawable.img_9
              , R.drawable.img_10
              , R.drawable.img_11
              , R.drawable.img_12
      };*/
    //  private static final String TAG = Hive_game.class.getSimpleName();

  /*  @ViewById(R.id.remove)
    Button remove;
    @ViewById(R.id.move)
    Button move;*/

    private HiveLayoutManager layoutManager;
    private HiveAdapter adapter;
    private int index = 0;
    private String contentPath, contentTitle, StudentID, resId, readingContentPath, resStartTime;
    private boolean onSdCard;
    private List<ScienceQuestion> questionModel;
    private boolean showanswer = false;
    private List<ScienceQuestionChoice> hivelist, hiveTemp, optionList;
    private String language = "locale";
    private RecyclerAdapter recyclerAdapter;
    private ScienceQuestionChoice scienceChoiceEnglish, play_hive_audio;
    private boolean issubmitted = false;
    private Animation animFadein;
    private MediaPlayer mediaPlayer;
    public static MediaPlayerUtil mediaPlayerUtil;
    private boolean play_hive = false;
    @AfterViews
    public void init() {
        // super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        // initViews();
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
        hivelist = new ArrayList<>();
        hiveTemp = new ArrayList<>();
        optionList = new ArrayList<>();

        presenter.setView(Hive_game.this, contentTitle, resId);
        presenter.getData(readingContentPath);
        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.HIVELAYOUT_GAME + " " + GameConstatnts.START);
        /*if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
            show_answer.setVisibility(View.GONE);
        }*/
        // hiveList = questionModel.get(index).getLstquestionchoice();
        animFadein = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.bounce_new);
        mediaPlayer = new MediaPlayer();
        mediaPlayerUtil = new MediaPlayerUtil(getActivity());
        mediaPlayerUtil.initCallback(this);
        initObjects();
        previous.setVisibility(View.INVISIBLE);
        //afterViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    private void initObjects() {
        BitmapCache.INSTANCE.init(getActivity(), 200 * 200 * 4 * 13);
    }

    public void loadQuestion(List<ScienceQuestion> questionModel) {
        this.questionModel = questionModel;
        if (questionModel != null) {
            showQuestion();
        }
    }

    @Override
    public void showResult() {
        if (adapter != null) {
            hive_recycler.startAnimation(animFadein);
            adapter.setSubmited(issubmitted);
            adapter.notifyDataSetChanged();
            optionListRecycler.setVisibility(View.GONE);
            showAnswer.setVisibility(View.INVISIBLE);
        }
    }

    private void showQuestion() {
        afterViews();
        loadOptions();
        //UNCOMMENT to show answer in learning
       /* if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test) && !FC_Constants.isPractice) {
            showAnswer.performClick();
        }*/
    }

    private void loadOptions() {
        optionList.clear();
        if (language.equalsIgnoreCase("locale")) {
            for (ScienceQuestionChoice scienceQuestionChoice : questionModel.get(index).getLstquestionchoice()) {
                if (scienceQuestionChoice.getIsQuestion() == null || !scienceQuestionChoice.getIsQuestion().equalsIgnoreCase("True")) {
                    optionList.add(scienceQuestionChoice);
                }
            }
            submitBtn.setVisibility(View.INVISIBLE);
        } else {
            submitBtn.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
            optionList.addAll(questionModel.get(index).getLstquestionchoice());
        }
        for (ScienceQuestionChoice scienceQuestionChoice1 : optionList) {
            scienceQuestionChoice1.setPlaying(false);
        }
        recyclerAdapter = new RecyclerAdapter(optionList, getActivity(), this);
        recyclerAdapter.setLanguage(language);
        adapter.setLanguage(language);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        optionListRecycler.setLayoutManager(linearLayoutManager);
        optionListRecycler.setAdapter(recyclerAdapter);
        adapter.notifyDataSetChanged();
    }
   /* private void initViews() {
        //  hive_recycler = (RecyclerView) findViewById(R.id.list);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int r = getRandomPosition();
                Log.d(TAG, "onClick: r" + r);
                adapter.addData(resIds[index % resIds.length], r);
                adapter.notifyItemInserted(r);
                index++;
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getItemCount() != 0) {
                    int r = getRandomPosition();
                    Log.d(TAG, "onClick: r" + r);
                    adapter.remove(r);
                    adapter.notifyItemRemoved(r);
                    index--;
                }
            }
        });
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int r = getRandomPosition();
                int r2 = getRandomPosition();
                Log.d(TAG, "onClick: r" + r);
                adapter.move(r, r2);
                adapter.notifyItemMoved(r, r2);
            }
        });
    }*/

 /*   private int getRandomPosition() {
        int count = adapter.getItemCount();
        if (count > 0) {
            return new Random().nextInt(count);
        } else {
            return 0;
        }
    }*/

    private void afterViews() {
        hivelist.clear();
        if (language.equalsIgnoreCase("locale")) {
            for (ScienceQuestionChoice scienceQuestionChoice : questionModel.get(index).getLstquestionchoice()) {
                if (scienceQuestionChoice.getIsQuestion() != null && scienceQuestionChoice.getIsQuestion().equalsIgnoreCase("True")) {
                    hivelist.add(scienceQuestionChoice);
                    break;
                }
            }
        }
        adapter = new HiveAdapter(hivelist, getActivity(), this);
        adapter.setLanguage(language);
        hive_recycler.setLayoutManager(layoutManager = new HiveLayoutManager(HiveLayoutManager.HORIZONTAL));
        hive_recycler.setAdapter(adapter);

//        layoutManager.setGravity(HiveLayoutManager.CENTER);
        layoutManager.setGravity(HiveLayoutManager.ALIGN_LEFT);
//        layoutManager.setGravity(HiveLayoutManager.ALIGN_RIGHT);
//        layoutManager.setGravity(HiveLayoutManager.ALIGN_TOP);
//        layoutManager.setGravity(HiveLayoutManager.ALIGN_BOTTOM);
//        layoutManager.setGravity(HiveLayoutManager.ALIGN_LEFT | HiveLayoutManager.ALIGN_TOP);
//        layoutManager.setGravity(HiveLayoutManager.ALIGN_LEFT | HiveLayoutManager.ALIGN_BOTTOM);
//        layoutManager.setGravity(HiveLayoutManager.ALIGN_RIGHT | HiveLayoutManager.ALIGN_TOP);
//        layoutManager.setGravity(HiveLayoutManager.ALIGN_RIGHT | HiveLayoutManager.ALIGN_BOTTOM);
        layoutManager.setPadding(0, 0, 0, 0);
    }

    @Click(R.id.showAnswer)
    public void showAnswer() {
        if (showanswer) {
            //hide answer
            // showAnswer.setText("Hint");
            showAnswer.setText(getResources().getString(R.string.hint));
            showanswer = false;
            //keywordOptionAdapter.setClickable(true);
            if (language.equalsIgnoreCase("English")) {
                submitBtn.setVisibility(View.VISIBLE);
            }
            if (!language.equalsIgnoreCase("English")) {
                next.setVisibility(View.VISIBLE);
            }
            optionListRecycler.setVisibility(View.VISIBLE);
            hivelist.clear();
            for (ScienceQuestionChoice scienceQuestionChoice : hiveTemp) {
                hivelist.add(scienceQuestionChoice);
            }
        } else {
            //show Answer
            showAnswer.setText(getResources().getString(R.string.hide_hint));
            showanswer = true;
            //keywordOptionAdapter.setClickable(false);
            submitBtn.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            optionListRecycler.setVisibility(View.GONE);
            hiveTemp.clear();
            hiveTemp.addAll(hivelist);
           /* for (ScienceQuestionChoice scienceQuestionChoice : hivelist) {
                hiveTemp.add(scienceQuestionChoice);
            }*/
            hivelist.clear();
            hivelist.addAll(questionModel.get(index).getLstquestionchoice());
           /* for (ScienceQuestionChoice scienceQuestionChoice : questionModel.get(index).getLstquestionchoice()) {
                hivelist.add(scienceQuestionChoice);
            }*/
        }
        adapter.setShowanswer(showanswer);
        // adapter.clearSelectedOptionList();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(ScienceQuestionChoice scienceQuestionChoice, String operation) {
        //int r = getRandomPosition();
        // Log.d(TAG, "onClick: r" + r);
        if (language.equalsIgnoreCase("locale")) {
            if (operation.equalsIgnoreCase("add")) {
                adapter.addData(scienceQuestionChoice);
                optionList.remove(scienceQuestionChoice);
            } else if (operation.equalsIgnoreCase("remove")) {
                scienceQuestionChoice.setPlaying(false);
                adapter.remove(scienceQuestionChoice);
                optionList.add(scienceQuestionChoice);
            } else if (operation.equalsIgnoreCase("play")) {
                try {
                    if (mediaPlayerUtil != null) {
                        mediaPlayerUtil.stopMedia();
                    }
                    play_hive = false;
                    play_hive_audio = null;
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(readingContentPath + "/" + scienceQuestionChoice.getSubUrl());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (operation.equalsIgnoreCase("getEnglishWord")) {
                scienceChoiceEnglish = scienceQuestionChoice;
            } else if (operation.equalsIgnoreCase("setEnglishWord")) {
                boolean isContain = false;
                if (scienceChoiceEnglish != null) {
                    optionList.remove(scienceChoiceEnglish);
                }
                if (scienceQuestionChoice.getUserAns() != null && !scienceQuestionChoice.getUserAns().isEmpty()) {
                    for (ScienceQuestionChoice scienceQuestionChoice1 : optionList) {
                        if (scienceQuestionChoice1.getEnglish().equalsIgnoreCase(scienceQuestionChoice.getUserAns())) {
                            isContain = true;
                            break;
                        }
                    }
                    if (!isContain && scienceChoiceEnglish != null) {
                        for (ScienceQuestionChoice scienceQuestionChoiceTemp : questionModel.get(index).getLstquestionchoice()) {
                            if (scienceQuestionChoiceTemp.getEnglish() != null && scienceQuestionChoiceTemp.getEnglish().equalsIgnoreCase(scienceQuestionChoice.getUserAns())) {
                                scienceQuestionChoiceTemp.setPlaying(false);
                                optionList.add(scienceQuestionChoiceTemp);
                                break;
                            }
                        }
                    }
                }
                if (scienceChoiceEnglish != null) {
                    scienceQuestionChoice.setStartTime(FC_Utility.getCurrentDateTime());
                    scienceQuestionChoice.setEndTime(FC_Utility.getCurrentDateTime());
                    scienceQuestionChoice.setUserAns(scienceChoiceEnglish.getEnglish());
                    recyclerAdapter.setRowIndex(-1);
                }
                scienceChoiceEnglish = null;
            } else if (operation.equalsIgnoreCase("play")) {
                try {
                    if (mediaPlayerUtil != null) {
                        mediaPlayerUtil.stopMedia();
                    }
                    play_hive = false;
                    play_hive_audio = null;
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(readingContentPath + "/" + scienceQuestionChoice.getEnglishURL());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (operation.equalsIgnoreCase("play_hive")) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            play_hive = true;
            play_hive_audio = scienceQuestionChoice;
            mediaPlayerUtil.playMedia(readingContentPath + "/" + scienceQuestionChoice.getSubUrl());
            // mediaPlayer.prepare();
            //  mediaPlayer.start();

        }
        adapter.notifyDataSetChanged();
        if (recyclerAdapter != null) {
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    @Click(R.id.btn_next)
    public void next() {
        int diff = checkAllOptionsSelected();
        if (diff > 0) {
            CustomLodingDialog dialog = new CustomLodingDialog(getActivity(), R.style.FC_DialogStyle);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.fc_custom_dialog);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            TextView dia_title = dialog.findViewById(R.id.dia_title);
            Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
            Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
            Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);
            dia_btn_yellow.setVisibility(View.GONE);
            /* dia_btn_red.setVisibility(View.GONE);*/

            dia_title.setText(getResources().getString(R.string.alertmessage));
            dia_btn_green.setText(getResources().getString(R.string.Okay));
            dia_btn_red.setText(getResources().getString(R.string.Cancel));

            dia_btn_green.setOnClickListener(v -> {
                dialog.dismiss();
                language = "English";
                loadOptions();
            });
            dia_btn_red.setOnClickListener(v -> {
                dialog.dismiss();
            });
        } else {
            language = "English";
            loadOptions();
        }
    }

    private int checkAllOptionsSelected() {
        int hiveCnt = check(hivelist);
        int totalCnt = check(questionModel.get(index).getLstquestionchoice());
        return totalCnt - hiveCnt;
    }

    private int check(List<ScienceQuestionChoice> list) {
        int correctCnt = 0;
        for (ScienceQuestionChoice scienceQuestionChoice : list) {
            if (scienceQuestionChoice.getCorrectAnswer().equalsIgnoreCase("True")) {
                correctCnt++;
            }
        }
        return correctCnt;
    }

    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), GameConstatnts.HIVELAYOUT_GAME + " " + GameConstatnts.END);
    }

    @Click(R.id.btn_submit)
    public void submit() {
        if (hivelist != null && presenter.checkIsAttempted(hivelist)) {
            if (issubmitted) {
                GameConstatnts.playGameNext(getActivity(), GameConstatnts.FALSE, this);
            } else {
                issubmitted = true;
                BaseActivity.correctSound.start();
                submitBtn.setText("Next");
                presenter.addLearntWords(questionModel.get(index), hivelist);
            }
        } else {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.TRUE, this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (!questionModel.get(index).getInstruction().isEmpty())
            GameConstatnts.showGameInfo(getActivity(), questionModel.get(index).getInstruction(), readingContentPath + questionModel.get(index).getInstructionUrl());
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    public void onDataNotFound(){
        Toast.makeText(getActivity(), "Data not found", Toast.LENGTH_LONG).show();
        bottom_control_container.setVisibility(View.INVISIBLE);
        showAnswer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onComplete() {
        if (play_hive_audio != null && play_hive_audio.getUserAns() != null && !play_hive_audio.getUserAns().isEmpty()) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            for (ScienceQuestionChoice scienceQuestionChoice : questionModel.get(index).getLstquestionchoice()) {
                if (scienceQuestionChoice.getEnglish().equalsIgnoreCase(play_hive_audio.getUserAns())) {
                    play_hive_audio=null;
                    mediaPlayerUtil.playMedia(readingContentPath + "/" + scienceQuestionChoice.getEnglishURL());
                    break;
                }
            }
        }
    }
}