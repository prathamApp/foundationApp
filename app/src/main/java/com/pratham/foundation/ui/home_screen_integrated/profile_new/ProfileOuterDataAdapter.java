package com.pratham.foundation.ui.home_screen_integrated.profile_new;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;

import java.util.ArrayList;
import java.util.List;


public class ProfileOuterDataAdapter extends RecyclerView.Adapter {

    private static final String TYPE_HEADER = "Header";
    private static final String TYPE_ITEM = "Resource";
    private static final String TYPE_FOOTER = "Footer";

    private Context mContext;
    String[] progressArray;
//    String[] progressSubArray = {"Certificate", "Projects", "Usage", "ImageQues"};
    String[] progressSubArray = {"Certificate"};
//    String[] progressImgSubArray = {"Certificate", "Projects", "Usage", "ImageQues"};
    String[] progressImgSubArray = {"Certificate"};
    String[] shareSubArray = {"Share App", "Share Content"};
    String[] shareImgSubArray = {"Share App", "Share Content"};

    public int childCounter = 0;
    ProfileContract.ProfileItemClicked profileItemClicked;

    public ProfileOuterDataAdapter(Context context, String[] progressArray, ProfileContract.ProfileItemClicked profileItemClicked) {
        this.progressArray = progressArray;
        this.mContext = context;
        this.profileItemClicked = profileItemClicked;
    }

    public class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View view) {
            super(view);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
        view = folder.inflate(R.layout.profile_menu_card, viewGroup, false);
        return new FolderHolder(view);
/*        switch (viewtype) {
            case 0:
            case 3:
                LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
                view = header.inflate(R.layout.list_header, viewGroup, false);
                return new EmptyHolder(view);
            case 1:
            case 2:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.content_folder_card_tab, viewGroup, false);
                return new FolderHolder(view);
            default:
                return null;

        }*/
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        FolderHolder itemRowHolder = (FolderHolder) viewHolder;
        String[] sublistList = new String[0];
        String[] subImgList = new String[0];
/*            if (sublistList.size()>4)
                itemRowHolder.next_nav_btn.setVisibility(View.VISIBLE);
            else
                itemRowHolder.next_nav_btn.setVisibility(View.GONE);*/
        try {
//            itemRowHolder.card_layout.setBackground(mContext.getResources().getDrawable(getRandomCardColor()));
            itemRowHolder.itemTitle.setText(progressArray[i]);
            if(progressArray[i].equalsIgnoreCase("Progress")){
                sublistList = progressSubArray;
                subImgList = progressImgSubArray;
            } else if(progressArray[i].equalsIgnoreCase("Share")){
                sublistList = shareSubArray;
                subImgList = shareImgSubArray;
            }

            ProfileInnerDataAdapter ProfileInnerDataAdapter = new ProfileInnerDataAdapter(mContext,
                    sublistList, subImgList, profileItemClicked, i);
            itemRowHolder.recycler_view_grid_list.setLayoutManager(new GridLayoutManager(mContext, 2));
            itemRowHolder.recycler_view_grid_list.setAdapter(ProfileInnerDataAdapter);
            childCounter += 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

/*
        switch (viewHolder.getItemViewType()) {
            case 1:
            case 2:
                FolderHolder itemRowHolder = (FolderHolder) viewHolder;
                contentTable = dataList.get(i);
                sublistList = new ArrayList<>();
                final String sectionName = contentTable.getNodeTitle();
                if (contentTable.getContentType() != null && contentTable.getResourceType() != null) {
                    if ((contentTable.getContentType().equalsIgnoreCase("FullDownload") ||
                            contentTable.getResourceType().equalsIgnoreCase("FullDownload"))
                            && (contentTable.isDownloaded.equalsIgnoreCase("false"))) {
                        itemRowHolder.btnMore.setVisibility(View.GONE);
                        itemRowHolder.actionBtn.setVisibility(View.VISIBLE);
                        itemRowHolder.actionBtn.setOnClickListener(v -> tempItemClicked.onContentDownloadClicked(dataList.get(i),
                                i, 0, "" + FC_Constants.FULL_DOWNLOAD));
                    } else {
                        int size = dataList.get(i).getNodelist().size() - 2;
                        itemRowHolder.btnMore.setText("SEE ALL " + size);
                        itemRowHolder.btnMore.setVisibility(View.GONE);
                        if (size > 6) {
                            itemRowHolder.btnMore.setVisibility(View.VISIBLE);
                            itemRowHolder.btnMore.setOnClickListener(v -> tempItemClicked.seeMore(dataList.get(i).getNodeId(),
                                    dataList.get(i).getNodeTitle()));
                        }
                        itemRowHolder.actionBtn.setVisibility(View.GONE);
                    }
                } else {
                    int size = dataList.get(i).getNodelist().size() - 2;
                    itemRowHolder.btnMore.setText("SEE ALL " + size);
                    itemRowHolder.btnMore.setVisibility(View.GONE);
                    if (size > 6) {
                        itemRowHolder.btnMore.setVisibility(View.VISIBLE);
                        itemRowHolder.btnMore.setOnClickListener(v -> tempItemClicked.seeMore(dataList.get(i).getNodeId(),
                                dataList.get(i).getNodeTitle()));
                    }
                    itemRowHolder.actionBtn.setVisibility(View.GONE);
                }
                sublistList = getList(dataList.get(i).getNodelist());
*/
/*            if (sublistList.size()>4)
                itemRowHolder.next_nav_btn.setVisibility(View.VISIBLE);
            else
                itemRowHolder.next_nav_btn.setVisibility(View.GONE);*//*


                try {
                    itemRowHolder.itemTitle.setText(sectionName);
                    LearningInnerDataAdapter learningInnerDataAdapter = new LearningInnerDataAdapter(mContext, sublistList, tempItemClicked, i,sectionName);
                    itemRowHolder.recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    itemRowHolder.recycler_view_list.setAdapter(learningInnerDataAdapter);
                    childCounter += 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
*/

    }

    private List<ContentTable> getList(List<ContentTable> nodelist) {
        List<ContentTable> tempList = new ArrayList<>();
        for (int i = 0; i < nodelist.size() && i < 6; i++)
            tempList.add(nodelist.get(i));
        return tempList;
    }

    @Override
    public int getItemViewType(int position) {
/*
        if (dataList.get(position).getNodeType() != null) {
            switch (dataList.get(position).getNodeType()) {
                case TYPE_HEADER:
                    return 0;
                case TYPE_ITEM:
                    return 2;
                case TYPE_FOOTER:
                    return 3;
                default:
                    return 1;
            }
        } else
*/
            return 1;
    }

    @Override
    public int getItemCount() {
        return progressArray.length;
    }

    public class FolderHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        RecyclerView recycler_view_grid_list;
        MaterialCardView card_layout;

        FolderHolder(View view) {
            super(view);
            this.itemTitle = view.findViewById(R.id.lbl_Progreess_header);
            this.recycler_view_grid_list = view.findViewById(R.id.recycler_view_grid_list);
            this.card_layout= view.findViewById(R.id.profile_card_layout);
        }

    }
}