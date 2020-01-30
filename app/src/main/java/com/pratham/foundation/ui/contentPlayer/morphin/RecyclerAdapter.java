package com.pratham.foundation.ui.contentPlayer.morphin;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<ListItem> {

    private List<ScienceQuestionChoice> datalist;
    private Context context;
    private OptionRecyclerInterface optionRecyclerInterface;
    //private boolean showanswer;
    private String language;
    private int rowIndex = -1;

    public RecyclerAdapter(List<ScienceQuestionChoice> datalist, Context context, OptionRecyclerInterface optionRecyclerInterface) {
        this.datalist = datalist;
        this.context = context;
        this.optionRecyclerInterface = optionRecyclerInterface;
    }

    @Override
    public ListItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ListItem(view);
    }

    @Override
    public void onBindViewHolder(ListItem holder, int position) {
        ScienceQuestionChoice scienceQuestionChoice = datalist.get(holder.getAdapterPosition());
        if (!language.equalsIgnoreCase("English")) {
            holder.hindiText.setText(scienceQuestionChoice.getSubQues());
        } else {
            holder.hindiText.setText(scienceQuestionChoice.getEnglish());
        }

        if (rowIndex == holder.getAdapterPosition()) {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.grad_layer_rounded));
        } else {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.border));
        }
        if(scienceQuestionChoice.isPlaying()){
            holder.sound.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_speaker_color));
        }else {
            holder.sound.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_speaker_black));
        }
         /*Bitmap bitmap;
       if (scienceQuestionChoice.getIsQuestion() != null && scienceQuestionChoice.getIsQuestion().equalsIgnoreCase("True")) {
            bitmap = BitmapCache.INSTANCE.getBitmap(R.drawable.yellow);
        } else {
            bitmap = BitmapCache.INSTANCE.getBitmap(R.drawable.grey_img);
        }
        HiveDrawable drawable = new HiveDrawable(HiveLayoutManager.HORIZONTAL, bitmap);
        holder.imageView.setImageDrawable(drawable);*/
       /* if(showanswer){
            holder.englishText.setText(scienceQuestionChoice.getEnglish());
        }else {
            holder.englishText.setText("");
        }*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (language.equalsIgnoreCase("English")) {
                    holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.grad_layer_rounded));
                    // datalist.get(holder.getAdapterPosition()).setIsclicked(true);
                    rowIndex = holder.getAdapterPosition();
                    optionRecyclerInterface.onItemClick(datalist.get(holder.getAdapterPosition()), "getEnglishWord");
                } else {
                    optionRecyclerInterface.onItemClick(datalist.get(holder.getAdapterPosition()), "add");
                }
            }
        });
        holder.sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ScienceQuestionChoice scienceQuestionChoice1 : datalist) {
                    if(datalist.get(holder.getAdapterPosition())==scienceQuestionChoice1){
                        scienceQuestionChoice1.setPlaying(true);
                    }else {
                        scienceQuestionChoice1.setPlaying(false);
                    }
                }
                optionRecyclerInterface.onItemClick(datalist.get(holder.getAdapterPosition()), "play");
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

   /* public void setShowanswer(boolean showanswer) {
        this.showanswer = showanswer;
    }
*/
    /* public void addData(Integer data,int index) {
        datalist.add(new ScienceQuestionChoice());
    }

    public void addData(Integer data) {
        resId.add(data);
    }

    public void remove(int index){
        resId.remove(index);
    }*/

    /*public void move(int r, int r2) {
        Integer id = resId.remove(r);
        resId.add(r2,id) ;
    }*/

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /*  public int getRowIndex() {
          return rowIndex;
      }
  */
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
}
