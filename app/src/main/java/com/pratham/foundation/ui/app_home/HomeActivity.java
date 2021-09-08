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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.async.API_Content;
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
import com.pratham.foundation.interfaces.API_Content_Result;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.Modal_InternetTime;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.app_home.learning_fragment.LearningFragment_;
import com.pratham.foundation.ui.app_home.profile_new.ProfileFragment_;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.BackBtnSound;
import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.ApplicationClass.getAppMode;
import static com.pratham.foundation.utility.FC_Constants.ACTIVITY_RESUMED;
import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.BACK_PRESSED;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_STUDENT_ID;
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
import static com.pratham.foundation.utility.FC_Constants.sec_Learning;
import static com.pratham.foundation.utility.FC_Constants.sec_Profile;
import static com.pratham.foundation.utility.FC_Utility.get12HrTime;
import static com.pratham.foundation.utility.FC_Utility.getRandomCardColor;

//import com.pratham.foundation.ui.app_home.test_fragment.supervisor.SupervisedAssessmentActivity;

@EActivity(R.layout.activity_home)
@SuppressLint("StaticFieldLeak")
public class HomeActivity extends BaseActivity implements LevelChanged, API_Content_Result {

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
    @ViewById(R.id.tv_level)
    public static TextView tv_level;
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
//    @ViewById(R.id.floating_info)
//    FloatingActionButton floating_info;

    public static String sub_Name, sub_nodeId = "";
    public static boolean languageChanged = false;
    public static LevelChanged levelChanged;
    List<ContentTable> rootList;
    String currSubj, levelTitle;
    SimpleDraweeView test_dialog_img;
    LottieAnimationView push_lottie;
    TextView txt_push_dialog_msg,txt_push_dialog_msg2;
    TextView txt_push_error;
    RelativeLayout rl_btn;
    Button ok_btn, eject_btn;
    public static Drawable drawableBg;

    @Bean(ContentDownloadingTask.class)
    public static ContentDownloadingTask contentDownloadingTask;

    @AfterViews
    public void initialize() {
        sub_nodeId = getIntent().getStringExtra("nodeId");
        sub_Name = getIntent().getStringExtra("nodeTitle");
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        drawableBg = ApplicationClass.getInstance().getResources().getDrawable(getRandomCardColor());
        showLoader();
        getInternetTime();
        new Handler().postDelayed(this::startActivityAndTabSetup, 200);
    }

