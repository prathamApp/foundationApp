package com.pratham.foundation.ui.app_home.display_content;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.Modal_FileDownloading;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.app_home.learning_fragment.attendance_bottom_fragment.AttendanceBottomFragment;
import com.pratham.foundation.ui.app_home.learning_fragment.attendance_bottom_fragment.AttendanceBottomFragment_;
import com.pratham.foundation.ui.contentPlayer.ContentPlayerActivity_;
import com.pratham.foundation.ui.contentPlayer.matchingPairGame.MatchThePairGameActivity;
import com.pratham.foundation.ui.contentPlayer.old_cos.conversation.ConversationActivity_;
import com.pratham.foundation.ui.contentPlayer.old_cos.reading_cards.ReadingCardsActivity_;
import com.pratham.foundation.ui.contentPlayer.opposites.OppositesActivity_;
import com.pratham.foundation.ui.contentPlayer.pdf_display.Fragment_PdfViewer_;
import com.pratham.foundation.ui.contentPlayer.reading_paragraphs.ReadingParagraphsActivity_;
import com.pratham.foundation.ui.contentPlayer.reading_rhyming.ReadingRhymesActivity_;
import com.pratham.foundation.ui.contentPlayer.reading_story_activity.ReadingStoryActivity_;
import com.pratham.foundation.ui.contentPlayer.video_player.ActivityVideoPlayer_;
import com.pratham.foundation.ui.contentPlayer.vocabulary_qa.ReadingVocabularyActivity_;
import com.pratham.foundation.ui.contentPlayer.web_view.WebViewActivity_;
import com.pratham.foundation.ui.contentPlayer.webviewpdf.PDFViewActivity_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.ApplicationClass.BackBtnSound;
import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.ApplicationClass.fileDownloadingList;
import static com.pratham.foundation.ui.app_home.HomeActivity.languageChanged;
import static com.pratham.foundation.utility.FC_Constants.DOWNLOAD_NODE_ID;
import static com.pratham.foundation.utility.FC_Constants.GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.IS_DOWNLOADING;
import static com.pratham.foundation.utility.FC_Constants.LOGIN_MODE;
import static com.pratham.foundation.utility.FC_Constants.QR_GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.currentLevel;
import static com.pratham.foundation.utility.FC_Constants.currentSubjectFolder;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;
import static com.pratham.foundation.utility.FC_Utility.get12HrTime;


@EActivity(R.layout.activity_content_display)
public class ContentDisplay extends BaseActivity implements ContentContract.ContentView, ContentClicked {

    //initialize the presenter.
    @Bean(ContentPresenter.class)
    ContentContract.ContentPresenter presenter;

    private RecyclerView recyclerView;
    private ContentAdapter contentAdapter;
    int tempDownloadPos, resumeCntr = 0;
    String resName, resServerImageName, parentName;
    List<ContentTable> ContentTableList;
    ProgressBar dialog_roundProgress;
    ProgressLayout progressLayout;
    TextView dialog_file_name;
    ImageView iv_file_trans;
    TextView txt_push_dialog_msg, txt_push_dialog_msg2;
    TextView txt_push_error;
    RelativeLayout rl_btn;
    Button ok_btn, eject_btn;

    @ViewById(R.id.tv_header_progress)
    TextView tv_header_progress;
    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.tv_Activity)
    TextView tv_Activity;
    @ViewById(R.id.ll_topic_parent)
    LinearLayout ll_topic_parent;
    @ViewById(R.id.iv_level)
    ImageView iv_level;
    @ViewById(R.id.tv_level)
    TextView tv_level;
    String nodeId, level, contentTitle;
    @ViewById(R.id.home_root_layout)
    RelativeLayout homeRoot;
    @ViewById(R.id.header_rl)
    RelativeLayout header_rl;
    //    @ViewById(R.id.tv_progress)
