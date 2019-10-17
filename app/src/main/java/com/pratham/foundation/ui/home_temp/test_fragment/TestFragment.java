package com.pratham.foundation.ui.home_temp.test_fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.collapsingView.RetractableToolbarUtil;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.ContentTableNew;
import com.pratham.foundation.modalclasses.CertificateModelClass;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.Modal_FileDownloading;
import com.pratham.foundation.ui.contentPlayer.web_view.WebViewActivity;
import com.pratham.foundation.ui.factRetrial.FactRetrial_;
import com.pratham.foundation.ui.home_temp.TempHomeActivity;
import com.pratham.foundation.ui.test.certificate.CertificateClicked;
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
import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ui.home_temp.TempHomeActivity.header_rl;
import static com.pratham.foundation.utility.FC_Constants.isTest;
import static com.pratham.foundation.utility.FC_Constants.testSessionEntered;

@EFragment(R.layout.fragment_test)
public class TestFragment extends Fragment implements TestContract.TestView,
        CertificateClicked , TestContract.LanguageSpinnerListner{

    @Bean(TestPresenter.class)
    TestContract.TestPresenter presenter;
    private List<CertificateModelClass> testList;

    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;
    @ViewById(R.id.btn_test_dw)
    Button btn_test_dw;

    private TestAdapter testAdapter;
    private int clicked_Pos = 0;
    public List<ContentTable> rootList, rootLevelList, dwParentList, childDwContentList;
    public List<ContentTableNew> contentParentList, contentDBList, contentApiList, childContentList;
    private String  downloadNodeId, resName, resServerImageName, downloadType, certi_Code = "";
    private int childPos = 0, parentPos = 0, resumeCntr = 0;
    public static  String language = "English";

    @AfterViews
    public void initialize() {
        rootList = new ArrayList<>();
        rootLevelList = new ArrayList<>();
        dwParentList = new ArrayList<>();
        childDwContentList = new ArrayList<>();
        contentParentList = new ArrayList<>();
        testList = new ArrayList<>();
        presenter.setView(TestFragment.this);
        RetractableToolbarUtil.ShowHideToolbarOnScrollingListener showHideToolbarListener;
        my_recycler_view.addOnScrollListener(showHideToolbarListener =
                new RetractableToolbarUtil.ShowHideToolbarOnScrollingListener(header_rl));
        presenter.getBottomNavId(TempHomeActivity.currentLevelNo, "Test");
    }

    private int dpToPx() {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics()));
    }

    @UiThread
    public void notifyAdapter() {
        sortAllList(contentParentList);
        if (testAdapter == null) {
            testAdapter = new TestAdapter(getActivity(), testList,TestFragment.this,TestFragment.this);
            RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(getActivity(), 1);
            my_recycler_view.setLayoutManager(myLayoutManager);
            my_recycler_view.setItemAnimator(new DefaultItemAnimator());
            my_recycler_view.setAdapter(testAdapter);
        } else
            testAdapter.notifyDataSetChanged();
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

    public void sortAllList(List<ContentTableNew> contentParentList) {
        Collections.sort(contentParentList, new Comparator<ContentTableNew>() {
            @Override
            public int compare(ContentTableNew o1, ContentTableNew o2) {
                return o1.getNodeId().compareTo(o2.getNodeId());
            }
        });
    }

    @UiThread
    public void addContentToViewList(List<ContentTableNew> contentParentList) {
        this.contentParentList.clear();
        this.contentParentList.addAll(contentParentList);
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
            if (message.getMessage().equalsIgnoreCase(FC_Constants.LEVEL_CHANGED))
                onLevelChanged();
            else if (message.getMessage().equalsIgnoreCase(FC_Constants.BACK_PRESSED))
                backBtnPressed();
            else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_STARTED)) {
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
            } else if (message.getMessage().equalsIgnoreCase(FC_Constants.UNZIPPING_DATA_FILE))
                dialog_file_name.setText("Unzipping...\nPlease wait" + resName);
            else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_COMPLETE)) {
                dialog_file_name.setText("Updating Data");
                String folderPath = "";
                try {
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

    @UiThread
    @Override
    public void setSelectedLevel(List<ContentTable> contentTable) {
        rootLevelList = contentTable;
        presenter.insertNodeId(contentTable.get(TempHomeActivity.currentLevelNo).getNodeId());

        String jsonName = getLevelWiseJson();
        JSONArray testData = presenter.getTestData(jsonName);
        presenter.generateTestData(testData, rootLevelList.get(TempHomeActivity.currentLevelNo).getNodeId());
    }

    public void onLevelChanged() {
        contentParentList.clear();
        presenter.removeLastNodeId();
        presenter.insertNodeId(rootLevelList.get(TempHomeActivity.currentLevelNo).getNodeId());
//        presenter.getDataForList();
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
    public void doubleQuestionCheck() {
        for (int i = 0; i < testList.size(); i++) {
            for (int j = i + 1; j < testList.size(); j++) {
                if (testList.get(i).getCertiCode().equalsIgnoreCase(testList.get(j).getCertiCode())) {
                    testList.get(i).setCodeCount(testList.get(i).getCodeCount() + 1);
                }
            }
        }
    }

    @Click(R.id.btn_test_dw)
    void onDownLoadClick() {
        resName = rootLevelList.get(TempHomeActivity.currentLevelNo).getNodeTitle();
        resServerImageName = rootLevelList.get(TempHomeActivity.currentLevelNo).getNodeServerImage();
        downloadType = FC_Constants.TEST_DOWNLOAD;
        presenter.downloadResource(rootLevelList.get(TempHomeActivity.currentLevelNo).getNodeId());
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
//                presenter.insertTestSession();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void testOpenData(ContentTable testData) {
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
                    Intent mainNew = new Intent(getActivity(), WebViewActivity.class);
                    mainNew.putExtra("resPath", path.toString());
                    mainNew.putExtra("resId", gameID);
                    mainNew.putExtra("mode", "test");
                    mainNew.putExtra("gameLevel", "" + testData.getNodeDesc());
                    mainNew.putExtra("gameName", "" + testData.getNodeTitle());
                    mainNew.putExtra("gameType", "" + testData.getResourceType());
                    mainNew.putExtra("gameCategory", "" + testData.getNodeKeywords());
                    startActivityForResult(mainNew, 1);
                } else {
                    Toast.makeText(getActivity(), "Game not found", Toast.LENGTH_SHORT).show();
                }
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.OPPOSITE_WORDS)) {
                Intent mainNew = new Intent(getActivity(), FactRetrial_.class);
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


    @UiThread
    @Override
    public void hideTestDownloadBtn() {
        btn_test_dw.setVisibility(View.GONE);
    }

    @UiThread
    @Override
    public void initializeTheIndex() {
        my_recycler_view.removeAllViews();
        if (testList.size() > 0) {
            if (testAdapter == null) {
                testAdapter = new TestAdapter(getActivity(), testList, TestFragment.this, TestFragment.this);
                RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(getActivity(), 1);
                my_recycler_view.setLayoutManager(myLayoutManager);
                my_recycler_view.setItemAnimator(new DefaultItemAnimator());
                my_recycler_view.setAdapter(testAdapter);
            } else
                testAdapter.notifyDataSetChanged();
        } else
            btn_test_dw.setVisibility(View.VISIBLE);
        dismissLoadingDialog();
        //        testAdapter.initializeIndex();
    }

    private void hideTestDownloadBtnOnComplete() {
        if (rootLevelList.get(TempHomeActivity.currentLevelNo).isDownloaded.equalsIgnoreCase("true"))
            btn_test_dw.setVisibility(View.GONE);
        else
            btn_test_dw.setVisibility(View.VISIBLE);
    }

    @UiThread
    @Override
    public void displayCurrentDownloadedTest() {
        String jsonName = getLevelWiseJson();
        JSONArray testData = presenter.getTestData(jsonName);
        presenter.generateTestData(testData, rootLevelList.get(TempHomeActivity.currentLevelNo).getNodeId());
    }

    private String getLevelWiseJson() {
        String jsonName = "TestBeginnerJson.json";
        switch (TempHomeActivity.currentLevelNo) {
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

    @Override
    public void showNoDataDownloadedDialog() {
        final Dialog dialog = new Dialog(getActivity());
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
            if (testAdapter != null) {
                contentParentList.clear();
                testAdapter.notifyDataSetChanged();
            }
            dialog.dismiss();
        });
    }

    private boolean loaderVisible = false;
    private Dialog myLoadingDialog;

    @UiThread
    @Override
    public void showLoader() {
        if (!loaderVisible) {
            loaderVisible = true;
            myLoadingDialog = new Dialog(getActivity());
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
    public void dismissLoadingDialog() {
        if (myLoadingDialog != null) {
            loaderVisible = false;
            myLoadingDialog.dismiss();
        }
    }

    @Override
    public void setLevelprogress(int percent) {
//        level_progress.setCurProgress(percent);
    }

    private Dialog downloadDialog;
    private ProgressLayout progressLayout;
    private TextView dialog_file_name;

    @SuppressLint("SetTextI18n")
    private void resourceDownloadDialog(Modal_FileDownloading modal_fileDownloading) {
        downloadDialog = new Dialog(getActivity());
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

    @UiThread
    public void showDownloadErrorDialog() {
        Dialog errorDialog = new Dialog(getActivity());
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
//            presenter.getDataForList();
        } else {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onSpinnerLanguageChanged(String selectedLanguage) {
        this.language = selectedLanguage;
        if(testAdapter == null) {
            testAdapter = new TestAdapter(getActivity(), testList, TestFragment.this, TestFragment.this);
            RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(getActivity(), 1);
            my_recycler_view.setLayoutManager(myLayoutManager);
            my_recycler_view.setItemAnimator(new DefaultItemAnimator());
            my_recycler_view.setAdapter(testAdapter);
        }else
            testAdapter.notifyDataSetChanged();
    }
}