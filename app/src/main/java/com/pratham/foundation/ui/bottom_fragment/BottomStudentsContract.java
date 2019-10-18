package com.pratham.foundation.ui.bottom_fragment;

import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.QuestionModel;

import java.util.List;

public interface BottomStudentsContract {

    interface StudentClickListener {
        public void onStudentClick(String StudentName, String StudentId);
    }


    public interface BottomStudentsView {
        void setStudentList(List<Student> studentList);

        void notifyStudentAdapter();

        void dismissProgressDialog();

        void dismissProgressDialog2();

        void gotoNext();
    }

    public interface BottomStudentsPresenter {
        void setView(BottomStudentsContract.BottomStudentsView viewBottomStudents);

        void showStudents();

        void updateStudentData();

        void populateDB();
    }

}
