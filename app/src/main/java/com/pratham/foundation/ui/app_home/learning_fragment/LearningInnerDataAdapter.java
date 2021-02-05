package com.pratham.foundation.ui.app_home.learning_fragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.ui.app_home.FragmentItemClicked;
import com.pratham.foundation.view_holders.ContentFileViewHolder;
import com.pratham.foundation.view_holders.ContentFolderViewHolder;
import com.pratham.foundation.view_holders.ContentTestViewHolder;
import com.pratham.foundation.view_holders.EmptyHolder;

import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.TYPE_ASSESSMENT;
import static com.pratham.foundation.utility.FC_Constants.TYPE_FOOTER;
import static com.pratham.foundation.utility.FC_Constants.TYPE_HEADER;
import static com.pratham.foundation.utility.FC_Constants.TYPE_ITEM;


public class LearningInnerDataAdapter extends RecyclerView.Adapter {

    private List<ContentTable> itemsList;
    private Context mContext;
    boolean dw_Ready = false;
    //    LearningContract.LearningItemClicked itemClicked;
    FragmentItemClicked itemClicked;
    int parentPos = 0;
    String parentName;

    public LearningInnerDataAdapter(Context context, List<ContentTable> itemsList,
            /*LearningContract.LearningItemClicked itemClicked*/FragmentItemClicked itemClicked, int parentPos, String parentName) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.itemClicked = itemClicked;
        this.parentPos = parentPos;
        this.parentName = parentName;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        switch (viewtype) {
            case 0:
                LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
                view = header.inflate(R.layout.content_item_file_header, viewGroup, false);
                return new EmptyHolder(view);
            case 1:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.content_item_folder_card_tab, viewGroup, false);
                return new ContentFolderViewHolder(view, itemClicked);
            case 2:
                LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
                view = file.inflate(R.layout.content_item_file_card, viewGroup, false);
                return new ContentFileViewHolder(view, itemClicked);
            case 3:
                LayoutInflater assessLI = LayoutInflater.from(viewGroup.getContext());
                view = assessLI.inflate(R.layout.content_item_folder_card_tab, viewGroup, false);
                return new ContentTestViewHolder(view, itemClicked);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("ABC_ADAPTER", "getItemViewType 1 INNER : "+itemsList.get(position).getNodeType());
        if (itemsList.get(position).getNodeType() != null) {
            String a = itemsList.get(position).getNodeType();
            Log.d("ABC_ADAPTER", "getItemViewType INNER : "+a);
            switch (a) {
                case TYPE_HEADER:
                case TYPE_FOOTER:
                    return 0;
                case TYPE_ITEM:
                    return 2;
                case TYPE_ASSESSMENT:
                    return 3;
                default:
                    return 1;
            }
        } else {
            Log.d("ABC_ADAPTER", "getItemViewType INNER ELSE : ");
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()) {
            case 1:
                //folder
                ContentFolderViewHolder folderHolder = (ContentFolderViewHolder) viewHolder;
                folderHolder.setFragmentFolderItem(itemsList.get(i), i, parentName, parentPos);
                break;
            case 2:
                //file
                ContentFileViewHolder fileHolder = (ContentFileViewHolder) viewHolder;
                fileHolder.setFragmentFileItem(itemsList.get(i), i, parentName, parentPos);
                break;
            case 3:
                ContentTestViewHolder TestHolder = (ContentTestViewHolder) viewHolder;
                TestHolder.setFragmentTestItem(itemsList.get(i), i, parentName, parentPos);
                break;
        }
    }

//    private void setAnimations(final View content_card_view) {
//        final Animation animation;
//        animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_list);
//        animation.setDuration(500);
//        content_card_view.setVisibility(View.VISIBLE);
//        content_card_view.setAnimation(animation);
//    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

//    public class FileHolder extends RecyclerView.ViewHolder {
//
//        TextView tvTitle;
//        ImageView  actionBtn,ib_update_btn;
//        SimpleDraweeView itemImage;
//        public MaterialCardView content_card_view;
//
//        FileHolder(View view) {
//            super(view);
//            tvTitle = view.findViewById(R.id.content_title);
//            itemImage = view.findViewById(R.id.content_thumbnail);
//            content_card_view = view.findViewById(R.id.content_card_view);
//            actionBtn = view.findViewById(R.id.ib_action_btn);
//            ib_update_btn = view.findViewById(R.id.ib_update_btn);
//        }
//    }

//    public class FolderHolder extends RecyclerView.ViewHolder {
//
//        TextView tvTitle;
//        protected TextView tv_progress;
//        SimpleDraweeView itemImage;
//        RelativeLayout rl_root;
//        MaterialCardView card_main;
//        ImageView iv_downld,ib_update_btn;
//
//        FolderHolder(View view) {
//            super(view);
//            this.tvTitle = view.findViewById(R.id.tvTitle);
//            this.tv_progress = view.findViewById(R.id.tv_progress);
//            this.itemImage = view.findViewById(R.id.item_Image);
//            this.rl_root = view.findViewById(R.id.rl_root);
//            this.card_main = view.findViewById(R.id.card_main);
//            this.iv_downld = view.findViewById(R.id.iv_downld);
//            this.ib_update_btn = view.findViewById(R.id.ib_update_btn);
//        }
//
//    }

//    public class EmptyHolder extends RecyclerView.ViewHolder {
//        EmptyHolder(View view) {
//            super(view);
//        }
//    }
}