package com.pratham.foundation.view_holders;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.modalclasses.SyncLogDataModal;

import org.json.JSONException;
import org.json.JSONObject;

public class SyncLogViewHolder extends RecyclerView.ViewHolder {

    /**
     * Created common holder which is used for cards.
     * Folder and file type of cards have different views
     */

    TextView stat_date, tv_data, tv_media, tv_courses, tv_syncstyle, tv_scorecount, tv_mediacount, tv_coursecount;
    MaterialCardView log_card_main;
    LinearLayout ll_data, ll_all_dbstats;
    RelativeLayout rl_media;


    public SyncLogViewHolder(View view) {
        super(view);
        stat_date = view.findViewById(R.id.stat_date);
        tv_data = view.findViewById(R.id.tv_data);
        tv_media = view.findViewById(R.id.tv_media);
        tv_courses = view.findViewById(R.id.tv_courses);
        tv_syncstyle = view.findViewById(R.id.tv_syncstyle);
        log_card_main = view.findViewById(R.id.log_card_main);
        ll_data = view.findViewById(R.id.ll_data);
        tv_scorecount = view.findViewById(R.id.tv_scorecount);
        tv_mediacount = view.findViewById(R.id.tv_mediacount);
        tv_coursecount = view.findViewById(R.id.tv_coursecount);
        rl_media = view.findViewById(R.id.rl_media);
    }

    public SyncLogViewHolder(View view, int a) {
        super(view);
        stat_date = view.findViewById(R.id.stat_date);
        tv_scorecount = view.findViewById(R.id.tv_scorecount);
        tv_mediacount = view.findViewById(R.id.tv_mediacount);
        tv_coursecount = view.findViewById(R.id.tv_coursecount);
        tv_syncstyle = view.findViewById(R.id.tv_syncstyle);
        log_card_main = view.findViewById(R.id.log_card_main);
        rl_media = view.findViewById(R.id.rl_media);
    }

