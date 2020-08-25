package com.pratham.foundation.ui.selectSubject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.app_home.HomeActivity_;
import com.pratham.foundation.ui.select_language_fragment.SelectLanguageFragment;
import com.pratham.foundation.ui.select_language_fragment.SelectLanguageFragment_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.BackBtnSound;
import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.utility.FC_Constants.APP_LANGUAGE_SELECTED;
import static com.pratham.foundation.utility.FC_Constants.UPDATE_AVAILABLE;
import static com.pratham.foundation.utility.FC_Constants.currentLevel;
import static com.pratham.foundation.utility.FC_Constants.currentSubjectFolder;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;

@EActivity(R.layout.activity_select_subject)
public class SelectSubject extends BaseActivity implements
        SelectSubjectContract.SubjectView, SelectSubjectContract.ItemClicked{

    @Bean(SelectSubjectPresenter.class)
    SelectSubjectContract.SubjectPresenter presenter;

    @ViewById(R.id.subject_recycler)
    RecyclerView subject_recycler;
    @ViewById(R.id.rl_ss_main)
    RelativeLayout rl_ss_main;
    @ViewById(R.id.rl_act)
    RelativeLayout rl_act;
    @ViewById(R.id.name)
    TextView name;
    @ViewById(R.id.name_welcome)
    TextView name_welcome;
    @ViewById(R.id.subject)
    TextView subject;
    @ViewById(R.id.tv_update)
    TextView tv_update;
    private Context context;
    SelectSubjectAdapter subjectAdapter;
    String studName;
    List<ContentTable> subjectList;

    @AfterViews
    protected void initiate() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        context = SelectSubject.this;
        subjectList = new ArrayList<>();
        Configuration config = getResources().getConfiguration();
        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
        presenter.setView(SelectSubject.this);
        tv_update.setVisibility(View.GONE);
        showLoader();

        if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).contains("group"))
            studName = FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_NAME, "");
        else
            studName = FastSave.getInstance().getString(
                    FC_Constants.CURRENT_STUDENT_NAME, "").split(" ")[0];
        name.setText(/*getResources().getString(R.string.Welcome) + " " + */studName + ".");

//        for (Locale locale : Locale.getAvailableLocales()) {
//            Log.d("LOCALES", locale.getLanguage() + "_" + locale.getCountry() + " [" + locale.getDisplayName() + "]");
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FC_Utility.setAppLocal(this,FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI));
        subjectList.clear();
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessage(FC_Constants.CHECK_UPDATE);
        EventBus.getDefault().post(eventMessage);

        if (!FastSave.getInstance().getBoolean(APP_LANGUAGE_SELECTED, false))
            langChangeButtonClick();
        presenter.getSubjectList();
    }

    @Override
    public void initializeSubjectList(List<ContentTable> subjectList) {
        this.subjectList.clear();
        this.subjectList.addAll(subjectList);
    }

    @UiThread
    @Override
    public void notifySubjAdapter() {
        if (subjectAdapter == null) {
            subjectAdapter = new SelectSubjectAdapter(this, subjectList);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            subject_recycler.setLayoutManager(mLayoutManager);
            subject_recycler.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(this, 14), true));
            subject_recycler.setItemAnimator(new DefaultItemAnimator());
            subject_recycler.setAdapter(subjectAdapter);
        } else
            subjectAdapter.notifyDataSetChanged();
