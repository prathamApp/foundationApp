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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.pratham.foundation.customView.collapsingView.RetractableToolbarUtil;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.progress_layout.ProgressLayout;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.modalclasses.CertificateModelClass;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.Modal_FileDownloading;
import com.pratham.foundation.services.shared_preferences.FastSave;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.ui.app_home.HomeActivity.header_rl;
import static com.pratham.foundation.ui.app_home.HomeActivity.levelChanged;
import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.CERTI_CODE;
import static com.pratham.foundation.utility.FC_Constants.CLOSE_TEST_EVENTBUS;
import static com.pratham.foundation.utility.FC_Constants.HINDI;
import static com.pratham.foundation.utility.FC_Constants.currentLevel;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;
import static com.pratham.foundation.utility.FC_Constants.testSessionEnded;
import static com.pratham.foundation.utility.FC_Constants.testSessionEntered;
import static com.pratham.foundation.utility.FC_Utility.getLevelWiseJson;
import static com.pratham.foundation.utility.FC_Utility.getLevelWiseTestName;

@EFragment(R.layout.fragment_test)
public class TestFragment extends Fragment implements TestContract.TestView,
        CertificateClicked, TestContract.LanguageSpinnerListner {

    @Bean(TestPresenter.class)
    TestContract.TestPresenter presenter;
    private List<CertificateModelClass> testList;
    private CertificateModelClass testOBJ;

    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;
    @ViewById(R.id.btn_test_dw)
    Button btn_test_dw;
    @ViewById(R.id.ib_langChange)
    ImageButton ib_langChange;
    @ViewById(R.id.rl_no_data)
    RelativeLayout rl_no_data;


    private TestAdapter testAdapter;
    private int clicked_Pos = 0;
    public List<ContentTable> rootList, rootLevelList, dwParentList, childDwContentList;
    public List<ContentTable> contentParentList, contentDBList, contentApiList, childContentList;
    private String downloadNodeId, resName, resServerImageName, downloadType, certi_Code = "";
    private int childPos = 0, parentPos = 0, resumeCntr = 0;
    public static String language = "Hindi";
    String currLang = "";
    Context context;
    private String[] languagesArray;


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
        testList = new ArrayList<>();
        presenter.setView(TestFragment.this);
        testSessionEntered = false;
        testSessionEnded = false;
        context = getActivity();
//        ib_langChange.setVisibility(View.GONE);
        my_recycler_view.addOnScrollListener(new RetractableToolbarUtil
                .ShowHideToolbarOnScrollingListener(header_rl));

        language = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, HINDI);
        Log.d("LANGUAGE TEST", "LANGUAGE TEST: "+language);

        ib_langChange.setVisibility(View.GONE);
        if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test)) {
            showLoader();
            presenter.getBottomNavId(currentLevel, sec_Test);
        }
    }

    @UiThread
    @Override
    public void showNoDataLayout() {
        try {
            dismissLoadingDialog();
            rl_no_data.setVisibility(View.VISIBLE);
            my_recycler_view.setVisibility(View.GONE);
            btn_test_dw.setVisibility(View.GONE);
            ib_langChange.setVisibility(View.GONE);
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

    private int dpToPx() {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics()));
    }

    @UiThread
    public void fragmentSelected() {
        try {
            showRecyclerLayout();
            ib_langChange.setVisibility(View.GONE);
            showLoader();
            language = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, HINDI);
            presenter.getBottomNavId(currentLevel, "" + sec_Test);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @UiThread
    public void notifyAdapter() {
        sortTestList(testList);
        if (testAdapter == null) {
            testAdapter = new TestAdapter(context, testList, TestFragment.this, TestFragment.this);
            RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(context, 1);
            my_recycler_view.setLayoutManager(myLayoutManager);
            my_recycler_view.setItemAnimator(new DefaultItemAnimator());
            my_recycler_view.setAdapter(testAdapter);
        } else
            testAdapter.notifyDataSetChanged();
        long delay;
        if (ApplicationClass.getAppMode())
            delay = 500L;
        else
            delay = 300L;
        dismissLoadingDialog();
        new Handler().postDelayed(() -> {
            try {
                if (downloadDialog != null)
                    downloadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delay);
    }

    public void sortAllList(List<ContentTable> contentParentList) {
        Collections.sort(contentParentList, (o1, o2) -> o1.getSeq_no() - o2.getSeq_no());
//        Collections.sort(contentParentList, (o1, o2) -> o1.getNodeId().compareTo(o2.getNodeId()));
    }

    public void sortTestList(List<CertificateModelClass> contentParentList) {
        Collections.sort(contentParentList, (o1, o2) -> o1.getSeq_no() - o2.getSeq_no());
//        Collections.sort(contentParentList, (o1, o2) -> o1.getNodeId().compareTo(o2.getNodeId()));
    }

    @Click(R.id.ib_langChange)
    public void langChangeButtonClick() {
        showLanguageSelectionDialog();
    }

    @Override
    public void setQuesTranslateLang(String[] languagesArray) {
        this.languagesArray = languagesArray;
    }

    BlurPopupWindow langDialog;
    Spinner lang_spinner;

    @UiThread
    @SuppressLint("SetTextI18n")
    public void showLanguageSelectionDialog() {
        try {
            langDialog = new BlurPopupWindow.Builder(context)
                    .setContentView(R.layout.fc_custom_language_dialog)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnTouchBackground(false)
                    .setDismissOnClickBack(false)
                    .bindClickListener(v -> {
                        new Handler().postDelayed(() -> {
                            onSpinnerLanguageChanged(language);
                            langDialog.dismiss();
                        }, 200);
                    }, R.id.dia_btn_green)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(10)
                    .setTintColor(0x30000000)
                    .build();

            TextView dia_title = langDialog.findViewById(R.id.dia_title);
            Button dia_btn_green = langDialog.findViewById(R.id.dia_btn_green);
            lang_spinner = langDialog.findViewById(R.id.lang_spinner);
            dia_btn_green.setText("OK");

            currLang = "" + HINDI;// + FastSave.getInstance().getString(FC_Constants.TEST_DISPLAY_LANGUAGE, "Hindi");
//        dia_title.setText("Current Language : " + currLang);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, R.layout.custom_spinner,
                    languagesArray);
//                context.getResources().getStringArray(R.array.certificate_Languages));
            dataAdapter.setDropDownViewResource(R.layout.custom_spinner);
            lang_spinner.setAdapter(dataAdapter);
            String[] languages = languagesArray;
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

            langDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private Array getLanguages() {
//        Array array;
//        String jsonName = getLevelWiseJson();
//        JSONArray testData = presenter.getLanguages(jsonName);
//    }

    @UiThread
    public void addContentToViewList(List<ContentTable> contentParentList) {
        this.contentParentList.clear();
        this.contentParentList.addAll(contentParentList);
    }

    boolean eventBusFlg = false;

    @Override
    public void onStart() {
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
        if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test)) {
            if (message != null) {
//            if (message.getMessage().contains(LEVEL_TEST_GIVEN))
//                addTestStarResult(message.getMessage());
                if (message.getMessage().contains(CLOSE_TEST_EVENTBUS)) {
                    eventBusFlg = false;
                    EventBus.getDefault().unregister(this);
                }
                if (message.getMessage().equalsIgnoreCase(FC_Constants.LEVEL_CHANGED))
                    fragmentSelected(); //onLevelChanged();
                else if (message.getMessage().equalsIgnoreCase(FC_Constants.BACK_PRESSED))
                    backBtnPressed();
                else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_STARTED)) {
                    dismissLoadingDialog();
                    resourceDownloadDialog(message.getModal_fileDownloading());
                } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FRAGMENT_SELECTED) ||
                        message.getMessage().equalsIgnoreCase(FC_Constants.FRAGMENT_RESELECTED)) {
                    fragmentSelected();
                } else if (message.getMessage().equalsIgnoreCase(FC_Constants.FILE_DOWNLOAD_UPDATE)) {
                    if (progressLayout != null)
                        progressLayout.setCurProgress(message.getModal_fileDownloading().getProgress());
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
                        int testIndex = currentLevel - 1;
                        rootLevelList.get(testIndex).setNodeUpdate(false);
                        isUpdate = false;
                        resName = "";
                        BackupDatabase.backup(context);
                        dismissDownloadDialog();
                        hideTestDownloadBtn();
                        displayCurrentDownloadedTest();
//                        if (downloadType.equalsIgnoreCase(FC_Constants.TEST_DOWNLOAD))
//                            presenter.updateDownloadJson(folderPath);
                    } catch (Exception e) {
                        downloadDialog.dismiss();
//                        dismissLoadingDialog();
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @UiThread
    public void showZipLoader() {
        dialog_file_name.setText("Loading\n" + resName + "\nPlease wait...");
        progressLayout.setVisibility(View.GONE);
        dialog_roundProgress.setVisibility(View.VISIBLE);
    }


//    private void addTestStarResult(String levelTestGiven) {
//        try {
//            String[] splitRes = levelTestGiven.split(":");
//            for (int i = 0; i < splitRes.length; i++) {
//                Log.d("splitRes", "addTestStarResult: " + splitRes[i]);
//            }
//            String cCode = levelTestGiven.split(":")[1];
//            int tMarks = Integer.parseInt(levelTestGiven.split(":")[2]);
//            int sMarks = Integer.parseInt(levelTestGiven.split(":")[3]);
//            if (cCode.equalsIgnoreCase(certi_Code)) {
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
////            } else {
////                testList.get(clicked_Pos).setAsessmentGiven(true);
////                testList.get(clicked_Pos).setTotalMarks(tMarks);
////                testList.get(clicked_Pos).setScoredMarks(sMarks);
////                float perc = 0f;
////                if (tMarks > 0 && sMarks <= tMarks)
////                    perc = ((float) sMarks / (float) tMarks) * 100;
////                testList.get(clicked_Pos).setStudentPercentage("" + perc);
////                testList.get(clicked_Pos).setCertificateRating(presenter.getStarRating(perc));
////                testAdapter.notifyItemChanged(clicked_Pos, testList.get(clicked_Pos));
////            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        checkAllAssessmentsDone();
//    }

    boolean isUpdate = false;

    @UiThread
    @Override
    public void setSelectedLevel(List<ContentTable> contentTable) {
        try {
            testList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    presenter.removeLastNodeId();
                    presenter.insertNodeId(rootLevelList.get(i).getNodeId());
                    String jsonName = getLevelWiseJson(Integer.parseInt(rootLevelList.get(i).getNodeTitle()));
                    isUpdate = rootLevelList.get(i).isNodeUpdate();
                    JSONArray testData = presenter.getTestData(jsonName);
                    if(testData!= null) {
                        boolean langFlg = false;
                        try {
                            for (int x = 0; x < testData.length(); x++) {
                                String lang = testData.getJSONObject(x).getString("lang");
                                if (lang.equalsIgnoreCase(FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, HINDI)))
                                    langFlg = true;
                            }
                            if (!langFlg)
                                language = testData.getJSONObject(0).getString("lang");
                            else
                                language = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, HINDI);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        presenter.generateTestData(testData, rootLevelList.get(i).getNodeId(), isUpdate);
                    }
                    else {
                        clearTestList();
                        my_recycler_view.removeAllViews();
                        testAdapter.notifyDataSetChanged();
                        dismissLoadingDialog();
                        ShowTestDownloadBtn();
                    }
                }
            } else if (rootLevelList != null) {
                if (rootLevelList.size() > 0) {
                    i = 0;
                    levelChanged.setActualLevel(rootLevelList, rootLevelList.get(i).getNodeTitle());
                    presenter.removeLastNodeId();
                    presenter.insertNodeId(rootLevelList.get(i).getNodeId());
                    String jsonName = getLevelWiseJson(Integer.parseInt(rootLevelList.get(i).getNodeTitle()));
                    isUpdate = rootLevelList.get(i).isNodeUpdate();
                    JSONArray testData = presenter.getTestData(jsonName);
                    boolean langFlg = false;
                    try {
                        for (int x = 0; x < testData.length(); x++) {
                            String lang = testData.getJSONObject(x).getString("lang");
                            if(lang.equalsIgnoreCase(FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, HINDI)))
                                langFlg=true;
                        }
                        if (!langFlg)
                            language = testData.getJSONObject(0).getString("lang");
                        else
                            language = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, HINDI);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    presenter.generateTestData(testData, rootLevelList.get(i).getNodeId(), isUpdate);
                } else {
                    testAdapter.notifyDataSetChanged();
                    dismissLoadingDialog();
                }
            } else {
                clearTestList();
                my_recycler_view.removeAllViews();
                testAdapter.notifyDataSetChanged();
                dismissLoadingDialog();
            }

        } catch (Exception e) {
            dismissLoadingDialog();
            e.printStackTrace();
        }