    public SyncLogViewHolder(View view, String b) {
        super(view);
        stat_date = view.findViewById(R.id.stat_date);
        tv_data = view.findViewById(R.id.tv_data);
        tv_media = view.findViewById(R.id.tv_media);
        tv_courses = view.findViewById(R.id.tv_courses);
        tv_syncstyle = view.findViewById(R.id.tv_syncstyle);
        log_card_main = view.findViewById(R.id.log_card_main);
        ll_data = view.findViewById(R.id.ll_data);
        tv_scorecount = view.findViewById(R.id.tv_scorecount);
        tv_mediacount = view.findViewById(R.id.tv_mediacount);
        tv_coursecount = view.findViewById(R.id.tv_coursecount);
        rl_media = view.findViewById(R.id.rl_media);
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void setSyncItem(Modal_Log contentItem, int position) {
        SyncLogDataModal logDataModal;
        stat_date.setText("Date: " + contentItem.getCurrentDateTime());

        if (contentItem.getErrorType().equalsIgnoreCase("successfully_pushed")) {
            stat_date.setBackground(ApplicationClass.getInstance().getResources()
                    .getDrawable(R.drawable.text_usage_game_grad));
            stat_date.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorSignBoard2));
            tv_syncstyle.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorSignBoard2));
        } else {
            stat_date.setBackground(ApplicationClass.getInstance().getResources()
                    .getDrawable(R.drawable.sync_failed_bg));
            stat_date.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorRedSignBoard));
            tv_syncstyle.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorRedSignBoard));
        }

        try {
            JSONObject jsonObj = new JSONObject(contentItem.getLogDetail());
            Gson gson = new Gson();
            logDataModal = gson.fromJson(jsonObj.toString(), SyncLogDataModal.class);
            if (logDataModal == null) {
                tv_data.setText("Data : 0");
                tv_media.setText("Media : 0");
                tv_courses.setText("Courses : 0");
                tv_mediacount.setText("0/0");
                tv_coursecount.setText("0/0");
                tv_scorecount.setText("0/0");
            } else {
                tv_data.setText("Data : " + logDataModal.getSync_data_length());
                tv_media.setText("Media : " + logDataModal.getSync_media_length());
                tv_courses.setText("Courses : " + logDataModal.getSync_course_enrollment_length());
                tv_mediacount.setText("" + logDataModal.getMediaCount());
                tv_coursecount.setText("" + logDataModal.getCoursesCount());
                tv_scorecount.setText("" + logDataModal.getScoreTable());
            }
            setAnimations(log_card_main, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void setAutoSyncItem(Modal_Log contentItem, int position) {
        try {
            SyncLogDataModal logDataModal;
            stat_date.setText("Date: " + contentItem.getCurrentDateTime());

            if (contentItem.getErrorType().equalsIgnoreCase("successfully_pushed")) {
                stat_date.setBackground(ApplicationClass.getInstance().getResources()
                        .getDrawable(R.drawable.text_usage_game_grad));
                stat_date.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorSignBoard2));
                tv_syncstyle.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorSignBoard2));
            } else {
                stat_date.setBackground(ApplicationClass.getInstance().getResources()
                        .getDrawable(R.drawable.sync_failed_bg));
                stat_date.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorRedSignBoard));
                tv_syncstyle.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorRedSignBoard));
            }
            try {
                JSONObject jsonObj = new JSONObject(contentItem.getLogDetail());
                Gson gson = new Gson();
                logDataModal = gson.fromJson(jsonObj.toString(), SyncLogDataModal.class);
                if (logDataModal == null) {
                    tv_data.setText("Data : 0");
                    tv_media.setText("Media : 0");
                    tv_courses.setText("Courses : 0");
                    tv_mediacount.setText("0/0");
                    tv_coursecount.setText("0/0");
                    tv_scorecount.setText("0/0");
                } else {
                    tv_data.setText("Data : " + logDataModal.getSync_data_length());
                    tv_media.setText("Media : " + logDataModal.getSync_media_length());
                    tv_courses.setText("Courses : " + logDataModal.getSync_course_enrollment_length());
                    tv_mediacount.setText("" + logDataModal.getMediaCount());
                    tv_coursecount.setText("" + logDataModal.getCoursesCount());
                    tv_scorecount.setText("" + logDataModal.getScoreTable());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            setAnimations(log_card_main, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void setDBSyncItem(Modal_Log contentItem, int position) {
        SyncLogDataModal logDataModal;
        stat_date.setText("Date: " + contentItem.getCurrentDateTime());

        if (contentItem.getErrorType() != null && contentItem.getErrorType().contains("failed")) {
            stat_date.setBackground(ApplicationClass.getInstance().getResources()
                    .getDrawable(R.drawable.sync_failed_bg));
            stat_date.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorRedSignBoard));
            tv_syncstyle.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorRedSignBoard));
        } else {
            Log.v("DB Push", "DB Push: " + contentItem.getCurrentDateTime());
            stat_date.setBackground(ApplicationClass.getInstance().getResources()
                    .getDrawable(R.drawable.text_usage_game_grad));
            stat_date.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorSignBoard2));
            tv_syncstyle.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.colorSignBoard2));
        }
//        tv_syncstyle.setText(contentItem.getErrorType());
        try {
            JSONObject jsonObj = new JSONObject(contentItem.getLogDetail());
            Gson gson = new Gson();
            logDataModal = gson.fromJson(jsonObj.toString(), SyncLogDataModal.class);
            if (logDataModal == null) {
                tv_coursecount.setText("0/0");
                tv_scorecount.setText("0/0");
            } else {
                tv_coursecount.setText("" + logDataModal.getCoursesCount());
                tv_scorecount.setText("" + logDataModal.getScoreTable());
            }
            setAnimations(log_card_main, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAnimations(final View content_card_view, final int position) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(ApplicationClass.getInstance(), R.anim.item_fall_down);
        animation.setDuration(500);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
    }


}