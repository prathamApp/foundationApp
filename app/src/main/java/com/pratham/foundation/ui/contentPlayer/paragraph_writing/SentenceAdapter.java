package com.pratham.foundation.ui.contentPlayer.paragraph_writing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.R;

import java.util.List;

public class SentenceAdapter extends RecyclerView.Adapter<SentenceAdapter.MyviewHolder> {
    List<String> datalist;
    Context context;
    int row_index=-1;
    public SentenceAdapter(List datalist, Context context) {
        this.datalist = datalist;
        this.context = context;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.listvew, viewGroup, false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder myviewHolder, int position) {
        myviewHolder.textView.setText(datalist.get(position));
        if(datalist.size()==1){
            ((TextView)myviewHolder.textView).setTextSize(75);
        }
       /* myviewHolder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index=myviewHolder.getAdapterPosition();
                notifyDataSetChanged();
                myviewHolder.rl_main.setBackground(ContextCompat
                        .getDrawable(context, R.drawable.rounded_border_yellow));

            }
        });*/
       /* if(row_index==position){
            myviewHolder.rl_main.setBackground(ContextCompat
                    .getDrawable(context, R.drawable.rounded_border_yellow));
        }
        else
        {
            myviewHolder.rl_main.setBackgroundResource(R.color.white);
        }*/
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RelativeLayout rl_main;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text1);
            rl_main = itemView.findViewById(R.id.rl_main);
        }
    }
}
