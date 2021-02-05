package com.pratham.foundation.ui.app_home.learning_fragment;

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

import static com.pratham.foundation.utility.FC_Constants.TYPE_FOOTER;
import static com.pratham.foundation.utility.FC_Constants.TYPE_HEADER;
import static com.pratham.foundation.utility.FC_Constants.TYPE_ITEM;
import static com.pratham.foundation.utility.FC_Constants.sec_Learning;


public class LearningOuterDataAdapter extends RecyclerView.Adapter {

    private List<ContentTable> dataList;
//    private List<ContentTable> sublistList;
//    private ContentTable contentTable;
    private Context mContext;
//    public int childCounter = 0;
    //    LearningContract.LearningItemClicked fragmentItemClicked;
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
//            case 1:
//            case 2:
            default:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.content_folder_card_tab, viewGroup, false);
                return new FragmentOuterViewHolder(view, fragmentItemClicked);
//            default:
//                return null;
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
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("crashDetection", "getItemViewType : "+position);
        Log.d("ABC_ADAPTER", "getItemViewType 1 OUTER : "+dataList.get(position).getNodeType());
        if (dataList.get(position).getNodeType() != null) {
            String a =dataList.get(position).getNodeType();
            Log.d("crashDetection", "getItemViewType : Type : "+a);
            Log.d("ABC_ADAPTER", "getItemViewType OUTER : "+a);
            switch (a) {
                case TYPE_HEADER:
                case TYPE_FOOTER:
                    return 0;
                case TYPE_ITEM:
                    return 2;
                default:
                    return 1;
            }
        } else {
            Log.d("ABC_ADAPTER", "getItemViewType OUTER ELSE : ");
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

//    private List<ContentTable> getList(List<ContentTable> nodelist) {
//        List<ContentTable> tempList = new ArrayList<>();
//        for (int i = 0; i < nodelist.size() && i < 6; i++)
//            tempList.add(nodelist.get(i));
//        return tempList;
//    }
//    public class FolderHolder extends RecyclerView.ViewHolder {
//        TextView itemTitle;
//        RecyclerView recycler_view_list;
//        Button btnMore, actionBtn;
//
//        FolderHolder(View view) {
//            super(view);
//            this.itemTitle = view.findViewById(R.id.itemTitle);
//            this.recycler_view_list = view.findViewById(R.id.recycler_view_list);
//            this.btnMore = view.findViewById(R.id.btnMore);
//            this.actionBtn = view.findViewById(R.id.ib_action_btn);
//        }
//
//    }
//    public class EmptyHolder extends RecyclerView.ViewHolder {
//        public EmptyHolder(View view) {
//            super(view);
//        }
//    }

}