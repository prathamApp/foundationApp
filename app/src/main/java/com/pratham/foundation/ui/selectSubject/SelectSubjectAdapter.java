package com.pratham.foundation.ui.selectSubject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;

import java.util.ArrayList;
import java.util.List;

public class SelectSubjectAdapter extends RecyclerView.Adapter<SelectSubjectAdapter.Myviewholder> {
    Context context;
    List<ContentTable> datalist;
    List selectedOption = new ArrayList();
    SelectSubjectContract.ItemClicked itemClicked;


    public SelectSubjectAdapter(Context context, List<ContentTable> datalist) {
        this.context = context;
        this.datalist = datalist;
        itemClicked = (SelectSubjectContract.ItemClicked) context;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subject, viewGroup, false);
        return new Myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myviewholder myviewholder, int i) {
        if (datalist.get(i).getNodeTitle().equalsIgnoreCase("Science")||
                datalist.get(i).getNodeKeywords().equalsIgnoreCase("Science")) {
            myviewholder.content_thumbnail.setImageResource(R.drawable.sci_01);
        } else if (datalist.get(i).getNodeTitle().equalsIgnoreCase("Maths")||
                datalist.get(i).getNodeKeywords().equalsIgnoreCase("Maths")) {
            myviewholder.content_thumbnail.setImageResource(R.drawable.math_01);
        } else if (datalist.get(i).getNodeTitle().equalsIgnoreCase("English")||
                datalist.get(i).getNodeKeywords().equalsIgnoreCase("English")) {
            myviewholder.content_thumbnail.setImageResource(R.drawable.eng_01);
        } else if (datalist.get(i).getNodeTitle().equalsIgnoreCase("H Science")||
                datalist.get(i).getNodeKeywords().equalsIgnoreCase("H Science")) {
            myviewholder.content_thumbnail.setImageResource(R.drawable.ic_cos_h_science);
        } else {
            myviewholder.content_thumbnail.setImageResource(R.drawable.lang_01);
        }
        myviewholder.content_thumbnail.setOnClickListener(v -> {
            itemClicked.onItemClicked(datalist.get(i));
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class Myviewholder extends RecyclerView.ViewHolder {
        ImageView content_thumbnail;

        public Myviewholder(@NonNull View itemView) {
            super(itemView);
            content_thumbnail = itemView.findViewById(R.id.content_thumbnail);
        }
    }

    public List getSelectedOptionList() {
        return selectedOption;
    }
}
