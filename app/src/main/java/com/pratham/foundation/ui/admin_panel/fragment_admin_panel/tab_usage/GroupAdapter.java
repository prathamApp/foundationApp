package com.pratham.foundation.ui.admin_panel.fragment_admin_panel.tab_usage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.Modal_TotalDaysGroupsPlayed;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds;
    private ContractOptions contractOptions;
    Context context1;

    public GroupAdapter(Context context, List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds, ContractOptions contractOptions) {
        context1 = context;
        this.modal_totalDaysGroupsPlayeds = modal_totalDaysGroupsPlayeds;
        this.contractOptions = contractOptions;
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_stat_group, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder viewHolder, int i) {
//        FC_Utility.setAppLocal(context1, FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI));
        Objects.requireNonNull(viewHolder.item_grp_card).setBackgroundResource(R.drawable.card_color_bg9);
        viewHolder.stat_grp_name.setText(modal_totalDaysGroupsPlayeds.get(viewHolder.getAdapterPosition()).getGroupName());
/*        String a = context1.getResources().getString(R.string.usage)
                +" "+ modal_totalDaysGroupsPlayeds.get(viewHolder.getAdapterPosition()).getDates() +" "+
                context1.getResources().getString(R.string.days);*/
        viewHolder.stat_grp_date.setText(context1.getResources().getString(R.string.usage)
                +" "+ modal_totalDaysGroupsPlayeds.get(viewHolder.getAdapterPosition()).getDates() +" "+
                context1.getResources().getString(R.string.days));
        viewHolder.itemView.setOnClickListener(v -> {
            contractOptions.menuClicked(viewHolder.getAdapterPosition(), null, null);
        });
    }

    @Override
    public int getItemCount() {
        return modal_totalDaysGroupsPlayeds.size();
    }

    public void updateItems(List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds) {
        this.modal_totalDaysGroupsPlayeds = modal_totalDaysGroupsPlayeds;
        notifyDataSetChanged();
    }

    public List<Modal_TotalDaysGroupsPlayed> getItems() {
        return modal_totalDaysGroupsPlayeds;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.stat_grp_name)
        TextView stat_grp_name;
        @BindView(R.id.stat_grp_date)
        TextView stat_grp_date;
        @BindView(R.id.item_grp_card)
        MaterialCardView item_grp_card;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
