package com.pratham.foundation.ui.contentPlayer.morphin;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;

/**
 * Created by zjchai on 16/9/10.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView,remove,play;
    TextView englishText, hindiText;

    public ImageViewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.img_img);
        englishText = itemView.findViewById(R.id.englishText);
        hindiText = itemView.findViewById(R.id.hindiText);
        remove = itemView.findViewById(R.id.remove);
        play = itemView.findViewById(R.id.play);
    }
}
