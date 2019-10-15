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
import com.pratham.foundation.custumView.SansButton;
import com.pratham.foundation.custumView.SansTextView;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_fragment.FactRetrival_;
import com.pratham.foundation.ui.contentPlayer.fillInTheBlanks.FillInTheBlanksFragment;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.KeywordsIdentificationFragment_;
import com.pratham.foundation.ui.contentPlayer.keywords_mapping.KeywordMappingFragment_;
import com.pratham.foundation.ui.contentPlayer.listenAndWritting.ListeningAndWritting;
import com.pratham.foundation.ui.contentPlayer.listenAndWritting.ListeningAndWritting_;
import com.pratham.foundation.ui.contentPlayer.multipleChoice.McqFillInTheBlanksFragment;
import com.pratham.foundation.ui.contentPlayer.paragraph_writing.ParagraphWritingFragment_;
import com.pratham.foundation.ui.contentPlayer.reading.ReadingFragment;
import com.pratham.foundation.ui.contentPlayer.trueFalse.TrueFalseFragment;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import java.util.List;

public class SequenceGameAdapter extends RecyclerView.Adapter<SequenceGameAdapter.MyViewHolder> {
    private Context context;
    List<ContentTable> gamesList;
    private String contentPath, contentTitle, StudentID, resId;
    private boolean onSdCard;

    public SequenceGameAdapter(Context context, List<ContentTable> gamesList) {
        this.context = context;
        this.gamesList = gamesList;
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
                ContentTable contentTable1 = gamesList.get(myViewHolder.getAdapterPosition());
                Bundle bundle = new Bundle();
                bundle.putString("contentPath", contentTable1.getResourcePath());
                bundle.putString("StudentID", FC_Constants.currentStudentID);
                bundle.putString("resId", contentTable1.getResourceId());
                bundle.putString("contentName", contentTable1.getNodeTitle());
                bundle.putBoolean("onSdCard", true);
                switch (contentTable1.getResourceId()) {
                    case "101":
                        FC_Utility.showFragment((Activity) context, new FactRetrival_(), R.id.RL_CPA,
                                bundle, FactRetrival_.class.getSimpleName());
                        break;
                    case "102":
                        FC_Utility.showFragment((Activity) context, new KeywordsIdentificationFragment_(), R.id.RL_CPA,
                                bundle, KeywordsIdentificationFragment_.class.getSimpleName());
                        break;

                    case "103":
                        FC_Utility.showFragment((Activity) context, new KeywordMappingFragment_(), R.id.RL_CPA,
                                bundle, KeywordMappingFragment_.class.getSimpleName());
                        break;
                    case "104":
                        FC_Utility.showFragment((Activity) context, new ParagraphWritingFragment_(), R.id.RL_CPA,
                                bundle, ParagraphWritingFragment_.class.getSimpleName());
                        break;
                    case "105":
                        FC_Utility.showFragment((Activity) context, new ListeningAndWritting_(), R.id.RL_CPA,
                                bundle, ListeningAndWritting_.class.getSimpleName());
                        break;
                    case "106":
                        FC_Utility.showFragment((Activity) context, new ReadingFragment(), R.id.RL_CPA,
                                bundle, ReadingFragment.class.getSimpleName());
                        break;
                    case "108":
                        FC_Utility.showFragment((Activity) context, new McqFillInTheBlanksFragment(), R.id.RL_CPA,
                                bundle, McqFillInTheBlanksFragment.class.getSimpleName());
                        break;
                    case "109":
                        FC_Utility.showFragment((Activity) context, new TrueFalseFragment(), R.id.RL_CPA,
                                bundle, TrueFalseFragment.class.getSimpleName());
                        break;
                    case "110":
                        FC_Utility.showFragment((Activity) context, new FillInTheBlanksFragment(), R.id.RL_CPA,
                                bundle, FillInTheBlanksFragment.class.getSimpleName());
                        break;

                }
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
