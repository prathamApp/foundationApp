package com.pratham.foundation.ui.old_home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.ui.contentPlayer.ContentPlayerActivity_;
import com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment.FactRetrieval_;
import com.pratham.foundation.ui.contentPlayer.web_view.WebViewActivity;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.customView.CircularImageView;
import com.pratham.foundation.customView.discrete_view.DSVOrientation;
import com.pratham.foundation.customView.discrete_view.DiscreteScrollItemTransformer;
import com.pratham.foundation.customView.discrete_view.DiscreteScrollView;
import com.pratham.foundation.customView.discrete_view.ScaleTransformer;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.ContentTableNew;
import com.pratham.foundation.modalclasses.CertificateModelClass;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.Modal_FileDownloading;
import com.pratham.foundation.ui.home_screen.display_content.ContentDisplay_;
import com.pratham.foundation.ui.selectSubject.SelectSubject_;
import com.pratham.foundation.ui.student_profile.Student_profile_activity;
import com.pratham.foundation.ui.test.assessment_type.TestTypeActivity;
import com.pratham.foundation.ui.test.certificate.CertificateClicked;
import com.pratham.foundation.ui.test.supervisor.SupervisedAssessmentActivity;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_exit;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_restart;
import static com.pratham.foundation.utility.FC_Constants.isTest;
import static com.pratham.foundation.utility.FC_Constants.testSessionEnded;
import static com.pratham.foundation.utility.FC_Constants.testSessionEntered;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;
import static com.pratham.foundation.utility.SplashSupportActivity.ButtonClickSound;

