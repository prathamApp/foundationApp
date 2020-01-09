package com.pratham.foundation.ui.contentPlayer.listenAndWritting;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etiennelawlor.discreteslider.library.ui.DiscreteSlider;
import com.etiennelawlor.discreteslider.library.utilities.DisplayUtility;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.customView.SansTextViewBold;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ScienceQuestion;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

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
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM;
import static com.pratham.foundation.utility.FC_Constants.activityPhotoPath;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;

@EFragment(R.layout.fragment_list_and_writting)
public class ListeningAndWritting extends Fragment implements ListeningAndWrittingContract.ListeningAndWrittingView, OnGameClose {
    @Bean(ListeningAndWrittingPresenterImp.class)
    ListeningAndWrittingContract.ListeningAndWrittingPresenter presenter;

    @ViewById(R.id.play_button)
    ImageButton play;

    @ViewById(R.id.capture)
    ImageButton capture;
 /*   @ViewById(R.id.radiogroup)
    RadioGroup radiogroup;*/

 /*   @ViewById(R.id.title)
    com.pratham.foundation.customView.SansTextView title;*/

    @ViewById(R.id.previous)
    ImageButton previous;

    @ViewById(R.id.show_answer)
    SansButton show_answer;
   /* @ViewById(R.id.submitcontainer)
    LinearLayout submitBtn;*/

    @ViewById(R.id.camera_controll)
    LinearLayout camera_controll;
    @ViewById(R.id.next)
    ImageButton next;
    @ViewById(R.id.preview)
    SansButton preview;
    @ViewById(R.id.count)
    SansTextViewBold  count;
    @ViewById(R.id.submit)
    SansButton submitBtn;

    @ViewById(R.id.discrete_slider)
    DiscreteSlider discreteSlider;
    @ViewById(R.id.tick_mark_labels_rl)
    RelativeLayout tickMarkLabelsRelativeLayout;

    private int index = 0;
    private String readingContentPath, contentPath, contentTitle, StudentID, resId, resStartTime;
    private boolean onSdCard;
    private int isPlaying = -1;
    private List<ScienceQuestion> listenAndWrittingModal;
    private String imageName = null;
    private static final int CAMERA_REQUEST = 1;
    private MediaPlayer mediaPlayer;
    CountDownTimer countDownTimer;
    private Uri capturedImageUri;
    int duration;
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

