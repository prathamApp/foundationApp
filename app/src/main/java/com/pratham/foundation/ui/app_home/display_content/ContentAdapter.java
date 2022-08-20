package com.pratham.foundation.ui.app_home.display_content;

import static com.pratham.foundation.utility.FC_Constants.TYPE_ASSESSMENT;
import static com.pratham.foundation.utility.FC_Constants.TYPE_ITEM;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.view_holders.ContentFileViewHolder;
import com.pratham.foundation.view_holders.ContentFolderViewHolder;
import com.pratham.foundation.view_holders.ContentTestViewHolder;

import java.util.List;


public class ContentAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private final List<ContentTable> contentViewList;
    ContentClicked contentClicked;

    public ContentAdapter(Context mContext, List<ContentTable> contentViewList, ContentClicked contentClicked) {
        this.mContext = mContext;
        this.contentViewList = contentViewList;
        this.contentClicked = contentClicked;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        // select view based on item type
        View view;
        switch (viewtype) {
            case 1:
                LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
                view = file.inflate(R.layout.content_card, viewGroup, false);
                return new ContentFileViewHolder(view, contentClicked);
            case 2:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.content_adaprer_item_folder_card, viewGroup, false);
                return new ContentFolderViewHolder(view, contentClicked);
            case 3:
                LayoutInflater assessLI = LayoutInflater.from(viewGroup.getContext());
                view = assessLI.inflate(R.layout.content_adaprer_item_folder_card, viewGroup, false);
                return new ContentTestViewHolder(view, contentClicked);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (contentViewList.get(position).getNodeType() != null) {
            switch (contentViewList.get(position).getNodeType()) {
                case TYPE_ITEM:
                    return 1;
                case TYPE_ASSESSMENT:
                    return 3;
                default:
                    return 2;
            }
        } else
            return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        final ContentTable contentList = contentViewList.get(position);
        switch (viewHolder.getItemViewType()) {
            case 1:
                //file
                ContentFileViewHolder contentFileViewHolder = (ContentFileViewHolder) viewHolder;
//                set view holder for file type item
                contentFileViewHolder.setFileItem(contentList,position);
                break;
            case 2:
                //folder
                ContentFolderViewHolder contentFolderViewHolder = (ContentFolderViewHolder) viewHolder;
//                set view holder for folder type item
                contentFolderViewHolder.setFolderItem(contentList,position);
                break;
            case 3:
                //Test
//                set view holder for folder type item
                ContentTestViewHolder TestHolder = (ContentTestViewHolder) viewHolder;
                TestHolder.setTestItem(contentList,position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contentViewList.size();
    }
}