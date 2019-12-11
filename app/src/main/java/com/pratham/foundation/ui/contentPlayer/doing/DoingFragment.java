package com.pratham.foundation.ui.contentPlayer.doing;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.customView.fancy_loaders_library.model.Line;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.services.stt.ContinuousSpeechService_New;
import com.pratham.foundation.services.stt.STT_Result_New;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
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

import static android.app.Activity.RESULT_OK;
import static com.pratham.foundation.database.AppDatabase.appDatabase;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.activityPhotoPath;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;


public class DoingFragment extends Fragment implements STT_Result_New.sttView,OnGameClose {
    /* @BindView(R.id.tittle)
        SansTextView tittle;*/
    @BindView(R.id.tv_question)
    TextView question;
    @BindView(R.id.iv_question_image)
    ImageView questionImage;
    @BindView(R.id.iv_question_gif)
    GifView questionGif;

    @BindView(R.id.vv_question)
    VideoView vv_question;

    @BindView(R.id.capture)
    ImageView capture;


   /* @BindView(R.id.RelativeLayout)
    RelativeLayout RelativeLayout;*/

    @BindView(R.id.preview)
    SansButton preview;

    @BindView(R.id.submit)
    SansButton submitBtn;

    @BindView(R.id.previous)
    ImageButton previous;

    @BindView(R.id.next)
    ImageButton next;

    @BindView(R.id.sub_questions_container)
    LinearLayout sub_questions_container;

    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;

    @BindView(R.id.camera_controll)
    LinearLayout camera_controll;

    @BindView(R.id.subQuestion)
    SansTextView subQuestion;


    @BindView(R.id.answer)
    SansTextView etAnswer;
    @BindView(R.id.btn_read_mic)
    ImageButton ib_mic;

   /* @BindView(R.id.bottom_bar1)
    LinearLayout bottom_control_container;*/

    String fileName;
    String questionPath;
    private Context context;
    private static final int CAMERA_REQUEST = 1;
    private String readingContentPath, contentPath, contentTitle, StudentID, resId, imageName, resStartTime;
    private int totalWordCount, learntWordCount,index=0;
    private ScienceQuestion scienceQuestion;
    private float perc;
    private boolean onSdCard;
    private List<ScienceQuestion> dataList;
    private String jsonName;
    private boolean isVideoQuestion = false;
    private List<ScienceQuestionChoice> scienceQuestionChoices;

    private float percScore = 0;
    private String answer;
    // public static SpeechRecognizer speech = null;
    private static boolean voiceStart = false;
    private static boolean[] correctArr;
    public static Intent intent;
    private Uri capturedImageUri;
    private ContinuousSpeechService_New continuousSpeechService;
    public Dialog myLoadingDialog;
    private String speechStartTime;
    boolean dialogFlg = false;