//    TextView tv_progress;
//    @ViewById(R.id.card_progressLayout)
//    ProgressLayout level_progress;

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

    @AfterViews
    public void initialize() {
        Runtime rs = Runtime.getRuntime();
        rs.freeMemory();
        rs.gc();
        rs.freeMemory();
        resumeCntr = 0;
        nodeId = getIntent().getStringExtra("nodeId");
        contentTitle = getIntent().getStringExtra("contentTitle");
        parentName = getIntent().getStringExtra("parentName");
        level = getIntent().getStringExtra("level");
//        changeBG(Integer.parseInt(level));
        //Setting the instance of view in the presenter.
        presenter.setView(ContentDisplay.this);
        ContentTableList = new ArrayList<>();
        recyclerView = findViewById(R.id.attendnce_recycler_view);
        contentAdapter = new ContentAdapter(this, ContentTableList, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        int dp = 5;
        if (FC_Constants.TAB_LAYOUT)
            dp = 10;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(this, dp), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contentAdapter);

        showLoader();
        presenter.addNodeIdToList(nodeId);

        // Setting the app local to ensure that the instruction and other strings come in the specified language.
        String a = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI);
        Log.d("INSTRUCTIONFRAG", "Select Subj: " + a);
        FC_Utility.setAppLocal(this, a);

//        presenter.displayProfileImage();
        presenter.getPerc(nodeId);
        presenter.getInternetTime();
        tv_Topic.setText("" + contentTitle);
        tv_Topic.setSelected(true);
        ll_topic_parent.setSelected(true);
        tv_Activity.setText("" + parentName);