@EActivity(R.layout.activity_home)
public class HomeActivity extends BaseActivity implements /*BottomNavigationView.OnNavigationItemSelectedListener,*/
        DiscreteScrollView.OnItemChangedListener,
        HomeContract.HomeView, ItemClicked, CertificateClicked {

    private Dialog myLoadingDialog;
    private int clicked_Pos = 0;
    @ViewById(R.id.navigation_view)
    BottomNavigationView navigationBottomView;
    @ViewById(R.id.full_level_progressLayout)
    ProgressLayout level_progress;
    @ViewById(R.id.tv_level)
    TextView tv_level;
    @ViewById(R.id.tv_studentName)
    TextView studentName;
    @ViewById(R.id.level_recycler)
    DiscreteScrollView level_recycler;
    @ViewById(R.id.test_recycler_view)
    RecyclerView test_recycler_view;
    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;
    @ViewById(R.id.home_root_layout)
    RelativeLayout homeRoot;
    @ViewById(R.id.test_lang_spinner)
    Spinner test_lang_spinner;
    @ViewById(R.id.profileImage)
    CircularImageView profileImage;
    @ViewById(R.id.btn_test_dw)
    Button btn_test_dw;
    @ViewById(R.id.header_rl)
    RelativeLayout header_rl;
    @DrawableRes(R.drawable.home_header_1_bg)
    Drawable homeHeader1;
    @DrawableRes(R.drawable.home_header_2_bg)
    Drawable homeHeader2;
    @DrawableRes(R.drawable.home_header_3_bg)
    Drawable homeHeader3;
    @DrawableRes(R.drawable.home_header_4_bg)
    Drawable homeHeader4;

    @Bean(HomePresenter.class)
    HomeContract.HomePresenter presenter;

    public static List<ContentTableNew> gameTestWebViewList;
    private List<ContentTableNew> contentParentList;
    private List<ContentTable> levelList;
    // --Commented out by Inspection (24-Jul-19 11:05 AM):public List<ContentTable> childDwContentList;
    private boolean navChanged = true;
    // --Commented out by Inspection (24-Jul-19 11:05 AM):API_Content api_content;
    private LevelAdapter levelAdapter;
    private RecyclerViewDataAdapter adapterParent;
    private String downloadNodeId,resName,resServerImageName,bottomNavNodeId,downloadType;
    private int currentLevelNo = 0;
    private int childPos = 0;
    private int parentPos = 0;
    private int resumeCntr = 0;
    private Dialog downloadDialog;
    private ProgressLayout progressLayout;
    private TextView dialog_file_name;
    private List<CertificateModelClass> testList;
    private TestAdapter testAdapter;
    private String language = "English",bottomSection = "Learning",certi_Code = "";
    public static String sub_nodeId="";
    public static boolean languageChanged = false;

    @AfterViews
    public void initialize() {
        Configuration config = getResources().getConfiguration();
/*        Log.d("HOME_ACT:", "Smallest Width: "+config.smallestScreenWidthDp);
        Log.d("HOME_ACT:", "Width DP: "+config.screenWidthDp);
        Log.d("HOME_ACT:", "Height DP: "+config.screenHeightDp);
        Log.d("HOME_ACT:", "INTERNAL PATH: " + ApplicationClass.pradigiPath);
        Log.d("HOME_ACT:", "SD-CARD PATH: " + ApplicationClass.contentSDPath);*/
        FC_Constants.TAB_LAYOUT = config.smallestScreenWidthDp > 425;
//        Log.d("HomeActivityTAG", "ActivityOnCreate      START: ");
        presenter.setView(HomeActivity.this);
        gameTestWebViewList = new ArrayList<>();
        resumeCntr = 0;
        sub_nodeId = getIntent().getStringExtra("nodeId");
        FC_Constants.currentSelectedLanguage = FastSave.getInstance().getString(FC_Constants.LANGUAGE, "");
        contentParentList = new ArrayList<>();
        levelList = new ArrayList<>();
        testList = new ArrayList<>();
        displayProfileNameAndImage();

        currentLevelNo = 0;
//        bottomNavNodeId = "4031";

        testAdapter = new TestAdapter(this, testList, language);
        RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(this, 1);
        test_recycler_view.setLayoutManager(myLayoutManager);
        test_recycler_view.setItemAnimator(new DefaultItemAnimator());
        test_recycler_view.setAdapter(testAdapter);

//        presenter.getLevelDataForList(0, bottomNavNodeId);
        presenter.getBottomNavId(currentLevelNo, "" + bottomSection);
        test_lang_spinner.setVisibility(View.GONE);

        navigationBottomView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        Log.d("HomeActivityTAG", "ActivityOnCreate      END: ");
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            try {
                levelList.clear();
                contentParentList.clear();
                presenter.clearNodeIds();
                presenter.clearLevelList();
                btn_test_dw.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            switch (menuItem.getItemId()) {
                case R.id.navigation_learning:
                    presenter.clearNodeIds();
                    navChanged = true;
                    bottomSection = "Learning";
//                    bottomNavNodeId = "4031";
                    FC_Constants.isPractice = false;
                    isTest = false;
                    levelList.clear();
                    if (!testSessionEnded) {
                        presenter.endTestSession();
                    }
                    test_lang_spinner.setVisibility(View.GONE);
                    my_recycler_view.setVisibility(View.VISIBLE);
                    test_recycler_view.setVisibility(View.GONE);
                    presenter.getBottomNavId(currentLevelNo, bottomSection);
//                    presenter.getLevelDataForList(currentLevelNo, bottomNavNodeId);
                    return true;
                case R.id.navigation_practice:
                    presenter.clearNodeIds();
                    bottomSection = "Practice";
//                    bottomNavNodeId = "4032";
                    navChanged = true;
                    FC_Constants.isPractice = true;
                    isTest = false;
                    if (!testSessionEnded) {
                        presenter.endTestSession();
                    }
                    levelList.clear();
                    test_lang_spinner.setVisibility(View.GONE);
                    my_recycler_view.setVisibility(View.VISIBLE);
                    test_recycler_view.setVisibility(View.GONE);
//                    presenter.getLevelDataForList(currentLevelNo, bottomNavNodeId);
                    presenter.getBottomNavId(currentLevelNo, bottomSection);
                    return true;
                case R.id.navigation_test:
                    presenter.clearNodeIds();
                    navChanged = true;
                    bottomSection = "Test";
//                    bottomNavNodeId = "4033";
                    FC_Constants.isPractice = false;
                    isTest = true;
                    testSessionEntered = false;
                    testSessionEnded = false;
                    showTestTypeSelectionDialog();
                    return true;
                case R.id.navigation_fun:
                    presenter.clearNodeIds();
                    navChanged = true;
                    bottomSection = "Fun";
//                    bottomNavNodeId = "4034";
                    FC_Constants.isPractice = false;
                    isTest = false;
                    if (!testSessionEnded) {
                        presenter.endTestSession();
                    }
                    test_lang_spinner.setVisibility(View.GONE);
                    my_recycler_view.setVisibility(View.VISIBLE);
                    test_recycler_view.setVisibility(View.GONE);
                    levelList.clear();
//                    presenter.getLevelDataForList(currentLevelNo, bottomNavNodeId);
                    presenter.getBottomNavId(currentLevelNo, bottomSection);
                    return true;
            }
            return false;
        }
    };

    @UiThread
    @Override
    public void setBotNodeId(String botID) {
        bottomNavNodeId = botID;
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
//            bottomNavNodeId = "4031";
            navigationBottomView.setSelectedItemId(R.id.navigation_learning);
            presenter.clearLevelList();
            presenter.clearNodeIds();
//            presenter.getLevelDataForList(0, bottomNavNodeId);
            bottomSection = "Learning";
            presenter.getBottomNavId(currentLevelNo, bottomSection);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("HomeActivityTAG", "ActivityResumed: ");
        if (resumeCntr == 0)
            resumeCntr = 1;
        else {
            if (!isTest && !languageChanged) {
                showLoader();
                presenter.getDataForList();
                String currentNodeID = presenter.getcurrentNodeID();
                try {
                    if (!currentNodeID.equalsIgnoreCase("na"))
                        presenter.findMaxScore("" + currentNodeID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (languageChanged) {
                languageChanged = false;
                presenter.clearLevelList();
                presenter.clearNodeIds();
                bottomSection = "Learning";
                navigationBottomView.setSelectedItemId(R.id.navigation_learning);
//                presenter.getBottomNavId(currentLevelNo, bottomSection);
            }
        }
    }

    @Click(R.id.btn_test_dw)
    void onDownLoadClick() {
        resName = levelList.get(currentLevelNo).getNodeTitle();
        resServerImageName = levelList.get(currentLevelNo).getNodeServerImage();
        downloadType = FC_Constants.TEST_DOWNLOAD;
        presenter.downloadResource(levelList.get(currentLevelNo).getNodeId());
    }

    @Override
    public void setLevelprogress(int percent) {
        setLevel_(percent);
    }

    @UiThread
    public void setLevel_(int percent) {
        level_progress.setCurProgress(percent);
    }

    @UiThread
    @Override
    public void setProfileName(String profileName) {
        studentName.setText(profileName);
    }

    @UiThread
    @Override
    public void setStudentProfileImage(String sImage) {
        if (sImage != null) {
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
        }
    }

    @UiThread
    public void displayProfileNameAndImage() {
        presenter.displayProfileName();
        presenter.displayProfileImage();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        try {
            if (adapterPosition < 0) {
                adapterPosition = 0;
            }
            if (!testSessionEnded) {
                presenter.endTestSession();
            }

            if (navChanged) {
                //in case adapterposition comes with a value =-1
                if (adapterPosition >= 0 && adapterPosition < levelList.size())
                    currentLevelNo = adapterPosition;
                else
                    currentLevelNo = 0;
                navChanged = false;
//                contentParentList.clear();
                tv_level.setText("" + levelList.get(currentLevelNo).getNodeTitle());
                changeBGNew("" + levelList.get(currentLevelNo).getNodeTitle());
                presenter.insertNodeId(levelList.get(currentLevelNo).getNodeId());
                if (bottomSection.equalsIgnoreCase("Test")) {
                    showLoader();
                    new Handler().postDelayed(() -> {
                        testList.clear();
                        testSessionEntered = false;
                        testSessionEnded = false;
                        FC_Constants.assessmentSession = "test-" + UUID.randomUUID().toString();
                        displayTest();
                    }, 200);
                } else
                    presenter.getDataForList();
            } else if (currentLevelNo != adapterPosition) {
                //in case adapterposition comes with a value =-1
                if (adapterPosition >= 0 && adapterPosition < levelList.size())
                    currentLevelNo = adapterPosition;
                else
                    currentLevelNo = 0;
                contentParentList.clear();
                presenter.clearNodeIds();
                presenter.clearLevelList();
                tv_level.setText("" + levelList.get(currentLevelNo).getNodeTitle());
                changeBGNew("" + levelList.get(currentLevelNo).getNodeTitle());
                presenter.insertNodeId(levelList.get(currentLevelNo).getNodeId());
                if (bottomSection.equalsIgnoreCase("Test")) {
                    showLoader();
                    new Handler().postDelayed(() -> {
                        testList.clear();
                        testSessionEntered = false;
                        testSessionEnded = false;
                        hideTestDownloadBtn();
                        FC_Constants.assessmentSession = "test-" + UUID.randomUUID().toString();
                        displayTest();
                    }, 200);
                } else
                    presenter.getDataForList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
    }

    private void hideTestDownloadBtnOnComplete() {
        if (levelList.get(currentLevelNo).isDownloaded.equalsIgnoreCase("true"))
            btn_test_dw.setVisibility(View.GONE);
        else
            btn_test_dw.setVisibility(View.VISIBLE);
    }

    @UiThread
    @Override
    public void displayCurrentDownloadedTest() {
        String jsonName = getLevelWiseJson();
        JSONArray testData = presenter.getTestData(jsonName);
        presenter.generateTestData(testData, levelList.get(currentLevelNo).getNodeId());
    }

    private void displayTest() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, getResources().getStringArray(R.array.certificate_Languages));
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        test_lang_spinner.setAdapter(dataAdapter);
        String jsonName = getLevelWiseJson();
        JSONArray testData = presenter.getTestData(jsonName);
        hideTestDownloadBtnOnComplete();
        presenter.generateTestData(testData, levelList.get(currentLevelNo).getNodeId());

        test_lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language = test_lang_spinner.getSelectedItem().toString();
                testAdapter = new TestAdapter(HomeActivity.this, testList, language);
                RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(HomeActivity.this, 1);
                test_recycler_view.setLayoutManager(myLayoutManager);
                test_recycler_view.setItemAnimator(new DefaultItemAnimator());
                test_recycler_view.setAdapter(testAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private String getLevelWiseJson() {
        String jsonName = "TestBeginnerJson.json";
        switch (currentLevelNo) {
            case 0:
                jsonName = "TestBeginnerJson.json";
                break;
            case 1:
                jsonName = "TestSubJuniorJson.json";
                break;
            case 2:
                jsonName = "TestJuniorJson.json";
                break;
            case 3:
                jsonName = "TestSubSeniorJson.json";
                break;
        }
        return jsonName;
    }

    public void changeBGNew(String levelName) {
        if (levelName.contains("1"))
            changeBG(0);
        if (levelName.contains("2"))
            changeBG(1);
        if (levelName.contains("3"))
            changeBG(2);
        if (levelName.contains("4"))
            changeBG(3);
        if (levelName.contains("5"))
            changeBG(4);
    }

    @UiThread
    public void changeBG(int levelNo) {
//        homeRoot.setBackground(oneDwBg);
        switch (levelNo) {
            case 0:
                header_rl.setBackground(homeHeader1);
                navigationBottomView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                //                homeRoot.setBackground(oneDwBg);
                break;
            case 1:
                header_rl.setBackground(homeHeader2);
                navigationBottomView.setBackgroundColor(getResources().getColor(R.color.level_2_color));
//                homeRoot.setBackground(twoDwBg);
                break;
            case 2:
                header_rl.setBackground(homeHeader3);
                navigationBottomView.setBackgroundColor(getResources().getColor(R.color.level_3_color));
//                homeRoot.setBackground(threeDwBg);
                break;
            case 3:
                header_rl.setBackground(homeHeader4);
                navigationBottomView.setBackgroundColor(getResources().getColor(R.color.level_4_color));
//                homeRoot.setBackground(fourDwBg);
                break;
        }
    }

    @UiThread
    @Override
    public void setSelectedLevel(List<ContentTable> contentTable) {
        levelList.clear();
        levelList.addAll(contentTable);
        Collections.sort(levelList, (o1, o2) -> o1.getNodeId().compareTo(o2.getNodeId()));
        if (levelAdapter == null) {
            levelAdapter = new LevelAdapter(this, levelList);
            level_recycler.setOrientation(DSVOrientation.VERTICAL);
            level_recycler.addOnItemChangedListener(this);
            level_recycler.setItemTransitionTimeMillis(200);
            level_recycler.setItemTransformer((DiscreteScrollItemTransformer) new ScaleTransformer.Builder().setMinScale(0.5f).build());
            level_recycler.setAdapter(levelAdapter);
        } else
            levelAdapter.notifyDataSetChanged();
    }

    @UiThread
    @Override
    public void addContentToViewList(List<ContentTableNew> contentParentList) {
        this.contentParentList.clear();
        this.contentParentList.addAll(contentParentList);
    }

    @UiThread
    @Override
    public void addContentToViewTestList(CertificateModelClass contentParentList) {
        CertificateModelClass ContentSplitted = splitResouces(contentParentList);
        testList.add(ContentSplitted);
    }

    private CertificateModelClass splitResouces(CertificateModelClass contentParentList) {
        String[] SplittedResId = contentParentList.getResourceId().split(";");
        String[] SplittedResPath = contentParentList.getResourcePath().split(";");

        if (SplittedResId.length > 0 && SplittedResPath.length > 0) {
            int random = FC_Utility.generateRandomNum(SplittedResId.length);
            contentParentList.setResourceId(SplittedResId[random]);
            contentParentList.setResourcePath(SplittedResPath[random]);
        }
        return contentParentList;
    }

    @UiThread
    @Override
    public void notifyAdapter() {
        sortAllList(contentParentList);
        if (adapterParent == null) {
            adapterParent = new RecyclerViewDataAdapter(this, contentParentList, this);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            my_recycler_view.setLayoutManager(mLayoutManager);
            my_recycler_view.addItemDecoration(new GridSpacingItemDecoration(1,dpToPx(this),true));
            my_recycler_view.setItemAnimator(new DefaultItemAnimator());
            my_recycler_view.setAdapter(adapterParent);
        } else
            adapterParent.notifyDataSetChanged();
        long delay;
        if (ApplicationClass.isTablet)
            delay = 500L;
        else
            delay = 300L;
        new Handler().postDelayed(() -> {
            dismissLoadingDialog();
            try {
                if (progressLayout != null)
                    downloadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delay);
    }

    private void sortAllList(List<ContentTableNew> contentParentList) {
        Collections.sort(contentParentList, (o1, o2) -> o1.getNodeId().compareTo(o2.getNodeId()));
        for (int i = 0; i < contentParentList.size(); i++) {
            List<ContentTableNew> contentChildList = contentParentList.get(i).getNodelist();
            Collections.sort(contentChildList, (o1, o2) -> o1.getNodeId().compareTo(o2.getNodeId()));
        }
    }

    @Override
    public void onContentClicked(ContentTableNew singleItem) {
        ButtonClickSound.start();
        if (singleItem.getResourceType().equalsIgnoreCase("category")) {
            Intent intent = new Intent(HomeActivity.this, ContentDisplay_.class);
            intent.putExtra("nodeId", singleItem.getNodeId());
            intent.putExtra("contentTitle", singleItem.getNodeTitle());
            intent.putExtra("level", "" + currentLevelNo);
            startActivity(intent);
        } else {
            contentParentList.clear();
            presenter.insertNodeId(singleItem.nodeId);
            presenter.getDataForList();
        }
    }

    @Override
    public void onContentOpenClicked(ContentTableNew contentList) {
        //Toast.makeText(this, "ContentOpen : Work In Progress", Toast.LENGTH_SHORT).show();
        //todo remove#
        ButtonClickSound.start();
        downloadNodeId = contentList.getNodeId();
        resName = contentList.getNodeTitle();
        if (contentList.getResourceType().toLowerCase().contains(FC_Constants.HTML_GAME_RESOURCE)) {
            String resPath;
            String gameID = contentList.getResourceId();
            if (contentList.isOnSDCard())
                resPath = ApplicationClass.contentSDPath + "/.FCA/English/Game/" + contentList.getResourcePath();
            else
                resPath = ApplicationClass.foundationPath + "/.FCA/English/Game/" + contentList.getResourcePath();
            File file = new File(resPath);
            Uri path = Uri.fromFile(file);
                Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
                intent.putExtra("resPath", path.toString());
                intent.putExtra("resId", gameID);
                intent.putExtra("mode", "normal");
                intent.putExtra("gameLevel", "" + contentList.getNodeDesc());
                intent.putExtra("gameType", "" + contentList.getResourceType());
                intent.putExtra("certiCode", contentList.getNodeDesc());
                intent.putExtra("gameCategory", "" + contentList.getNodeKeywords());
                startActivity(intent);
        }else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.IDENTIFY_KEYWORDS) ||
                contentList.getResourceType().equalsIgnoreCase(FC_Constants.FACT_RETRIVAL) ||
                contentList.getResourceType().equalsIgnoreCase(FC_Constants.KEY_WORD_MAPPING)) {
            Intent mainNew = new Intent(HomeActivity.this, ContentPlayerActivity_.class);
            mainNew.putExtra("resId", contentList.getResourceId());
            mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
            mainNew.putExtra("contentName", contentList.getNodeTitle());
            mainNew.putExtra("onSdCard", contentList.isOnSDCard());
            mainNew.putExtra("contentPath", contentList.getResourcePath());
            startActivity(mainNew);
        }



      /*  else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.RC_RESOURCE)) {
            presenter.enterRCData(contentList);
        } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.CONVO_RESOURCE)) {
            Intent mainNew = new Intent(TempHomeActivity.this, ConversationActivity_.class);
            mainNew.putExtra("storyId", contentList.getResourceId());
            mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
            mainNew.putExtra("contentName", contentList.getNodeTitle());
            mainNew.putExtra("onSdCard", contentList.isOnSDCard());
            mainNew.putExtra("certiCode", contentList.getNodeDesc());
            mainNew.putExtra("contentPath", contentList.getResourcePath());
            startActivity(mainNew);
        } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.COMIC_CONVO_RESOURCE)) {
            Intent mainNew = new Intent(TempHomeActivity.this, ReadingCardsActivity_.class);
            mainNew.putExtra("storyId", contentList.getResourceId());
            mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
            mainNew.putExtra("contentName", contentList.getNodeTitle());
            mainNew.putExtra("onSdCard", contentList.isOnSDCard());
            mainNew.putExtra("contentPath", contentList.getResourcePath());
            startActivity(mainNew);
        } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.RHYME_RESOURCE) || contentList.getResourceType().equalsIgnoreCase(FC_Constants.STORY_RESOURCE)) {
            Intent mainNew = new Intent(TempHomeActivity.this, ReadingStoryActivity_.class);
            mainNew.putExtra("storyId", contentList.getResourceId());
            mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
            mainNew.putExtra("storyPath", contentList.getResourcePath());
            mainNew.putExtra("storyTitle", contentList.getNodeTitle());
            mainNew.putExtra("onSdCard", contentList.isOnSDCard());
            mainNew.putExtra("contentType", contentList.getResourceType());
            startActivity(mainNew);
        } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.WORD_ANDROID)) {
            Intent mainNew = new Intent(TempHomeActivity.this, ReadingWordScreenActivity.class);
            mainNew.putExtra("resId", contentList.getResourceId());
            mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
            mainNew.putExtra("contentPath", contentList.getResourcePath());
            mainNew.putExtra("onSdCard", contentList.isOnSDCard());
            mainNew.putExtra("contentTitle", contentList.getNodeTitle());
            startActivity(mainNew);
        } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.PARA_ANDROID)) {
            Intent mainNew = new Intent(TempHomeActivity.this, ReadingParagraphsActivity_.class);
            mainNew.putExtra("resId", contentList.getResourceId());
            mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
            mainNew.putExtra("contentPath", contentList.getResourcePath());
            mainNew.putExtra("onSdCard", contentList.isOnSDCard());
            mainNew.putExtra("contentTitle", contentList.getNodeTitle());
            startActivity(mainNew);
        } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.VOCAB_ANDROID)) {
            Intent mainNew = new Intent(TempHomeActivity.this, ReadingVocabularyActivity_.class);
            mainNew.putExtra("resId", contentList.getResourceId());
            mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
            mainNew.putExtra("contentPath", contentList.getResourcePath());
            mainNew.putExtra("contentTitle", contentList.getNodeTitle());
            mainNew.putExtra("vocabLevel", contentList.getNodeDesc());
            mainNew.putExtra("onSdCard", contentList.isOnSDCard());
            mainNew.putExtra("vocabCategory", contentList.getNodeKeywords());
            startActivity(mainNew);
        } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.RHYMING_WORD_ANDROID)) {
            Intent mainNew = new Intent(TempHomeActivity.this, ReadingRhymesActivity_.class);
            mainNew.putExtra("resId", contentList.getResourceId());
            mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
            mainNew.putExtra("contentPath", contentList.getResourcePath());
            mainNew.putExtra("contentTitle", contentList.getNodeTitle());
            mainNew.putExtra("onSdCard", contentList.isOnSDCard());
            mainNew.putExtra("rhymeLevel", contentList.getNodeDesc());
            startActivity(mainNew);
        } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.OPPOSITE_WORDS)) {
            Intent mainNew = new Intent(TempHomeActivity.this, OppositesActivity_.class);
            mainNew.putExtra("resId", contentList.getResourceId());
            mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
            mainNew.putExtra("contentName", contentList.getNodeTitle());
            mainNew.putExtra("onSdCard", contentList.isOnSDCard());
            mainNew.putExtra("contentPath", contentList.getResourcePath());
            startActivity(mainNew);
        } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.MATCH_THE_PAIR)) {
            Intent mainNew = new Intent(TempHomeActivity.this, MatchThePairGameActivity.class);
            mainNew.putExtra("resId", contentList.getResourceId());
            mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
            mainNew.putExtra("contentName", contentList.getNodeTitle());
            mainNew.putExtra("onSdCard", contentList.isOnSDCard());
            mainNew.putExtra("contentPath", contentList.getResourcePath());
            startActivity(mainNew);
        } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.CHATBOT_ANDROID)) {
            Intent talkbot = new Intent(this, com.pratham.cityofstories.talkbot.feature.TempHomeActivity.class);
            startActivity(talkbot);
        }*/
        resServerImageName = contentList.getNodeServerImage();
    }

    @UiThread
    @Override
    public void onContentTestOpenClicked(ContentTableNew contentList) {
        try {
            gameTestWebViewList.clear();
            Intent intent = new Intent(this, TestTypeActivity.class);
            intent.putExtra("nodeId", contentList.getNodeId());
            startActivity(intent);
/*            String CertiTitle = contentList.getNodeTitle();
            gameTestWebViewList.clear();
            Intent intent = new Intent(this, CertificateActivity.class);
            intent.putExtra("nodeId", contentList.getNodeId());
            intent.putExtra("CertiTitle", CertiTitle);
            intent.putExtra("display", "text");
            startActivity(intent);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void seeMore(String nodeId, String nodeTitle) {
        Intent intent = new Intent(HomeActivity.this, ContentDisplay_.class);
        intent.putExtra("nodeId", nodeId);
        intent.putExtra("contentTitle", nodeTitle);
        intent.putExtra("level", "" + currentLevelNo);
        startActivity(intent);
    }

    @Override
    public void openRCGame(ContentTableNew contentList) {
        //todo remove#
      /*  if (!FC_Utility.isDataConnectionAvailable(this)) {
            showNoInternet(contentList);
        } else {
            Intent intent = new Intent(this, RCGameActivity.class);
            intent.putExtra("nodeId", contentList.getNodeId());
            intent.putExtra("resId", "10385");
            intent.putExtra("internet", true);
//            presenter.starContentEntry(""+ContentTableList.get(position).getResourceId(), "ReadingChallenge start");
            BackupDatabase.backup(this);
            startActivity(intent);
        }*/
    }

    @SuppressLint("SetTextI18n")
    private void showNoInternet(ContentTableNew contentList) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        Button exit_btn = dialog.findViewById(R.id.dia_btn_red);
        Button restart_btn = dialog.findViewById(R.id.dia_btn_yellow);
        TextView title = dialog.findViewById(R.id.dia_title);
        title.setText("For full game please connect to internet..");
        exit_btn.setText("Ok");
        dialog.show();
        restart_btn.setVisibility(View.GONE);

        //todo remove#
      /*  exit_btn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(TempHomeActivity.this, RCGameActivity.class);
            intent.putExtra("nodeId", contentList.getNodeId());
            intent.putExtra("resId", "10385");
            intent.putExtra("internet", false);
//                presenter.starContentEntry(""+ContentTableList.get(position).getResourceId(), "ReadingChallenge start");
            startActivity(intent);
        });*/
    }

    @Override
    public void onContentDownloadClicked(/*int position ,*/ContentTableNew contentList, int parentPos, int childPos, String downloadType) {

        this.downloadType = downloadType;
        downloadNodeId = contentList.getNodeId();
        ButtonClickSound.start();
//        downloadNodeId = "" + 1371;
        this.parentPos = parentPos;
        this.childPos = childPos;
        resName = contentList.getNodeTitle();
        resServerImageName = contentList.getNodeServerImage();
        if (FC_Utility.isDataConnectionAvailable(HomeActivity.this))
            presenter.downloadResource(downloadNodeId);
        else
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void resourceDownloadDialog(Modal_FileDownloading modal_fileDownloading) {
        downloadDialog = new Dialog(this);
        downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(downloadDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        downloadDialog.setContentView(R.layout.dialog_file_downloading);
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();
        progressLayout = downloadDialog.findViewById(R.id.dialog_progressLayout);
        dialog_file_name = downloadDialog.findViewById(R.id.dialog_file_name);
        ImageView iv_file_trans = downloadDialog.findViewById(R.id.iv_file_trans);
        Glide.with(this).load(resServerImageName).into(iv_file_trans);
        dialog_file_name.setText("" + resName);
        progressLayout.setCurProgress(modal_fileDownloading.getProgress());
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
            if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_STARTED)) {
                resourceDownloadDialog(message.getModal_fileDownloading());
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_UPDATE)) {
                if (progressLayout != null)
                    progressLayout.setCurProgress(message.getModal_fileDownloading().getProgress());
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_ERROR)) {
                downloadDialog.dismiss();
                showDownloadErrorDialog();
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.UNZIPPING_ERROR)) {
                downloadDialog.dismiss();
                showDownloadErrorDialog();
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.UNZIPPING_DATA_FILE)) {
                dialog_file_name.setText("Unzipping...\nPlease wait" + resName);
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_COMPLETE)) {
                dialog_file_name.setText("Updating Data");
                String folderPath = "";
                try {
                    if (downloadType.equalsIgnoreCase(FC_Constants.FULL_DOWNLOAD)) {
                        folderPath = contentParentList.get(parentPos).getResourcePath();
                        contentParentList.get(parentPos).setIsDownloaded("true");
                        presenter.updateDownloads();
                        presenter.updateCurrentNode(contentParentList.get(parentPos));
                        new Handler().postDelayed(() -> {
                            downloadDialog.dismiss();
                            adapterParent.notifyItemChanged(parentPos, contentParentList.get(parentPos));
                        }, 500);
                    } else if (downloadType.equalsIgnoreCase(FC_Constants.SINGLE_RES_DOWNLOAD)) {
                        folderPath = contentParentList.get(parentPos).getNodelist().get(childPos).getResourcePath();
                        contentParentList.get(parentPos).getNodelist().get(childPos).setIsDownloaded("true");
                        presenter.updateDownloads();
                        presenter.updateCurrentNode(contentParentList.get(parentPos));
                        new Handler().postDelayed(() -> {
                            downloadDialog.dismiss();
                            adapterParent.notifyItemChanged(parentPos, contentParentList.get(parentPos));
                        }, 500);
                    }
                    resName = "";
                    if (downloadType.equalsIgnoreCase(FC_Constants.TEST_DOWNLOAD)) {
                        presenter.updateDownloadJson(folderPath);
                    }
                } catch (Exception e) {
                    downloadDialog.dismiss();
                    dismissLoadingDialog();
                    e.printStackTrace();
                }
            }
        }
    }

    @UiThread
    @Override
    public void dismissDownloadDialog() {
        if (downloadDialog != null)
            downloadDialog.dismiss();
    }

    private void showDownloadErrorDialog() {
        Dialog errorDialog = new Dialog(this);
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(errorDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        errorDialog.setContentView(R.layout.dialog_file_error_downloading);
        errorDialog.setCanceledOnTouchOutside(false);
        errorDialog.show();
        Button ok_btn = errorDialog.findViewById(R.id.dialog_error_btn);

        ok_btn.setOnClickListener(v -> errorDialog.dismiss());
    }

    @Override
    public void onContentDeleteClicked(ContentTableNew contentList) {

    }

    @SuppressLint("SetTextI18n")
    @UiThread
    @Override
    public void showNoDataDownloadedDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView title = dialog.findViewById(R.id.dia_title);
        dialog.show();
        title.setText("Connect to Internet");
        Button btn_gree = dialog.findViewById(R.id.dia_btn_green);
        Button btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button btn_red = dialog.findViewById(R.id.dia_btn_red);

        btn_gree.setText("Ok");
        btn_red.setVisibility(View.GONE);
        btn_yellow.setVisibility(View.GONE);

        btn_gree.setOnClickListener(v -> {
            if (adapterParent != null) {
                contentParentList.clear();
                adapterParent.notifyDataSetChanged();
            }
            dialog.dismiss();
        });
    }

    private boolean loaderVisible = false;

    @UiThread
    @Override
    public void showLoader() {
        if (!loaderVisible) {
            loaderVisible = true;
            myLoadingDialog = new Dialog(this);
            myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(myLoadingDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myLoadingDialog.setContentView(R.layout.loading_dialog);
            myLoadingDialog.setCanceledOnTouchOutside(false);
//        myLoadingDialog.setCancelable(false);
            myLoadingDialog.show();
        }
    }

    @Override
    public void dismissLoadingDialog() {
        if (myLoadingDialog != null) {
            loaderVisible = false;
            myLoadingDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (presenter.removeLastNodeId()) {
            contentParentList.clear();
            presenter.getDataForList();
        } else {
            if (isTest) {
//                bottomNavNodeId = "4031";
                presenter.clearLevelList();
                presenter.clearNodeIds();
                bottomSection = "Learning";
                navigationBottomView.setSelectedItemId(R.id.navigation_learning);
                presenter.getBottomNavId(currentLevelNo, bottomSection);
//                presenter.getLevelDataForList(0, bottomNavNodeId);
            } else {
                exitDialog();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void exitDialog() {
        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fc_custom_dialog);
/*        Bitmap map=FC_Utility.takeScreenShot(TempHomeActivity.this);
        Bitmap fast=FC_Utility.fastblur(map, 20);
        final Drawable draw=new BitmapDrawable(getResources(),fast);
        dialog.getWindow().setBackgroundDrawable(draw);*/
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button next_btn = dialog.findViewById(R.id.dia_btn_green);
        Button test_btn = dialog.findViewById(R.id.dia_btn_yellow);
        Button revise_btn = dialog.findViewById(R.id.dia_btn_red);

        revise_btn.setText("" + dialog_btn_exit);
        test_btn.setText("" + dialog_btn_cancel);
        next_btn.setText("" + dialog_btn_restart);

        next_btn.setOnClickListener(v -> {
            endSession(HomeActivity.this);
            if (!ApplicationClass.isTablet) {
                startActivity(new Intent(HomeActivity.this, SelectSubject_.class));
            } else {
                startActivity(new Intent(HomeActivity.this, SelectSubject_.class));
            }
            presenter.clearNodeIds();
            finish();
            dialog.dismiss();
        });

        revise_btn.setOnClickListener(v -> {
            endSession(HomeActivity.this);
            presenter.clearNodeIds();
            finishAffinity();
            dialog.dismiss();
        });

        test_btn.setOnClickListener(v -> dialog.dismiss());
    }


    @Override
    public void doubleQuestionCheck() {
        for (int i = 0; i < testList.size(); i++) {
            for (int j = i + 1; j < testList.size(); j++) {
                if (testList.get(i).getCertiCode().equalsIgnoreCase(testList.get(j).getCertiCode())) {
                    testList.get(i).setCodeCount(testList.get(i).getCodeCount() + 1);
                }
            }
        }
    }

    @UiThread
    @Override
    public void initializeTheIndex() {
        test_recycler_view.removeAllViews();
        dismissLoadingDialog();
        if (testList.size() > 0) {
            testAdapter.notifyDataSetChanged();
        } else
            btn_test_dw.setVisibility(View.VISIBLE);
//        testAdapter.initializeIndex();
    }

    @UiThread
    @Override
    public void hideTestDownloadBtn() {
        btn_test_dw.setVisibility(View.GONE);
    }

    @Override
    public void notifyTestAdapter() {
        testAdapter.notifyDataSetChanged();
        if (downloadDialog != null) {
            downloadDialog.dismiss();
            hideTestDownloadBtn();
        }
    }

    @Override
    public void onCertificateClicked(int position, String nodeId) {
    }

    @Override
    public void onCertificateOpenGame(int position, String nodeId) {
        try {
            clicked_Pos = position;
            presenter.getTempData("" + nodeId);
            if (!testSessionEntered) {
                presenter.insertTestSession();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ContentTable splitResouces(ContentTable contentParentList) {
        String[] SplittedResId = contentParentList.getResourceId().split(";");
        String[] SplittedResPath = contentParentList.getResourcePath().split(";");

        if (SplittedResId.length > 0 && SplittedResPath.length > 0) {
            int random = FC_Utility.generateRandomNum(SplittedResId.length);
            contentParentList.setResourceId(SplittedResId[random]);
            contentParentList.setResourcePath(SplittedResPath[random]);
        }
        return contentParentList;
    }

    @Override
    public void testOpenData(ContentTable testData) {
        //todo remove#
        try {
            isTest = true;
            certi_Code = testData.getNodeDesc();
            if (testData.getResourceType().toLowerCase()
                    .contains(FC_Constants.HTML_GAME_RESOURCE)) {
                ContentTable testDataSplit = splitResouces(testData);
                String resPath;
                String gameID = testData.getResourceId();
                if (testData.isOnSDCard())
                    resPath = ApplicationClass.contentSDPath + "/.FCA/English/Game/" + testDataSplit.getResourcePath();
                else
                    resPath = ApplicationClass.foundationPath + "/.FCA/English/Game/" + testDataSplit.getResourcePath();
                File file = new File(resPath);
                if (file.exists()) {
                    Uri path = Uri.fromFile(file);
                    Intent mainNew = new Intent(HomeActivity.this, WebViewActivity.class);
                    mainNew.putExtra("resPath", path.toString());
                    mainNew.putExtra("resId", gameID);
                    mainNew.putExtra("mode", "test");
                    mainNew.putExtra("gameLevel", "" + testData.getNodeDesc());
                    mainNew.putExtra("gameName", "" + testData.getNodeTitle());
                    mainNew.putExtra("gameType", "" + testData.getResourceType());
                    mainNew.putExtra("gameCategory", "" + testData.getNodeKeywords());
                    startActivityForResult(mainNew, 1);
                } else {
                    Toast.makeText(this, "Game not found", Toast.LENGTH_SHORT).show();
                }
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.OPPOSITE_WORDS)) {
                Intent mainNew = new Intent(HomeActivity.this, FactRetrieval_.class);
                mainNew.putExtra("resId", testData.getResourceId());
                mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
                mainNew.putExtra("contentName", testData.getNodeTitle());
                mainNew.putExtra("onSdCard", testData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("contentPath", testData.getResourcePath());
                startActivityForResult(mainNew, 1);
            }


         /*   else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.CONVO_RESOURCE)) {
                ContentTable randomTestData = presenter.getRandomData(testData.getResourceType(), testData.getNodeKeywords());
                Intent mainNew = new Intent(TempHomeActivity.this, ConversationActivity_.class);
                mainNew.putExtra("storyId", randomTestData.getResourceId());
                mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
                mainNew.putExtra("contentName", randomTestData.getNodeTitle());
                mainNew.putExtra("onSdCard", randomTestData.isOnSDCard());
                mainNew.putExtra("contentPath", randomTestData.getResourcePath());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.COMIC_CONVO_RESOURCE)) {
                ContentTable randomTestData = presenter.getRandomData(testData.getResourceType(), testData.getNodeKeywords());
                Intent mainNew = new Intent(TempHomeActivity.this, ReadingCardsActivity_.class);
                mainNew.putExtra("storyId", randomTestData.getResourceId());
                mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
                mainNew.putExtra("contentName", randomTestData.getNodeTitle());
                mainNew.putExtra("onSdCard", randomTestData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("contentPath", randomTestData.getResourcePath());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.OPPOSITE_WORDS)) {
                Intent mainNew = new Intent(TempHomeActivity.this, OppositesActivity_.class);
                mainNew.putExtra("resId", testData.getResourceId());
                mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
                mainNew.putExtra("contentName", testData.getNodeTitle());
                mainNew.putExtra("onSdCard", testData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("contentPath", testData.getResourcePath());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.RHYME_RESOURCE) || testData.getResourceType().equalsIgnoreCase(FC_Constants.STORY_RESOURCE)) {
                ContentTable randomTestData = presenter.getRandomData(testData.getResourceType(), testData.getNodeKeywords());
                Intent mainNew = new Intent(TempHomeActivity.this, ReadingStoryActivity_.class);
                mainNew.putExtra("storyId", randomTestData.getResourceId());
                mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
                mainNew.putExtra("storyPath", randomTestData.getResourcePath());
                mainNew.putExtra("storyTitle", randomTestData.getNodeTitle());
                mainNew.putExtra("onSdCard", randomTestData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("contentType", randomTestData.getResourceType());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.WORD_ANDROID)) {
                Intent mainNew = new Intent(TempHomeActivity.this, ReadingWordScreenActivity.class);
                mainNew.putExtra("resId", testData.getResourceId());
                mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
                mainNew.putExtra("contentPath", testData.getResourcePath());
                mainNew.putExtra("onSdCard", testData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("contentTitle", testData.getNodeTitle());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.PARA_ANDROID)) {
                ContentTable randomTestData = presenter.getRandomData(testData.getResourceType(), testData.getNodeKeywords());
                Intent mainNew = new Intent(TempHomeActivity.this, ReadingParagraphsActivity_.class);
                mainNew.putExtra("resId", randomTestData.getResourceId());
                mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
                mainNew.putExtra("contentPath", randomTestData.getResourcePath());
                mainNew.putExtra("onSdCard", randomTestData.isOnSDCard());
                mainNew.putExtra("resType", randomTestData.getResourceType());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("contentTitle", randomTestData.getNodeTitle());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.PARA_VOCAB_ANDROID)) {
                ContentTable randomTestData = presenter.getRandomData(testData.getResourceType(), testData.getNodeKeywords());
                Intent mainNew = new Intent(TempHomeActivity.this, ReadingParagraphsActivity_.class);
                mainNew.putExtra("resId", randomTestData.getResourceId());
                mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
                mainNew.putExtra("contentPath", randomTestData.getResourcePath());
                mainNew.putExtra("onSdCard", randomTestData.isOnSDCard());
                mainNew.putExtra("resType", randomTestData.getResourceType());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("contentTitle", randomTestData.getNodeTitle());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.VOCAB_ANDROID)) {
                Intent mainNew = new Intent(TempHomeActivity.this, ReadingVocabularyActivity_.class);
                mainNew.putExtra("resId", testData.getResourceId());
                mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
                mainNew.putExtra("contentPath", testData.getResourcePath());
                mainNew.putExtra("contentTitle", testData.getNodeTitle());
                mainNew.putExtra("vocabLevel", testData.getNodeDesc());
                mainNew.putExtra("onSdCard", testData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("vocabCategory", "$$$");
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.RHYMING_WORD_ANDROID)) {
                ContentTable randomTestData = presenter.getRandomData(testData.getResourceType(), testData.getNodeKeywords());
                Intent mainNew = new Intent(TempHomeActivity.this, ReadingRhymesActivity_.class);
                mainNew.putExtra("resId", randomTestData.getResourceId());
                mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
                mainNew.putExtra("contentPath", randomTestData.getResourcePath());
                mainNew.putExtra("contentTitle", randomTestData.getNodeTitle());
                mainNew.putExtra("onSdCard", randomTestData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("rhymeLevel", randomTestData.getNodeDesc());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.MATCH_THE_PAIR)) {
                Intent mainNew = new Intent(TempHomeActivity.this, MatchThePairGameActivity.class);
                mainNew.putExtra("resId", testData.getResourceId());
                mainNew.putExtra("StudentID", FC_Constants.currentStudentID);
                mainNew.putExtra("contentPath", testData.getResourcePath());
                mainNew.putExtra("contentTitle", testData.getNodeTitle());
                mainNew.putExtra("onSdCard", testData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("rhymeLevel", testData.getNodeDesc());
                startActivityForResult(mainNew, 1);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
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
            checkAllAssessmentsDone();
        } else if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                presenter.clearNodeIds();
                navChanged = true;
                levelList.clear();
                //  JSONArray testData= presenter.getTestData();
                test_lang_spinner.setVisibility(View.VISIBLE);
                my_recycler_view.setVisibility(View.GONE);
                test_recycler_view.setVisibility(View.VISIBLE);
                bottomSection = "Test";
//                presenter.getLevelDataForList(currentLevelNo, bottomNavNodeId);
                presenter.getBottomNavId(currentLevelNo, bottomSection);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                try {
//                    bottomNavNodeId = "4031";
                    presenter.clearLevelList();
                    presenter.clearNodeIds();
                    navigationBottomView.setSelectedItemId(R.id.navigation_learning);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                presenter.getLevelDataForList(0, bottomNavNodeId);
            }
        }
    }

    private void checkAllAssessmentsDone() {
        boolean testGiven = true;
//        if (!certiMode.equalsIgnoreCase("display")) {
        JSONObject jsonObjectAssessment = new JSONObject();
        FC_Constants.currentLevel = currentLevelNo;
        for (int i = 0; i < testList.size(); i++) {
            try {
                if (testList.get(i).isAsessmentGiven()) {
                    jsonObjectAssessment.put("CertCode" + i + "_" + testList.get(i).getCertiCode(), "" + testList.get(i).getStudentPercentage());
                } else {
                    testGiven = false;
                    break;
                }
                //question
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (testGiven) {
            presenter.endTestSession();
            presenter.recordTestData(jsonObjectAssessment, "");
            new Handler().postDelayed(this::showTestCompleteDialog, 1000);
        }
//        }
    }

    @SuppressLint("SetTextI18n")
    private void showTestCompleteDialog() {
        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fc_custom_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView title = dialog.findViewById(R.id.dia_title);
        Button next_btn = dialog.findViewById(R.id.dia_btn_green);
        Button test_btn = dialog.findViewById(R.id.dia_btn_yellow);
        Button revise_btn = dialog.findViewById(R.id.dia_btn_red);

        title.setText("You can view the certificates in the profile section");
        next_btn.setText("Ok");
        revise_btn.setVisibility(View.GONE);
        test_btn.setVisibility(View.GONE);
        dialog.show();

        next_btn.setOnClickListener(v -> dialog.dismiss());
    }

    @Click(R.id.profileImage)
    public void loadProfile() {
        startActivity(new Intent(HomeActivity.this, Student_profile_activity.class));
    }
}
