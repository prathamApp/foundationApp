package com.pratham.foundation.ui.app_home.profile_new.show_image_question;

import static com.pratham.foundation.utility.FC_Constants.activityPhotoPath;
import static com.pratham.foundation.utility.FC_Utility.showZoomDialog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.customView.media_controller.PlayerControlView;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ImageJsonObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


@EActivity(R.layout.activity_show_image_question)
public class ShowImgQuestionActivity extends BaseActivity implements ShowImgQuestionContract.ShowImgQuestionView{

    @Bean(ShowImgQuestionPresenter.class)
    ShowImgQuestionContract.ShowImgQuestionPresenter presenter;

    @ViewById(R.id.main_back)
    ImageView main_back;
    @ViewById(R.id.tv_Game_Name)
    TextView tv_Game_Name;
    @ViewById(R.id.tv_Question)
    TextView tv_Question;
    @ViewById(R.id.ques_card)
    MaterialCardView ques_card;

    @ViewById(R.id.iv_ans_image)
    ImageView iv_ans_image;
    @ViewById(R.id.iv_question_image)
    ImageView question_image;
    @ViewById(R.id.iv_question_gif)
    GifView question_gif;
    @ViewById(R.id.rl_videoView)
    RelativeLayout rl_videoView;
    @ViewById(R.id.ques_videoView)
    VideoView ques_videoView;
    @ViewById(R.id.player_control_view)
    PlayerControlView player_control_view;

    @ViewById(R.id.zoom_image_ans)
    ImageView zoom_image_ans;

    @ViewById(R.id.zoom_image_que)
    ImageView zoom_image_que;

    Score scoreDisp;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        scoreDisp = (Score) getIntent().getSerializableExtra("scoreDisp");
        presenter.setView(ShowImgQuestionActivity.this);
        ques_card.setVisibility(View.GONE);
        displayContent();
    }

    @Click(R.id.main_back)
    public void pressedBack(){
        super.onBackPressed();
        finish();
    }

    @UiThread
    public void displayContent() {
        ImageJsonObject imageJsonObject;
        try {
            JSONObject jsonObj = new JSONObject(scoreDisp.getResourceID());
            Gson gson = new Gson();
            imageJsonObject = gson.fromJson(jsonObj.toString(), ImageJsonObject.class);

            tv_Game_Name.setText(imageJsonObject.getGameName());
            String question = imageJsonObject.getQuestion();
            tv_Question.setText(question);
            String questionMedia = imageJsonObject.getQueImageName();
            if(!questionMedia.equalsIgnoreCase("na") &&
                    !questionMedia.equalsIgnoreCase("") &&
                    questionMedia != null){
                setQuestionMedia(questionMedia);
            }
            setStudentAnsImage(imageJsonObject.getAnsImageName());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @UiThread
    public void setStudentAnsImage(String ansImageName) {
        try {
            File img = new File(activityPhotoPath+""+ansImageName);
            if(img.exists()) {
//                Bitmap bmImg;/* = BitmapFactory.decodeFile(activityPhotoPath + "" + ansImageName);*/
//                Bitmap bmImg = BitmapFactory.decodeStream(new FileInputStream(activityPhotoPath + "" + ansImageName));
                Bitmap bmImg = BitmapFactory.decodeFile(img.toString());
//                BitmapFactory.decodeStream(new FileInputStream(img.toString()));
                iv_ans_image.setImageBitmap(bmImg);
//                Uri capturedImageUri = Uri.fromFile(img);
//                iv_ans_image.setImageURI(capturedImageUri);
                zoom_image_ans.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String localPath = activityPhotoPath + ansImageName;
                        showZoomDialog(ShowImgQuestionActivity.this, localPath, localPath);
                    }
                });
            }else {
                Log.d("TAG", "setStudentAnsImage: NOT FOUND");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @UiThread
    public void setQuestionMedia(String questionMedia) {
        try {
            if(questionMedia.contains(".jpg") || questionMedia.contains(".jpeg") || questionMedia.contains(".png")){
                ques_card.setVisibility(View.VISIBLE);
                rl_videoView.setVisibility(View.GONE);
                question_gif.setVisibility(View.GONE);
                question_image.setVisibility(View.VISIBLE);
                Bitmap bmImg = BitmapFactory.decodeFile(questionMedia);
                BitmapFactory.decodeStream(new FileInputStream(questionMedia));
                question_image.setImageBitmap(bmImg);
                zoom_image_que.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String localPath = questionMedia;
                        showZoomDialog(ShowImgQuestionActivity.this,localPath, localPath);
                    }
                });
            }
            if(questionMedia.contains(".mp4") || questionMedia.contains(".3gp")){
                ques_card.setVisibility(View.VISIBLE);
                rl_videoView.setVisibility(View.VISIBLE);
                question_gif.setVisibility(View.GONE);
                question_image.setVisibility(View.GONE);
                ques_videoView.setMediaController(player_control_view.getMediaControllerWrapper());
                ques_videoView.setVideoPath(questionMedia);
                ques_videoView.start();
                ques_videoView.setOnPreparedListener(mp -> {
                    player_control_view.show();
                });
            }
            else if(questionMedia.contains(".gif")){
                ques_card.setVisibility(View.VISIBLE);
                rl_videoView.setVisibility(View.GONE);
                question_image.setVisibility(View.GONE);

                question_gif.setVisibility(View.VISIBLE);
                question_gif.setGifResource(new FileInputStream(questionMedia));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}