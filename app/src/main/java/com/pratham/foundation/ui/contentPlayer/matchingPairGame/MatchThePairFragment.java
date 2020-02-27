package com.pratham.foundation.ui.contentPlayer.matchingPairGame;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.MatchThePair;
import com.pratham.foundation.utility.FC_Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MatchThePairFragment extends Fragment implements StartDragListener {

    @BindView(R.id.rl_english)
    RecyclerView rl_english;
    @BindView(R.id.rl_hindi)
    RecyclerView rl_hindi;
    

    boolean clickCnt = false;
    ItemTouchHelper touchHelper;
    ArrayList<MatchThePair> hindiList, englishList;
    String questionStartTime;
    List<MatchThePair> draggedList = new ArrayList<>();
    MatchThePairListner matchThePairListner;
    int correctCnt = 0;
    ItemTouchHelper.Callback callback;


  /*  public MatchThePairListner getMatchThePairListner() {
        return matchThePairListner;
    }*/

    public void setMatchThePairListner(MatchThePairListner matchThePairListner) {
        this.matchThePairListner = matchThePairListner;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.match_the_pair_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        questionStartTime = FC_Utility.getCurrentDateTime();
        Bundle bundle = getArguments();
        hindiList = (ArrayList<MatchThePair>) bundle.getSerializable("hindiList");
        englishList = (ArrayList<MatchThePair>) bundle.getSerializable("englishList");

        checkAnswer();
        while (correctCnt == hindiList.size()) {
            Collections.shuffle(hindiList);
            checkAnswer();
        }


        MatchPairAdapter matchPairAdapter = new MatchPairAdapter(englishList, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rl_english.setLayoutManager(linearLayoutManager);
        rl_english.setAdapter(matchPairAdapter);


        DragDropAdapter dragDropAdapter = new DragDropAdapter(hindiList, getActivity(), this);
        callback = new ItemMoveCallback(dragDropAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(null);
        touchHelper.attachToRecyclerView(rl_hindi);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity().getApplicationContext());
        rl_hindi.setLayoutManager(linearLayoutManager1);
        rl_hindi.setAdapter(dragDropAdapter);


    }




    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);

    }

    @Override
    public void onItemDragged(List<MatchThePair> draggedList) {
        this.draggedList = draggedList;
        correctCnt = 0;

        if (draggedList.size() > 0) {
            for (int i = 0; i < englishList.size(); i++) {
                if (draggedList.get(i).getLangText().equalsIgnoreCase(englishList.get(i).getLangText())) {
                    correctCnt++;
                    int finalI = i;
//                    new Handler().postDelayed(() -> {
                    View view = Objects.requireNonNull(rl_hindi.findViewHolderForAdapterPosition(i)).itemView;
                    view.setBackground(getResources().getDrawable(R.drawable.ripple_rectangle_green));
                    Objects.requireNonNull(rl_english.findViewHolderForAdapterPosition(i)).itemView.setBackground(getResources().getDrawable(R.drawable.ripple_rectangle_green));
//                    }, 600);
                } else {
                    View view = Objects.requireNonNull(rl_hindi.findViewHolderForAdapterPosition(i)).itemView;
                    view.setBackground(getResources().getDrawable(R.drawable.ripple_rectangle_card));
                    Objects.requireNonNull(rl_english.findViewHolderForAdapterPosition(i)).itemView.setBackground(getResources().getDrawable(R.drawable.ripple_rectangle_card));
                }
            }

        }


        if (correctCnt == draggedList.size()) {
      /*      try {
                touchHelper.attachToRecyclerView(null);
            } catch (Exception e){
                e.printStackTrace();
            }*/
            new Handler().postDelayed(() -> {
//                touchHelper.attachToRecyclerView(rl_hindi);
                if (matchThePairListner != null) {
                    matchThePairListner.onItemDragListener(draggedList);
                }

            }, 1500);
        }

    }
      /*  } else if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
        if (draggedList.size() > 0) {
            for (int i = 0; i < draggedList.size(); i++) {
                if (draggedList.get(i).getLangText().equalsIgnoreCase(englishList.get(i).getLangText()))
                    correctCnt++;
            }

            int finalCorrectCnt = correctCnt;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (finalCorrectCnt == draggedList.size()) {
                            *//*showCorrectDialog();
                            addLearntWords();
                            addScore();*//*

                        if (matchThePairListner != null) {
                            matchThePairListner.onItemDragListener(draggedList);
                        }
                    }
                }
            }, 1000);

            }
        }*/

    @OnClick(R.id.btn_check)
    public void checkAnsClick() {
        if (draggedList.size() == 0) {
            new BubbleShowCaseBuilder(getActivity())
                    .title("Note: ")
                    .description("swap to match the answer on the right to the sentence on the left")
                    .backgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccentDark))
                    .closeActionImage(ContextCompat.getDrawable(getActivity(), R.drawable.ic_close))
                    .targetView(rl_hindi.getChildAt(0)).show();
        } else checkAnswer();
    }


    public void checkAnswer() {
        correctCnt = 0;
        if (draggedList.size() > 0) {
            for (int i = 0; i < draggedList.size(); i++) {
                if (draggedList.get(i).getLangText().equalsIgnoreCase(englishList.get(i).getLangText()))
                    correctCnt++;
            }

            if (correctCnt == draggedList.size())
                matchThePairListner.onSubmitClicked(true);
            else matchThePairListner.onSubmitClicked(false);

        } else {

            for (int i = 0; i < hindiList.size(); i++) {
                if (hindiList.get(i).getLangText().equalsIgnoreCase(englishList.get(i).getLangText()))
                    correctCnt++;
            }

         /*   if (correctCnt == hindiList.size())
                matchThePairListner.onSubmitClicked(true);
            else matchThePairListner.onSubmitClicked(false);
*/

        }
    }
}


