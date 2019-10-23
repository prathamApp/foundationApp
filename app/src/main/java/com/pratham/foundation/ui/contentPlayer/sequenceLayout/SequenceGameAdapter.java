package com.pratham.foundation.ui.contentPlayer.sequenceLayout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import com.pratham.foundation.ui.contentPlayer.doing.DoingFragment;
import com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment.FactRetrieval_;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.Fact_Retrieval_Fragment;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.Fact_Retrieval_Fragment_;
import com.pratham.foundation.ui.contentPlayer.fillInTheBlanks.FillInTheBlanksFragment;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.KeywordsIdentificationFragment_;
import com.pratham.foundation.ui.contentPlayer.keywords_mapping.KeywordMappingFragment_;
import com.pratham.foundation.ui.contentPlayer.listenAndWritting.ListeningAndWritting_;
import com.pratham.foundation.ui.contentPlayer.multipleChoice.McqFillInTheBlanksFragment;
import com.pratham.foundation.ui.contentPlayer.paragraph_writing.ParagraphWritingFragment_;
import com.pratham.foundation.ui.contentPlayer.pictionary.pictionaryFragment;
import com.pratham.foundation.ui.contentPlayer.reading.ReadingFragment;
import com.pratham.foundation.ui.contentPlayer.story_reading.StoryReadingFragment;
import com.pratham.foundation.ui.contentPlayer.story_reading.StoryReadingFragment_;
import com.pratham.foundation.ui.contentPlayer.trueFalse.TrueFalseFragment;
import com.pratham.foundation.ui.contentPlayer.video_view.FragmentVideoView;
import com.pratham.foundation.ui.contentPlayer.video_view.FragmentVideoView_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

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
