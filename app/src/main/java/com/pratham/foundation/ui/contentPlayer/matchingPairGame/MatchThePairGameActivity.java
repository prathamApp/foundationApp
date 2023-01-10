package com.pratham.foundation.ui.contentPlayer.matchingPairGame;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.MatchThePair;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MatchThePairGameActivity extends BaseActivity implements MatchThePairListner {
    @BindView(R.id.rl_match_the_pair)
    RelativeLayout rl_match_the_pair;
    @BindView(R.id.topicTitle)
    TextView title;
    /* @BindView(R.id.rl_english)
     RecyclerView rl_english;
     @BindView(R.id.rl_hindi)
     RecyclerView rl_hindi;*/

    @BindView(R.id.btn_next)
    ImageButton next;
    @BindView(R.id.btn_prev)
    ImageButton prev;
    @BindView(R.id.frameLayoutMathThePair)
    FrameLayout frameLayoutMathThePair;
    @BindDrawable(R.drawable.bg_anim_fourth_layer)
    Drawable fourDwBg;
    List<MatchThePair> matchThePairList;
    int totalWordCount, learntWordCount;
    // List<MatchThePair> draggedList = new ArrayList<>();
    List<MatchThePair> randomList;
    ArrayList<MatchThePair> englishList = new ArrayList<>();
    ArrayList<MatchThePair> hindiList = new ArrayList<>();
    int questionCnt = 0;
    String readingContentPath, contentPath, contentTitle, StudentID, resId;
    boolean onSdCard;
    String questionStartTime;
    String gameStartTime;
    FragmentManager fragmentManager;
    CustomLodingDialog correctDialog;
    TextView dia_title = null;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_the_pair_game);
        ButterKnife.bind(this);
        context = MatchThePairGameActivity.this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        contentPath = getIntent().getStringExtra("contentPath");
        StudentID = getIntent().getStringExtra("StudentID");
        resId = getIntent().getStringExtra("resId");
        contentTitle = getIntent().getStringExtra("contentName");
        onSdCard = getIntent().getBooleanExtra("onSdCard", false);
        gameStartTime = FC_Utility.getCurrentDateTime();

        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";

        rl_match_the_pair.setBackground(fourDwBg);
        getMatchPairDataFromDB();
    }

    @SuppressLint("StaticFieldLeak")
    private void getMatchPairDataFromDB() {
        new AsyncTask<Object, Void, Object>() {

            @Override
            protected Object doInBackground(Object... objects) {
//                matchThePairList=new ArrayList<>();
                matchThePairList = AppDatabase.getDatabaseInstance(MatchThePairGameActivity.this)
                        .getMatchThePairDao()
                        .getQuestions(FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI));
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Collections.shuffle(matchThePairList);
                randomList = new ArrayList<>();

                float perc = getCompletionPercentage();
                totalWordCount = matchThePairList.size();
                learntWordCount = getLearntWordsCount();
//        setCompletionPercentage();
                randomList.clear();
//        int randomNo = FC_Utility.generateRandomNum(matchThePairList.size());
                for (int i = 0; i < matchThePairList.size(); i++) {
                    if (perc < 99.5) {
                        if (!checkWord("" + matchThePairList.get(i).getParaTitle()))
                            randomList.add(matchThePairList.get(i));
                    } else randomList.add(matchThePairList.get(i));
                    if (randomList.size() >= 5)
                        break;
                }

                if (randomList.size() > 0) {
//            List<MatchThePair> randomList = AppDatabase.getDatabaseInstance(this).getMatchThePairDao().getRandomQuestions();
                    MatchThePair currentQuestion = randomList.get(questionCnt);
//          MatchThePair currentQuestion =randomList.get(new Random().nextInt((randomList.size() - 0) + 1) + 0);
                    generateQuestions(currentQuestion);

                } else {
                    matchThePairList = getDataFromJson();
                    for (int i = 0; i < matchThePairList.size(); i++) {
                        String[] splittedHinList;
                        if (FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI)
                                .equalsIgnoreCase(FC_Constants.HINDI)) {
                            splittedHinList = splitSentence(matchThePairList.get(i).getLangText(), ("(?<=\\|\\s)|(?<=[?!]\\s)"));
                        } else {
                            splittedHinList = splitSentence(matchThePairList.get(i).getLangText(), ("(?<=\\.\\s)|(?<=[?!]\\s)"));
                        }
                        String[] splittedEngList = splitSentence(matchThePairList.get(i).getParaText(), ("(?<=\\|\\s)|(?<=[?!]\\s)"));

                        Log.d("@@", matchThePairList.get(i).getParaTitle() + splittedEngList.length + splittedHinList.length);
                    }
                    if (matchThePairList.size() > 0) {
                        AppDatabase.getDatabaseInstance(MatchThePairGameActivity.this).getMatchThePairDao().insertAllParas(matchThePairList);
                        getMatchPairDataFromDB();
                    } else
                        Toast.makeText(MatchThePairGameActivity.this, "No data", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

    }


    private void generateQuestions(MatchThePair randomQuestion) {
        try {
            if (questionCnt == 0) {
                prev.setVisibility(View.GONE);
            } else prev.setVisibility(View.VISIBLE);

            englishList.clear();
            hindiList.clear();
            String[] splittedHinList;

            String[] splittedEngList = splitSentence(randomQuestion.getParaText(), "[!.?|।]+");
            String langTemp = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI);
            if (langTemp.equalsIgnoreCase(FC_Constants.HINDI)) {
                splittedHinList = splitSentence(randomQuestion.getLangText(), "[!.?|।]+");
            } else {
                splittedHinList = splitSentence(randomQuestion.getLangText(), "[!.?|।]+");
            }
            assert splittedEngList != null;
            if (splittedEngList.length > 0) {
                for (int splitCnt = 0; splitCnt < splittedEngList.length; splitCnt++) {
                    MatchThePair matchThePair = new MatchThePair();
                    matchThePair.setId(randomQuestion.getId());
                    matchThePair.setParaText(splittedEngList[splitCnt].trim());
                    assert splittedHinList != null;
                    matchThePair.setLangText(splittedHinList[splitCnt].trim());
                    matchThePair.setParaTitle(randomQuestion.getParaTitle());
                    englishList.add(matchThePair);
                }
            }
            hindiList.addAll(englishList);
        /*if (splittedHinList.length > 0) {
            for (int splitCnt = 0; splitCnt < splittedHinList.length; splitCnt++) {
                MatchThePair matchThePair = new MatchThePair();
                matchThePair.setId(randomQuestion.getId());
                matchThePair.setLangText(splittedHinList[splitCnt].trim());
                hindiList.add(matchThePair);
            }
        }*/
            title.setText(randomQuestion.getParaTitle());
            Collections.shuffle(hindiList);
            setListsToRecycler(englishList, hindiList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListsToRecycler(ArrayList<MatchThePair> englishList, ArrayList<MatchThePair> hindiList) {

        questionStartTime = FC_Utility.getCurrentDateTime();

      /*  MatchPairAdapter matchPairAdapter = new MatchPairAdapter(englishList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        rl_english.setLayoutManager(linearLayoutManager);
        rl_english.setAdapter(matchPairAdapter);

        rl_hindi.removeAllViews();
        rl_hindi.invalidate();*/
        Bundle bundle = new Bundle();
        bundle.putSerializable("hindiList", hindiList);
        bundle.putSerializable("englishList", englishList);
        frameLayoutMathThePair.removeAllViewsInLayout();
        MatchThePairFragment matchThePairFragment = new MatchThePairFragment();
        matchThePairFragment.setArguments(bundle);
        matchThePairFragment.setMatchThePairListner(this);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.frameLayoutMathThePair, matchThePairFragment);
        fragmentTransaction.commit();
       /* DragDropAdapter dragDropAdapter = new DragDropAdapter(hindiList, this);
        ItemTouchHelper.Callback callback = new ItemMoveCallback(dragDropAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(null);
        touchHelper.attachToRecyclerView(rl_hindi);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this.getApplicationContext());
        rl_hindi.setLayoutManager(linearLayoutManager1);
        rl_hindi.setAdapter(dragDropAdapter);*/
    }

    private String[] splitSentence(String langText, String separator) {
        String[] splittedList = langText.split(separator);
        if (splittedList.length > 0)
            return splittedList;
        else return null;
    }

    private List<MatchThePair> getDataFromJson() {
        List<MatchThePair> matchThePairsList = new ArrayList<>();
        try {
//            InputStream is = this.getAssets().open("match_the_pair.json");
            InputStream is = new FileInputStream(readingContentPath + "match_the_pair.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            Gson gson = new Gson();
            Type type = new TypeToken<List<MatchThePair>>() {}.getType();
            matchThePairsList = gson.fromJson(jsonStr, type);
            for (int i = 0; i < matchThePairsList.size(); i++)
                matchThePairsList.get(i).setParaLang(FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matchThePairsList;
    }


    private void showCorrectDialog(String msg) {

        if (correctDialog == null) {
            correctDialog = new CustomLodingDialog(MatchThePairGameActivity.this, R.style.FC_DialogStyle);
            correctDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            correctDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            correctDialog.setContentView(R.layout.show_word_next_dialog);
            correctDialog.setCancelable(false);
            correctDialog.setCanceledOnTouchOutside(false);

            dia_title = correctDialog.findViewById(R.id.dia_title);
            Button next_btn = correctDialog.findViewById(R.id.dia_btn_next);
            Button test_btn = correctDialog.findViewById(R.id.dia_btn_test);
            Button revise_btn = correctDialog.findViewById(R.id.dia_btn_revise);

            next_btn.setText("Next");
            test_btn.setText("Cancel");
            revise_btn.setVisibility(View.GONE);

            next_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (correctDialog.isShowing())
                        correctDialog.dismiss();
                    onNextClick();
                }
            });
            test_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (correctDialog.isShowing())
                        correctDialog.dismiss();
                }
            });
        }
        if (!correctDialog.isShowing()) {
            if (dia_title == null) {
                correctDialog = null;
                showCorrectDialog(msg);
            } else {
                dia_title.setText(msg);
                correctDialog.show();
            }
        }
    }


    @OnClick(R.id.btn_next)
    public void onNextClick() {
        if (questionCnt < 4) {
            questionCnt++;
            generateQuestions(randomList.get(questionCnt));
        } else showNextDialog();
    }

    public void showNextDialog() {

        CustomLodingDialog nextDialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
        nextDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nextDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nextDialog.setContentView(R.layout.show_word_next_dialog);
        nextDialog.setCancelable(false);
        nextDialog.setCanceledOnTouchOutside(false);
        if (!nextDialog.isShowing())
            nextDialog.show();

        Button next_btn = nextDialog.findViewById(R.id.dia_btn_next);
        Button test_btn = nextDialog.findViewById(R.id.dia_btn_test);
        Button revise_btn = nextDialog.findViewById(R.id.dia_btn_revise);
        test_btn.setText("Cancel");
        revise_btn.setVisibility(View.GONE);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMatchPairDataFromDB();
                questionCnt = 0;
                if (nextDialog.isShowing())
                    nextDialog.dismiss();
            }
        });

        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextDialog.dismiss();
            }
        });

        revise_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextDialog.isShowing())
                    nextDialog.dismiss();
