package com.pratham.foundation.ui.home_screen.display_content;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.Modal_FileDownloading;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.contentPlayer.ContentPlayerActivity_;
import com.pratham.foundation.ui.contentPlayer.matchingPairGame.MatchThePairGameActivity;
import com.pratham.foundation.ui.contentPlayer.old_cos.conversation.ConversationActivity_;
import com.pratham.foundation.ui.contentPlayer.old_cos.reading_cards.ReadingCardsActivity_;
import com.pratham.foundation.ui.contentPlayer.opposites.OppositesActivity_;
import com.pratham.foundation.ui.contentPlayer.reading_paragraphs.ReadingParagraphsActivity_;
import com.pratham.foundation.ui.contentPlayer.reading_rhyming.ReadingRhymesActivity_;
import com.pratham.foundation.ui.contentPlayer.reading_story_activity.ReadingStoryActivity_;
import com.pratham.foundation.ui.contentPlayer.vocabulary_qa.ReadingVocabularyActivity_;
import com.pratham.foundation.ui.contentPlayer.web_view.WebViewActivity;
import com.pratham.foundation.ui.student_profile.Student_profile_activity;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pratham.foundation.ui.home_screen.HomeActivity.languageChanged;
import static com.pratham.foundation.utility.FC_Constants.LOGIN_MODE;
import static com.pratham.foundation.utility.FC_Constants.QR_GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;
import static com.pratham.foundation.utility.SplashSupportActivity.ButtonClickSound;


@EActivity(R.layout.activity_content_display)
public class ContentDisplay extends BaseActivity implements ContentContract.ContentView, ContentClicked {

    @Bean(ContentPresenter.class)
    ContentContract.ContentPresenter presenter;

    private RecyclerView recyclerView;
    private ContentAdapter contentAdapter;
    int tempDownloadPos, resumeCntr = 0;
    String downloadNodeId, resName, resServerImageName, parentName;
    List<ContentTable> ContentTableList;
    public Dialog downloadDialog;
    ProgressLayout progressLayout;
    TextView dialog_file_name;
    ImageView iv_file_trans;

    @ViewById(R.id.tv_header_progress)
    TextView tv_header_progress;
    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.tv_Activity)
    TextView tv_Activity;
    @ViewById(R.id.iv_level)
    ImageView iv_level;
    String nodeId, level, contentTitle;
    @ViewById(R.id.home_root_layout)
    RelativeLayout homeRoot;
    @ViewById(R.id.header_rl)
    RelativeLayout header_rl;
//    @ViewById(R.id.tv_progress)
//    TextView tv_progress;
    @ViewById(R.id.card_progressLayout)
    ProgressLayout level_progress;

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
/*    @ViewById(R.id.floating_back)
    FloatingActionButton floating_back;
    @ViewById(R.id.floating_info)
    FloatingActionButton floating_info;*/
    @ViewById(R.id.main_back)
    ImageView main_back;


    @AfterViews
    public void initialize() {
        resumeCntr = 0;
        nodeId = getIntent().getStringExtra("nodeId");
        contentTitle = getIntent().getStringExtra("contentTitle");
        parentName = getIntent().getStringExtra("parentName");
        level = getIntent().getStringExtra("level");
        changeBG(Integer.parseInt(level));
/*        floating_back.setImageResource(R.drawable.ic_left_arrow_white);
        floating_info.setImageResource(R.drawable.ic_info_outline_white);*/
        presenter.setView(ContentDisplay.this);
//        presenter.getPerc(nodeId);

        ContentTableList = new ArrayList<>();
        recyclerView = findViewById(R.id.attendnce_recycler_view);
        contentAdapter = new ContentAdapter(this, ContentTableList, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        int dp = 12;
        if (FC_Constants.TAB_LAYOUT)
            dp = 20;

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(this,dp), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contentAdapter);
        FC_Constants.isTest = false;
        presenter.displayProfileImage();
        if(!LOGIN_MODE.equalsIgnoreCase(QR_GROUP_MODE))
            presenter.getPerc(nodeId);
        tv_Topic.setText("" + contentTitle);
        tv_Activity.setText("" + parentName);
    }

    @SuppressLint("SetTextI18n")
    private void changeBG(int levelNo) {
        switch (levelNo) {
            case 0:
                header_rl.setBackground(homeHeader0);
                iv_level.setImageResource(R.drawable.level_1);
                break;
            case 1:
                header_rl.setBackground(homeHeader1);
                iv_level.setImageResource(R.drawable.level_2);
                break;
            case 2:
                header_rl.setBackground(homeHeader2);
                iv_level.setImageResource(R.drawable.level_3);
                break;
            case 3:
                header_rl.setBackground(homeHeader3);
                iv_level.setImageResource(R.drawable.level_4);
                break;
            case 4:
                header_rl.setBackground(homeHeader4);
                iv_level.setImageResource(R.drawable.level_5);
                break;
        }
    }

    @UiThread
    @Override
    public void setStudentProfileImage(String sImage) {
        showLoader();
/*        if (sImage != null) {
            if (sImage.equalsIgnoreCase("group_icon"))
                profileImage.setImageResource(R.drawable.ic_grp_btn);
            else if (!FC_Constants.GROUP_LOGIN && ApplicationClass.isTablet)
                profileImage.setImageResource(R.drawable.b2);
            else {
                profileImage.setImageResource(R.drawable.b3);
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
        } else
            profileImage.setImageResource(R.drawable.b2);*/

        presenter.addNodeIdToList(nodeId);
        presenter.getListData();

    }

    @UiThread
    @Override
    public void setHeaderProgress(int percent) {
        tv_header_progress.setText(""+percent+"%");
//        level_progress.setCurProgress(percent);
    }

    @Override
    public void clearContentList() {
        ContentTableList.clear();
    }

    @Override
    public void addContentToViewList(List<ContentTable> contentTable) {
        ContentTableList.addAll(contentTable);
    }

    @Click(R.id.profileImage)
    public void loadProfile() {
        startActivity(new Intent(ContentDisplay.this, Student_profile_activity.class));
    }

    @Override
    public void notifyAdapter() {
        Collections.sort(ContentTableList, (o1, o2) -> o1.getNodeId().compareTo(o2.getNodeId()));
        contentAdapter.notifyDataSetChanged();
        new Handler().postDelayed(this::dismissLoadingDialog, 300);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!languageChanged) {
            Log.d("HomeActivityTAG", "ActivityResumed: ");
            if (resumeCntr == 0)
                resumeCntr = 1;
            else {
                showLoader();
                if(!LOGIN_MODE.equalsIgnoreCase(QR_GROUP_MODE))
                presenter.getPerc(nodeId);
                notifyAdapter();
            }
        } else
            finish();
    }

    @Click(R.id.main_back)
    public void backClicked(){
        onBackPressed();
    }

    /*    @Override
    public void ActivityResumed() {
        super.ActivityResumed();
    }*/

    @SuppressLint("SetTextI18n")
    @Override
    public void showNoDataDownloadedDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.fc_custom_dialog);
