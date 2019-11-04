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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
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
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.foundation.database.AppDatabase.appDatabase;


public class DoingFragment extends Fragment implements OnGameClose {

    @BindView(R.id.tv_question)
    TextView question;
    @BindView(R.id.iv_question_image)
    ImageView questionImage;
    @BindView(R.id.iv_question_gif)
    GifView questionGif;

    @BindView(R.id.vv_question)
    VideoView vv_question;

    @BindView(R.id.tittle)
    SansTextView tittle;

    @BindView(R.id.RelativeLayout)
    RelativeLayout RelativeLayout;

    String fileName;
    String questionPath;
    private Context context;
    private String resourcePath;
    private static final int CAMERA_REQUEST = 1;


    private String readingContentPath, contentPath, contentTitle, StudentID, resId, imageName, resStartTime;
    private int totalWordCount, learntWordCount;
    private ScienceQuestion scienceQuestion;
    private float perc;
    private boolean onSdCard;
    private List<ScienceQuestion> dataList;
    private String jsonName;

    public DoingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        // speechService = new ContinuousSpeechService(context, ReadingFragment.this);
        // speechService.resetSpeechRecognizer();
        contentPath = getArguments().getString("contentPath");
        StudentID = getArguments().getString("StudentID");
        resId = getArguments().getString("resId");
        contentTitle = getArguments().getString("contentName");
        jsonName = getArguments().getString("jsonName");
        if (getArguments().getBoolean("onSdCard", false)) onSdCard = true;
        else onSdCard = false;
        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + "/.FCA/English/Game/" + contentPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + "/.FCA/English/Game/" + contentPath + "/";
        resStartTime = FC_Utility.getCurrentDateTime();
        addScore(0, "", 0, 0, resStartTime, jsonName + " " + GameConstatnts.START);
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
            if (word != null)
                return true;
            else
                return false;
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
        count = appDatabase.getKeyWordDao().checkWordCount(FC_Constants.currentStudentID, resId);
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

