package com.pratham.foundation.ui.contentPlayer.pictionary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GifView;
import com.pratham.foundation.customView.SansTextView;
import com.pratham.foundation.modalclasses.ScienceQuestionChoice;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class resultAdapter extends RecyclerView.Adapter {
    List<ScienceQuestionChoice> optionList;
    Context context;
    String path;
    int IMAGE = 0;
    int TEXT = 1;

    public resultAdapter(List<ScienceQuestionChoice> optionList, Context context,String path) {
        this.optionList = optionList;
        this.context = context;
        this.path = path;
    }

    @Override
    public int getItemViewType(int position) {
        if (optionList.get(position).getSubUrl().trim().equalsIgnoreCase("")) {
            return TEXT;
        } else {
            return IMAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (i == IMAGE) {
            view = LayoutInflater.from(context).inflate(R.layout.result_image, viewGroup, false);
            return new MyviewHolderImage(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.result_text, viewGroup, false);
            return new MyviewHolderText(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (optionList.get(position).getSubUrl().trim().equalsIgnoreCase("")) {
            MyviewHolderText textViewHolder = (MyviewHolderText) viewHolder;
            textViewHolder.textView.setText(optionList.get(position).getSubQues());
        } else {
            MyviewHolderImage imageViewHolder = (MyviewHolderImage) viewHolder;
            setImage(imageViewHolder.imageView,optionList.get(position).getSubUrl(),path + optionList.get(position).getSubUrl());
        }

    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }

    public class MyviewHolderImage extends RecyclerView.ViewHolder {
        ImageView imageView;
        public MyviewHolderImage(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageview);
        }
    }

    public class MyviewHolderText extends RecyclerView.ViewHolder {
        SansTextView textView;

        public MyviewHolderText(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }
    }
    private void setImage(View view, final String choiceurl, String placeholderTemp) {
//        if (AssessmentApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
        String path = choiceurl.replace(" ","");
        String placeholder = placeholderTemp.replace(" ","");
        String[] imgPath = path.split("\\.");
        int len;
        if (imgPath.length > 0)
            len = imgPath.length - 1;
        else len = 0;


        if (imgPath[len].equalsIgnoreCase("gif")) {

            try {
                GifView gifView = (GifView) view;
                InputStream gif = new FileInputStream(placeholder);
                gifView.setGifResource(gif);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    /*        Glide.with(getActivity()).asGif()
                    .load(path)
                    .apply(new RequestOptions()
                            .placeholder(Drawable.createFromPath(placeholder)))
                    .into(imageView);*/
        } else {
            ImageView imageView = (ImageView) view;
            Glide.with(context)
                    .load(path)
                    .apply(new RequestOptions()
                            .placeholder(Drawable.createFromPath(placeholder)))
                    .into(imageView);
        }


//        }
    }
}