/*        Bitmap map=FC_Utility.takeScreenShot(HomeActivity.this);
        Bitmap fast=FC_Utility.fastblur(map, 20);
        final Drawable draw=new BitmapDrawable(getResources(),fast);
        dialog.getWindow().setBackgroundDrawable(draw);*/
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();

        TextView dia_tv = dialog.findViewById(R.id.dia_title);
        Button btn_gree = dialog.findViewById(R.id.dia_btn_green);
        Button btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button btn_red = dialog.findViewById(R.id.dia_btn_red);

        btn_gree.setText("Ok");
        dia_tv.setText("Connect to Internet");
        btn_red.setVisibility(View.GONE);
        btn_yellow.setVisibility(View.GONE);

        btn_gree.setOnClickListener(v -> {
            finish();
            dialog.dismiss();
        });
    }

    @Override
    public void onContentClicked(int position, String nId) {
        ButtonClickSound.start();
        ContentTableList.clear();
        presenter.addNodeIdToList(nId);
        presenter.getListData();
    }

    @UiThread
    @Override
    public void onPreResOpenClicked(int position, String nId, String title) {
        ButtonClickSound.start();
        Intent mainNew = new Intent(ContentDisplay.this, ContentPlayerActivity_.class);
        mainNew.putExtra("nodeID", nId);
        mainNew.putExtra("title", title);
        startActivity(mainNew);
    }

    @Override
    public void onContentOpenClicked(int position, String nodeId) {
        //todo remove#
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        resName = ContentTableList.get(position).getNodeTitle();
        if(ContentTableList.get(position).getNodeType().equalsIgnoreCase("PreResource")||
                ContentTableList.get(position).getResourceType().equalsIgnoreCase("PreResource")){
                Intent mainNew = new Intent(ContentDisplay.this, ContentPlayerActivity_.class);
                mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", ContentTableList.get(position).getNodeTitle());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                startActivity(mainNew);
        }else {
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
                    Intent mainNew = new Intent(ContentDisplay.this, WebViewActivity.class);
                    mainNew.putExtra("resPath", path.toString());
                    mainNew.putExtra("resId", gameID);
                    mainNew.putExtra("mode", "normal");
                    mainNew.putExtra("gameLevel", "" + ContentTableList.get(position).getNodeDesc());
                    mainNew.putExtra("gameName", "" + ContentTableList.get(position).getNodeTitle());
                    mainNew.putExtra("gameType", "" + ContentTableList.get(position).getResourceType());
                    mainNew.putExtra("gameCategory", "" + ContentTableList.get(position).getNodeKeywords());
                    startActivity(mainNew);
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
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.COMIC_CONVO_RESOURCE)) {
                Intent mainNew = new Intent(ContentDisplay.this, ReadingCardsActivity_.class);
                mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", ContentTableList.get(position).getNodeTitle());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                startActivity(mainNew);
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.OPPOSITE_WORDS)) {
                Intent mainNew = new Intent(ContentDisplay.this, OppositesActivity_.class);
                mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", ContentTableList.get(position).getNodeTitle());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
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
                startActivity(mainNew);
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.RHYMING_WORD_ANDROID)) {
                Intent mainNew = new Intent(ContentDisplay.this, ReadingRhymesActivity_.class);
                mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                mainNew.putExtra("contentTitle", ContentTableList.get(position).getNodeTitle());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("rhymeLevel", ContentTableList.get(position).getNodeDesc());
                startActivity(mainNew);
            } else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.MATCH_THE_PAIR)) {
                Intent mainNew = new Intent(ContentDisplay.this, MatchThePairGameActivity.class);
                mainNew.putExtra("resId", ContentTableList.get(position).getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", ContentTableList.get(position).getResourcePath());
                mainNew.putExtra("contentTitle", ContentTableList.get(position).getNodeTitle());
                mainNew.putExtra("onSdCard", ContentTableList.get(position).isOnSDCard());
                mainNew.putExtra("rhymeLevel", ContentTableList.get(position).getNodeDesc());
                startActivity(mainNew);
            } /*else if (ContentTableList.get(position).getResourceType().equalsIgnoreCase(FC_Constants.CHATBOT_ANDROID)) {
            Intent talkbot = new Intent(this, com.pratham.cityofstories.talkbot.feature.HomeActivity.class);
            startActivity(talkbot);
        }*/
        }
        resServerImageName = ContentTableList.get(position).getNodeServerImage();
    }

    @Override
    public void onContentDownloadClicked(int position, String nodeId) {
        ButtonClickSound.start();
        downloadNodeId = "" + nodeId;
        resName = ContentTableList.get(position).getNodeTitle();
        resServerImageName = ContentTableList.get(position).getNodeServerImage();
        tempDownloadPos = position;
        if (FC_Utility.isDataConnectionAvailable(ContentDisplay.this))
            presenter.downloadResource(downloadNodeId);
        else
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onContentDeleteClicked(int position, ContentTable contentList) {
        ButtonClickSound.start();
        Log.d("Delete_Clicked", "onClick: G_Activity");
        showDeleteDialog(position, contentList);
    }

    @SuppressLint("SetTextI18n")
    private void showDeleteDialog(int deletePos, ContentTable contentTableItem) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.exit_dialog);
        TextView tv_title = dialog.findViewById(R.id.dia_title);
        Button exit_btn = dialog.findViewById(R.id.dia_btn_exit);
        Button restart_btn = dialog.findViewById(R.id.dia_btn_restart);
        dialog.show();
        tv_title.setText("Delete\n" + contentTableItem.getNodeTitle());
        exit_btn.setText("NO");
        restart_btn.setText("YES");

        exit_btn.setOnClickListener(v -> dialog.dismiss());

        restart_btn.setOnClickListener(v -> {
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
        if (presenter.removeLastNodeId()) {
            ContentTableList.clear();
            presenter.getListData();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    private void resourceDownloadDialog(Modal_FileDownloading modal_fileDownloading) {
        downloadDialog = new Dialog(this);
        downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        downloadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        downloadDialog.setContentView(R.layout.dialog_file_downloading);
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();
        progressLayout = downloadDialog.findViewById(R.id.dialog_progressLayout);
        dialog_file_name = downloadDialog.findViewById(R.id.dialog_file_name);
        iv_file_trans = downloadDialog.findViewById(R.id.iv_file_trans);
        Glide.with(this).load(resServerImageName).into(iv_file_trans);
        dialog_file_name.setText(resName);
        progressLayout.setCurProgress(modal_fileDownloading.getProgress());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
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
                resName = "";
                ContentTableList.get(tempDownloadPos).setIsDownloaded("true");
                new Handler().postDelayed(() -> {
                    downloadDialog.dismiss();
                    contentAdapter.notifyItemChanged(tempDownloadPos, ContentTableList.get(tempDownloadPos));
                }, 500);
            }

        }
    }

    public Dialog myLoadingDialog;

    @UiThread
    @Override
    public void showLoader() {
        myLoadingDialog = new Dialog(this);
        myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myLoadingDialog.setContentView(R.layout.loading_dialog);
        myLoadingDialog.setCanceledOnTouchOutside(false);
//        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }

    @UiThread
    @Override
    public void dismissLoadingDialog() {
        if (myLoadingDialog != null) {
            myLoadingDialog.dismiss();
        }
    }


    @UiThread
    public void showDownloadErrorDialog() {
        Dialog errorDialog = new Dialog(this);
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        errorDialog.setContentView(R.layout.dialog_file_error_downloading);
        errorDialog.setCanceledOnTouchOutside(false);
        errorDialog.show();
        Button ok_btn = errorDialog.findViewById(R.id.dialog_error_btn);

        ok_btn.setOnClickListener(v -> errorDialog.dismiss());
    }
}