package com.pratham.foundation.ui.home_screen.level;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pratham.foundation.R;

public class LevelImageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private int contentTableList;

    public class ImageHolder extends RecyclerView.ViewHolder {

        ImageView iv_level_img;

        public ImageHolder(View view) {
            super(view);
            iv_level_img = view.findViewById(R.id.iv_level_img);
        }
    }

    public LevelImageAdapter(Context mContext, int contentTableList) {
        this.mContext = mContext;
        this.contentTableList = contentTableList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
        view = header.inflate(R.layout.fc_level_card, viewGroup, false);
        return new LevelImageAdapter.ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ImageHolder holder = (ImageHolder) viewHolder;

        switch (position){
            case 0:
                holder.iv_level_img.setImageResource(R.drawable.level_1);
                break;
            case 1:
                holder.iv_level_img.setImageResource(R.drawable.level_2);
                break;
            case 2:
                holder.iv_level_img.setImageResource(R.drawable.level_3);
                break;
            case 3:
                holder.iv_level_img.setImageResource(R.drawable.level_4);
                break;
            case 4:
                holder.iv_level_img.setImageResource(R.drawable.level_5);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contentTableList;
    }
}