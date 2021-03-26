package com.pratham.foundation.ui.app_home.profile_new.course_enrollment;

import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.modalclasses.Model_CourseEnrollment;

import java.util.Calendar;
import java.util.List;

public interface CourseEnrollmentContract {

    interface CourseEnrollmentView {
        void setBoardList(List<ContentTable> boardList);

        void setLangList(List<ContentTable> langList);

        void setSubjList(List<ContentTable> subjList);

        void setTabList(List<ContentTable> setTabList);

        void setLevelList(List<ContentTable> setLevelList);

        void courseAdded();

        void courseAlreadySelected();

        void noCource();

        void addContentToViewList(List<Model_CourseEnrollment> courseEnrollments);

        void notifyAdapter();

        void showNoData();

        void courseError();
    }

    interface CourseEnrollmentPresenter {
        void setView(CourseEnrollmentContract.CourseEnrollmentView CourseEnrollmentView);

        void getRootData();

        void loadLanguages(String selectedBoardId);

        void loadSubjects(String selectedLangId, String selectedLangName);

        void loadTabs(String selectedSubjectId);

        void loadLevels(String selectedTabId);

        void addCourseToDb(String week, ContentTable selectedCourse, Calendar startDate, Calendar endDate);

        void getEnrolledCourses();
    }
}
