package com.pratham.foundation.ui.test.assessment_type;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.database.domain.Session;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.database.domain.SupervisorData;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.ASSESSMENT_SESSION;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_SUPERVISOR_ID;
import static com.pratham.foundation.utility.FC_Constants.assessmentSession;
import static com.pratham.foundation.utility.FC_Constants.currentsupervisorID;


public class TestTypePresenter implements TestTypeContract.TestTypePresenter {

    Context context;
    TestTypeContract.TestTypeView testTypeView;
    List<Student> studentList;

    public TestTypePresenter(Context context, TestTypeContract.TestTypeView testTypeView) {
        this.context = context;
        this.testTypeView = testTypeView;
    }

    public void AddSupervisor(final String supervisorID, final String sName,
                              final String supervisorPhoto) {
        new AsyncTask<Object, Void, Object>() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    assessmentSession = "test-" + ApplicationClass.getUniqueID();
                    FastSave.getInstance().saveString(ASSESSMENT_SESSION, assessmentSession);
                    SupervisorData supervisorData = new SupervisorData();
                    supervisorData.setSupervisorId(supervisorID);
                    supervisorData.setSupervisorName(sName);
                    supervisorData.setAssessmentSessionId(assessmentSession);
                    supervisorData.setSupervisorPhoto(supervisorPhoto);

                    AppDatabase.appDatabase.getSupervisorDataDao().insert(supervisorData);
                    BackupDatabase.backup(context);

                    AppDatabase.appDatabase.getStatusDao().updateValue("AssessmentSession", "" + assessmentSession);
                    assessmentSession = assessmentSession;
                    FC_Constants.assessmentFlag = true;

                    String AppStartDateTime = AppDatabase.appDatabase.getStatusDao().getValue("AppStartDateTime");
                    Session startSesion = new Session();
                    startSesion.setSessionID("" + assessmentSession);
                    String timerTime = FC_Utility.getCurrentDateTime();
                    Log.d("doInBackground", "--------------------------------------------doInBackground : " + timerTime);
                    startSesion.setFromDate(timerTime);
                    startSesion.setToDate("NA");
                    startSesion.setSentFlag(0);
                    currentsupervisorID = "" + supervisorID;
                    FastSave.getInstance().saveString(CURRENT_SUPERVISOR_ID, currentsupervisorID);
                    AppDatabase.appDatabase.getSessionDao().insert(startSesion);
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                getData();
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void getData() {
        new AsyncTask<Object, Void, Object>() {
            ContentTable contentTable = new ContentTable();

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    List<ContentTable> dwParentList;
                    dwParentList = new ArrayList<>();
                    dwParentList = AppDatabase.appDatabase.getContentTableDao().getNodeData("" + TestTypeActivity.nodeId);
                    try {
                                contentTable.setNodeId("" + dwParentList.get(0).getNodeId());
                                contentTable.setNodeType("" + dwParentList.get(0).getNodeType());
                                contentTable.setNodeTitle("" + dwParentList.get(0).getNodeTitle());
                                contentTable.setNodeKeywords("" + dwParentList.get(0).getNodeKeywords());
                                contentTable.setNodeAge("" + dwParentList.get(0).getNodeAge());
                                contentTable.setNodeDesc("" + dwParentList.get(0).getNodeDesc());
                                contentTable.setNodeServerImage("" + dwParentList.get(0).getNodeServerImage());
                                contentTable.setNodeImage("" + dwParentList.get(0).getNodeImage());
                                contentTable.setResourceId("" + dwParentList.get(0).getResourceId());
                                contentTable.setResourceType("" + dwParentList.get(0).getResourceType());
                                contentTable.setResourcePath("" + dwParentList.get(0).getResourcePath());
                                contentTable.setParentId("" + dwParentList.get(0).getParentId());
                                contentTable.setLevel("" + dwParentList.get(0).getLevel());
                                contentTable.setContentType(dwParentList.get(0).getContentType());
                                contentTable.setIsDownloaded("" + dwParentList.get(0).getIsDownloaded());
                                contentTable.setOnSDCard(false);
                                contentTable.setNodelist(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return contentTable;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                testTypeView.StartTestActivity(contentTable.getNodeId(),contentTable.getNodeTitle());
            }
        }.execute();
    }

    @Override
    public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        try {

            File direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal");
            if (!direct.exists()) direct.mkdir();
            direct = new File(Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/supervisorImages");
            if (!direct.exists()) direct.mkdir();

            File file = new File(direct, fileName);

            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            testTypeView.setPhotoSaved(true);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getStudents() {
        if (ApplicationClass.isTablet && FC_Constants.GROUP_LOGIN) {
            new AsyncTask<Object, Void, Object>() {

                @Override
                protected Object doInBackground(Object[] objects) {
                    try {
                        studentList = AppDatabase.appDatabase.getStudentDao().getGroupwiseStudents(FC_Constants.currentStudentID);
                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    testTypeView.showAttendence();
                    testTypeView.notifyTestAdapter(studentList);
                }
            }.execute();
        } else {
/*            if (FC_Constants.supervisedAssessment)
                testTypeView.StartTestDisplayActivity();*/
        }
    }


}