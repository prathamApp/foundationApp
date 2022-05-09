package com.pratham.foundation.view_holders;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.StudentSyncUsageModal;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

public class SyncDetailsViewHolder extends RecyclerView.ViewHolder {

    /**
     * Created common holder which is used for cards.
     * Folder and file type of cards have different views
     */

    TextView tv_name, tv_eid, tv_resources, tv_courses,tv_attendancecount;
    MaterialCardView main_student_sync_item_card;
    RelativeLayout rl_details;

    public SyncDetailsViewHolder(View view) {
        super(view);
        tv_name = view.findViewById(R.id.tv_name);
        tv_resources = view.findViewById(R.id.tv_resources);
        tv_courses = view.findViewById(R.id.tv_courses);
        tv_attendancecount = view.findViewById(R.id.tv_attendancecount);
        main_student_sync_item_card = view.findViewById(R.id.main_student_sync_item_card);
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void setSyncItem(StudentSyncUsageModal studentItem, int pos) {
        String a = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI);
        Log.d("INSTRUCTIONFRAG", "Select Subj: " + a);
        FC_Utility.setAppLocal(ApplicationClass.getInstance(), a);
        try {
            tv_name.setText("" + studentItem.getDate());
            tv_resources.setText(ApplicationClass.getInstance().getResources()
                    .getString(R.string.total_resources)+" : " + studentItem.getResources());
            tv_courses.setText(ApplicationClass.getInstance().getResources()
                    .getString(R.string.total_courses)+" : " + studentItem.getCourses());
            tv_attendancecount.setText(ApplicationClass.getInstance().getResources()
                    .getString(R.string.total_attendance)+" : " + studentItem.getAttendanceCount());
            setAnimations(main_student_sync_item_card);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAnimations(final View content_card_view) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(ApplicationClass.getInstance(), R.anim.item_fall_down);
        animation.setDuration(500);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
    }
}