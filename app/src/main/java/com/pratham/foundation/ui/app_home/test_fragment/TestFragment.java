package com.pratham.foundation.ui.app_home.test_fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.collapsingView.RetractableToolbarUtil;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.modalclasses.CertificateModelClass;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.Modal_FileDownloading;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.app_home.HomeActivity;
import com.pratham.foundation.ui.contentPlayer.ContentPlayerActivity_;
import com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment.FactRetrieval_;
import com.pratham.foundation.ui.contentPlayer.matchingPairGame.MatchThePairGameActivity;
import com.pratham.foundation.ui.contentPlayer.old_cos.conversation.ConversationActivity_;
import com.pratham.foundation.ui.contentPlayer.old_cos.reading_cards.ReadingCardsActivity_;
import com.pratham.foundation.ui.contentPlayer.opposites.OppositesActivity_;
import com.pratham.foundation.ui.contentPlayer.reading_paragraphs.ReadingParagraphsActivity_;
import com.pratham.foundation.ui.contentPlayer.reading_rhyming.ReadingRhymesActivity_;
import com.pratham.foundation.ui.contentPlayer.reading_story_activity.ReadingStoryActivity_;
import com.pratham.foundation.ui.contentPlayer.vocabulary_qa.ReadingVocabularyActivity_;
import com.pratham.foundation.ui.contentPlayer.web_view.WebViewActivity;
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
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ui.app_home.HomeActivity.header_rl;
import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.CERTI_CODE;
import static com.pratham.foundation.utility.FC_Constants.CLOSE_TEST_EVENTBUS;
import static com.pratham.foundation.utility.FC_Constants.LEVEL_TEST_GIVEN;
import static com.pratham.foundation.utility.FC_Constants.currentLevel;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;
import static com.pratham.foundation.utility.FC_Constants.testSessionEnded;
import static com.pratham.foundation.utility.FC_Constants.testSessionEntered;

