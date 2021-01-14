package com.pratham.foundation.ui.app_home.learning_fragment.attendance_bottom_fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.view_holders.BottomeFragStudentViewHolder;
import com.pratham.foundation.view_holders.EmptyHolder;

import java.util.List;

/**
 * Created by Anki on 10/30/2018.
 */

public class AttendanceStudentsAdapter extends RecyclerView.Adapter {
    List<Student> fragmentModalsList;
    AttendanceStudentsContract.AttendanceStudentClickListener studentClickListener;
    Context mContext;

    public AttendanceStudentsAdapter(Context c, Fragment context, List<Student> fragmentModalsList) {
        this.fragmentModalsList = fragmentModalsList;
        this.studentClickListener = (AttendanceStudentsContract.AttendanceStudentClickListener) context;
        this.mContext = c;
    }

    @Override
    public int getItemViewType(int position) {
        if (fragmentModalsList.get(position).getStudentID().equalsIgnoreCase("#####"))
            return 0;
        else
            return 1;
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
                return new BottomeFragStudentViewHolder(view, studentClickListener);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case 1:
                //Student
                BottomeFragStudentViewHolder fragStudentViewHolder = (BottomeFragStudentViewHolder) viewHolder;
                fragStudentViewHolder.setAttendenceFragmentStudentItem(fragmentModalsList.get(position), studentClickListener, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return fragmentModalsList.size();
    }
}