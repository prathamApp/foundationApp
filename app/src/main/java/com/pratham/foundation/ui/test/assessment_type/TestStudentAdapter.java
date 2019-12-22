package com.pratham.foundation.ui.test.assessment_type;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.Student;

import java.util.List;


public class TestStudentAdapter extends RecyclerView.Adapter<TestStudentAdapter.MyViewHolder> {

    private Context mContext;
    private int lastPos = -1;
    private List<Student> studentViewList;
    TestStudentClicked studentClicked;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;
        public RelativeLayout content_card_view;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tv_studentName);
            thumbnail = view.findViewById(R.id.test_student_img);
            content_card_view = view.findViewById(R.id.test_student_card);
        }
    }

    public TestStudentAdapter(Context mContext, List<Student> studentViewList, TestStudentClicked studentClicked) {
        this.mContext = mContext;
        this.studentViewList = studentViewList;
        this.studentClicked = studentClicked;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_student_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Student studentList = studentViewList.get(position);
        holder.title.setText(studentList.getFullName());
        if (studentList.getGender() != null) {
            if (studentList.getGender().equalsIgnoreCase("male"))
                Glide.with(mContext).load(R.drawable.b2/* ApplicationClass.contentSDPath + "/.FCA/"+
                        FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI)+"/App_Thumbs/b2.png"*/)
                        .into(holder.thumbnail);
            else
                Glide.with(mContext).load(R.drawable.g1/*2ApplicationClass.contentSDPath + "/.FCA/"+
                        FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI)+"/App_Thumbs/g1.png"*/)
                        .into(holder.thumbnail);
        } else
            Glide.with(mContext).load(R.drawable.g1/*ApplicationClass.contentSDPath + "/.FCA/"+
                    FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI)+"/App_Thumbs/g1.png"*/)
                    .into(holder.thumbnail);
        holder.content_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentClicked.onStudentClicked(position, "" + studentList.getStudentID());
            }
        });
        setAnimations(holder.content_card_view, position);
    }

    private void setAnimations(final View content_card_view, final int position) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.item_fall_down);
        animation.setDuration(500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*                if (position > lastPos) {*/
                content_card_view.setVisibility(View.VISIBLE);
                content_card_view.setAnimation(animation);
                lastPos = position;
//                }
            }
        }, (long) (20));

    }

    @Override
    public int getItemCount() {
        return studentViewList.size();
    }
}