package com.pratham.foundation.ui.contentPlayer.pictionary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.ScienceQuestion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class ResultAdapterS extends RecyclerView.Adapter<ResultAdapterS.MyViewHolder> {
    List<ScienceQuestion> scienceQuestionList;
    Context context;
    String path;


    public ResultAdapterS(List<ScienceQuestion> optionList, Context context, String path) {
        this.scienceQuestionList = optionList;
        this.context = context;
        this.path = path;
    }


    @NonNull
    @Override
    public ResultAdapterS.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_result_row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        ScienceQuestionChoice correctAns=null;
        ScienceQuestionChoice userAns=null;
        final ScienceQuestion scienceQuestion = scienceQuestionList.get(i);
        myViewHolder.question.setText(scienceQuestion.getQuestion());
        if (!scienceQuestion.getPhotourl().equalsIgnoreCase("")) {
            myViewHolder.questionImg.setVisibility(View.VISIBLE);
            setImage(myViewHolder.questionImg, scienceQuestion.getPhotourl(), path + scienceQuestion.getPhotourl());
        } else {
            myViewHolder.questionImg.setVisibility(View.GONE);
        }
        if (checkAnswer(scienceQuestion)) {
            myViewHolder.iv_correct_wrong_indicator.setImageResource(R.drawable.ic_check_white);
            myViewHolder.iv_correct_wrong_indicator.setBackgroundColor(context.getResources().getColor(R.color.level_1_color));
        } else {
            myViewHolder.iv_correct_wrong_indicator.setImageResource(R.drawable.ic_close_white_24dp);
            myViewHolder.iv_correct_wrong_indicator.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
        }
        List<ScienceQuestionChoice> optionListlist = scienceQuestion.getLstquestionchoice();
        for (int k = 0; k < optionListlist.size(); k++) {
            if (optionListlist.get(k).getCorrectAnswer().equalsIgnoreCase("true")) {
                correctAns = optionListlist.get(k);
            }
            if (optionListlist.get(k).getQid().equalsIgnoreCase(scienceQuestion.getUserAnswer())) {
                userAns = optionListlist.get(k);
            }
        }
        if(correctAns!=null){
            if (correctAns.getSubUrl().trim().equalsIgnoreCase("")) {
                //return TEXT;
                myViewHolder.btncorrectAnswer.setVisibility(View.GONE);
                myViewHolder.correctAnswer.setVisibility(View.VISIBLE);
                myViewHolder.correctAnswer.setText(correctAns.getSubQues());
            } else {
              //  return IMAGE;
                myViewHolder.btncorrectAnswer.setVisibility(View.VISIBLE);
                myViewHolder.correctAnswer.setVisibility(View.GONE);
                setImage(myViewHolder.btncorrectAnswer,correctAns.getSubUrl(), path + correctAns.getSubUrl());
               // myViewHolder.correctAnswer.setText(correctAns.getSubQues());
            }
        }
        if(userAns!=null){
            if (userAns.getSubUrl().trim().equalsIgnoreCase("")) {
                //return TEXT;
                myViewHolder.btnUserAnswer.setVisibility(View.GONE);
                myViewHolder.userAnswer.setVisibility(View.VISIBLE);
                myViewHolder.userAnswer.setText(userAns.getSubQues());
            } else {
                //  return IMAGE;
                myViewHolder.btnUserAnswer.setVisibility(View.VISIBLE);
                myViewHolder.userAnswer.setVisibility(View.GONE);
                setImage(myViewHolder.btnUserAnswer,userAns.getSubUrl(), path + userAns.getSubUrl());
                // myViewHolder.correctAnswer.setText(correctAns.getSubQues());
            }
        }

    }

    private boolean checkAnswer(ScienceQuestion scienceQuestion) {
        List<ScienceQuestionChoice> optionListlist = scienceQuestion.getLstquestionchoice();
        for (int i = 0; i < optionListlist.size(); i++) {
            if (optionListlist.get(i).getQid().equalsIgnoreCase(scienceQuestion.getUserAnswer()) && optionListlist.get(i).getCorrectAnswer().equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return scienceQuestionList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView question, userAnswer, correctAnswer, tv_you_answered_label;
        CardView cardView;
        ImageView questionImg, btncorrectAnswer, btnUserAnswer;
        ImageView iv_correct_wrong_indicator;
        LinearLayout ll_correct_ans, ll_user_ans;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.tv_result_question);
            tv_you_answered_label = itemView.findViewById(R.id.tv_you_answered_label);
            questionImg = itemView.findViewById(R.id.question_img);
            userAnswer = itemView.findViewById(R.id.tv_you_answered);
            correctAnswer = itemView.findViewById(R.id.tv_correct_answer);
            btnUserAnswer = itemView.findViewById(R.id.useranswer);
            btncorrectAnswer = itemView.findViewById(R.id.btn_correct_answer);
            cardView = itemView.findViewById(R.id.result_card_view);
            ll_correct_ans = itemView.findViewById(R.id.ll_correct_ans);
            ll_user_ans = itemView.findViewById(R.id.ll_user_ans);
            iv_correct_wrong_indicator = itemView.findViewById(R.id.iv_correct_wrong_indicator);
        }
    }

    private void setImage(View view, final String choiceurl, String placeholderTemp) {
//        if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
        String path = choiceurl.replace(" ", "");
        String placeholder = placeholderTemp.replace(" ", "");
        String[] imgPath = path.split("\\.");
        int len;
        if (imgPath.length > 0)
            len = imgPath.length - 1;
        else len = 0;


        if (imgPath[len].equalsIgnoreCase("gif")) {

            try {
                GifView gifView = (GifView) view;
                InputStream gif = new FileInputStream(placeholder);
                gifView.setGifResource(gif);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    /*        Glide.with(getActivity()).asGif()
                    .load(path)
                    .apply(new RequestOptions()
                            .placeholder(Drawable.createFromPath(placeholder)))
                    .into(imageView);*/
        } else {
            ImageView imageView = (ImageView) view;
            Glide.with(context)
                    .load(path)
                    .apply(new RequestOptions()
                            .placeholder(Drawable.createFromPath(placeholder)))
                    .into(imageView);
        }


//        }
    }
}
