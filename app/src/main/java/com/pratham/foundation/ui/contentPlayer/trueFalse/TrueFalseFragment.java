package com.pratham.foundation.ui.contentPlayer.trueFalse;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.fontsview.SansButton;
import com.pratham.foundation.customView.fontsview.SansTextViewBold;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TrueFalseFragment extends Fragment {
    @BindView(R.id.tv_question)
    TextView question;
    @BindView(R.id.rg_true_false)
    RadioGroup rg_true_false;
    @BindView(R.id.iv_question_image)
    ImageView questionImage;
    @BindView(R.id.iv_question_gif)
    GifView questionGif;
    @BindView(R.id.rb_true)
    Button radioButtonTrue;
    @BindView(R.id.rb_false)
    Button radioButtonFalse;

    /* private static final String POS = "pos";
     private static final String SCIENCE_QUESTION = "scienceQuestion";*/
    private float perc;

    private int pos;
    //private ScienceQuestion scienceQuestion;
    private String readingContentPath, contentPath, contentTitle, StudentID, resId;
    private int totalWordCount, learntWordCount, index = 0;
    private boolean onSdCard;
    private List<ScienceQuestion> dataList;
    private List<ScienceQuestion> selectedFive;
    private List<String> correctWordList, wrongWordList;
    private boolean isTest = false;
    Context context;

    public TrueFalseFragment() {
        // Required empty public constructor
    }

  /*  public static TrueFalseFragment newInstance(int pos, ScienceQuestion scienceQuestion) {
        TrueFalseFragment trueFalseFragment = new TrueFalseFragment();
        Bundle args = new Bundle();
        args.putInt("pos", pos);
        args.putSerializable("scienceQuestion", scienceQuestion);
        trueFalseFragment.setArguments(args);
        return trueFalseFragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //   pos = getArguments().getInt(POS, 0);
            //scienceQuestion = (ScienceQuestion) getArguments().getSerializable(SCIENCE_QUESTION);
            //assessmentAnswerListener = (ScienceAssessmentActivity) getActivity();
            contentPath = getArguments().getString("contentPath");
            StudentID = getArguments().getString("StudentID");
            resId = getArguments().getString("resId");
            contentTitle = getArguments().getString("contentName");
            onSdCard = getArguments().getBoolean("onSdCard", false);
            context = getActivity();
            if (onSdCard)
                readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";
            isTest = FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test);

            getData();
        }
    }

    private void getData() {
        try {
            InputStream is = new FileInputStream(readingContentPath + "true_false.json");
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
                    if (!checkWord("" + dataList.get(i).getQuestion()))
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
        count = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWordCount(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId);
        return count;
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = AppDatabase.getDatabaseInstance(context).getKeyWordDao().checkWord(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""), resId, wordStr);
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
        return inflater.inflate(R.layout.layout_true_false_row, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setTrueFalseQuestion();
    }

    public void setTrueFalseQuestion() {


        question.setText(selectedFive.get(index).getQuestion());
        if (!selectedFive.get(index).getPhotourl().equalsIgnoreCase("")) {
            questionImage.setVisibility(View.VISIBLE);
//            if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {

            String fileName = selectedFive.get(index).getPhotourl();
//                String localPath = ApplicationClass.getStoragePath() + Assessment_Constants.STORE_DOWNLOADED_MEDIA_PATH + "/" + fileName;
            String localPath = readingContentPath + "/" + fileName;


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
//                    zoomImg.setVisibility(View.VISIBLE);
                    } else {*/
                    gif = new FileInputStream(localPath);
                    questionImage.setVisibility(View.GONE);
                    questionGif.setVisibility(View.VISIBLE);
                    questionGif.setGifResource(gif);
                    // }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Bitmap bmImg = BitmapFactory.decodeFile(localPath);
                    BitmapFactory.decodeStream(new FileInputStream(localPath));
                    questionImage.setImageBitmap(bmImg);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
/*            } else {
                String fileName = getFileName(scienceQuestion.getQid(), scienceQuestion.getPhotourl());
//                String localPath = ApplicationClass.getStoragePath() + Assessment_Constants.STORE_DOWNLOADED_MEDIA_PATH + "/" + fileName;
                String localPath = AssessmentApplication.assessPath + Assessment_Constants.STORE_DOWNLOADED_MEDIA_PATH + "/" + fileName;
                Bitmap bitmap = BitmapFactory.decodeFile(localPath);
                questionImage.setImageBitmap(bitmap);
            }*/
        } else questionImage.setVisibility(View.GONE);


        radioButtonTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFive.get(index).setUserAnswer("true");