//                revise();
            }
        });

    }

    @OnClick(R.id.btn_prev)
    public void onPrevClick() {
        if (questionCnt > 0) {
            questionCnt--;
            generateQuestions(randomList.get(questionCnt));
        }
    }

    public void revise() {
        questionCnt = 0;
        generateQuestions(randomList.get(questionCnt));
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"), resId, wordStr);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public float getCompletionPercentage() {
        float perc;
        totalWordCount = matchThePairList.size();
        learntWordCount = getLearntWordsCount();

        if (learntWordCount > 0) {
            perc = ((float) learntWordCount / (float) totalWordCount) * 100;
        } else
            perc = 0;

        return perc;
    }

    public void setCompletionPercentage() {
        float perc;
        totalWordCount = matchThePairList.size();
        learntWordCount = getLearntWordsCount();
        String Label = "resourceProgress";
        if (learntWordCount > 0) {
            perc = ((float) learntWordCount / (float) totalWordCount) * 100;
//            addExitScore(perc, learntWordCount, totalWordCount, gameStartTime, Label);
            addContentProgress(perc, Label);
        } else
//            addExitScore(0, learntWordCount, totalWordCount, gameStartTime, Label);
            addContentProgress(0, Label);
    }


    private void addContentProgress(float perc, String label) {
        ContentProgress contentProgress = new ContentProgress();
        contentProgress.setProgressPercentage("" + perc);
        contentProgress.setResourceId("" + resId);
        contentProgress.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
        contentProgress.setStudentId("" + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ?"NA"
                    :FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "")));
        contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
        contentProgress.setLabel("" + label);
        contentProgress.setSentFlag(0);
        AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);
    }


    //    @Background
    private int getLearntWordsCount() {
        int count = 0;
        count = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"), resId);
        return count;
    }

    public void addLearntWords(List<MatchThePair> draggedList) {
        KeyWords keyWords = new KeyWords();
        keyWords.setResourceId(resId);
        keyWords.setSentFlag(0);
        keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"));
        keyWords.setKeyWord(draggedList.get(0).getParaTitle().toLowerCase());
        keyWords.setTopic(FC_Constants.MATCH_THE_PAIR);
        keyWords.setWordType("word");
        keyWords.setTopic("");
        AppDatabase.getDatabaseInstance(context).getKeyWordDao().insert(keyWords);
        BackupDatabase.backup(this);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        showExitDialog();
    }

    @SuppressLint("StaticFieldLeak")
    private void showExitDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_title.setText("Exit the game?");
        dia_btn_green.setText("Yes");
        dia_btn_red.setText("No");
        dia_btn_yellow.setText("" + FC_Constants.dialog_btn_cancel);
        dialog.show();

        dia_btn_green.setOnClickListener(v -> {
            new AsyncTask<Object, Void, Object>() {
                @Override
                protected Object doInBackground(Object... objects) {
                    setCompletionPercentage();
                    return null;
                }
            }.execute();
            finish();
            dialog.dismiss();
        });
        dia_btn_red.setOnClickListener(v -> dialog.dismiss());
        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
    }


    public void addScore(List<MatchThePair> draggedList) {
        try {
            String deviceId = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(resId);
            score.setQuestionId(Integer.parseInt(draggedList.get(0).getId()));
            score.setScoredMarks(10);
            score.setTotalMarks(10);
            score.setStudentID(((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ? "NA"
                    : FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA")));
            score.setGroupId(((FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "").equals(null)) ? "NA"
                    : FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, "NA")));
            score.setStartDateTime(questionStartTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(4);
            score.setLabel(draggedList.get(0).getParaTitle() + " - " + contentPath);
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);

            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                Assessment assessment = new Assessment();
                assessment.setResourceIDa(resId);
                assessment.setSessionIDa(FastSave.getInstance().getString(FC_Constants.ASSESSMENT_SESSION, ""));
                assessment.setSessionIDm(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                assessment.setQuestionIda(Integer.parseInt(draggedList.get(0).getId()));
                assessment.setScoredMarksa(10);
                assessment.setTotalMarksa(10);
                assessment.setStudentIDa(FastSave.getInstance().getString(FC_Constants.CURRENT_ASSESSMENT_STUDENT_ID, ""));
                assessment.setStartDateTimea(questionStartTime);
                assessment.setDeviceIDa(deviceId.equals(null) ? "0000" : deviceId);
                assessment.setEndDateTime(FC_Utility.getCurrentDateTime());
                assessment.setLevela(FC_Constants.currentLevel);
                assessment.setLabel("test: " + contentPath);
                assessment.setSentFlag(0);
                AppDatabase.getDatabaseInstance(context).getAssessmentDao().insert(assessment);
            }

            BackupDatabase.backup(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 /*   public void addExitScore(float perc, int scoredMarks, int totalMarks, String resStartTime, String Label) {
        try {
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(resId);
            score.setQuestionId(0);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"));
            score.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, ""));
            score.setStartDateTime(resStartTime);
            score.setDeviceID("" + perc);
            score.setEndDateTime(ApplicationClass.getCurrentDateTime());
            score.setLevel(0);
            score.setLabel("" + Label);
            score.setSentFlag(0);
            appDatabase.getScoreDao().insert(score);
            BackupDatabase.backup(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onItemDragListener(List draggedList) {
        showCorrectDialog("Correct Answer");
        new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object... objects) {
                addLearntWords(draggedList);
                addScore(draggedList);
                return null;
            }
        }.execute();

    }

    @Override
    public void onSubmitClicked(boolean isCorrect) {
        if (isCorrect) {
            showCorrectDialog("Correct Answer");
        } else {
            showCorrectDialog("Wrong Answer");
        }
    }
}
