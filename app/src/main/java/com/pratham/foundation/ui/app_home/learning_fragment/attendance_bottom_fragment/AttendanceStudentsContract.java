package com.pratham.foundation.ui.app_home.learning_fragment.attendance_bottom_fragment;

import com.pratham.foundation.database.domain.Student;

import java.util.List;

public interface AttendanceStudentsContract {

    interface AttendanceStudentClickListener {
        void onStudentClick(Student student, int pos);
    }


    interface AttendanceStudentsView {
        void setStudentList(List<Student> studentList);

        void notifyStudentAdapter();

        void dismissProgressDialog();

        void dismissProgressDialog2();

        void clearList();
    }

    interface AttendanceStudentsPresenter {
        void setView(AttendanceStudentsContract.AttendanceStudentsView viewBottomStudents);

        void showStudents();

    }

}
