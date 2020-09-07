package com.pratham.foundation.ui.test.assessment_type;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.utility.FC_Utility;

import java.io.File;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.StudentPhotoPath;


public class TestStudentAdapter extends RecyclerView.Adapter<TestStudentAdapter.MyViewHolder> {

    private Context mContext;
    private int lastPos = -1;
    private List<Student> studentViewList;
    TestStudentClicked studentClicked;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public SimpleDraweeView thumbnail;
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
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Student studentList = studentViewList.get(position);
        holder.title.setText(studentList.getFullName());
        File file = new File(StudentPhotoPath + "" + studentList.getStudentID() + ".jpg");
        if (file.exists()) {
            holder.thumbnail.setImageURI(Uri.fromFile(file));
        } else{
            if(studentList.getGender() != null && studentList.getGender().equalsIgnoreCase("male"))
                holder.thumbnail.setImageResource(FC_Utility.getRandomMaleAvatar(mContext));
            else
                holder.thumbnail.setImageResource(FC_Utility.getRandomFemaleAvatar(mContext));
        }
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

        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
        lastPos = position;
    }

    @Override
    public int getItemCount() {
        return studentViewList.size();
    }
}