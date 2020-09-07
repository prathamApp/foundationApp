package com.pratham.foundation.ui.contentPlayer.matchingPairGame;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.MatchThePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DragDropAdapter extends RecyclerView.Adapter<DragDropAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {
    List<MatchThePair> draggedList = new ArrayList<>();
    private List<MatchThePair> data;

    Context context;
    /* DragDropListener dragDropListener;
     QuestionTypeListener questionTypeListener;*/
    StartDragListener startDragListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        View rowView;

        public MyViewHolder(View itemView) {
            super(itemView);

            rowView = itemView;
            mTitle = itemView.findViewById(R.id.tv_text);
        }
    }

    public DragDropAdapter(List<MatchThePair> data, Context context, StartDragListener startDragListener) {
        this.data = data;
        this.context = context;
        /* questionTypeListener = scienceAdapter;*/
        this.startDragListener = startDragListener;
    }

    /*   public DragDropAdapter(ArrangeSequenceViewHolder arrangeSequenceViewHolder, List<ScienceQuestionChoice> data, Context context, ScienceAdapter scienceAdapter) {
           this.data = data;
           this.context = context;
           questionTypeListener = scienceAdapter;
           startDragListener = arrangeSequenceViewHolder;
       }
   */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_simple_text_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

/*
        if (FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI).equalsIgnoreCase("Gujarati")) {
            Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/muktavaani_gujarati.ttf");
            holder.mTitle.setTypeface(face);
            holder.mTitle.setText(data.get(position).getLangText());
        } else if (FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI).equalsIgnoreCase("Assamese")) {
            Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/lohit_oriya.ttf");
            holder.mTitle.setTypeface(face);
            holder.mTitle.setText(data.get(position).getLangText());
        } else if (FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI).equalsIgnoreCase("Odiya")) {
            Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/lohit_oriya.ttf");
            holder.mTitle.setTypeface(face);
            holder.mTitle.setText(data.get(position).getLangText());
        } else
*/
            holder.mTitle.setText(data.get(position).getLangText());

            holder.rowView.setTag(data.get(position).getLangText());
//        if(data.get(position))
        draggedList.clear();

        //For swipe
       /* holder.mTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() ==
                        MotionEvent.ACTION_DOWN) {
                    startDragListener.requestDrag(holder);
                }
                return false;
            }
        });*/

      /*  if (!Assessment_Constants.isShowcaseDisplayed)
            if (position == 0) {
                Assessment_Constants.isShowcaseDisplayed = true;
                new BubbleShowCaseBuilder((Activity) context)
                        .title("Note: ")
                        .description("swap to match the answer on the right to the word on the left")
                        .backgroundColor(ContextCompat.getColor(context, R.color.colorAccentDark))
                        .closeActionImage(ContextCompat.getDrawable(context, R.drawable.ic_close))
                        .targetView(holder.itemView).show();
            }*/

    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(data, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(data, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        draggedList = data;
        Log.d("sss", draggedList.toString());
        startDragListener.onItemDragged(draggedList);
//        dragDropListener.setList(draggedList, data.get(0).getQid());
//        questionTypeListener.setAnswer("", "", data.get(0).getQid(), draggedList);

    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
        myViewHolder.rowView.setBackground(context.getResources().getDrawable(R.drawable.ripple_rectangle_green_selected));
        myViewHolder.rowView.setElevation(8);
    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
//        myViewHolder.rowView.setBackground(context.getResources().getDrawable(R.drawable.ripple_rectangle_card));
    }
}