//        name_welcome.setText(getResources().getString(R.string.Welcome).toString());
//        subject.setText(getResources().getString(R.string.select_subject).toString());
    }

    private boolean loaderVisible = false;
    private CustomLodingDialog myLoadingDialog;

    @UiThread
    @Override
    public void showLoader() {
        if (!loaderVisible) {
            loaderVisible = true;
            myLoadingDialog = new CustomLodingDialog(context);
            myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(myLoadingDialog.getWindow()).
                    setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myLoadingDialog.setContentView(R.layout.loading_dialog);
            myLoadingDialog.setCanceledOnTouchOutside(false);
//        myLoadingDialog.setCancelable(false);
            myLoadingDialog.show();
        }
    }

    @UiThread
    @Override
    public void dismissLoadingDialog() {
        try {
            loaderVisible = false;
            new Handler().postDelayed(() -> {
                if (myLoadingDialog != null && myLoadingDialog.isShowing())
                    myLoadingDialog.dismiss();
            }, 950);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Click(R.id.tv_update)
    public void updateClicked(){
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessage(FC_Constants.START_UPDATE);
        EventBus.getDefault().post(eventMessage);
    }

    @Override
    public void onItemClicked(ContentTable contentTableObj) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        currentLevel = 0;
        String currentSubject;
        if (contentTableObj.getSubject() != null)
            currentSubject = contentTableObj.getSubject();
        else
            currentSubject = "English";
/*
        if (contentTableObj.getNodeKeywords().equals("Science")) {
            currentSubjectFolder = "Science";
        } else if (contentTableObj.getNodeTitle().equals("Maths") ||
                contentTableObj.getNodeKeywords().equals("Maths")) {
            currentSubjectFolder = "Maths";
        } else if (contentTableObj.getNodeTitle().equals("English") ||
                contentTableObj.getNodeKeywords().equals("English")) {
            currentSubjectFolder = "English";
        } else if (contentTableObj.getNodeTitle().equals("H Science") ||
                contentTableObj.getNodeKeywords().equals("H Science")) {
            currentSubjectFolder = "H_Science";
        } else
            currentSubjectFolder = "LS_Science";
*/

        currentSubjectFolder = "" + currentSubject;
        gameFolderPath = "/.FCA/" + currentSubjectFolder + "/Game";
        FastSave.getInstance().saveString(FC_Constants.CURRENT_SUBJECT, currentSubject);
        FastSave.getInstance().saveString(FC_Constants.CURRENT_FOLDER_NAME, currentSubjectFolder);
        FastSave.getInstance().saveString(FC_Constants.CURRENT_ROOT_NODE, contentTableObj.getNodeId());
        Intent intent = new Intent(context, HomeActivity_.class);
        intent.putExtra("nodeId", contentTableObj.getNodeId());
        intent.putExtra("nodeTitle", contentTableObj.getNodeTitle());
        startActivity(intent);
//        FastSave.getInstance().saveString(FC_Constants.APP_LANGUAGE, FC_Constants.MARATHI);
        String a = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI);
        Log.d("INSTRUCTIONFRAG", "Select Subj: "+a);
        FC_Utility.setAppLocal(this,a);
//        context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SelectSubject.this).toBundle());
    }

    @Click(R.id.btn_back)
    public void pressedBackButton() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        try {
            BackBtnSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments > 0) {
            getSupportFragmentManager().popBackStack();
            if(fragments==1){
                rl_act.setVisibility(View.VISIBLE);
                onAppSpinnerLanguageChanged(FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, ""));
                FC_Utility.setAppLocal(this,FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI));
                presenter.clearSubjList();
                presenter.getSubjectList();
                name_welcome.setText(getResources().getString(R.string.Welcome));
                subject.setText(getResources().getString(R.string.select_subject));
            }
        } else {
            exitDialog();
        }
    }

    @Click(R.id.ib_langChange)
    public void langChangeButtonClick() {
        rl_act.setVisibility(View.GONE);
        FC_Utility.showFragment((Activity) context, new SelectLanguageFragment_(), R.id.rl_ss_main,
                null, SelectLanguageFragment.class.getSimpleName());
//        showLoader();
//        presenter.getLanguage();
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageReceived(EventMessage message) {
        if (message.getMessage().equalsIgnoreCase(UPDATE_AVAILABLE)) {
            tv_update.setVisibility(View.VISIBLE);
        }
    }
            BlurPopupWindow langDialog;
    String language, currLang, languageNodeId;

    public void onAppSpinnerLanguageChanged(String selectedLanguage) {
        language = selectedLanguage;
    }

    BlurPopupWindow exitDialog;

    @UiThread
    @SuppressLint("SetTextI18n")
    public void exitDialog() {
        exitDialog = new BlurPopupWindow.Builder(this)
                .setContentView(R.layout.lottie_exit_dialog)
                .bindClickListener(v -> {
                    endSession(this);
                    exitDialog.dismiss();
                    new Handler().postDelayed(this::finishAffinity, 200);
                }, R.id.dia_btn_yes)
                .bindClickListener(v -> exitDialog.dismiss(), R.id.dia_btn_no)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(true)
                .setDismissOnClickBack(true)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();
        exitDialog.show();
    }
}