        setVideoQuestion();
    }

    public void setVideoQuestion() {
        if (scienceQuestion != null) {
            fileName = scienceQuestion.getPhotourl();
            //getFileName(scienceQuestion.getQid(), scienceQuestion.getPhotourl());

//        questionPath = Environment.getExternalStorageDirectory().toString() + "/.Assessment/Content/Downloaded" + "/" + fileName;
            questionImage.setVisibility(View.GONE);
            questionGif.setVisibility(View.GONE);
            question.setText(scienceQuestion.getQuestion());
            if (scienceQuestion.getQuestion().equalsIgnoreCase(""))
                question.setVisibility(View.GONE);
            else question.setText(scienceQuestion.getQuestion());

            question.setMovementMethod(new ScrollingMovementMethod());
            if (!scienceQuestion.getInstruction().isEmpty())
                tittle.setText(scienceQuestion.getInstruction());
            if (fileName != null && !fileName.isEmpty()) {
                RelativeLayout.setVisibility(View.VISIBLE);
                if (fileName.toLowerCase().endsWith(".jpeg") || fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".png")) {
                    questionPath = readingContentPath + fileName;
                    Glide.with(getActivity())
                            .load(questionPath)
                            .apply(new RequestOptions()
                                    .placeholder(Drawable.createFromPath(questionPath)))
                            .into(questionImage);
                    questionImage.setVisibility(View.VISIBLE);
                } else if (fileName.toLowerCase().endsWith(".gif")) {
                    questionPath = readingContentPath + fileName;
                    InputStream gif;
                    try {
                        gif = new FileInputStream(questionPath);
                        questionGif.setVisibility(View.VISIBLE);
                        questionGif.setGifResource(gif);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        questionPath = readingContentPath + fileName;
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 1;
                        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(questionPath, MediaStore.Images.Thumbnails.MICRO_KIND);
                        BitmapDrawable ob = new BitmapDrawable(getResources(), thumb);
                        questionImage.setBackgroundDrawable(ob);
                        questionImage.setVisibility(View.VISIBLE);
                    } catch (Exception e) {

                    }

                }
            } else {
                RelativeLayout.setVisibility(View.INVISIBLE);
            }

        } else {
            Toast.makeText(context, "data not found", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.iv_question_image})
    public void onVideoClicked() {
     /*   ZoomImageDialog zoomImageDialog = new ZoomImageDialog(getActivity(), path, scienceQuestion.getQtid());
        zoomImageDialog.show();*/

        MediaController mediaController = new MediaController(getActivity());

        questionImage.setVisibility(View.GONE);
        vv_question.setVisibility(View.VISIBLE);
        vv_question.setVideoPath(questionPath);
        vv_question.setMediaController(mediaController);
        mediaController.setAnchorView(vv_question);
        vv_question.setZOrderOnTop(true);
        vv_question.setZOrderMediaOverlay(true);
        vv_question.start();

    }

    @Override
    public void onPause() {
        super.onPause();
        vv_question.pause();
    }

    @OnClick(R.id.capture)
    public void captureClick() {
        imageName = "" + ApplicationClass.getUniqueID();
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, CAMERA_REQUEST);
    }

    @OnClick(R.id.preview)
    public void previewClick() {
        File filePath = new File(Environment.getExternalStorageDirectory().toString() + "/.FC/Internal/photos/" + imageName);
        if (filePath.exists())
            ShowPreviewDialog(filePath);
    }

    @OnClick(R.id.submit)
    public void submitClick() {
        File filePath = new File(Environment.getExternalStorageDirectory().toString() + "/.FC/Internal/photos/" + imageName);
        if (filePath.exists()) {
            addLearntWords(scienceQuestion, imageName);
            imageName = null;
        } else {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.TRUE, this);
        }
        // GameConstatnts.playGameNext(getActivity());
       /* Bundle bundle = GameConstatnts.findGameData("105");
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new ListeningAndWritting_(), R.id.RL_CPA,
                    bundle, ListeningAndWritting_.class.getSimpleName());
        }*/

    }

    public void addLearntWords(ScienceQuestion questionModel, String imageName) {
        if (imageName != null && !imageName.isEmpty()) {
            KeyWords keyWords = new KeyWords();
            keyWords.setResourceId(resId);
            keyWords.setSentFlag(0);
            keyWords.setStudentId(FC_Constants.currentStudentID);
            keyWords.setKeyWord(questionModel.getTitle());
            keyWords.setWordType("word");
            addScore(GameConstatnts.getInt(questionModel.getQid()), jsonName, 0, 0, FC_Utility.getCurrentDateTime(), imageName);
            appDatabase.getKeyWordDao().insert(keyWords);
            Toast.makeText(context, "inserted succussfully", Toast.LENGTH_LONG).show();
            GameConstatnts.playGameNext(context, GameConstatnts.FALSE, (OnGameClose) this);
        } else {
            GameConstatnts.playGameNext(context, GameConstatnts.TRUE, (OnGameClose) this);
        }
        BackupDatabase.backup(context);
    }

    public void addScore(int wID, String Word, int scoredMarks, int totalMarks, String resStartTime, String Label) {
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
            score.setEndDateTime(FC_Utility.getCurrentDateTime());
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
                assessment.setEndDateTime(FC_Utility.getCurrentDateTime());
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
        ImageButton dia_btn_cross = dialog.findViewById(R.id.dia_btn_cross);

        dialog.show();
        try {
            Bitmap bmImg = BitmapFactory.decodeFile("" + path);
            BitmapFactory.decodeStream(new FileInputStream(path));
            iv_dia_preview.setImageBitmap(bmImg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dia_btn_cross.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("codes", String.valueOf(requestCode) + resultCode);
        try {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
               /* preview.setVisibility(View.VISIBLE);
                preview.setImageBitmap(photo);
                preview.setScaleType(ImageView.ScaleType.FIT_XY);*/
                createDirectoryAndSaveFile(photo, imageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        try {

            File direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FC");
            if (!direct.exists()) direct.mkdir();
            direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FC/Internal");
            if (!direct.exists()) direct.mkdir();
            direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FC/Internal/photos");
            if (!direct.exists()) direct.mkdir();

            File file = new File(direct, fileName);

            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // isPhotoSaved = true;
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void gameClose() {
        addScore(0, "", 0, 0, resStartTime, jsonName + " " + GameConstatnts.END);
    }
}
