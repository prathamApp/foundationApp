package com.pratham.foundation.ui.contentPlayer.doing;

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
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
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
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.services.stt.ContinuousSpeechService_New;
import com.pratham.foundation.services.stt.STT_Result_New;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.activityPhotoPath;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EFragment(R.layout.layout_video_row)
public class DoingFragment extends Fragment implements STT_Result_New.sttView, OnGameClose, DoingFragmentContract.DoingFragmentView {
    /* @BindView(R.id.tittle)
        SansTextView tittle;*/
    @ViewById(R.id.tv_question)
    TextView question;
    @ViewById(R.id.iv_question_image)
    ImageView questionImage;
    @ViewById(R.id.iv_question_gif)
    GifView questionGif;
    @ViewById(R.id.vv_question)
    VideoView vv_question;
    @ViewById(R.id.capture)
    ImageView capture;
    @ViewById(R.id.reset_btn)
    SansButton reset_btn;
   /* @BindView(R.id.RelativeLayout)
    RelativeLayout RelativeLayout;*/
   @ViewById(R.id.preview)
    SansButton preview;
    @ViewById(R.id.submit)
    SansButton submitBtn;
    @ViewById(R.id.previous)
    ImageButton previous;
    @ViewById(R.id.next)
    ImageButton next;
    @ViewById(R.id.sub_questions_container)
    LinearLayout sub_questions_container;
    @ViewById(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @ViewById(R.id.camera_controll)
    LinearLayout camera_controll;
    @ViewById(R.id.subQuestion)
    SansTextView subQuestion;
    @ViewById(R.id.answer)
    SansTextView etAnswer;
    @ViewById(R.id.btn_read_mic)
    ImageButton ib_mic;

    @Bean(DoingFragmentPresenter.class)
    DoingFragmentContract.DoingFragmentPresenter presenter;

    String fileName;
    String questionPath;
    private Context context;
    private static final int CAMERA_REQUEST = 1;
    private String readingContentPath, contentPath, contentTitle, StudentID, resId, imageName, resStartTime;
    private int index = 0;
    private ScienceQuestion scienceQuestion;
    private float perc;
    private boolean onSdCard;
    private List<ScienceQuestion> dataList;
    private String jsonName;
    private boolean isVideoQuestion = false;
    private List<ScienceQuestionChoice> scienceQuestionChoices;

    private float percScore = 0;
    private String answer;

    private static boolean voiceStart = false;
    private static boolean[] correctArr;
    public static Intent intent;
    private Uri capturedImageUri;
    private ContinuousSpeechService_New continuousSpeechService;
    public CustomLodingDialog myLoadingDialog;
    private String speechStartTime;
    boolean dialogFlg = false;

    public DoingFragment() {
        // Required empty public constructor
    }

   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
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
    } */

    @AfterViews
    public void initiate() {
        context = getActivity();
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

        if (!GameConstatnts.WATCHING_VIDEO.equalsIgnoreCase(jsonName)) {
            camera_controll.setVisibility(View.VISIBLE);
        }
        //hide camera controls for reading stt game
        if (jsonName.equalsIgnoreCase(GameConstatnts.READING_STT)) {
            capture.setVisibility(View.GONE);
        }
        preview.setVisibility(View.GONE);


        imageName = "" + ApplicationClass.getUniqueID() + ".jpg";
        presenter.setView(this, jsonName, resId,contentTitle);
        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), jsonName + " " + GameConstatnts.START,resId);
        presenter.getData(readingContentPath);
    }









    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_video_row, container, false);
    }*/

