package com.pratham.foundation.ui.contentPlayer.sequenceLayout;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.List;

@EBean
public class SequenceLayoutPresenterImp implements SequeanceLayoutContract.SequenceLayoutPresenter {
    private Context context;
    private List<ContentTable> gamesList;
    private SequeanceLayoutContract.SequenceLayoutView view;
    private boolean isTest = false;

    public SequenceLayoutPresenterImp(Context context) {
        this.context = context;
    }


    @Override
    public void getData() {
      /*  //get data
        String text = FC_Utility.loadJSONFromAsset(context, "contentPlayer.json");
        Gson gson = new Gson();
        Type type = new TypeToken<List<ContentTable>>() {
        }.getType();
        gamesList = gson.fromJson(text, type);*/
       // view.loadUI();
    }


    @Override
    public void setView(SequeanceLayoutContract.SequenceLayoutView sequenceLayoutView) {
        view = sequenceLayoutView;
    }


}
