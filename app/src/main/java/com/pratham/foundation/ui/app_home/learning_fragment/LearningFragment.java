package com.pratham.foundation.ui.app_home.learning_fragment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.customView.collapsingView.RetractableToolbarUtil;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.Modal_FileDownloading;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.app_home.FragmentItemClicked;
import com.pratham.foundation.ui.app_home.display_content.ContentDisplay_;
import com.pratham.foundation.ui.contentPlayer.ContentPlayerActivity_;
import com.pratham.foundation.ui.contentPlayer.matchingPairGame.MatchThePairGameActivity;
import com.pratham.foundation.ui.contentPlayer.old_cos.conversation.ConversationActivity_;
import com.pratham.foundation.ui.contentPlayer.old_cos.reading_cards.ReadingCardsActivity_;
import com.pratham.foundation.ui.contentPlayer.opposites.OppositesActivity_;
import com.pratham.foundation.ui.contentPlayer.reading_paragraphs.ReadingParagraphsActivity_;
import com.pratham.foundation.ui.contentPlayer.reading_rhyming.ReadingRhymesActivity_;
import com.pratham.foundation.ui.contentPlayer.reading_story_activity.ReadingStoryActivity_;
import com.pratham.foundation.ui.contentPlayer.video_view.ActivityVideoView_;
import com.pratham.foundation.ui.contentPlayer.vocabulary_qa.ReadingVocabularyActivity_;
import com.pratham.foundation.ui.contentPlayer.web_view.WebViewActivity_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.ui.app_home.HomeActivity.header_rl;
import static com.pratham.foundation.ui.app_home.HomeActivity.levelChanged;
import static com.pratham.foundation.ui.app_home.HomeActivity.sub_Name;
import static com.pratham.foundation.ui.app_home.HomeActivity.tv_header_progress;
import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.COMING_SOON;
import static com.pratham.foundation.utility.FC_Constants.currentLevel;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Constants.sec_Learning;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;

