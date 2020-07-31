package com.pratham.foundation.ui.app_home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.async.ContentDownloadingTask;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.submarine_view.SubmarineItem;
import com.pratham.foundation.customView.submarine_view.SubmarineView;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.Session;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.app_home.fun.FunFragment_;
import com.pratham.foundation.ui.app_home.learning_fragment.LearningFragment_;
import com.pratham.foundation.ui.app_home.practice_fragment.PracticeFragment_;
import com.pratham.foundation.ui.app_home.profile_new.ProfileFragment_;
import com.pratham.foundation.ui.app_home.test_fragment.TestFragment_;
import com.pratham.foundation.ui.app_home.test_fragment.supervisor.SupervisedAssessmentActivity;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.BackBtnSound;
import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.ApplicationClass.getAppMode;
import static com.pratham.foundation.utility.FC_Constants.ACTIVITY_RESUMED;
import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.ASSESSMENT_SESSION;
import static com.pratham.foundation.utility.FC_Constants.BACK_PRESSED;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_STUDENT_ID;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_SUPERVISOR_ID;
import static com.pratham.foundation.utility.FC_Constants.FRAGMENT_RESELECTED;
import static com.pratham.foundation.utility.FC_Constants.FRAGMENT_SELECTED;
import static com.pratham.foundation.utility.FC_Constants.GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.INDIVIDUAL_MODE;
import static com.pratham.foundation.utility.FC_Constants.LEVEL_CHANGED;
import static com.pratham.foundation.utility.FC_Constants.LOGIN_MODE;
import static com.pratham.foundation.utility.FC_Constants.SECTION_COMPLETION_PERC;
import static com.pratham.foundation.utility.FC_Constants.activityPhotoPath;
import static com.pratham.foundation.utility.FC_Constants.currentLevel;
import static com.pratham.foundation.utility.FC_Constants.sec_Fun;
import static com.pratham.foundation.utility.FC_Constants.sec_Learning;
import static com.pratham.foundation.utility.FC_Constants.sec_Practice;
import static com.pratham.foundation.utility.FC_Constants.sec_Profile;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;
import static com.pratham.foundation.utility.FC_Constants.supervisedAssessment;
import static com.pratham.foundation.utility.FC_Constants.testSessionEnded;
import static com.pratham.foundation.utility.FC_Constants.testSessionEntered;

@EActivity(R.layout.activity_home)
@SuppressLint("StaticFieldLeak")
public class HomeActivity extends BaseActivity implements LevelChanged {

    @ViewById(R.id.tv_header_progress)
    public static TextView tv_header_progress;
    @ViewById(R.id.viewpager)
    ViewPager viewpager;
    @ViewById(R.id.home_root_layout)
    RelativeLayout home_root_layout;
    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.tv_Activity)
    TextView tv_Activity;
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
    @DrawableRes(R.drawable.home_header_0_bg)
    Drawable homeHeader0;
    @DrawableRes(R.drawable.home_header_1_bg)
    Drawable homeHeader1;
    @DrawableRes(R.drawable.home_header_2_bg)
    Drawable homeHeader2;
    @DrawableRes(R.drawable.home_header_3_bg)
    Drawable homeHeader3;
    @DrawableRes(R.drawable.home_header_4_bg)
    Drawable homeHeader4;
    @ViewById(R.id.main_back)
    ImageView main_back;
    @ViewById(R.id.floating_info)
    FloatingActionButton floating_info;

    public static String sub_Name, sub_nodeId = "";
    public static boolean languageChanged = false;
    public static LevelChanged levelChanged;
    List<ContentTable> rootList;
    String currSubj, levelTitle;
    SimpleDraweeView test_dialog_img;
    @Bean(ContentDownloadingTask.class)
    public static ContentDownloadingTask contentDownloadingTask;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//        Configuration config = getResources().getConfiguration();