//        add node for maintaining list
//        get child node and display
        presenter.getListData();

    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @SuppressLint("SetTextI18n")
    private void changeBG(/*int levelNo*/) {
        Log.d("CURRENT_LEVEL_NAME", "changeBG: "+FastSave.getInstance().getString(FC_Constants.CURRENT_LEVEL_NAME, ""));
        tv_level.setText("" + FastSave.getInstance().getString(FC_Constants.CURRENT_LEVEL_NAME, ""));
//        set level no
//        iv_level.setBackground(getResources().getDrawable(R.drawable.home_footer_3_bg));
    }

    @UiThread
    @Override
    public void setStudentProfileImage(String sImage) {
        presenter.addNodeIdToList(nodeId);
        presenter.getListData();
    }

    @UiThread
    @Override
    public void setHeaderProgress(int percent) {
        if(percent>100)
            percent = 100;
        tv_header_progress.setText("" + percent + "%");
//        level_progress.setCurProgress(percent);
    }

    @Override
    public void clearContentList() {
        ContentTableList.clear();
    }

    @UiThread
    @Override
    public void addContentToViewList(List<ContentTable> contentTable) {
        ContentTableList.addAll(contentTable);
    }

    @Click(R.id.profileImage)
    public void loadProfile() {
//        startActivity(new Intent(ContentDisplay.this, Student_profile_activity.class), ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
    }

    @UiThread
    @Override
    public void notifyAdapter() {
//        sort the list by seq no
        Collections.sort(ContentTableList, (o1, o2) -> o1.getSeq_no() - o2.getSeq_no());
        contentAdapter.notifyDataSetChanged();
        new Handler().postDelayed(this::dismissLoadingDialog, 300);
    }

    private void hideSystemUI() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeBG();
        if (!languageChanged) {
            hideSystemUI();
            Log.d("HomeActivityTAG", "ActivityResumed: ");
            if (resumeCntr == 0)
                resumeCntr = 1;
            else {
                hideSystemUI();
                showLoader();
                presenter.getListData();
                if (!LOGIN_MODE.equalsIgnoreCase(QR_GROUP_MODE))
                    presenter.getPerc(nodeId);
//                notifyAdapter();
            }
        } else
            finish();
    }

    @Click(R.id.main_back)
    public void backClicked() {
        onBackPressed();
    }

    /*    @UiThread
        @Click(R.id.dw_list_btn)
        public void dwBtnClicked() {
            DownloadBottomSheetFragment_ downloadBottomSheetFragment = new DownloadBottomSheetFragment_();
            downloadBottomSheetFragment.show(getSupportFragmentManager(), DownloadBottomSheetFragment.class.getSimpleName());
        }*/
    BlurPopupWindow noDataDlg;

    @UiThread
    @SuppressLint("SetTextI18n")
    @Override
    public void showNoDataDownloadedDialog() {
        noDataDlg = new BlurPopupWindow.Builder(ContentDisplay.this)
                .setContentView(R.layout.fc_custom_dialog)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(true)
                .setScaleRatio(0.2f)
                .bindClickListener(v -> {
                    new Handler().postDelayed(() -> {
                        main_back.performClick();
                        noDataDlg.dismiss();
                    }, 200);
                }, R.id.dia_btn_green)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();

        TextView title = noDataDlg.findViewById(R.id.dia_title);
        Button btn_gree = noDataDlg.findViewById(R.id.dia_btn_green);
        Button btn_yellow = noDataDlg.findViewById(R.id.dia_btn_yellow);
        Button btn_red = noDataDlg.findViewById(R.id.dia_btn_red);
        btn_gree.setText("Ok");
        title.setText("Connect to Internet");
        btn_red.setVisibility(View.GONE);
        btn_yellow.setVisibility(View.GONE);
        noDataDlg.show();
    }

    @Override
    public void onContentClicked(int position, String nId) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        ContentTableList.clear();
        presenter.addNodeIdToList(nId);
        presenter.getListData();
    }

    @Override
    public void onTestContentClicked(int posi, ContentTable itemContent) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        FC_Constants.AssLang = itemContent.getContentLanguage();
        FC_Constants.examId = itemContent.getNodeKeywords();
        if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, GROUP_MODE).equalsIgnoreCase(GROUP_MODE)) {
            List<Groups> groupsStudentsList;
            groupsStudentsList = AppDatabase.getDatabaseInstance(this).getGroupsDao().GetStudentsByGroupId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            openAttendanceDialog(groupsStudentsList);
        } else {
//        Toast.makeText(ContentDisplay.this, "Opening Pankh Practice App", Toast.LENGTH_SHORT).show();
            try {
                String profileName = "";
                profileName = AppDatabase.getDatabaseInstance(this).getStudentDao().getFullName(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));

                Bundle bundle = new Bundle();
                FastSave.getInstance().getString(FC_Constants.CURRENT_FOLDER_NAME, currentSubjectFolder);

                bundle.putString("appName", "" + getResources().getString(R.string.app_name));
                bundle.putString("studentId", "" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                bundle.putString("studentName", "" + profileName);
                bundle.putString("subjectName", "" + FastSave.getInstance().getString(FC_Constants.CURRENT_SUBJECT, ""));
                bundle.putString("subjectLanguage", "" + itemContent.getContentLanguage());
                bundle.putString("examId", "" + itemContent.getNodeKeywords());
                bundle.putString("subjectLevel", "" + currentLevel);
                Intent launchIntent = new Intent("com.pratham.assessment.ui.choose_assessment.science.ScienceAssessmentActivity_");
                Objects.requireNonNull(launchIntent).putExtras(bundle);
                presenter.addScoreToDB(itemContent.getNodeKeywords());
                startActivityForResult(launchIntent, FC_Constants.APP_INTENT_REQUEST_CODE);
                // null pointer check in case package name was not found
            } catch (Exception e) {
                downloadAssessmentAppDialog();
                e.printStackTrace();
            }
        }
    }

    @UiThread
    public void openAttendanceDialog(List<Groups> groupsList) {
        AttendanceBottomFragment_ bottomStudentsFragment = new AttendanceBottomFragment_();
        bottomStudentsFragment.show(getSupportFragmentManager(),
                AttendanceBottomFragment.class.getSimpleName());
    }

    BlurPopupWindow fcDialog;

    @SuppressLint("SetTextI18n")
    @UiThread
    public void downloadAssessmentAppDialog() {
        try {
            fcDialog = new BlurPopupWindow.Builder(ContentDisplay.this)
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

            dia_btn_red.setText(ContentDisplay.this.getResources().getString(R.string.Cancel));
            dia_title.setText("Please Download Assessment App From Google Play Store");
            dia_btn_green.setText(getResources().getString(R.string.Okay));

            fcDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void onPreResOpenClicked(int position, String nId, String title, boolean onSDCard) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        String sdStatus = "F";
        if (onSDCard)
            sdStatus = "T";

        Intent mainNew = new Intent(ContentDisplay.this, ContentPlayerActivity_.class);
        mainNew.putExtra("nodeID", nId);
        mainNew.putExtra("title", title);
        mainNew.putExtra("onSDCard", onSDCard);
        mainNew.putExtra("sdStatus", sdStatus);
        startActivity(mainNew);
//        startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
    }


    @Override
    public void onContentOpenClicked(int position, String nodeId) {
//        contentopenClicked
//        check the type of resource and open the respective activity
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        resName = ContentTableList.get(position).getNodeTitle();
        if (ContentTableList.get(position).getNodeType().equalsIgnoreCase("PreResource") ||
                ContentTableList.get(position).getResourceType().equalsIgnoreCase("PreResource")) {
            String sdStatus = "F";
            if (ContentTableList.get(position).isOnSDCard())
                sdStatus = "T";

            Intent mainNew = new Intent(ContentDisplay.this, ContentPlayerActivity_.class);
            mainNew.putExtra("nodeID", ContentTableList.get(position).getNodeId());
            mainNew.putExtra("title", ContentTableList.get(position).getNodeTitle());
            mainNew.putExtra("onSDCard",  ContentTableList.get(position).isOnSDCard());
            mainNew.putExtra("sdStatus", sdStatus);
            startActivity(mainNew);

/*            Intent mainNew = new Intent(ContentDisplay.this, ContentPlayerActivity_.class);
            mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
            mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            mainNew.putExtra("contentName", ContentTableList.get(position).getNodeTitle());
            mainNew.putExtra("onSdCard",);
            mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
            startActivity(mainNew);*/
//            startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
        } else {
            if (ContentTableList.get(position).getResourceType().toLowerCase()
                    .contains(FC_Constants.HTML_GAME_RESOURCE)) {
                String resPath = null;
                String gameID = ContentTableList.get(position).getResourceId();
                if (ContentTableList.get(position).isOnSDCard())
                    resPath = ApplicationClass.contentSDPath + gameFolderPath + "/" +
                            ContentTableList.get(position).getResourcePath();
                else
                    resPath = ApplicationClass.foundationPath + gameFolderPath + "/" +
                            ContentTableList.get(position).getResourcePath();
                File file = new File(resPath);
                if (file.exists()) {
                    Uri path = Uri.fromFile(file);
                    Intent mainNew = new Intent(ContentDisplay.this, WebViewActivity_.class);
                    mainNew.putExtra("resPath", path.toString());
                    mainNew.putExtra("resId", gameID);
                    mainNew.putExtra("mode", "normal");
                    mainNew.putExtra("gameLevel", "" + ContentTableList.get(position).getNodeDesc());
                    mainNew.putExtra("gameName", "" + ContentTableList.get(position).getNodeTitle());
                    mainNew.putExtra("gameType", "" + ContentTableList.get(position).getResourceType());
                    mainNew.putExtra("gameCategory", "" + ContentTableList.get(position).getNodeKeywords());
                    startActivity(mainNew);
//                    startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
                } else {
                    Toast.makeText(this, "Game not found", Toast.LENGTH_SHORT).show();
                }
/*        } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.RC_RESOURCE)) {
//            presenter.enterRCData(contentList);*/
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.CONVO_RESOURCE)) {
                Intent mainNew = new Intent(ContentDisplay.this, ConversationActivity_.class);
                mainNew.putExtra("storyId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", ContentTableList.get(position).getNodeTitle());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                startActivity(mainNew);
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.COMIC_CONVO_RESOURCE)) {
                Intent mainNew = new Intent(ContentDisplay.this, ReadingCardsActivity_.class);
                mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", ContentTableList.get(position).getNodeTitle());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                startActivity(mainNew);
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.OPPOSITE_WORDS)) {
                Intent mainNew = new Intent(ContentDisplay.this, OppositesActivity_.class);
                mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", ContentTableList.get(position).getNodeTitle());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
                startActivity(mainNew);
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.RHYME_RESOURCE)
                    || ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.STORY_RESOURCE)) {
                Intent mainNew = new Intent(ContentDisplay.this, ReadingStoryActivity_.class);
                mainNew.putExtra("storyId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("storyPath", ContentTableList.get(position).getResourcePath());
                mainNew.putExtra("storyTitle", ContentTableList.get(position).getNodeTitle());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("sttLang", ContentTableList.get(position).getContentLanguage());
                mainNew.putExtra("contentType", ContentTableList.get(position).getResourceType());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
                startActivity(mainNew);
            } /*else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.WORD_ANDROID)) {
            Intent mainNew = new Intent(ContentDisplay.this, ReadingWordScreenActivity.class);
            mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
            mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
            mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
            mainNew.putExtra("contentTitle", ContentTableList.get(position).getNodeTitle());
            startActivity(mainNew);
        } */ else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.PARA_ANDROID)) {
                Intent mainNew = new Intent(ContentDisplay.this, ReadingParagraphsActivity_.class);
                mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("resType", ContentTableList.get(position).getResourceType());
                mainNew.putExtra("contentTitle", ContentTableList.get(position).getNodeTitle());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
                startActivity(mainNew);
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.PARA_VOCAB_ANDROID)) {
                Intent mainNew = new Intent(ContentDisplay.this, ReadingParagraphsActivity_.class);
                mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("resType", ContentTableList.get(position).getResourceType());
                mainNew.putExtra("sttLang", ContentTableList.get(position).getContentLanguage());
                mainNew.putExtra("contentTitle", ContentTableList.get(position).getNodeTitle());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
                startActivity(mainNew);
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.VOCAB_ANDROID)) {
                Intent mainNew = new Intent(ContentDisplay.this, ReadingVocabularyActivity_.class);
                mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                mainNew.putExtra("contentTitle", ContentTableList.get(position).getNodeTitle());
                mainNew.putExtra("vocabLevel", ContentTableList.get(position).getNodeDesc());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("sttLang", ContentTableList.get(position).getContentLanguage());
                mainNew.putExtra("vocabCategory", ContentTableList.get(position).getNodeKeywords());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
                startActivity(mainNew);
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.RHYMING_WORD_ANDROID)) {
                Intent mainNew = new Intent(ContentDisplay.this, ReadingRhymesActivity_.class);
                mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                mainNew.putExtra("contentTitle", ContentTableList.get(position).getNodeTitle());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("rhymeLevel", ContentTableList.get(position).getNodeDesc());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
                startActivity(mainNew);
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.MATCH_THE_PAIR)) {
                Intent mainNew = new Intent(ContentDisplay.this, MatchThePairGameActivity.class);
                mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                mainNew.putExtra("contentTitle", ContentTableList.get(position).getNodeTitle());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("rhymeLevel", ContentTableList.get(position).getNodeDesc());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
                startActivity(mainNew);
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.YOUTUBE_LINK) ||
                    ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.VIDEO)) {
                Intent intent = new Intent(ContentDisplay.this, ActivityVideoPlayer_.class);
                intent.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                intent.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                intent.putExtra("resId", ContentTableList.get(position).getResourceId());
                intent.putExtra("contentName", ContentTableList.get(position).getNodeTitle());
                intent.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                intent.putExtra("contentType", ContentTableList.get(position).getResourceType());
//                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
                startActivity(intent);
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.PDF) ||
                    ContentTableList.get(position).getResourceType().equalsIgnoreCase("PDF")) {
                String sdStatus = "F";
                if (ContentTableList.get(position).isOnSDCard())
                    sdStatus = "T";
                Intent intent1 = new Intent(ContentDisplay.this, Fragment_PdfViewer_.class);
//                Intent intent1 = new Intent(ContentDisplay.this, PDFViewActivity_.class);
                intent1.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                intent1.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                intent1.putExtra("resId", ContentTableList.get(position).getResourceId());
                intent1.putExtra("contentName", ContentTableList.get(position).getNodeTitle());
                intent1.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                startActivity(intent1);
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase("PDF_ZOOM")
                    || ContentTableList.get(position).getResourceType().equalsIgnoreCase("PDF_new")) {
                String sdStatus = "F";
                if (ContentTableList.get(position).isOnSDCard())
                    sdStatus = "T";
//                Intent intent1 = new Intent(context, Fragment_PdfViewer_.class);
                Intent intent1 = new Intent(this, PDFViewActivity_.class);
                intent1.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                intent1.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                intent1.putExtra("resId", ContentTableList.get(position).getResourceId());
                intent1.putExtra("contentName", ContentTableList.get(position).getNodeTitle());
                intent1.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                startActivity(intent1);
            } else {
                String sdStatus = "F";
                if (ContentTableList.get(position).isOnSDCard())
                    sdStatus = "T";
                Intent mainNew = new Intent(ContentDisplay.this, ContentPlayerActivity_.class);
                mainNew.putExtra("nodeID", ContentTableList.get(position).getNodeId());
                mainNew.putExtra("title", ContentTableList.get(position).getNodeTitle());
                mainNew.putExtra("sdStatus", sdStatus);
                mainNew.putExtra("testData", ContentTableList.get(position));
                mainNew.putExtra("testcall", FC_Constants.INDIVIDUAL_MODE);
                startActivityForResult(mainNew, 1461);
            } /*else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.CHATBOT_ANDROID)) {
            Intent talkbot = new Intent(this, com.pratham.cityofstories.talkbot.feature.HomeActivity.class);
            startActivity(talkbot);
        }*/
        }
        resServerImageName = ContentTableList.get(position).getNodeServerImage();
    }

    BlurPopupWindow changeDateDialog;
    @SuppressLint("SetTextI18n")
    @UiThread
    public void showChangeDateDialog(String newDate, String sTime) {
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
            int t1 = Integer.parseInt(sTime.substring(0, 2));
            int t2 = Integer.parseInt(FC_Utility.getCurrentTime().substring(0, 2));
/*
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

            txt_push_dialog_msg.setText(this.getString(R.string.device_date_time_change) + "-\n" + FC_Utility.getCurrentDate() + "\n" + tm2);
            txt_push_dialog_msg2.setText(this.getString(R.string.internet_date_time_change) + "-\n" + newDate + "\n" + tm);
            changeDateDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onContentDownloadClicked(int position, String nodeId) {
        if (!IS_DOWNLOADING) {
            showLoader();
//        content download clicked
            try {
                ButtonClickSound.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
//        gather info
            DOWNLOAD_NODE_ID = "" + nodeId;
//        ContentTableList.get(position).setIsDownloading("true");
//        contentAdapter.notifyItemChanged(position, ContentTableList);
            resName = ContentTableList.get(position).getNodeTitle();
            resServerImageName = ContentTableList.get(position).getNodeServerImage();
            tempDownloadPos = position;
            if (FC_Utility.isDataConnectionAvailable(ContentDisplay.this)) {
                presenter.downloadResource(DOWNLOAD_NODE_ID, ContentTableList.get(position));
            } else
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Downloading other resource..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onContentDeleteClicked(int position, ContentTable contentList) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        Log.d("Delete_Clicked", "onClick: G_Activity");
        showDeleteDialog(position, contentList);
    }

    @SuppressLint("SetTextI18n")
    private void showDeleteDialog(int deletePos, ContentTable contentTableItem) {
        final CustomLodingDialog dialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.lottie_delete_dialog);
        TextView tv_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yes = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_no = dialog.findViewById(R.id.dia_btn_red);
        SimpleDraweeView iv_file_trans = dialog.findViewById(R.id.iv_file_trans);
        try {
            File file;
            if (contentTableItem.isOnSDCard())
                file = new File(ApplicationClass.contentSDPath +
                        "" + App_Thumbs_Path + contentTableItem.getNodeImage());
            else
                file = new File(ApplicationClass.foundationPath +
                        "" + App_Thumbs_Path + contentTableItem.getNodeImage());
            if (file.exists())
                Objects.requireNonNull(iv_file_trans).setImageURI(Uri.fromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_title.setText("Delete\n" + contentTableItem.getNodeTitle()+" ?");
        dialog.show();

        dia_btn_no.setOnClickListener(v -> dialog.dismiss());
        dia_btn_yes.setOnClickListener(v -> {
            presenter.deleteContent(deletePos, contentTableItem);
            dialog.dismiss();
        });
    }

    @UiThread
    @Override
    public void notifyAdapterItem(int deletePos) {
        ContentTableList.get(deletePos).setIsDownloaded("false");
        contentAdapter.notifyItemChanged(deletePos, ContentTableList.get(deletePos));
    }

    @Override
    public void onBackPressed() {
        try {
            BackBtnSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (presenter.removeLastNodeId()) {
            ContentTableList.clear();
            presenter.getListData();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    public CustomLodingDialog downloadDialog;

    @SuppressLint("SetTextI18n")
    @UiThread
    public void resourceDownloadDialog(Modal_FileDownloading modal_fileDownloading) {
        downloadDialog = null;
        downloadDialog = new CustomLodingDialog(ContentDisplay.this, R.style.FC_DialogStyle);
        downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(downloadDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        downloadDialog.setContentView(R.layout.dialog_file_downloading);
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();

        SimpleDraweeView iv_file_trans = downloadDialog.findViewById(R.id.iv_file_trans);
        dialog_file_name = downloadDialog.findViewById(R.id.dialog_file_name);
        progressLayout = downloadDialog.findViewById(R.id.dialog_progressLayout);
        dialog_roundProgress = downloadDialog.findViewById(R.id.dialog_roundProgress);
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse("" + resServerImageName))
                .setLocalThumbnailPreviewsEnabled(false)
                .build();
        if (imageRequest != null) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setOldController(iv_file_trans.getController())
                    .build();
            iv_file_trans.setController(controller);
        }
        dialog_file_name.setText("" + resName);
        progressLayout.setCurProgress(modal_fileDownloading.getProgress());
        downloadDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageReceived(EventMessage message) {
        if (message != null) {
            Modal_FileDownloading modal_fileDownloading = message.getModal_fileDownloading();
            if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_STARTED)) {
                dismissLoadingDialog();
//                show download dialog with progress bar
                resourceDownloadDialog(message.getModal_fileDownloading());
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_UPDATE)) {
//                SET progress bar value
                if (progressLayout != null)
                    progressLayout.setCurProgress(message.getModal_fileDownloading().getProgress());
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_ERROR) ||
                    message.getMessage().equalsIgnoreCase(FC_Constants.UNZIPPING_ERROR) ||
                    message.getMessage().equalsIgnoreCase(FC_Constants.RESPONSE_CODE_ERROR)) {
//                show error dialog if download failed
                dismissDownloadDialog();
                showDownloadErrorDialog();
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.UNZIPPING_DATA_FILE)) {
//                after download is completed show loader while unzipping the contents of the downloaded zip
                showZipLoader();
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_COMPLETE)) {
//                after unzipping update the DB and refresh the adapter
                dialog_file_name.setText("Updating Data");
                resName = "";
                ContentTableList.get(tempDownloadPos).setIsDownloaded("true");
                ContentTableList.get(tempDownloadPos).setNodeUpdate(false);
                new Handler().postDelayed(() -> {
                    dismissDownloadDialog();
//                    removeItemFromList(modal_fileDownloading);
                    contentAdapter.notifyItemChanged(tempDownloadPos, ContentTableList.get(tempDownloadPos));
                }, 500);
                BackupDatabase.backup(this);
            }

        }
    }

    private void removeItemFromList(Modal_FileDownloading modal_fileDownloading) {
        int index = 0;
        for (int x = 0; x < fileDownloadingList.size(); x++) {
            if (fileDownloadingList.get(x).getDownloadId().equalsIgnoreCase(DOWNLOAD_NODE_ID)) {
                index = x;
                break;
            }
        }
        fileDownloadingList.remove(index);
    }

    @UiThread
    public void dismissDownloadDialog() {
        try {
            if (!desFlag) {
                if (downloadDialog != null)
                    new Handler().postDelayed(() -> {
                        downloadDialog.dismiss();
                    }, 300);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void showZipLoader() {
        try {
            dialog_file_name.setText("Unzipping...\n" + resName + "\nPlease wait");
            progressLayout.setVisibility(View.GONE);
            dialog_roundProgress.setVisibility(View.VISIBLE);
            dialog_roundProgress.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CustomLodingDialog myLoadingDialog;

    @UiThread
    @Override
    public void showLoader() {
        try {
            if (myLoadingDialog == null) {
                myLoadingDialog = new CustomLodingDialog(this);
                myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myLoadingDialog.setContentView(R.layout.loading_dialog);
                myLoadingDialog.setCanceledOnTouchOutside(false);
                myLoadingDialog.show();
            } else if (!myLoadingDialog.isShowing())
                myLoadingDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void dismissLoadingDialog() {
        try {
            new Handler().postDelayed(() -> {
                if (myLoadingDialog != null && myLoadingDialog.isShowing() && !desFlag)
                    myLoadingDialog.dismiss();
            }, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean desFlag = false;

    @Override
    protected void onDestroy() {
        desFlag = true;
        super.onDestroy();
    }

    @UiThread
    @Override
    public void showToast(String msg) {
        Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();
    }

    CustomLodingDialog errorDialog;

    @UiThread
    public void showDownloadErrorDialog() {
        errorDialog = new CustomLodingDialog(ContentDisplay.this, R.style.FC_DialogStyle);
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(errorDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        errorDialog.setContentView(R.layout.dialog_file_error_downloading);
        errorDialog.setCanceledOnTouchOutside(false);
        Button dialog_error_btn = errorDialog.findViewById(R.id.dialog_error_btn);
        dialog_error_btn.setOnClickListener(v -> new Handler().postDelayed(() ->
                errorDialog.dismiss(), 200));
        errorDialog.show();
    }
}

/*<com.airbnb.lottie.LottieAnimationView
        android:id="@+id/dw_list_btn"
        android:layout_width="70dp"
        android:layout_height="70dp"
        android:layout_alignParentRight="true"
        android:layout_alignParentBottom="true"
        android:layout_marginEnd="20dp"
        android:layout_marginBottom="20dp"
        android:elevation="5dp"
        android:padding="@dimen/_5sdp"
        android:background="@drawable/dialog_image_bg"
        app:lottie_autoPlay="true"
        app:lottie_fileName="lottie_download.json"
        app:lottie_loop="true" />*/