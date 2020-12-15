package com.pratham.foundation.ui.app_home.profile_new;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;

import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.progressImgSubArray;
import static com.pratham.foundation.utility.FC_Constants.progressSubArray;
import static com.pratham.foundation.utility.FC_Constants.statusImgSubArray;
import static com.pratham.foundation.utility.FC_Constants.statusSubArray;


public class ProfileOuterDataAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private String[] progressArray;

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
            if (progressArray[i].equalsIgnoreCase("Progress")) {
                sublistList = progressSubArray;
                subImgList = progressImgSubArray;
            } else if (progressArray[i].equalsIgnoreCase("Status")) {
                sublistList = statusSubArray;
                subImgList = statusImgSubArray;
            }

            ProfileInnerDataAdapter ProfileInnerDataAdapter = new ProfileInnerDataAdapter(mContext,
                    sublistList, subImgList, profileItemClicked, i);
            itemRowHolder.recycler_view_grid_list.setLayoutManager(new GridLayoutManager(mContext, 2));
            itemRowHolder.recycler_view_grid_list.setAdapter(ProfileInnerDataAdapter);
            childCounter += 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                case TYPE_FOOTER:
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
            this.card_layout = view.findViewById(R.id.profile_card_layout);
        }

    }
}