//        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
        sub_nodeId = getIntent().getStringExtra("nodeId");
        sub_Name = getIntent().getStringExtra("nodeTitle");
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//        changeBackground(sub_Name);
        showLoader();
        new Handler().postDelayed(this::startActivityAndTabSetup, 200);
    }

    private void startActivityAndTabSetup() {
        Runtime rs = Runtime.getRuntime();
        rs.freeMemory();
        rs.gc();
        rs.freeMemory();
        FastSave.getInstance().saveString(APP_SECTION, sec_Learning);
        changeBGNew(1);
        sub_nodeId = FastSave.getInstance().getString(FC_Constants.CURRENT_ROOT_NODE, "");
        FastSave.getInstance().saveInt(FC_Constants.CURRENT_LEVEL, 1);
        currentLevel = FastSave.getInstance().getInt(FC_Constants.CURRENT_LEVEL, 1);
        tv_header_progress.setText("0%");
        floating_info.setImageResource(R.drawable.ic_info_outline_white);
        currSubj = FastSave.getInstance().getString(FC_Constants.CURRENT_SUBJECT, "");
        //        floating_back.setImageResource(R.drawable.ic_left_arrow_white);
        levelChanged = HomeActivity.this;
        rootList = new ArrayList<>();
        displayProfileName();
        setupViewPager(viewpager);
//        displayProfileImage();
        tabLayout.setupWithViewPager(viewpager);
        setupTabIcons();
        setLevel();
        if(!getAppMode())
            submarine.setCircleSize(getResources().getDimension(R.dimen._20sdp));
        new Handler().postDelayed(() -> {
            getCompletion();
        }, 2000);
    }

    private void getCompletion() {
//        getNewCompletion();
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessage(SECTION_COMPLETION_PERC);
        EventBus.getDefault().post(eventMessage);
    }
//
//    private void getNewCompletion() {
//        new Handler().postDelayed(() -> {
//            EventMessage eventMessage = new EventMessage();
//            eventMessage.setMessage(SECTION_COMPLETION_PERC);
//            EventBus.getDefault().post(eventMessage);
//            Log.d(SECTION_COMPLETION_PERC, "CURRENT SEC: --------------: "+FastSave.getInstance().getString(APP_SECTION,"")+" :---------------------------------");
//            Log.d(SECTION_COMPLETION_PERC, "getNewCompletion: -----------------------------------------------");
//            getNewCompletion();
//        }, 10500);
//    }

