package com.pratham.foundation.ui.contentPlayer.reading_rhyming;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.ModalRhymingWords;

import java.util.List;

import static com.pratham.foundation.ui.contentPlayer.reading_rhyming.ReadingRhymesActivity.newData;


public class RhymingWordsAdapter extends RecyclerView.Adapter<RhymingWordsAdapter.MyViewHolder> {

    public Context mContext;
    private int lastPos = -1;
    public List<ModalRhymingWords> rhymingWordsList;
    RhymingWordClicked wordClicked;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView rhymingWord;
        public RelativeLayout rl_card, rhyming_card_view;

        public MyViewHolder(View view) {
            super(view);
            rhymingWord = view.findViewById(R.id.rhyming_card_word);
            rl_card = view.findViewById(R.id.rl_card);
            rhyming_card_view = view.findViewById(R.id.rhyming_card_view);
        }
    }

    public RhymingWordsAdapter(Context mContext, List<ModalRhymingWords> rhymingWordsLists, RhymingWordClicked wordClicked) {
        this.mContext = mContext;
        this.rhymingWordsList = rhymingWordsLists;
        this.wordClicked = wordClicked;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rhyming_word_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ModalRhymingWords wordList = rhymingWordsList.get(position);

        if(position % 2 == 0)
            holder.rhyming_card_view.setGravity(RelativeLayout.ALIGN_RIGHT);
        else
            holder.rhyming_card_view.setGravity(RelativeLayout.ALIGN_LEFT);

        holder.rhymingWord.setText(wordList.getWord().toUpperCase());
        holder.rhymingWord.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        if (wordList.isCorrectRead())
            holder.rhymingWord.setTextColor(mContext.getResources().getColor(R.color.colorBtnGreenDark));
        if (newData && wordList.isReadOut()) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.rhyming_zoom_in_and_out);
            holder.rhymingWord.startAnimation(animation);
        }

        holder.rl_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordClicked.onItemClicked(position, wordList.getWordAudio());
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.rhyming_zoom_in_and_out);
                holder.rhymingWord.startAnimation(animation);
            }
        });

    }

    @Override
    public int getItemCount() {
        return rhymingWordsList.size();
    }
}