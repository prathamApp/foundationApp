package com.pratham.foundation.ui.app_home.profile_new.students_synced_data.students_synced_fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.StudentSyncDetails;
import com.pratham.foundation.view_holders.SyncStudentsViewHolder;

import java.util.List;


public class SyncedStudentAdapter extends RecyclerView.Adapter {

    private final List<StudentSyncDetails> syncDetailsList;
    private final Context mContext;
    SyncedStudentContract.StudentItemClicked itemClicked;

    public SyncedStudentAdapter(Context context, List<StudentSyncDetails> syncDetailsList,
                                SyncedStudentContract.StudentItemClicked itemClicked) {
        this.syncDetailsList = syncDetailsList;
        this.mContext = context;
        this.itemClicked = itemClicked;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        Log.d("crashDetection", "onCreateViewHolder viewtype : " + viewtype);
        LayoutInflater studItem = LayoutInflater.from(viewGroup.getContext());
        view = studItem.inflate(R.layout.student_sync_item_card, viewGroup, false);
        return new SyncStudentsViewHolder(view, itemClicked);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//        switch (viewHolder.getItemViewType()) {
//            case 0:
                SyncStudentsViewHolder studentsViewHolder = (SyncStudentsViewHolder) viewHolder;
                studentsViewHolder.setSyncItem(syncDetailsList.get(i), i);
//                break;
//        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
/*        if (dataList.get(position).getNodeType() != null) {
            String a =dataList.get(position).getNodeType();
            switch (a) {
                case TYPE_HEADER:
                    return 0;
                case TYPE_FOOTER:
                    return 999;
                case TYPE_ITEM:
                    return 2;
                default:
                    return 1;
            }
        } else {
            return 1;
        }*/
    }

    @Override
    public int getItemCount() {
        return (null != syncDetailsList ? syncDetailsList.size() : 0);
    }
}