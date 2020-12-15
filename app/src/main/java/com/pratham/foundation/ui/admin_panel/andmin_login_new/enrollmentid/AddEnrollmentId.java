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
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.database.domain.StudentEnrollment;
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.interfaces.SplashInterface;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.ui.bottom_fragment.add_student.AvatarClickListener;
import com.pratham.foundation.ui.splash_activity.SplashActivity;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    TextView tv_name_str;
    TextView tv_class_str;
    TextView tv_school_str;
    ImageView image_view;

    private API_Content api_content;
    private Context mContext;
    private Gson gson;
    private String avatarName, gender;
    private Student student;

//    public AddEnrollmentId() {
//        // Required empty public constructor
//    }

    private ArrayList<AvatarModal> avatarList = new ArrayList<>();
    private static SplashInterface splashInterface;
    private EnrollmentAvatarAdapter avatarAdapter;

    @AfterViews
    public void initialize() {
        mContext = AddEnrollmentId.this;
        FC_Constants.StudentPhotoPath = Environment.getExternalStorageDirectory().toString() + "/.PankhInternal/StudentProfilePhotos/";
        api_content = new API_Content(mContext, AddEnrollmentId.this);
        gson = new Gson();

        ArrayAdapter<String> classAdapter = new ArrayAdapter(mContext, R.layout.custom_spinner,
                getResources().getStringArray(R.array.student_class));
        addAvatarsInList();
        student = new Student();
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
 /*   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        student = new Student();
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
        Objects.requireNonNull(getActivity()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_student, container, false);
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.bind(this, view);
//        setupUI(ll_form);
//        editorListener(et_studentID);
        return view;
    }*/

/*    public static AddEnrollmentId newInstance(SplashInterface splashInter) {
        AddEnrollmentId frag = new AddEnrollmentId();
        student = new Student();
        Bundle args = new Bundle();
        args.putString("title", "Create Profile");
        frag.setArguments(args);
        splashInterface = splashInter;
        return frag;
    }*/



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

/*    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        SplashActivity.fragmentAddStudentOpenFlg = false;
    }*/

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

/*
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();
        FC_Constants.StudentPhotoPath = Environment.getExternalStorageDirectory().toString() + "/.PankhInternal/StudentProfilePhotos/";
        api_content = new API_Content(mContext, AddEnrollmentId.this);
        gson = new Gson();

        ArrayAdapter<String> classAdapter = new ArrayAdapter(mContext, R.layout.custom_spinner,
                getResources().getStringArray(R.array.student_class));
        addAvatarsInList();
        avatarAdapter = new AvatarAdapter(getActivity(), this, avatarList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(getActivity(), 10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(avatarAdapter);
        avatarAdapter.notifyDataSetChanged();
    }
*/

/*    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }*/

//    public void editorListener(final EditText view) {
//        view.setOnEditorActionListener(
//                new EditText.OnEditorActionListener() {
//                    @Override
//                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                        if (actionId == EditorInfo.IME_ACTION_NEXT ||
//                                actionId == EditorInfo.IME_ACTION_SEARCH ||
//                                actionId == EditorInfo.IME_ACTION_DONE ||
//                                event != null &&
//                                        event.getAction() == KeyEvent.ACTION_DOWN &&
//                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                            view.clearFocus();
//                            if (view != null) {
//                                InputMethodManager imm = (InputMethodManager) getActivity()
//                                        .getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                            }
//                            return true;
//                        }
//                        return false; // pass on to other listeners.
//                    }
//                }
//        );
//    }

/*    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }*/

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
    }

    @SuppressLint("StaticFieldLeak")
    @Click(R.id.btn_enroll_student)
    public void onAddNewClick() {

/*        try {
            ButtonClickSound.start();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        if (FC_Utility.isDataConnectionAvailable(mContext)) {
            if (et_studentID.getText().toString().equalsIgnoreCase("") || avatarName == null) {
                Toast.makeText(mContext, "Please fill all the details..", Toast.LENGTH_SHORT).show();
            } else {
                showLoader();
                String myUrl = STUDENT_BY_ENROLLMENT_NO_API + et_studentID.getText().toString();
                api_content.getStudentByEnrollmentNo(STUDENT_BY_ENROLLMENT_NO, myUrl);
            }
//            new AsyncTask<Object, Void, Object>() {
//                @Override
//                protected Object doInBackground(Object[] objects) {
//
//                    student.setDeviceId(AppDatabase.getDatabaseInstance(getContext()).getStatusDao().getValue("DeviceId"));
//                    AppDatabase.getDatabaseInstance(getActivity()).getStudentDao().insert(student);
//                    BackupDatabase.backup(getActivity());
//                    return null;
//                }
//            }.execute();
//            Toast.makeText(getActivity(), "Profile created Successfully..", Toast.LENGTH_SHORT).show();
//            splashInterface.onChildAdded();
//            dismiss();
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
//        dismiss();
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
//        avatarName = ApplicationClass.pradigiPath + "/.Pankh/English/App_Thumbs/" + StudentName;
    }

    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(STUDENT_BY_ENROLLMENT_NO)) {
            try {
                Type listType = new TypeToken<ArrayList<StudentEnrollment>>() {
                }.getType();
                List<StudentEnrollment> serverEnrollmentList = gson.fromJson(response, listType);
                if(serverEnrollmentList!= null && serverEnrollmentList.size()>0) {
                    student.setAge(serverEnrollmentList.get(0).getAge());
                    student.setMiddleName("" + serverEnrollmentList.get(0).getVillageId());
                    student.setFullName(serverEnrollmentList.get(0).getFullName());
                    student.setGender(serverEnrollmentList.get(0).getGender());
                    student.setGroupId(serverEnrollmentList.get(0).getGroupId());
                    student.setGroupName(serverEnrollmentList.get(0).getGroupName());
                    student.setStud_Class(serverEnrollmentList.get(0).getStud_Class());
                    student.setVillageName(serverEnrollmentList.get(0).getVillageName());
                    student.setStudentID(serverEnrollmentList.get(0).getStudentID());
                    student.setStudentUID("PS");
                    student.setRegDate(FC_Utility.getCurrentDateTime());
                    student.setAvatarName(avatarName);
                    student.setDeviceId(AppDatabase.getDatabaseInstance(mContext).getStatusDao().getValue("DeviceId"));
                    ShowDetailsDilog(student);
                }else {
                    dismissLoadingDialog();
                    Toast.makeText(mContext, "Wrong Enrollment id", Toast.LENGTH_SHORT).show();
                }
/*
                AppDatabase.getDatabaseInstance(mContext).getStudentDao().insert(student);
                BackupDatabase.backup(mContext);
                dismissLoadingDialog();
                Toast.makeText(getActivity(), "Profile created Successfully..", Toast.LENGTH_SHORT).show();
                splashInterface.onChildAdded();
                new Handler().postDelayed(() ->
                        dismiss(), 1000);
                        */
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    BlurPopupWindow studentDialog;

    public void ShowDetailsDilog(Student student) {
        studentDialog = new BlurPopupWindow.Builder(mContext)
                .setContentView(R.layout.student_details_dialog)
                .bindClickListener(v -> {
                    new Handler().postDelayed(() -> {
                        addStudentData(student);
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

        tv_name_str = studentDialog.findViewById(R.id.tv_name_str);
        tv_class_str = studentDialog.findViewById(R.id.tv_class_str);
        tv_school_str = studentDialog.findViewById(R.id.tv_school_str);
        image_view = studentDialog.findViewById(R.id.image_view);

        Bitmap bmImg = BitmapFactory.decodeFile(ApplicationClass.foundationPath + "/.FCA/App_Thumbs/" + student.getAvatarName());
        image_view.setImageBitmap(bmImg);
        tv_name_str.setText(student.getFullName());
        tv_class_str.setText(student.getStud_Class());
        tv_school_str.setText(student.getVillageName());
        studentDialog.show();

    }

    private void addStudentData(Student student) {
        try {
            AppDatabase.getDatabaseInstance(mContext).getStudentDao().insert(student);
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
