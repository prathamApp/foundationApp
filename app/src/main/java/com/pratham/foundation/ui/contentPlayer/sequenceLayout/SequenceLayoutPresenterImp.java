package com.pratham.foundation.ui.contentPlayer.sequenceLayout;

import android.content.Context;

import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.services.shared_preferences.FastSave;

import org.androidannotations.annotations.EBean;

import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

@EBean
public class SequenceLayoutPresenterImp implements SequeanceLayoutContract.SequenceLayoutPresenter {
    private Context context;
    private List<ContentTable> gamesList;
    private SequeanceLayoutContract.SequenceLayoutView view;
    private boolean isTest;
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
        isTest= FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Test);
    }
}