/*    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // ButterKnife.bind(this, view);
        if (!GameConstatnts.WATCHING_VIDEO.equalsIgnoreCase(jsonName)) {
            camera_controll.setVisibility(View.VISIBLE);
        }
        //hide camera controls for reading stt game
        if(jsonName.equalsIgnoreCase(GameConstatnts.READING_STT)){
            capture.setVisibility(View.GONE);
        }
        preview.setVisibility(View.GONE);
        setVideoQuestion();
    }*/

    public void setVideoQuestion(ScienceQuestion scienceQuestion) {
        if (scienceQuestion != null) {
            this.scienceQuestion = scienceQuestion;
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
                continuousSpeechService = new ContinuousSpeechService_New(context, DoingFragment.this, FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI));
                continuousSpeechService.resetSpeechRecognizer();
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
        etAnswer.setMovementMethod(new ScrollingMovementMethod());
        if(scienceQuestionChoices.get(index).getUserAns().trim()!=null && !scienceQuestionChoices.get(index).getUserAns().isEmpty()){
            myAns=scienceQuestionChoices.get(index).getUserAns();
            etAnswer.setText(myAns);
        }else {
            myAns="";
            etAnswer.setText(myAns);
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
               /* scienceQuestionChoices.get(index).setUserAns(s.toString());
                scienceQuestionChoices.get(index).setStartTime(speechStartTime);
                scienceQuestionChoices.get(index).setEndTime( FC_Utility.getCurrentDateTime());*/
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
            if (!GameConstatnts.WATCHING_VIDEO.equalsIgnoreCase(jsonName)) {
                camera_controll.setVisibility(View.VISIBLE);
            }
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
            myLoadingDialog = new CustomLodingDialog(context);
            myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myLoadingDialog.setContentView(R.layout.loading_dialog);
            myLoadingDialog.setCanceledOnTouchOutside(false);
            myLoadingDialog.show();
        }
    }


    @Click(R.id.btn_read_mic)
    public void onMicClicked() {
        if (scienceQuestion != null)
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
            showButtons();
        } else if (micPressed == 1) {
            ib_mic.setImageResource(R.drawable.ic_stop_black);
            hideButtons();
        }
    }
    private void hideButtons() {
        previous.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        reset_btn.setVisibility(View.INVISIBLE);
        submitBtn.setVisibility(View.INVISIBLE);
        camera_controll.setVisibility(View.INVISIBLE);
    }

    private void showButtons() {
        if (index == 0) {
            previous.setVisibility(View.INVISIBLE);
        } else {
            previous.setVisibility(View.VISIBLE);
        }
        if (index == (scienceQuestionChoices.size() - 1)) {
            submitBtn.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
        } else {
            submitBtn.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
        }
        if (index == (scienceQuestionChoices.size() - 1)) {
            submitBtn.setVisibility(View.VISIBLE);
            if (!GameConstatnts.WATCHING_VIDEO.equalsIgnoreCase(jsonName)) {
                camera_controll.setVisibility(View.VISIBLE);
            }

        }
        reset_btn.setVisibility(View.VISIBLE);
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
        if (continuousSpeechService != null)
            continuousSpeechService.resetSpeechRecognizer();
       /* if (speech != null) {
            micPressed(0);
            voiceStart = false;
        }*/
    }


    @Override
    public void Stt_onResult(ArrayList<String> matches) {
      //  micPressed(0);
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
        myAns += " "+sttResult;
        etAnswer.setText(myAns);
        scienceQuestionChoices.get(index).setUserAns(myAns);
       // scienceQuestionChoices.get(index).setStartTime(speechStartTime);
       // scienceQuestionChoices.get(index).setEndTime( FC_Utility.getCurrentDateTime());

//        voiceStart = false;
//        micPressed(0);
//        continuousSpeechService.stopSpeechInput();
    }

    String myAns="";
    @Click(R.id.previous)
    public void onPreviousClick() {
        if (scienceQuestionChoices != null)
            if (index > 0) {
                index--;
                loadSubQuestions();
            }
    }

    @Click(R.id.reset_btn)
    public void reset(){
        myAns="";
        etAnswer.setText(myAns);
        scienceQuestionChoices.get(index).setUserAns(myAns);
       // scienceQuestionChoices.get(index).setStartTime("");
      //  scienceQuestionChoices.get(index).setEndTime("");
    }

    @Click(R.id.next)
    public void onNextClick() {
        if (scienceQuestionChoices != null)
            if (index < (scienceQuestionChoices.size() - 1)) {
                index++;
                loadSubQuestions();
            }
    }

    ////////////////////////////
    @Click({R.id.iv_question_image})
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
            micPressed(0);
            voiceStart = false;
        }
    }

    @Click(R.id.capture)
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

    @Click(R.id.preview)
    public void previewClick() {
        File filePath = new File(activityPhotoPath + imageName);
        if (filePath.exists())
            ShowPreviewDialog(filePath);
    }

    @Click(R.id.submit)
    public void submitClick() {
        File filePath = new File(activityPhotoPath + imageName);
        if (filePath.exists()) {
            presenter.addLearntWords(scienceQuestion, imageName);
            imageName = null;
        } else {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.TRUE, this);
        }
    }


    private void ShowPreviewDialog(File path) {
        final CustomLodingDialog dialog = new CustomLodingDialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_image_preview_dialog);
        dialog.setCanceledOnTouchOutside(false);
        ImageView iv_dia_preview = dialog.findViewById(R.id.iv_dia_preview);
        SansButton dia_btn_cross = dialog.findViewById(R.id.dia_btn_cross);
        ImageButton camera = dialog.findViewById(R.id.camera);
        dialog.show();


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
                        scienceQuestion.setStartTime(FC_Utility.getCurrentDateTime());
                        scienceQuestion.setEndTime(FC_Utility.getCurrentDateTime());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void gameClose() {
        //add questions only attempted (correct or wrong any)
        if(scienceQuestionChoices!=null && !scienceQuestionChoices.isEmpty()) {
            int count=0;
            for (int i = 0; i <scienceQuestionChoices.size() ; i++) {
                if(scienceQuestionChoices.get(i).getUserAns()!=null && !scienceQuestionChoices.get(i).getUserAns().trim().isEmpty()){
                    count++;
                    presenter.addScore(GameConstatnts.getInt(scienceQuestion.getQid()), jsonName, 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), scienceQuestionChoices.get(i).toString(),resId);
                }
            }
            returnScore(scienceQuestionChoices.size(),count);
        }
        presenter.addScore(0, "", 0, 0, resStartTime, FC_Utility.getCurrentDateTime(), jsonName + " " + GameConstatnts.END,resId);
    }
    private void returnScore(int totalscore,int scoredmarks){
        GameConstatnts.postScoreEvent(totalscore, scoredmarks);
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
