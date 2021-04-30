package com.pratham.foundation.ui.app_home.profile_new.display_image_ques_list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ImageJsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.pratham.foundation.utility.FC_Utility.getSectionName;
import static com.pratham.foundation.utility.FC_Utility.getSubjectNameFromNum;

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
        view = file.inflate(R.layout.layout_image_ques_row, viewGroup, false);
        return new FileHolder(view);
    }

    public class FileHolder extends RecyclerView.ViewHolder {

        TextView title, tv_timestamp, tv_section;
        RelativeLayout certi_root;

        public FileHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tv_res_title);
            tv_timestamp = view.findViewById(R.id.tv_timestamp);
            tv_section = view.findViewById(R.id.tv_section);
//            tv_view = view.findViewById(R.id.tv_view);
            certi_root = view.findViewById(R.id.certi_root);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        final Score contentItem = scoreList.get(position);
        ImageJsonObject imageJsonObject;
        try {
            JSONObject jsonObj = new JSONObject(contentItem.getResourceID());
            Gson gson = new Gson();
            imageJsonObject = gson.fromJson(jsonObj.toString(), ImageJsonObject.class);

            ImageQuesAdapter.FileHolder holder = (ImageQuesAdapter.FileHolder) viewHolder;

//            String cLevel = "" + contentItem.getLevel();
            String level = "L-" + contentItem.getLevel();
            String cTitle = imageJsonObject.getGameName();

//            if (cLevel.equalsIgnoreCase("1"))
//                level = "L-1";
//            else if (cLevel.equalsIgnoreCase("2"))
//                level = "L-2";
//            else if (cLevel.equalsIgnoreCase("3"))
//                level = "L-3";
//            else if (cLevel.equalsIgnoreCase("4"))
//                level = "L-4";
//            else if (cLevel.equalsIgnoreCase("5"))
//                level = "L-5";

            String section = getSubjectNameFromNum(contentItem.getQuestionId()) +
                    " " + level + " " + getSectionName(contentItem.getScoredMarks());

            holder.title.setText(cTitle);
            holder.tv_section.setText(section);
            holder.tv_timestamp.setText(contentItem.getEndDateTime());
            holder.certi_root.setOnClickListener(v -> ImageQuesItemClicked.gotoQuestions(scoreList.get(position)));
            setAnimations(holder.certi_root, position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAnimations(final View content_card_view, final int position) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.item_fall_down);
        animation.setDuration(500);

        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
        lastPos = position;
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }
}