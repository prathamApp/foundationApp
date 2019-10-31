package com.pratham.foundation.ui.contentPlayer.word_writting;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;
import com.pratham.foundation.ui.contentPlayer.paragraph_writing.SentenceAdapter;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnClick;

@EFragment(R.layout.fragment_paragraph_writing)
public class WordWritingFragment extends Fragment
        implements WordWritingContract.WordWritingView, OnGameClose {

    @Bean(WordWritingPresenter.class)
    WordWritingContract.WordWritingPresenter presenter;

    private int index = 0;
    @ViewById(R.id.paragraph)
    RecyclerView paragraph;
    @ViewById(R.id.scrollView)
    ScrollView scrollView;
    @ViewById(R.id.previous)
    Button previous;
    @ViewById(R.id.capture)
    Button capture;
    @ViewById(R.id.next)
    Button next;
    @ViewById(R.id.title)
    SansTextView title;

    private List<String> paragraphWords;
    //    private RelativeLayout.LayoutParams viewParam;
    private LinearLayoutManager layoutManager;
    private RecyclerView.SmoothScroller smoothScroller;
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
                readingContentPath = ApplicationClass.contentSDPath + "/.FCA/English/Game/" + contentPath + "/";
            else
                readingContentPath = ApplicationClass.foundationPath + "/.FCA/English/Game/" + contentPath + "/";
        }
        presenter.setView(WordWritingFragment.this, resId, readingContentPath);
        presenter.getData();
        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime, GameConstatnts.PARAGRAPH_WRITING + " " + GameConstatnts.START);
    }

    @Override
    public void showParagraph(List<ScienceQuestion> questionModel) {
        this.questionModel = questionModel;
        title.setText(questionModel.get(0).getInstruction());
        paragraphWords=new ArrayList<>();
        for (int i=0;i<questionModel.size();i++){
            paragraphWords.add(questionModel.get(i).getQuestion());
        }
       // paragraphWords = questionModel.getQuestion().trim().split("(?<=\\.\\s)|(?<=[?!]\\s)");
        SentenceAdapter arrayAdapter = new SentenceAdapter(paragraphWords, getActivity());
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
        imageName = "" + ApplicationClass.getUniqueID();
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, CAMERA_REQUEST);
    }

    @Click(R.id.preview)
    public void previewClick() {
        File filePath = new File(Environment.getExternalStorageDirectory().toString() + "/.FC/Internal/photos/" + imageName);
        if (filePath.exists())
            ShowPreviewDialog(filePath);
    }

    @Click(R.id.submit)
    public void submitClick() {
        File filePath = new File(Environment.getExternalStorageDirectory().toString() + "/.FC/Internal/photos/" + imageName);
        if (filePath.exists()) {
            presenter.addLearntWords(questionModel, imageName);
            imageName = null;
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
                presenter.createDirectoryAndSaveFile(photo, imageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime, GameConstatnts.PARAGRAPH_WRITING + " " + GameConstatnts.END);
    }
}
