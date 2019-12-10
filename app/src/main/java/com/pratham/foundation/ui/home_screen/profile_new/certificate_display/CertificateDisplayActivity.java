package com.pratham.foundation.ui.home_screen.profile_new.certificate_display;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.ui.student_profile.Student_profile_activity;
import com.pratham.foundation.ui.test.certificate.CertificateActivity;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.GROUP_LOGIN;
import static com.pratham.foundation.utility.FC_Constants.GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.INDIVIDUAL_MODE;
import static com.pratham.foundation.utility.FC_Constants.LOGIN_MODE;
import static com.pratham.foundation.utility.FC_Constants.QR_GROUP_MODE;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;

@EActivity(R.layout.activity_certificate_display)
public class CertificateDisplayActivity extends BaseActivity implements
        CertificateDisplayContract.CertificateView,
        CertificateDisplayContract.CertificateItemClicked{

    @Bean(CertificateDisplayPresenter.class)
    CertificateDisplayContract.CertificatePresenter presenter;

    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.certificate_recycler_view)
    RecyclerView recycler_view;
    @ViewById(R.id.tv_Activity)
    TextView tv_Activity;
    String sub_Name;
    CertificateAdapter certificateAdapter;
    List<Assessment> assessmentMainList;

    @AfterViews
    public void initialize() {
        Configuration config = getResources().getConfiguration();
        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
        presenter.setView(CertificateDisplayActivity.this);
        displayProfileName();
        displayProfileImage();
        showCertificates();
    }

    @Background
    public void showCertificates() {
        List<Assessment> assessmentList;
        if(GROUP_LOGIN)
            assessmentList = AppDatabase.getDatabaseInstance(this).getAssessmentDao()
                    .getCertificatesGroups(FC_Constants.currentGroup, FC_Constants.CERTIFICATE_LBL);
        else
            assessmentList = AppDatabase.getDatabaseInstance(this).getAssessmentDao()
                    .getCertificates(FC_Constants.currentStudentID, FC_Constants.CERTIFICATE_LBL);

        addToAdapter(assessmentList);
    }

    @UiThread
    public void addToAdapter(List<Assessment> assessmentList) {
        assessmentMainList = assessmentList;
        if(certificateAdapter==null) {
            certificateAdapter = new CertificateAdapter(this, assessmentList,
                    CertificateDisplayActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(this), true));
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(certificateAdapter);
        }else
            certificateAdapter.notifyDataSetChanged();
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
//        tv_Activity.setText(profileName);
        tv_Topic.setText(profileName);
    }

    @Override
    public void gotoCertificate(Assessment assessment) {
        Toast.makeText(this, "gotoCertificate", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, CertificateActivity.class);
        intent.putExtra("nodeId", "na");
        intent.putExtra("CertiTitle", "" + assessment.getLevela());
        intent.putExtra("display", "display");
        intent.putExtra("assessment", assessment);
        startActivity(intent);
    }
}