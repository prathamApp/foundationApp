package com.pratham.foundation.ui.contentPlayer.vocabulary_qa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.view_holders.VocabCategoryViewHolder;

import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter {

    private final List<String> dataList;
    private final Context mContext;
    ReadingVocabularyContract.ReadingCateogryClick cateogryClick;

    public CategoryAdapter(Context context, List<String> dataList, ReadingVocabularyContract.ReadingCateogryClick cateogryClick) {
        this.dataList = dataList;
        this.mContext = context;
        this.cateogryClick = cateogryClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
        view = header.inflate(R.layout.vocab_category, viewGroup, false);
        return new VocabCategoryViewHolder(view, cateogryClick);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                VocabCategoryViewHolder categoryViewHolder = (VocabCategoryViewHolder) viewHolder;
                categoryViewHolder.setItem(dataList.get(i), i);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }
}