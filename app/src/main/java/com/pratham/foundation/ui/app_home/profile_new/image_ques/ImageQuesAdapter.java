package com.pratham.foundation.ui.app_home.profile_new.image_ques;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.database.domain.Score;

import java.util.List;


public class ImageQuesAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private int lastPos = -1;
    private List<Score> scoreList;
    ImageQuesContract.ImageQuesItemClicked ImageQuesItemClicked;

    public ImageQuesAdapter(Context mContext, List<Score> scoreList,
                            ImageQuesContract.ImageQuesItemClicked ImageQuesItemClicked) {
        this.mContext = mContext;
        this.scoreList = scoreList;
        this.ImageQuesItemClicked = ImageQuesItemClicked;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
        view = file.inflate(R.layout.layout_certificate_row, viewGroup, false);
        return new FileHolder(view);
/*        switch (viewtype) {
            case 1:
                LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
                view = file.inflate(R.layout.content_card, viewGroup, false);
                return new FileHolder(view);
            case 2:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.content_adaprer_item_folder_card, viewGroup, false);
                return new FolderHolder(view);
            default:
                return null;
        }*/
    }

    public class FileHolder extends RecyclerView.ViewHolder {

        TextView title, tv_timestamp, tv_view;
        RelativeLayout certi_root;

        public FileHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tv_assessment);
            tv_timestamp = view.findViewById(R.id.tv_timestamp);
            tv_view = view.findViewById(R.id.tv_view);
            certi_root = view.findViewById(R.id.certi_root);
        }
    }

    public class FolderHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView tv_progress;
        SimpleDraweeView itemImage;
        protected RelativeLayout rl_root;
        protected ProgressLayout progressLayout;
        MaterialCardView card_main;

        public FolderHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.tv_progress = view.findViewById(R.id.tv_progress);
            this.itemImage = view.findViewById(R.id.item_Image);
            this.rl_root = view.findViewById(R.id.rl_root);
            this.card_main = view.findViewById(R.id.card_main);
            progressLayout = view.findViewById(R.id.card_progressLayout);
        }
    }

    @Override
    public int getItemViewType(int position) {
//        if (contentViewList.get(position).getNodeType() != null) {
//            switch (contentViewList.get(position).getNodeType()) {
//                case "Resource":
//                    return 1;
//                default:
//                    return 2;
//            }
//        } else
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        final Score contentItem = scoreList.get(position);
        ImageQuesAdapter.FileHolder holder = (ImageQuesAdapter.FileHolder) viewHolder;

        String cLevel = "" + contentItem.getLevel();
        String cTitle = "" /*+ FC_Utility.getSubjectNameFromNum(contentItem.getQuestionIda())*/;

        if (cLevel.equalsIgnoreCase("0"))
            holder.title.setText(cTitle + " Level 1");
        else if (cLevel.equalsIgnoreCase("1"))
            holder.title.setText(cTitle + " Level 2");
        else if (cLevel.equalsIgnoreCase("2"))
            holder.title.setText(cTitle + " Level 3");
        else if (cLevel.equalsIgnoreCase("3"))
            holder.title.setText(cTitle + " Level 4");
        else if (cLevel.equalsIgnoreCase("4"))
            holder.title.setText(cTitle + " Level 5");

        holder.tv_timestamp.setText(contentItem.getEndDateTime());
        holder.certi_root.setOnClickListener(v -> ImageQuesItemClicked.gotoQuestions(scoreList.get(position)));
        setAnimations(holder.certi_root, position);
    }

    private void setAnimations(final View content_card_view, final int position) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.item_fall_down);
        animation.setDuration(500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*                if (position > lastPos) {*/
                content_card_view.setVisibility(View.VISIBLE);
                content_card_view.setAnimation(animation);
                lastPos = position;
//                }
            }
        }, (long) (20));

    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }
}