    public void getInternetTime(){
            if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork() || ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
                //Checks if device is connected to raspberry pie
                if (!ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_RASPBERRY_PI)) {
                    API_Content api_content;
                    api_content = new API_Content(this, this);
                    api_content.getInternetTimeApi(FC_Constants.INTERNET_TIME, FC_Constants.INTERNET_TIME_API);
                }
            }
    }

    @Override
    public void receivedContent_PI_SubLevel(String header, String response, int pos, int size) {
    }

    @Override
    public void receivedContent(String header, String response) {
        if (header.equalsIgnoreCase(FC_Constants.INTERNET_TIME)) {
            try {
                Type listType = new TypeToken<Modal_InternetTime>() {
                }.getType();
                Gson gson;
                gson = new Gson();
                Modal_InternetTime serverTime = gson.fromJson(response, listType);
                String sDate = serverTime.getDatetime().split("T")[0];
                Log.d("TAG", "$$$$    :    " +sDate);
                String sTime = serverTime.getDatetime().split("T")[1].substring(0,5);
//                String newDate = sDate.substring(5)+"-"+sDate.substring(0,4) + " "+ sTime;
//                2021-01-19
                String newDate = String.format("%s-%s-%s", sDate.substring(8), sDate.substring(5, 7), sDate.substring(0, 4));
                String newDateTime = String.format("%s-%s-%s %s", sDate.substring(8), sDate.substring(5, 7), sDate.substring(0, 4), sTime);
                Log.d("TAG", "$$$$    :    " +newDate);
                Log.d("TAG", "$$$$    :    " +newDateTime);

                String fcDate = FC_Utility.getCurrentDate();
                String fcTime = FC_Utility.getCurrentTime();
                Log.d("TAG", "$$$$    :" +fcDate);
                Log.d("TAG", "$$$$    :" +fcTime);
                int t1 = Integer.parseInt(sTime.substring(0,2));
                int t1s = Integer.parseInt(sTime.substring(3,5));
                int t2 = Integer.parseInt(fcTime.substring(0,2));
                int t2s = Integer.parseInt(fcTime.substring(3,5));
                Log.d("TAG", "$$$$  T1  :" +t1 + "    "+t1s);
                Log.d("TAG", "$$$$  T2  :" +t2 + "    "+t2s);
                if(!fcDate.equalsIgnoreCase(newDate)){
                    showChangeDateDialog(newDate, sTime);
                }else {
                    if(t1>t2) {
                        if ((t1 - t2) > 1) {
                            Log.d("TAG", "$$$$  t1>t2  :" +t2 + "    "+t2s);
                            showChangeDateDialog(newDate, sTime);
                        }
                    }else if(t2>t1) {
                        if ((t2 - t1) > 1) {
                            Log.d("TAG", "$$$$  t2>t1  :" +t2 + "    "+t2s);
                            showChangeDateDialog(newDate, sTime);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    BlurPopupWindow changeDateDialog;
    @SuppressLint("SetTextI18n")
    private void showChangeDateDialog(String newDate, String sTime) {
        try {
            changeDateDialog = new BlurPopupWindow.Builder(this)
                    .setContentView(R.layout.app_date_dialog)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnTouchBackground(false)
                    .setDismissOnClickBack(false)
                    .setScaleRatio(0.2f)
                    .bindClickListener(v -> {
                        new Handler().postDelayed(() -> {
                            changeDateDialog.dismiss();
                            startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                        }, 200);
                    }, R.id.ok_btn)
                    .bindClickListener(v -> {
                        new Handler().postDelayed(() -> {
                            changeDateDialog.dismiss();
                        }, 200);
                    }, R.id.eject_btn)
                    .setBlurRadius(10)
                    .setTintColor(0x30000000)
                    .build();

            txt_push_dialog_msg = changeDateDialog.findViewById(R.id.txt_push_dialog_msg);
            txt_push_dialog_msg2 = changeDateDialog.findViewById(R.id.txt_push_dialog_msg2);
            ok_btn = changeDateDialog.findViewById(R.id.ok_btn);
            eject_btn = changeDateDialog.findViewById(R.id.eject_btn);

            String tm = "", tm2 = ""/*, type = "am", type2 = "am"*/;
/*
            int t1 = Integer.parseInt(sTime.substring(0, 2));
            int t2 = Integer.parseInt(FC_Utility.getCurrentTime().substring(0, 2));
            if (t1 >= 12)
                type = "pm";
            else
                type = "am";
            if (t2 >= 12)
                type2 = "pm";
            else
                type2 = "am";
*/
            tm = get12HrTime(sTime);
            tm2 = get12HrTime(FC_Utility.getCurrentTime().substring(0, 5));

            txt_push_dialog_msg.setText(this.getString(R.string.device_date_time_change) + " " + FC_Utility.getCurrentDate() + "\n" + tm2);
            txt_push_dialog_msg2.setText(this.getString(R.string.internet_date_time_change) + " " + newDate + "\n" + tm);
            changeDateDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receivedError(String header) {
        getInternetTime();
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
//        floating_info.setImageResource(R.drawable.ic_info_outline_white);
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
                                builder.setTargetView(tv_level).build();
                                break;
                            case R.id.tv_level:
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
    }

    private void updatingForDynamicLocationViews() {
        tv_level.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mGuideView.updateGuideViewLocation();
            }
        });
    }

    private void getCompletion() {
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessage(SECTION_COMPLETION_PERC);
        EventBus.getDefault().post(eventMessage);
    }

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
    public void setActualLevel(List<ContentTable> level_List, String title, int setlevel) {
        rootList = level_List;
        if (rootList != null)
            sortContentList(rootList);
        levelTitle = title;
        FastSave.getInstance().saveString(FC_Constants.CURRENT_LEVEL_NAME, levelTitle);
        FC_Constants.currentLevel = setlevel;
        setLevel();
    }

    @UiThread
    public void setLevel() {
        SubmarineItem[] item = new SubmarineItem[rootList.size()];
        for (int j = 0; j < rootList.size(); j++) {
            item[j] = new SubmarineItem(rootList.get(j).getNodeTitle(), null);
        }

        try {
            FastSave.getInstance().saveInt(FC_Constants.CURRENT_LEVEL, currentLevel);
            tv_level.setText(levelTitle);
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
            FC_Constants.currentLevel = rootList.get(position).getSeq_no();
            FC_Constants.levelNodeID = rootList.get(position).getNodeId();
//            changeBGNew(FC_Constants.currentLevel);
            FastSave.getInstance().saveInt(FC_Constants.CURRENT_LEVEL, currentLevel);
            levelTitle = rootList.get(position).getNodeTitle();
            FastSave.getInstance().saveString(FC_Constants.CURRENT_LEVEL_NAME, levelTitle);
            tv_level.setText("" + levelTitle);
            EventMessage eventMessage = new EventMessage();
            eventMessage.setMessage(LEVEL_CHANGED);
            EventBus.getDefault().post(eventMessage);
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
            submarine.addSubmarineItem(item[i]);
        }
    }

    private void changeBGNew(int currentLevel) {
//        Change Background to distinguish it by different color.
        tabLayout.setBackground(getResources().getDrawable(R.drawable.home_footer_3_bg));
/*
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
*/
    }

    @SuppressLint("SetTextI18n")
    private void setupTabIcons() {
//        Add Tab Icons
        TextView learningTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_text, null);
        learningTab.setText("" + getResources().getString(R.string.Learning));
        learningTab.setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.ic_learning, 0, 0);

        TextView profileTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_text, null);
        profileTab.setText("" + getResources().getString(R.string.Profile));
        profileTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile, 0, 0);

        tabLayout.getTabAt(0).setCustomView(learningTab);
        tabLayout.getTabAt(1).setCustomView(profileTab);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    BackBtnSound.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                BackupDatabase.backup(HomeActivity.this);
                BackupDatabase.backup(HomeActivity.this);
                if (new File(Environment.getExternalStorageDirectory().toString()
                        + "/.FCAInternal/DBZip").exists())
                    new File(Environment.getExternalStorageDirectory().toString()
                            + "/.FCAInternal/DBZip").delete();

                if (tab.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.Profile))) {
                    FastSave.getInstance().saveString(APP_SECTION, sec_Profile);
                    header_rl.setVisibility(View.GONE);
                    EventMessage eventMessage = new EventMessage();
                    eventMessage.setMessage(FRAGMENT_SELECTED);
                    EventBus.getDefault().post(eventMessage);
                } else {
                    FastSave.getInstance().saveString(APP_SECTION, sec_Learning);
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

                if (new File(Environment.getExternalStorageDirectory().toString()
                        + "/.FCAInternal/DBZip").exists())
                    new File(Environment.getExternalStorageDirectory().toString()
                            + "/.FCAInternal/DBZip").delete();

                if (tab.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.Profile))) {
                    FastSave.getInstance().saveString(APP_SECTION, sec_Profile);
                    EventMessage eventMessage = new EventMessage();
                    eventMessage.setMessage(FRAGMENT_RESELECTED);
                    EventBus.getDefault().post(eventMessage);
                    header_rl.setVisibility(View.GONE);
                } else {
                    FastSave.getInstance().saveString(APP_SECTION, sec_Learning);
                    EventMessage eventMessage = new EventMessage();
                    eventMessage.setMessage(FRAGMENT_RESELECTED);
                    EventBus.getDefault().post(eventMessage);
                    header_rl.setVisibility(View.VISIBLE);
                }
                getCompletion();
            }
        });
    }

    BlurPopupWindow myDialog;
