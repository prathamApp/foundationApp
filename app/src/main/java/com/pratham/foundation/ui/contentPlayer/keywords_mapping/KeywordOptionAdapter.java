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

import java.util.ArrayList;
import java.util.List;

public class KeywordOptionAdapter extends RecyclerView.Adapter<KeywordOptionAdapter.Myviewholder> {
    Context context;
    List<OptionKeyMap> datalist;
    List selectedOption = new ArrayList();
    int maxSelect;

    public KeywordOptionAdapter(Context context, List<OptionKeyMap> datalist, int maxSelect) {
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
        myviewholder.textView.setText(datalist.get(i).getOption());
        if (datalist.get(myviewholder.getAdapterPosition()).isIsclicked()) {
            myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.correct_bg));
        } else {
            myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_bg));
        }
        myviewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (datalist.get(myviewholder.getAdapterPosition()).isIsclicked()) {
                    if (selectedOption.contains(datalist.get(myviewholder.getAdapterPosition()).getOption())) {
                        selectedOption.remove(datalist.get(myviewholder.getAdapterPosition()).getOption());
                    }
                    datalist.get(myviewholder.getAdapterPosition()).setIsclicked(false);
                    myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_bg));
                } else {
                    if (maxSelect >selectedOption.size()) {
                        if (!selectedOption.contains(datalist.get(myviewholder.getAdapterPosition()).getOption())) {
                            selectedOption.add(datalist.get(myviewholder.getAdapterPosition()).getOption());
                        }
                        datalist.get(myviewholder.getAdapterPosition()).setIsclicked(true);
                        myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.correct_bg));
                    }else {
                        Toast.makeText(context,"you can not select more than "+maxSelect+" items",Toast.LENGTH_LONG).show();
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
}