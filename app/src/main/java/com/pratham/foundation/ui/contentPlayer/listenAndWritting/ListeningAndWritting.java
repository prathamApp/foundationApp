package com.pratham.foundation.ui.contentPlayer.listenAndWritting;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;
import com.pratham.foundation.utility.FC_Utility;
import com.pratham.foundation.utility.MediaPlayerUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.activityPhotoPath;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EFragment(R.layout.fragment_list_and_writting)
public class ListeningAndWritting extends Fragment implements ListeningAndWrittingContract.ListeningAndWrittingView, OnGameClose {
    @Bean(ListeningAndWrittingPresenterImp.class)
    ListeningAndWrittingContract.ListeningAndWrittingPresenter presenter;

    @ViewById(R.id.play_button)
    ImageView play;
    @ViewById(R.id.radiogroup)
    RadioGroup radiogroup;

 /*   @ViewById(R.id.title)
    com.pratham.foundation.customView.SansTextView title;*/

    @ViewById(R.id.previous)
    ImageButton previous;
   /* @ViewById(R.id.submitcontainer)
    LinearLayout submitBtn;*/

    @ViewById(R.id.camera_controll)
    LinearLayout camera_controll;
    @ViewById(R.id.next)
    ImageButton next;
    @ViewById(R.id.preview)
    SansButton preview;
    @ViewById(R.id.count)
    SansTextView count;
    @ViewById(R.id.submit)
    SansButton submitBtn;
    private int index = 0;
    private String readingContentPath, contentPath, contentTitle, StudentID, resId, resStartTime;
    private boolean onSdCard;
    private int isPlaying = -1;
    private List<ScienceQuestion> listenAndWrittingModal;
    private MediaPlayerUtil mediaPlayerUtil;
    private String imageName = null;
    private static final int CAMERA_REQUEST = 1;

    float rate = 1.0f;
    SoundPool sp = null;
    int sID = 0;
    int id;

    public ListeningAndWritting() {
        // Required empty public constructor
    }

    @AfterViews
    protected void initiate() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            contentPath = bundle.getString("contentPath");
            StudentID = bundle.getString("StudentID");
            resId = bundle.getString("resId");
            contentTitle = bundle.getString("contentName");
            onSdCard = bundle.getBoolean("onSdCard", false);
        }
        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";

        EventBus.getDefault().register(this);
        preview.setVisibility(View.INVISIBLE);
        presenter.setView(ListeningAndWritting.this, contentTitle, resId);
        mediaPlayerUtil = new MediaPlayerUtil(getActivity());
        presenter.fetchJsonData(readingContentPath);
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.slow:
                        rate = 0.8f;
                        // do operations specific to this selection
                        break;
                    case R.id.normal:
                        rate = 1f;
                        // do operations specific to this selection
                        break;
                    case R.id.fast:
                        rate = 1.2f;
                        // do operations specific to this selection
                        break;
                }
            }
        });
        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime, GameConstatnts.LISTNING_AND_WRITTING + " " + GameConstatnts.START);


    }

    @Override
    @UiThread
    public void loadUI(List<ScienceQuestion> listenAndWrittingModal) {
        this.listenAndWrittingModal = listenAndWrittingModal;
       /* if (listenAndWrittingModal.get(index).getInstruction() != null && !listenAndWrittingModal.get(index).getInstruction().isEmpty())
            title.setText(listenAndWrittingModal.get(index).getInstruction());*/
        setAudioResource();
    }

    private void setAudioResource() {
        try {
            if (sp != null)
                sp.stop(sID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Glide.with(getActivity()).load(R.drawable.play_button)
                .into(play);
        isPlaying=-1;
        count.setText("" + (index + 1));
        submitBtn.setVisibility(View.INVISIBLE);
        camera_controll.setVisibility(View.INVISIBLE);
        if (index == 0) {
            previous.setVisibility(View.INVISIBLE);
        } else {
            previous.setVisibility(View.VISIBLE);
        }
        if (index == (listenAndWrittingModal.size() - 1)) {
            submitBtn.setVisibility(View.VISIBLE);
            camera_controll.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
        } else {
            submitBtn.setVisibility(View.INVISIBLE);
            camera_controll.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
        }
    }
    @Click(R.id.show_answer)
    public void showAnswer(){
        try {
            isPlaying = 0;
            sp.pause(sID);
            Glide.with(getActivity()).load(R.drawable.play_button)
                    .into(play);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_show_ans_listenandwrite);
        SansTextView infoText = dialog.findViewById(R.id.info);
        infoText.setMovementMethod(new ScrollingMovementMethod());
        if (listenAndWrittingModal.get(index).getQuestion() != null)
            infoText.setText(listenAndWrittingModal.get(index).getQuestion());
        dialog.show();
    }
    @Click(R.id.play_button)
    public void onPlayClick() {
        // mediaPlayerUtil.playMedia(readingContentPath + "/" + listenAndWrittingModal.getSound());

        try {
            id = sp.load(readingContentPath + listenAndWrittingModal.get(index).getPhotourl(), 1);
            sp.setRate(sID, rate);

            sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (isPlaying == -1) {
                        isPlaying = 1;
                        Glide.with(getActivity()).load(R.drawable.pausebars)
                                .into(play);
                        sID = sp.play(id, 1, 1, 1, 0, rate);
                    } else if (isPlaying == 1) {
                        isPlaying = 0;
                        sp.pause(sID);
                        Glide.with(getActivity()).load(R.drawable.play_button)
                                .into(play);
                    } else if (isPlaying == 0) {
                        isPlaying = 1;
                        Glide.with(getActivity()).load(R.drawable.pausebars)
                                .into(play);
                        sp.resume(sID);
                    }

                }

            });


        } catch (
                Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Click(R.id.replay)
    public void replay(){
        isPlaying = -1;
        onPlayClick();
    }

    @Click(R.id.capture)
    public void captureClick() {
        imageName = "" + ApplicationClass.getUniqueID() + ".jpg";
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, CAMERA_REQUEST);
    }

    @Click(R.id.preview)
    public void previewClick() {
        File filePath = new File(activityPhotoPath + imageName);
        if (filePath.exists())
            ShowPreviewDialog(filePath);
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
                preview.setVisibility(View.VISIBLE);
            }
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

    @Click(R.id.submit)
    public void submitClick() {
        File filePath = new File(activityPhotoPath + imageName);
        if (filePath.exists()) {
            presenter.addLearntWords(listenAndWrittingModal, imageName);
            imageName = null;
        } else {
            GameConstatnts.playGameNext(getActivity(), GameConstatnts.TRUE, this);
        }
        //GameConstatnts.playGameNext(getActivity());
    }

    @Override
    public void gameClose() {
        presenter.addScore(0, "", 0, 0, resStartTime, GameConstatnts.LISTNING_AND_WRITTING + " " + GameConstatnts.END);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        try {
            sp.stop(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.previous)
    public void onPreviousClick() {
        if (listenAndWrittingModal != null)
            if (index > 0) {
                index--;
                setAudioResource();
            }
    }

    @Click(R.id.next)
    public void onNextClick() {
        if (listenAndWrittingModal != null)
            if (index < (listenAndWrittingModal.size() - 1)) {
                index++;
                setAudioResource();
            }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        GameConstatnts.showGameInfo(getActivity(),listenAndWrittingModal.get(index).getInstruction());
    }
}