    public DoingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        continuousSpeechService = new ContinuousSpeechService_New(context, DoingFragment.this, FC_Constants.currentSelectedLanguage);
        continuousSpeechService.resetSpeechRecognizer();
        contentPath = getArguments().getString("contentPath");
        StudentID = getArguments().getString("StudentID");
        resId = getArguments().getString("resId");
        contentTitle = getArguments().getString("contentName");
        jsonName = getArguments().getString("jsonName");
        onSdCard = getArguments().getBoolean("onSdCard", false);
        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";
        EventBus.getDefault().register(this);
        imageName = "" + ApplicationClass.getUniqueID() + ".jpg";
        resStartTime = FC_Utility.getCurrentDateTime();
        addScore(0, "", 0, 0, resStartTime,FC_Utility.getCurrentDateTime(), jsonName + " " + GameConstatnts.START);
        getData();
    }

    private void getData() {
        try {
            InputStream is = new FileInputStream(readingContentPath + jsonName + ".json");
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
            contentProgress.setSessionId("" + FC_Constants.currentSession);
            contentProgress.setStudentId("" + FC_Constants.currentStudentID);
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
            perc = getPercentage();
            Collections.shuffle(dataList);
            for (int i = 0; i < dataList.size(); i++) {
                if (perc < 95) {
                    if (!checkWord("" + dataList.get(i).getTitle()) || dataList.get(i).getTitle().isEmpty()) {
                        scienceQuestion = dataList.get(i);
                        break;
                    }
                } else {
                    scienceQuestion = dataList.get(i);
                    break;
                }
            }
            //view.loadUI(listenAndWrittingModal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkWord(String wordStr) {
        try {
            String word = appDatabase.getKeyWordDao().checkWord(FC_Constants.currentStudentID, resId, wordStr);
            return word != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
       // count = appDatabase.getKeyWordDao().checkWordCount(FC_Constants.currentStudentID, resId);
        count = appDatabase.getKeyWordDao().checkUniqueWordCount(FC_Constants.currentStudentID, resId);
        return count;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_video_row, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        //hide camera controls for reading stt game
        if(jsonName.equalsIgnoreCase(GameConstatnts.READING_STT)){
            capture.setVisibility(View.GONE);
        }
        preview.setVisibility(View.GONE);
        setVideoQuestion();
    }

    public void setVideoQuestion() {
        if (scienceQuestion != null) {
            isVideoQuestion = false;
            fileName = scienceQuestion.getPhotourl();
            //getFileName(scienceQuestion.getQid(), scienceQuestion.getPhotourl());

      //questionPath = Environment.getExternalStorageDirectory().toString() + "/.Assessment/Content/Downloaded" + "/" + fileName;
            questionImage.setVisibility(View.GONE);
            questionGif.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
            if (scienceQuestion.getQuestion().trim().equalsIgnoreCase(""))
                question.setVisibility(View.GONE);
            else question.setText(scienceQuestion.getQuestion());

            question.setMovementMethod(new ScrollingMovementMethod());
          /*  if (!scienceQuestion.getInstruction().isEmpty())
                tittle.setText(scienceQuestion.getInstruction());*/
            if (fileName != null && !fileName.isEmpty()) {
               // RelativeLayout.setVisibility(View.VISIBLE);
                if (fileName.toLowerCase().endsWith(".jpeg") || fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".png")) {
                    questionPath = readingContentPath + fileName;
                    Glide.with(getActivity())
                            .load(questionPath)
                            .apply(new RequestOptions()
                                    .placeholder(Drawable.createFromPath(questionPath)))
                            .into(questionImage);
                    questionImage.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.VISIBLE);
                } else if (fileName.toLowerCase().endsWith(".gif")) {
                    questionPath = readingContentPath + fileName;
                    InputStream gif;
                    try {
                        gif = new FileInputStream(questionPath);
                        questionGif.setVisibility(View.VISIBLE);
                        relativeLayout.setVisibility(View.VISIBLE);
                        questionGif.setGifResource(gif);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        isVideoQuestion = true;
                        questionPath = readingContentPath + fileName;
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 1;
                        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(questionPath, MediaStore.Images.Thumbnails.MICRO_KIND);
                        BitmapDrawable ob = new BitmapDrawable(getResources(), thumb);
                        questionImage.setBackgroundDrawable(ob);
                        questionImage.setVisibility(View.VISIBLE);
                        relativeLayout.setVisibility(View.VISIBLE);
                    } catch (Exception e) {

                    }

                }
            }
            scienceQuestionChoices=scienceQuestion.getLstquestionchoice();
            if(scienceQuestionChoices!=null&& !scienceQuestionChoices.isEmpty()){
                loadSubQuestions();
            }else {
                sub_questions_container.setVisibility(View.GONE);
                previous.setVisibility(View.INVISIBLE);
                next.setVisibility(View.INVISIBLE);
            }

        } else {
            Toast.makeText(context, "data not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSubQuestions() {
        if (continuousSpeechService != null) {
            continuousSpeechService.onEndOfSpeech();
            // speech.stopListening();
            micPressed(0);
            voiceStart = false;
        }
        subQuestion.setText(scienceQuestionChoices.get(index).getSubQues());
        subQuestion.setMovementMethod(new ScrollingMovementMethod());
        if(scienceQuestionChoices.get(index).getUserAns().trim()!=null && !scienceQuestionChoices.get(index).getUserAns().isEmpty()){
            etAnswer.setText(scienceQuestionChoices.get(index).getUserAns());
        }else {
            etAnswer.setText("");
        }
        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                scienceQuestionChoices.get(index).setUserAns(s.toString());
                scienceQuestionChoices.get(index).setStartTime(speechStartTime);
                scienceQuestionChoices.get(index).setEndTime( FC_Utility.getCurrentDateTime());
            }
        });
        submitBtn.setVisibility(View.INVISIBLE);
        camera_controll.setVisibility(View.INVISIBLE);
        if (index == 0) {
            previous.setVisibility(View.INVISIBLE);
        } else {
            previous.setVisibility(View.VISIBLE);
        }
        if (index == (scienceQuestionChoices.size() - 1)) {
            submitBtn.setVisibility(View.VISIBLE);
            camera_controll.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
        } else {
            submitBtn.setVisibility(View.INVISIBLE);
            camera_controll.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
        }
    }

