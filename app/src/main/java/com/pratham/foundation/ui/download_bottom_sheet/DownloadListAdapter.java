package com.pratham.foundation.ui.download_bottom_sheet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.Modal_FileDownloading;
import com.pratham.foundation.view_holders.DownloadItemListHolder;

import java.util.List;

/**
 * Created by Anki on 10/30/2018.
 */

public class DownloadListAdapter extends RecyclerView.Adapter {
    Context mContext;
    List<Modal_FileDownloading> downloadingList;

    public DownloadListAdapter(Context c, Fragment context, List<Modal_FileDownloading> fileDownloadingList) {
        downloadingList = fileDownloadingList;
        this.mContext = c;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        LayoutInflater item = LayoutInflater.from(viewGroup.getContext());
        view = item.inflate(R.layout.downoad_list_item, viewGroup, false);
        return new DownloadItemListHolder(view);
//        switch (viewType) {
//            case 0:
//                LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
//                view = header.inflate(R.layout.student_item_file_header, viewGroup, false);
//                return new EmptyHolder(view);
//            default:
//                return null;
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        DownloadItemListHolder itemListHolder = (DownloadItemListHolder) viewHolder;
        itemListHolder.setDownloadItem(position, downloadingList.get(position));

    }

    @Override
    public int getItemCount() {
        return downloadingList.size();
    }
}