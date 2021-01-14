package com.pratham.foundation.ui.admin_panel.andmin_login_new.enrollmentid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.async.API_Content;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.AvatarModal;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.database.domain.StudentEnrollmentModel;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.interfaces.SplashInterface;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.Model_CourseEnrollment;
import com.pratham.foundation.ui.bottom_fragment.add_student.AvatarClickListener;
import com.pratham.foundation.ui.splash_activity.SplashActivity;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.utility.FC_Constants.STUDENT_BY_ENROLLMENT_NO;
import static com.pratham.foundation.utility.FC_Constants.STUDENT_BY_ENROLLMENT_NO_API;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;
import static com.pratham.foundation.utility.FC_Utility.hideSoftKeyboard;

@EActivity(R.layout.activity_add_enrollment_id)
public class AddEnrollmentId extends BaseActivity implements AvatarClickListener, API_Content_Result {

    @ViewById(R.id.form_root)
    RelativeLayout homeRoot;
    @ViewById(R.id.rv_Avatars)
    RecyclerView recyclerView;
    @ViewById(R.id.et_studentID)
    EditText et_studentID;
    @ViewById(R.id.txt_using)
    TextView txt_using;
    @ViewById(R.id.btn_back)
    ImageButton btn_back;
    @ViewById(R.id.form_root)
    RelativeLayout form_root;
    TextView tv_name_str,tv_class_str,tv_school_str;
    ImageView image_view;
    LinearLayout ll_class,ll_name,ll_school;

    private API_Content api_content;
    private Context mContext;
    private Gson gson;
    private String avatarName, gender;

//    public AddEnrollmentId() {
//        // Required empty public constructor
//    }

    private ArrayList<AvatarModal> avatarList = new ArrayList<>();
    private static SplashInterface splashInterface;
    private EnrollmentAvatarAdapter avatarAdapter;