/*
        try {
            rootLevelList = contentTable;
            if (rootLevelList != null) {
                levelChanged.setActualLevel(rootLevelList.size());
            }
            presenter.insertNodeId(contentTable.get(currentLevel).getNodeId());
            String jsonName = getLevelWiseJson();
            isUpdate = rootLevelList.get(currentLevel).isNodeUpdate();
            JSONArray testData = presenter.getTestData(jsonName);
            presenter.generateTestData(testData, rootLevelList.get(currentLevel).getNodeId(), isUpdate);
        } catch (Exception e) {
            dismissLoadingDialog();
            e.printStackTrace();
        }
*/
    }

    public void onLevelChanged() {
        try {
            showRecyclerLayout();
            contentParentList.clear();
            testList.clear();
            ib_langChange.setVisibility(View.GONE);
            int i = 0;
            boolean found = false;
            for (i = 0; i < rootLevelList.size(); i++)
                if (rootLevelList.get(i).getNodeTitle().contains("" + currentLevel)) {
                    found = true;
                    break;
                }
            if (rootLevelList != null) {
                if (rootLevelList.size() > i) {
                    levelChanged.setActualLevel(rootLevelList, rootLevelList.get(i).getNodeTitle());
                    presenter.removeLastNodeId();
                    presenter.insertNodeId(rootLevelList.get(i).getNodeId());
                    String jsonName = getLevelWiseJson(Integer.parseInt(rootLevelList.get(i).getNodeTitle()));
                    isUpdate = rootLevelList.get(i).isNodeUpdate();
                    JSONArray testData = presenter.getTestData(jsonName);
                    boolean langFlg = false;
                    try {
                        for (int x = 0; x < testData.length(); x++) {
                            String lang = testData.getJSONObject(x).getString("lang");
                            if(lang.equalsIgnoreCase(FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, HINDI)))
                                langFlg=true;
                        }
                        if (!langFlg)
                            language = testData.getJSONObject(0).getString("lang");
                        else
                            language = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, HINDI);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    presenter.generateTestData(testData, rootLevelList.get(i).getNodeId(), isUpdate);
                }
            }

        } catch (Exception e) {
            dismissLoadingDialog();
            e.printStackTrace();
        }

