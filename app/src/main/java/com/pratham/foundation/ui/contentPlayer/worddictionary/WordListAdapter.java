package com.pratham.foundation.ui.contentPlayer.worddictionary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.Modal_TotalDaysGroupsPlayed;
import com.pratham.foundation.modalclasses.WordDictionaryModel;
import com.pratham.foundation.ui.admin_panel.tab_usage.ContractOptions;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> {

    private List<WordDictionaryModel> word_char_list;
    private final RecyclerClicked contractOptions;
    Context context1;

    public WordListAdapter(Context context, List<WordDictionaryModel> word_char_list, RecyclerClicked contractOptions) {
        context1 = context;
        this.word_char_list = word_char_list;
        this.contractOptions = contractOptions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_word_list, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

       // Objects.requireNonNull(viewHolder.item_grp_card).setBackgroundResource(R.drawable.card_color_bg9);
        viewHolder.word_char.setText(word_char_list.get(viewHolder.getAdapterPosition()).getCh());


        viewHolder.itemView.setOnClickListener(v -> {
           contractOptions.OnItemClcick(viewHolder.getAdapterPosition(),word_char_list.get(viewHolder.getAdapterPosition()));
        });
    }

    @Override
    public int getItemCount() {
        return word_char_list.size();
    }

    public void updateItems(List<WordDictionaryModel> word_char_list) {
        this.word_char_list = word_char_list;
        notifyDataSetChanged();
    }

    public List<WordDictionaryModel> getItems() {
        return word_char_list;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.word_char)
        TextView word_char;

        @BindView(R.id.item_word_char)
        MaterialCardView item_word_char;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
