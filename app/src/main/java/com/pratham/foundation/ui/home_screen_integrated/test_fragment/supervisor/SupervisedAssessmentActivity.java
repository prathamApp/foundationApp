package com.pratham.foundation.ui.home_screen_integrated.test_fragment.supervisor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.database.domain.SupervisorData;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.test.assessment_type.TestStudentAdapter;
import com.pratham.foundation.ui.test.assessment_type.TestStudentClicked;
import com.pratham.foundation.utility.FC_Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.foundation.ApplicationClass.isTablet;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_ASSESSMENT_STUDENT_ID;
import static com.pratham.foundation.utility.FC_Constants.GROUP_MODE;


public class SupervisedAssessmentActivity extends BaseActivity implements TestStudentClicked {
    @BindView(R.id.submitBtn)
    Button submitBtn;
    @BindView(R.id.btn_camera)
    ImageButton btn_camera;
    @BindView(R.id.supervisor_name)
    EditText supervisor_name;
    @BindView(R.id.iv_image)
    ImageView iv_image;
    @BindView(R.id.rl_attendence)
    RelativeLayout attendence_layout;
    @BindView(R.id.rl_formLayout)
    RelativeLayout rl_formLayout;
    /*@BindDrawable(R.drawable.bg_anim)
    Drawable oneDwBg;*/

    String imageName = "";
    boolean isPhotoSaved = false, attendenceFlg = false;
    String supervisorId = "";
    String nodeId = "", testMode = "unsupervised";
    private static final int CAMERA_REQUEST = 1888;
    TestStudentAdapter testStudentAdapter;
    List<Student> studentTableList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervised_assessment);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        testMode = getIntent().getStringExtra("testMode");
        supervisorId = "" + ApplicationClass.getUniqueID();
        studentTableList = new ArrayList<>();
        setTitle("");

        if (testMode.equalsIgnoreCase("unsupervised")) {
            if (isTablet) {
                if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(GROUP_MODE)) {
                    attendence_layout.setVisibility(View.VISIBLE);
                    getStudents();
                } else {
                    FastSave.getInstance().saveString(CURRENT_ASSESSMENT_STUDENT_ID,
                            FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                    submitSupervisorData();
                }
            } else {
                FastSave.getInstance().saveString(CURRENT_ASSESSMENT_STUDENT_ID,
                        FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                submitSupervisorData();
            }
        } else {
            rl_formLayout.setVisibility(View.VISIBLE);
            attendence_layout.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_camera)
    public void openCamera() {
        imageName = supervisorId + ".jpg";
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
                iv_image.setScaleType(ImageView.ScaleType.FIT_XY);
                createDirectoryAndSaveFile(photo, imageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitSupervisorData() {
        String sName = "" + supervisor_name.getText();
        if (isPhotoSaved) {
            if (sName.length() != 0) {
                FC_Constants.supervisedAssessment = true;
                AddSupervisorToDB(supervisorId, sName, imageName);
            }
        } else {
            AnimateCamButtom(this, btn_camera);
        }
        goToAssessment();
    }

    @OnClick(R.id.submitBtn)
    public void submitClicked() {
        String sName = "" + supervisor_name.getText();
        if (isPhotoSaved) {
            if (sName.length() != 0) {
                FC_Constants.supervisedAssessment = true;
                if (FC_Constants.GROUP_LOGIN) {
                    getStudents();
                }else {
                    FastSave.getInstance().saveString(FC_Constants.CURRENT_ASSESSMENT_STUDENT_ID, FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                    submitSupervisorData();
                }
            } else Toast.makeText(this, "Enter supervisor name", Toast.LENGTH_SHORT).show();
        } else {
            AnimateCamButtom(this, btn_camera);
        }
    }

    public void AnimateCamButtom(Context c, final ImageButton imageButton) {
        final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.side_shake);
        imageButton.startAnimation(animShake);
    }

    @SuppressLint("StaticFieldLeak")
    public void AddSupervisorToDB(final String supervisorID, final String sName,
                                  final String supervisorPhoto) {
        new AsyncTask<Object, Void, Object>() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    SupervisorData supervisorData = new SupervisorData();
                    supervisorData.setSupervisorId(supervisorID);
                    supervisorData.setSupervisorName(sName);
                    supervisorData.setAssessmentSessionId(FastSave.getInstance().getString(FC_Constants.ASSESSMENT_SESSION, ""));
                    supervisorData.setSupervisorPhoto(supervisorPhoto);

                    AppDatabase.getDatabaseInstance(SupervisedAssessmentActivity.this).getSupervisorDataDao().insert(supervisorData);
                    BackupDatabase.backup(SupervisedAssessmentActivity.this);

                    AppDatabase.getDatabaseInstance(SupervisedAssessmentActivity.this).getStatusDao().updateValue("AssessmentSession", "" + FastSave.getInstance().getString(FC_Constants.ASSESSMENT_SESSION, ""));
                    FastSave.getInstance().saveString(FC_Constants.CURRENT_SUPERVISOR_ID, supervisorID);
//                    getStudents();
                    // goToAssessment();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getStudents() {
        new AsyncTask<Object, Void, Object>() {
            List<Student> studentList;

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    if (isTablet && FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(GROUP_MODE)) {
                        studentList = AppDatabase.appDatabase.getStudentDao().getGroupwiseStudents(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                    } else {
                        studentList = AppDatabase.appDatabase.getStudentDao().getAllStudents();
                    }
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                attendenceFlg = true;
                attendence_layout.setVisibility(View.VISIBLE);
                rl_formLayout.setVisibility(View.GONE);
                setTitle("");
                studentTableList.addAll(studentList);
                setStudentsToRecycler();
            }
        }.execute();
    }

    private void setStudentsToRecycler() {
        recyclerView = findViewById(R.id.test_attendnce_recycler);
        testStudentAdapter = new TestStudentAdapter(this, studentTableList, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(this,10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(testStudentAdapter);
        testStudentAdapter.notifyDataSetChanged();
    }

    private void goToAssessment() {
        setResult(RESULT_OK);
        finish();
    }

    public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        try {

            File direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal");
            if (!direct.exists()) direct.mkdir();
            direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/supervisorImages/");
            if (!direct.exists()) direct.mkdir();
            File file = new File(direct, fileName);
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            isPhotoSaved = true;
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
//        setResult(RESULT_CANCELED);
//        super.onBackPressed();
    }

    @Override
    public void onStudentClicked(int position, String studentId) {
        FastSave.getInstance().saveString(FC_Constants.CURRENT_ASSESSMENT_STUDENT_ID, studentId);
        submitSupervisorData();
    }

}
