package com.pratham.foundation.ui.selectSubject.testPDF;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.modalclasses.Diagnostic_pdf_Modal;

import org.androidannotations.annotations.EBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@EBean
public class ShowTestPDFPresenter implements ShowTestPDFContract.TestPDFPresenter {

    Context context;
    Gson gson;
    ShowTestPDFContract.TestPDFView testPDFView;
    List<Diagnostic_pdf_Modal> pdf_modalList;

    ShowTestPDFPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(ShowTestPDFContract.TestPDFView testPDFView) {
        this.testPDFView = testPDFView;
        gson = new Gson();
    }

    @Override
    public void getPDFs() {
        try {
//            Log.d("TAG", "getPDFs: IN");
            File dir = new File(ApplicationClass.getStoragePath() + "/PrathamBackups/StudentPDFs/");
            File[] pdf_files = dir.listFiles();
/*            String[] pdf_filesS = dir.list();
            for (int i = 0; i < pdf_filesS.length; i++) {
                Log.d("TAG", "getPDFs: "+pdf_filesS[i]);
            }*/
            if (pdf_files != null) {
                pdf_modalList = new ArrayList<>();
                List<String> fileNameListStrings = new ArrayList<>();
                for (int i = 0; i < pdf_files.length; i++) {
//                    Log.d("TAG", "getPDFs Before IF : i "+i+"   Name : "+pdf_files[i].getName());
                    try {
                        if (pdf_files[i].isFile() && pdf_files[i].getName().substring(pdf_files[i]
                                .getName().lastIndexOf(".")).equalsIgnoreCase(".pdf")) {

                            String[] arrOfStr = pdf_files[i].getName().split("_", 2);
//                        Log.d("TAG", "getPDFs: i "+i+"   Name : "+pdf_files[i].getName());

                            if (arrOfStr != null) {
                                String enrollment_id = pdf_files[i].getName().split("_")[0];
                                String subject_name = pdf_files[i].getName().split("_")[1];
//                                subject_name = subject_name.substring(0, subject_name.length() - 4);
//                            subject_name = subject_name.split(".")[0];
                                Log.d("TAG", "getPDFs: enrollment_id : " + enrollment_id);
                                Log.d("TAG", "getPDFs: subject_name : " + subject_name);
                                Student student = null;
                                student = AppDatabase.getDatabaseInstance(context).getStudentDao().getStudentByEnrollmentId(enrollment_id);
                                Diagnostic_pdf_Modal diagnosticPdfModal = new Diagnostic_pdf_Modal();
                                diagnosticPdfModal.setEnrollment_id(enrollment_id);
                                if (student != null)
                                    diagnosticPdfModal.setStudent_name(student.getFullName());
                                else
                                    diagnosticPdfModal.setStudent_name("(User Not Registered)");
                                diagnosticPdfModal.setSubject_name(subject_name);
                                diagnosticPdfModal.setFile_path(pdf_files[i].getAbsolutePath());
                                pdf_modalList.add(diagnosticPdfModal);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (pdf_modalList.size() > 0) {
                    testPDFView.setList(pdf_modalList);
                    testPDFView.setAdapter();
                } else
                    testPDFView.showNoData();
            } else {
                testPDFView.showNoData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            testPDFView.showNoData();
        }
    }
}