package com.pratham.foundation.ui.contentPlayer.word_writting;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.modalclasses.ScienceQuestion;
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
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM;
import static com.pratham.foundation.utility.FC_Constants.activityPhotoPath;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EFragment(R.layout.fragment_word_writing)
public class WordWritingFragment extends Fragment
        implements WordWritingContract.WordWritingView, OnGameClose {

    @Bean(WordWritingPresenter.class)
    WordWritingContract.WordWritingPresenter presenter;

    private int index = 0;
    @ViewById(R.id.previous)
    ImageButton previous;
    @ViewById(R.id.camera_controll)
    LinearLayout camera_controll;
    @ViewById(R.id.next)
    ImageButton next;
    @ViewById(R.id.preview)
    SansButton preview;
    @ViewById(R.id.text)
    TextView text;
    @ViewById(R.id.submit)
    SansButton submitBtn;

    @ViewById(R.id.capture)
    ImageButton capture;
    /* @ViewById(R.id.title)
     SansTextView title;*/
    private Uri capturedImageUri;
    private List<String> paragraphWords;
    private static final int CAMERA_REQUEST = 1;
    private String imageName = null, imagePath;
    private String contentPath, contentTitle, StudentID, resId, readingContentPath, resStartTime;
    private boolean onSdCard;
    private List<ScienceQuestion> questionModel;

    @AfterViews
    protected void initiate() {
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
        EventBus.getDefault().register(this);
        preview.setVisibility(View.GONE);
        imageName = "" + ApplicationClass.getUniqueID() + ".jpg";

        presenter.setView(WordWritingFragment.this, resId, readingContentPath);
        presenter.getData();
     /*   if (questionModel != null)
            GameConstatnts.showGameInfo(getActivity(), questionModel.get(index).getInstruction(), readingContentPath + questionModel.get(index).getInstructionUrl());
*/
        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime, GameConstatnts.PARAGRAPH_WRITING + " " + GameConstatnts.START);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            text.setAutoSizeTextTypeWithDefaults(AUTO_SIZE_TEXT_TYPE_UNIFORM);
        } else {
            TextViewCompat.setAutoSizeTextTypeWithDefaults(text, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
    }

    @Override
    public void showParagraph(List<ScienceQuestion> questionModel) {
        this.questionModel = questionModel;
        /*  title.setText(questionModel.get(0).getInstruction());*/
        paragraphWords = new ArrayList<>();
        for (int i = 0; i < questionModel.size(); i++) {
            paragraphWords.add(questionModel.get(i).getQuestion());
        }
        // paragraphWords = questionModel.getQuestion().trim().split("(?<=\\.\\s)|(?<=[?!]\\s)");
       /* SentenceAdapter arrayAdapter = new SentenceAdapter(paragraphWords, getActivity());
        paragraph.setAdapter(arrayAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        paragraph.setLayoutManager(layoutManager);*/
        ShowSingleQuestion();
    }

    private void ShowSingleQuestion() {
        text.setText(paragraphWords.get(index));
        submitBtn.setVisibility(View.INVISIBLE);
        camera_controll.setVisibility(View.INVISIBLE);
        // preview.setVisibility(View.INVISIBLE);
        if (index == 0) {
            previous.setVisibility(View.INVISIBLE);
        } else {
            previous.setVisibility(View.VISIBLE);
        }
        if (index == (paragraphWords.size() - 1)) {
            submitBtn.setVisibility(View.VISIBLE);
            camera_controll.setVisibility(View.VISIBLE);
            // preview.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
        } else {
            submitBtn.setVisibility(View.INVISIBLE);
            camera_controll.setVisibility(View.INVISIBLE);
            //preview.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
        }
    }


    @Click(R.id.previous)
    public void onPreviousClick() {
        if (paragraphWords != null)
            if (index > 0) {
                index--;
                ShowSingleQuestion();
            }
    }

    @Click(R.id.next)
    public void onNextClick() {
        if (paragraphWords != null)
            if (index < (paragraphWords.size() - 1)) {
                index++;
                ShowSingleQuestion();
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
            presenter.addLearntWords(questionModel, imageName);
            imageName = null;
        } else {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.TRUE, this);
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

                /*Bitmap photo = (Bitmap) data.getExtras().get("data");
                preview.setVisibility(View.VISIBLE);
                preview.setImageBitmap(photo);
                preview.setScaleType(ImageView.ScaleType.FIT_XY);
                presenter.createDirectoryAndSaveFile(photo, imageName);
                preview.setVisibility(View.VISIBLE);
                capture.setVisibility(View.GONE);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime, GameConstatnts.PARAGRAPH_WRITING + " " + GameConstatnts.END);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        GameConstatnts.showGameInfo(getActivity(), questionModel.get(index).getInstruction(), readingContentPath + questionModel.get(index).getInstructionUrl());
    }
}
