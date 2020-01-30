package com.pratham.foundation.ui.contentPlayer.pictionary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.ParaSttQuestionListModel;

import java.util.List;

public class ParaSttResultAdapter extends RecyclerView.Adapter<ParaSttResultAdapter.MyViewHolder> {
    List<ParaSttQuestionListModel> optionList;
    Context context;
    String path;


    public ParaSttResultAdapter(List<ParaSttQuestionListModel> optionList, Context context) {
        this.optionList = optionList;
        this.context = context;
        this.path = path;
    }

    @NonNull
    @Override
    public ParaSttResultAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_result_row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final ParaSttQuestionListModel paraSttQuestionListModel = optionList.get(i);
        myViewHolder.question.setText(paraSttQuestionListModel.getQuesText());
        myViewHolder.questionImg.setVisibility(View.GONE);
        myViewHolder.iv_correct_wrong_indicator.setImageResource(R.drawable.ic_check_white);
        myViewHolder.iv_correct_wrong_indicator.setBackgroundColor(context.getResources().getColor(R.color.colorBtnGreenDark));
        myViewHolder.correctAnswer.setText(""+paraSttQuestionListModel.getAnswerText());
        myViewHolder.userAnswer.setText(""+paraSttQuestionListModel.getStudentText());
    }

    @Override
    public int getItemCount() {
        return optionList.size();
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
}
