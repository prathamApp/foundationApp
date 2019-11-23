package com.pratham.foundation.ui.home_screen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.customView.submarine_view.SubmarineItem;
import com.pratham.foundation.customView.submarine_view.SubmarineView;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.home_screen.fun.FunFragment_;
import com.pratham.foundation.ui.home_screen.learning_fragment.LearningFragment_;
import com.pratham.foundation.ui.home_screen.practice_fragment.PracticeFragment_;
import com.pratham.foundation.ui.student_profile.Student_profile_activity;
import com.pratham.foundation.ui.student_profile.Student_profile_activity_;
import com.pratham.foundation.ui.test.supervisor.SupervisedAssessmentActivity;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import github.hellocsl.cursorwheel.CursorWheelLayout;

import static com.pratham.foundation.utility.FC_Constants.BACK_PRESSED;
import static com.pratham.foundation.utility.FC_Constants.GROUP_LOGIN;
import static com.pratham.foundation.utility.FC_Constants.GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.INDIVIDUAL_MODE;
import static com.pratham.foundation.utility.FC_Constants.LEVEL_CHANGED;
import static com.pratham.foundation.utility.FC_Constants.LOGIN_MODE;
import static com.pratham.foundation.utility.FC_Constants.QR_GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.currentSubject;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_exit;

@EActivity(R.layout.activity_home)
public class HomeActivity extends BaseActivity implements LevelChanged, CursorWheelLayout.OnMenuSelectedListener {

    public static String sub_Name, sub_nodeId = "";
    @ViewById(R.id.viewpager)
    ViewPager viewpager;
    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.tv_Activity)
    TextView tv_Activity;
    @ViewById(R.id.card_progressLayout)
    public static ProgressLayout tv_progress;
    @ViewById(R.id.tabs)
    TabLayout tabLayout;
    @ViewById(R.id.header_rl)
    public static RelativeLayout header_rl;
    @ViewById(R.id.submarine)
    public static SubmarineView submarine;
    @ViewById(R.id.iv_level)
    public static ImageView iv_level;

