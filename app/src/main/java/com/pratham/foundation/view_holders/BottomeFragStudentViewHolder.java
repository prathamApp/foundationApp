package com.pratham.foundation.view_holders;

import static com.pratham.foundation.ui.bottom_fragment.BottomStudentsFragment.groupClicked;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.card.MaterialCardView;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.database.domain.StudentAndGroup_BottomFragmentModal;
import com.pratham.foundation.ui.app_home.learning_fragment.attendance_bottom_fragment.AttendanceStudentsContract;
import com.pratham.foundation.ui.bottom_fragment.BottomStudentsContract;
import com.pratham.foundation.utility.FC_Utility;

import java.util.Objects;

public class BottomeFragStudentViewHolder extends RecyclerView.ViewHolder {

    /**
     * Created common holder which is used for cards.
     * Folder and file type of cards have different views
     */

    @Nullable
    TextView studentName, group_name,child_enroll_id;
    @Nullable
    SimpleDraweeView avatar, iv_grp;
    @Nullable
    RelativeLayout rl_card, rl_root;
    @Nullable
    MaterialCardView rl_child_attendance, group_card;


    private BottomStudentsContract.StudentClickListener contentClicked;
    private BottomStudentsContract.StudentClickListener contentClicked2;
    AttendanceStudentsContract.AttendanceStudentClickListener studentClickListener;
    
    public BottomeFragStudentViewHolder(View itemView, final BottomStudentsContract.StudentClickListener contentClicked) {
        super(itemView);
//        contentClicked interface initilized for click events
        studentName = itemView.findViewById(R.id.child_name);
        avatar = itemView.findViewById(R.id.iv_child);
        rl_card = itemView.findViewById(R.id.rl_card);
        child_enroll_id = itemView.findViewById(R.id.child_enroll_id);
        rl_child_attendance = itemView.findViewById(R.id.rl_child_attendance);
        this.contentClicked = contentClicked;
    }

    public BottomeFragStudentViewHolder(View view, BottomStudentsContract.StudentClickListener studentClickListener, boolean b) {
        super(view);
        group_name = itemView.findViewById(R.id.group_name);
        iv_grp = itemView.findViewById(R.id.iv_grp);
        rl_root = itemView.findViewById(R.id.rl_root);
        child_enroll_id = itemView.findViewById(R.id.child_enroll_id);
        group_card = itemView.findViewById(R.id.group_card);
        this.contentClicked2 = contentClicked;
    }

    public BottomeFragStudentViewHolder(View view, AttendanceStudentsContract.AttendanceStudentClickListener studentClickListener) {
        super(view);
        studentName = itemView.findViewById(R.id.child_name);
        avatar = itemView.findViewById(R.id.iv_child);
        rl_card = itemView.findViewById(R.id.rl_card);
        child_enroll_id = itemView.findViewById(R.id.child_enroll_id);
        rl_child_attendance = itemView.findViewById(R.id.rl_child_attendance);
        this.studentClickListener = studentClickListener;
    }

    @SuppressLint("CheckResult")
    public void setFragmentStudentItem(StudentAndGroup_BottomFragmentModal fragmentModalsList,
                                       BottomStudentsContract.StudentClickListener studentClickListener,
                                       int pos) {
        try {
            if(fragmentModalsList.getEnrollmentID() != null && !fragmentModalsList.getEnrollmentID().equalsIgnoreCase("PS")) {
//                AppDatabase.getDatabaseInstance().getStudentDao().getEnrollMentId(fragmentModalsList.getStudentID());
                Objects.requireNonNull(child_enroll_id).setText("Id: "+fragmentModalsList.getEnrollmentID());
            }else
                Objects.requireNonNull(child_enroll_id).setText("Id: "+fragmentModalsList.getStudentID());
            Objects.requireNonNull(child_enroll_id).setSelected(true);
            Objects.requireNonNull(studentName).setText(fragmentModalsList.getFullName());
//            Objects.requireNonNull(studentName).setSelected(true);
            if (groupClicked) {
                if (fragmentModalsList.getGender().equalsIgnoreCase("male")) {
                    Objects.requireNonNull(avatar).setImageResource(FC_Utility.getRandomMaleAvatar(ApplicationClass.getInstance()));
                } else
                    Objects.requireNonNull(avatar).setImageResource(FC_Utility.getRandomFemaleAvatar(ApplicationClass.getInstance()));
            } else if (fragmentModalsList.getAvatarName() != null)
                switch (fragmentModalsList.getAvatarName()) {
                    case "b1.png":
                        Objects.requireNonNull(avatar).setImageResource(R.drawable.b1);
                        break;
                    case "b2.png":
                        Objects.requireNonNull(avatar).setImageResource(R.drawable.b2);
                        break;
                    case "b3.png":
                        Objects.requireNonNull(avatar).setImageResource(R.drawable.b3);
                        break;
                    case "g1.png":
                        Objects.requireNonNull(avatar).setImageResource(R.drawable.g1);
                        break;
                    case "g2.png":
                        Objects.requireNonNull(avatar).setImageResource(R.drawable.g2);
                        break;
                    case "g3.png":
                        Objects.requireNonNull(avatar).setImageResource(R.drawable.g3);
                        break;
                }
            else
                Objects.requireNonNull(avatar).setImageResource(R.drawable.b2);

            if (fragmentModalsList.isChecked()) {
                rl_card.setBackground(ApplicationClass.getInstance().getResources().getDrawable(R.drawable.card_color_bg1));
                studentName.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.white));
                child_enroll_id.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.white));
            } else {
                rl_card.setBackground(ApplicationClass.getInstance().getResources().getDrawable(R.drawable.card_color_bg6));
                studentName.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.dark_blue));
                child_enroll_id.setTextColor(ApplicationClass.getInstance().getResources().getColor(R.color.dark_blue));
            }

