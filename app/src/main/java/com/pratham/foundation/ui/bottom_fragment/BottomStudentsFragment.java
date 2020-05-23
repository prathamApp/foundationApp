package com.pratham.foundation.ui.bottom_fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.async.DownloadData;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.interfaces.SplashInterface;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.services.shared_preferences.FastSave;
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

    private ArrayList avatars = new ArrayList();
    private List<Student> studentList;
    StudentsAdapter adapter;
    Gson gson;
    Context context;

    @AfterViews
    public void initialize() {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        presenter.setView(BottomStudentsFragment.this);
        btn_download_all_data.setVisibility(View.GONE);
        studentList = new ArrayList<>();
        gson = new Gson();
        hideSystemUI();
        context = getActivity();
        if (ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork())
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_KOLIBRI_HOTSPOT)) {
                btn_download_all_data.setVisibility(View.VISIBLE);
            }
        presenter.showStudents();
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
    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    @UiThread
    @Override
    public void notifyStudentAdapter() {
        if (adapter == null) {
            adapter = new StudentsAdapter(getActivity(), this, studentList, avatars);
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
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_KOLIBRI_HOTSPOT)) {
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
        Glide.with(this).load(R.drawable.splash_group).into(iv_file_trans);
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
            String curSession = AppDatabase.appDatabase.getStatusDao().getValue("CurrentSession");
            String toDateTemp = AppDatabase.appDatabase.getSessionDao().getToDate(curSession);
            if (toDateTemp.equalsIgnoreCase("na")) {
                AppDatabase.appDatabase.getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
            }
            BackupDatabase.backup(getActivity());
        } catch (Exception e) {
            String curSession = AppDatabase.appDatabase.getStatusDao().getValue("CurrentSession");
            AppDatabase.appDatabase.getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void onStudentClick(String studentName, String studentId) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (!SPLASH_OPEN)
            endSession();
//        EventMessage eventMessage = new EventMessage();
//        eventMessage.setMessage(FC_Constants.BOTTOM_FRAGMENT_END_SESSION);
//        EventBus.getDefault().post(eventMessage);
        showProgressDialog();
        String currentSession = "" + UUID.randomUUID().toString();
        FastSave.getInstance().saveString(FC_Constants.LOGIN_MODE, INDIVIDUAL_MODE);
        FastSave.getInstance().saveString(FC_Constants.CURRENT_SESSION, "" + currentSession);
        FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_ID, "" + studentId);
        FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_NAME, "" + studentName);
        presenter.updateStudentData();
        SPLASH_OPEN = false;
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
