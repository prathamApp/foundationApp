package com.pratham.foundation.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.modalclasses.Modal_FileDownloading;

public class DownloadItemListHolder extends RecyclerView.ViewHolder {

    /**
     * Created common holder which is used for cards.
     * Folder and file type of cards have different views
     */

    @Nullable
    TextView item_name;
    @Nullable
    ProgressLayout item_progressLayout;

    public DownloadItemListHolder(View itemView) {
        super(itemView);
        item_name = itemView.findViewById(R.id.item_name);
        item_progressLayout = itemView.findViewById(R.id.item_progressLayout);
    }

    public void setDownloadItem(int position, Modal_FileDownloading modal_fileDownloading) {
        item_name.setText(modal_fileDownloading.getFilename());
        item_progressLayout.setCurProgress(modal_fileDownloading.getProgress());
    }
}