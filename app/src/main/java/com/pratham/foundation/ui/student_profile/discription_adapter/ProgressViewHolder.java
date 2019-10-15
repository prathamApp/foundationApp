package com.pratham.foundation.ui.student_profile.discription_adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.pratham.foundation.R;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.progress)
    ProgressLayout progress;

    @BindView(R.id.title)
    TextView title;

    public ProgressViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