@EFragment(R.layout.fragment_tab_one)
public class LearningFragment extends Fragment implements LearningContract.LearningView,
        FragmentItemClicked {
//        LearningContract.LearningItemClicked {

    @Bean(LearningPresenter.class)
    LearningContract.LearningPresenter presenter;

    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;
    @ViewById(R.id.rl_no_data)
    RelativeLayout rl_no_data;
    private LearningOuterDataAdapter adapterParent;
    public List<ContentTable> rootList, rootLevelList, dwParentList, childDwContentList;
    public List<ContentTable> contentParentList, contentDBList, contentApiList, childContentList;
    private String downloadNodeId, resName, resServerImageName, downloadType;
    private int childPos = 0, parentPos = 0, resumeCntr = 0;
    Context context;

    @AfterViews
    public void initialize() {
        Runtime rs = Runtime.getRuntime();
        rs.freeMemory();
        rs.gc();
        rs.freeMemory();
        rootList = new ArrayList<>();
        rootLevelList = new ArrayList<>();
        dwParentList = new ArrayList<>();
        childDwContentList = new ArrayList<>();
        contentParentList = new ArrayList<>();
        context = getActivity();
        presenter.setView(LearningFragment.this);
        my_recycler_view.addOnScrollListener(new RetractableToolbarUtil
                .ShowHideToolbarOnScrollingListener(header_rl));
        if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Learning)) {
            showLoader();
            presenter.getBottomNavId(currentLevel, "" + sec_Learning);
        }
    }

    @Override
    public void showComingSoonDiaog() {
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessage(COMING_SOON);
        EventBus.getDefault().post(eventMessage);
    }

    @UiThread
    public void notifyAdapter() {
        try {
            for (int i = 0; i < contentParentList.size(); i++)
                Log.d("Practice List", "" + contentParentList.get(i).getNodeTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

        showRecyclerLayout();
        if (adapterParent == null) {
            try {
                adapterParent = new LearningOuterDataAdapter(context, contentParentList, this);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
                my_recycler_view.setLayoutManager(mLayoutManager);
                my_recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(context), true));
                my_recycler_view.setItemAnimator(new DefaultItemAnimator());
                my_recycler_view.setAdapter(adapterParent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            adapterParent.notifyDataSetChanged();
        long delay;
        dismissLoadingDialog();
        try {
            if (downloadDialog != null)
                downloadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void addContentToViewList(List<ContentTable> contentParentList) {
        this.contentParentList.clear();
        this.contentParentList.addAll(contentParentList);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

/*
    @Override
    public void onResume() {
        super.onResume();
        if (resumeCntr == 0)
            resumeCntr = 1;
        else {
            showLoader();
            presenter.getDataForList();
            String currentNodeID = presenter.getcurrentNodeID();
            try {
                if (!currentNodeID.equalsIgnoreCase("na"))
                    presenter.findMaxScore("" + currentNodeID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
*/

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageReceived(EventMessage message) {
//            else if (message.getMessage().equalsIgnoreCase(FC_Constants.SECTION_COMPLETION_PERC))
//            getCompletionPercAgain();
        if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Learning)) {
            if (message != null) {
                if (message.getMessage().equalsIgnoreCase(FC_Constants.LEVEL_CHANGED))
                    onLevelChanged();
                else if (message.getMessage().equalsIgnoreCase(FC_Constants.BACK_PRESSED))
                    backBtnPressed();
                else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_STARTED)) {
                    dismissLoadingDialog();
                    resourceDownloadDialog(message.getModal_fileDownloading());
                } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_UPDATE)) {
                    if (progressLayout != null)
                        progressLayout.setCurProgress(message.getModal_fileDownloading().getProgress());
                } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FRAGMENT_SELECTED) ||
                        message.getMessage().equalsIgnoreCase(FC_Constants.FRAGMENT_RESELECTED) ||
                        message.getMessage().equalsIgnoreCase(FC_Constants.ACTIVITY_RESUMED)) {
                    fragmentSelected();
                } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_ERROR) ||
                        message.getMessage().equalsIgnoreCase(FC_Constants.UNZIPPING_ERROR) ||
                        message.getMessage().equalsIgnoreCase(FC_Constants.RESPONSE_CODE_ERROR)) {
                    dismissDownloadDialog();
                    showDownloadErrorDialog();
                } else if (message.getMessage().equalsIgnoreCase(FC_Constants.UNZIPPING_DATA_FILE))
                    showZipLoader();
                else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_COMPLETE)) {
                    dialog_file_name.setText("Updating Data");
                    String folderPath = "";
                    try {
                        if (downloadType.equalsIgnoreCase(FC_Constants.FULL_DOWNLOAD)) {
                            folderPath = contentParentList.get(parentPos).getResourcePath();
                            contentParentList.get(parentPos).setIsDownloaded("true");
                            contentParentList.get(parentPos).setNodeUpdate(false);
//                            presenter.updateDownloads();
//                            presenter.updateCurrentNode(contentParentList.get(parentPos));
                            new Handler().postDelayed(() -> {
                                downloadDialog.dismiss();
                                adapterParent.notifyItemChanged(parentPos, contentParentList.get(parentPos));
                            }, 200);
                        } else if (downloadType.equalsIgnoreCase(FC_Constants.SINGLE_RES_DOWNLOAD)) {
                            folderPath = Objects.requireNonNull(contentParentList.get(parentPos).getNodelist()).get(childPos).getResourcePath();
                            Objects.requireNonNull(contentParentList.get(parentPos).getNodelist()).get(childPos).setIsDownloaded("true");
                            Objects.requireNonNull(contentParentList.get(parentPos).getNodelist()).get(childPos).setNodeUpdate(false);
//                            presenter.updateDownloads();
//                            presenter.updateCurrentNode(contentParentList.get(parentPos));
                            new Handler().postDelayed(() -> {
                                downloadDialog.dismiss();
                                adapterParent.notifyItemChanged(parentPos, contentParentList.get(parentPos));
                            }, 200);
                        }
                        resName = "";
                        if (downloadType.equalsIgnoreCase(FC_Constants.TEST_DOWNLOAD))
                            presenter.updateDownloadJson(folderPath);
                    } catch (Exception e) {
                        downloadDialog.dismiss();
                        dismissLoadingDialog();
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @UiThread
    public void showZipLoader() {
        try {
            dialog_file_name.setText("Loading\n" + resName + "\nPlease wait...");
            progressLayout.setVisibility(View.GONE);
            dialog_roundProgress.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCompletionPercAgain() {
        String currentNodeID = presenter.getcurrentNodeID();
        Log.d("getCompletion", "getCompletionPercAgain: " + currentNodeID);
        try {
            if (!currentNodeID.equalsIgnoreCase("na"))
                presenter.findMaxScore("" + currentNodeID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fragmentSelected() {
        showLoader();
        presenter.getBottomNavId(currentLevel, "" + sec_Learning);
//        presenter.getDataForList();
        String currentNodeID = presenter.getcurrentNodeID();
        try {
            if (!currentNodeID.equalsIgnoreCase("na"))
                presenter.findMaxScore("" + currentNodeID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSelectedLevel(List<ContentTable> contentTable) {
        try {
            rootLevelList = contentTable;
            contentParentList.clear();
            presenter.removeLastNodeId();
            int i = 0;
            boolean found = false;
            for (i = 0; i < rootLevelList.size(); i++)
                if (rootLevelList.get(i).getNodeTitle().contains("" + currentLevel)) {
                    found = true;
                    break;
                }
            if (rootLevelList != null && found) {
                if (rootLevelList.size() > i) {
                    levelChanged.setActualLevel(rootLevelList, rootLevelList.get(i).getNodeTitle());
                    presenter.insertNodeId(rootLevelList.get(i).getNodeId());
                    presenter.getDataForList();
                } else
                    showNoDataLayout();
            } else if (rootLevelList != null) {
                if (rootLevelList.size() > 0) {
                    i = 0;
                    levelChanged.setActualLevel(rootLevelList, rootLevelList.get(i).getNodeTitle());
                    presenter.insertNodeId(rootLevelList.get(i).getNodeId());
                    presenter.getDataForList();
                }
            } else
                showNoDataLayout();

        } catch (Exception e) {
            e.printStackTrace();
            showNoDataLayout();
        }
    }

    public void onLevelChanged() {
        try {
            contentParentList.clear();
            presenter.removeLastNodeId();
            int i = 0;
            boolean found = false;
            for (i = 0; i < rootLevelList.size(); i++)
                if (rootLevelList.get(i).getNodeTitle().contains("" + currentLevel)) {
                    found = true;
                    break;
                }

            if (found) {
                presenter.insertNodeId(rootLevelList.get(i).getNodeId());
                presenter.getDataForList();
            } else showNoDataLayout();

        } catch (Exception e) {
            showNoDataLayout();
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void showNoDataLayout() {
        try {
            dismissLoadingDialog();
            my_recycler_view.setVisibility(View.GONE);
            rl_no_data.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void showRecyclerLayout() {
        try {
            my_recycler_view.setVisibility(View.VISIBLE);
            rl_no_data.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    BlurPopupWindow noDataDlg;

    @SuppressLint("SetTextI18n")
    @Override
    @UiThread
    public void showNoDataDownloadedDialog() {
        noDataDlg = new BlurPopupWindow.Builder(context)
                .setContentView(R.layout.fc_custom_dialog)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(true)
                .setScaleRatio(0.2f)
                .bindClickListener(v -> {
                    new Handler().postDelayed(() -> {
                        if (adapterParent != null) {
                            contentParentList.clear();
                            adapterParent.notifyDataSetChanged();
                        }
                        noDataDlg.dismiss();
                    }, 200);
                    dismissLoadingDialog();
                }, R.id.dia_btn_green)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();

        TextView title = noDataDlg.findViewById(R.id.dia_title);
        Button btn_gree = noDataDlg.findViewById(R.id.dia_btn_green);
        Button btn_yellow = noDataDlg.findViewById(R.id.dia_btn_yellow);
        Button btn_red = noDataDlg.findViewById(R.id.dia_btn_red);
        btn_gree.setText("Ok");
        title.setText("No Data Found");
        btn_red.setVisibility(View.GONE);
        btn_yellow.setVisibility(View.GONE);
        noDataDlg.show();
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

    @Override
    @UiThread
    public void dismissLoadingDialog() {
        try {
            loaderVisible = false;
            new Handler().postDelayed(() -> {
                if (myLoadingDialog != null && myLoadingDialog.isShowing())
                    myLoadingDialog.dismiss();
            }, 150);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    @Override
    public void setLevelprogress(int percent) {
        if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Learning))
            tv_header_progress.setText(percent + "%");
    }

//    @SuppressLint("SetTextI18n")
//    private void resourceDownloadDialog(Modal_FileDownloading modal_fileDownloading) {
//        downloadDialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
//        downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Objects.requireNonNull(downloadDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        downloadDialog.setContentView(R.layout.dialog_file_downloading);
//        downloadDialog.setCanceledOnTouchOutside(false);
//        downloadDialog.show();
//        progressLayout = downloadDialog.findViewById(R.id.dialog_progressLayout);
//        dialog_file_name = downloadDialog.findViewById(R.id.dialog_file_name);
//        SimpleDraweeView iv_file_trans = downloadDialog.findViewById(R.id.iv_file_trans);
//        Glide.with(this).load(resServerImageName).into(iv_file_trans);
//        dialog_file_name.setText("" + resName);
//        progressLayout.setCurProgress(modal_fileDownloading.getProgress());
//    }

    @UiThread
    @Override
    public void onPreResOpenClicked(int position, String nId, String title, boolean onSDCard) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        Intent mainNew = new Intent(context, ContentPlayerActivity_.class);
        mainNew.putExtra("nodeID", nId);
        mainNew.putExtra("title", title);
        mainNew.putExtra("onSDCard", onSDCard);
        startActivity(mainNew);
//        startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
    }

    @Override
    public void onContentClicked(ContentTable singleItem, String parentName) {
        FastSave.getInstance().saveString(APP_SECTION, "" + sec_Learning);
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (singleItem.getNodeType().equalsIgnoreCase("category") || singleItem.getResourceType().equalsIgnoreCase("category")) {
            Intent intent = new Intent(context, ContentDisplay_.class);
            intent.putExtra("nodeId", singleItem.getNodeId());
            intent.putExtra("parentName", parentName);
            intent.putExtra("contentTitle", singleItem.getNodeTitle());
            intent.putExtra("level", "" + currentLevel);
//            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            startActivity(intent);
        } else {
            contentParentList.clear();
            presenter.insertNodeId(singleItem.nodeId);
            presenter.getDataForList();
        }
    }

    @Override
    public void onContentOpenClicked(ContentTable contentList) {
        //Toast.makeText(this, "ContentOpen : Work In Progress", Toast.LENGTH_SHORT).show();
        FastSave.getInstance().saveString(APP_SECTION, sec_Learning);
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        downloadNodeId = contentList.getNodeId();
        resName = contentList.getNodeTitle();
        if (contentList.getNodeType().equalsIgnoreCase("PreResource") ||
                contentList.getResourceType().equalsIgnoreCase("PreResource")) {
            Intent mainNew = new Intent(context, ContentPlayerActivity_.class);
            mainNew.putExtra("resId", contentList.getResourceId());
            mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            mainNew.putExtra("contentName", contentList.getNodeTitle());
            mainNew.putExtra("onSdCard", contentList.isOnSDCard());
            mainNew.putExtra("contentPath", contentList.getResourcePath());
//            startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            startActivity(mainNew);
        } else {
            if (contentList.getResourceType().toLowerCase().contains(FC_Constants.HTML_GAME_RESOURCE)) {
                String resPath;
                String gameID = contentList.getResourceId();
                if (contentList.isOnSDCard())
                    resPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentList.getResourcePath();
                else
                    resPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentList.getResourcePath();
                File file = new File(resPath);
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent(context, WebViewActivity_.class);
                intent.putExtra("resPath", path.toString());
                intent.putExtra("resId", gameID);
                intent.putExtra("mode", "normal");
                intent.putExtra("gameLevel", "" + contentList.getNodeDesc());
                intent.putExtra("gameType", "" + contentList.getResourceType());
                intent.putExtra("certiCode", contentList.getNodeDesc());
                intent.putExtra("gameCategory", "" + contentList.getNodeKeywords());
//                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                startActivity(intent);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.RC_RESOURCE)) {
//                presenter.enterRCData(contentList);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.CONVO_RESOURCE)) {
                Intent mainNew = new Intent(context, ConversationActivity_.class);
                mainNew.putExtra("storyId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", contentList.getNodeTitle());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("certiCode", contentList.getNodeDesc());
                mainNew.putExtra("contentPath", contentList.getResourcePath());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                startActivity(mainNew);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.COMIC_CONVO_RESOURCE)) {
                Intent mainNew = new Intent(context, ReadingCardsActivity_.class);
                mainNew.putExtra("storyId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", contentList.getNodeTitle());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("contentPath", contentList.getResourcePath());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                startActivity(mainNew);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.RHYME_RESOURCE) || contentList.getResourceType().equalsIgnoreCase(FC_Constants.STORY_RESOURCE)) {
                Intent mainNew = new Intent(context, ReadingStoryActivity_.class);
                mainNew.putExtra("storyId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("storyPath", contentList.getResourcePath());
                mainNew.putExtra("storyTitle", contentList.getNodeTitle());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("contentType", contentList.getResourceType());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                startActivity(mainNew);
            } /*else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.WORD_ANDROID)) {
                Intent mainNew = new Intent(context, ReadingWordScreenActivity.class);
                mainNew.putExtra("resId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", contentList.getResourcePath());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("contentTitle", contentList.getNodeTitle());
                startActivity(mainNew);
            }*/ else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.PARA_ANDROID)) {
                Intent mainNew = new Intent(context, ReadingParagraphsActivity_.class);
                mainNew.putExtra("resId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", contentList.getResourcePath());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("contentTitle", contentList.getNodeTitle());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                startActivity(mainNew);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.VOCAB_ANDROID)) {
                Intent mainNew = new Intent(context, ReadingVocabularyActivity_.class);
                mainNew.putExtra("resId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", contentList.getResourcePath());
                mainNew.putExtra("contentTitle", contentList.getNodeTitle());
                mainNew.putExtra("vocabLevel", contentList.getNodeDesc());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("vocabCategory", contentList.getNodeKeywords());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                startActivity(mainNew);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.RHYMING_WORD_ANDROID)) {
                Intent mainNew = new Intent(context, ReadingRhymesActivity_.class);
                mainNew.putExtra("resId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", contentList.getResourcePath());
                mainNew.putExtra("contentTitle", contentList.getNodeTitle());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("rhymeLevel", contentList.getNodeDesc());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                startActivity(mainNew);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.OPPOSITE_WORDS)) {
                Intent mainNew = new Intent(context, OppositesActivity_.class);
                mainNew.putExtra("resId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", contentList.getNodeTitle());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("contentPath", contentList.getResourcePath());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                startActivity(mainNew);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.MATCH_THE_PAIR)) {
                Intent mainNew = new Intent(context, MatchThePairGameActivity.class);
                mainNew.putExtra("resId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", contentList.getNodeTitle());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("contentPath", contentList.getResourcePath());
//                startActivity(mainNew, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                startActivity(mainNew);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.VIDEO)) {
                Intent intent = new Intent(context, ActivityVideoView_.class);
                intent.putExtra("contentPath", contentList.getResourcePath());
                intent.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                intent.putExtra("resId", contentList.getResourceId());
                intent.putExtra("contentName", contentList.getNodeTitle());
                intent.putExtra("onSdCard", contentList.isOnSDCard());
//                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ContentDisplay.this).toBundle());
                startActivity(intent);
            }
        }
        resServerImageName = contentList.getNodeServerImage();
    }

    @UiThread
    @Override
    public void onContentDownloadClicked(ContentTable contentList, int parentPos, int childPos, String downloadType) {
        showLoader();
        this.downloadType = downloadType;
        downloadNodeId = contentList.getNodeId();
        FastSave.getInstance().saveString(APP_SECTION, "" + sec_Learning);
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

//        downloadNodeId = "" + 1371;
        this.parentPos = parentPos;
        this.childPos = childPos;
        resName = contentList.getNodeTitle();
        resServerImageName = contentList.getNodeServerImage();
        if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Learning)) {
            if (FC_Utility.isDataConnectionAvailable(context))
                presenter.downloadResource(downloadNodeId);
            else
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    //    private BlurPopupWindow downloadDialog;
    private CustomLodingDialog downloadDialog;
    private ProgressLayout progressLayout;
    private TextView dialog_file_name;
    ProgressBar dialog_roundProgress;

    @SuppressLint("SetTextI18n")
    @UiThread
    public void resourceDownloadDialog(Modal_FileDownloading modal_fileDownloading) {
        if (downloadDialog != null)
            downloadDialog = null;
        try {
            downloadDialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
            downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(downloadDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            downloadDialog.setContentView(R.layout.dialog_file_downloading);
            downloadDialog.setCanceledOnTouchOutside(false);
            downloadDialog.show();
//            downloadDialog = new BlurPopupWindow.Builder(context)
//                    .setContentView(R.layout.dialog_file_downloading)
//                    .setGravity(Gravity.CENTER)
//                    .setDismissOnTouchBackground(false)
//                    .setDismissOnClickBack(true)
//                    .setScaleRatio(0.2f)
//                    .setBlurRadius(10)
//                    .setTintColor(0x30000000)
//                    .build();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void dismissDownloadDialog() {
        try {
            if (downloadDialog != null)
                new Handler().postDelayed(() -> {
                    downloadDialog.dismiss();
                    downloadDialog = null;
                }, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    BlurPopupWindow errorDialog;
    CustomLodingDialog errorDialog;

    @UiThread
    public void showDownloadErrorDialog() {
        try {
//            errorDialog = new BlurPopupWindow.Builder(context)
//                    .setContentView(R.layout.dialog_file_error_downloading)
//                    .setGravity(Gravity.CENTER)
//                    .setDismissOnTouchBackground(false)
//                    .setDismissOnClickBack(true)
//                    .bindClickListener(v -> {
//                        new Handler().postDelayed(() ->
//                                errorDialog.dismiss(), 200);
//                    }, R.id.dialog_error_btn)
//                    .setScaleRatio(0.2f)
//                    .setBlurRadius(10)
//                    .setTintColor(0x30000000)
//                    .build();
//            errorDialog.show();
            errorDialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
            errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(errorDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorDialog.setContentView(R.layout.dialog_file_error_downloading);
            errorDialog.setCanceledOnTouchOutside(false);
            Button dialog_error_btn = errorDialog.findViewById(R.id.dialog_error_btn);
            dialog_error_btn.setOnClickListener(v -> new Handler().postDelayed(() ->
                    errorDialog.dismiss(), 200));
            errorDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.btn_back)
    public void backBtnPressed() {
        if (presenter.removeLastNodeId()) {
            contentParentList.clear();
            presenter.getDataForList();
        } else {
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    @Override
    public void onContentDeleteClicked(ContentTable contentList) {
    }

    @Override
    public void seeMore(String nodeId, String nodeTitle) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        FastSave.getInstance().saveString(APP_SECTION, sec_Learning);
        Intent intent = new Intent(context, ContentDisplay_.class);
        intent.putExtra("nodeId", nodeId);
        intent.putExtra("contentTitle", nodeTitle);
        intent.putExtra("parentName", sub_Name);
        intent.putExtra("level", "" + currentLevel);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        startActivity(intent);
    }


    public void reveal(View view, View startView) {
        // previously invisible view
        try {
            int centerX = view.getWidth();
            int centerY = view.getHeight();
            int startRadius = 0;
            int endRadius = (int) Math.hypot(centerX, centerY);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, (int) startView.getX(), (int) startView.getY(), startRadius, endRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(300);
            view.setVisibility(View.VISIBLE);
            anim.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}