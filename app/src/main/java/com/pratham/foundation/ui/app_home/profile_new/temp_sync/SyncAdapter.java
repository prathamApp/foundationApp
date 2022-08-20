package com.pratham.foundation.ui.app_home.profile_new.temp_sync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.SyncLog;
import com.pratham.foundation.view_holders.NewSyncLogViewHolder;

import java.util.List;


public class SyncAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<SyncLog> syncLogList;
    SyncResultContract.SyncResultItemClick itemClick;

    public SyncAdapter(Context mContext, List<SyncLog> syncLogList, SyncResultContract.SyncResultItemClick itemClick) {
        this.mContext = mContext;
        this.syncLogList = syncLogList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        // select view based on item type
        View view;
        switch (viewtype) {
            case 1:
                LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
                view = file.inflate(R.layout.new_item_sync_card, viewGroup, false);
                return new NewSyncLogViewHolder(view, itemClick);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
/*        if (syncLogList.get(position).getNodeType() != null) {
            switch (syncLogList.get(position).getNodeType()) {
                case TYPE_ITEM:
                    return 1;
                case TYPE_ASSESSMENT:
                    return 3;
                default:
                    return 2;
            }
        } else
            return 0;*/
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        final SyncLog syncLog = syncLogList.get(position);
        switch (viewHolder.getItemViewType()) {
            case 1:
                //file
                NewSyncLogViewHolder logViewHolder = (NewSyncLogViewHolder) viewHolder;
//                set view holder for file type item
                logViewHolder.setSyncItem(syncLog, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return syncLogList.size();
    }
}