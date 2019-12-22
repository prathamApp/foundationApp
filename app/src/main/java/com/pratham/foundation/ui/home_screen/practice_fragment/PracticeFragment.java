package com.pratham.foundation.ui.home_screen.practice_fragment;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.customView.collapsingView.RetractableToolbarUtil;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
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
import com.pratham.foundation.ui.home_screen.display_content.ContentDisplay_;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ui.home_screen.HomeActivity.header_rl;
import static com.pratham.foundation.ui.home_screen.HomeActivity.levelChanged;
import static com.pratham.foundation.ui.home_screen.HomeActivity.sub_Name;
import static com.pratham.foundation.ui.home_screen.HomeActivity.tv_header_progress;
import static com.pratham.foundation.utility.FC_Constants.currentLevel;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;
import static com.pratham.foundation.utility.SplashSupportActivity.ButtonClickSound;

@EFragment(R.layout.fragment_tab_one)
public class PracticeFragment extends Fragment implements PracticeContract.PracticeView,
        PracticeContract.PracticeItemClicked {

    @Bean(PracticePresenter.class)
    PracticeContract.PracticePresenter presenter;

    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;
    private PracticeOuterDataAdapter adapterParent;
    public List<ContentTable> rootList, rootLevelList, dwParentList, childDwContentList;
    public List<ContentTable> contentParentList, contentDBList, contentApiList, childContentList;
    private String downloadNodeId, resName, resServerImageName, downloadType;
    private int childPos = 0, parentPos = 0, resumeCntr = 0;

    @AfterViews
    public void initialize() {
        FC_Constants.isTest = false;
        rootList = new ArrayList<>();
        rootLevelList = new ArrayList<>();
        dwParentList = new ArrayList<>();
        childDwContentList = new ArrayList<>();
        contentParentList = new ArrayList<>();
        presenter.setView(PracticeFragment.this);
        RetractableToolbarUtil.ShowHideToolbarOnScrollingListener showHideToolbarListener;
        my_recycler_view.addOnScrollListener(showHideToolbarListener =
                new RetractableToolbarUtil.ShowHideToolbarOnScrollingListener(header_rl));
        presenter.getBottomNavId(currentLevel, "Practice");
    }

    @UiThread
    public void notifyAdapter() {
        try {
            for (int i = 0; i < contentParentList.size(); i++)
                Log.d("Practice List", "" + contentParentList.get(i).getNodeTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (adapterParent == null) {
            adapterParent = new PracticeOuterDataAdapter(getActivity(), contentParentList, this);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
            my_recycler_view.setLayoutManager(mLayoutManager);
            my_recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(getActivity()), true));
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

    public void sortAllList(List<ContentTable> contentParentList) {
        Collections.sort(contentParentList, new Comparator<ContentTable>() {
            @Override
            public int compare(ContentTable o1, ContentTable o2) {
                return o1.getNodeId().compareTo(o2.getNodeId());
            }
        });
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

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (resumeCntr == 0)
//            resumeCntr = 1;
//        else {
//            showLoader();
//            presenter.getDataForList();
//            String currentNodeID = presenter.getcurrentNodeID();
//            try {
//                if (!currentNodeID.equalsIgnoreCase("na"))
//                    presenter.findMaxScore("" + currentNodeID);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void fragmentSelected() {
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

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(FC_Constants.LEVEL_CHANGED))
                onLevelChanged();
            else if (message.getMessage().equalsIgnoreCase(FC_Constants.BACK_PRESSED))
                backBtnPressed();
            else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_STARTED)) {
                resourceDownloadDialog(message.getModal_fileDownloading());
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_UPDATE)) {
                if (progressLayout != null)
                    progressLayout.setCurProgress(message.getModal_fileDownloading().getProgress());
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FRAGMENT_SELECTED)) {
                fragmentSelected();
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FRAGMENT_RESELECTED)) {
                fragmentSelected();
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_ERROR)) {
                downloadDialog.dismiss();
                showDownloadErrorDialog();
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.UNZIPPING_ERROR)) {
                downloadDialog.dismiss();
                showDownloadErrorDialog();
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.UNZIPPING_DATA_FILE))
                dialog_file_name.setText("Unzipping...\nPlease wait" + resName);
            else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_COMPLETE)) {
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

    @Override
    public void setSelectedLevel(List<ContentTable> contentTable) {
        rootLevelList = contentTable;
        if (rootLevelList != null)
            levelChanged.setActualLevel(rootLevelList.size());
        presenter.insertNodeId(contentTable.get(currentLevel).getNodeId());
        presenter.getDataForList();
    }

    public void onLevelChanged() {
        contentParentList.clear();
        presenter.removeLastNodeId();
        presenter.insertNodeId(rootLevelList.get(currentLevel).getNodeId());
        presenter.getDataForList();
    }

    @Override
    public void showNoDataDownloadedDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(getActivity());
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
    private CustomLodingDialog myLoadingDialog;

    @UiThread
    @Override
    public void showLoader() {
        if (!loaderVisible) {
            try {
                loaderVisible = true;
                myLoadingDialog = new CustomLodingDialog(getActivity());
                myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                Objects.requireNonNull(myLoadingDialog.getWindow()).
                        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myLoadingDialog.setContentView(R.layout.loading_dialog);
                myLoadingDialog.setCanceledOnTouchOutside(false);
//        myLoadingDialog.setCancelable(false);
                myLoadingDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dismissLoadingDialog() {
        if (myLoadingDialog != null) {
            loaderVisible = false;
            myLoadingDialog.dismiss();
        }
    }

    @UiThread
    @Override
    public void setLevelprogress(int percent) {
        tv_header_progress.setText(percent + "%");
//        tv_progress.setCurProgress(percent);
//        level_progress.setCurProgress(percent);
    }

    private CustomLodingDialog downloadDialog;
    private ProgressLayout progressLayout;
    private TextView dialog_file_name;

    @SuppressLint("SetTextI18n")
    private void resourceDownloadDialog(Modal_FileDownloading modal_fileDownloading) {
        downloadDialog = new CustomLodingDialog(getActivity());
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
    public void dismissDownloadDialog() {
        if (downloadDialog != null)
            downloadDialog.dismiss();
    }

    @Override
    public void onContentClicked(ContentTable singleItem, String parentName) {
        ButtonClickSound.start();
        FC_Constants.isPractice = true;
        FC_Constants.isTest = false;
        if (singleItem.getNodeType().equalsIgnoreCase("category")) {
            Intent intent = new Intent(getActivity(), ContentDisplay_.class);
            intent.putExtra("nodeId", singleItem.getNodeId());
            intent.putExtra("contentTitle", singleItem.getNodeTitle());
            intent.putExtra("parentName", parentName);
            intent.putExtra("level", "" + currentLevel);
            startActivity(intent);
        } else if (singleItem.getNodeType().equalsIgnoreCase("preResource")) {
            Intent mainNew = new Intent(getActivity(), ContentPlayerActivity_.class);
            mainNew.putExtra("nodeID", singleItem.getNodeId());
            mainNew.putExtra("title", singleItem.getNodeTitle());
            startActivity(mainNew);
        } else {
            contentParentList.clear();
            presenter.insertNodeId(singleItem.nodeId);
            presenter.getDataForList();
        }
    }

    @Override
    public void onContentOpenClicked(ContentTable contentList) {
        //Toast.makeText(this, "ContentOpen : Work In Progress", Toast.LENGTH_SHORT).show();
        //todo remove#
        ButtonClickSound.start();
        FC_Constants.isPractice = true;
        FC_Constants.isTest = false;
        downloadNodeId = contentList.getNodeId();
        resName = contentList.getNodeTitle();
        if (contentList.getNodeType().equalsIgnoreCase("PreResource") ||
                contentList.getResourceType().equalsIgnoreCase("PreResource")) {
            Intent mainNew = new Intent(getActivity(), ContentPlayerActivity_.class);
            mainNew.putExtra("resId", contentList.getResourceId());
            mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            mainNew.putExtra("contentName", contentList.getNodeTitle());
            mainNew.putExtra("onSdCard", contentList.isOnSDCard());
            mainNew.putExtra("contentPath", contentList.getResourcePath());
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
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("resPath", path.toString());
                intent.putExtra("resId", gameID);
                intent.putExtra("mode", "normal");
                intent.putExtra("gameLevel", "" + contentList.getNodeDesc());
                intent.putExtra("gameType", "" + contentList.getResourceType());
                intent.putExtra("certiCode", contentList.getNodeDesc());
                intent.putExtra("gameCategory", "" + contentList.getNodeKeywords());
                startActivity(intent);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.RC_RESOURCE)) {
//                presenter.enterRCData(contentList);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.CONVO_RESOURCE)) {
                Intent mainNew = new Intent(getActivity(), ConversationActivity_.class);
                mainNew.putExtra("storyId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", contentList.getNodeTitle());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("certiCode", contentList.getNodeDesc());
                mainNew.putExtra("contentPath", contentList.getResourcePath());
                startActivity(mainNew);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.COMIC_CONVO_RESOURCE)) {
                Intent mainNew = new Intent(getActivity(), ReadingCardsActivity_.class);
                mainNew.putExtra("storyId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", contentList.getNodeTitle());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("contentPath", contentList.getResourcePath());
                startActivity(mainNew);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.RHYME_RESOURCE) || contentList.getResourceType().equalsIgnoreCase(FC_Constants.STORY_RESOURCE)) {
                Intent mainNew = new Intent(getActivity(), ReadingStoryActivity_.class);
                mainNew.putExtra("storyId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("storyPath", contentList.getResourcePath());
                mainNew.putExtra("storyTitle", contentList.getNodeTitle());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("contentType", contentList.getResourceType());
                startActivity(mainNew);
            } /*else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.WORD_ANDROID)) {
                Intent mainNew = new Intent(getActivity(), ReadingWordScreenActivity.class);
                mainNew.putExtra("resId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", contentList.getResourcePath());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("contentTitle", contentList.getNodeTitle());
                startActivity(mainNew);
            }*/ else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.PARA_ANDROID)) {
                Intent mainNew = new Intent(getActivity(), ReadingParagraphsActivity_.class);
                mainNew.putExtra("resId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", contentList.getResourcePath());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("contentTitle", contentList.getNodeTitle());
                startActivity(mainNew);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.VOCAB_ANDROID)) {
                Intent mainNew = new Intent(getActivity(), ReadingVocabularyActivity_.class);
                mainNew.putExtra("resId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", contentList.getResourcePath());
                mainNew.putExtra("contentTitle", contentList.getNodeTitle());
                mainNew.putExtra("vocabLevel", contentList.getNodeDesc());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("vocabCategory", contentList.getNodeKeywords());
                startActivity(mainNew);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.RHYMING_WORD_ANDROID)) {
                Intent mainNew = new Intent(getActivity(), ReadingRhymesActivity_.class);
                mainNew.putExtra("resId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", contentList.getResourcePath());
                mainNew.putExtra("contentTitle", contentList.getNodeTitle());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("rhymeLevel", contentList.getNodeDesc());
                startActivity(mainNew);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.OPPOSITE_WORDS)) {
                Intent mainNew = new Intent(getActivity(), OppositesActivity_.class);
                mainNew.putExtra("resId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", contentList.getNodeTitle());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("contentPath", contentList.getResourcePath());
                startActivity(mainNew);
            } else if (contentList.getResourceType().equalsIgnoreCase(FC_Constants.MATCH_THE_PAIR)) {
                Intent mainNew = new Intent(getActivity(), MatchThePairGameActivity.class);
                mainNew.putExtra("resId", contentList.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", contentList.getNodeTitle());
                mainNew.putExtra("onSdCard", contentList.isOnSDCard());
                mainNew.putExtra("contentPath", contentList.getResourcePath());
                startActivity(mainNew);
            }
        }
        resServerImageName = contentList.getNodeServerImage();
    }

    @Override
    public void onContentDownloadClicked(ContentTable contentList, int parentPos, int childPos, String downloadType) {
        this.downloadType = downloadType;
        FC_Constants.isPractice = true;
        FC_Constants.isTest = false;
        downloadNodeId = contentList.getNodeId();
        ButtonClickSound.start();
//        downloadNodeId = "" + 1371;
        this.parentPos = parentPos;
        this.childPos = childPos;
        resName = contentList.getNodeTitle();
        resServerImageName = contentList.getNodeServerImage();
        if (FC_Utility.isDataConnectionAvailable(getActivity()))
            presenter.downloadResource(downloadNodeId);
        else
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();

    }

    @UiThread
    public void showDownloadErrorDialog() {
        CustomLodingDialog errorDialog = new CustomLodingDialog(getActivity());
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        errorDialog.setContentView(R.layout.dialog_file_error_downloading);
        errorDialog.setCanceledOnTouchOutside(false);
        errorDialog.show();
        Button ok_btn = errorDialog.findViewById(R.id.dialog_error_btn);

        ok_btn.setOnClickListener(v -> errorDialog.dismiss());
    }

    @Click(R.id.btn_back)
    public void backBtnPressed() {
        if (presenter.removeLastNodeId()) {
            contentParentList.clear();
            presenter.getDataForList();
        } else {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onContentDeleteClicked(ContentTable contentList) {
    }

    @Override
    public void seeMore(String nodeId, String nodeTitle) {
        FC_Constants.isTest = false;
        Intent intent = new Intent(getActivity(), ContentDisplay_.class);
        intent.putExtra("nodeId", nodeId);
        intent.putExtra("contentTitle", nodeTitle);
        intent.putExtra("parentName", sub_Name);
        intent.putExtra("level", "" + currentLevel);
        startActivity(intent);
    }

}