package com.pratham.foundation.ui.app_home.practice_fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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
import static com.pratham.foundation.utility.FC_Constants.sec_Practice;


public class PracticeOuterDataAdapter extends RecyclerView.Adapter {

    private List<ContentTable> dataList;
    private List<ContentTable> sublistList;
    private ContentTable contentTable;
    private Context mContext;
    public int childCounter = 0;
//    PracticeContract.PracticeItemClicked fragmentItemClicked;
    FragmentItemClicked fragmentItemClicked;


    public PracticeOuterDataAdapter(Context context, List<ContentTable> dataList,
                                    FragmentItemClicked fragmentItemClicked) {
        this.dataList = dataList;
        this.mContext = context;
        this.fragmentItemClicked = fragmentItemClicked;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        switch (viewtype) {
            case 0:
                LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
                view = header.inflate(R.layout.list_header, viewGroup, false);
                return new EmptyHolder(view);
            case 1:
            case 2:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.content_folder_card_tab, viewGroup, false);
                return new FragmentOuterViewHolder(view,fragmentItemClicked);
            default:
                return null;

        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        switch (viewHolder.getItemViewType()) {
            case 1:
            case 2:
                FragmentOuterViewHolder itemRowHolder = (FragmentOuterViewHolder) viewHolder;
                itemRowHolder.setOuterItem(dataList.get(i),i,sec_Practice);
//                FolderHolder itemRowHolder = (FolderHolder) viewHolder;
//                contentTable = dataList.get(i);
//                sublistList = new ArrayList<>();
//                final String sectionName = contentTable.getNodeTitle();
//                if (contentTable.getContentType() != null && contentTable.getResourceType() != null) {
//                    if (contentTable.getNodeType().equalsIgnoreCase("PreResource")
//                            && (contentTable.isDownloaded.equalsIgnoreCase("false"))) {
////                    if ((contentTable.getContentType().equalsIgnoreCase("FullDownload") ||
////                            contentTable.getResourceType().equalsIgnoreCase("FullDownload"))
////                            && (contentTable.isDownloaded.equalsIgnoreCase("false"))) {
//                        itemRowHolder.btnMore.setVisibility(View.GONE);
//                        itemRowHolder.actionBtn.setVisibility(View.VISIBLE);
//                        itemRowHolder.actionBtn.setOnClickListener(v -> fragmentItemClicked.onContentDownloadClicked(dataList.get(i),
//                                i, 0, "" + FC_Constants.FULL_DOWNLOAD));
//                    } else {
//                        int size = dataList.get(i).getNodelist().size() - 2;
//                        itemRowHolder.btnMore.setText("SEE ALL " + size);
//                        itemRowHolder.btnMore.setVisibility(View.GONE);
//                        if (size > 6) {
//                            itemRowHolder.btnMore.setVisibility(View.VISIBLE);
//                            itemRowHolder.btnMore.setOnClickListener(v -> fragmentItemClicked.seeMore(dataList.get(i).getNodeId(),
//                                    dataList.get(i).getNodeTitle()));
//                        }
//                        itemRowHolder.actionBtn.setVisibility(View.GONE);
//                    }
//                } else {
//                    int size = dataList.get(i).getNodelist().size() - 2;
//                    itemRowHolder.btnMore.setText("SEE ALL " + size);
//                    itemRowHolder.btnMore.setVisibility(View.GONE);
//                    if (size > 6) {
//                        itemRowHolder.btnMore.setVisibility(View.VISIBLE);
//                        itemRowHolder.btnMore.setOnClickListener(v -> fragmentItemClicked.seeMore(dataList.get(i).getNodeId(),
//                                dataList.get(i).getNodeTitle()));
//                    }
//                    itemRowHolder.actionBtn.setVisibility(View.GONE);
//                }
//                sublistList = getList(dataList.get(i).getNodelist());
///*            if (sublistList.size()>4)
//                itemRowHolder.next_nav_btn.setVisibility(View.VISIBLE);
//            else
//                itemRowHolder.next_nav_btn.setVisibility(View.GONE);*/
//
//                try {
//                    itemRowHolder.itemTitle.setText(sectionName);
//                    PracticeInnerDataAdapter learningInnerDataAdapter = new PracticeInnerDataAdapter(mContext, sublistList, fragmentItemClicked, i,sectionName);
//                    itemRowHolder.recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
//                    itemRowHolder.recycler_view_list.setAdapter(learningInnerDataAdapter);
//                    childCounter += 1;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position).getNodeType() != null) {
            switch (dataList.get(position).getNodeType()) {
                case TYPE_HEADER:
                case TYPE_FOOTER:
                    return 0;
                case TYPE_ITEM:
                    return 2;
                default:
                    return 1;
            }
        } else
            return 1;
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
//
//    public class EmptyHolder extends RecyclerView.ViewHolder {
//        public EmptyHolder(View view) {
//            super(view);
//        }
//    }
//    public class FolderHolder extends RecyclerView.ViewHolder {
//
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
}