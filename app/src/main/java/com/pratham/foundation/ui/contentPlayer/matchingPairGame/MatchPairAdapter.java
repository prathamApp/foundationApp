package com.pratham.foundation.ui.contentPlayer.matchingPairGame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.MatchThePair;

import java.util.List;

public class MatchPairAdapter extends RecyclerView.Adapter<MatchPairAdapter.MyViewHolder> {
    List<MatchThePair> pairList;
    Context context;

    public MatchPairAdapter(List<MatchThePair> pairList, Context context) {
        this.pairList = pairList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tv_text);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_simple_text_row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        MatchThePair matchThePair = pairList.get(i);
//        if (FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI).equalsIgnoreCase("Gujarati")) {
//            Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/muktavaani_gujarati.ttf");
//            myViewHolder.text.setTypeface(face);
//            myViewHolder.text.setText(matchThePair.getParaText());
//        } else if (FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI).equalsIgnoreCase("Assamese")) {
//            Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/lohit_oriya.ttf");
//            myViewHolder.text.setTypeface(face);
//            myViewHolder.text.setText(matchThePair.getParaText());
//        } else if (FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI).equalsIgnoreCase("Odiya")) {
//            Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/lohit_oriya.ttf");
//            myViewHolder.text.setTypeface(face);
//            myViewHolder.text.setText(matchThePair.getParaText());
//        } else
            myViewHolder.text.setText(matchThePair.getParaText());
    }

    @Override
    public int getItemCount() {
        return pairList.size();
    }
}