//    @Background
//    public void displayProfileImage() {
//        String sImage;
//        try {
//            if (!GROUP_LOGIN)
//                sImage = AppDatabase.getDatabaseInstance(this).getStudentDao().getStudentAvatar(FastSave.getInstance().getString(CURRENT_STUDENT_ID, ""));
//            else
//                sImage = "group_icon";
//        } catch (Exception e) {
//            e.printStackTrace();
//            sImage = "group_icon";
//        }
//    }


    @Override
    protected void onResume() {
        super.onResume();
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessage(ACTIVITY_RESUMED);
        EventBus.getDefault().post(eventMessage);
    }

    private CustomLodingDialog myLoadingDialog;

    @UiThread
    public void showLoader() {
        if (myLoadingDialog == null) {
            myLoadingDialog = new CustomLodingDialog(this);
            myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(myLoadingDialog.getWindow()).
                    setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myLoadingDialog.setContentView(R.layout.loading_dialog);
            myLoadingDialog.setCanceledOnTouchOutside(true);
            myLoadingDialog.setCancelable(true);
            myLoadingDialog.show();
        } else
            myLoadingDialog.show();
    }

    @UiThread
    public void dismissLoadingDialog() {
        try {
            new Handler().postDelayed(() -> {
                if (myLoadingDialog != null && myLoadingDialog.isShowing())
                    myLoadingDialog.dismiss();
            }, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    public void displayProfileName() {
        String profileName = "";
        try {
            activityPhotoPath = Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/ActivityPhotos/" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "") + "/";
            Log.d("activityPhotoPath", "initialize activityPhotoPath: " + activityPhotoPath);
            if (!new File(activityPhotoPath).exists())
                new File(activityPhotoPath).mkdir();
            try {
                File direct = new File(activityPhotoPath + ".nomedia");
                if (!direct.exists())
                    direct.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (FastSave.getInstance().getString(LOGIN_MODE, "").equalsIgnoreCase(GROUP_MODE))
                profileName = AppDatabase.getDatabaseInstance(HomeActivity.this)
                        .getGroupsDao().getGroupNameByGrpID(FastSave.getInstance()
                                .getString(CURRENT_STUDENT_ID, ""));
/*            else if (!LOGIN_MODE.equalsIgnoreCase(QR_GROUP_MODE)) {
                profileName = AppDatabase.getDatabaseInstance(this)
                        .getStudentDao().getFullName(FastSave.getInstance()
                                .getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            }*/
            if (FastSave.getInstance().getString(LOGIN_MODE, "").equalsIgnoreCase(INDIVIDUAL_MODE))
                profileName = FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_NAME, "")
                        .split(" ")[0];

        } catch (Exception e) {
            e.printStackTrace();
        }
        setProfileName(profileName);
    }

    @UiThread
    public void setProfileName(String profileName) {
        tv_Activity.setText(profileName);
        tv_Topic.setText(sub_Name);
        tv_Topic.setSelected(true);
    }

    private void sortContentList(List<ContentTable> contentParentList) {
        Collections.sort(contentParentList, (o1, o2) -> o1.getSeq_no() - (o2.getSeq_no()));
    }

    @Override
    public void setActualLevel(List<ContentTable> level_List, String title) {
        rootList = level_List;
        if (rootList != null)
            sortContentList(rootList);
        levelTitle = title;
        setLevel();
    }

    @UiThread
    public void setLevel() {
        SubmarineItem item = new SubmarineItem(getDrawable(R.drawable.level_1), null);
        SubmarineItem item2 = new SubmarineItem(getDrawable(R.drawable.level_2), null);
        SubmarineItem item3 = new SubmarineItem(getDrawable(R.drawable.level_3), null);
        SubmarineItem item4 = new SubmarineItem(getDrawable(R.drawable.level_4), null);
        SubmarineItem item5 = new SubmarineItem(getDrawable(R.drawable.level_5), null);

        try {
            FC_Constants.currentLevel = Integer.parseInt(levelTitle);
            FastSave.getInstance().saveInt(FC_Constants.CURRENT_LEVEL, currentLevel);
            if (levelTitle.contains("1")) {
                iv_level.setImageResource(R.drawable.level_1);
                changeBGNew(1);
            } else if (levelTitle.contains("2")) {
                iv_level.setImageResource(R.drawable.level_2);
                changeBGNew(2);
            } else if (levelTitle.contains("3")) {
                iv_level.setImageResource(R.drawable.level_3);
                changeBGNew(3);
            } else if (levelTitle.contains("4")) {
                iv_level.setImageResource(R.drawable.level_4);
                changeBGNew(4);
            } else if (levelTitle.contains("5")) {
                iv_level.setImageResource(R.drawable.level_5);
                changeBGNew(5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        submarine.setSubmarineItemClickListener((position, submarineItem) -> {
            try {
                ButtonClickSound.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            FC_Constants.currentLevel = Integer.parseInt(rootList.get(position).getNodeTitle());
            FastSave.getInstance().saveInt(FC_Constants.CURRENT_LEVEL, Integer.parseInt(rootList.get(position).getNodeTitle()));
/*
            if (levelTitle.contains("1")) {
                iv_level.setImageResource(R.drawable.level_1);
                changeBGNew(0);
            } else if (levelTitle.contains("2")) {
                iv_level.setImageResource(R.drawable.level_2);
                changeBGNew(1);
            } else if (levelTitle.contains("3")) {
                iv_level.setImageResource(R.drawable.level_3);
                changeBGNew(2);
            } else if (levelTitle.contains("4")) {
                iv_level.setImageResource(R.drawable.level_4);
                changeBGNew(3);
            } else if (levelTitle.contains("5")) {
                iv_level.setImageResource(R.drawable.level_5);
                changeBGNew(4);
            }
*/
            switch (currentLevel) {
                case 1:
                    iv_level.setImageResource(R.drawable.level_1);
                    changeBGNew(1);
                    break;
                case 2:
                    iv_level.setImageResource(R.drawable.level_2);
                    changeBGNew(2);
                    break;
                case 3:
                    iv_level.setImageResource(R.drawable.level_3);
                    changeBGNew(3);
                    break;
                case 4:
                    iv_level.setImageResource(R.drawable.level_4);
                    changeBGNew(4);
                    break;
                case 5:
                    iv_level.setImageResource(R.drawable.level_5);
                    changeBGNew(5);
                    break;
            }
            EventMessage eventMessage = new EventMessage();
            eventMessage.setMessage(LEVEL_CHANGED);
            EventBus.getDefault().post(eventMessage);
//            changeBGNew(FastSave.getInstance().getInt(FC_Constants.CURRENT_LEVEL, position));
            submarine.dip();
        });

        submarine.setSubmarineCircleClickListener(() -> {
            try {
                BackBtnSound.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            submarine.dip();
        });

        try {
            submarine.clearAllSubmarineItems();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < rootList.size(); i++) {
            if (rootList.get(i).getNodeTitle().contains("1"))
                submarine.addSubmarineItem(item);
            else if (rootList.get(i).getNodeTitle().contains("2"))
                submarine.addSubmarineItem(item2);
            else if (rootList.get(i).getNodeTitle().contains("3"))
                submarine.addSubmarineItem(item3);
            else if (rootList.get(i).getNodeTitle().contains("4"))
                submarine.addSubmarineItem(item4);
            else if (rootList.get(i).getNodeTitle().contains("5"))
                submarine.addSubmarineItem(item5);
        }
    }

    private void changeBGNew(int currentLevel) {
        switch (currentLevel) {
            case 1:
//                header_rl.setBackground(homeHeader0);
                tabLayout.setBackground(getResources().getDrawable(R.drawable.home_footer_0_bg));
                break;
            case 2:
//                header_rl.setBackground(homeHeader1);
                tabLayout.setBackground(getResources().getDrawable(R.drawable.home_footer_1_bg));
//                tabLayout.setBackgroundColor(getResources().getColor(R.color.level_1_color));
                break;
            case 3:
//                header_rl.setBackground(homeHeader2);
                tabLayout.setBackground(getResources().getDrawable(R.drawable.home_footer_2_bg));
//                tabLayout.setBackgroundColor(getResources().getColor(R.color.level_2_color));
                break;
            case 4:
//                header_rl.setBackground(homeHeader3);
                tabLayout.setBackground(getResources().getDrawable(R.drawable.home_footer_3_bg));
//                tabLayout.setBackgroundColor(getResources().getColor(R.color.level_3_color));
                break;
            case 5:
//                header_rl.setBackground(homeHeader4);
                tabLayout.setBackground(getResources().getDrawable(R.drawable.home_footer_4_bg));
//                tabLayout.setBackgroundColor(getResources().getColor(R.color.level_4_color));
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setupTabIcons() {
        TextView learningTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_text, null);
        learningTab.setText("" + getResources().getString(R.string.Learning));
        learningTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_learning, 0, 0);

        TextView practiceTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_text, null);
        practiceTab.setText("" + getResources().getString(R.string.Practice));
        practiceTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_practice, 0, 0);

        TextView profileTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_text, null);
        profileTab.setText("" + getResources().getString(R.string.Profile));
        profileTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile, 0, 0);

        TextView testTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_text, null);
        testTab.setText("" + getResources().getString(R.string.Test));
        testTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_test, 0, 0);

        if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).contains("group") || !getAppMode()) {
            FastSave.getInstance().saveString(APP_SECTION, sec_Learning);
            tabLayout.getTabAt(0).setCustomView(learningTab);
            tabLayout.getTabAt(1).setCustomView(practiceTab);
        } else {
            FastSave.getInstance().saveString(APP_SECTION, sec_Practice);
            tabLayout.getTabAt(0).setCustomView(practiceTab);
            tabLayout.getTabAt(1).setCustomView(learningTab);
        }

        if (currSubj.equalsIgnoreCase("english")) {
            TextView funTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_text, null);
            funTab.setText("" + getResources().getString(R.string.Fun));
            funTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fun, 0, 0);

            tabLayout.getTabAt(2).setCustomView(testTab);
            tabLayout.getTabAt(3).setCustomView(funTab);
            tabLayout.getTabAt(4).setCustomView(profileTab);
        } else if (currSubj.equalsIgnoreCase("LS_Science") || currSubj.equalsIgnoreCase("Maths")) {
            tabLayout.getTabAt(2).setCustomView(testTab);
            tabLayout.getTabAt(3).setCustomView(profileTab);
        } else {
            tabLayout.getTabAt(2).setCustomView(profileTab);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
/*                if (tab.getText().toString().equalsIgnoreCase("Learning")) {
                    FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test) = true;
                }if (tab.getText().toString().equalsIgnoreCase("Practice")) {
                }*/
                try {
                    BackBtnSound.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                if (tab.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.Test))) {
                    FastSave.getInstance().saveString(APP_SECTION, sec_Test);
                    String assessmentSession = "test-" + ApplicationClass.getUniqueID();
                    FastSave.getInstance().saveString(ASSESSMENT_SESSION, assessmentSession);
                    EventMessage eventMessage = new EventMessage();
                    eventMessage.setMessage(FRAGMENT_SELECTED);
                    EventBus.getDefault().post(eventMessage);
                    showTestTypeSelectionDialog();
                } else if (tab.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.Profile))) {
                    FastSave.getInstance().saveString(APP_SECTION, sec_Profile);
                    FastSave.getInstance().saveBoolean(supervisedAssessment, false);
                    if (testSessionEntered && !testSessionEnded)
                        endTestSession();
                    header_rl.setVisibility(View.GONE);
                } else {
                    if (tab.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.Learning)))
                        FastSave.getInstance().saveString(APP_SECTION, sec_Learning);
                    else if (tab.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.Practice)))
                        FastSave.getInstance().saveString(APP_SECTION, sec_Practice);
                    else if (tab.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.Fun)))
                        FastSave.getInstance().saveString(APP_SECTION, sec_Fun);

                    FastSave.getInstance().saveBoolean(supervisedAssessment, false);
                    if (testSessionEntered && !testSessionEnded)
                        endTestSession();
                    EventMessage eventMessage = new EventMessage();
                    eventMessage.setMessage(FRAGMENT_SELECTED);
                    EventBus.getDefault().post(eventMessage);
                    header_rl.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                try {
                    BackBtnSound.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                if (tab.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.Test))) {
                    FastSave.getInstance().saveString(APP_SECTION, sec_Test);
                    EventMessage eventMessage = new EventMessage();
                    eventMessage.setMessage(FRAGMENT_RESELECTED);
                    EventBus.getDefault().post(eventMessage);
                    showTestTypeSelectionDialog();
                } else if (tab.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.Profile))) {
                    FastSave.getInstance().saveString(APP_SECTION, sec_Profile);
                    header_rl.setVisibility(View.GONE);
                } else {
                    if (tab.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.Learning)))
                        FastSave.getInstance().saveString(APP_SECTION, sec_Learning);
                    else if (tab.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.Practice)))
                        FastSave.getInstance().saveString(APP_SECTION, sec_Practice);
                    else if (tab.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.Fun)))
                        FastSave.getInstance().saveString(APP_SECTION, sec_Fun);
                    EventMessage eventMessage = new EventMessage();
                    eventMessage.setMessage(FRAGMENT_RESELECTED);
                    EventBus.getDefault().post(eventMessage);
                    header_rl.setVisibility(View.VISIBLE);
                }
                getCompletion();
            }
        });
    }

    @Background
    public void endTestSession() {
        try {
            Session startSesion = new Session();
            String toDateTemp = AppDatabase.getDatabaseInstance(HomeActivity.this).getSessionDao().
                    getToDate(FastSave.getInstance().getString(FC_Constants.ASSESSMENT_SESSION, ""));
            if (toDateTemp != null && toDateTemp.equalsIgnoreCase("na")) {
                AppDatabase.getDatabaseInstance(HomeActivity.this).getSessionDao().UpdateToDate(FastSave.getInstance()
                                .getString(FC_Constants.ASSESSMENT_SESSION, ""),
                        FC_Utility.getCurrentDateTime());
            }
            BackupDatabase.backup(this);
            AppDatabase.getDatabaseInstance(HomeActivity.this).getSessionDao().insert(startSesion);
            FastSave.getInstance().saveString(FC_Constants.ASSESSMENT_SESSION, "NA");
            testSessionEntered = false;
            testSessionEnded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    BlurPopupWindow myDialog;

    @SuppressLint("SetTextI18n")
    private void showTestTypeSelectionDialog() {
        FastSave.getInstance().saveString(CURRENT_SUPERVISOR_ID, "NA");
        FastSave.getInstance().saveBoolean(supervisedAssessment, false);
        myDialog = new BlurPopupWindow.Builder(this)
                .setContentView(R.layout.test_type_dialog)
                .bindClickListener(v -> {
                    FastSave.getInstance().saveBoolean(supervisedAssessment, false);
                    Intent intent = new Intent(HomeActivity.this, SupervisedAssessmentActivity.class);
                    intent.putExtra("testMode", "unsupervised");
                    new Handler().postDelayed(() -> {
                        myDialog.dismiss();
                        startActivity(intent);
                    }, 200);
                }, R.id.btn_unsupervised)
                .bindClickListener(v -> {
                    FastSave.getInstance().saveBoolean(supervisedAssessment, true);
                    Intent intent = new Intent(HomeActivity.this, SupervisedAssessmentActivity.class);
                    intent.putExtra("testMode", "supervised");
                    new Handler().postDelayed(() -> {
                        myDialog.dismiss();
                        startActivity(intent);
                    }, 200);
                }, R.id.btn_supervised)
                .bindClickListener(v -> {
                    tabLayout.getTabAt(0).select();
                    new Handler().postDelayed(() -> {
                        myDialog.dismiss();
                    }, 200);
                }, R.id.dia_btn_green)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(false)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();
        myDialog.show();
/*        test_dialog_img = myDialog.findViewById(R.id.test_dialog_img);
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse("http://www.prodigi.openiscool.org/repository/Images/SoundOutPhonemes.png"))
                .setLocalThumbnailPreviewsEnabled(false)
                .build();
        if(imageRequest !=null ) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .build();
            test_dialog_img.setController(controller);
        }*/
    }


    boolean comngSoonFlg = false;

    @UiThread
    @SuppressLint("SetTextI18n")
    void showComingSoonDia() {
        myDialog = new BlurPopupWindow.Builder(this)
                .setContentView(R.layout.lottie_coming_soon)
                .bindClickListener(v -> {
                    comngSoonFlg = false;
                    new Handler().postDelayed(() -> {
                        myDialog.dismiss();
                    }, 200);
                }, R.id.dia_btn_yes)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(false)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();
        myDialog.show();
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 1461){
//        if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test)) {
            if (resultCode == Activity.RESULT_OK) {
                String cCode = data.getStringExtra("cCode");
                int tMarks = data.getIntExtra("tMarks", 0);
                int sMarks = data.getIntExtra("sMarks", 0);
                try {
//                    new Handler().postDelayed(() -> {
                    EventMessage eventMessage = new EventMessage();
                    eventMessage.setMessage(LEVEL_TEST_GIVEN + ":" + cCode + ":" + tMarks + ":" + sMarks);
                    EventBus.getDefault().post(eventMessage);
//                    }, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 10) {
*/
/*            if (resultCode == Activity.RESULT_OK) {
            } else if (resultCode == Activity.RESULT_CANCELED) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*//*

        }
    }
*/

    @Click(R.id.iv_level)
    public void levelChange() {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        submarine.show();
    }

    private void setupViewPager(ViewPager viewpager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewpager.setOffscreenPageLimit(5);
        if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).contains("group") || !getAppMode()) {
            adapter.addFrag(new LearningFragment_(), "" + getResources().getString(R.string.Learning));
            adapter.addFrag(new PracticeFragment_(), "" + getResources().getString(R.string.Practice));
            if (currSubj.equalsIgnoreCase("LS_Science") || currSubj.equalsIgnoreCase("Maths")) {
                adapter.addFrag(new TestFragment_(), "" + getResources().getString(R.string.Test));
            } else if (currSubj.equalsIgnoreCase("english")) {
                adapter.addFrag(new TestFragment_(), "" + getResources().getString(R.string.Test));
                adapter.addFrag(new FunFragment_(), "" + getResources().getString(R.string.Fun));
            }
            adapter.addFrag(new ProfileFragment_(), "" + getResources().getString(R.string.Profile));
        } else {
            adapter.addFrag(new PracticeFragment_(), "" + getResources().getString(R.string.Practice));
            adapter.addFrag(new LearningFragment_(), "" + getResources().getString(R.string.Learning));
            if (currSubj.equalsIgnoreCase("LS_Science") || currSubj.equalsIgnoreCase("Maths")) {
                adapter.addFrag(new TestFragment_(), "" + getResources().getString(R.string.Test));
            } else if (currSubj.equalsIgnoreCase("english")) {
                adapter.addFrag(new TestFragment_(), "" + getResources().getString(R.string.Test));
                adapter.addFrag(new FunFragment_(), "" + getResources().getString(R.string.Fun));
            }
            adapter.addFrag(new ProfileFragment_(), "" + getResources().getString(R.string.Profile));
        }
        viewpager.setAdapter(adapter);
        new Handler().postDelayed(() -> {
            dismissLoadingDialog();
        }, 300);
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(FC_Constants.COMING_SOON)) {
                if (!comngSoonFlg) {
                    comngSoonFlg = true;
                    showComingSoonDia();
                }
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.BOTTOM_FRAGMENT_END_SESSION)) {
                endSession();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Background
    public void endSession() {
        try {
            String curSession = AppDatabase.getDatabaseInstance(HomeActivity.this).getStatusDao().getValue("CurrentSession");
            String toDateTemp = AppDatabase.getDatabaseInstance(HomeActivity.this).getSessionDao().getToDate(curSession);
            if (toDateTemp.equalsIgnoreCase("na")) {
                AppDatabase.getDatabaseInstance(HomeActivity.this).getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
            }
            BackupDatabase.backup(HomeActivity.this);
        } catch (Exception e) {
            String curSession = AppDatabase.getDatabaseInstance(HomeActivity.this).getStatusDao().getValue("CurrentSession");
            AppDatabase.getDatabaseInstance(HomeActivity.this).getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
            e.printStackTrace();
        }
    }

    @Click(R.id.main_back)
    public void backBtnPressed() {
        try {
            BackBtnSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessage(BACK_PRESSED);
        EventBus.getDefault().post(eventMessage);
    }

    boolean dialogFlg = false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

//    @SuppressLint("SetTextI18n")
//    private void exitDialog() {
//        CustomLodingDialog dialog = new CustomLodingDialog(HomeActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.fc_custom_dialog);
///*      Bitmap map=FC_Utility.takeScreenShot(HomeActivity.this);
//        Bitmap fast=FC_Utility.fastblur(map, 20);
//        final Drawable draw=new BitmapDrawable(getResources(),fast);
//        dialog.getWindow().setBackgroundDrawable(draw);*/
//        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//
//        TextView dia_title = dialog.findViewById(R.id.dia_title);
//        Button next_btn = dialog.findViewById(R.id.dia_btn_green);
//        Button test_btn = dialog.findViewById(R.id.dia_btn_yellow);
//        Button revise_btn = dialog.findViewById(R.id.dia_btn_red);
//
//        dia_title.setText("" + getResources().getString(R.string.change_subj));
//        revise_btn.setText("" + getResources().getString(R.string.Exit));
//        test_btn.setText("" + getResources().getString(R.string.Cancel));
//        next_btn.setText("" + getResources().getString(R.string.Okay));
//
//        next_btn.setOnClickListener(v -> {
//            try {
//                ButtonClickSound.start();
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            }
//            dialogFlg = false;
//            dialog.dismiss();
//            finish();
//        });
//
//        revise_btn.setOnClickListener(v -> {
//            try {
//                ButtonClickSound.start();
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            }
//            endSession(HomeActivity.this);
//            dialogFlg = false;
//            dialog.dismiss();
//            finishAffinity();
//        });
//
//        test_btn.setOnClickListener(v -> {
//            try {
//                ButtonClickSound.start();
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            }
//            dialogFlg = false;
//            dialog.dismiss();
//        });
//    }

}