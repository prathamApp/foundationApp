package com.pratham.foundation.ui.home_temp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;

import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.MyViewHolder> {

    private Context mContext;
    private List<ContentTable> contentTableList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout level_card_view;
        public ImageView iv_level_img;

        public MyViewHolder(View view) {
            super(view);
            level_card_view = view.findViewById(R.id.level_card_view);
            iv_level_img = view.findViewById(R.id.iv_level_img);
        }
    }

    public LevelAdapter(Context mContext, List<ContentTable> contentTableList) {
        this.mContext = mContext;
        this.contentTableList = contentTableList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ras_level_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if(contentTableList.get(position).getNodeTitle().contains("1"))
            holder.iv_level_img.setImageResource(R.drawable.level_1);
        else if(contentTableList.get(position).getNodeTitle().contains("2"))
            holder.iv_level_img.setImageResource(R.drawable.level_2);
        else if(contentTableList.get(position).getNodeTitle().contains("3"))
            holder.iv_level_img.setImageResource(R.drawable.level_3);
        else if(contentTableList.get(position).getNodeTitle().contains("4"))
            holder.iv_level_img.setImageResource(R.drawable.level_4);
        else if(contentTableList.get(position).getNodeTitle().contains("5"))
            holder.iv_level_img.setImageResource(R.drawable.level_5);
    }

    @Override
    public int getItemCount() {
        return contentTableList.size();
    }
}