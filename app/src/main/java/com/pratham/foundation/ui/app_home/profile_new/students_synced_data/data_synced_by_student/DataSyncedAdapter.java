package com.pratham.foundation.ui.app_home.profile_new.students_synced_data.data_synced_by_student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.StudentSyncUsageModal;
import com.pratham.foundation.view_holders.EmptyHolder;
import com.pratham.foundation.view_holders.SyncDetailsViewHolder;

import java.util.List;


public class DataSyncedAdapter extends RecyclerView.Adapter {

    private final List<StudentSyncUsageModal> syncDetailsList;
    private final Context mContext;

    public DataSyncedAdapter(Context context, List<StudentSyncUsageModal> syncDetailsList) {
        this.syncDetailsList = syncDetailsList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        Log.d("crashDetection", "onCreateViewHolder viewtype : "+viewtype);
        switch (viewtype) {
            case 1:
                LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
                view = header.inflate(R.layout.list_header, viewGroup, false);
                return new EmptyHolder(view);
            case 999:
                LayoutInflater footer = LayoutInflater.from(viewGroup.getContext());
                view = footer.inflate(R.layout.student_item_file_footer, viewGroup, false);
                return new EmptyHolder(view);
            default:
                LayoutInflater studItem = LayoutInflater.from(viewGroup.getContext());
                view = studItem.inflate(R.layout.details_sync_item_card, viewGroup, false);
                return new SyncDetailsViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        switch (viewHolder.getItemViewType()) {
            case 0:
                SyncDetailsViewHolder detailsViewHolder = (SyncDetailsViewHolder) viewHolder;
                detailsViewHolder.setSyncItem(syncDetailsList.get(i),i);
                break;
        }
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