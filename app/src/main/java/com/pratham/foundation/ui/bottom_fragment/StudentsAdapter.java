package com.pratham.foundation.ui.bottom_fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.view_holders.EmptyHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anki on 10/30/2018.
 */

public class StudentsAdapter extends RecyclerView.Adapter {
    List<Student> studentAvatarList;
    ArrayList avatarList;
    BottomStudentsContract.StudentClickListener studentClickListener;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView studentName;
        ImageView avatar;
        RelativeLayout rl_card;

        public MyViewHolder(View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.child_name);
            avatar = itemView.findViewById(R.id.iv_child);
            rl_card = itemView.findViewById(R.id.rl_card);
        }
    }

    public StudentsAdapter(Context c, Fragment context, List<Student> studentAvatars, ArrayList avatarList) {
        this.studentAvatarList = studentAvatars;
        this.avatarList = avatarList;
        this.studentClickListener = (BottomStudentsContract.StudentClickListener) context;
        this.mContext = c;
    }

    @Override
    public int getItemViewType(int position) {
        if (studentAvatarList.get(position).getStudentID() != null) {
            if (studentAvatarList.get(position).getStudentID().equalsIgnoreCase("#####"))
                return 0;
            else
                return 1;
        } else
            return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
                view = header.inflate(R.layout.student_item_file_header, viewGroup, false);
                return new EmptyHolder(view);
            case 1:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.student_card, viewGroup, false);
                return new MyViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case 1:
                //folder
                MyViewHolder holder = (MyViewHolder) viewHolder;
                Student studentAvatar = studentAvatarList.get(position);
                holder.studentName.setSelected(true);
                holder.studentName.setText(studentAvatar.getFullName());

                if (studentAvatar.getAvatarName() != null)
                    switch (studentAvatar.getAvatarName()) {
                        case "b1.png":
                            holder.avatar.setImageResource(R.drawable.b1);
                            break;
                        case "b2.png":
                            holder.avatar.setImageResource(R.drawable.b2);
                            break;
                        case "b3.png":
                            holder.avatar.setImageResource(R.drawable.b3);
                            break;
                        case "g1.png":
                            holder.avatar.setImageResource(R.drawable.g1);
                            break;
                        case "g2.png":
                            holder.avatar.setImageResource(R.drawable.g2);
                            break;
                        case "g3.png":
                            holder.avatar.setImageResource(R.drawable.g3);
                            break;
                    }
                else
                    holder.avatar.setImageResource(R.drawable.b2);

                holder.rl_card.setOnClickListener(view -> {
                    studentClickListener.onStudentClick(studentAvatarList.get(position).getFullName(), studentAvatarList.get(position).getStudentID());
                });
        }
    }

    @Override
    public int getItemCount() {
        return studentAvatarList.size();
    }
}