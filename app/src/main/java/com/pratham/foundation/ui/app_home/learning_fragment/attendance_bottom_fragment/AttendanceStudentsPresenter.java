package com.pratham.foundation.ui.app_home.learning_fragment.attendance_bottom_fragment;

import android.content.Context;

import com.google.gson.Gson;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

@EBean
public class AttendanceStudentsPresenter implements AttendanceStudentsContract.AttendanceStudentsPresenter {

    private AttendanceStudentsContract.AttendanceStudentsView myView;
    private Context context;
    Gson gson;
    private List<Student> studentDBList;
    private List<Student> fragmentModalsList;

    public AttendanceStudentsPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(AttendanceStudentsContract.AttendanceStudentsView viewBottomStudents) {
        this.myView = viewBottomStudents;
        gson = new Gson();
        fragmentModalsList = new ArrayList<>();
        studentDBList = new ArrayList<>();
    }

    @Background
    @Override
    public void showStudents() {
        try {

            fragmentModalsList.clear();
            myView.clearList();
            String sID = FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, "");
            studentDBList = AppDatabase.getDatabaseInstance(context).getStudentDao().getGroupwiseStudents(sID);

            if (studentDBList != null) {
                Student studentHeader = new Student();
                studentHeader.setStudentID("#####");
                studentHeader.setFullName("");
                studentHeader.setAvatarName("");
                fragmentModalsList.add(studentHeader);
            }

            if (studentDBList != null) {
                for (int i = 0; i < studentDBList.size(); i++) {
                    Student studentAvatar = new Student();
                    studentAvatar.setStudentID(studentDBList.get(i).getStudentID());
                    studentAvatar.setFullName(studentDBList.get(i).getFullName());
                    studentAvatar.setAvatarName(studentDBList.get(i).getAvatarName());
                    studentAvatar.setGender(studentDBList.get(i).getGender());
                    studentAvatar.setChecked(false);
                    fragmentModalsList.add(studentAvatar);
                }
            }

            if (fragmentModalsList != null) {
                Student studentHeader = new Student();
                studentHeader.setStudentID("#####");
                studentHeader.setFullName("");
                studentHeader.setAvatarName("");
                fragmentModalsList.add(studentHeader);
            }

            myView.setStudentList(fragmentModalsList);
            myView.notifyStudentAdapter();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

