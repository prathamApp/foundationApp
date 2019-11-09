package com.pratham.foundation.ui.contentPlayer.keywords_mapping;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.OptionKeyMap;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.utility.FC_Utility;

import java.util.ArrayList;
import java.util.List;

public class KeywordOptionAdapter extends RecyclerView.Adapter<KeywordOptionAdapter.Myviewholder> {
    Context context;
    List<ScienceQuestionChoice> datalist;
    List<ScienceQuestionChoice> selectedOption = new ArrayList();
    int maxSelect;
    boolean isClickable = true;

    public KeywordOptionAdapter(Context context, List<ScienceQuestionChoice> datalist, int maxSelect) {
        this.context = context;
        this.datalist = datalist;
        this.maxSelect = maxSelect;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_keyword, viewGroup, false);
        return new Myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myviewholder myviewholder, int i) {
        myviewholder.textView.setText(datalist.get(i).getSubQues());
        if (datalist.get(myviewholder.getAdapterPosition()).isIsclicked()) {
            myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.correct_bg));
        } else {
            myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_bg));
        }
        myviewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickable) {
                    if (datalist.get(myviewholder.getAdapterPosition()).isIsclicked()) {
                        if (selectedOption.contains(datalist.get(myviewholder.getAdapterPosition()))) {
                            selectedOption.remove(datalist.get(myviewholder.getAdapterPosition()));
                        }
                        datalist.get(myviewholder.getAdapterPosition()).setIsclicked(false);
                        myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_bg));
                    } else {
                        if (!selectedOption.contains(datalist.get(myviewholder.getAdapterPosition()))) {
                            datalist.get(myviewholder.getAdapterPosition()).setStartTime(FC_Utility.getCurrentDateTime());
                            datalist.get(myviewholder.getAdapterPosition()).setEndTime(FC_Utility.getCurrentDateTime());
                            selectedOption.add(datalist.get(myviewholder.getAdapterPosition()));
                        }
                        datalist.get(myviewholder.getAdapterPosition()).setIsclicked(true);
                        myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.correct_bg));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class Myviewholder extends RecyclerView.ViewHolder {
        TextView textView;

        public Myviewholder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.keywordname);
        }
    }

    public List getSelectedOptionList() {
        return selectedOption;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }
}
