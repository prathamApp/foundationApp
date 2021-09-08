package com.pratham.foundation.ui.bottom_fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.async.DownloadData;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.dao.StatusDao;
import com.pratham.foundation.database.domain.Attendance;
import com.pratham.foundation.database.domain.Session;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.database.domain.StudentAndGroup_BottomFragmentModal;
import com.pratham.foundation.interfaces.SplashInterface;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.admin_panel.andmin_login_new.AdminConsoleActivityNew_;
import com.pratham.foundation.ui.admin_panel.andmin_login_new.enrollmentid.AddEnrollmentId_;
import com.pratham.foundation.ui.bottom_fragment.add_student.AddStudentFragment;
import com.pratham.foundation.ui.selectSubject.SelectSubject_;
import com.pratham.foundation.ui.splash_activity.SplashActivity;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.ui.splash_activity.SplashActivity.fragmentBottomOpenFlg;
import static com.pratham.foundation.utility.FC_Constants.APP_VERSION;
import static com.pratham.foundation.utility.FC_Constants.INDIVIDUAL_MODE;
import static com.pratham.foundation.utility.FC_Constants.SPLASH_OPEN;


@EFragment(R.layout.student_list_fragment)
public class BottomStudentsFragment extends BottomSheetDialogFragment
        implements BottomStudentsContract.BottomStudentsView, BottomStudentsContract.StudentClickListener,
        SplashInterface {

    @Bean(BottomStudentsPresenter.class)
    BottomStudentsContract.BottomStudentsPresenter presenter;

    @ViewById(R.id.students_recyclerView)
    RecyclerView rl_students;
    @ViewById(R.id.add_student)
    Button add_student;
    @ViewById(R.id.btn_download_all_data)
    Button btn_download_all_data;
    @ViewById(R.id.pratham_login)
    ImageView pratham_login;
    @ViewById(R.id.go_next)
    ImageView go_next;
    @ViewById(R.id.version_tv)
    TextView version_tv;

    private ArrayList avatars = new ArrayList();
    private List<StudentAndGroup_BottomFragmentModal> fragmentModalsList;
    StudentsAdapter adapter;
    String groupID, groupName;
    public static boolean groupClicked = false;
    Gson gson;
    Context context;

    @AfterViews
    public void initialize() {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        presenter.setView(BottomStudentsFragment.this);
        btn_download_all_data.setVisibility(View.GONE);
        fragmentModalsList = new ArrayList<>();
        gson = new Gson();
        hideSystemUI();
        groupClicked = false;
        FastSave.getInstance().saveBoolean(FC_Constants.PRATHAM_STUDENT, false);
        context = getActivity();
//        if (ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork())
//            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
//                btn_download_all_data.setVisibility(View.VISIBLE);
//            }
        presenter.showStudents();
        PackageInfo pInfo = null;
        String verCode = "";
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            verCode = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        FastSave.getInstance().saveString(APP_VERSION, verCode);
        version_tv.setText("v"+verCode);
    }

    @Click(R.id.btn_Enrollment)
    public void addEnrollmentId() {
        Intent intent = new Intent(getActivity(), AddEnrollmentId_.class);
        startActivityForResult(intent, 1);
    }

    private void hideSystemUI() {
        Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    @UiThread
    @Override
    public void setStudentList(List<StudentAndGroup_BottomFragmentModal> fragmentModalsList) {
        this.fragmentModalsList.addAll(fragmentModalsList);
    }

    @UiThread
    @Override
    public void clearList() {
        this.fragmentModalsList.clear();
    }


    @UiThread
    @Override
    public void notifyStudentAdapter() {
        if (adapter == null) {
            adapter = new StudentsAdapter(getActivity(), this, fragmentModalsList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false);
            rl_students.setLayoutManager(mLayoutManager);
            rl_students.setAdapter(adapter);
        } else
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        SplashActivity.fragmentBottomPauseFlg = true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        try {
            fragmentBottomOpenFlg = false;
            EventMessage eventMessage = new EventMessage();
            eventMessage.setMessage(FC_Constants.BOTTOM_FRAGMENT_CLOSED);
            EventBus.getDefault().post(eventMessage);
//            Objects.requireNonNull(getActivity()).onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ProgressLayout progressLayout;
    ProgressBar roundProgress;
    TextView dialog_file_name;
    ImageView iv_file_trans;
    Dialog progress;

    @Click(R.id.btn_download_all_data)
    public void onBtnDownload() {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        if (ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                setProgressDailog();
                new DownloadData().doInBackground();
            } else {
                Toast.makeText(getActivity(), "Connect to Kolibri", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    public void setProgressDailog() {
        progress = new Dialog(context);
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(progress.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress.setContentView(R.layout.dialog_file_downloading);
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
        progressLayout = progress.findViewById(R.id.dialog_progressLayout);
        roundProgress = progress.findViewById(R.id.dialog_roundProgress);
        dialog_file_name = progress.findViewById(R.id.dialog_file_name);
        iv_file_trans = progress.findViewById(R.id.iv_file_trans);
        iv_file_trans.setImageResource(R.drawable.splash_group);
        dialog_file_name.setText("Downloading please wait");
        progressLayout.setCurProgress(0);
        progress.show();
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    @Subscribe
    public void messageRecieved(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase("reload"))
                presenter.showStudents();
            if (message.getMessage().equalsIgnoreCase(FC_Constants.DATA_FILE_PROGRESS)) {
                if (progress != null)
                    progressLayout.setCurProgress((int) message.getProgress());
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.UNZIPPING_DATA_FILE)) {
                if (progress != null) {
                    dialog_file_name.setText("Unzipping please wait..");
                }
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.DATA_ZIP_COMPLETE)) {
                Log.d("pushorassign", "Zipping Completed.. ");
                if (progress != null)
                    dialog_file_name.setText("Zipping Completed..");
                new Handler().postDelayed(() -> {
                    dialog_file_name.setText("Loading Data..");
                    Log.d("pushorassign", "Loading Data..");
                    presenter.populateDB();
                }, 1000);
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.DATA_DOWNLOAD_ERROR)) {
                if (progress != null)
                    dialog_file_name.setText("Download Error..");
                new Handler().postDelayed(() -> progress.dismiss(), 1000);
            }
        }
    }

    @Click(R.id.go_next)
    public void setNext(View v) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        ArrayList<Student> checkedStds = new ArrayList<>();
        Student checkedStd = new Student();
        for (int i = 0; i < fragmentModalsList.size(); i++) {
            if (fragmentModalsList.get(i).isChecked()) {
                checkedStd.setStudentID(fragmentModalsList.get(i).getStudentID());
                checkedStd.setAvatarName(fragmentModalsList.get(i).getAvatarName());
                checkedStd.setFullName(fragmentModalsList.get(i).getFullName());
                checkedStd.setGroupId(fragmentModalsList.get(i).getGroupId());
                checkedStds.add(checkedStd);
            }
        }
        if (checkedStds.size() > 0) {
            //todo remove comment
            //   ApplicationClass.bubble_mp.start();
            endSession();
            startSession(checkedStds);
            //todo remove#
            //startActivity(new Intent(getActivity(), HomeActivity_.class));
//            startActivity(new Intent(getActivity(), SelectSubject_.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            gotoNext();
        } else {
            Toast.makeText(getContext(), "Please Select Students !", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void startSession(final ArrayList<Student> stud) {
        String newCurrentSession;

        try {
            StatusDao statusDao = AppDatabase.getDatabaseInstance(getContext()).getStatusDao();
            newCurrentSession = "" + UUID.randomUUID().toString();
            String currentSession = newCurrentSession;
            FastSave.getInstance().saveString(FC_Constants.CURRENT_SESSION, currentSession);
            statusDao.updateValue("CurrentSession", "" + currentSession);

            Session startSesion = new Session();
            startSesion.setSessionID("" + currentSession);
            String timerTime = FC_Utility.getCurrentDateTime();
            Log.d("doInBackground", "--------------------------------------------doInBackground : " + timerTime);
            startSesion.setFromDate(timerTime);
            startSesion.setToDate("NA");
            startSesion.setSentFlag(0);
            AppDatabase.getDatabaseInstance(getContext()).getSessionDao().insert(startSesion);
            Log.d("ChildAttendence", "Student Count: " + stud.size());

            Attendance attendance = new Attendance();
            for (int i = 0; i < stud.size(); i++) {
                FastSave.getInstance().saveString(FC_Constants.CURRENT_API_STUDENT_ID, "" + stud.get(i).getStudentID());
                attendance.setSessionID("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                attendance.setStudentID("" + stud.get(i).getStudentID());
                attendance.setDate(FC_Utility.getCurrentDateTime());
                attendance.setGroupID(groupID);
                attendance.setSentFlag(0);
                AppDatabase.getDatabaseInstance(getContext()).getAttendanceDao().insert(attendance);
                Log.d("ChildAttendence", "currentSession : " + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, "") + "  StudentId: " + stud.get(i).getStudentID());
            }

            String currentStudentID = "";
            FastSave.getInstance().saveString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE);
            currentStudentID = groupID;
            /*
                currentStudentID = stud.get(0).getStudentID();
                String currentStudName = stud.get(0).getFullName();
                FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_NAME, currentStudName);
*/
            FastSave.getInstance().saveString(FC_Constants.CURRENT_SESSION, "" + currentSession);
            FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_NAME, "" + groupName);
            FastSave.getInstance().saveString(FC_Constants.CURRENT_API_STUDENT_ID, "" + groupID);
            FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_ID, currentStudentID);
            BackupDatabase.backup(getContext());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.add_student)
    public void addStudent() {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        SplashActivity.fragmentAddStudentOpenFlg = true;
        AddStudentFragment addStudentFragment = AddStudentFragment.newInstance(this);
        addStudentFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),
                AddStudentFragment.class.getSimpleName());
    }

    @Click(R.id.pratham_login)
    public void openPrathamLogin() {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(context, AdminConsoleActivityNew_.class));
//        SplashActivity.fragmentAddStudentOpenFlg = true;
//        AddStudentFragment addStudentFragment = AddStudentFragment.newInstance(this);
//        addStudentFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),
//                AddStudentFragment.class.getSimpleName());
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d("BottomSheetCancel", "onCancel: aaaaaaaaaaaaaaaaaaaa");
    }

    @Override
    public void onResume() {
        super.onResume();
        SplashActivity.fragmentBottomPauseFlg = false;
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public ProgressDialog progressDialog;

    @UiThread
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Loading... Please wait...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();

    }

    @UiThread
    @Override
    public void dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void dismissProgressDialog2() {
        try {
            new Handler().postDelayed(() -> {
                if (progress != null && progress.isShowing())
                    progress.dismiss();
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void endSession() {
        try {
//            String curSession = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("CurrentSession");
            String curSession = FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, "" + "NA");
            AppDatabase.getDatabaseInstance(context).getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
            BackupDatabase.backup(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void onStudentClick(StudentAndGroup_BottomFragmentModal bottomFragmentModal, int position) {
        if (groupClicked) {
            FastSave.getInstance().saveBoolean(FC_Constants.PRATHAM_STUDENT, true);
            if (bottomFragmentModal.isChecked())
                fragmentModalsList.get(position).setChecked(false);
            else
                fragmentModalsList.get(position).setChecked(true);
            adapter.notifyItemChanged(position);
        } else {
            try {
                ButtonClickSound.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if (!FastSave.getInstance().getBoolean(SPLASH_OPEN, false))
                endSession();
//            EventMessage eventMessage = new EventMessage();
//            eventMessage.setMessage(FC_Constants.BOTTOM_FRAGMENT_END_SESSION);
//            EventBus.getDefault().post(eventMessage);
            showProgressDialog();
            if(!bottomFragmentModal.getGroupId().equalsIgnoreCase("PS"))
                FastSave.getInstance().saveBoolean(FC_Constants.PRATHAM_STUDENT, true);
            else
                FastSave.getInstance().saveBoolean(FC_Constants.PRATHAM_STUDENT, false);

            String currentSession = "" + UUID.randomUUID().toString();
            FastSave.getInstance().saveString(FC_Constants.LOGIN_MODE, INDIVIDUAL_MODE);
            FastSave.getInstance().saveString(FC_Constants.CURRENT_SESSION, "" + currentSession);
            FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_ID, "" + bottomFragmentModal.getStudentID());
            FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_NAME, "" + bottomFragmentModal.getFullName());
            FastSave.getInstance().saveString(FC_Constants.CURRENT_API_STUDENT_ID, "" + bottomFragmentModal.getStudentID());
//            FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_PROGRAM_ID, "" + bottomFragmentModal.getProgramID());
            presenter.updateStudentData();
            FastSave.getInstance().saveBoolean(SPLASH_OPEN, false);
        }
    }

    @UiThread
    @Override
    public void onGroupClick(String studentName, String studentId) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        groupClicked = true;
        go_next.setVisibility(View.VISIBLE);
        groupID = studentId;
        groupName = studentName;
//        FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_PROGRAM_ID, "" + programID);
        presenter.getStudentsFromGroup(studentId);
/*        if (!SPLASH_OPEN)
            endSession();
//        EventMessage eventMessage = new EventMessage();
//        eventMessage.setMessage(FC_Constants.BOTTOM_FRAGMENT_END_SESSION);
//        EventBus.getDefault().post(eventMessage);
        showProgressDialog();
        String currentSession = "" + UUID.randomUUID().toString();
        FastSave.getInstance().saveString(FC_Constants.LOGIN_MODE, GROUP_MODE);
        FastSave.getInstance().saveString(FC_Constants.CURRENT_SESSION, "" + currentSession);
        FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_ID, "" + studentId);
        FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_NAME, "" + studentName);
        FastSave.getInstance().saveString(FC_Constants.CURRENT_API_STUDENT_ID, "" + studentId);
        presenter.updateStudentData();
        SPLASH_OPEN = false;*/
    }

    @UiThread
    @Override
    public void gotoNext() {
        dismissProgressDialog();
        startActivity(new Intent(getActivity(), SelectSubject_.class));
/*        try {
            if (bgMusic != null && bgMusic.isPlaying()) {
                bgMusic.stop();
                bgMusic.setLooping(false);
                bgMusic.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void onChildAdded() {
        presenter.showStudents();
    }
}
