package com.pratham.foundation.ui.contentPlayer.paragraph_writing;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.services.shared_preferences.FastSave;
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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.pratham.foundation.utility.FC_Constants.activityPhotoPath;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EFragment(R.layout.fragment_paragraph_writing)
public class ParagraphWritingFragment extends Fragment implements ParagraphWritingContract.ParagraphWritingView, OnGameClose {

    @Bean(ParagraphWritingPresenter.class)
    ParagraphWritingContract.ParagraphWritingPresenter presenter;

    private int index = 0;
    @ViewById(R.id.paragraph)
    RecyclerView paragraph;
    @ViewById(R.id.scrollView)
    ScrollView scrollView;

    @ViewById(R.id.capture)
    ImageButton capture;

    @ViewById(R.id.next)
    ImageButton next;
    @ViewById(R.id.previous)
    ImageButton previous;

    @ViewById(R.id.play_button)
    ImageButton play;

    @ViewById(R.id.play_button_control)
    LinearLayout play_button_control;
    @ViewById(R.id.camera_controll)
    LinearLayout camera_controll;
    @ViewById(R.id.submit)
    SansButton submitBtn;
   /* @ViewById(R.id.replay)
    ImageButton replay;*/

    /*@ViewById(R.id.previous)
    Button previous;*/
   /* @ViewById(R.id.capture)
    ImageView capture;*/
  /*  @ViewById(R.id.next)
    Button next;*/

    @ViewById(R.id.preview)
    SansButton preview;
 /*   @ViewById(R.id.title)
    SansTextView title;*/

