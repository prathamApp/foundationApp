package com.pratham.foundation.ui.contentPlayer.keywords_mapping;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Utility;

import java.util.ArrayList;
import java.util.List;

public class KeywordOptionAdapter extends RecyclerView.Adapter<KeywordOptionAdapter.Myviewholder> {
    Context context;
    List<ScienceQuestionChoice> datalist;
    List<ScienceQuestionChoice> selectedOption = new ArrayList();
    int maxSelect;
    boolean isClickable = true;
    private boolean showAnswer=false;
    KeywordMappingContract.KeywordMappingPresenter presenter;

    public KeywordOptionAdapter(Context context, List<ScienceQuestionChoice> datalist, int maxSelect,KeywordMappingContract.KeywordMappingPresenter presenter) {
        this.context = context;
        this.datalist = datalist;
        this.maxSelect = maxSelect;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_keyword, viewGroup, false);
        return new Myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Myviewholder myviewholder, int position) {
        int pos = position + 1;
        int topMargin = pxFromDp(myviewholder.textView.getContext(), -30);
        int leftMargin = pxFromDp(myviewholder.textView.getContext(), 45); //3 times of 17
        GridLayoutManager.LayoutParams param = (GridLayoutManager.LayoutParams) myviewholder.textView.getLayoutParams();
        if (pos <= 2) {
            param.setMargins(0, 0, 0, 0);
        } else if (pos % 3 == 0) {
            param.setMargins((leftMargin * 2), topMargin, 0, 0);
        } else {
            param.setMargins(0, topMargin, 0, 0);
        }
        myviewholder.textView.setLayoutParams(param);
        myviewholder.textView.setText(datalist.get(position).getSubQues());

        myviewholder.textView.setPaintFlags(  myviewholder.textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
       // myviewholder.textView.setTextColor(Color.WHITE);
        if (datalist.get(myviewholder.getAdapterPosition()).isIsclicked()) {
            myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.hexagon_yellow));
             myviewholder.textView.setTextColor(Color.WHITE);
            selectedOption.add(datalist.get(myviewholder.getAdapterPosition()));
        } else {
            myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.hexagon));
            myviewholder.textView.setTextColor(context.getResources().getColor(R.color.colorText));
        }


        myviewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickable) {
                    if (datalist.get(myviewholder.getAdapterPosition()).isIsclicked()) {
                        selectedOption.remove(datalist.get(myviewholder.getAdapterPosition()));
                        datalist.get(myviewholder.getAdapterPosition()).setIsclicked(false);
                        myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.hexagon));
                    } else {
                        if (!selectedOption.contains(datalist.get(myviewholder.getAdapterPosition()))) {
                            datalist.get(myviewholder.getAdapterPosition()).setStartTime(FC_Utility.getCurrentDateTime());
                            datalist.get(myviewholder.getAdapterPosition()).setEndTime(FC_Utility.getCurrentDateTime());
                            selectedOption.add(datalist.get(myviewholder.getAdapterPosition()));
                        }
                        datalist.get(myviewholder.getAdapterPosition()).setIsclicked(true);
                        myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.hexagon_yellow));
                    }
                }
            }
        });
        //SHOW ANSWER
        if (!FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
            if(!isClickable && !showAnswer){
           /* if (!presenter.checkAnswerNew(datalist,  myviewholder.textView.getText().toString())) {
                myviewholder.textView.setTextColor(Color.RED);
                myviewholder.textView.setPaintFlags(myviewholder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                myviewholder.textView.setPaintFlags(  myviewholder.textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            }*/

//                if (!presenter.checkAnswerNew(datalist,  myviewholder.textView.getText().toString())) {
                if (!datalist.get(position).getCorrectAnswer().equalsIgnoreCase("true")) {
                    if(datalist.get(myviewholder.getAdapterPosition()).isIsclicked()){
                        myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.hexagon_red));
                    }else {
                        myviewholder.textView.setTextColor(context.getResources().getColor(R.color.red));
                    }
                    myviewholder.textView.setPaintFlags(myviewholder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    myviewholder.textView.setPaintFlags(  myviewholder.textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    if(datalist.get(myviewholder.getAdapterPosition()).isIsclicked()){
                        myviewholder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.hexagon_green));
                    }else {
                        myviewholder.textView.setTextColor(context.getResources().getColor(R.color.colorSignBoard));
                    }
                }
            }
            //SHOW HINT
            if(!isClickable && showAnswer){
//                int itemPos = myviewholder.getAdapterPosition();
//                String myWord = ""+myviewholder.textView.getText().toString().trim();
//                Log.d("NARUTOUZUMAKI", itemPos+" : "+myWord+".");
                if (!datalist.get(position).getCorrectAnswer().equalsIgnoreCase("true")) {
                    myviewholder.textView.setTextColor(context.getResources().getColor(R.color.red));
                    myviewholder.textView.setPaintFlags(myviewholder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    myviewholder.textView.setPaintFlags(  myviewholder.textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }
/*                if (!presenter.checkAnswerNew(datalist, myWord)) {
                    myviewholder.textView.setTextColor(Color.RED);
                    myviewholder.textView.setPaintFlags(myviewholder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    myviewholder.textView.setPaintFlags(  myviewholder.textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }*/
            }
        }
    }

    private int pxFromDp(final Context context, final float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class Myviewholder extends RecyclerView.ViewHolder {
        TextView textView;

        public Myviewholder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.keywordname);
        }
    }

    public List getSelectedOptionList() {
        return selectedOption;
    }
    public void clearSelectedOptionList(){
        selectedOption.clear();
    }
    public boolean isClickable() {
        return isClickable;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    public boolean isShowAnswer() {
        return showAnswer;
    }

    public void setShowAnswer(boolean showAnswer) {
        this.showAnswer = showAnswer;
    }
}