        imageName = "" + ApplicationClass.getUniqueID() + ".jpg";
        preview.setVisibility(View.GONE);
        EventBus.getDefault().register(this);
        presenter.setView(ListeningAndWritting.this, contentTitle, resId);
        presenter.fetchJsonData(readingContentPath);
       /* if (listenAndWrittingModal != null)
            GameConstatnts.showGameInfo(getActivity(), listenAndWrittingModal.get(index).getInstruction(), readingContentPath + listenAndWrittingModal.get(index).getInstructionUrl());
*/

        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
     /*   radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
       */
        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.addScore(0, "", 0, 0, resStartTime, GameConstatnts.LISTNING_AND_WRITTING + " " + GameConstatnts.START,resId);
        tickMarkLabelsRelativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tickMarkLabelsRelativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                tickMarkLabelsRelativeLayout.getHeight(); //height is ready
                addTickMarkTextLabels();
            }
        });
        if (FC_Constants.isTest) {
            show_answer.setVisibility(View.GONE);
        }
    }

    private void addTickMarkTextLabels() {
        discreteSlider.setOnDiscreteSliderChangeListener(new DiscreteSlider.OnDiscreteSliderChangeListener() {
            @Override
            public void onPositionChanged(int position) {
                int childCount = tickMarkLabelsRelativeLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    TextView tv = (TextView) tickMarkLabelsRelativeLayout.getChildAt(i);
                    if (i == position)
                        tv.setTextColor(getResources().getColor(R.color.colorBtnGreenDark));
                    else
                        tv.setTextColor(getResources().getColor(R.color.colorRed));

                }
                switch (position) {
                    case 0:
                        rate = 0.8f;
                        // do operations specific to this selection
                        setCountDown((duration / 0.8));
                        break;
                    case 1:
                        rate = 1f;
                        setCountDown((duration));
                        // do operations specific to this selection
                        break;
                    case 2:
                        rate = 1.2f;
                        setCountDown((duration / 1.2));
                        // do operations specific to this selection
                        break;
                }
                try {

                    sp.stop(sID);
                    setPlayImage();
                   /* Glide.with(getActivity()).load(R.drawable.ic_play_arrow_black)
                            .into(play);*/
                    isPlaying = -1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        int tickMarkCount = discreteSlider.getTickMarkCount();
        float tickMarkRadius = discreteSlider.getTickMarkRadius();
        int width = tickMarkLabelsRelativeLayout.getMeasuredWidth();

        int discreteSliderBackdropLeftMargin = DisplayUtility.dp2px(getContext(), 32);
        int discreteSliderBackdropRightMargin = DisplayUtility.dp2px(getContext(), 32);
        float firstTickMarkRadius = tickMarkRadius;
        float lastTickMarkRadius = tickMarkRadius;
        int interval = (width - (discreteSliderBackdropLeftMargin + discreteSliderBackdropRightMargin) - ((int) (firstTickMarkRadius + lastTickMarkRadius)))
                / (tickMarkCount - 1);

        String[] tickMarkLabels = {" Slow ", "Normal", " Fast "};
        int tickMarkLabelWidth = DisplayUtility.dp2px(getContext(), 40);

        for (int i = 0; i < tickMarkCount; i++) {
            SansTextView tv = new SansTextView(getContext());
            tv.setTextSize(25);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            tv.setText(tickMarkLabels[i]);
            tv.setGravity(Gravity.CENTER);
            if (i == discreteSlider.getPosition())
                tv.setTextColor(getResources().getColor(R.color.colorBtnGreenDark));
            else
                tv.setTextColor(getResources().getColor(R.color.colorRed));

//                    tv.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));

            int left = discreteSliderBackdropLeftMargin + (int) firstTickMarkRadius + (i * interval) - (tickMarkLabelWidth / 2);

            layoutParams.setMargins(left,
                    0,
                    0,
                    0);
            tv.setLayoutParams(layoutParams);

            tickMarkLabelsRelativeLayout.addView(tv);
        }
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

            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();

            mediaPlayer.reset();
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(readingContentPath + listenAndWrittingModal.get(index).getPhotourl());
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();

            if (rate == 0.8f) {
                setCountDown((duration /0.8));
            } else if (rate == 1f) {
                setCountDown((duration));
            } else if (rate == 1.2f) {
                setCountDown((duration / 1.2));
            }
            if (sp != null)
                sp.stop(sID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setPlayImage();
        /*Glide.with(getActivity()).load(R.drawable.ic_play_arrow_black)
                .into(play);*/
        isPlaying = -1;
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


    public void setCountDown(double duration) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        countDownTimer = new CountDownTimer((int) duration, 100) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try {
                    sp.stop(sID);
           /* Glide.with(getActivity()).load(R.drawable.ic_play_arrow_black)
                    .into(play);*/
                    setPlayImage();
                    isPlaying = -1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    @Click(R.id.show_answer)
    public void showAnswer() {
        try {
            isPlaying = 0;
            sp.pause(sID);
            setPlayImage();
         /*   Glide.with(getActivity()).load(R.drawable.ic_play_arrow_black)
                    .into(play);*/
        } catch (Exception e) {
            e.printStackTrace();
        }


        final CustomLodingDialog dialog = new CustomLodingDialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_show_ans_listenandwrite);
        TextView infoText = dialog.findViewById(R.id.info);
        infoText.setMovementMethod(new ScrollingMovementMethod());
        if (listenAndWrittingModal.get(index).getQuestion() != null)
            infoText.setText(listenAndWrittingModal.get(index).getQuestion());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            infoText.setAutoSizeTextTypeWithDefaults(AUTO_SIZE_TEXT_TYPE_UNIFORM);
        } else {
            TextViewCompat.setAutoSizeTextTypeWithDefaults(infoText, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }
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
                        setPauseImage();
                        /*Glide.with(getActivity()).load(R.drawable.ic_pause_black)
                                .into(play);*/
                        sID = sp.play(id, 1, 1, 1, 0, rate);
                        countDownTimer.start();
                    } else if (isPlaying == 1) {
                        isPlaying = 0;
                        sp.pause(sID);
                        countDownTimer.pause();
                        setPlayImage();
                       /* Glide.with(getActivity()).load(R.drawable.ic_play_arrow_black)
                                .into(play);*/
                    } else if (isPlaying == 0) {
                        isPlaying = 1;
                        setPauseImage();
                       /* Glide.with(getActivity()).load(R.drawable.ic_pause_black)
                                .into(play);*/
                        sp.resume(sID);
                        countDownTimer.resume();
                    }

                }

            });

        } catch (
                Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    @Click(R.id.replay)
    public void replay() {
        try {
            sp.stop(sID);
           /* Glide.with(getActivity()).load(R.drawable.ic_play_arrow_black)
                    .into(play);*/
            setPlayImage();
            isPlaying = -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // onPlayClick();
    }

    @Click(R.id.capture)
    public void captureClick() {
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
        if (imageName != null) {
            File filePath = new File(activityPhotoPath + imageName);
            if (filePath.exists())
                ShowPreviewDialog(filePath);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("codes", String.valueOf(requestCode) + resultCode);
        try {
            if (requestCode == CAMERA_REQUEST) {
                if (resultCode == RESULT_OK) {
                    capture.setVisibility(View.GONE);
                    preview.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        presenter.addScore(0, "", 0, 0, resStartTime, GameConstatnts.LISTNING_AND_WRITTING + " " + GameConstatnts.END,resId);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        try {
            countDownTimer.onFinish();
            sp.stop(sID);
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
        GameConstatnts.showGameInfo(getActivity(), listenAndWrittingModal.get(index).getInstruction(), readingContentPath + listenAndWrittingModal.get(index).getInstructionUrl());
    }
}
