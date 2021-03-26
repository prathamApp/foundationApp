package com.pratham.foundation.ui.app_home.profile_new.course_enrollment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.Model_CourseEnrollment;
import com.pratham.foundation.view_holders.CourseViewHolder;

import java.util.List;


public class EnrolledCoursesAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Model_CourseEnrollment> contentViewList;

    public EnrolledCoursesAdapter(Context mContext, List<Model_CourseEnrollment> contentViewList) {
        this.mContext = mContext;
        this.contentViewList = contentViewList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        // select view based on item type
        View view;
        LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
        view = file.inflate(R.layout.layout_image_ques_row, viewGroup, false);
        return new CourseViewHolder(view/*, contentClicked*/);
//        switch (viewtype) {
//            case 1:
//                LayoutInflater file = LayoutInflater.from(viewGroup.getContext());
//                view = file.inflate(R.layout.content_card, viewGroup, false);
//                return new ContentFileViewHolder(view, contentClicked);
//            case 2:
//                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
//                view = folder.inflate(R.layout.content_adaprer_item_folder_card, viewGroup, false);
//                return new ContentFolderViewHolder(view, contentClicked);
//            case 3:
//                LayoutInflater assessLI = LayoutInflater.from(viewGroup.getContext());
//                view = assessLI.inflate(R.layout.content_adaprer_item_folder_card, viewGroup, false);
//                return new ContentTestViewHolder(view, contentClicked);
//            default:
//                return null;
//        }
    }

    @Override
    public int getItemViewType(int position) {
        if (contentViewList.get(position).getCoachVerified()==0/*isCoachVerified()*//* != null*/) {
            return 1;
/*            switch (contentViewList.get(position).getCourse_status()) {
                case TYPE_ITEM:
                    return 1;
                case TYPE_ASSESSMENT:
                    return 3;
                default:
                    return 2;
            }*/
        } else
            return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        final Model_CourseEnrollment contentList = contentViewList.get(position);
        if (viewHolder.getItemViewType() == 1) {
            CourseViewHolder courseViewHolder = (CourseViewHolder) viewHolder;
//                set view holder for file type item
            courseViewHolder.setData(contentList, position);
        }
    }

    @Override
    public int getItemCount() {
        return contentViewList.size();
    }
}