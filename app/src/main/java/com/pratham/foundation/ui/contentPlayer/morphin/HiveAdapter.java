package com.pratham.foundation.ui.contentPlayer.morphin;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.hive.HiveDrawable;
import com.pratham.foundation.customView.hive.HiveLayoutManager;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;

import java.util.List;

/**
 * Created by zjchai on 16/9/10.
 */
public class HiveAdapter extends RecyclerView.Adapter<ImageViewHolder> {

    private List<ScienceQuestionChoice> datalist;
    private Context context;
    private boolean showanswer;
    private OptionRecyclerInterface optionRecyclerInterface;
    private String language;
    private boolean isSubmited=false;

    public void setSubmited(boolean submited) {
        isSubmited = submited;
    }

    public HiveAdapter(List<ScienceQuestionChoice> datalist, Context context, OptionRecyclerInterface optionRecyclerInterface) {
        this.datalist = datalist;
        this.context = context;
        this.optionRecyclerInterface = optionRecyclerInterface;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_img, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Bitmap bitmap;
        ScienceQuestionChoice scienceQuestionChoice = datalist.get(holder.getAdapterPosition());
        if(!isSubmited) {
            if (scienceQuestionChoice.getIsQuestion() != null && scienceQuestionChoice.getIsQuestion().equalsIgnoreCase("True")) {
                bitmap = BitmapCache.INSTANCE.getBitmap(R.drawable.yellow);
                holder.remove.setVisibility(View.INVISIBLE);
                if (showanswer) {
                    holder.play.setVisibility(View.INVISIBLE);
                } else {
                    holder.play.setVisibility(View.VISIBLE);
                }
            } else {
                bitmap = BitmapCache.INSTANCE.getBitmap(R.drawable.grey_img);
                if (showanswer) {
                    holder.remove.setVisibility(View.INVISIBLE);
                    holder.play.setVisibility(View.INVISIBLE);
                } else {
                    holder.remove.setVisibility(View.VISIBLE);
                    holder.play.setVisibility(View.VISIBLE);
                }
            }
            HiveDrawable drawable = new HiveDrawable(HiveLayoutManager.HORIZONTAL, bitmap);
            holder.imageView.setImageDrawable(drawable);
            holder.hindiText.setText(scienceQuestionChoice.getSubQues());
            holder.hindiText.setSelected(true);
            if (showanswer) {
                holder.englishText.setText(scienceQuestionChoice.getEnglish());
                holder.englishText.setSelected(true);
            } else {
                if (language.equalsIgnoreCase("English")) {
                    holder.englishText.setText(scienceQuestionChoice.getUserAns());
                    holder.englishText.setSelected(true);
                    holder.remove.setVisibility(View.GONE);
                } else {
                    holder.englishText.setText("");
                }
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    optionRecyclerInterface.onItemClick(datalist.get(holder.getAdapterPosition()), "setEnglishWord");
                }
            });
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    optionRecyclerInterface.onItemClick(datalist.get(holder.getAdapterPosition()), "remove");
                }
            });
        }else {
            holder.hindiText.setText(scienceQuestionChoice.getSubQues());
            holder.englishText.setText(scienceQuestionChoice.getUserAns());
            if (scienceQuestionChoice.isTrue()) {
                bitmap = BitmapCache.INSTANCE.getBitmap(R.drawable.greenimg);
                holder.remove.setVisibility(View.INVISIBLE);
            } else {
                bitmap = BitmapCache.INSTANCE.getBitmap(R.drawable.grey_img);
            }
            holder.remove.setVisibility(View.INVISIBLE);
            HiveDrawable drawable = new HiveDrawable(HiveLayoutManager.HORIZONTAL, bitmap);
            holder.imageView.setImageDrawable(drawable);
        }
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionRecyclerInterface.onItemClick(datalist.get(holder.getAdapterPosition()), "play_hive");
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public void setShowanswer(boolean showanswer) {
        this.showanswer = showanswer;
    }

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
    public void addData(ScienceQuestionChoice scienceQuestionChoice) {
        datalist.add(scienceQuestionChoice);
    }

    public void remove(ScienceQuestionChoice scienceQuestionChoice) {
        datalist.remove(scienceQuestionChoice);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
