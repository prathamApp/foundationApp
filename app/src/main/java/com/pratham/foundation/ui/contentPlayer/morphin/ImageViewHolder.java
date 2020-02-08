package com.pratham.foundation.ui.contentPlayer.morphin;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.hive.HiveDrawable;
import com.pratham.foundation.customView.hive.HiveLayoutManager;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;

/**
 * Created by zjchai on 16/9/10.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView,remove,play;
    TextView englishText, hindiText;

    public ImageViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.img_img);
        englishText = (TextView) itemView.findViewById(R.id.englishText);
        hindiText = (TextView) itemView.findViewById(R.id.hindiText);
        remove = (ImageView) itemView.findViewById(R.id.remove);
        play = (ImageView) itemView.findViewById(R.id.play);
    }
}
