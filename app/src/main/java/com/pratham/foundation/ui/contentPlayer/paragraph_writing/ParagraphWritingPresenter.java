package com.pratham.foundation.ui.contentPlayer.paragraph_writing;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.pratham.foundation.ui.identifyKeywords.QuestionModel;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@EBean
public class ParagraphWritingPresenter implements ParagraphWritingContract.ParagraphWritingPresenter{
    private QuestionModel questionModel;
    private ParagraphWritingContract.ParagraphWritingView view;
    private Context context;

    public ParagraphWritingPresenter(Context context) {
        this.context = context;
    }

    public void setView(ParagraphWritingContract.ParagraphWritingView view) {
        this.view = view;
    }

    @Override
    public void getData() {

        questionModel = new QuestionModel();
        String text = FC_Utility.loadJSONFromAsset(context, "factRetrial.json");
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
       /* questionModel = new QuestionModel();
        String[] instrumentNames = {"Firstly", "activity", "MainActivityPresenter", "through", "Interface"};
        questionModel.setParagraph("Firstly, This activity implements the MainActivityPresenter.View Interface through which it’s overridden method will be called. Secondly, we have to create the MainActivityPresenter object with view as a constructor. We use this presenter object to listen the user input and update the data as well as view. I think that's better to leverage the xml shape drawable resource power if that fits your needs. With a from scratch project (for android-8), define res/layout/main.xml");
        questionModel.setKeywords(new ArrayList(Arrays.asList(instrumentNames)));*/
        view.showParagraph(questionModel);
    }



    public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        try {

            File direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FC");
            if (!direct.exists()) direct.mkdir();
            direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FC/Internal");
            if (!direct.exists()) direct.mkdir();
            direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FC/Internal/photos");
            if (!direct.exists()) direct.mkdir();

            File file = new File(direct, fileName);

            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // isPhotoSaved = true;
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
