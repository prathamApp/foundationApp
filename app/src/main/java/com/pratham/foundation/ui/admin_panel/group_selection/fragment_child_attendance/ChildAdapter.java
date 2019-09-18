package com.pratham.foundation.ui.admin_panel.group_selection.fragment_child_attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.Student;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildHolder> {
    private ArrayList<Student> datalist;
    private ArrayList<Integer> male_avatar;
    private ArrayList<Integer> female_avatar;
    private Context context;
    int finalPos;
    private ContractChildAttendance.attendanceView attendanceView;

    public ChildAdapter(Context context, ArrayList<Student> datalist,
                        ArrayList<Integer> female_avatar, ArrayList<Integer> male_avatar, ContractChildAttendance.attendanceView attendanceView) {
        this.context = context;
        this.datalist = datalist;
        this.female_avatar = female_avatar;
        this.male_avatar = male_avatar;
        this.attendanceView = attendanceView;
    }

    @NonNull
    @Override
    public ChildHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = null;
        v = inflater.inflate(R.layout.item_child_attendance, parent, false);
        return new ChildHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChildHolder viewHolder, int pos) {
        // pos = viewHolder.getAdapterPosition();
        viewHolder.child_name.setText(datalist.get(pos).getFullName());
        if(datalist.get(pos).getGender().equalsIgnoreCase("male"))
            viewHolder.child_avatar.setImageResource(male_avatar.get(pos));
        else
            viewHolder.child_avatar.setImageResource(female_avatar.get(pos));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    attendanceView.childItemClicked(datalist.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
            }
        });
        if (datalist.get(viewHolder.getAdapterPosition()).isChecked()) {
            viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.ripple_rectangle_transparent_dark));
            viewHolder.child_name.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.rounded_rectangle_stroke_bg));
            viewHolder.child_name.setTextColor(context.getResources().getColor(R.color.colorBlack));
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class ChildHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.child_name)
        TextView child_name;
        @BindView(R.id.iv_child)
        ImageView child_avatar;
        @BindView(R.id.rl_child_attendance)
        RelativeLayout main_layout;

        public ChildHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
