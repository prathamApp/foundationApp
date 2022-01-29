package com.pratham.foundation.ui.app_home.learning_fragment;

import static com.pratham.foundation.utility.FC_Constants.TYPE_FOOTER;
import static com.pratham.foundation.utility.FC_Constants.TYPE_HEADER;
import static com.pratham.foundation.utility.FC_Constants.TYPE_ITEM;
import static com.pratham.foundation.utility.FC_Constants.sec_Learning;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.ui.app_home.FragmentItemClicked;
import com.pratham.foundation.view_holders.EmptyHolder;
import com.pratham.foundation.view_holders.FragmentOuterViewHolder;

import java.util.List;


public class LearningOuterDataAdapter extends RecyclerView.Adapter {

    private final List<ContentTable> dataList;
    private final Context mContext;
    FragmentItemClicked fragmentItemClicked;

    public LearningOuterDataAdapter(Context context, List<ContentTable> dataList,
            /*LearningContract.LearningItemClicked fragmentItemClicked*/ FragmentItemClicked fragmentItemClicked) {
        this.dataList = dataList;
        this.mContext = context;
        this.fragmentItemClicked = fragmentItemClicked;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        Log.d("crashDetection", "onCreateViewHolder viewtype : "+viewtype);
        switch (viewtype) {
            case 0:
                LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
                view = header.inflate(R.layout.list_header, viewGroup, false);
                return new EmptyHolder(view);
            case 999:
                LayoutInflater footer = LayoutInflater.from(viewGroup.getContext());
                view = footer.inflate(R.layout.student_item_file_footer, viewGroup, false);
                return new EmptyHolder(view);
            default:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.content_folder_card_tab, viewGroup, false);
                return new FragmentOuterViewHolder(view, fragmentItemClicked);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        switch (viewHolder.getItemViewType()) {
            case 1:
            case 2:
                FragmentOuterViewHolder itemRowHolder = (FragmentOuterViewHolder) viewHolder;
                itemRowHolder.setOuterItem(dataList.get(i),i,sec_Learning);
            case 999:
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position).getNodeType() != null) {
            String a =dataList.get(position).getNodeType();
            switch (a) {
                case TYPE_HEADER:
                    return 0;
                case TYPE_FOOTER:
                    return 999;
                case TYPE_ITEM:
                    return 2;
                default:
                    return 1;
            }
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }
}