package com.pratham.foundation.ui.contentPlayer.sequenceLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;

import com.pratham.foundation.R;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.doing.DoingFragment;
import com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment.FactRetrieval_;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.Fact_Retrieval_Fragment_;
import com.pratham.foundation.ui.contentPlayer.fillInTheBlanks.FillInTheBlanksFragment;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.KeywordsIdentificationFragment_;
import com.pratham.foundation.ui.contentPlayer.keywords_mapping.KeywordMappingFragment_;
import com.pratham.foundation.ui.contentPlayer.listenAndWritting.ListeningAndWritting_;
import com.pratham.foundation.ui.contentPlayer.multipleChoice.McqFillInTheBlanksFragment;
import com.pratham.foundation.ui.contentPlayer.new_reading_fragment.ContentReadingFragment;
import com.pratham.foundation.ui.contentPlayer.new_reading_fragment.ContentReadingFragment_;
import com.pratham.foundation.ui.contentPlayer.new_vocab_reading.VocabReadingFragment;
import com.pratham.foundation.ui.contentPlayer.new_vocab_reading.VocabReadingFragment_;
import com.pratham.foundation.ui.contentPlayer.paragraph_writing.ParagraphWritingFragment_;
import com.pratham.foundation.ui.contentPlayer.pictionary.pictionaryFragment;
import com.pratham.foundation.ui.contentPlayer.reading.ReadingFragment;
import com.pratham.foundation.ui.contentPlayer.trueFalse.TrueFalseFragment;
import com.pratham.foundation.ui.contentPlayer.video_view.ActivityVideoView_;
import com.pratham.foundation.ui.contentPlayer.word_writting.WordWritingFragment_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_sequence_layout)
public class SequenceLayout extends Fragment implements SequeanceLayoutContract.SequenceLayoutView,
        SequeanceLayoutContract.clickListner {

    @Bean(SequenceLayoutPresenterImp.class)
    SequeanceLayoutContract.SequenceLayoutPresenter sequenceLayoutPresenter;

    @ViewById(R.id.recycler_view)
    RecyclerView recyclerView;
    @ViewById(R.id.btn_back)
    ImageButton btn_back;

    private String nodeID;
    private boolean onSdCard;
    Context context;
    List<ContentTable> contentTableList;

    public SequenceLayout() {
        // Required empty public constructor
    }

    @AfterViews
    public void initiate() {
        context = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            nodeID = bundle.getString("nodeID");
            getListResData(nodeID);
            sequenceLayoutPresenter.setView(this);
            //sequenceLayoutPresenter.getData();
        }
    }

    @Background
    public void getListResData(String nodeId) {
        try {
            contentTableList = new ArrayList<>();
            contentTableList = AppDatabase.appDatabase.getContentTableDao().getContentData("" + nodeId);
            loadUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @UiThread
    public void loadUI() {
        GameConstatnts.gameList = contentTableList;
        SequenceGameAdapter sequenceGameAdapter = new SequenceGameAdapter(getActivity(), contentTableList, SequenceLayout.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(sequenceGameAdapter);
    }

    @Click(R.id.playFromStart)
    public void playFromStartClick() {
        GameConstatnts.currentGameAdapterposition = -1;
        GameConstatnts.plaGame(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void contentClicked(ContentTable contentTable) {
        Bundle bundle = new Bundle();
        bundle.putString("contentPath", contentTable.getResourcePath());
        bundle.putString("StudentID", FC_Constants.currentStudentID);
        bundle.putString("resId", contentTable.getResourceId());
        bundle.putString("contentName", contentTable.getNodeTitle());
        bundle.putBoolean("onSdCard", true);
        bundle.putString("jsonName", contentTable.getResourceType());

        switch (contentTable.getResourceType()) {
            case GameConstatnts.FACTRETRIEVAL:
                FC_Utility.showFragment((Activity) context, new FactRetrieval_(), R.id.RL_CPA,
                        bundle, FactRetrieval_.class.getSimpleName());
                break;
            case GameConstatnts.KEYWORD_IDENTIFICATION:
                FC_Utility.showFragment((Activity) context, new KeywordsIdentificationFragment_(), R.id.RL_CPA,
                        bundle, KeywordsIdentificationFragment_.class.getSimpleName());
                break;
            case GameConstatnts.KEYWORD_MAPPING:
                FC_Utility.showFragment((Activity) context, new KeywordMappingFragment_(), R.id.RL_CPA,
                        bundle, KeywordMappingFragment_.class.getSimpleName());
                break;
            case GameConstatnts.PARAGRAPH_WRITING:
                if (FC_Constants.currentLevel <= 2 ) {
                    FC_Utility.showFragment((Activity) context, new WordWritingFragment_(), R.id.RL_CPA,
                            bundle, WordWritingFragment_.class.getSimpleName());
                } else {
                    FC_Utility.showFragment((Activity) context, new ParagraphWritingFragment_(), R.id.RL_CPA,
                            bundle, ParagraphWritingFragment_.class.getSimpleName());
                }
                break;
            case GameConstatnts.LISTNING_AND_WRITTING:
                FC_Utility.showFragment((Activity) context, new ListeningAndWritting_(), R.id.RL_CPA,
                        bundle, ListeningAndWritting_.class.getSimpleName());
                break;
            case "106":
                FC_Utility.showFragment((Activity) context, new ReadingFragment(), R.id.RL_CPA,
                        bundle, ReadingFragment.class.getSimpleName());
                break;
            case "108":
                FC_Utility.showFragment((Activity) context, new McqFillInTheBlanksFragment(), R.id.RL_CPA,
                        bundle, McqFillInTheBlanksFragment.class.getSimpleName());
                break;
            case "109":
                FC_Utility.showFragment((Activity) context, new TrueFalseFragment(), R.id.RL_CPA,
                        bundle, TrueFalseFragment.class.getSimpleName());
                break;
            case "110":
                FC_Utility.showFragment((Activity) context, new FillInTheBlanksFragment(), R.id.RL_CPA,
                        bundle, FillInTheBlanksFragment.class.getSimpleName());
                break;
            case GameConstatnts.READINGGAME:
                FC_Utility.showFragment((Activity) context, new pictionaryFragment(), R.id.RL_CPA,
                        bundle, pictionaryFragment.class.getSimpleName());
                break;
            case "112":
                FC_Utility.showFragment((Activity) context, new Fact_Retrieval_Fragment_(), R.id.RL_CPA,
                        bundle, Fact_Retrieval_Fragment_.class.getSimpleName());
                break;
            case GameConstatnts.PICTIONARYFRAGMENT:
                FC_Utility.showFragment((Activity) context, new ContentReadingFragment_(), R.id.RL_CPA,
                        bundle, ContentReadingFragment.class.getSimpleName());
                break;
            case GameConstatnts.READ_VOCAB_ANDROID:
                FC_Utility.showFragment((Activity) context, new VocabReadingFragment_(), R.id.RL_CPA,
                        bundle, VocabReadingFragment.class.getSimpleName());
                break;
            case GameConstatnts.THINKANDWRITE:
            case GameConstatnts.DOING_ACT_READ:
            case GameConstatnts.DOING_ACT_VIDEO:
            case GameConstatnts.LetterWriting:
                FC_Utility.showFragment((Activity) context, new DoingFragment(), R.id.RL_CPA,
                        bundle, DoingFragment.class.getSimpleName());
                break;
            case GameConstatnts.VIDEO:
                Intent intent = new Intent(context, ActivityVideoView_.class);
                intent.putExtra("contentPath", contentTable.getResourcePath());
                intent.putExtra("StudentID", FC_Constants.currentStudentID);
                intent.putExtra("resId", contentTable.getResourceId());
                intent.putExtra("contentName", contentTable.getNodeTitle());
                intent.putExtra("onSdCard", true);
                context.startActivity(intent);
//                FC_Utility.showFragment((Activity) context, new ActivityVideoView_(), R.id.RL_CPA,
//                        bundle, ActivityVideoView.class.getSimpleName());
                break;
        }

    }

    @Click(R.id.btn_back)
    public void arrowBackpresses(){
        getActivity().onBackPressed();
    }
}