    @AfterViews
    public void initialize() {
        mContext = AddEnrollmentId.this;
        FC_Constants.StudentPhotoPath = Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/StudentProfilePhotos/";
        api_content = new API_Content(mContext, AddEnrollmentId.this);
        gson = new Gson();
        addAvatarsInList();

        ArrayAdapter<String> classAdapter = new ArrayAdapter(mContext, R.layout.custom_spinner,
                getResources().getStringArray(R.array.student_class));
        avatarAdapter = new EnrollmentAvatarAdapter(mContext, this, avatarList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(mContext, 10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(avatarAdapter);
        avatarAdapter.notifyDataSetChanged();
        setupUI(form_root);
        setupUI(recyclerView);
    }

    @Override
    public void onPause() {
        super.onPause();
        SplashActivity.fragmentAddStudentOpenFlg = true;
        SplashActivity.fragmentAddStudentPauseFlg = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        SplashActivity.fragmentAddStudentOpenFlg = true;
        SplashActivity.fragmentAddStudentPauseFlg = false;
    }

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        hideSoftKeyboard(AddEnrollmentId.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void addAvatarsInList() {
        AvatarModal avatarModal = new AvatarModal();
        avatarModal.setAvatarName("g1.png");
        avatarModal.setClickFlag(false);
        avatarList.add(avatarModal);

        AvatarModal avatarModal1 = new AvatarModal();
        avatarModal1.setAvatarName("b1.png");
        avatarModal1.setClickFlag(false);
        avatarList.add(avatarModal1);

        AvatarModal avatarModal2 = new AvatarModal();
        avatarModal2.setAvatarName("g2.png");
        avatarModal2.setClickFlag(false);
        avatarList.add(avatarModal2);

        AvatarModal avatarModal3 = new AvatarModal();
        avatarModal3.setAvatarName("b2.png");
        avatarModal3.setClickFlag(false);
        avatarList.add(avatarModal3);

        AvatarModal avatarModal4 = new AvatarModal();
        avatarModal4.setAvatarName("g3.png");
        avatarModal4.setClickFlag(false);
        avatarList.add(avatarModal4);

        AvatarModal avatarModal5 = new AvatarModal();
        avatarModal5.setAvatarName("b3.png");
        avatarModal5.setClickFlag(false);
        avatarList.add(avatarModal5);

        AvatarModal avatarModal6 = new AvatarModal();
        avatarModal6.setAvatarName("ic_grp_btn.png");
        avatarModal6.setClickFlag(false);
        avatarList.add(avatarModal6);
    }

    @SuppressLint("StaticFieldLeak")
    @Click(R.id.btn_enroll_student)
    public void onAddNewClick() {
        try {
            ButtonClickSound.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (FC_Utility.isDataConnectionAvailable(mContext)) {
            if (et_studentID.getText().toString().equalsIgnoreCase("") || avatarName == null) {
                Toast.makeText(mContext, "Please fill all the details..", Toast.LENGTH_SHORT).show();
            } else {
                showLoader();
                String myUrl = STUDENT_BY_ENROLLMENT_NO_API + et_studentID.getText().toString();
                api_content.getStudentByEnrollmentNo(STUDENT_BY_ENROLLMENT_NO, myUrl);
            }
        }
    }

    private boolean loaderVisible = false;
    private CustomLodingDialog myLoadingDialog;

    public void showLoader() {
        if (!loaderVisible) {
            loaderVisible = true;
            myLoadingDialog = new CustomLodingDialog(mContext);
            myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(myLoadingDialog.getWindow()).
                    setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myLoadingDialog.setContentView(R.layout.loading_dialog);
            myLoadingDialog.setCanceledOnTouchOutside(false);
//        myLoadingDialog.setCancelable(false);
            myLoadingDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        try {
            loaderVisible = false;
            new Handler().postDelayed(() -> {
                if (myLoadingDialog != null && myLoadingDialog.isShowing())
                    myLoadingDialog.dismiss();
            }, 150);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.btn_back)
    public void pressedBackButton() {
        SplashActivity.fragmentAddStudentOpenFlg = false;
//        try {
//            BackBtnSound.start();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
        finish();
        EventMessage message = new EventMessage();
        message.setMessage("reload");
        EventBus.getDefault().post(message);
    }

    @Override
    public void onAvatarClick(int position, String StudentName) {
        avatarName = StudentName;
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(form_root.getWindowToken(), 0);
        for (int i = 0; i < avatarList.size(); i++)
            avatarList.get(i).setClickFlag(false);
        avatarList.get(position).setClickFlag(true);
        avatarAdapter.notifyDataSetChanged();
    }

    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(STUDENT_BY_ENROLLMENT_NO)) {
            try {
                Type listType = new TypeToken<StudentEnrollmentModel>() {
                }.getType();
                StudentEnrollmentModel enrollmentModel = gson.fromJson(response, listType);
                String deviceID = FC_Utility.getDeviceID();
                if (enrollmentModel != null) {
                    ShowDetailsDilog(enrollmentModel);
/*                    if (enrollmentModel.getLstCourseEnroll() != null) {
                        List<Model_CourseEnrollment> courseEnrollmentList = new ArrayList<>();
                        for (int v = 0; v < enrollmentModel.getLstCourseEnroll().size(); v++) {
                            Model_CourseEnrollment model_courseEnrollment = new Model_CourseEnrollment();
                            model_courseEnrollment.setCourseId(enrollmentModel.getLstCourseEnroll().get(v).getCourseId());
                            model_courseEnrollment.setGroupId(enrollmentModel.getLstCourseEnroll().get(v).getGroupId());
                            model_courseEnrollment.setPlanFromDate(enrollmentModel.getLstCourseEnroll().get(v).getPlanFromDate());
                            model_courseEnrollment.setPlanToDate(enrollmentModel.getLstCourseEnroll().get(v).getPlanToDate());
                            model_courseEnrollment.setLanguage(enrollmentModel.getLstCourseEnroll().get(v).getLanguage());
                            courseEnrollmentList.add(model_courseEnrollment);
                        }
                        AppDatabase.getDatabaseInstance(this).getCourseDao().insertListCourse(courseEnrollmentList);
                    }
                    if (enrollmentModel.getEnrollmentType().equalsIgnoreCase("Student")) {
                        Student student = new Student();
                        student.setStudentID(enrollmentModel.getLstStudent().get(0).getStudentId());
                        student.setFullName(enrollmentModel.getLstStudent().get(0).getFullName());
                        student.setMiddleName("PS");
                        student.setLastName(enrollmentModel.getLstStudent().get(0).getStudentEnrollment());
                        student.setStud_Class(enrollmentModel.getLstStudent().get(0).getStudClass());
                        student.setAge(enrollmentModel.getLstStudent().get(0).getAge());
                        student.setGender(enrollmentModel.getLstStudent().get(0).getGender());
                        student.setGroupId(enrollmentModel.getLstStudent().get(0).getGroupId());
                        student.setGroupName(enrollmentModel.getLstStudent().get(0).getGroupName());
                        for (int i = 0; i < avatarList.size(); i++)
                            if (avatarList.get(i).getClickFlag())
                                student.setAvatarName(avatarList.get(i).getAvatarName());
                        student.setDeviceId(deviceID);
                        AppDatabase.getDatabaseInstance(this).getStudentDao().insert(student);
                    } else {
                        Groups groups = new Groups();
                        groups.setGroupId(enrollmentModel.getGroupId());
                        groups.setGroupName(enrollmentModel.getGroupName());
                        groups.setVillageId(enrollmentModel.getVillageId());
                        groups.setProgramId(enrollmentModel.getProgramId());
                        groups.setGroupCode(enrollmentModel.getGroupCode());
                        groups.setSchoolName(enrollmentModel.getSchoolName());
                        groups.setVIllageName(enrollmentModel.getVIllageName());
                        groups.setDeviceId(deviceID);
                        AppDatabase.getDatabaseInstance(this).getGroupsDao().insert(groups);
                        List<Student> studentList = new ArrayList<>();
                        for (int i = 0; i < enrollmentModel.getLstStudent().size(); i++) {
                            Student student = new Student();
                            student.setStudentID(enrollmentModel.getLstStudent().get(i).getStudentId());
                            student.setFullName(enrollmentModel.getLstStudent().get(i).getFullName());
                            student.setLastName(enrollmentModel.getLstStudent().get(i).getStudentEnrollment());
                            student.setStud_Class(enrollmentModel.getLstStudent().get(i).getStudClass());
                            student.setAge(enrollmentModel.getLstStudent().get(i).getAge());
                            student.setGender(enrollmentModel.getLstStudent().get(i).getGender());
                            student.setGroupId(enrollmentModel.getLstStudent().get(i).getGroupId());
                            student.setGroupName(enrollmentModel.getLstStudent().get(i).getGroupName());
                            student.setDeviceId(deviceID);
                            studentList.add(student);
                        }
                        AppDatabase.getDatabaseInstance(this).getStudentDao().insertAll(studentList);
                    }*/
                    dismissLoadingDialog();
                } else {
                    dismissLoadingDialog();
                    Toast.makeText(mContext, "Wrong Enrollment id", Toast.LENGTH_SHORT).show();
                }
//                if(serverEnrollmentList!= null && serverEnrollmentList.size()>0) {
//                    student.setAge(serverEnrollmentList.get(0).getAge());
//                    student.setMiddleName("" + serverEnrollmentList.get(0).getVillageId());
//                    student.setFullName(serverEnrollmentList.get(0).getFullName());
//                    student.setGender(serverEnrollmentList.get(0).getGender());
//                    student.setGroupId(serverEnrollmentList.get(0).getGroupId());
//                    student.setGroupName(serverEnrollmentList.get(0).getGroupName());
//                    student.setStud_Class(serverEnrollmentList.get(0).getStud_Class());
//                    student.setVillageName(serverEnrollmentList.get(0).getVillageName());
//                    student.setStudentID(serverEnrollmentList.get(0).getStudentID());
//                    student.setStudentUID("PS");
//                    student.setRegDate(FC_Utility.getCurrentDateTime());
//                    student.setAvatarName(avatarName);
//                    student.setDeviceId(AppDatabase.getDatabaseInstance(mContext).getStatusDao().getValue("DeviceId"));
//                    ShowDetailsDilog(student);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    BlurPopupWindow studentDialog;

    @UiThread
    public void ShowDetailsDilog(StudentEnrollmentModel enrollmentModel) {
        try {
            studentDialog = new BlurPopupWindow.Builder(mContext)
                    .setContentView(R.layout.student_details_dialog)
                    .bindClickListener(v -> {
                        new Handler().postDelayed(() -> {
                            addStudentData(enrollmentModel);
                            studentDialog.dismiss();
                        }, 200);
                    }, R.id.dia_btn_save)
                    .bindClickListener(v -> {
                        new Handler().postDelayed(() -> studentDialog.dismiss(), 200);
                    }, R.id.dia_btn_cancel)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnTouchBackground(false)
                    .setDismissOnClickBack(false)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(10)
                    .setTintColor(0x30000000)
                    .build();

            ll_school = studentDialog.findViewById(R.id.ll_school);
            ll_name = studentDialog.findViewById(R.id.ll_name);
            ll_class = studentDialog.findViewById(R.id.ll_class);
            tv_name_str = studentDialog.findViewById(R.id.tv_name_str);
            tv_class_str = studentDialog.findViewById(R.id.tv_class_str);
            tv_school_str = studentDialog.findViewById(R.id.tv_school_str);
            image_view = studentDialog.findViewById(R.id.image_view);

            Bitmap bmImg = BitmapFactory.decodeFile(ApplicationClass.foundationPath + "/.FCA/App_Thumbs/ic_grp_btn.png");
            image_view.setImageBitmap(bmImg);
            String name, stuClass, village;
            if (enrollmentModel.getEnrollmentType().equalsIgnoreCase("Student")) {
                name = enrollmentModel.getLstStudent().get(0).getFullName();
                stuClass = enrollmentModel.getLstStudent().get(0).getStudClass();
                ll_school.setVisibility(View.GONE);
                tv_class_str.setText(stuClass);
            } else {
                name = enrollmentModel.getGroupName();
                village = enrollmentModel.getSchoolName();
                ll_class.setVisibility(View.GONE);
                tv_school_str.setText(village);
            }
            tv_name_str.setText(name);
            studentDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    private void addStudentData(StudentEnrollmentModel enrollmentModel) {
        try {
            dismissLoadingDialog();
            String deviceID = FC_Utility.getDeviceID();
            if (enrollmentModel.getLstCourseEnroll() != null) {
                List<Model_CourseEnrollment> courseEnrollmentList = new ArrayList<>();
                for (int v = 0; v < enrollmentModel.getLstCourseEnroll().size(); v++) {
                    Model_CourseEnrollment model_courseEnrollment = new Model_CourseEnrollment();
                    model_courseEnrollment.setCourseId(enrollmentModel.getLstCourseEnroll().get(v).getCourseId());
                    model_courseEnrollment.setGroupId(enrollmentModel.getLstCourseEnroll().get(v).getGroupId());
                    model_courseEnrollment.setPlanFromDate(enrollmentModel.getLstCourseEnroll().get(v).getPlanFromDate());
                    model_courseEnrollment.setPlanToDate(enrollmentModel.getLstCourseEnroll().get(v).getPlanToDate());
                    model_courseEnrollment.setLanguage(enrollmentModel.getLstCourseEnroll().get(v).getLanguage());
                    courseEnrollmentList.add(model_courseEnrollment);
                }
                AppDatabase.getDatabaseInstance(this).getCourseDao().insertListCourse(courseEnrollmentList);
            }
            if (enrollmentModel.getEnrollmentType().equalsIgnoreCase("Student")) {
                Student student = new Student();
                student.setStudentID(enrollmentModel.getLstStudent().get(0).getStudentId());
                student.setFullName(enrollmentModel.getLstStudent().get(0).getFullName());
                student.setMiddleName("PS");
                student.setLastName(enrollmentModel.getLstStudent().get(0).getStudentEnrollment());
                student.setStud_Class(enrollmentModel.getLstStudent().get(0).getStudClass());
                student.setAge(enrollmentModel.getLstStudent().get(0).getAge());
                student.setGender(enrollmentModel.getLstStudent().get(0).getGender());
                student.setGroupId(enrollmentModel.getLstStudent().get(0).getGroupId());
                student.setGroupName(enrollmentModel.getLstStudent().get(0).getGroupName());
                for (int i = 0; i < avatarList.size(); i++)
                    if (avatarList.get(i).getClickFlag())
                        student.setAvatarName(avatarList.get(i).getAvatarName());
                student.setDeviceId(deviceID);
                AppDatabase.getDatabaseInstance(this).getStudentDao().insert(student);
            } else {
                Groups groups = new Groups();
                groups.setGroupId(enrollmentModel.getGroupId());
                groups.setGroupName(enrollmentModel.getGroupName());
                groups.setVillageId(enrollmentModel.getVillageId());
                groups.setProgramId(enrollmentModel.getProgramId());
                groups.setGroupCode(enrollmentModel.getGroupCode());
                groups.setSchoolName(enrollmentModel.getSchoolName());
                groups.setVIllageName(enrollmentModel.getVIllageName());
                groups.setDeviceId(deviceID);
                AppDatabase.getDatabaseInstance(this).getGroupsDao().insert(groups);
                List<Student> studentList = new ArrayList<>();
                for (int i = 0; i < enrollmentModel.getLstStudent().size(); i++) {
                    Student student = new Student();
                    student.setStudentID(enrollmentModel.getLstStudent().get(i).getStudentId());
                    student.setFullName(enrollmentModel.getLstStudent().get(i).getFullName());
                    student.setLastName(enrollmentModel.getLstStudent().get(i).getStudentEnrollment());
                    student.setStud_Class(enrollmentModel.getLstStudent().get(i).getStudClass());
                    student.setAge(enrollmentModel.getLstStudent().get(i).getAge());
                    student.setGender(enrollmentModel.getLstStudent().get(i).getGender());
                    student.setGroupId(enrollmentModel.getLstStudent().get(i).getGroupId());
                    student.setGroupName(enrollmentModel.getLstStudent().get(i).getGroupName());
                    student.setDeviceId(deviceID);
                    studentList.add(student);
                }
                AppDatabase.getDatabaseInstance(this).getStudentDao().insertAll(studentList);
            }
            dismissLoadingDialog();
            BackupDatabase.backup(mContext);
            Toast.makeText(mContext, "Profile created Successfully..", Toast.LENGTH_SHORT).show();
//        splashInterface.onChildAdded();
        } catch (Exception e) {
            dismissLoadingDialog();
            new Handler().postDelayed(() ->
                    btn_back.performClick(), 1000);
            e.printStackTrace();
        }
    }

    @Override
    public void receivedError(String header) {
        Toast.makeText(mContext, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
    }
}
