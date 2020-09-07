package com.pratham.foundation.ui.admin_panel.group_selection.fragment_select_group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.Groups;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private ArrayList<Groups> datalist;
    private Context context;
    private ContractGroup contractGroup;

    public GroupAdapter(Context context, ArrayList<Groups> datalist, ContractGroup contractGroup) {
        this.context = context;
        this.datalist = datalist;
        this.contractGroup = contractGroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_group, parent, false);
        return new GroupAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int pos) {
        pos = viewHolder.getAdapterPosition();
        viewHolder.group_name.setText(datalist.get(pos).getGroupName());
        viewHolder.group_name.setSelected(true);
        if (datalist.get(pos).isSelected()) {
            viewHolder.group_card.setBackground(context.getResources().getDrawable(R.drawable.card_color_bg1));
            viewHolder.group_name.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            viewHolder.group_card.setBackground(context.getResources().getDrawable(R.drawable.card_color_bg6));
            viewHolder.group_name.setTextColor(context.getResources().getColor(R.color.dark_blue));
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contractGroup.groupItemClicked(datalist.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
            }
        });
    }

    public void updateGroupItems(final ArrayList<Groups> newGroups) {
        datalist = newGroups;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.group_card)
        MaterialCardView group_card;
        @BindView(R.id.group_name)
        TextView group_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
