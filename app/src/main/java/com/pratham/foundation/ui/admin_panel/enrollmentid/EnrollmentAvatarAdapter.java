package com.pratham.foundation.ui.admin_panel.enrollmentid;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.AvatarModal;
import com.pratham.foundation.ui.bottom_fragment.add_student.AvatarClickListener;

import java.util.ArrayList;


/**
 * Created by Anki on 10/30/2018.
 */

public class EnrollmentAvatarAdapter extends RecyclerView.Adapter<EnrollmentAvatarAdapter.MyViewHolder> {
    /* List<String> studentAvatarList;*/
    ArrayList<AvatarModal> avatarList;
    Context context;
    AvatarClickListener avatarClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;

        public MyViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.iv_avatar);
           /* word.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
        }
    }

    public EnrollmentAvatarAdapter(Context context, Activity fragment, ArrayList<AvatarModal> avatarList) {
        this.avatarList = avatarList;
        this.context = context;
        this.avatarClickListener = (AvatarClickListener) fragment;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_avatar_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AvatarModal studentAvatar = avatarList.get(position);
        holder.avatar.setImageResource(R.drawable.b1);
        switch (studentAvatar.getAvatarName()) {
            case "b1.png":
                holder.avatar.setImageResource(R.drawable.b1);
                break;
            case "b2.png":
                holder.avatar.setImageResource(R.drawable.b2);
                break;
            case "b3.png":
                holder.avatar.setImageResource(R.drawable.b3);
                break;
            case "g1.png":
                holder.avatar.setImageResource(R.drawable.g1);
                break;
            case "g2.png":
                holder.avatar.setImageResource(R.drawable.g2);
                break;
            case "g3.png":
                holder.avatar.setImageResource(R.drawable.g3);
                break;
            default:
                holder.avatar.setImageResource(R.drawable.ic_grp_btn);
                break;
        }
        if (studentAvatar.getClickFlag())
            holder.avatar.setBackground(context.getResources().getDrawable(R.drawable.card_color_bg1));
        else
            holder.avatar.setBackground(context.getResources().getDrawable(R.drawable.card_color_bg6));

        holder.avatar.setOnClickListener(v -> avatarClickListener.onAvatarClick(position, studentAvatar.getAvatarName()));
    }

    @Override
    public int getItemCount() {
        return avatarList.size();
    }
}