package com.pratham.foundation.ui.app_home.learning_fragment.attendance_bottom_fragment;

import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.utility.FC_Constants.currentLevel;
import static com.pratham.foundation.utility.FC_Constants.currentSubjectFolder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@EFragment(R.layout.attendance_bottom_fragment)
public class AttendanceBottomFragment extends BottomSheetDialogFragment
        implements AttendanceStudentsContract.AttendanceStudentsView, AttendanceStudentsContract.AttendanceStudentClickListener{

    @Bean(AttendanceStudentsPresenter.class)
    AttendanceStudentsContract.AttendanceStudentsPresenter presenter;

    @ViewById(R.id.students_recyclerView)
    RecyclerView rl_students;

    private final ArrayList avatars = new ArrayList();
    private List<Student> fragmentModalsList;
    AttendanceStudentsAdapter adapter;
    Gson gson;
    Context context;

    @AfterViews
    public void initialize() {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        presenter.setView(AttendanceBottomFragment.this);
        fragmentModalsList = new ArrayList<>();
        gson = new Gson();
        hideSystemUI();
        context = getActivity();
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
    public void setStudentList(List<Student> fragmentModalsList) {
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
            adapter = new AttendanceStudentsAdapter(getActivity(), this, fragmentModalsList);
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

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d("BottomSheetCancel", "onCancel: aaaaaaaaaaaaaaaaaaaa");
    }

    @Override
    public void onResume() {
        super.onResume();
    }


/*
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
*/

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


    @UiThread
    @Override
    public void onStudentClick(Student bottomFragmentModal, int position) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            String profileName = "";
            profileName = AppDatabase.getDatabaseInstance(context).getStudentDao().getFullName(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "NA"));

            Bundle bundle = new Bundle();
            FastSave.getInstance().getString(FC_Constants.CURRENT_FOLDER_NAME, currentSubjectFolder);

            bundle.putString("appName", "" + getResources().getString(R.string.app_name));
            bundle.putString("studentId", "" + ((FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals("")
                    || FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "").equals(null)) ?"NA"
                    :FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "")));
            bundle.putString("studentName", "" + profileName);
            bundle.putString("subjectName", "" + FastSave.getInstance().getString(FC_Constants.CURRENT_SUBJECT, ""));
            bundle.putString("subjectLanguage", "" + FC_Constants.AssLang);
            bundle.putString("examId", "" + FC_Constants.examId);
            bundle.putString("subjectLevel", "" + currentLevel);
            bundle.putString("studentGroupId", "NA");
//            Intent launchIntent = new Intent("com.doedelhi.pankhpractice.ui.choose_assessment.ChooseAssessmentActivity_");
            Intent launchIntent = new Intent("com.pratham.assessment.ui.choose_assessment.science.ScienceAssessmentActivity_");
            //Intent launchIntent = Objects.requireNonNull(getActivity()).getPackageManager()
            //        .getLaunchIntentForPackage("com.doedelhi.pankhpractice");
            Objects.requireNonNull(launchIntent).putExtras(bundle);
            startActivityForResult(launchIntent, FC_Constants.APP_INTENT_REQUEST_CODE);
            // null pointer check in case package name was not found
        } catch (Exception e) {
            downloadAssessmentAppDialog();
            e.printStackTrace();
        }
    }

    BlurPopupWindow fcDialog;

    @SuppressLint("SetTextI18n")
    @UiThread
    public void downloadAssessmentAppDialog() {
        try {
            fcDialog = new BlurPopupWindow.Builder(context)
                    .setContentView(R.layout.fc_custom_dialog)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnTouchBackground(false)
                    .setDismissOnClickBack(false)
                    .bindClickListener(v -> {
                        new Handler().postDelayed(() -> {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.pratham.assessment")));
                            fcDialog.dismiss();
                        }, 200);
                    }, R.id.dia_btn_green)
                    .bindClickListener(v -> {
                        new Handler().postDelayed(() -> {
                            fcDialog.dismiss();
                        }, 200);
                    }, R.id.dia_btn_red)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(10)
                    .setTintColor(0x30000000)
                    .build();

            TextView dia_title = fcDialog.findViewById(R.id.dia_title);
            Button dia_btn_yellow = fcDialog.findViewById(R.id.dia_btn_yellow);
            Button dia_btn_green = fcDialog.findViewById(R.id.dia_btn_green);
            Button dia_btn_red = fcDialog.findViewById(R.id.dia_btn_red);
            dia_btn_yellow.setVisibility(View.GONE);
            LottieAnimationView dl_lottie_view = fcDialog.findViewById(R.id.dl_lottie_view);

            dl_lottie_view.setAnimation("playstore_lottie.json");
            dl_lottie_view.playAnimation();

            dia_btn_red.setText(context.getResources().getString(R.string.Cancel));
            dia_title.setText("Please Download Assessment App From Google Play Store");
            dia_btn_green.setText(getResources().getString(R.string.Okay));

            fcDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
