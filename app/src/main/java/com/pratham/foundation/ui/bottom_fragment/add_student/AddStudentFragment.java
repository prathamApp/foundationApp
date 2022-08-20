package com.pratham.foundation.ui.bottom_fragment.add_student;

import static com.pratham.foundation.utility.FC_Utility.dpToPx;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.AvatarModal;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.interfaces.SplashInterface;
import com.pratham.foundation.ui.splash_activity.SplashActivity;
import com.pratham.foundation.utility.FC_Utility;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddStudentFragment extends DialogFragment implements AvatarClickListener {

    @BindView(R.id.form_root)
    RelativeLayout homeRoot;

    @BindView(R.id.rv_Avatars)
    RecyclerView recyclerView;

    @BindView(R.id.et_studentName)
    EditText et_studentName;

    @BindView(R.id.spinner_age)
    Spinner spinner_age;

/*    @BindView(R.id.spinner_class)
    Spinner spinner_class;*/

    @BindView(R.id.rb_male)
    RadioButton rb_male;

    @BindView(R.id.rb_female)
    RadioButton rb_female;

    String gender = "";
    String avatarName;

    public AddStudentFragment() {
        // Required empty public constructor
    }

    ArrayList<AvatarModal> avatarList = new ArrayList<>();
    static SplashInterface splashInterface;
    AvatarAdapter avatarAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
        Objects.requireNonNull(getActivity()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void editorListener(final EditText view) {
        view.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT ||
                                actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            view.clearFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity())
                                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                                Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            return true;
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );
    }

    public static AddStudentFragment newInstance(SplashInterface splashInter) {
        AddStudentFragment frag = new AddStudentFragment();
        Bundle args = new Bundle();
        args.putString("title", "Create Profile");
        frag.setArguments(args);
        splashInterface = splashInter;
        return frag;
    }

    @Override
    public void onPause() {
        super.onPause();
        SplashActivity.fragmentAddStudentPauseFlg = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        SplashActivity.fragmentAddStudentOpenFlg = true;
        SplashActivity.fragmentAddStudentPauseFlg = false;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        SplashActivity.fragmentAddStudentOpenFlg = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter ageAdapter = new ArrayAdapter(getActivity(), R.layout.custom_spinner, getResources().getStringArray(R.array.age));
        spinner_age.setAdapter(ageAdapter);

        spinner_age.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return false;
            }
        });
/*        spinner_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });*/
        spinner_age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        ArrayAdapter<String> classAdapter = new ArrayAdapter(getActivity(), R.layout.custom_spinner, getResources().getStringArray(R.array.student_class));
        //spinner_class.setAdapter(classAdapter);
        addAvatarsInList();
        avatarAdapter = new AvatarAdapter(getActivity(), this, avatarList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(getActivity(), 10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(avatarAdapter);
        avatarAdapter.notifyDataSetChanged();


/*        et_studentName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });*/
    }

    private void hideKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_student, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.bind(this, view);
        editorListener(et_studentName);
        return view;
    }

/*    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
       if(activity.getCurrentFocus()!=null && activity.getCurrentFocus().getWindowToken() != null)
           inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }*/

    /*public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
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
    }*/

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @OnClick(R.id.rb_male)
    public void maleGenderClicked() {
//        try {
//            ButtonClickSound.start();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
        // rb_male.setBackground(getResources().getDrawable(R.drawable.correct_bg));
        // rb_female.setBackground(getResources().getDrawable(R.drawable.ripple_rectangle));
        gender = "Male";
    }

    @OnClick(R.id.rb_female)
    public void femaleGenderClicked() {
//        try {
//            ButtonClickSound.start();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
        //rb_female.setBackground(getResources().getDrawable(R.drawable.correct_bg));
        //rb_male.setBackground(getResources().getDrawable(R.drawable.ripple_rectangle));
        gender = "Female";
    }

    @SuppressLint("StaticFieldLeak")
    @OnClick(R.id.btn_add_new_student)
    public void onAddNewClick() {
        ApplicationClass.vibrator.vibrate(60);
        if (et_studentName.getText().toString().equalsIgnoreCase("") ||
                (spinner_age.getSelectedItemPosition() == 0) ||
                gender.equalsIgnoreCase("") || avatarName == null) {
            Toast.makeText(getActivity(), ""+getResources().getString(R.string.please_fill_details), Toast.LENGTH_SHORT).show();
        } else {
            Student student = new Student();
            student.setStudentID(ApplicationClass.getUniqueID().toString());
            student.setFullName(et_studentName.getText().toString());
            student.setAge(Integer.parseInt(spinner_age.getSelectedItem().toString()));
            student.setStud_Class(/*spinner_class.getSelectedItem().toString()*/"");
            student.setGender(gender);
            student.setAvatarName(avatarName);
            student.setGroupId("PS");
            student.setMiddleName("PS");
            student.setLastName("PS");
            student.setEnrollmentId("PS");
            student.setRegDate(FC_Utility.getCurrentDateTime());
//            student.setProgramId("0");
            new AsyncTask<Object, Void, Object>() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    student.setDeviceId(AppDatabase.getDatabaseInstance(getContext()).getStatusDao().getValue("DeviceId"));
                    AppDatabase.getDatabaseInstance(getActivity()).getStudentDao().insert(student);
                    BackupDatabase.backup(getActivity());
                    return null;
                }
            }.execute();
            Toast.makeText(getActivity(), ""+getResources().getString(R.string.profile_created_successfully), Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(getActivity(), ChooseLevelActivity.class));
            splashInterface.onChildAdded();
            dismiss();
        }
    }

    @Override
    public void onAvatarClick(int position, String StudentName) {
        avatarName = StudentName;
        for (int i = 0; i < avatarList.size(); i++)
            avatarList.get(i).setClickFlag(false);
        avatarList.get(position).setClickFlag(true);
        avatarAdapter.notifyDataSetChanged();
//        avatarName = ApplicationClass.pradigiPath + "/.FCA/English/App_Thumbs/" + StudentName;
    }
}