/*
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
    }
*/

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

    @Click(R.id.tv_level)
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
        viewpager.setOffscreenPageLimit(2);
        adapter.addFrag(new LearningFragment_(), "" + getResources().getString(R.string.Learning));
        adapter.addFrag(new ProfileFragment_(), "" + getResources().getString(R.string.Profile));
/*        if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GR`OUP_MODE).contains("group") || !getAppMode()) {
            adapter.addFrag(new LearningFragment_(), "" + getResources().getString(R.string.Learning));
            adapter.addFrag(new PracticeFragment_(), "" + getResources().getString(R.string.Practice));
            if (currSubj.equalsIgnoreCase("english")) {
                adapter.addFrag(new TestFragment_(), "" + getResources().getString(R.string.Test));
                adapter.addFrag(new FunFragment_(), "" + getResources().getString(R.string.Fun));
            } else *//*if (currSubj.equalsIgnoreCase("LS_Science") || currSubj.equalsIgnoreCase("Maths"))*//* {
                adapter.addFrag(new TestFragment_(), "" + getResources().getString(R.string.Test));
            }
            adapter.addFrag(new ProfileFragment_(), "" + getResources().getString(R.string.Profile));
        } else {
            adapter.addFrag(new PracticeFragment_(), "" + getResources().getString(R.string.Practice));
            adapter.addFrag(new LearningFragment_(), "" + getResources().getString(R.string.Learning));
            if (currSubj.equalsIgnoreCase("english")) {
                adapter.addFrag(new TestFragment_(), "" + getResources().getString(R.string.Test));
                adapter.addFrag(new FunFragment_(), "" + getResources().getString(R.string.Fun));
            } else *//*if (currSubj.equalsIgnoreCase("LS_Science") || currSubj.equalsIgnoreCase("Maths"))*//* {
                adapter.addFrag(new TestFragment_(), "" + getResources().getString(R.string.Test));
            }
            adapter.addFrag(new ProfileFragment_(), "" + getResources().getString(R.string.Profile));
        }*/
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
        //TODO check this
        backBtnPressed();
//        super.onBackPressed();
//        finish();
    }
}