//    @ViewById(R.id.level_circle)
//    CursorWheelLayout level_circle;

    @ViewById(R.id.profileImage)
    ImageView profileImage;
    @DrawableRes(R.drawable.home_header_1_bg)
    Drawable homeHeader1;
    @DrawableRes(R.drawable.home_header_2_bg)
    Drawable homeHeader2;
    @DrawableRes(R.drawable.home_header_3_bg)
    Drawable homeHeader3;
    @DrawableRes(R.drawable.home_header_4_bg)
    Drawable homeHeader4;
    @ViewById(R.id.floating_back)
    FloatingActionButton floating_back;
    @ViewById(R.id.floating_info)
    FloatingActionButton floating_info;

    public static boolean languageChanged = false;
    static int count = 0;
    public static LevelChanged levelChanged;

    @AfterViews
    public void initialize() {
        Configuration config = getResources().getConfiguration();
        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
        sub_nodeId = getIntent().getStringExtra("nodeId");
        sub_Name = getIntent().getStringExtra("nodeTitle");
        floating_back.setImageResource(R.drawable.ic_left_arrow_white);
        floating_info.setImageResource(R.drawable.ic_info_outline_white);
        FC_Constants.currentSelectedLanguage = FastSave.getInstance().getString(FC_Constants.LANGUAGE, "");
        setupViewPager(viewpager);
        tv_progress.setCurProgress(0);
        levelChanged = HomeActivity.this;
        count = 0;
        tabLayout.setupWithViewPager(viewpager);
        setupTabIcons();
/*        IconForm iconForm = new IconForm.Builder(this)
                .setIconSize(45)
                .setIconTint(ContextCompat.getColor(this, R.color.colorPrimary))
                .setIconScaleType(ImageView.ScaleType.CENTER)
                .build();*/
        setLevel();
        displayProfileName();
        displayProfileImage();
    }

    @Background
    public void displayProfileImage() {
        String sImage;
        try {
            if (!GROUP_LOGIN)
                sImage = AppDatabase.getDatabaseInstance(this).getStudentDao().getStudentAvatar(FC_Constants.currentStudentID);
            else
                sImage = "group_icon";
        } catch (Exception e) {
            e.printStackTrace();
            sImage = "group_icon";
        }
        setStudentProfileImage(sImage);
    }

    @Click(R.id.profileImage)
    public void loadProfile() {
        startActivity(new Intent(this, Student_profile_activity.class));
    }

    @UiThread
    public void setStudentProfileImage(String sImage) {
/*        if (sImage != null) {
            if (sImage.equalsIgnoreCase("group_icon"))
                profileImage.setImageResource(R.drawable.ic_grp_btn);
            else {
                profileImage.setImageResource(R.drawable.b2);
                switch (sImage) {
                    case "b1.png":
                        profileImage.setImageResource(R.drawable.b1);
                        break;
                    case "b2.png":
                        profileImage.setImageResource(R.drawable.b2);
                        break;
                    case "b3.png":
                        profileImage.setImageResource(R.drawable.b3);
                        break;
                    case "g1.png":
                        profileImage.setImageResource(R.drawable.g1);
                        break;
                    case "g2.png":
                        profileImage.setImageResource(R.drawable.g2);
                        break;
                    case "g3.png":
                        profileImage.setImageResource(R.drawable.g3);
                        break;
                }
            }
        }*/
    }

    @Background
    public void displayProfileName() {
        String profileName = "QR Group";
        try {
            if (LOGIN_MODE.equalsIgnoreCase(GROUP_MODE))
                profileName = AppDatabase.getDatabaseInstance(this).getGroupsDao().getGroupNameByGrpID(FC_Constants.currentStudentID);
            else if (!LOGIN_MODE.equalsIgnoreCase(QR_GROUP_MODE)) {
                profileName = AppDatabase.getDatabaseInstance(this).getStudentDao().getFullName(FC_Constants.currentStudentID);
            }

            if (LOGIN_MODE.equalsIgnoreCase(INDIVIDUAL_MODE))
                profileName = profileName.split(" ")[0];

        } catch (Exception e) {
            e.printStackTrace();
        }
        setProfileName(profileName);
    }

    @UiThread
    public void setProfileName(String profileName) {
        tv_Activity.setText(profileName);
        tv_Topic.setText(sub_Name);
    }

    @Override
    public void setActualLevel(int levelCount) {
        count = levelCount;
        setLevel();
    }

    @UiThread
    public void setLevel() {
/*
        try {
            if(count>0){
            List<Level_ImageData> imageDatas = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                imageDatas.add(new Level_ImageData("" + i));
            }
            Level_ImageAdapter simpleImageAdapter = new Level_ImageAdapter(this, imageDatas);
            level_circle.setAdapter(simpleImageAdapter);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            level_circle.setOnMenuSelectedListener((parent, view, pos) -> {
                FC_Constants.currentLevel = pos;
                EventMessage eventMessage = new EventMessage();
                eventMessage.setMessage(LEVEL_CHANGED);
                EventBus.getDefault().post(eventMessage);
                changeBGNew(FC_Constants.currentLevel);
//                Toast.makeText(HomeActivity.this, "Level : " + pos, Toast.LENGTH_SHORT).show();
            });
        }catch (Exception e){e.printStackTrace();}
*/

        SubmarineItem item = new SubmarineItem(getDrawable(R.drawable.level_1), null);
        SubmarineItem item2 = new SubmarineItem(getDrawable(R.drawable.level_2), null);
        SubmarineItem item3 = new SubmarineItem(getDrawable(R.drawable.level_3), null);
        SubmarineItem item4 = new SubmarineItem(getDrawable(R.drawable.level_4), null);
        SubmarineItem item5 = new SubmarineItem(getDrawable(R.drawable.level_5), null);


        submarine.setSubmarineItemClickListener((position, submarineItem) -> {
            FC_Constants.currentLevel = position;
            switch (position) {
                case 0:
                    iv_level.setImageResource(R.drawable.level_1);
                    break;
                case 1:
                    iv_level.setImageResource(R.drawable.level_2);
                    break;
                case 2:
                    iv_level.setImageResource(R.drawable.level_3);
                    break;
                case 3:
                    iv_level.setImageResource(R.drawable.level_4);
                    break;
                case 4:
                    iv_level.setImageResource(R.drawable.level_5);
                    break;
            }
            EventMessage eventMessage = new EventMessage();
            eventMessage.setMessage(LEVEL_CHANGED);
            EventBus.getDefault().post(eventMessage);
            changeBGNew(FC_Constants.currentLevel);
            submarine.dip();
        });

        submarine.setSubmarineCircleClickListener(() -> {
            submarine.dip();
        });

        try {
            submarine.clearAllSubmarineItems();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < count; i++) {
            switch (i) {
                case 0:
                    submarine.addSubmarineItem(item);
                    break;
                case 1:
                    submarine.addSubmarineItem(item2);
                    break;
                case 2:
                    submarine.addSubmarineItem(item3);
                    break;
                case 3:
                    submarine.addSubmarineItem(item4);
                    break;
                case 4:
                    submarine.addSubmarineItem(item5);
                    break;
            }
        }
    }

    private void changeBGNew(int currentLevel) {
        switch (currentLevel) {
            case 0:
                header_rl.setBackground(homeHeader1);
                tabLayout.setBackgroundColor(getResources().getColor(R.color.level_1_color));
                break;
            case 1:
                header_rl.setBackground(homeHeader2);
                tabLayout.setBackgroundColor(getResources().getColor(R.color.level_2_color));
                break;
            case 2:
                header_rl.setBackground(homeHeader3);
                tabLayout.setBackgroundColor(getResources().getColor(R.color.level_3_color));
                break;
            case 3:
                header_rl.setBackground(homeHeader4);
                tabLayout.setBackgroundColor(getResources().getColor(R.color.level_4_color));
                break;
            case 4:
                header_rl.setBackground(homeHeader4);
                tabLayout.setBackgroundColor(getResources().getColor(R.color.level_4_color));
                break;
        }
    }

    private void setupTabIcons() {
        TextView learningTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_text, null);
        learningTab.setText("Learning");
        learningTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_learning, 0, 0);

        TextView practiceTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_text, null);
        practiceTab.setText("Practice");
        practiceTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_practice, 0, 0);

        TextView profileTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_text, null);
        profileTab.setText("Profile");
        profileTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile, 0, 0);

        if (currentSubject.equalsIgnoreCase("english")) {
//            TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_text, null);
//            tabThree.setText("Test");
//            tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_test, 0, 0);
//            tabLayout.getTabAt(2).setCustomView(tabThree);
            TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_text, null);
            tabFour.setText("Fun");
            tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fun, 0, 0);
            tabLayout.getTabAt(2).setCustomView(tabFour);
            tabLayout.getTabAt(3).setCustomView(profileTab);
        }else
            tabLayout.getTabAt(2).setCustomView(profileTab);

        if (LOGIN_MODE.contains("group")) {
            tabLayout.getTabAt(0).setCustomView(learningTab);
            tabLayout.getTabAt(1).setCustomView(practiceTab);
        } else {
            tabLayout.getTabAt(0).setCustomView(practiceTab);
            tabLayout.getTabAt(1).setCustomView(learningTab);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().toString().equalsIgnoreCase("Profile")){
                    header_rl.setVisibility(View.GONE);
                }else
                    header_rl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getText().toString().equalsIgnoreCase("Profile")){
                    header_rl.setVisibility(View.GONE);
                }else
                    header_rl.setVisibility(View.VISIBLE);

                if(tab.getPosition()== 2){
                    //TODO Test Dialog
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void showTestTypeSelectionDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.test_type_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_red.setText("unsupervised");
        dia_btn_green.setText("" + dialog_btn_cancel);
        dia_btn_yellow.setText("supervised");
        dialog.show();

        dia_btn_red.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(HomeActivity.this, SupervisedAssessmentActivity.class);
            intent.putExtra("testMode", "unsupervised");
            startActivityForResult(intent, 10);
        });

        dia_btn_yellow.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(HomeActivity.this, SupervisedAssessmentActivity.class);
            intent.putExtra("testMode", "supervised");
            startActivityForResult(intent, 10);
        });

        dia_btn_green.setOnClickListener(v -> {
            dialog.dismiss();
            //TODO Goto Learning
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
/*            if (resultCode == Activity.RESULT_OK) {
                String cCode = data.getStringExtra("cCode");
                int tMarks = data.getIntExtra("tMarks", 0);
                int sMarks = data.getIntExtra("sMarks", 0);
                try {
                    if (cCode.equalsIgnoreCase(certi_Code)) {
                        testList.get(clicked_Pos).setAsessmentGiven(true);
                        testList.get(clicked_Pos).setTotalMarks(tMarks);
                        testList.get(clicked_Pos).setScoredMarks(sMarks);
                        float perc = ((float) sMarks / (float) tMarks) * 100;
                        testList.get(clicked_Pos).setStudentPercentage("" + perc);
                        testList.get(clicked_Pos).setCertificateRating(presenter.getStarRating(perc));
                        testAdapter.notifyItemChanged(clicked_Pos, testList.get(clicked_Pos));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            checkAllAssessmentsDone();*/
        } else if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
//                EventMessage eventMessage = new EventMessage();
//                eventMessage.setMessage(LEVEL_CHANGED);
//                EventBus.getDefault().post(eventMessage);

/*                presenter.clearNodeIds();
                navChanged = true;
                levelList.clear();
                test_lang_spinner.setVisibility(View.VISIBLE);
                my_recycler_view.setVisibility(View.GONE);
                test_recycler_view.setVisibility(View.VISIBLE);
                bottomSection = "Test";
                presenter.getBottomNavId(currentLevel, bottomSection);*/
            } else if (resultCode == Activity.RESULT_CANCELED) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Click(R.id.iv_level)
    public void levelChange() {
        submarine.show();
    }

    private void setupViewPager(ViewPager viewpager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewpager.setOffscreenPageLimit(5);
        if (LOGIN_MODE.contains("group")) {
            adapter.addFrag(new LearningFragment_(), "Learning");
            adapter.addFrag(new PracticeFragment_(), "Practice");
        } else {
            adapter.addFrag(new PracticeFragment_(), "Practice");
            adapter.addFrag(new LearningFragment_(), "Learning");
        }
        if (currentSubject.equalsIgnoreCase("english")) {
//            adapter.addFrag(new TestFragment_(), "Test");
            adapter.addFrag(new FunFragment_(), "Fun");
            adapter.addFrag(new Student_profile_activity_(), "Profile");
        }
        else
            adapter.addFrag(new Student_profile_activity_(), "Profile");

        viewpager.setAdapter(adapter);
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

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(FC_Constants.LEVEL_CHANGED)) {
            }
        }
    }

    @Click(R.id.floating_back)
    public void backBtnPressed() {
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessage(BACK_PRESSED);
        EventBus.getDefault().post(eventMessage);
    }

    boolean dialogFlg = false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
