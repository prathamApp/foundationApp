package com.pratham.foundation.ui.app_home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTableNew;
import com.pratham.foundation.utility.FC_Constants;

import java.util.ArrayList;
import java.util.List;


public class RecyclerViewDataAdapter extends RecyclerView.Adapter<RecyclerViewDataAdapter.ItemRowHolder> {

    private static final String TYPE_HEADER = "Header";
    private static final String TYPE_ITEM = "Resource";
    private static final String TYPE_FOOTER = "Footer";

    private List<ContentTableNew> dataList;
    private List<ContentTableNew> sublistList;
    private ContentTableNew contentTable;
    private Context mContext;
    public int childCounter = 0;
    ItemClicked itemClicked;

    public RecyclerViewDataAdapter(Context context, List<ContentTableNew> dataList, ItemClicked itemClicked) {
        this.dataList = dataList;
        this.mContext = context;
        this.itemClicked = itemClicked;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = null;

        if (viewType == 0)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_header, null);
        else if (viewType == 1) {
            if (!FC_Constants.TAB_LAYOUT)
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_folder_card, null);
            else
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_folder_card_tab, null);
        } else if (viewType == 2)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_folder_footer, null);
        else if (viewType == 3) {
            if (!FC_Constants.TAB_LAYOUT)
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_folder_card, null);
            else
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_folder_card_tab, null);
        }
        ItemRowHolder mh = new ItemRowHolder(v);
        childCounter = 0;
        return mh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, int i) {
        contentTable = dataList.get(i);
        sublistList = new ArrayList<>();

        if (contentTable.getNodeType().equalsIgnoreCase("Header") ||
                contentTable.getNodeType().equalsIgnoreCase("Footer")) {
        } else {
            final String sectionName = contentTable.getNodeTitle();
            if (contentTable.getContentType() != null && contentTable.getResourceType() != null) {
                if ((contentTable.getContentType().equalsIgnoreCase("FullDownload") ||
                        contentTable.getResourceType().equalsIgnoreCase("FullDownload"))
                        && (contentTable.isDownloaded.equalsIgnoreCase("false"))) {
                    itemRowHolder.btnMore.setVisibility(View.GONE);
                    itemRowHolder.actionBtn.setVisibility(View.VISIBLE);
                    itemRowHolder.actionBtn.setOnClickListener(v -> itemClicked.onContentDownloadClicked(dataList.get(i),
                            i, 0, "" + FC_Constants.FULL_DOWNLOAD));
                } else {
                    int size = dataList.get(i).getNodelist().size() - 2;
                    itemRowHolder.btnMore.setText("SEE ALL " + size);
                    itemRowHolder.btnMore.setVisibility(View.GONE);
                    if (size > 6) {
                        itemRowHolder.btnMore.setVisibility(View.VISIBLE);
                        itemRowHolder.btnMore.setOnClickListener(v -> itemClicked.seeMore(dataList.get(i).getNodeId(),
                                dataList.get(i).getNodeTitle()));
                    }
                    itemRowHolder.actionBtn.setVisibility(View.GONE);
                }
            } else {
                int size = dataList.get(i).getNodelist().size() - 2;
                itemRowHolder.btnMore.setText("SEE ALL " + size);
                itemRowHolder.btnMore.setVisibility(View.GONE);
                if (size > 6) {
                    itemRowHolder.btnMore.setVisibility(View.VISIBLE);
                    itemRowHolder.btnMore.setOnClickListener(v -> itemClicked.seeMore(dataList.get(i).getNodeId(),
                            dataList.get(i).getNodeTitle()));
                }
                itemRowHolder.actionBtn.setVisibility(View.GONE);
            }
            sublistList = getList(dataList.get(i).getNodelist());
/*            if (sublistList.size()>4)
                itemRowHolder.next_nav_btn.setVisibility(View.VISIBLE);
            else
                itemRowHolder.next_nav_btn.setVisibility(View.GONE);*/

            try {
                itemRowHolder.itemTitle.setText(sectionName);
                SectionListDataAdapter itemListDataAdapter = new SectionListDataAdapter(mContext, sublistList, itemClicked, i);
                itemRowHolder.recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                itemRowHolder.recycler_view_list.setAdapter(itemListDataAdapter);
                childCounter += 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<ContentTableNew> getList(List<ContentTableNew> nodelist) {
        List<ContentTableNew> tempList = new ArrayList<>();
        for (int i = 0; i < nodelist.size() && i < 6; i++)
            tempList.add(nodelist.get(i));
        return tempList;
    }

    @Override
    public int getItemViewType(int position) {

        if (dataList.get(position).getNodeType() != null) {
            switch (dataList.get(position).getNodeType()) {
                case TYPE_HEADER:
                    return 0;
                case TYPE_ITEM:
                    return 2;
                case TYPE_FOOTER:
                    return 3;
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

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        RecyclerView recycler_view_list;
        Button btnMore, actionBtn;

        //        ImageButton next_nav_btn;
        ItemRowHolder(View view) {
            super(view);
            this.itemTitle = (TextView) view.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) view.findViewById(R.id.recycler_view_list);
            this.btnMore = (Button) view.findViewById(R.id.btnMore);
            this.actionBtn = (Button) view.findViewById(R.id.ib_action_btn);
//            this.next_nav_btn= (ImageButton) view.findViewById(R.id.next_nav_btn);
        }

    }
}