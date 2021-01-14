package com.pratham.foundation.ui.app_home.profile_new;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;


public class ProfileInnerDataAdapter extends RecyclerView.Adapter {

    private String[] itemsList;
    private String[] itemsImgList;
    private Context mContext;
    boolean dw_Ready = false;
    ProfileContract.ProfileItemClicked itemClicked;
    int parentPos = 0;
    //    List maxScore;
    String parentName;
    private static final String TYPE_HEADER = "Header";
    private static final String TYPE_ITEM = "Resource";

    public ProfileInnerDataAdapter(Context context, String[] itemsList, String[] itemsImgList, ProfileContract.ProfileItemClicked profileItemClicked, int parentPos) {
        this.itemsList = itemsList;
        this.itemsImgList = itemsImgList;
        this.mContext = context;
        this.itemClicked = profileItemClicked;
        this.parentPos = parentPos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
        view = header.inflate(R.layout.profile_item_card, viewGroup, false);
        return new ItemHolder(view);
/*
        switch (viewtype) {
            case 0:
                LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
                view = header.inflate(R.layout.content_item_file_header, viewGroup, false);
                return new EmptyHolder(view);
            case 1:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.content_item_folder_card_tab, viewGroup, false);
                return new FolderHolder(view);
            case 2:
                LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
                view = file.inflate(R.layout.content_item_file_card, viewGroup, false);
                return new FileHolder(view);
            default:
                return null;
        }
*/
    }

    @Override
    public int getItemViewType(int position) {
/*
        if (itemsList.get(position).getNodeType() != null) {
            switch (itemsList.get(position).getNodeType()) {
                case TYPE_HEADER:
                    return 0;
                case TYPE_ITEM:
                    return 2;
                default:
                    return 1;
            }
        } else
*/
        return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ItemHolder itemHolder = (ItemHolder) viewHolder;
        switch (itemsImgList[i]) {
            case "Certificate":
                itemHolder.tvTitle.setText(mContext.getResources().getString(R.string.Certificates));
                itemHolder.item_Image.setImageResource(R.drawable.certificates);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("Certificate"));
                break;
            case "Projects":
                itemHolder.tvTitle.setText("Projects");
                itemHolder.item_Image.setImageResource(R.drawable.lang_01);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("Projects"));
                break;
            case "Usage":
                itemHolder.tvTitle.setText(mContext.getResources().getString(R.string.usage));
                itemHolder.item_Image.setImageResource(R.drawable.progress_report);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("Usage"));
                break;
            case "ImageQues":
                itemHolder.tvTitle.setText(mContext.getResources().getString(R.string.img_ques));
                itemHolder.item_Image.setImageResource(R.drawable.camera);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("ImageQues"));
                break;
            case "ChitChat":
                itemHolder.tvTitle.setText(mContext.getResources().getString(R.string.chat_l5));
                itemHolder.item_Image.setImageResource(R.drawable.chat);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("ChitChat"));
                break;
            case "Share Content":
                itemHolder.tvTitle.setText(mContext.getResources().getString(R.string.share_share));
                itemHolder.item_Image.setImageResource(R.drawable.ic_share_receive);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("Share Content"));
                break;
            case "Share App":
                itemHolder.tvTitle.setText(mContext.getResources().getString(R.string.share_share));
                itemHolder.item_Image.setImageResource(R.drawable.app_share);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("Share App"));
                break;
            case "syncImg":
                itemHolder.tvTitle.setText(mContext.getResources().getString(R.string.sync_status));
                itemHolder.item_Image.setImageResource(R.drawable.refresh);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("sync status"));
                break;
            case "syncDimg":
                itemHolder.tvTitle.setText(mContext.getResources().getString(R.string.sync));
                itemHolder.item_Image.setImageResource(R.drawable.ic_sync);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("Sync Data"));
                break;
            case "Lang_ic":
                itemHolder.tvTitle.setText(mContext.getResources().getString(R.string.lang_pack));
                itemHolder.item_Image.setImageResource(R.drawable.translator);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("Lang_ic"));
                break;
            case "cleanImg":
                itemHolder.tvTitle.setText(mContext.getResources().getString(R.string.free_space));
                itemHolder.item_Image.setImageResource(R.drawable.broom_clean);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("free space"));
                break;
            case "enroll_course":
                itemHolder.tvTitle.setText(mContext.getResources().getString(R.string.enroll_course));
                itemHolder.item_Image.setImageResource(R.drawable.enroll_course);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("enroll_course"));
                break;
            case "change_time":
                itemHolder.tvTitle.setText("Change Time");
                itemHolder.item_Image.setImageResource(R.drawable.clock);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("change_time"));
                break;
            case "tab_usage":
                itemHolder.tvTitle.setText(mContext.getResources().getString(R.string.usage));
                itemHolder.item_Image.setImageResource(R.drawable.progress_report);
                itemHolder.rl_root.setOnClickListener(v -> itemClicked.itemClicked("tab_usage"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.length;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected ImageView item_Image;
        protected RelativeLayout rl_root;

        public ItemHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tv_title);
            this.item_Image = view.findViewById(R.id.item_Image);
            this.rl_root = view.findViewById(R.id.rl_root);
        }

    }

}