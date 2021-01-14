package com.pratham.foundation.ui.app_home.profile_new.course_enrollment;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
    CourseEnrollmentContract.CourseEnrollmentView viewCE;

    public CourseEnrollmentPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(CourseEnrollmentContract.CourseEnrollmentView viewCE) {
        this.viewCE = viewCE;
    }

    @Override
    public void getRootData() {
        boardList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(newRootParentId,
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%");
        if (boardList.size() > 0)
            viewCE.setBoardList(boardList);
        else
            viewCE.noCource();
    }

    @Override
    public void loadLanguages(String selectedId) {
        langList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(selectedId,
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%");
        viewCE.setLangList(langList);
    }

    @Override
    public void loadSubjects(String selectedId) {
        subjList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(selectedId,
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%");
        viewCE.setSubjList(subjList);
    }

    @Override
    public void loadTabs(String selectedId) {
        setTabList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(selectedId,
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%");
        viewCE.setTabList(setTabList);
    }

    @Override
    public void loadLevels(String selectedId) {
        setLevelList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getLanguages(selectedId,
                "%" + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "na") + "%");
        viewCE.setLevelList(setLevelList);
    }

    private HashMap<String, List<Model_CourseEnrollment>> coursesPerWeek = new HashMap<>();

    @Background
    @Override
    public void addCourseToDb(String week, ContentTable selectedCourse, Calendar startDate, Calendar endDate) {
        String groupId = FastSave.getInstance().getString(CURRENT_STUDENT_ID, "");
        List<Model_CourseEnrollment> courseEnrollments = enrolledCoursesFromDb(week, groupId);
        boolean isCourseAlreadyEnrolled = false;
        for (Model_CourseEnrollment cen : Objects.requireNonNull(courseEnrollments)) {
            if (selectedCourse.getNodeId().equalsIgnoreCase(cen.getCourseDetail().getNodeId())) {
                isCourseAlreadyEnrolled = true;
                break;
            }
        }
        if (!isCourseAlreadyEnrolled) {
            Model_CourseEnrollment courseEnrollment = new Model_CourseEnrollment();
            courseEnrollment.setCoachVerificationDate("");
            courseEnrollment.setCoachVerified(false);
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

            courseEnrollment.setCourseExperience(new Gson().toJson(model_courseExperience));
            courseEnrollment.setCourseDetail(selectedCourse);
            courseEnrollment.setCourseId(selectedCourse.getNodeId());
            courseEnrollment.setGroupId(groupId);
            courseEnrollment.setPlanFromDate(week + " " + startDate.getTime().toString());
            courseEnrollment.setPlanToDate(week + " " + endDate.getTime().toString());
            courseEnrollment.setSentFlag(0);
            courseEnrollment.setLanguage(FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI));
            //add @courseEnrollment in hashmap and db
            List<Model_CourseEnrollment> enrollments;
            if (coursesPerWeek.containsKey(week)) {
                enrollments = new ArrayList<>(Objects.requireNonNull(coursesPerWeek.get(week)));
                enrollments.add(courseEnrollment);
            } else {
                enrollments = new ArrayList<>();
                enrollments.add(courseEnrollment);
            }
            coursesPerWeek.put(week, enrollments);
            AppDatabase.getDatabaseInstance(context).getCourseDao().insertCourse(courseEnrollment);
            viewCE.courseAdded();
            BackupDatabase.backup(context);

        } else {
            //course is already added in that particular week
            viewCE.courseAlreadySelected();
        }
    }

    @Override
    public void getEnrolledCourses() {
        List<Model_CourseEnrollment> courseEnrollments = AppDatabase.getDatabaseInstance(context).getCourseDao().
                fetchEnrolledCoursesNew(FastSave.getInstance().getString(CURRENT_STUDENT_ID, ""),
                        FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI));

        if(courseEnrollments.size()>0) {
            for(int i =0 ; i<courseEnrollments.size(); i++){
                ContentTable contentTable = AppDatabase.getDatabaseInstance(context).getContentTableDao().getContent(courseEnrollments.get(i).getCourseId());
                if(contentTable != null) {
                    ContentTable contentTableLearn = AppDatabase.getDatabaseInstance(context).getContentTableDao().getContent(contentTable.getParentId());
                    ContentTable contentTableSubj = AppDatabase.getDatabaseInstance(context).getContentTableDao().getContent(contentTableLearn.getParentId());
                    contentTable.setSubject(""+contentTableSubj.getNodeTitle());
                    courseEnrollments.get(i).setCourseDetail(contentTable);
                }
                else {
                    contentTable = new ContentTable();
                    contentTable.setSubject(courseEnrollments.get(i).getLanguage());
                    courseEnrollments.get(i).setCourseDetail(contentTable);
                }
            }
            viewCE.addContentToViewList(courseEnrollments);
            viewCE.notifyAdapter();
        }
        else {
            viewCE.showNoData();
        }
    }

    public List<Model_CourseEnrollment> enrolledCoursesFromDb(String week, String groupId) {
        coursesPerWeek.remove(week);
        List<Model_CourseEnrollment> courseEnrollments = AppDatabase.getDatabaseInstance(context).getCourseDao().
                fetchEnrolledCourses(groupId, week, FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI));
        if (courseEnrollments == null) return null;
        List<Model_CourseEnrollment> temp = new ArrayList<>();
        try {
            for (Model_CourseEnrollment ce : courseEnrollments) {
                Model_CourseExperience courseExperience = new Gson().fromJson(ce.getCourseExperience(), Model_CourseExperience.class);
                if (!courseExperience.getStatus().equalsIgnoreCase(FC_Constants.FEEDBACK_GIVEN)) {
                    ce.setCourseDetail(AppDatabase.getDatabaseInstance(context).getContentTableDao().getContent(ce.getCourseId()));
    //                ce.setProgressCompleted(isCourseProgressCompleted(ce, week));
                    temp.add(ce);
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        if (temp.size() > 0)
            coursesPerWeek.put(week, temp);
        return temp;
    }

}