/*
        File file;
        file = new File(ApplicationClass.contentSDPath +
                "" + App_Thumbs_Path + fragmentModalsList.getAvatarName());
        if(!file.exists())
            file = new File(ApplicationClass.foundationPath +
                "" + App_Thumbs_Path + fragmentModalsList.getAvatarName());
        if(file.exists())
            Objects.requireNonNull(avatar).setImageURI(Uri.fromFile(file));
*/
            Objects.requireNonNull(rl_card).setOnClickListener(v -> studentClickListener.onStudentClick(fragmentModalsList, pos));
            setSlideAnimations(Objects.requireNonNull(rl_child_attendance));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("CheckResult")
    public void setFragmentGroupItem(StudentAndGroup_BottomFragmentModal fragmentModalsList,
                                     BottomStudentsContract.StudentClickListener studentClickListener) {

        try {
            Objects.requireNonNull(child_enroll_id).setText("Id: "+fragmentModalsList.getEnrollmentID());
            Objects.requireNonNull(child_enroll_id).setSelected(true);
            Objects.requireNonNull(group_name).setText(fragmentModalsList.getFullName());
            Objects.requireNonNull(group_name).setSelected(true);
            Objects.requireNonNull(iv_grp).setImageResource(R.drawable.ic_grp_btn);
            setSlideAnimations(Objects.requireNonNull(group_card));
/*
        File file;
        file = new File(ApplicationClass.contentSDPath +
                "" + App_Thumbs_Path + fragmentModalsList.getAvatarName());
        if(!file.exists())
            file = new File(ApplicationClass.foundationPath +
                "" + App_Thumbs_Path + fragmentModalsList.getAvatarName());
        if(file.exists())
            Objects.requireNonNull(avatar).setImageURI(Uri.fromFile(file));
*/

            Objects.requireNonNull(rl_root).setOnClickListener(v ->
                    studentClickListener.onGroupClick(fragmentModalsList.getFullName(),
                    fragmentModalsList.getStudentID(),fragmentModalsList.getGroupId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
//
//        setSlideAnimations(Objects.requireNonNull(rl_child_attendance));
    }

    private void setSlideAnimations(final View content_card_view) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(ApplicationClass.getInstance(), R.anim.slide_list);
        animation.setDuration(300);
        content_card_view.setVisibility(View.VISIBLE);
        content_card_view.setAnimation(animation);
    }

    public void setAttendenceFragmentStudentItem(Student student, AttendanceStudentsContract.AttendanceStudentClickListener studentClickListener, int position) {
        try {
            Objects.requireNonNull(child_enroll_id).setText("Id: "+student.getStudentID());
            Objects.requireNonNull(child_enroll_id).setSelected(true);
            Objects.requireNonNull(studentName).setText(student.getFullName());
            Objects.requireNonNull(studentName).setSelected(true);
            if (student.getGender().equalsIgnoreCase("male")) {
                Objects.requireNonNull(avatar).setImageResource(FC_Utility.getRandomMaleAvatar(ApplicationClass.getInstance()));
            } else
                Objects.requireNonNull(avatar).setImageResource(FC_Utility.getRandomFemaleAvatar(ApplicationClass.getInstance()));
            Objects.requireNonNull(rl_card).setOnClickListener(v -> studentClickListener.onStudentClick(student, position));
            setSlideAnimations(Objects.requireNonNull(rl_child_attendance));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}