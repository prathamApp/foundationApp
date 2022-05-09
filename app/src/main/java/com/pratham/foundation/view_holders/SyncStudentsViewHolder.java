package com.pratham.foundation.view_holders;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.StudentSyncDetails;
import com.pratham.foundation.ui.app_home.profile_new.students_synced_data.students_synced_fragment.SyncedStudentContract;

public class SyncStudentsViewHolder extends RecyclerView.ViewHolder {

    /**
     * Created common holder which is used for cards.
     * Folder and file type of cards have different views
     */

    TextView tv_name, tv_eid, tv_resources, tv_courses,btn_details,tv_attendance,tv_studId;
    MaterialCardView main_student_sync_item_card;
    SyncedStudentContract.StudentItemClicked itemClicked;


    public SyncStudentsViewHolder(View view, SyncedStudentContract.StudentItemClicked itemClicked) {
        super(view);
        tv_name = view.findViewById(R.id.tv_name);
        tv_eid = view.findViewById(R.id.tv_eid);
        tv_resources = view.findViewById(R.id.tv_resources);
        tv_courses = view.findViewById(R.id.tv_courses);
        tv_attendance = view.findViewById(R.id.tv_attendance);
        tv_studId = view.findViewById(R.id.tv_studId);
        main_student_sync_item_card = view.findViewById(R.id.main_student_sync_item_card);
        btn_details = view.findViewById(R.id.btn_details);
        this.itemClicked = itemClicked;
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void setSyncItem(StudentSyncDetails studentItem, int pos) {
        try {
            tv_name.setText("" + studentItem.getName());
            tv_eid.setText(ApplicationClass.getInstance().getResources()
                    .getString(R.string.enroll_id)+" : " + studentItem.getEId());
            tv_resources.setText(ApplicationClass.getInstance().getResources()
                    .getString(R.string.total_resources)+" : " + studentItem.getTotalResources());
            tv_courses.setText(ApplicationClass.getInstance().getResources()
                    .getString(R.string.total_courses)+" : " + studentItem.getTotalCourses());
            tv_attendance.setText(ApplicationClass.getInstance().getResources()
                    .getString(R.string.total_attendance)+" : " + studentItem.getTotalAttendance());
            tv_studId.setText(ApplicationClass.getInstance().getResources()
                    .getString(R.string.stud_id)+" : " + studentItem.getStudentID());

            btn_details.setOnClickListener(view -> itemClicked.showDetails(studentItem, pos));

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