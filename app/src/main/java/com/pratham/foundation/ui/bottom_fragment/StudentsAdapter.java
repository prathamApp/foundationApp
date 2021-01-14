package com.pratham.foundation.ui.bottom_fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.StudentAndGroup_BottomFragmentModal;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.view_holders.BottomeFragStudentViewHolder;
import com.pratham.foundation.view_holders.EmptyHolder;

import java.util.List;

/**
 * Created by Anki on 10/30/2018.
 */

public class StudentsAdapter extends RecyclerView.Adapter {
    List<StudentAndGroup_BottomFragmentModal> fragmentModalsList;
    BottomStudentsContract.StudentClickListener studentClickListener;
    Context mContext;

    public StudentsAdapter(Context c, Fragment context, List<StudentAndGroup_BottomFragmentModal> fragmentModalsList) {
        this.fragmentModalsList = fragmentModalsList;
        this.studentClickListener = (BottomStudentsContract.StudentClickListener) context;
        this.mContext = c;
    }

    @Override
    public int getItemViewType(int position) {
        if (fragmentModalsList.get(position).getType().equalsIgnoreCase(FC_Constants.STUDENTS))
            return 1;
        else if (fragmentModalsList.get(position).getType().equalsIgnoreCase(FC_Constants.GROUP_MODE))
            return 2;
        else if (fragmentModalsList.get(position).getType().equalsIgnoreCase(FC_Constants.TYPE_FOOTER))
            return 3;
        else
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
            case 3:
                LayoutInflater footer = LayoutInflater.from(viewGroup.getContext());
                view = footer.inflate(R.layout.student_item_file_footer, viewGroup, false);
                return new EmptyHolder(view);
            case 1:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.student_card, viewGroup, false);
                return new BottomeFragStudentViewHolder(view, studentClickListener);
            case 2:
                LayoutInflater grpFolder = LayoutInflater.from(viewGroup.getContext());
                view = grpFolder.inflate(R.layout.item_group, viewGroup, false);
                return new BottomeFragStudentViewHolder(view, studentClickListener,true);
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
                fragStudentViewHolder.setFragmentStudentItem(fragmentModalsList.get(position), studentClickListener,position);
                break;
            case 2:
                //Group
                BottomeFragStudentViewHolder fragGroupViewHolder = (BottomeFragStudentViewHolder) viewHolder;
                fragGroupViewHolder.setFragmentGroupItem(fragmentModalsList.get(position), studentClickListener);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return fragmentModalsList.size();
    }
}