//                questionTypeListener.setAnswer("", radioButtonTrue.getText().toString(), scienceQuestion.getQid(), null);
                // assessmentAnswerListener.setAnswerInActivity("", radioButtonTrue.getText().toString(), scienceQuestion.getQid(), null);
                radioButtonTrue.setSelected(true);
                radioButtonTrue.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_corner_dialog));
                radioButtonFalse.setBackground(getActivity().getResources().getDrawable(R.drawable.ripple_rectangle_true_false));
                //  radioButtonTrue.setTextColor(Assessment_Utility.selectedColor);
                radioButtonFalse.setSelected(false);
                // radioButtonFalse.setTextColor(getActivity().getResources().getColor(R.color.white));
            }
        });

        radioButtonFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFive.get(index).setUserAnswer("false");
                //      assessmentAnswerListener.setAnswerInActivity("", radioButtonFalse.getText().toString(), scienceQuestion.getQid(), null);
//                questionTypeListener.setAnswer("", radioButtonFalse.getText().toString(), scienceQuestion.getQid(), null);
                radioButtonFalse.setSelected(true);
                //   radioButtonFalse.setTextColor(Assessment_Utility.selectedColor);
                radioButtonTrue.setSelected(false);
                radioButtonTrue.setBackground(getActivity().getResources().getDrawable(R.drawable.ripple_rectangle_true_false));
                radioButtonFalse.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_corner_dialog));
                //  radioButtonTrue.setTextColor(getActivity().getResources().getColor(R.color.white));
            }
        });
       /* rg_true_false.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radioButtonTrue.getId()*//* && (!isFirstLoad)*//*) {
                    questionTypeListener.setAnswer("", radioButtonTrue.getText().toString(), scienceQuestion.getQid(), null);
                    radioButtonTrue.setSelected(true);
                    radioButtonTrue.setTextColor(Assessment_Utility.selectedColor);
                    radioButtonFalse.setSelected(false);
                    radioButtonFalse.setTextColor(context.getResources().getColor(R.color.white));

                } else if (checkedId == radioButtonFalse.getId() *//*&& (!isFirstLoad)*//*) {
                    questionTypeListener.setAnswer("", radioButtonFalse.getText().toString(), scienceQuestion.getQid(), null);
                    radioButtonFalse.setSelected(true);
                    radioButtonFalse.setTextColor(Assessment_Utility.selectedColor);
                    radioButtonTrue.setSelected(false);
                    radioButtonTrue.setTextColor(context.getResources().getColor(R.color.white));


                } else {
                    Toast.makeText(context, "Select Answer", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        if (selectedFive.get(index).getUserAnswer().equalsIgnoreCase("true")) {
            radioButtonTrue.setSelected(true);
            radioButtonTrue.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_corner_dialog));
            radioButtonFalse.setBackground(getActivity().getResources().getDrawable(R.drawable.ripple_rectangle_true_false));
            //  radioButtonTrue.setTextColor(Assessment_Utility.selectedColor);
            radioButtonFalse.setSelected(false);
            //radioButtonFalse.setTextColor(getActivity().getResources().getColor(R.color.white));


        } else if (selectedFive.get(index).getUserAnswer().equalsIgnoreCase("false")) {
            radioButtonFalse.setSelected(true);
            //radioButtonFalse.setTextColor(Assessment_Utility.selectedColor);
            radioButtonTrue.setSelected(false);
            radioButtonTrue.setBackground(getActivity().getResources().getDrawable(R.drawable.ripple_rectangle_true_false));
            radioButtonFalse.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_corner_dialog));
            // radioButtonTrue.setTextColor(getActivity().getResources().getColor(R.color.white));

        } else {
            radioButtonFalse.setSelected(false);
            radioButtonTrue.setSelected(false);
            radioButtonTrue.setBackground(getActivity().getResources().getDrawable(R.drawable.ripple_rectangle_true_false));
            radioButtonFalse.setBackground(getActivity().getResources().getDrawable(R.drawable.ripple_rectangle_true_false));
            //  radioButtonTrue.setTextColor(getActivity().getResources().getColor(R.color.white));
            // radioButtonFalse.setTextColor(getActivity().getResources().getColor(R.color.white));

        }

    }

    @OnClick(R.id.previous)
    public void onPreviousClick() {
        if (index > 0) {
            index--;
            setTrueFalseQuestion();
        }
    }

    @OnClick(R.id.next)
    public void onNextClick() {
        if (index < 4) {
            index++;
            setTrueFalseQuestion();
        }
    }

    @OnClick(R.id.submitBtn)
    public void onsubmitBtnClick() {
        if (selectedFive != null)
            addLearntWords(selectedFive);
        //GameConstatnts.playGameNext(getActivity());
        /*Bundle bundle = GameConstatnts.findGameData("106");
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new ReadingFragment(), R.id.RL_CPA,
                    bundle, ReadingFragment.class.getSimpleName());
        }*/

    }

    public void addLearntWords(List<ScienceQuestion> selectedAnsList) {
        correctWordList = new ArrayList<>();
        wrongWordList = new ArrayList<>();
        if (selectedAnsList != null && !selectedAnsList.isEmpty()) {
            for (int i = 0; i < selectedAnsList.size(); i++) {
                if (selectedAnsList.get(i).getAnswer().equalsIgnoreCase(selectedAnsList.get(i).getUserAnswer())) {
                    KeyWords keyWords = new KeyWords();
                    keyWords.setResourceId(resId);
                    keyWords.setSentFlag(0);
                    keyWords.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                    String key = selectedAnsList.get(i).getQuestion();
                    keyWords.setKeyWord(key);
                    keyWords.setWordType("word");
                    keyWords.setTopic("");
                    AppDatabase.getDatabaseInstance(context).getKeyWordDao().insert(keyWords);
                    correctWordList.add("\n\n" + selectedAnsList.get(i).getQuestion());
                    addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid()), GameConstatnts.TRUE_FALSE, 10, 10, FC_Utility.getCurrentDateTime(), selectedAnsList.get(i).getUserAnswer());

                } else {
                    if (selectedAnsList.get(i).getUserAnswer() != null && !selectedAnsList.get(i).getUserAnswer().trim().equalsIgnoreCase("")) {
                        wrongWordList.add("\n\n" + selectedAnsList.get(i).getQuestion());
                        addScore(GameConstatnts.getInt(selectedAnsList.get(i).getQid()), GameConstatnts.TRUE_FALSE, 0, 10, FC_Utility.getCurrentDateTime(), selectedAnsList.get(i).getUserAnswer());
                    }
                }
            }
            if (!isTest) {
                showResult(correctWordList, wrongWordList);
            }
        }
        BackupDatabase.backup(getActivity());

    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label) {
        try {
            String deviceId = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("DeviceId");
            Score score = new Score();
            score.setSessionID(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
            score.setResourceID(resId);
            score.setQuestionId(wID);
            score.setScoredMarks(scoredMarks);
            score.setTotalMarks(totalMarks);
            score.setStudentID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            score.setGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_GROUP_ID, ""));
            score.setStartDateTime(resStartTime);
            score.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
            score.setLevel(4);
            score.setLabel(Word + " - " + Label);
            score.setSentFlag(0);
            AppDatabase.getDatabaseInstance(context).getScoreDao().insert(score);

            if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test)) {
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
                assessment.setEndDateTime(FC_Utility.getCurrentDateTime());
                assessment.setLevela(FC_Constants.currentLevel);
                assessment.setLabel("test: " + Label);
                assessment.setSentFlag(0);
                AppDatabase.getDatabaseInstance(context).getAssessmentDao().insert(assessment);
            }
            BackupDatabase.backup(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showResult(List<String> correctWord, List<String> wrongWord) {
        final CustomLodingDialog dialog = new CustomLodingDialog(getActivity(), R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_result_true_false);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        SansTextViewBold correct_keywords = dialog.findViewById(R.id.correct_keywords);
        SansTextViewBold correct_title = dialog.findViewById(R.id.dia_title);
        SansTextViewBold dia_title_wrong = dialog.findViewById(R.id.dia_title_wrong);
        SansTextViewBold wrong_keywords = dialog.findViewById(R.id.wrong_keywords);
        SansButton dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        correct_keywords.setText(correctWord.toString().substring(1, correctWord.toString().length() - 1));
        wrong_keywords.setText(wrongWord.toString().substring(1, wrongWord.toString().length() - 1));
        correct_keywords.setMovementMethod(new ScrollingMovementMethod());
        wrong_keywords.setMovementMethod(new ScrollingMovementMethod());


        dia_btn_yellow.setText("OK");
        correct_title.setText("correct sentences");
        dia_title_wrong.setText("wrong sentences");
        dia_btn_yellow.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
