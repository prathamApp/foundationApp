package com.pratham.foundation.ui.bottom_fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.interfaces.SplashInterface;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.ui.bottom_fragment.add_student.AddStudentFragment;
import com.pratham.foundation.ui.selectSubject.SelectSubject_;
import com.pratham.foundation.ui.splash_activity.SplashActivity;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.pratham.foundation.ui.splash_activity.SplashActivity.bgMusic;
import static com.pratham.foundation.utility.FC_Constants.currentStudentName;


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

    @AfterViews
    public void initialize() {
        presenter.setView(BottomStudentsFragment.this);
        btn_download_all_data.setVisibility(View.GONE);
        studentList = new ArrayList<>();
        gson = new Gson();

        if (ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork())
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_KOLIBRI_HOTSPOT)) {
                btn_download_all_data.setVisibility(View.VISIBLE);
            }
        presenter.showStudents();
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
            getActivity().onBackPressed();
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
        if (ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_KOLIBRI_HOTSPOT)) {
                setProgressDailog();
                new DownloadData().doInBackground();
            } else {
                Toast.makeText(getActivity(), "Connect to Kolibri", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @UiThread
    public void setProgressDailog() {
        progress = new Dialog(getActivity());
        progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        SplashActivity.fragmentAddStudentOpenFlg = true;
        AddStudentFragment addStudentFragment = AddStudentFragment.newInstance(this);
        addStudentFragment.show(getActivity().getSupportFragmentManager(), AddStudentFragment.class.getSimpleName());
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
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading... Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Background
    @Override
    public void dismissProgressDialog() {
        try {
            if (progressDialog != null)
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void dismissProgressDialog2() {
        try {
            if (progress != null)
                progress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void onStudentClick(String studentName, String studentId) {
        showProgressDialog();
        FC_Constants.currentStudentID = studentId;
        currentStudentName = studentName;
        FC_Constants.currentSession = "" + UUID.randomUUID().toString();
        presenter.updateStudentData();
    }

    @UiThread
    @Override
    public void gotoNext() {
        dismissProgressDialog();
        Intent intent = new Intent(getActivity(), SelectSubject_.class);
        intent.putExtra("studName", currentStudentName);
        startActivity(intent);
        try {
            if (bgMusic != null && bgMusic.isPlaying()) {
                bgMusic.stop();
                bgMusic.setLooping(false);
                bgMusic.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getActivity().finish();
    }

    @Override
    public void onChildAdded() {
        presenter.showStudents();
    }
}