    private String[] paragraphWords;
    // private RelativeLayout.LayoutParams viewParam;
   /* private LinearLayoutManager layoutManager;
    private RecyclerView.SmoothScroller smoothScroller;*/
    private static final int CAMERA_REQUEST = 1;
    private String contentPath, contentTitle, StudentID, resId, readingContentPath, resStartTime;
    private boolean onSdCard;
    private List<ScienceQuestion> questionModel;
    private String jsonName;
    MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private String REGEXF = "(?<=\\.\\s)|(?<=[?!]\\s)|(?<=।)|(?<=\\|)";
    @AfterViews
    protected void initiate() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            contentPath = bundle.getString("contentPath");
            StudentID = bundle.getString("StudentID");
            resId = bundle.getString("resId");
            contentTitle = bundle.getString("contentName");
            onSdCard = bundle.getBoolean("onSdCard", false);
            jsonName = getArguments().getString("jsonName");
            if (onSdCard)
                readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";
        }
        EventBus.getDefault().register(this);

        preview.setVisibility(View.GONE);
        mediaPlayer = new MediaPlayer();
        presenter.setView(ParagraphWritingFragment.this, resId, readingContentPath, jsonName);
        presenter.getData();
        if (!FastSave.getInstance().getString(FC_Constants.CURRENT_SUBJECT, "").equalsIgnoreCase("Science") && jsonName.equalsIgnoreCase(GameConstatnts.PARAGRAPH_WRITING)) {
            play_button_control.setVisibility(View.GONE);
        }
        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime,FC_Utility.getCurrentDateTime(), jsonName + " " + GameConstatnts.START);
    }

    @Override
    public void showParagraph(List<ScienceQuestion> questionModel) {
        this.questionModel = questionModel;
        for (int i = 0; i < questionModel.size(); i++) {
            questionModel.get(i).setUserAnswer("" + ApplicationClass.getUniqueID() + ".jpg");
        }
        showSingleParagraph();
    }

    private void showSingleParagraph() {
        paragraphWords = questionModel.get(index).getQuestion().trim().split(REGEXF);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(readingContentPath + "/" + questionModel.get(index).getPhotourl());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SentenceAdapter arrayAdapter = new SentenceAdapter(Arrays.asList(paragraphWords), getActivity());
        paragraph.setAdapter(arrayAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        paragraph.setLayoutManager(layoutManager);
        submitBtn.setVisibility(View.INVISIBLE);
        if (questionModel.get(index).getUserAnswer() != null && !questionModel.get(index).getUserAnswer().isEmpty()) {
            File filePath = new File(activityPhotoPath + questionModel.get(index).getUserAnswer());
            if (filePath.exists()) {
                capture.setVisibility(View.GONE);
                preview.setVisibility(View.VISIBLE);
            } else {
                capture.setVisibility(View.VISIBLE);
                preview.setVisibility(View.GONE);
            }
        } else {
            capture.setVisibility(View.VISIBLE);
            preview.setVisibility(View.GONE);
        }
        if (index == 0) {
            previous.setVisibility(View.INVISIBLE);
        } else {
            previous.setVisibility(View.VISIBLE);
        }
        if (index == (questionModel.size() - 1)) {
            submitBtn.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
        } else {
            submitBtn.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.previous)
    public void onPreviousClick() {
        if (questionModel != null)
            if (index > 0) {
                index--;
                showSingleParagraph();
            }
    }

    @Click(R.id.next)
    public void onNextClick() {
        if (questionModel != null)
            if (index < (questionModel.size() - 1)) {
                index++;
                showSingleParagraph();
            }
    }


 /*   private void highlightText() {
        // paragraph.smoothScrollToPosition(index);
        View view = paragraph.getChildAt(index);
        paragraph.getLayoutManager().scrollToPosition(index + 1);
        view.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_border_yellow));
    }*/

    @Click(R.id.capture)
    public void captureClick() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(activityPhotoPath);
        if (!imagesFolder.exists()) imagesFolder.mkdirs();
        File image = new File(imagesFolder, questionModel.get(index).getUserAnswer());
        Uri capturedImageUri = Uri.fromFile(image);
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, capturedImageUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Click(R.id.preview)
    public void previewClick() {
        File filePath = new File(activityPhotoPath + questionModel.get(index).getUserAnswer());
        if (filePath.exists())
            ShowPreviewDialog(filePath);
    }

    @Click(R.id.submit)
    public void submitClick() {
        if (checkIsPlayed(questionModel)) {
            presenter.addLearntWords(questionModel);
        } else {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.TRUE, this);
        }
    }

    private boolean checkIsPlayed(List<ScienceQuestion> questionModel) {
        for (int i = 0; i < questionModel.size(); i++) {
            if (presenter.checkIsAttempted(questionModel.get(i))) {
                return true;
            }
        }
        return false;
    }

    private void ShowPreviewDialog(File path) {
        final Dialog dialog = new CustomLodingDialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_image_preview_dialog);
        dialog.setCanceledOnTouchOutside(false);
        ImageView iv_dia_preview = dialog.findViewById(R.id.iv_dia_preview);
        SansButton dia_btn_cross = dialog.findViewById(R.id.dia_btn_cross);
        ImageButton camera = dialog.findViewById(R.id.camera);

        dialog.show();
        File imagesFolder = new File(activityPhotoPath);
        if (!imagesFolder.exists()) imagesFolder.mkdirs();
        File image = new File(imagesFolder, questionModel.get(index).getUserAnswer());
        Uri capturedImageUri = Uri.fromFile(image);
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
                        questionModel.get(index).setStartTime(FC_Utility.getCurrentDateTime());
                        questionModel.get(index).setEndTime(FC_Utility.getCurrentDateTime());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.play_button)
    public void onPlayClick() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                setPlayImage();
            } else {
                mediaPlayer.start();
                setPauseImage();
            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    setPlayImage();
                }
            });
        }
    }

    @Click(R.id.replay)
    public void onStopClick() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                setPlayImage();
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(readingContentPath + "/" + questionModel.get(index).getPhotourl());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void setPlayImage() {
        play.setImageDrawable(getActivity().getDrawable(R.drawable.ic_play_arrow_black));
        play.setBackground(getActivity().getResources().getDrawable(R.drawable.button_green));
    }

    private void setPauseImage() {
        play.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pause_black));
        play.setBackground(getActivity().getResources().getDrawable(R.drawable.button_yellow));
    }

    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime,FC_Utility.getCurrentDateTime(), jsonName + " " + GameConstatnts.END);
    }

    @Override
    public void onStop() {
        mediaPlayer.stop();
        setPlayImage();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        GameConstatnts.showGameInfo(getActivity(), questionModel.get(index).getInstruction(), readingContentPath + questionModel.get(index).getInstructionUrl());
    }
}
