package com.pratham.foundation.ui.app_home.profile_new.course_enrollment;

import android.content.Context;

import com.google.gson.Gson;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.modalclasses.Model_CourseEnrollment;
import com.pratham.foundation.modalclasses.Model_CourseExperience;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.utility.FC_Constants.CURRENT_STUDENT_ID;
import static com.pratham.foundation.utility.FC_Constants.newRootParentId;

@EBean
public class CourseEnrollmentPresenter implements CourseEnrollmentContract.CourseEnrollmentPresenter {

    private List<ContentTable> boardList, langList, subjList, setTabList, setLevelList;

    Context context;
    String selectedLangName = "na";
    CourseEnrollmentContract.CourseEnrollmentView viewCE;

    public CourseEnrollmentPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(CourseEnrollmentContract.CourseEnrollmentView viewCE) {
        this.viewCE = viewCE;
    }

    @Background
    @Override
    public void getRootData() {
        boardList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getRootDetails(newRootParentId,
                /*FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")*/
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%");
        if (boardList.size() > 0)
            viewCE.setBoardList(boardList);
        else
            viewCE.noCource();
    }

    @Background
    @Override
    public void loadLanguages(String selectedId) {
        langList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(selectedId,
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%"/*,
                FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")*/);
        viewCE.setLangList(langList);
    }

    @Background
    @Override
    public void loadSubjects(String selectedId, String selectedLangName) {
        this.selectedLangName = selectedLangName;
        subjList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(selectedId,
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%"/*,
                FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")*/);
        viewCE.setSubjList(subjList);
    }

    @Background
    @Override
    public void loadTabs(String selectedId) {
        setTabList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(selectedId,
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%"/*,
                FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")*/);
        viewCE.setTabList(setTabList);
    }

    @Background
    @Override
    public void loadLevels(String selectedId) {
        setLevelList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(selectedId,
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%"/*,
                FastSave.getInstance().getString(CURRENT_STUDENT_PROGRAM_ID,"na")*/);
        viewCE.setLevelList(setLevelList);
    }

    private HashMap<String, List<Model_CourseEnrollment>> coursesPerWeek = new HashMap<>();

    @Background
    @Override
    public void addCourseToDb(String week, ContentTable selectedCourse, Calendar startDate, Calendar endDate) {
        boolean isCourseAlreadyEnrolled = false, errorCourse = false;
        String groupId = FastSave.getInstance().getString(CURRENT_STUDENT_ID, "");
        try {
            List<Model_CourseEnrollment> courseEnrollments = enrolledCoursesFromDb(week, groupId);
            for (Model_CourseEnrollment cen : Objects.requireNonNull(courseEnrollments)) {
                if (selectedCourse.getNodeId().equalsIgnoreCase(cen.getCourseDetail().getNodeId())) {
                    isCourseAlreadyEnrolled = true;
                    break;
                }
            }
        } catch (Exception e) {
            errorCourse = true;
            e.printStackTrace();
        }
        try {
            if (!isCourseAlreadyEnrolled) {
//                if (!errorCourse) {
                    Model_CourseEnrollment courseEnrollment = new Model_CourseEnrollment();
                    courseEnrollment.setCoachVerificationDate("");
                    courseEnrollment.setCoachVerified(0);
                    //add experience as json object string in db
                    Model_CourseExperience model_courseExperience = new Model_CourseExperience();
                    model_courseExperience.setAssignments(null);
                    model_courseExperience.setWords_learnt("");
                    model_courseExperience.setAssignments_completed("");
                    model_courseExperience.setAssignments_description("");
                    model_courseExperience.setCoach_comments("");
                    model_courseExperience.setCoach_verification_date("");
                    model_courseExperience.setCoach_image("");
                    model_courseExperience.setAssignment_submission_date(FC_Utility.getCurrentDateTime());
                    model_courseExperience.setStatus(FC_Constants.COURSE_NOT_VERIFIED);

                    courseEnrollment.setCourseExperience("" + new Gson().toJson(model_courseExperience));
                    courseEnrollment.setCourseDetail(selectedCourse);
                    courseEnrollment.setCourseId(selectedCourse.getNodeId());
                    courseEnrollment.setGroupId(groupId);
                    courseEnrollment.setPlanFromDate(week + " " + startDate.getTime().toString());
                    courseEnrollment.setPlanToDate(week + " " + endDate.getTime().toString());
                    courseEnrollment.setSentFlag(0);
                    if (!selectedLangName.equalsIgnoreCase("na"))
                        courseEnrollment.setLanguage(selectedLangName);
                    else
                        courseEnrollment.setLanguage(FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI));

                    AppDatabase.getDatabaseInstance(context).getCourseDao().insertCourse(courseEnrollment);
                    viewCE.courseAdded();
                    BackupDatabase.backup(context);
//                } else
//                    viewCE.courseError();
            } else {
                //course is already added in that particular week
                viewCE.courseAlreadySelected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getEnrolledCourses() {
        List<Model_CourseEnrollment> courseEnrollments = AppDatabase.getDatabaseInstance(context).getCourseDao().
                fetchEnrolledCoursesNew(FastSave.getInstance().getString(CURRENT_STUDENT_ID, ""),
                        FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI));

        if (courseEnrollments.size() > 0) {
            for (int i = 0; i < courseEnrollments.size(); i++) {
                ContentTable contentTable = AppDatabase.getDatabaseInstance(context).getContentTableDao().getContent(courseEnrollments.get(i).getCourseId());
                if (contentTable != null) {
                    ContentTable contentTableLearn = AppDatabase.getDatabaseInstance(context).getContentTableDao().getContent(contentTable.getParentId());
                    ContentTable contentTableSubj = AppDatabase.getDatabaseInstance(context).getContentTableDao().getContent(contentTableLearn.getParentId());
                    contentTable.setSubject("" + contentTableSubj.getNodeTitle());
                    courseEnrollments.get(i).setCourseDetail(contentTable);
                } else {
                    contentTable = new ContentTable();
                    contentTable.setSubject(courseEnrollments.get(i).getLanguage());
                    courseEnrollments.get(i).setCourseDetail(contentTable);
                }
            }
            viewCE.addContentToViewList(courseEnrollments);
            viewCE.notifyAdapter();
        } else {
            viewCE.showNoData();
        }
    }

    public List<Model_CourseEnrollment> enrolledCoursesFromDb(String week, String groupId) {
        coursesPerWeek.remove(week);
        List<Model_CourseEnrollment> courseEnrollments = AppDatabase.getDatabaseInstance(context).getCourseDao().
                fetchEnrolledCourses(groupId, week, FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI));
        if (courseEnrollments == null) return null;
        List<Model_CourseEnrollment> temp = new ArrayList<>();
        for (Model_CourseEnrollment ce : courseEnrollments) {
            Model_CourseExperience courseExperience = new Gson().fromJson(ce.getCourseExperience(), Model_CourseExperience.class);
            ce.setCourseDetail(AppDatabase.getDatabaseInstance(context).getContentTableDao().getContent(ce.getCourseId()));
            temp.add(ce);
        }
        if (temp.size() > 0)
            coursesPerWeek.put(week, temp);
        return temp;
    }

}