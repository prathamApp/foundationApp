package com.pratham.foundation.ui.test.certificate;

import android.content.Context;
import android.util.Log;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.modalclasses.CertificateModelClass;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.pratham.foundation.ui.test.certificate.CertificateActivity.assessmentProfile;
import static com.pratham.foundation.utility.FC_Utility.getLevelWiseJson;
import static com.pratham.foundation.utility.FC_Utility.getSubjectName;

@EBean
public class CertificatePresenter implements CertificateContract.CertificatePresenter {

    CertificateContract.CertificateView certificateView;
    Context context;
    public List<ContentTable> certiGameList;
    ArrayList<String> codesText;
    String testStudentId;
    JSONArray resultCodeList = null, quesCodeList = null;

    @Override
    public void setView(CertificateContract.CertificateView certificateView) {
        this.certificateView = certificateView;
        codesText = new ArrayList<>();
    }

    public CertificatePresenter(Context context) {
        this.context = context;
    }

    @Background
    @Override
    public void fillAdapter(Assessment assessmentProfile, String certiTitle) {
        try {

            JSONObject jsonObject;
            JSONArray certiData = fetchAssessmentList(certiTitle);
            jsonObject = new JSONObject(assessmentProfile.getResourceIDa());

            Iterator<String> iter = jsonObject.keys();
            codesText = new ArrayList<>();
            codesText.clear();

            while (iter.hasNext()) {
                String key = iter.next();

                try {
                    codesText.add(key.split("_")[1]);
                    CertificateModelClass contentTable = new CertificateModelClass();

                    contentTable.setCodeCount(Collections.frequency(codesText, key.split("_")[1]));
                    contentTable.setNodeId("" + assessmentProfile.getResourceIDa());
                    contentTable.setCertiCode("" + key.split("_")[1]);
                    contentTable.setNodeAge("Q");
                    contentTable.setResourceId("");
                    contentTable.setResourcePath("");
                    contentTable.setAsessmentGiven(true);
                    contentTable.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                    contentTable.setScoredMarks(0);
                    contentTable.setTotalMarks(0);
                    contentTable.setCertificateRating(getStarRating(Float.parseFloat(jsonObject.getString(key))));
                    contentTable.setStudentPercentage("" + jsonObject.getString(key));

                    for (int i = 0; i < certiData.length(); i++) {
                        String lang = certiData.getJSONObject(i).getString("lang");
                        String questionList = certiData.getJSONObject(i).getJSONObject("questionList").getString("" + key.split("_")[1]);
                        String answerList = certiData.getJSONObject(i).getJSONObject("answerList").getString("" + key.split("_")[1]);

                        if (lang.equalsIgnoreCase("english")) {
                            contentTable.setEnglishQues("" + questionList);
                            contentTable.setEnglishAnsw("" + answerList);
                        } else if (lang.equalsIgnoreCase("hindi")) {
                            contentTable.setHindiQues("" + questionList);
                            contentTable.setHindiAnsw("" + answerList);
                        } else if (lang.equalsIgnoreCase("marathi")) {
                            contentTable.setMarathiQues("" + questionList);
                            contentTable.setMarathiAnsw("" + answerList);
                        } else if (lang.equalsIgnoreCase("Gujarati")) {
                            contentTable.setGujaratiQues("" + questionList);
                            contentTable.setGujaratiAnsw("" + answerList);
                        } else if (lang.equalsIgnoreCase("Kannada")) {
                            contentTable.setKannadaQues("" + questionList);
                            contentTable.setKannadaAnsw("" + answerList);
                        } else if (lang.equalsIgnoreCase("Bengali")) {
                            contentTable.setBengaliQues("" + questionList);
                            contentTable.setBengaliAnsw("" + answerList);
                        } else if (lang.equalsIgnoreCase("Assamese")) {
                            contentTable.setAssameseQues("" + questionList);
                            contentTable.setAssameseAnsw("" + answerList);
                        } else if (lang.equalsIgnoreCase("Telugu")) {
                            contentTable.setTeluguQues("" + questionList);
                            contentTable.setTeluguAnsw("" + answerList);
                        } else if (lang.equalsIgnoreCase("Tamil")) {
                            contentTable.setTamilQues("" + questionList);
                            contentTable.setTamilAnsw("" + answerList);
                        } else if (lang.equalsIgnoreCase("Odia")) {
                            contentTable.setOdiaQues("" + questionList);
                            contentTable.setOdiaAnsw("" + answerList);
                        } else if (lang.equalsIgnoreCase("Urdu")) {
                            contentTable.setUrduQues("" + questionList);
                            contentTable.setUrduAnsw("" + answerList);
                        } else if (lang.equalsIgnoreCase("Punjabi")) {
                            contentTable.setPunjabiQues("" + questionList);
                            contentTable.setPunjabiAnsw("" + answerList);
                        }
                    }
                    certificateView.addContentToViewList(contentTable);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            certificateView.doubleQuestionCheck();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!assessmentProfile.getDeviceIDa().equalsIgnoreCase("na")) {
            CertificateModelClass contentTable = new CertificateModelClass();
            contentTable.setNodeAge("SUP");
            contentTable.setResourceId(""+assessmentProfile.getDeviceIDa());
            certificateView.addContentToViewList(contentTable);
        }
        certificateView.notifyAdapter();
    }

    @Override
    public float getStarRating(float perc) {
        float ratings = 5;

        if (perc < 21)
            ratings = (float) 1;
        else if (perc >= 21 && perc < 41)
            ratings = (float) 2;
        else if (perc >= 41 && perc < 61)
            ratings = (float) 3;
        else if (perc >= 61 && perc < 81)
            ratings = (float) 4;
        else if (perc >= 81)
            ratings = (float) 5;

        return ratings;
    }

    @Override
    public JSONArray fetchAssessmentList(String level) {
        JSONArray returnCodeList = null;
        try {
            String jsonName = getLevelWiseJson(Integer.parseInt(level));

            Log.d("CertiPresenter", "fetchAssessmentList: " + jsonName);

//            InputStream is = context.getAssets().open("" + jsonName);
//            InputStream is = context.getAssets().open("CertificateData.json");
            InputStream is = new FileInputStream(ApplicationClass.foundationPath + "/.FCA/" + jsonName);
//            FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI)+"/Game/CertificateData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONArray jsonArray = new JSONArray(jsonStr);
            String subjName = getSubjectName(assessmentProfile.getQuestionIda());
            for (int i = 0; i < jsonArray.length(); i++) {
                String subj = ((JSONObject) jsonArray.get(i)).get("storyLanguage").toString();
                if (subj.equalsIgnoreCase(subjName)) {
                    returnCodeList = ((JSONObject) jsonArray.get(i)).getJSONArray("CodeList");
                    return returnCodeList;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}