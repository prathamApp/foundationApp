package com.pratham.foundation.ui.contentPlayer.paragraph_writing;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;
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
import java.util.Arrays;

import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.pratham.foundation.utility.FC_Constants.activityPhotoPath;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EFragment(R.layout.fragment_paragraph_writing)
public class ParagraphWritingFragment extends Fragment
        implements ParagraphWritingContract.ParagraphWritingView, OnGameClose {

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
    private String imageName = null;
    private String contentPath, contentTitle, StudentID, resId, readingContentPath, resStartTime;
    private boolean onSdCard;
    private ScienceQuestion questionModel;
    private Uri capturedImageUri;

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
        next.setVisibility(View.GONE);
        previous.setVisibility(View.GONE);
        imageName = "" + ApplicationClass.getUniqueID() + ".jpg";
        presenter.setView(ParagraphWritingFragment.this, resId, readingContentPath);
        presenter.getData();
        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime, GameConstatnts.PARAGRAPH_WRITING + " " + GameConstatnts.START);
    }

    @Override
    public void showParagraph(ScienceQuestion questionModel) {
        this.questionModel = questionModel;
      //  File filePath = new File(activityPhotoPath + imageName);
        /*    title.setText(questionModel.getInstruction());*/
        paragraphWords = questionModel.getQuestion().trim().split("(?<=\\.\\s)|(?<=[?!]\\s)");
        SentenceAdapter arrayAdapter = new SentenceAdapter(Arrays.asList(paragraphWords), getActivity());
        paragraph.setAdapter(arrayAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        paragraph.setLayoutManager(layoutManager);
    }

    @OnClick(R.id.previous)
    public void showPrevios() {
        if (index > 0) {
            View view = paragraph.getChildAt(index);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setElevation(0);
            }
            view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
            index--;
            //   paragraph.requestChildFocus(paragraph.getChildAt(index), paragraph.getChildAt(index + 1));
            highlightText();
        }
    }

    @OnClick(R.id.next)
    public void showNext() {
        if (index < (paragraph.getAdapter().getItemCount() - 1)) {
            View view = paragraph.getChildAt(index);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setElevation(0);
            }
            view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
            index++;
            highlightText();
        }
    }

    private void highlightText() {
        // paragraph.smoothScrollToPosition(index);
        View view = paragraph.getChildAt(index);
        paragraph.getLayoutManager().scrollToPosition(index + 1);
        view.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_border_yellow));
    }

    @Click(R.id.capture)
    public void captureClick() {
       /* Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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

        //  GameConstatnts.playGameNext(getActivity());
       /* Bundle bundle = GameConstatnts.findGameData("105");
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new ListeningAndWritting_(), R.id.RL_CPA,
                    bundle, ListeningAndWritting_.class.getSimpleName());
        }*/

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
        iv_dia_preview.setImageURI(capturedImageUri);
        /*try {
            Bitmap bmImg = BitmapFactory.decodeFile("" + path);
            BitmapFactory.decodeStream(new FileInputStream(path));
            iv_dia_preview.setImageBitmap(bmImg);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

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
               /* if (data.getExtras() != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                 //   presenter.createDirectoryAndSaveFile(photo, imageName);
                    capture.setVisibility(View.GONE);
                    preview.setVisibility(View.VISIBLE);
                }*/
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
        GameConstatnts.showGameInfo(getActivity(), questionModel.getInstruction());
    }
}
