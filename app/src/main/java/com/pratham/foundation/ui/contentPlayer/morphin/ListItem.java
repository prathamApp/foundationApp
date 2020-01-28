package com.pratham.foundation.ui.contentPlayer.morphin;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.pratham.foundation.R;

public class ListItem extends RecyclerView.ViewHolder {
    ImageView sound, remove;
    TextView englishText, hindiText;

    public ListItem(View itemView) {
        super(itemView);
        sound = (ImageView) itemView.findViewById(R.id.sound);
       // englishText = (TextView) itemView.findViewById(R.id.englishText);
        hindiText = (TextView) itemView.findViewById(R.id.hindiText);
       // remove = (ImageView) itemView.findViewById(R.id.remove);
    }
}

