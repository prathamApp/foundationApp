package com.pratham.foundation.ui.home_screen.profile_new.certificate_display;

import android.content.Intent;
import android.content.res.Configuration;
import android.widget.TextView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.ui.student_profile.Student_profile_activity;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import static com.pratham.foundation.utility.FC_Constants.GROUP_LOGIN;
import static com.pratham.foundation.utility.FC_Constants.GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.INDIVIDUAL_MODE;
import static com.pratham.foundation.utility.FC_Constants.LOGIN_MODE;
import static com.pratham.foundation.utility.FC_Constants.QR_GROUP_MODE;

@EActivity(R.layout.activity_certificate_display)
public class CertificateDisplayActivity extends BaseActivity implements CertificateDisplayContract.CertificateView, CertificateDisplayContract.CertificateItemClicked{

    @Bean(CertificateDisplayPresenter.class)
    CertificateDisplayContract.CertificatePresenter presenter;

    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.tv_Activity)
    TextView tv_Activity;
    String sub_Name;

    @AfterViews
    public void initialize() {
        Configuration config = getResources().getConfiguration();
        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
        sub_Name = getIntent().getStringExtra("nodeTitle");
        presenter.setView(CertificateDisplayActivity.this);
        displayProfileName();
        displayProfileImage();
    }


    @Background
    public void displayProfileImage() {
        String sImage;
        try {
            if (!GROUP_LOGIN)
                sImage = AppDatabase.getDatabaseInstance(this).getStudentDao().getStudentAvatar(FC_Constants.currentStudentID);
            else
                sImage = "group_icon";
        } catch (Exception e) {
            e.printStackTrace();
            sImage = "group_icon";
        }
        setStudentProfileImage(sImage);
    }

    @Click(R.id.profileImage)
    public void loadProfile() {
        startActivity(new Intent(this, Student_profile_activity.class));
    }

    @UiThread
    public void setStudentProfileImage(String sImage) {
    }

    @Background
    public void displayProfileName() {
        String profileName = "QR Group";
        try {
            if (LOGIN_MODE.equalsIgnoreCase(GROUP_MODE))
                profileName = AppDatabase.getDatabaseInstance(this).getGroupsDao().getGroupNameByGrpID(FC_Constants.currentStudentID);
            else if (!LOGIN_MODE.equalsIgnoreCase(QR_GROUP_MODE)) {
                profileName = AppDatabase.getDatabaseInstance(this).getStudentDao().getFullName(FC_Constants.currentStudentID);
            }

            if (LOGIN_MODE.equalsIgnoreCase(INDIVIDUAL_MODE))
                profileName = profileName.split(" ")[0];

        } catch (Exception e) {
            e.printStackTrace();
        }
        setProfileName(profileName);
    }

    @UiThread
    public void setProfileName(String profileName) {
        tv_Activity.setText(profileName);
        tv_Topic.setText(sub_Name);
    }
}