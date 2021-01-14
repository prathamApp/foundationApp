package com.pratham.foundation.view_holders;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.Model_CourseEnrollment;

import java.util.Objects;

public class CourseViewHolder extends RecyclerView.ViewHolder {

    /**
     * Created common holder which is used for cards.
     * Folder and file type of cards have different views
     */

    @Nullable
    TextView title, tv_timestamp, tv_section;
    @Nullable
    RelativeLayout certi_root;
    @Nullable
    LottieAnimationView tv_view;

    public CourseViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.tv_res_title);
        tv_timestamp = itemView.findViewById(R.id.tv_timestamp);
        tv_section = itemView.findViewById(R.id.tv_section);
        certi_root = itemView.findViewById(R.id.certi_root);
        tv_view = itemView.findViewById(R.id.tv_view);
    }

    private String parseDate(String date) {
        String[] date_split = date.split(" ");
        return date_split[1] + ", " + date_split[2] + " " + date_split[3] + " " + date_split[6];
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void setData(Model_CourseEnrollment contentList, int position) {

//        add card and its click listners
        Objects.requireNonNull(title).setText(contentList.getCourseDetail().getSubject()+" -> "+contentList.getCourseDetail().getNodeTitle());
        title.setSelected(true);
        Objects.requireNonNull(tv_section).setVisibility(View.VISIBLE);
        String time = parseDate(contentList.getPlanFromDate()) + " - " +parseDate(contentList.getPlanToDate());
        Log.d("CourseViewHolder", "setData: "+time);
        Objects.requireNonNull(tv_section).setText(time);
        tv_section.setSelected(true);
        Objects.requireNonNull(tv_timestamp).setVisibility(View.VISIBLE);
        Objects.requireNonNull(tv_timestamp).setText("Language : " + contentList.getCourseDetail().getContentLanguage());
        tv_view.setAnimation("timer_time.json");
        tv_view.playAnimation();

        setSlideAnimations(Objects.requireNonNull(certi_root));
    }

    private void setAnimations(final View content_card_view) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(ApplicationClass.getInstance(), R.anim.item_fall_down);
        animation.setDuration(300);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
    }

    private void setSlideAnimations(final View content_card_view) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(ApplicationClass.getInstance(), R.anim.slide_list);
        animation.setDuration(300);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
    }
}