//        if (!dialogFlg) {
//            dialogFlg = true;
//            exitDialog();
//        }
    }

    @SuppressLint("SetTextI18n")
    private void exitDialog() {
        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fc_custom_dialog);
/*      Bitmap map=FC_Utility.takeScreenShot(HomeActivity.this);
        Bitmap fast=FC_Utility.fastblur(map, 20);
        final Drawable draw=new BitmapDrawable(getResources(),fast);
        dialog.getWindow().setBackgroundDrawable(draw);*/
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button next_btn = dialog.findViewById(R.id.dia_btn_green);
        Button test_btn = dialog.findViewById(R.id.dia_btn_yellow);
        Button revise_btn = dialog.findViewById(R.id.dia_btn_red);

        dia_title.setText("Change Subject?");
        revise_btn.setText("" + dialog_btn_exit);
        test_btn.setText("" + dialog_btn_cancel);
        next_btn.setText("OK");

        next_btn.setOnClickListener(v -> {
            finish();
            dialogFlg = false;
            dialog.dismiss();
        });

        revise_btn.setOnClickListener(v -> {
            endSession(HomeActivity.this);
            finishAffinity();
            dialogFlg = false;
            dialog.dismiss();
        });

        test_btn.setOnClickListener(v -> {
            dialogFlg = false;
            dialog.dismiss();
        });
    }

    @Override
    public void onItemSelected(CursorWheelLayout parent, View view, int pos) {
        Log.d("LevelChanger", "onItemSelected: " + pos);
    }
}