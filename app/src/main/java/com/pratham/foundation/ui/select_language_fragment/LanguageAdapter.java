package com.pratham.foundation.ui.select_language_fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.view_holders.SelectLangViewHolder;

import java.util.List;


public class LanguageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<ContentTable> contentViewList;
    SelectLangContract.LangItemClicked contentClicked;

    public LanguageAdapter(Context mContext, List<ContentTable> contentViewList, SelectLangContract.LangItemClicked contentClicked) {
        this.mContext = mContext;
        this.contentViewList = contentViewList;
        this.contentClicked = contentClicked;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        switch (viewtype) {
            case 1:
                LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
                view = file.inflate(R.layout.language_item, viewGroup, false);
                return new SelectLangViewHolder(view, contentClicked);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        final ContentTable contentList = contentViewList.get(position);
        switch (viewHolder.getItemViewType()) {
            case 1:
                //file
                SelectLangViewHolder contentFileViewHolder = (SelectLangViewHolder) viewHolder;
                contentFileViewHolder.setItem(contentList,position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contentViewList.size();
    }
}