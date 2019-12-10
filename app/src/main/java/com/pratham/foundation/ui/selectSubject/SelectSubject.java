package com.pratham.foundation.ui.selectSubject;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.home_screen.HomeActivity_;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.utility.FC_Constants.currentLevel;
import static com.pratham.foundation.utility.FC_Constants.currentStudentName;
import static com.pratham.foundation.utility.FC_Constants.currentSubjectFolder;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;


@EActivity(R.layout.activity_select_subject)
public class SelectSubject extends BaseActivity implements
        SelectSubjectContract.View, SelectSubjectContract.itemClicked {

    @Bean(SelectSubjectPresenter.class)
    SelectSubjectContract.Presenter presenter;

    @ViewById(R.id.subject_recycler)
    RecyclerView subject_recycler;
    @ViewById(R.id.name)
    TextView name;
    private Context context;
    SelectSubjectAdapter subjectAdapter;
    String studName;

    @AfterViews
    protected void initiate() {
        context = SelectSubject.this;
        Configuration config = getResources().getConfiguration();
        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
        List<ContentTable> subjectList = presenter.getSubjectList();

        if(FC_Constants.LOGIN_MODE.contains("group"))
            studName = currentStudentName;
        else
            studName = currentStudentName.split(" ")[0];
        name.setText(getResources().getString(R.string.Welcome)+" "+studName+".");
        subjectAdapter = new SelectSubjectAdapter(this, subjectList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        subject_recycler.setLayoutManager(mLayoutManager);

        int dp = 12;
        if (FC_Constants.TAB_LAYOUT)
            dp = 20;
        subject_recycler.addItemDecoration(new GridSpacingItemDecoration(
                2, dpToPx(this,dp), true));
        subject_recycler.setItemAnimator(new DefaultItemAnimator());
        subject_recycler.setAdapter(subjectAdapter);
    }

    private int getScreenWidthDp() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

    @Override
    public void onItemClicked(ContentTable contentTableObj) {
        FC_Constants.currentSubject = contentTableObj.getNodeTitle();
        currentLevel = 0;

        if (contentTableObj.getNodeTitle().equals("Science")) {
            currentSubjectFolder = "Science";
        } else if (contentTableObj.getNodeTitle().equals("Maths")) {
            currentSubjectFolder = "Maths";
        } else if (contentTableObj.getNodeTitle().equals("English")) {
            currentSubjectFolder = "English";
        } else if (contentTableObj.getNodeTitle().equals("H Science")) {
            currentSubjectFolder = "H_Science";
        }else
            currentSubjectFolder = "LS_Science";

        gameFolderPath = "/.FCA/"+currentSubjectFolder+"/Game";
        FastSave.getInstance().saveString(FC_Constants.CURRENT_FOLDER_NAME, currentSubjectFolder);
        FastSave.getInstance().saveString(FC_Constants.CURRENT_ROOT_NODE, contentTableObj.getNodeId());
        Intent intent = new Intent(context, HomeActivity_.class);
        intent.putExtra("nodeId", contentTableObj.getNodeId());
        intent.putExtra("nodeTitle", contentTableObj.getNodeTitle());
        context.startActivity(intent);
    }

    @Click(R.id.btn_back)
    public void pressedBackButton(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        exitDialog();
    }

    @SuppressLint("SetTextI18n")
    private void exitDialog() {
        Dialog dialog = new Dialog(SelectSubject.this,R.style.ExitDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.lottie_exit_dialog);
/*      Bitmap map=FC_Utility.takeScreenShot(HomeActivity.this);
        Bitmap fast=FC_Utility.fastblur(map, 20);
        final Drawable draw=new BitmapDrawable(getResources(),fast);
        dialog.getWindow().setBackgroundDrawable(draw);*/
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

//        TextView dia_title = dialog.findViewById(R.id.dia_title);
//        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_no = dialog.findViewById(R.id.dia_btn_no);
        TextView dia_btn_yes = dialog.findViewById(R.id.dia_btn_yes);

//        dia_btn_green.setText (getResources().getString(R.string.Restart));
//        dia_btn_yes.setText   (getResources().getString(R.string.Exit));
//        dia_btn_no.setText(getResources().getString(R.string.Cancel));

//        dia_btn_green.setOnClickListener(v -> {
//            finishAffinity();
//            context.startActivity(new Intent(context, SplashActivity_.class));
//            dialog.dismiss();
//        });

        dia_btn_yes.setOnClickListener(v -> {
            endSession(this);
            finishAffinity();
            dialog.dismiss();
        });

        dia_btn_no.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

}
