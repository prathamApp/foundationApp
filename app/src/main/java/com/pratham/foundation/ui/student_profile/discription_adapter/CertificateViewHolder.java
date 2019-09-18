package com.pratham.foundation.ui.student_profile.discription_adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

import com.pratham.foundation.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CertificateViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.lv_Leaderboard)
    ListView lv_Leaderboard;

    public CertificateViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
