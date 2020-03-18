package com.pratham.foundation.ui.admin_panel.group_selection.fragment_child_attendance;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.Student;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pratham.foundation.utility.FC_Constants.StudentPhotoPath;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildHolder> {
    private ArrayList<Student> datalist;
    private ArrayList<Integer> male_avatar;
    private ArrayList<Integer> female_avatar;
    private Context context;
    int finalPos;
    private ContractChildAttendance.attendanceView attendanceView;

    public ChildAdapter(Context context, ArrayList<Student> datalist,
                        ArrayList<Integer> female_avatar, ArrayList<Integer> male_avatar, ContractChildAttendance.attendanceView attendanceView) {
        this.context = context;
        this.datalist = datalist;
        this.female_avatar = female_avatar;
        this.male_avatar = male_avatar;
        this.attendanceView = attendanceView;
    }

    @NonNull
    @Override
    public ChildHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = null;
        v = inflater.inflate(R.layout.item_child_attendance, parent, false);
        return new ChildHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChildHolder viewHolder, int pos) {
        // pos = viewHolder.getAdapterPosition();
        viewHolder.child_name.setText(datalist.get(pos).getFullName());
        datalist.get(pos).getStudentID();
        File file;
        file = new File(StudentPhotoPath + "" + datalist.get(pos).getStudentID() + ".jpg");
        Fresco.getImagePipeline().clearCaches();
//        Fresco.getImagePipeline().clearDiskCaches();
//        Fresco.getImagePipeline().clearMemoryCaches();
        if (file.exists()) {
            viewHolder.child_avatar.setImageURI(Uri.fromFile(file));
        } else {
            if (datalist.get(pos).getGender().equalsIgnoreCase("male"))
                viewHolder.child_avatar.setImageResource(male_avatar.get(pos));
            else
                viewHolder.child_avatar.setImageResource(female_avatar.get(pos));
        }
        viewHolder.itemView.setOnClickListener(v -> attendanceView.
                childItemClicked(datalist.get(viewHolder.getAdapterPosition()),
                        viewHolder.getAdapterPosition()));
        viewHolder.iv_camera.setOnClickListener(v -> attendanceView.
                clickPhoto(datalist.get(pos).getStudentID(), pos));
        if (datalist.get(viewHolder.getAdapterPosition()).isChecked()) {
            viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.card_color_bg1));
            viewHolder.child_name.setTextColor(context.getResources().getColor(R.color.white));
            viewHolder.iv_camera.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_photo_camera_black));
        } else {
            viewHolder.iv_camera.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_photo_camera));
            viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.card_color_bg6));
            viewHolder.child_name.setTextColor(context.getResources().getColor(R.color.dark_blue));
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class ChildHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.child_name)
        TextView child_name;
        @BindView(R.id.iv_child)
        SimpleDraweeView child_avatar;
        @BindView(R.id.iv_camera)
        ImageView iv_camera;
        @BindView(R.id.rl_child_attendance)
        MaterialCardView main_layout;

        public ChildHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
