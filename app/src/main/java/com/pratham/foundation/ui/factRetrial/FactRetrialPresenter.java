package com.pratham.foundation.ui.factRetrial;

import android.content.Context;

import com.pratham.foundation.ui.identifyKeywords.QuestionModel;
import com.pratham.foundation.utility.Utils;

import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@EBean
public class FactRetrialPresenter implements FactRetrialController.Presenter {
    private QuestionModel questionModel;
    private FactRetrialController.View view;
    private Context context;

    public FactRetrialPresenter(Context context) {
        this.view = (FactRetrialController.View) context;
        this.context = context;
    }

    /*public FactRetrialPresenter(FactRetrialController.View view) {
        this.view = view;
        context = (Context) view;
    }*/

    @Override
    public void getData() {
        //get data
        questionModel = new QuestionModel();
        String text = Utils.loadJSONFromAsset(context, "factRetrial.json");
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

        //  String[] instrumentNames = {"Firstly", "activity", "MainActivityPresenter", "through", "Interface"};
        //  questionModel.setParagraph("Firstly, This activity implements the MainActivityPresenter.View Interface through which it’s overridden method will be called. Secondly, we have to create the MainActivityPresenter object with view as a constructor. We use this presenter object to listen the user input and update the data as well as view. I think that's better to leverage the xml shape drawable resource power if that fits your needs. With a from scratch project (for android-8), define res/layout/main.xml");
        // questionModel.setKeywords(new ArrayList(Arrays.asList(instrumentNames)));
        view.showParagraph(questionModel);
    }
}
