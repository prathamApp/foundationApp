package com.pratham.foundation.ui.test.certificate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.database.domain.SupervisorData;
import com.pratham.foundation.modalclasses.CertificateModelClass;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.contentPlayer.web_view.WebViewActivity;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.pratham.foundation.ui.test.certificate.CertificateActivity.assessmentProfile;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_FOLDER_NAME;


public class CertificatePresenter implements CertificateContract.CertificatePresenter {

    CertificateContract.CertificateView certificateView;
    Context context;
    public List<ContentTable> certiGameList;
    ArrayList<String> codesText;
    String testStudentId;
    JSONArray resultCodeList = null, quesCodeList = null;

    public CertificatePresenter(Context context, CertificateContract.CertificateView certificateView) {
        this.context = context;
        this.certificateView = certificateView;
        codesText = new ArrayList<>();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void getStudentName(String certiMode) {
        new AsyncTask<Object, Void, Object>() {
            String studName = "";

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Student student;
                    String sId = assessmentProfile.getStudentIDa();
                    student = AppDatabase.appDatabase.getStudentDao().getStudent(sId);
                    studName = "" + student.getFullName();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                certificateView.setStudentName(studName);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void proceed(JSONArray certiData, String nodeId) {
        try {
            new AsyncTask<Object, Void, Object>() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        certiGameList = AppDatabase.appDatabase.getContentTableDao().getContentData(nodeId);
                        WebViewActivity.gameLevel = certiGameList.get(0).getNodeAge();
                        BackupDatabase.backup(context);
                        codesText = new ArrayList<>();
                        codesText.clear();

                        try {
                            for (int j = 0; j < certiGameList.size(); j++) {

                                codesText.add(certiGameList.get(j).getNodeDesc());

                                CertificateModelClass contentTable = new CertificateModelClass();

                                contentTable.setCodeCount(Collections.frequency(codesText, certiGameList.get(j).getNodeDesc()));

                                contentTable.setNodeId("" + certiGameList.get(j).getNodeId());
                                contentTable.setCertiCode("" + certiGameList.get(j).getNodeDesc());
                                contentTable.setNodeAge("" + certiGameList.get(j).getNodeAge());
                                contentTable.setResourceId("" + certiGameList.get(j).getResourceId());
                                contentTable.setResourcePath("" + certiGameList.get(j).getResourcePath());
                                contentTable.setAsessmentGiven(false);
                                contentTable.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                                contentTable.setScoredMarks(0);
                                contentTable.setTotalMarks(0);
                                contentTable.setCertificateRating(0.0f);
                                contentTable.setStudentPercentage("");

                                for (int i = 0; i < certiData.length(); i++) {
                                    String lang = certiData.getJSONObject(i).getString("lang");
                                    String questionList = certiData.getJSONObject(i).getJSONObject("questionList").getString("" + certiGameList.get(j).getNodeDesc());
                                    String answerList = certiData.getJSONObject(i).getJSONObject("answerList").getString("" + certiGameList.get(j).getNodeDesc());
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
                            }

                            certificateView.doubleQuestionCheck();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    certificateView.initializeTheIndex();
                    certificateView.notifyAdapter();
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fillAdapter(Assessment assessmentProfile, JSONArray certiData) {
        try {

            JSONObject jsonObject;
/*            if (FC_Constants.GROUP_LOGIN) {
                testStudentId = assessmentProfile.getResourceIDa().split("$")[0];
                jsonObject = new JSONObject(assessmentProfile.getResourceIDa().split("$")[1]);
            }else*/
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
                    contentTable.setNodeAge("");
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
        certificateView.initializeTheIndex();
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
    public void recordTestData(JSONObject jsonObjectAssessment, String certiTitle) {
        new AsyncTask<Object, Void, Object>() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Assessment assessment = new Assessment();
                    assessment.setResourceIDa(jsonObjectAssessment.toString());
                    /*gameWebViewList.get(WebViewActivity.gameCounter).getResourceId()*/
//                            WebViewActivity.webResId);
                    assessment.setSessionIDa(FastSave.getInstance().getString(FC_Constants.ASSESSMENT_SESSION, ""));
                    assessment.setSessionIDm(FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                    assessment.setQuestionIda(0);
                    assessment.setScoredMarksa(0);
                    assessment.setTotalMarksa(0);
                    assessment.setStudentIDa(FastSave.getInstance().getString(FC_Constants.CURRENT_ASSESSMENT_STUDENT_ID, ""));
                    assessment.setStartDateTimea(""+FastSave.getInstance().getString(FC_Constants.CURRENT_FOLDER_NAME, ""));
//                    if (FC_Constants.GROUP_LOGIN)
//                        assessment.setStartDateTimea(FastSave.getInstance().getString(FC_Constants.CURRENT_ASSESSMENT_STUDENT_ID, "") + "_" + certiTitle);
//                    else
//                        assessment.setStartDateTimea("" + certiTitle);
                    assessment.setEndDateTime(FC_Utility.getCurrentDateTime());
                    if (FC_Constants.supervisedAssessment)
                        assessment.setDeviceIDa("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SUPERVISOR_ID, ""));
                    else
                        assessment.setDeviceIDa("na");
                    assessment.setLevela(Integer.parseInt(WebViewActivity.gameLevel));
                    assessment.setLabel("" + FC_Constants.CERTIFICATE_LBL);
                    assessment.setSentFlag(0);
                    AppDatabase.appDatabase.getAssessmentDao().insert(assessment);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    @Override
    public JSONArray fetchAssessmentList(String level) {
        JSONArray returnCodeList = null;
        try {
            InputStream is;
            String jsonName = "TestBeginnerJson.json";
            switch (level) {
                case "0":
                    jsonName = "TestBeginnerJson.json";
                    break;
                case "1":
                    jsonName = "TestSubJuniorJson.json";
                    break;
                case "2":
                    jsonName = "TestJuniorJson.json";
                    break;
                case "3":
                    jsonName = "TestSubSeniorJson.json";
                    break;
            }

            Log.d("CertiPresenter", "fetchAssessmentList: " + jsonName);

            is = context.getAssets().open("" + jsonName);
//            InputStream is = context.getAssets().open("CertificateData.json");
//            InputStream is = new FileInputStream(ApplicationClass.pradigiPath + "/.FCA/"+
//            FC_Constants.currentSelectedLanguage+"/Game/CertificateData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONArray jsonArray = new JSONArray(jsonStr);
            for(int i=0; i<jsonArray.length(); i++) {
                String subj = ((JSONObject) jsonArray.get(i)).get("storyLanguage").toString();
                if (subj.equalsIgnoreCase(FastSave.getInstance().getString(CURRENT_FOLDER_NAME, ""))) {
                    returnCodeList = ((JSONObject) jsonArray.get(i)).getJSONArray("CodeList");
                    return returnCodeList;
                }
            }

/*
            String jsonStr = new String(buffer);
            JSONObject jsonObj = new JSONObject(jsonStr);
            returnCodeList = jsonObj.getJSONArray("CodeList");
*/
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public void getSupervisorData(String certiMode) {
        new AsyncTask<Object, Void, Object>() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    SupervisorData supervisorData;
                    if (!certiMode.equalsIgnoreCase("display")) {
                        supervisorData = AppDatabase.appDatabase.getSupervisorDataDao().getSupervisorById(FastSave.getInstance().getString(FC_Constants.CURRENT_SUPERVISOR_ID, ""));
                    } else
                        supervisorData = AppDatabase.appDatabase.getSupervisorDataDao().getSupervisorById("" + assessmentProfile.getDeviceIDa());

                    certificateView.setSupervisorData("" + supervisorData.getSupervisorName(), "" +
                            Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/supervisorImages/" +
                            supervisorData.getSupervisorPhoto());

                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
        }.execute();
    }

    public void fetchAssessments() {
        try {
//            InputStream is = new FileInputStream(ApplicationClass.pradigiPath + "/.FCA/"+FC_Constants.currentSelectedLanguage+"/Game/CertificateData.json");
            InputStream is = context.getAssets().open("CertificateData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONObject jsonObj = new JSONObject(jsonStr);
            quesCodeList = jsonObj.getJSONArray("quesCodeList");
            resultCodeList = jsonObj.getJSONArray("resultCodeList");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //createLists(quesCodeList, resultCodeList);
    }

}