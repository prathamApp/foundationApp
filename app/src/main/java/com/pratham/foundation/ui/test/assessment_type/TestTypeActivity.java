package com.pratham.foundation.ui.test.assessment_type;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.test.certificate.CertificateActivity;
import com.pratham.foundation.utility.FC_Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.foundation.utility.FC_Constants.CURRENT_ASSESSMENT_STUDENT_ID;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;
import static com.pratham.foundation.utility.SplashSupportActivity.ButtonClickSound;


public class TestTypeActivity extends BaseActivity implements TestTypeContract.TestTypeView, TestStudentClicked {

    TestTypeContract.TestTypePresenter presenter;

    @BindView(R.id.btn_yes)
    ImageButton btn_yes;
    @BindView(R.id.btn_no)
    ImageButton btn_no;
    @BindView(R.id.rl_mainLayout)
    RelativeLayout first_layout;
    @BindView(R.id.rl_attendence)
    RelativeLayout attendence_layout;
    @BindView(R.id.supervisor_form_rl)
    RelativeLayout form_layout;
    @BindView(R.id.supervisor_name)
    EditText supervisor_name;
    @BindView(R.id.iv_image)
    ImageView iv_image;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.btn_submit)
    Button btn_submit;

    private RecyclerView recyclerView;
    String imageName, supervisorID, assessmentSession;
    private static final int CAMERA_REQUEST = 1888;
    static String nodeId;
    boolean formFlg = false, attendenceFlg = false, photoSaved = false;
    TestStudentAdapter testStudentAdapter;
    List<Student> studentTableList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_type);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        presenter = new TestTypePresenter(TestTypeActivity.this, this);
        nodeId = getIntent().getStringExtra("nodeId");
        formFlg = false;
        studentTableList = new ArrayList<>();
        form_layout.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.test_attendnce_recycler);
        testStudentAdapter = new TestStudentAdapter(this, studentTableList, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(this,10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(testStudentAdapter);

    }

    @OnClick(R.id.btn_yes)
    public void showForm() {
        formFlg = true;
        btn_no.setBackgroundResource(R.drawable.ripple_effect);
        btn_yes.setBackgroundResource(R.drawable.ripple_effect_selected);
        form_layout.setVisibility(View.VISIBLE);
        ButtonClickSound.start();
        supervisorID = "" + ApplicationClass.getUniqueID();
        FC_Constants.supervisedAssessment = true;
    }

    @OnClick(R.id.btn_no)
    public void gotoNext() {
        supervisorID = "";
        btn_no.setBackgroundResource(R.drawable.ripple_effect_selected);
        btn_yes.setBackgroundResource(R.drawable.ripple_effect);
        form_layout.setVisibility(View.GONE);
        ButtonClickSound.start();
        FC_Constants.supervisedAssessment = false;
        if(ApplicationClass.isTablet && FC_Constants.GROUP_LOGIN)
            presenter.getStudents();
        else
            submitSupervisorData();
    }

    @OnClick(R.id.btn_submit)
    public void submitClicked(){
        if(ApplicationClass.isTablet && FC_Constants.GROUP_LOGIN)
            presenter.getStudents();
        else
            submitSupervisorData();
        presenter.getStudents();
    }

    @Override
    public void StartTestActivity(String nodeId, String certiTitle) {
        Intent i = new Intent(this, CertificateActivity.class);
        i.putExtra("nodeId", nodeId);
        i.putExtra("CertiTitle", certiTitle);
        i.putExtra("display", FastSave.getInstance().getString(FC_Constants.ASSESSMENT_SESSION, ""));
        startActivity(i);
    }

    @Override
    public void setPhotoSaved(boolean photoSaved) {
        this.photoSaved = photoSaved;
    }

    public void submitSupervisorData() {
        if(FC_Constants.supervisedAssessment) {
            String sName = "" + supervisor_name.getText();
            ButtonClickSound.start();
            if (photoSaved) {
                if (sName.length() != 0) {
                    FC_Constants.supervisedAssessment = true;
                    presenter.AddSupervisor(supervisorID, sName, imageName);
                }
            } else {
                AnimateCamButtom(this, iv_image);
            }
        }else {
            FastSave.getInstance().saveString(CURRENT_ASSESSMENT_STUDENT_ID, "NA");
            presenter.AddSupervisor("NA", "NA", "NA");
        }
    }

    public void AnimateCamButtom(Context c, final ImageView imageView) {
        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.side_shake);
        imageView.startAnimation(animShake);
    }


/*    public void hideForm() {
        ButtonClickSound.start();
        tv_title.setText("Select Test Mode");
        form_layout.setVisibility(View.GONE);
    }

    public void showFrstLayout() {
        first_layout.setVisibility(View.VISIBLE);
    }

    public void hideFrstLayout() {
        first_layout.setVisibility(View.GONE);
    }*/

/*    @OnClick(R.id.submitBtn)
    public void submitSupervisorData() {
        String sName = "" + supervisor_name.getText();
        ButtonClickSound.start();
        if (photoSaved) {
            if (sName.length() != 0) {
                FC_Constants.supervisedAssessment = true;
                presenter.AddSupervisor(supervisorID, sName, imageName);
            }
        } else {
            AnimateCamButtom(this, btn_camera);
        }
    }*/

/*    public void AnimateCamButtom(Context c, final ImageButton imageButton) {
        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.side_shake);
        imageButton.startAnimation(animShake);
    }*/

    @OnClick(R.id.iv_image)
    public void openCamera() {
        photoSaved = false;
        ButtonClickSound.start();
        imageName = supervisorID + ".jpg";
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("codes", String.valueOf(requestCode) + resultCode);
        try {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                iv_image.setVisibility(View.VISIBLE);
                iv_image.setImageBitmap(photo);
                // String selectedImagePath = getPath(photo);
                presenter.createDirectoryAndSaveFile(photo, imageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
/*        if (attendenceFlg) {
            hideAttendence();
            if (formFlg) {
                showForm();
                formFlg = false;
                attendenceFlg = false;
            }else
                showFrstLayout();
        }
        else if (formFlg && !attendenceFlg) {
            formFlg = false;
            FC_Constants.supervisedAssessment = false;
            hideForm();
            showFrstLayout();
            supervisorID = "";
        } else if(!formFlg && !attendenceFlg){*/
            super.onBackPressed();
            finish();
//        }
    }

    public void hideAttendence() {
        attendenceFlg = false;
        attendence_layout.setVisibility(View.GONE);
    }

    @Override
    public void showAttendence() {
        attendenceFlg = true;
/*        if (formFlg)
            hideForm();
        else
            hideFrstLayout();*/
        attendence_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void notifyTestAdapter(List<Student> studentList) {
        studentTableList.addAll(studentList);
        testStudentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStudentClicked(int position, String studentId) {
        FastSave.getInstance().saveString(FC_Constants.CURRENT_ASSESSMENT_STUDENT_ID, studentId);
        submitSupervisorData();
    }
}