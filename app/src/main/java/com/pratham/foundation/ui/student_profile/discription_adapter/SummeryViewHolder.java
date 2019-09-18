package com.pratham.foundation.ui.student_profile.discription_adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pratham.foundation.R;


public class SummeryViewHolder extends RecyclerView.ViewHolder {
    TextView title,count;

    public SummeryViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        count = itemView.findViewById(R.id.count);
    }
}