@EFragment(R.layout.fragment_test)
public class TestFragment extends Fragment implements TestContract.TestView,
        CertificateClicked, TestContract.LanguageSpinnerListner {

    @Bean(TestPresenter.class)
    TestContract.TestPresenter presenter;
    private List<CertificateModelClass> testList;

    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;
    @ViewById(R.id.btn_test_dw)
    Button btn_test_dw;
    @ViewById(R.id.ib_langChange)
    ImageButton ib_langChange;

    private TestAdapter testAdapter;
    private int clicked_Pos = 0;
    public List<ContentTable> rootList, rootLevelList, dwParentList, childDwContentList;
    public List<ContentTable> contentParentList, contentDBList, contentApiList, childContentList;
    private String downloadNodeId, resName, resServerImageName, downloadType, certi_Code = "";
    private int childPos = 0, parentPos = 0, resumeCntr = 0;
    public static String language = "Hindi";
    String currLang = "";
    Context context;


    @AfterViews
    public void initialize() {
        rootList = new ArrayList<>();
        rootLevelList = new ArrayList<>();
        dwParentList = new ArrayList<>();
        childDwContentList = new ArrayList<>();
        contentParentList = new ArrayList<>();
        testList = new ArrayList<>();
        presenter.setView(TestFragment.this);
        testSessionEntered = false;
        testSessionEnded = false;
        context = getActivity();
//        ib_langChange.setVisibility(View.GONE);
        my_recycler_view.addOnScrollListener(new RetractableToolbarUtil
                .ShowHideToolbarOnScrollingListener(header_rl));
        presenter.getBottomNavId(currentLevel, "Test");
    }

    private int dpToPx() {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics()));
    }

    @UiThread
    public void notifyAdapter() {
//        sortAllList(contentParentList);
        if (testAdapter == null) {
            testAdapter = new TestAdapter(context, testList, TestFragment.this, TestFragment.this);
            RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(context, 1);
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

    public void sortAllList(List<ContentTable> contentParentList) {
        Collections.sort(contentParentList, (o1, o2) -> o1.getNodeId().compareTo(o2.getNodeId()));
    }

    @Click(R.id.ib_langChange)
    public void langChangeButtonClick() {
        showLanguageSelectionDialog();
    }

    @SuppressLint("SetTextI18n")
    private void showLanguageSelectionDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_language_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Spinner lang_spinner = dialog.findViewById(R.id.lang_spinner);
        dia_btn_green.setText("OK");
        dialog.show();
        currLang = "" + FastSave.getInstance().getString(FC_Constants.LANGUAGE, "Hindi");
        dia_title.setText("Current Language : " + currLang);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, R.layout.custom_spinner,
                context.getResources().getStringArray(R.array.certificate_Languages));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang_spinner.setAdapter(dataAdapter);
        String[] languages = getResources().getStringArray(R.array.certificate_Languages);
        for (int i = 0; i < languages.length; i++) {
            if (currLang.equalsIgnoreCase(languages[i])) {
                lang_spinner.setSelection(i);
                break;
            }
        }

        lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language = lang_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dia_btn_green.setOnClickListener(v -> {
            onSpinnerLanguageChanged(language);
            dialog.dismiss();
        });
    }

    @UiThread
    public void addContentToViewList(List<ContentTable> contentParentList) {
        this.contentParentList.clear();
        this.contentParentList.addAll(contentParentList);
    }

    boolean eventBusFlg = false;

    @Override
    public void onStart() {
        FastSave.getInstance().saveString(APP_SECTION, ""+sec_Test);
        super.onStart();
        if (!eventBusFlg) {
            eventBusFlg = true;
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().contains(LEVEL_TEST_GIVEN))
                addTestStarResult(message.getMessage());
            if (message.getMessage().contains(CLOSE_TEST_EVENTBUS)) {
                eventBusFlg = false;
                EventBus.getDefault().unregister(this);
            }
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

    private void addTestStarResult(String levelTestGiven) {
        try {
            String[] splitRes = levelTestGiven.split(":");
            for (int i = 0; i < splitRes.length; i++) {
                Log.d("splitRes", "addTestStarResult: " + splitRes[i]);
            }
            String cCode = levelTestGiven.split(":")[1];
            int tMarks = Integer.parseInt(levelTestGiven.split(":")[2]);
            int sMarks = Integer.parseInt(levelTestGiven.split(":")[3]);
            if (cCode.equalsIgnoreCase(certi_Code)) {
                testList.get(clicked_Pos).setAsessmentGiven(true);
                testList.get(clicked_Pos).setTotalMarks(tMarks);
                testList.get(clicked_Pos).setScoredMarks(sMarks);
                float perc = 0f;
                if (tMarks > 0 && sMarks <= tMarks)
                    perc = ((float) sMarks / (float) tMarks) * 100;
                testList.get(clicked_Pos).setStudentPercentage("" + perc);
                testList.get(clicked_Pos).setCertificateRating(presenter.getStarRating(perc));
                testAdapter.notifyItemChanged(clicked_Pos, testList.get(clicked_Pos));
            }
//            } else {
//                testList.get(clicked_Pos).setAsessmentGiven(true);
//                testList.get(clicked_Pos).setTotalMarks(tMarks);
//                testList.get(clicked_Pos).setScoredMarks(sMarks);
//                float perc = 0f;
//                if (tMarks > 0 && sMarks <= tMarks)
//                    perc = ((float) sMarks / (float) tMarks) * 100;
//                testList.get(clicked_Pos).setStudentPercentage("" + perc);
//                testList.get(clicked_Pos).setCertificateRating(presenter.getStarRating(perc));
//                testAdapter.notifyItemChanged(clicked_Pos, testList.get(clicked_Pos));
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkAllAssessmentsDone();
    }

    @UiThread
    @Override
    public void setSelectedLevel(List<ContentTable> contentTable) {
        try {
            rootLevelList = contentTable;
            presenter.insertNodeId(contentTable.get(currentLevel).getNodeId());
            String jsonName = getLevelWiseJson();
            JSONArray testData = presenter.getTestData(jsonName);
            presenter.generateTestData(testData, rootLevelList.get(currentLevel).getNodeId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLevelChanged() {

        contentParentList.clear();
        testList.clear();
        presenter.removeLastNodeId();
        presenter.insertNodeId(rootLevelList.get(currentLevel).getNodeId());
        String jsonName = getLevelWiseJson();
        JSONArray testData = presenter.getTestData(jsonName);
        presenter.generateTestData(testData, rootLevelList.get(currentLevel).getNodeId());
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
        try {
            resName = rootLevelList.get(currentLevel).getNodeTitle();
            resServerImageName = rootLevelList.get(currentLevel).getNodeServerImage();
            downloadType = FC_Constants.TEST_DOWNLOAD;
            presenter.downloadResource(rootLevelList.get(currentLevel).getNodeId());
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void testOpenData(ContentTable testData) {
        try {
            FastSave.getInstance().saveString(APP_SECTION, "" + sec_Test);

            certi_Code = testData.getNodeDesc();
            FastSave.getInstance().saveString(CERTI_CODE, "" + certi_Code);

            if (testData.getResourceType().toLowerCase()
                    .contains(FC_Constants.HTML_GAME_RESOURCE)) {
                ContentTable testDataSplit = splitResouces(testData);
                String resPath;
                String gameID = testData.getResourceId();
                if (testData.isOnSDCard())
                    resPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + testDataSplit.getResourcePath();
                else
                    resPath = ApplicationClass.foundationPath + gameFolderPath + "/" + testDataSplit.getResourcePath();
                File file = new File(resPath);
                if (file.exists()) {
                    Uri path = Uri.fromFile(file);
                    Intent mainNew = new Intent(context, WebViewActivity.class);
                    mainNew.putExtra("resPath", path.toString());
                    mainNew.putExtra("resId", gameID);
                    mainNew.putExtra("mode", "test");
                    mainNew.putExtra("gameLevel", "" + testData.getNodeDesc());
                    mainNew.putExtra("gameName", "" + testData.getNodeTitle());
                    mainNew.putExtra("gameType", "" + testData.getResourceType());
                    mainNew.putExtra("sttLang", "English");
                    mainNew.putExtra("gameCategory", "" + testData.getNodeKeywords());
                    startActivityForResult(mainNew, 1);
                } else {
                    Toast.makeText(context, "Game not found", Toast.LENGTH_SHORT).show();
                }
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.OPPOSITE_WORDS)) {
                Intent mainNew = new Intent(context, FactRetrieval_.class);
                mainNew.putExtra("resId", testData.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", testData.getNodeTitle());
                mainNew.putExtra("sttLang", "English");
                mainNew.putExtra("onSdCard", testData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("contentPath", testData.getResourcePath());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.CONVO_RESOURCE)) {
                ContentTable randomTestData = presenter.getRandomData(testData.getResourceType(), testData.getNodeKeywords());
                Intent mainNew = new Intent(context, ConversationActivity_.class);
                mainNew.putExtra("storyId", randomTestData.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", randomTestData.getNodeTitle());
                mainNew.putExtra("onSdCard", randomTestData.isOnSDCard());
                mainNew.putExtra("contentPath", randomTestData.getResourcePath());
                mainNew.putExtra("sttLang", "English");
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.COMIC_CONVO_RESOURCE)) {
                ContentTable randomTestData = presenter.getRandomData(testData.getResourceType(), testData.getNodeKeywords());
                Intent mainNew = new Intent(context, ReadingCardsActivity_.class);
                mainNew.putExtra("storyId", randomTestData.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", randomTestData.getNodeTitle());
                mainNew.putExtra("onSdCard", randomTestData.isOnSDCard());
                mainNew.putExtra("sttLang", "English");
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("contentPath", randomTestData.getResourcePath());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.OPPOSITE_WORDS)) {
                Intent mainNew = new Intent(context, OppositesActivity_.class);
                mainNew.putExtra("resId", testData.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", testData.getNodeTitle());
                mainNew.putExtra("onSdCard", testData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("sttLang", "English");
                mainNew.putExtra("contentPath", testData.getResourcePath());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.RHYME_RESOURCE) || testData.getResourceType().equalsIgnoreCase(FC_Constants.STORY_RESOURCE)) {
                ContentTable randomTestData = presenter.getRandomData(testData.getResourceType(), testData.getNodeKeywords());
                Intent mainNew = new Intent(context, ReadingStoryActivity_.class);
                mainNew.putExtra("storyId", randomTestData.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("storyPath", randomTestData.getResourcePath());
                mainNew.putExtra("storyTitle", randomTestData.getNodeTitle());
                mainNew.putExtra("sttLang", "English");
                mainNew.putExtra("onSdCard", randomTestData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("contentType", randomTestData.getResourceType());
                startActivityForResult(mainNew, 1);
            } /*else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.WORD_ANDROID)) {
                Intent mainNew = new Intent(context, ReadingWordScreenActivity.class);
                mainNew.putExtra("resId", testData.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", testData.getResourcePath());
                mainNew.putExtra("onSdCard", testData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("contentTitle", testData.getNodeTitle());
                startActivityForResult(mainNew, 1);
            } */ else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.PARA_ANDROID)) {
                ContentTable randomTestData = presenter.getRandomData(testData.getResourceType(), testData.getNodeKeywords());
                Intent mainNew = new Intent(context, ReadingParagraphsActivity_.class);
                mainNew.putExtra("resId", randomTestData.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", randomTestData.getResourcePath());
                mainNew.putExtra("onSdCard", randomTestData.isOnSDCard());
                mainNew.putExtra("resType", randomTestData.getResourceType());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("sttLang", "English");
                mainNew.putExtra("contentTitle", randomTestData.getNodeTitle());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.PARA_VOCAB_ANDROID)) {
                ContentTable randomTestData = presenter.getRandomData(testData.getResourceType(), testData.getNodeKeywords());
                Intent mainNew = new Intent(context, ReadingParagraphsActivity_.class);
                mainNew.putExtra("resId", randomTestData.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", randomTestData.getResourcePath());
                mainNew.putExtra("onSdCard", randomTestData.isOnSDCard());
                mainNew.putExtra("resType", randomTestData.getResourceType());
                mainNew.putExtra("sttLang", "English");
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("contentTitle", randomTestData.getNodeTitle());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.VOCAB_ANDROID)) {
                Intent mainNew = new Intent(context, ReadingVocabularyActivity_.class);
                mainNew.putExtra("resId", testData.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", testData.getResourcePath());
                mainNew.putExtra("contentTitle", testData.getNodeTitle());
                mainNew.putExtra("sttLang", "English");
                mainNew.putExtra("vocabLevel", testData.getNodeDesc());
                mainNew.putExtra("onSdCard", testData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("vocabCategory", "$$$");
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.RHYMING_WORD_ANDROID)) {
                ContentTable randomTestData = presenter.getRandomData(testData.getResourceType(), testData.getNodeKeywords());
                Intent mainNew = new Intent(context, ReadingRhymesActivity_.class);
                mainNew.putExtra("resId", randomTestData.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", randomTestData.getResourcePath());
                mainNew.putExtra("contentTitle", randomTestData.getNodeTitle());
                mainNew.putExtra("onSdCard", randomTestData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("sttLang", "English");
                mainNew.putExtra("rhymeLevel", randomTestData.getNodeDesc());
                startActivityForResult(mainNew, 1);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.MATCH_THE_PAIR)) {
                Intent mainNew = new Intent(context, MatchThePairGameActivity.class);
                mainNew.putExtra("resId", testData.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentPath", testData.getResourcePath());
                mainNew.putExtra("contentTitle", testData.getNodeTitle());
                mainNew.putExtra("onSdCard", testData.isOnSDCard());
                mainNew.putExtra("sttLang", "English");
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("rhymeLevel", testData.getNodeDesc());
                startActivityForResult(mainNew, 1);
            } else {
                Intent mainNew = new Intent(context, ContentPlayerActivity_.class);
                mainNew.putExtra("testData", testData);
                mainNew.putExtra("testcall", "true");
                startActivityForResult(mainNew, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void hideTestDownloadBtn() {
        try {
            btn_test_dw.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void initializeTheIndex() {
        try {
            my_recycler_view.removeAllViews();
            if (testList.size() > 0) {
                if (testAdapter == null) {
                    testAdapter = new TestAdapter(context, testList, TestFragment.this, TestFragment.this);
                    RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(context, 1);
                    my_recycler_view.setLayoutManager(myLayoutManager);
                    my_recycler_view.setItemAnimator(new DefaultItemAnimator());
                    my_recycler_view.setAdapter(testAdapter);
                } else
                    testAdapter.notifyDataSetChanged();
            } else
                btn_test_dw.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dismissLoadingDialog();
        //        testAdapter.initializeIndex();
    }

    private void hideTestDownloadBtnOnComplete() {
        if (rootLevelList.get(currentLevel).isDownloaded.equalsIgnoreCase("true"))
            btn_test_dw.setVisibility(View.GONE);
        else
            btn_test_dw.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    }

    private void checkAllAssessmentsDone() {
        boolean testGiven = true;
//        if (!certiMode.equalsIgnoreCase("display")) {
        JSONObject jsonObjectAssessment = new JSONObject();
        for (int i = 0; i < testList.size(); i++) {
            try {
                if (!testList.get(i).getContentType().equalsIgnoreCase("header")) {
                    if (testList.get(i).isAsessmentGiven()) {
                        jsonObjectAssessment.put("CertCode" + i + "_" + testList.get(i).getCertiCode(),
                                "" + testList.get(i).getStudentPercentage());
                    } else {
                        testGiven = false;
                        break;
                    }
                }
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
    @UiThread
    public void showTestCompleteDialog() {
        try {
            CustomLodingDialog dialog = new CustomLodingDialog(context/*,R.style.ExitDialog*/);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.fc_custom_dialog);
/*      Bitmap map=FC_Utility.takeScreenShot(context);
        Bitmap fast=FC_Utility.fastblur(map, 20);
        final Drawable draw=new BitmapDrawable(getResources(),fast);
        dialog.getWindow().setBackgroundDrawable(draw);*/
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            TextView dia_title = dialog.findViewById(R.id.dia_title);
//        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
            Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
            Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
            Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);
            dia_btn_yellow.setVisibility(View.GONE);
            dia_btn_red.setVisibility(View.GONE);

            dia_title.setText(getResources().getString(R.string.Test_Complete_Dialog));
            dia_btn_green.setText(getResources().getString(R.string.Okay));

            dia_btn_green.setOnClickListener(v -> {
                onLevelChanged();
                dialog.dismiss();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void displayCurrentDownloadedTest() {
        String jsonName = getLevelWiseJson();
        JSONArray testData = presenter.getTestData(jsonName);
        presenter.generateTestData(testData, rootLevelList.get(currentLevel).getNodeId());
    }

    private String getLevelWiseJson() {
        String jsonName = "TestBeginnerJson.json";
        switch (currentLevel) {
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
            case 4:
                jsonName = "TestSeniorJson.json";
                break;
        }
        return jsonName;
    }

    @Override
    public void showNoDataDownloadedDialog() {
        try {
            final CustomLodingDialog dialog = new CustomLodingDialog(Objects.requireNonNull(context));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean loaderVisible = false;
    private CustomLodingDialog myLoadingDialog;

    @UiThread
    @Override
    public void showLoader() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismissLoadingDialog() {
        try {
            if (myLoadingDialog != null) {
                loaderVisible = false;
                myLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void setLevelprogress(int percent) {
        HomeActivity.tv_progress.setCurProgress(percent);
//        level_progress.setCurProgress(percent);
    }

    private CustomLodingDialog downloadDialog;
    private ProgressLayout progressLayout;
    private TextView dialog_file_name;

    @SuppressLint("SetTextI18n")
    private void resourceDownloadDialog(Modal_FileDownloading modal_fileDownloading) {
        downloadDialog = new CustomLodingDialog(context);
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
        CustomLodingDialog errorDialog = new CustomLodingDialog(context);
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(errorDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    @Override
    public void onSpinnerLanguageChanged(String selectedLanguage) {
        language = selectedLanguage;
        if (testAdapter == null) {
            testAdapter = new TestAdapter(context, testList, TestFragment.this, TestFragment.this);
            RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(context, 1);
            my_recycler_view.setLayoutManager(myLayoutManager);
            my_recycler_view.setItemAnimator(new DefaultItemAnimator());
            my_recycler_view.setAdapter(testAdapter);
        } else
            testAdapter.notifyDataSetChanged();
    }
}