////////////////////////////////


    public void showLoader() {
        if (!dialogFlg) {
            dialogFlg = true;
            myLoadingDialog = new Dialog(context);
            myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myLoadingDialog.setContentView(R.layout.loading_dialog);
            myLoadingDialog.setCanceledOnTouchOutside(false);
            myLoadingDialog.show();
        }
    }


    @OnClick(R.id.btn_read_mic)
    public void onMicClicked() {
        callSTT();
    }

    public void callSTT() {
        if (!voiceStart) {
            voiceStart = true;
            micPressed(1);
            showLoader();
            speechStartTime= FC_Utility.getCurrentDateTime();
            continuousSpeechService.startSpeechInput();
            // speechService.startSpeechInput();
        } else {
            voiceStart = false;
            micPressed(0);
            showLoader();
            continuousSpeechService.stopSpeechInput();
            //speechService.stopSpeechInput();
        }
    }


    public void micPressed(int micPressed) {
        if (micPressed == 0) {
            ib_mic.setImageResource(R.drawable.ic_mic_black);
        } else if (micPressed == 1) {
            ib_mic.setImageResource(R.drawable.ic_stop_black);
        }
    }

    @Override
    public void silenceDetected() {


    }

    @Override
    public void stoppedPressed() {
        dismissLoadingDialog();
    }

    @Override
    public void sttEngineReady() {
        dismissLoadingDialog();
    }


    public void dismissLoadingDialog() {
        if (dialogFlg) {
            dialogFlg = false;
            if (myLoadingDialog != null)
                myLoadingDialog.dismiss();
        }
    }





    @Override
    public void onResume() {
        super.onResume();
        //   speechService.resetSpeechRecognizer();
        continuousSpeechService.resetSpeechRecognizer();
       /* if (speech != null) {
            micPressed(0);
            voiceStart = false;
        }*/
    }


    @Override
    public void Stt_onResult(ArrayList<String> matches) {
        micPressed(0);
//        ib_mic.stopRecording();

        System.out.println("LogTag" + " onResults");
//        ArrayList<String> matches = results;

        String sttResult = "";
//        String sttResult = matches.get(0);
        String sttQuestion;
        for (int i = 0; i < matches.size(); i++) {
            System.out.println("LogTag" + " onResults :  " + matches.get(i));

            if (matches.get(i).equalsIgnoreCase(scienceQuestion.getAnswer()))
                sttResult = matches.get(i);
            else sttResult = matches.get(0);
        }
        sttQuestion = scienceQuestion.getAnswer();
        String quesFinal = sttQuestion.replaceAll(STT_REGEX, "");


        String[] splitQues = quesFinal.split(" ");
        String[] splitRes = sttResult.split(" ");

        if (splitQues.length < splitRes.length)
            correctArr = new boolean[splitRes.length];
        else correctArr = new boolean[splitQues.length];


        for (int j = 0; j < splitRes.length; j++) {
            for (int i = 0; i < splitQues.length; i++) {
                if (splitRes[j].equalsIgnoreCase(splitQues[i])) {
                    // ((TextView) readChatFlow.getChildAt(i)).setTextColor(getResources().getColor(R.color.readingGreen));
                    correctArr[i] = true;
                    //sendClikChanger(1);
                    break;
                }
            }
        }


        int correctCnt = 0;
        for (int x = 0; x < correctArr.length; x++) {
            if (correctArr[x])
                correctCnt++;
        }
        percScore = ((float) correctCnt / (float) correctArr.length) * 100;
        Log.d("Punctu", "onResults: " + percScore);
        if (percScore >= 75) {

            for (int i = 0; i < splitQues.length; i++) {
                //((TextView) readChatFlow.getChildAt(i)).setTextColor(getResources().getColor(R.color.readingGreen));
                correctArr[i] = true;
            }

//            scrollView.setBackgroundResource(R.drawable.convo_correct_bg);
        }

        etAnswer.setText(sttResult);
        scienceQuestionChoices.get(index).setUserAns(sttResult);
        scienceQuestionChoices.get(index).setStartTime(speechStartTime);
        scienceQuestionChoices.get(index).setEndTime( FC_Utility.getCurrentDateTime());

        voiceStart = false;
        micPressed(0);
        continuousSpeechService.stopSpeechInput();
    }

    @OnClick(R.id.previous)
    public void onPreviousClick() {
        if (scienceQuestionChoices != null)
            if (index > 0) {
                index--;
                loadSubQuestions();
            }
    }

    @OnClick(R.id.next)
    public void onNextClick() {
        if (scienceQuestionChoices != null)
            if (index < (scienceQuestionChoices.size() - 1)) {
                index++;
                loadSubQuestions();
            }
    }

    ////////////////////////////
    @OnClick({R.id.iv_question_image})
    public void onVideoClicked() {
     /*   ZoomImageDialog zoomImageDialog = new ZoomImageDialog(getActivity(), path, scienceQuestion.getQtid());
        zoomImageDialog.show();*/
        if (isVideoQuestion) {
            Intent intent = new Intent(getActivity(), FullScreenVideo_.class);
            intent.putExtra("questionPath", questionPath);
            startActivity(intent);
        }
        // MediaController mediaController = new MediaController(getActivity());

      /*  questionImage.setVisibility(View.GONE);
        vv_question.setVisibility(View.VISIBLE);
        vv_question.setVideoPath(questionPath);
        vv_question.setMediaController(mediaController);
        mediaController.setAnchorView(vv_question);
        vv_question.setZOrderOnTop(true);
        vv_question.setZOrderMediaOverlay(true);
        vv_question.start();*/

    }

    @Override
    public void onPause() {
        super.onPause();
        vv_question.pause();
        if (continuousSpeechService != null) {
            continuousSpeechService.stopSpeechInput();
        }
        micPressed(0);
        voiceStart = false;
    }

    @OnClick(R.id.capture)
    public void captureClick() {
      /*  Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, CAMERA_REQUEST);*/
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(activityPhotoPath);
        if (!imagesFolder.exists()) imagesFolder.mkdirs();
        File image = new File(imagesFolder, imageName);
        capturedImageUri = Uri.fromFile(image);
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, capturedImageUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @OnClick(R.id.preview)
    public void previewClick() {
        File filePath = new File(activityPhotoPath + imageName);
        if (filePath.exists())
            ShowPreviewDialog(filePath);
    }

    @OnClick(R.id.submit)
    public void submitClick() {
        File filePath = new File(activityPhotoPath + imageName);
        if (filePath.exists()) {
            addLearntWords(scienceQuestion, imageName);
            imageName = null;
        } else {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.TRUE, this);
        }
    }

    public void addLearntWords(ScienceQuestion questionModel, String imageName) {
        if (imageName != null && !imageName.isEmpty()) {
            KeyWords keyWords = new KeyWords();
            keyWords.setResourceId(resId);
            keyWords.setSentFlag(0);
            keyWords.setStudentId(FC_Constants.currentStudentID);
            keyWords.setKeyWord(questionModel.getTitle());
            keyWords.setWordType("word");
            addScore(GameConstatnts.getInt(questionModel.getQid()), jsonName, 0, 0,resStartTime, FC_Utility.getCurrentDateTime(), imageName);
            appDatabase.getKeyWordDao().insert(keyWords);
            setCompletionPercentage();
            Toast.makeText(context, "inserted successfully", Toast.LENGTH_LONG).show();
            GameConstatnts.playGameNext(context, GameConstatnts.FALSE, this);
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, this);
        }
        BackupDatabase.backup(context);
    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime,String resEndTime, String Label) {
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
            score.setLabel(Word + "___" + Label);
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
                assessment.setLabel("test: ___" + Label);
                assessment.setSentFlag(0);
                appDatabase.getAssessmentDao().insert(assessment);
            }
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void ShowPreviewDialog(File path) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_image_preview_dialog);
        dialog.setCanceledOnTouchOutside(false);
        ImageView iv_dia_preview = dialog.findViewById(R.id.iv_dia_preview);
        SansButton dia_btn_cross = dialog.findViewById(R.id.dia_btn_cross);
        ImageButton camera = dialog.findViewById(R.id.camera);
        dialog.show();
        /*try {
            Bitmap bmImg = BitmapFactory.decodeFile("" + path);
            BitmapFactory.decodeStream(new FileInputStream(path));
            iv_dia_preview.setImageBitmap(bmImg);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        iv_dia_preview.setImageURI(capturedImageUri);
        dia_btn_cross.setOnClickListener(v -> {
            dialog.dismiss();
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                captureClick();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("codes", String.valueOf(requestCode) + resultCode);
        try {
            if (requestCode == CAMERA_REQUEST) {
                if (requestCode == CAMERA_REQUEST) {
                    if (resultCode == RESULT_OK) {
                        capture.setVisibility(View.GONE);
                        preview.setVisibility(View.VISIBLE);
                    }
                }
                //Bitmap photo = (Bitmap) data.getExtras().get("data");
               /* preview.setVisibility(View.VISIBLE);
                preview.setImageBitmap(photo);
                preview.setScaleType(ImageView.ScaleType.FIT_XY);*/

               // createDirectoryAndSaveFile(photo, imageName);
               /* capture.setVisibility(View.GONE);
                preview.setVisibility(View.VISIBLE);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        try {

            File direct = new File(activityPhotoPath);
            File file = new File(direct, fileName);

            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // isPhotoSaved = true;
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/
    @Override
    public void gameClose() {
        //add quetions only attempted (correct or wrong any)
        if(scienceQuestionChoices!=null && !scienceQuestionChoices.isEmpty()) {
            for (int i = 0; i <scienceQuestionChoices.size() ; i++) {
                if(scienceQuestionChoices.get(i).getUserAns()!=null && !scienceQuestionChoices.get(i).getUserAns().trim().isEmpty()){
                    addScore(GameConstatnts.getInt(scienceQuestion.getQid()), jsonName, 0, 0,scienceQuestionChoices.get(i).getStartTime(), scienceQuestionChoices.get(i).getEndTime(), scienceQuestionChoices.get(i).toString());
                }
            }
        }
        addScore(0, "", 0, 0, resStartTime,FC_Utility.getCurrentDateTime(), jsonName + " " + GameConstatnts.END);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

        if (continuousSpeechService != null) {
            continuousSpeechService.onEndOfSpeech();
            // speech.stopListening();
            micPressed(0);
            voiceStart = false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        if (!scienceQuestion.getInstruction().isEmpty())
            GameConstatnts.showGameInfo(getActivity(), scienceQuestion.getInstruction(),readingContentPath+scienceQuestion.getInstructionUrl());
    }
}
