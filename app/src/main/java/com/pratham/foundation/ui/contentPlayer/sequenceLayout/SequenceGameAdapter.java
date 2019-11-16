package com.pratham.foundation.ui.contentPlayer.sequenceLayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;

import java.util.List;

public class SequenceGameAdapter extends RecyclerView.Adapter<SequenceGameAdapter.MyViewHolder> {
    private Context context;
    List<ContentTable> gamesList;
    private String contentPath, contentTitle, StudentID, resId;
    private boolean onSdCard;
    SequeanceLayoutContract.clickListner clickListner;

    public SequenceGameAdapter(Context context, List<ContentTable> gamesList, SequeanceLayoutContract.clickListner clickListner) {
        this.context = context;
        this.gamesList = gamesList;
        this.clickListner = clickListner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        ContentTable contentTable = gamesList.get(i);
        myViewHolder.title.setText(contentTable.getNodeTitle());
        myViewHolder.ib_action_btn.setVisibility(View.GONE);
        myViewHolder.content_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameConstatnts.currentGameAdapterposition=myViewHolder.getAdapterPosition();
                ContentTable contentTable1 = gamesList.get(myViewHolder.getAdapterPosition());
                clickListner.contentClicked(contentTable1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gamesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail, ib_action_btn;
        public RelativeLayout content_card_view;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.content_title);
            thumbnail = view.findViewById(R.id.content_thumbnail);
            content_card_view = view.findViewById(R.id.content_card_view);
            ib_action_btn = view.findViewById(R.id.ib_action_btn);
        }
    }

}