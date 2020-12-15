package com.pratham.foundation.ui.app_home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.async.ContentDownloadingTask;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.showcaseviewlib.GuideView;
import com.pratham.foundation.customView.showcaseviewlib.config.DismissType;
import com.pratham.foundation.customView.showcaseviewlib.listener.GuideListener;
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
import static com.pratham.foundation.utility.FC_Constants.HOME_ACTIVITY_SHOWCASE;
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
    @ViewById(R.id.tabLayout)
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
        sub_nodeId = getIntent().getStringExtra("nodeId");
        sub_Name = getIntent().getStringExtra("nodeTitle");
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
        String a = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI);
        Log.d("INSTRUCTIONFRAG", "Select Subj: " + a);
        FC_Utility.setAppLocal(this, a);
        setupViewPager(viewpager);
//        displayProfileImage();
        // Setup the bottom tabs accordingly
        tabLayout.setupWithViewPager(viewpager);
        setupTabIcons();
        setLevel();
        if (!getAppMode())
            submarine.setCircleSize(70);
//            submarine.setCircleSize(getResources().getDimension(R.dimen._40sdp));
        new Handler().postDelayed(this::getCompletion, 2000);
        if (!FastSave.getInstance().getBoolean(HOME_ACTIVITY_SHOWCASE, false))
            setShowcaseView();
    }

    private GuideView.Builder builder;
    private GuideView mGuideView;

    @UiThread
    public void setShowcaseView() {
        builder = new GuideView.Builder(this)
                .setTitle(getResources().getString(R.string.progress))
                .setContentText(getResources().getString(R.string.Your_Progress_will))
                .setDismissType(DismissType.selfView) //optional - default dismissible by TargetView
                .setTargetView(tv_header_progress)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        switch (view.getId()) {
                            case R.id.tv_header_progress:
                                builder.setTitle(getResources().getString(R.string.Level));
                                builder.setContentText(getResources().getString(R.string.Click_to_switch_levels));
                                builder.setTargetView(iv_level).build();
                                break;
                            case R.id.iv_level:
                                builder.setTitle(getResources().getString(R.string.Sections));
                                builder.setContentText(getResources().getString(R.string.Click_to_switch_Sections));
                                builder.setTargetView(tabLayout).build();
                                break;
                            case R.id.tabLayout:
                                return;
                        }
                        mGuideView = builder.build();
                        mGuideView.show();
                    }
                });
        mGuideView = builder.build();
        mGuideView.show();
        FastSave.getInstance().saveBoolean(HOME_ACTIVITY_SHOWCASE, true);
//        updatingForDynamicLocationViews();
    }

    private void updatingForDynamicLocationViews() {
        iv_level.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mGuideView.updateGuideViewLocation();
            }
        });
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
        //Fetching information form DB and Displaying
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

            //Setting image depending on the level
            FC_Constants.currentLevel = Integer.parseInt(rootList.get(position).getNodeTitle());
            FastSave.getInstance().saveInt(FC_Constants.CURRENT_LEVEL, Integer.parseInt(rootList.get(position).getNodeTitle()));
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
//        Change Background to distinguish it by different color.
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
//        Add Tab Icons
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
        } else /*if (currSubj.equalsIgnoreCase("LS_Science") || currSubj.equalsIgnoreCase("Maths"))*/ {
            tabLayout.getTabAt(2).setCustomView(testTab);
            tabLayout.getTabAt(3).setCustomView(profileTab);
        }  /*else {
            tabLayout.getTabAt(2).setCustomView(profileTab);
//            tabLayout.getTabAt(2).setCustomView(testTab);
//            tabLayout.getTabAt(3).setCustomView(profileTab);
        }*/
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
                    header_rl.setVisibility(View.GONE);
                    EventMessage eventMessage = new EventMessage();
                    eventMessage.setMessage(FRAGMENT_SELECTED);
                    EventBus.getDefault().post(eventMessage);
                    FastSave.getInstance().saveBoolean(supervisedAssessment, false);
                    if (testSessionEntered && !testSessionEnded)
                        endTestSession();
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
        //End Test Session when the test is over or tab is changed
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
//       Select Test Type - Supervised or Unsupervised
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
//        Loading fragments (learning, practice, test, fun, profile) on the viewPager.
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewpager.setOffscreenPageLimit(5);
        if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).contains("group") || !getAppMode()) {
            adapter.addFrag(new LearningFragment_(), "" + getResources().getString(R.string.Learning));
            adapter.addFrag(new PracticeFragment_(), "" + getResources().getString(R.string.Practice));
            if (currSubj.equalsIgnoreCase("english")) {
                adapter.addFrag(new TestFragment_(), "" + getResources().getString(R.string.Test));
                adapter.addFrag(new FunFragment_(), "" + getResources().getString(R.string.Fun));
            } else /*if (currSubj.equalsIgnoreCase("LS_Science") || currSubj.equalsIgnoreCase("Maths"))*/ {
                adapter.addFrag(new TestFragment_(), "" + getResources().getString(R.string.Test));
            }
            adapter.addFrag(new ProfileFragment_(), "" + getResources().getString(R.string.Profile));
        } else {
            adapter.addFrag(new PracticeFragment_(), "" + getResources().getString(R.string.Practice));
            adapter.addFrag(new LearningFragment_(), "" + getResources().getString(R.string.Learning));
            if (currSubj.equalsIgnoreCase("english")) {
                adapter.addFrag(new TestFragment_(), "" + getResources().getString(R.string.Test));
                adapter.addFrag(new FunFragment_(), "" + getResources().getString(R.string.Fun));
            } else /*if (currSubj.equalsIgnoreCase("LS_Science") || currSubj.equalsIgnoreCase("Maths"))*/ {
                adapter.addFrag(new TestFragment_(), "" + getResources().getString(R.string.Test));
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
        // Event Message Receiver
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
        //Ending main session
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
        //Fire event bus to trigger backpress event on the fragment.
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
}