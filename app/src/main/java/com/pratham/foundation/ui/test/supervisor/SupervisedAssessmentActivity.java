package com.pratham.foundation.ui.test.supervisor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
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
import com.pratham.foundation.R;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.database.domain.SupervisorData;
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
import static com.pratham.foundation.utility.FC_Constants.assessmentSession;


public class SupervisedAssessmentActivity extends AppCompatActivity implements TestStudentClicked {
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
                if (FC_Constants.GROUP_LOGIN) {
                    attendence_layout.setVisibility(View.VISIBLE);
                    getStudents();
                } else {
                    FC_Constants.currentAssessmentStudentID = FC_Constants.currentStudentID;
                    submitSupervisorData();
                }
            } else {
                FC_Constants.currentAssessmentStudentID = FC_Constants.currentStudentID;
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
                    FC_Constants.currentAssessmentStudentID = FC_Constants.currentStudentID;
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
                    supervisorData.setAssessmentSessionId(assessmentSession);
                    supervisorData.setSupervisorPhoto(supervisorPhoto);

                    AppDatabase.getDatabaseInstance(SupervisedAssessmentActivity.this).getSupervisorDataDao().insert(supervisorData);
                    BackupDatabase.backup(SupervisedAssessmentActivity.this);

                    AppDatabase.getDatabaseInstance(SupervisedAssessmentActivity.this).getStatusDao().updateValue("AssessmentSession", "" + assessmentSession);
                    FC_Constants.currentsupervisorID = "" + supervisorID;

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
                    if (isTablet && FC_Constants.GROUP_LOGIN) {

                        studentList = AppDatabase.appDatabase.getStudentDao().getGroupwiseStudents(FC_Constants.currentStudentID);
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
        recyclerView = (RecyclerView) findViewById(R.id.test_attendnce_recycler);
        testStudentAdapter = new TestStudentAdapter(this, studentTableList, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
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

            File direct = new File(Environment.getExternalStorageDirectory().toString() + "/.EngGameInternal");
            if (!direct.exists()) direct.mkdir();
            direct = new File(Environment.getExternalStorageDirectory().toString() + "/.EngGameInternal/supervisorImages/");
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
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onStudentClicked(int position, String studentId) {
        FC_Constants.currentAssessmentStudentID = "" + studentId;
        submitSupervisorData();
    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + avatar) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + avatar) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }


}
