package com.pratham.foundation.ui.selectSubject;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.app_home.HomeActivity_;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.ApplicationClass.BackBtnSound;
import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.utility.FC_Constants.currentLevel;
import static com.pratham.foundation.utility.FC_Constants.currentSubjectFolder;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;

@EActivity(R.layout.activity_select_subject)
public class SelectSubject extends BaseActivity implements
        SelectSubjectContract.SubjectView, SelectSubjectContract.ItemClicked,
        SelectSubjectContract.AppLanguageSpinnerListner {

    @Bean(SelectSubjectPresenter.class)
    SelectSubjectContract.SubjectPresenter presenter;

    @ViewById(R.id.subject_recycler)
    RecyclerView subject_recycler;
    @ViewById(R.id.name)
    TextView name;
    private Context context;
    SelectSubjectAdapter subjectAdapter;
    String studName;

    @AfterViews
    protected void initiate() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        context = SelectSubject.this;
        Configuration config = getResources().getConfiguration();
        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
        List<ContentTable> subjectList = presenter.getSubjectList();

        presenter.setView(SelectSubject.this);

        if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).contains("group"))
            studName = FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_NAME, "");
        else
            studName = FastSave.getInstance().getString(
                    FC_Constants.CURRENT_STUDENT_NAME, "").split(" ")[0];
        name.setText(/*getResources().getString(R.string.Welcome) + " " + */studName + ".");
        subjectAdapter = new SelectSubjectAdapter(this, subjectList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        subject_recycler.setLayoutManager(mLayoutManager);

        int dp = 12;
        if (FC_Constants.TAB_LAYOUT)
            dp = 20;

        subject_recycler.addItemDecoration(new GridSpacingItemDecoration(
                1, dpToPx(this, dp), true));
        subject_recycler.setItemAnimator(new DefaultItemAnimator());
        subject_recycler.setAdapter(subjectAdapter);
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onItemClicked(ContentTable contentTableObj) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        currentLevel = 0;
        String currentSubject = contentTableObj.getNodeKeywords();
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

        gameFolderPath = "/.FCA/" + currentSubjectFolder + "/Game";
        FastSave.getInstance().saveString(FC_Constants.CURRENT_SUBJECT, currentSubject);
        FastSave.getInstance().saveString(FC_Constants.CURRENT_FOLDER_NAME, currentSubjectFolder);
        FastSave.getInstance().saveString(FC_Constants.CURRENT_ROOT_NODE, contentTableObj.getNodeId());
        Intent intent = new Intent(context, HomeActivity_.class);
        intent.putExtra("nodeId", contentTableObj.getNodeId());
        intent.putExtra("nodeTitle", contentTableObj.getNodeTitle());
        startActivity(intent);
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
        exitDialog();
    }

    @Click(R.id.ib_langChange)
    public void langChangeButtonClick() {
        presenter.getLanguageFromApi();
    }

    BlurPopupWindow langDialog;
    String language, currLang;

    @SuppressLint("SetTextI18n")
    @Override
    @UiThread
    public void showLanguageSelectionDialog(List<ContentTable> serverContentList) {
        langDialog = new BlurPopupWindow.Builder(context)
                .setContentView(R.layout.fc_custom_language_dialog)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(false)
                .bindClickListener(v -> {
                    new Handler().postDelayed(() -> {
                        onAppSpinnerLanguageChanged(language);
                        langDialog.dismiss();
                        langDialog = null;
                    }, 200);
                }, R.id.dia_btn_green)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();

        TextView dia_title = langDialog.findViewById(R.id.dia_title);
        Button dia_btn_green = langDialog.findViewById(R.id.dia_btn_green);
        Spinner lang_spinner = langDialog.findViewById(R.id.lang_spinner);
        dia_btn_green.setText("OK");

        currLang = "" + FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, "Hindi");
        dia_title.setText("Current Language : " + currLang);

        ArrayList<String> langStrings =new ArrayList<>();
        for(int j=0; j<serverContentList.size(); j++)
        langStrings.add(""+serverContentList.get(j).getNodeTitle());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, R.layout.custom_spinner,
                langStrings);
//                context.getResources().getStringArray(R.array.certificate_Languages));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang_spinner.setAdapter(dataAdapter);
//        String[] languages = getResources().getStringArray(R.array.certificate_Languages);
//        for (int i = 0; i < languages.length; i++) {
//            if (currLang.equalsIgnoreCase(languages[i])) {
//                lang_spinner.setSelection(i);
//                break;
//            }
//        }

        lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language = serverContentList.get(position).getNodeTitle();
                String nodeId = serverContentList.get(position).getNodeId();
                Toast.makeText(context, "nodeId : "+nodeId, Toast.LENGTH_SHORT).show();
                FastSave.getInstance().saveString(FC_Constants.APP_LANGUAGE, ""+language);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        langDialog.show();
    }



    @Override
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
