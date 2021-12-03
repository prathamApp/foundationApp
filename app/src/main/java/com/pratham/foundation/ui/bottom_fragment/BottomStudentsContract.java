package com.pratham.foundation.ui.bottom_fragment;

import com.pratham.foundation.database.domain.StudentAndGroup_BottomFragmentModal;

import java.util.List;

public interface BottomStudentsContract {

    interface StudentClickListener {
        void onStudentClick(StudentAndGroup_BottomFragmentModal fragmentModal, int pos);
        void onGroupClick(String StudentName, String StudentId, String groupId);
    }


    interface BottomStudentsView {
        void setStudentList(List<StudentAndGroup_BottomFragmentModal> studentList);

        void notifyStudentAdapter();

        void dismissProgressDialog();

        void dismissProgressDialog2();

        void gotoNext();

        void clearList();
    }

    interface BottomStudentsPresenter {
        void setView(BottomStudentsContract.BottomStudentsView viewBottomStudents);

        void showStudents();

        void updateStudentData();

        void populateDB();

        void getStudentsFromGroup(String studentId);
    }

}
