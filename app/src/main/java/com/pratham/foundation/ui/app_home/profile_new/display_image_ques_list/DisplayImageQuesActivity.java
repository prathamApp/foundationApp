package com.pratham.foundation.ui.app_home.profile_new.display_image_ques_list;

import static com.pratham.foundation.utility.FC_Constants.GROUP_LOGIN;
import static com.pratham.foundation.utility.FC_Constants.GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.INDIVIDUAL_MODE;
import static com.pratham.foundation.utility.FC_Constants.QR_GROUP_MODE;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.app_home.profile_new.show_image_question.ShowImgQuestionActivity_;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_certificate_display)
public class DisplayImageQuesActivity extends BaseActivity implements
        ImageQuesContract.ImageQuesView,
        ImageQuesContract.ImageQuesItemClicked{

    @Bean(ImageQuesPresenter.class)
    ImageQuesContract.ImageQuesPresenter presenter;

    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.certificate_recycler_view)
    RecyclerView recycler_view;
    @ViewById(R.id.tv_Activity)
    TextView tv_Activity;
    String sub_Name;
    ImageQuesAdapter certificateAdapter;
    List<Score> imgQuesMainList;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Configuration config = getResources().getConfiguration();
        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
        presenter.setView(DisplayImageQuesActivity.this);
        displayProfileName();
        displayProfileImage();
        presenter.showQuestion();
    }

    @UiThread
    @Override
    public void addToAdapter(List<Score> assessmentList) {
        imgQuesMainList = assessmentList;
        if(certificateAdapter==null) {
            certificateAdapter = new ImageQuesAdapter(this, imgQuesMainList,
                    DisplayImageQuesActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(this), true));
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(certificateAdapter);
        }else
            certificateAdapter.notifyDataSetChanged();
    }

    @ViewById(R.id.rl_no_data)
    RelativeLayout rl_no_data;
    @UiThread
    @Override
    public void showNoData() {
        recycler_view.setVisibility(View.GONE);
        rl_no_data.setVisibility(View.VISIBLE);
    }

    @Background
    public void displayProfileImage() {
        String sImage;
        try {
            if (!GROUP_LOGIN)
                sImage = AppDatabase.getDatabaseInstance(this).getStudentDao().getStudentAvatar(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"));
            else
                sImage = "group_icon";
        } catch (Exception e) {
            e.printStackTrace();
            sImage = "group_icon";
        }
        setStudentProfileImage(sImage);
    }

    @UiThread
    public void setStudentProfileImage(String sImage) {
    }

    @Background
    public void displayProfileName() {
        String profileName = "QR Group";
        try {
            if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(GROUP_MODE))
                profileName = AppDatabase.getDatabaseInstance(this).getGroupsDao().getGroupNameByGrpID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"));
            else if (!FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(QR_GROUP_MODE)) {
                profileName = AppDatabase.getDatabaseInstance(this).getStudentDao().getFullName(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"));
            }

            if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(INDIVIDUAL_MODE))
                profileName = profileName.split(" ")[0];

        } catch (Exception e) {
            e.printStackTrace();
        }
        setProfileName(profileName);
    }

    @Click(R.id.main_back)
    public void pressedBack(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @UiThread
    public void setProfileName(String profileName) {
//        tv_Activity.setText(profileName);
        tv_Topic.setText(profileName);
    }

    @Override
    public void gotoQuestions(Score scoreDisp) {
//        Toast.makeText(this, "gotoCertificate", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ShowImgQuestionActivity_.class);
        intent.putExtra("scoreDisp", scoreDisp);
        startActivity(intent);
    }
}