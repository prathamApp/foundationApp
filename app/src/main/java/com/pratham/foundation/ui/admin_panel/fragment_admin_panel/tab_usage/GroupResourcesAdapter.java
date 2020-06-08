package com.pratham.foundation.ui.admin_panel.fragment_admin_panel.tab_usage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.flexbox.FlexDirection;
import com.pratham.foundation.customView.flexbox.FlexboxLayoutManager;
import com.pratham.foundation.customView.flexbox.JustifyContent;
import com.pratham.foundation.modalclasses.Modal_ResourcePlayedByGroups;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupResourcesAdapter extends RecyclerView.Adapter<GroupResourcesAdapter.ViewHolder> {

    private HashMap<String, List<Modal_ResourcePlayedByGroups>> modal_resourcePlayedByGroups;
    private Context context1;

    public GroupResourcesAdapter(Context context, HashMap<String, List<Modal_ResourcePlayedByGroups>> modal_resourcePlayedByGroups) {
        context1 = context;
        this.modal_resourcePlayedByGroups = modal_resourcePlayedByGroups;
    }

    @NonNull
    @Override
    public GroupResourcesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_stat_grp_resource, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupResourcesAdapter.ViewHolder viewHolder, int i) {
        Set<String> keyset = modal_resourcePlayedByGroups.keySet();
        String[] keys = keyset.toArray(new String[keyset.size()]);
        viewHolder.stat_date.setText(keys[viewHolder.getAdapterPosition()]);
        ResourcesAdapter resourcesAdapter = new ResourcesAdapter(context1,modal_resourcePlayedByGroups.get(keys[viewHolder.getAdapterPosition()]));
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(context1, FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        viewHolder.rv_stat_resource.setLayoutManager(flexboxLayoutManager);
        viewHolder.rv_stat_resource.setAdapter(resourcesAdapter);
    }

    @Override
    public int getItemCount() {
        return modal_resourcePlayedByGroups.size();
    }

    public void updateData(HashMap<String, List<Modal_ResourcePlayedByGroups>> modal_resourcePlayedByGroups) {
        this.modal_resourcePlayedByGroups = modal_resourcePlayedByGroups;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.stat_date)
        TextView stat_date;
        @BindView(R.id.rv_stat_resource)
        RecyclerView rv_stat_resource;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