/*        contentParentList.clear();
        testList.clear();
        ib_langChange.setVisibility(View.GONE);
        presenter.removeLastNodeId();
        presenter.insertNodeId(rootLevelList.get(currentLevel).getNodeId());
        String jsonName = getLevelWiseJson();
        isUpdate = rootLevelList.get(currentLevel).isNodeUpdate();
        JSONArray testData = presenter.getTestData(jsonName);
        presenter.generateTestData(testData, rootLevelList.get(currentLevel).getNodeId(), isUpdate);*/
    }

    @UiThread
    @Override
    public void clearTestList() {
        testList.clear();
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
        try {
            for (int i = 0; i < testList.size(); i++) {
                for (int j = i + 1; j < testList.size(); j++) {
                    if (testList.get(i).getCertiCode().equalsIgnoreCase(testList.get(j).getCertiCode())) {
                        testList.get(i).setCodeCount(testList.get(i).getCodeCount() + 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void onCertificateUpdate() {
        btn_test_dw.performClick();
    }

    @Click(R.id.btn_test_dw)
    void onDownLoadClick() {
        try {
            showLoader();
            int i = 0;
            for (i = 0; i < rootLevelList.size(); i++)
                if (rootLevelList.get(i).getNodeTitle().contains("" + currentLevel)) {
                    break;
                }
//            resName = rootLevelList.get(currentLevel).getNodeTitle();
            resName = getLevelWiseTestName(Integer.parseInt(rootLevelList.get(i).getNodeTitle()));
            resServerImageName = rootLevelList.get(i).getNodeServerImage();
            downloadType = FC_Constants.TEST_DOWNLOAD;
            presenter.downloadTestJson(Integer.parseInt(rootLevelList.get(i).getNodeTitle()));
            presenter.downloadResource(rootLevelList.get(i).getNodeId());
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
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
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
                    Intent mainNew = new Intent(context, WebViewActivity_.class);
                    mainNew.putExtra("resPath", path.toString());
                    mainNew.putExtra("resId", gameID);
                    mainNew.putExtra("mode", "test");
                    mainNew.putExtra("gameLevel", "" + testData.getNodeDesc());
                    mainNew.putExtra("gameName", "" + testData.getNodeTitle());
                    mainNew.putExtra("gameType", "" + testData.getResourceType());
                    mainNew.putExtra("sttLang", "English");
                    mainNew.putExtra("gameCategory", "" + testData.getNodeKeywords());
                    startActivityForResult(mainNew, 1461);
                } else {
                    showGameNotFoundToast();
                }
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.FACT_RETRIVAL)) {
                Intent mainNew = new Intent(context, FactRetrieval_.class);
                mainNew.putExtra("resId", testData.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", testData.getNodeTitle());
                mainNew.putExtra("sttLang", "English");
                mainNew.putExtra("onSdCard", testData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("contentPath", testData.getResourcePath());
                startActivityForResult(mainNew, 1461);
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
                startActivityForResult(mainNew, 1461);
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
                startActivityForResult(mainNew, 1461);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.OPPOSITE_WORDS)) {
                Intent mainNew = new Intent(context, OppositesActivity_.class);
                mainNew.putExtra("resId", testData.getResourceId());
                mainNew.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                mainNew.putExtra("contentName", testData.getNodeTitle());
                mainNew.putExtra("onSdCard", testData.isOnSDCard());
                mainNew.putExtra("certiCode", testData.getNodeDesc());
                mainNew.putExtra("sttLang", "English");
                mainNew.putExtra("contentPath", testData.getResourcePath());
                startActivityForResult(mainNew, 1461);
            } else if (testData.getResourceType().equalsIgnoreCase(FC_Constants.RHYME_RESOURCE) ||
                    testData.getResourceType().equalsIgnoreCase(FC_Constants.STORY_RESOURCE)) {
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
                startActivityForResult(mainNew, 1461);
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
                startActivityForResult(mainNew, 1461);
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
                startActivityForResult(mainNew, 1461);
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
                startActivityForResult(mainNew, 1461);
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
                startActivityForResult(mainNew, 1461);
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
                startActivityForResult(mainNew, 1461);
            } else {
                Intent mainNew = new Intent(context, ContentPlayerActivity_.class);
                mainNew.putExtra("testData", testData);
                mainNew.putExtra("testcall", "true");
                startActivityForResult(mainNew, 1461);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void showGameNotFoundToast() {
        Toast.makeText(context, "Game not found", Toast.LENGTH_SHORT).show();
    }

    @UiThread
    @Override
    public void hideTestDownloadBtn() {
        try {
            btn_test_dw.setVisibility(View.GONE);
            ib_langChange.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void ShowTestDownloadBtn() {
        try {
            btn_test_dw.setVisibility(View.VISIBLE);
            ib_langChange.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void initializeTheIndex() {
        try {
            dismissLoadingDialog();
            my_recycler_view.removeAllViews();
            if (testList.size() > 2) {
                hideTestDownloadBtn();
                if (testAdapter == null) {
                    testAdapter = new TestAdapter(context, testList, TestFragment.this, TestFragment.this);
                    RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(context, 1);
                    my_recycler_view.setLayoutManager(myLayoutManager);
                    my_recycler_view.setItemAnimator(new DefaultItemAnimator());
                    my_recycler_view.setAdapter(testAdapter);
                } else
                    testAdapter.notifyDataSetChanged();
            } else {
                btn_test_dw.setVisibility(View.VISIBLE);
                ib_langChange.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        testAdapter.initializeIndex();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1461)
            if (resultCode == Activity.RESULT_OK) {
                String cCode = data.getStringExtra("cCode");
                int tMarks = data.getIntExtra("tMarks", 0);
                int sMarks = data.getIntExtra("sMarks", 0);
                try {
//                    if (cCode.equalsIgnoreCase(certi_Code)) {
                    testList.get(clicked_Pos).setAsessmentGiven(true);
                    testList.get(clicked_Pos).setTotalMarks(tMarks);
                    testList.get(clicked_Pos).setScoredMarks(sMarks);
                    float perc = 0f;
                    if (tMarks > 0 && sMarks <= tMarks)
                        perc = ((float) sMarks / (float) tMarks) * 100;
                    testList.get(clicked_Pos).setStudentPercentage("" + perc);
                    testList.get(clicked_Pos).setCertificateRating(presenter.getStarRating(perc));
                    testAdapter.notifyItemChanged(clicked_Pos, testList.get(clicked_Pos));
//                    }
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
                testOBJ = testList.get(i);
                String cType = testOBJ.getContentType();
                if (!cType.equalsIgnoreCase("header") &&
                        !cType.equalsIgnoreCase("footer")) {
                    if (testOBJ.isAsessmentGiven()) {
                        jsonObjectAssessment.put("CertCode" + i + "_" + testOBJ.getCertiCode(),
                                "" + testOBJ.getStudentPercentage());
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

    BlurPopupWindow fcDialog;

    @SuppressLint("SetTextI18n")
    @UiThread
    public void showTestCompleteDialog() {
        try {
            fcDialog = new BlurPopupWindow.Builder(context)
                    .setContentView(R.layout.fc_custom_dialog)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnTouchBackground(false)
                    .setDismissOnClickBack(false)
                    .bindClickListener(v -> {
                        new Handler().postDelayed(() -> {
                            onLevelChanged();
                            fcDialog.dismiss();
                        }, 200);
                    }, R.id.dia_btn_green)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(10)
                    .setTintColor(0x30000000)
                    .build();

            TextView dia_title = fcDialog.findViewById(R.id.dia_title);
            Button dia_btn_yellow = fcDialog.findViewById(R.id.dia_btn_yellow);
            Button dia_btn_green = fcDialog.findViewById(R.id.dia_btn_green);
            Button dia_btn_red = fcDialog.findViewById(R.id.dia_btn_red);
            dia_btn_yellow.setVisibility(View.GONE);
            dia_btn_red.setVisibility(View.GONE);

            dia_title.setText(getResources().getString(R.string.Test_Complete_Dialog));
            dia_btn_green.setText(getResources().getString(R.string.Okay));

            fcDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void displayCurrentDownloadedTest() {
        int i = 0;
        for (i = 0; i < rootLevelList.size(); i++)
            if (rootLevelList.get(i).getNodeTitle().contains("" + currentLevel)) {
                break;
            }
//            resName = rootLevelList.get(currentLevel).getNodeTitle();
        String jsonName = getLevelWiseJson(Integer.parseInt(rootLevelList.get(i).getNodeTitle()));
        JSONArray testData = presenter.getTestData(jsonName);
        boolean langFlg = false;
        try {
            for (int x = 0; x < testData.length(); x++) {
                String lang = testData.getJSONObject(x).getString("lang");
                if(lang.equalsIgnoreCase(FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, HINDI)))
                    langFlg=true;
            }
            if (!langFlg)
                language = testData.getJSONObject(0).getString("lang");
            else
                language = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, HINDI);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        presenter.generateTestData(testData, rootLevelList.get(i).getNodeId(), isUpdate);
    }

    @Override
    public void showNoDataDownloadedDialog() {
        try {
            fcDialog = new BlurPopupWindow.Builder(context)
                    .setContentView(R.layout.fc_custom_dialog)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnTouchBackground(false)
                    .setDismissOnClickBack(true)
                    .setScaleRatio(0.2f)
                    .bindClickListener(v -> {
                        new Handler().postDelayed(() -> {
                            if (testAdapter != null) {
                                contentParentList.clear();
                                testAdapter.notifyDataSetChanged();
                            }
                            fcDialog.dismiss();
                        }, 200);
                    }, R.id.dia_btn_green)
                    .setBlurRadius(10)
                    .setTintColor(0x30000000)
                    .build();

            TextView title = fcDialog.findViewById(R.id.dia_title);
            Button btn_gree = fcDialog.findViewById(R.id.dia_btn_green);
            Button btn_yellow = fcDialog.findViewById(R.id.dia_btn_yellow);
            Button btn_red = fcDialog.findViewById(R.id.dia_btn_red);
            btn_gree.setText("Ok");
            title.setText("Connect to Internet");
            btn_red.setVisibility(View.GONE);
            btn_yellow.setVisibility(View.GONE);
            fcDialog.show();
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
                Objects.requireNonNull(myLoadingDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
    @UiThread
    public void dismissLoadingDialog() {
        try {
            loaderVisible = false;
            new Handler().postDelayed(() -> {
                if (myLoadingDialog != null && myLoadingDialog.isShowing())
                    myLoadingDialog.dismiss();
            }, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void setLevelprogress(int percent) {
//        HomeActivity.tv_progress.setCurProgress(percent);
//        level_progress.setCurProgress(percent);
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
//            downloadDialog = new BlurPopupWindow.Builder(context)
//                    .setContentView(R.layout.dialog_file_downloading)
//                    .setGravity(Gravity.CENTER)
//                    .setDismissOnTouchBackground(false)
//                    .setDismissOnClickBack(true)
//                    .setScaleRatio(0.2f)
//                    .setBlurRadius(10)
//                    .setTintColor(0x30000000)
//                    .build();
            downloadDialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
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