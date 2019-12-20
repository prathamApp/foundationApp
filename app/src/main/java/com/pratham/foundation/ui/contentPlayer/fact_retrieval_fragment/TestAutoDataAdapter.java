package com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansTextView;

import java.util.HashSet;
import java.util.List;

/**
 * Created by flisar on 03.03.2017.
 */

public class TestAutoDataAdapter extends RecyclerView.Adapter<TestAutoDataAdapter.ViewHolder> {

    public List<String> datalist;
    private Context mContext;
    private ItemClickListener mClickListener;

    private HashSet<Integer> mSelected;



    private boolean showAnswerEnabled = false;

    public TestAutoDataAdapter(Context context, List datalist) {
        mContext = context;
        this.datalist = datalist;
        mSelected = new HashSet<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.textvew, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvText.setText(datalist.get(position));
        if (!showAnswerEnabled) {
            if (mSelected.contains(position))
                holder.tvText.setBackgroundColor(mContext.getResources().getColor(R.color.yellow_text_bg));
               // holder.tvText.setBackgroundColor(Color.GREEN);
            else
                holder.tvText.setBackgroundColor(Color.WHITE);
        }else {
            if (mSelected.contains(position))
                holder.tvText.setBackgroundColor(mContext.getResources().getColor(R.color.light_green));
            else
                holder.tvText.setBackgroundColor(Color.WHITE);
        }
    }
    public boolean isShowAnswerEnabled() {
        return showAnswerEnabled;
    }

    public void setShowAnswerEnabled(boolean showAnswerEnabled) {
        this.showAnswerEnabled = showAnswerEnabled;
    }
    @Override
    public int getItemCount() {
        return datalist.size();
    }

    // ----------------------
    // Selection
    // ----------------------

    public void toggleSelection(int pos) {
        if (mSelected.contains(pos))
            mSelected.remove(pos);
        else
            mSelected.add(pos);
        notifyItemChanged(pos);
    }

    public void select(int pos, boolean selected) {
        if (selected)
            mSelected.add(pos);
        else
            mSelected.remove(pos);
        notifyItemChanged(pos);
    }

    public void selectRange(int start, int end, boolean selected) {
        for (int i = start; i <= end; i++) {
            if (selected)
                mSelected.add(i);
            else
                mSelected.remove(i);
        }
        notifyItemRangeChanged(start, end - start + 1);
    }

    public void deselectAll() {
        // this is not beautiful...
        mSelected.clear();
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (int i = 0; i < datalist.size(); i++)
            mSelected.add(i);
        notifyDataSetChanged();
    }

    public int getCountSelected() {
        return mSelected.size();
    }

    public HashSet<Integer> getSelection() {
        return mSelected;
    }

    // ----------------------
    // Click Listener
    // ----------------------

    public void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);

        boolean onItemLongClick(View view, int position);
    }

    // ----------------------
    // ViewHolder
    // ----------------------

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public SansTextView tvText;

        public ViewHolder(View itemView) {
            super(itemView);
            tvText = (SansTextView) itemView.findViewById(R.id.text);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null)
                return mClickListener.onItemLongClick(view, getAdapterPosition());
            return false;
        }
    }
}
