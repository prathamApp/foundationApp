package com.pratham.foundation.ui.contentPlayer.sequenceLayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansButton;
import com.pratham.foundation.customView.SansTextView;
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
        View view = LayoutInflater.from(context).inflate(R.layout.sequence_game_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        ContentTable contentTable = gamesList.get(i);
        myViewHolder.gameName.setText(contentTable.getNodeTitle());
        myViewHolder.play.setOnClickListener(new View.OnClickListener() {
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        SansButton play;
        SansTextView gameName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.gameIcon);
            play = itemView.findViewById(R.id.play);
            gameName = itemView.findViewById(R.id.gameName);
        }
    }
}
