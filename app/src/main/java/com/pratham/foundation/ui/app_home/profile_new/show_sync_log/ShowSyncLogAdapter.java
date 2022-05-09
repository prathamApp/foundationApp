package com.pratham.foundation.ui.app_home.profile_new.show_sync_log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.view_holders.SyncLogViewHolder;

import java.util.List;

public class ShowSyncLogAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final int lastPos = -1;
    private final List<Modal_Log> showSyncLogList;

    public ShowSyncLogAdapter(Context mContext, List<Modal_Log> showSyncLogList) {
        this.mContext = mContext;
        this.showSyncLogList = showSyncLogList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
/*
        View view;
        LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
        view = file.inflate(R.layout.item_sync_card, viewGroup, false);
        return new SyncLogViewHolder(view);

*/
        View view;
        switch (viewtype) {
            case 0:
                Log.d("ABC_ADAPTER", "Case : 0 ");
                LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
                view = header.inflate(R.layout.item_sync_card_db, viewGroup, false);
                return new SyncLogViewHolder(view, 1);
            case 1:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.item_sync_card, viewGroup, false);
                return new SyncLogViewHolder(view);
            case 2:
                LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
                view = file.inflate(R.layout.item_sync_card_auto, viewGroup, false);
                return new SyncLogViewHolder(view, "b");
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("ABC_ADAPTER", "1 getItemViewType SYNCLOG : " + showSyncLogList.get(position).getExceptionMessage());
        if (showSyncLogList.get(position).getExceptionMessage() != null) {
            String a = showSyncLogList.get(position).getExceptionMessage();
            Log.d("ABC_ADAPTER", "2 getItemViewType SYNCLOG : " + a);
            switch (a) {
                case FC_Constants.DB_ZIP_PUSH:
                    return 0;
                case FC_Constants.APP_MANUAL_SYNC:
                    return 1;
                case FC_Constants.APP_AUTO_SYNC:
                    return 2;
                default:
                    return 0;
            }
        } else {
            Log.d("ABC_ADAPTER", "getItemViewType SYNCLOG ELSE : ");
            return 0;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        switch (viewHolder.getItemViewType()) {
            case 0:
                SyncLogViewHolder syncDBLogViewHolder = (SyncLogViewHolder) viewHolder;
                syncDBLogViewHolder.setDBSyncItem(showSyncLogList.get(position), position);
                break;
            case 1:
                SyncLogViewHolder syncLogViewHolder = (SyncLogViewHolder) viewHolder;
                syncLogViewHolder.setSyncItem(showSyncLogList.get(position), position);
                break;
            case 2:
                SyncLogViewHolder syncAutoLogViewHolder = (SyncLogViewHolder) viewHolder;
                syncAutoLogViewHolder.setAutoSyncItem(showSyncLogList.get(position), position);
                break;
        }

/*        SyncLogDataModal logDataModal;
        holder.stat_date.setText("Date: "+contentItem.getCurrentDateTime());
        if(contentItem.getExceptionMessage().equalsIgnoreCase("App_Manual_Sync"))
            holder.tv_syncstyle.setText("Manual");
        else if(contentItem.getExceptionMessage().equalsIgnoreCase("App_Auto_Sync"))
            holder.tv_syncstyle.setText("Auto");
        else {
            holder.ll_data.setVisibility(View.GONE);
            holder.rl_media.setVisibility(View.GONE);
            holder.tv_syncstyle.setText("DB");
        }

        if(!contentItem.getErrorType().contains("successful")){
            holder.stat_date.setBackground(mContext.getResources()
                    .getDrawable(R.drawable.sync_failed_bg));
            holder.stat_date.setTextColor(mContext.getResources().getColor(R.color.colorRedSignBoard));
            holder.tv_syncstyle.setTextColor(mContext.getResources().getColor(R.color.colorRedSignBoard));
        }

        try {
            JSONObject jsonObj = new JSONObject(contentItem.getLogDetail());
            Gson gson = new Gson();
            logDataModal = gson.fromJson(jsonObj.toString(), SyncLogDataModal.class);
            if(logDataModal==null) {
                holder.tv_data.setText("Data synced : 0");
                holder.tv_media.setText("Media synced : 0" );
                holder.tv_courses.setText("Courses synced : 0");
            }else {
                holder.tv_data.setText("Data synced : " + logDataModal.getSync_data_length());
                holder.tv_media.setText("Media synced : " + logDataModal.getSync_media_length());
                holder.tv_courses.setText("Courses synced : " + logDataModal.getSync_course_enrollment_length());
                holder.tv_mediacount.setText("" + logDataModal.getMediaCount());
                holder.tv_coursecount.setText("" + logDataModal.getCoursesCount());
                holder.tv_scorecount.setText("" + logDataModal.getScoreTable());
            }
            setAnimations(holder.log_card_main, position);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        }

        @Override
        public int getItemCount () {
            return showSyncLogList.size();
        }
    }