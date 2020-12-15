package com.pratham.foundation.ui.group_selection.fragment_child_attendance;

import com.pratham.foundation.database.domain.Student;


public interface ContractChildAttendance {
    interface attendanceView {
        void childItemClicked(Student student, int position);

        void clickPhoto(String studentID, int pos);

//        void moveToDashboardOnChildClick(Student student, int position, View v);
    }
}
