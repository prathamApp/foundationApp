package com.pratham.foundation.ui.contentPlayer.sequenceLayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.pratham.foundation.R;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_fragment.FactRetrival_;
import com.pratham.foundation.ui.contentPlayer.multipleChoice.McqFillInTheBlanksFragment;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_sequence_layout)
public class SequenceLayout extends Fragment implements SequeanceLayoutContract.SequenceLayoutView {

    @Bean(SequenceLayoutPresenterImp.class)
    SequeanceLayoutContract.SequenceLayoutPresenter sequenceLayoutPresenter;

    @ViewById(R.id.recycler_view)
    RecyclerView recyclerView;


    private String nodeID;
    private boolean onSdCard;
    List<ContentTable> contentTableList;

    public SequenceLayout() {
        // Required empty public constructor
    }

    @AfterViews
    public void initiate() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            nodeID = bundle.getString("nodeID");
            getListResData(nodeID);
            sequenceLayoutPresenter.setView(this);
            sequenceLayoutPresenter.getData();
        }
    }

    @Background
    public void getListResData(String nodeId) {
        try {
            contentTableList = new ArrayList<>();
            contentTableList = AppDatabase.appDatabase.getContentTableDao().getContentData("" + nodeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void loadUI(List<ContentTable> list) {
        GameConstatnts.gameList = list;
        SequenceGameAdapter sequenceGameAdapter = new SequenceGameAdapter(getActivity(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(sequenceGameAdapter);
    }

    @Click(R.id.playFromStart)
    public void playFromStartClick() {
        Bundle bundle = GameConstatnts.findGameData("108");
        if (bundle != null) {
            FC_Utility.showFragment(getActivity(), new McqFillInTheBlanksFragment(), R.id.RL_CPA,
                    bundle, McqFillInTheBlanksFragment.class.getSimpleName());
        } else {
            Toast.makeText(getActivity(), "resource not found", Toast.LENGTH_SHORT).show();
        }
    }
}