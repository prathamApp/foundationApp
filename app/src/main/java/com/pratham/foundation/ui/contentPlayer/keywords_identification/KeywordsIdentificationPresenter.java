package com.pratham.foundation.ui.contentPlayer.keywords_identification;

import android.content.Context;

import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

@EBean
public class KeywordsIdentificationPresenter implements KeywordsIdentificationContract.KeywordsPresenter{
    private QuestionModel questionModel;
    KeywordsIdentificationContract.KeywordsView viewKeywords;
    Context context;

    public KeywordsIdentificationPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(KeywordsIdentificationContract.KeywordsView viewKeywords) {
        this.viewKeywords = viewKeywords;
    }

    @Override
    public void getData() {
        questionModel = new QuestionModel();
        String text = FC_Utility.loadJSONFromAsset(context, "identifyKeywords.json");
        List instrumentNames = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(text);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            questionModel.setParagraph((String) jsonObject.get("paragraph"));
            JSONArray array = (JSONArray) jsonObject.get("keywords");
            for (int i = 0; i < array.length(); i++) {
                instrumentNames.add(array.getString(i));
            }
            questionModel.setKeywords(instrumentNames);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //get data
//        questionModel = new QuestionModel();
//        String[] instrumentNames = {"Firstly", "activity", "MainActivityPresenter", "through", "Interface"};
//        questionModel.setParagraph("Firstly, This activity implements the MainActivityPresenter.View Interface through which it’s overridden method will be called. Secondly, we have to create the MainActivityPresenter object with view as a constructor. We use this presenter object to listen the user input and update the data as well as view. I think that's better to leverage the xml shape drawable resource power if that fits your needs. With a from scratch project (for android-8), define res/layout/main.xml");
//        questionModel.setKeywords(new ArrayList(Arrays.asList(instrumentNames)));
        viewKeywords.showParagraph(questionModel);
    }
}

