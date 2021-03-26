package com.pratham.foundation.ui.app_home.profile_new.course_enrollment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.modalclasses.Model_CourseEnrollment;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.utility.FC_Constants.GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.INDIVIDUAL_MODE;
import static com.pratham.foundation.utility.FC_Constants.QR_GROUP_MODE;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;

@EActivity(R.layout.activity_course_enrollment)
public class CourseEnrollmentActivity extends BaseActivity implements
        CourseEnrollmentContract.CourseEnrollmentView {

    @Bean(CourseEnrollmentPresenter.class)
    CourseEnrollmentContract.CourseEnrollmentPresenter presenter;

    @ViewById(R.id.board_spinner)
    Spinner board_spinner;
    @ViewById(R.id.level_spinner)
    Spinner level_spinner;
    @ViewById(R.id.learning_spinner)
    Spinner learning_spinner;
    @ViewById(R.id.subject_spinner)
    Spinner subject_spinner;
    @ViewById(R.id.lang_spinner)
    Spinner lang_spinner;
    @ViewById(R.id.rl_calendar_view)
    RelativeLayout rl_calendar_view;
    @ViewById(R.id.rl_no_data)
    RelativeLayout rl_no_data;
    @ViewById(R.id.spinner_rl)
    RelativeLayout spinner_rl;
    @ViewById(R.id.iv_close_calendar)
    ImageView iv_close_calendar;
    @ViewById(R.id.date_btn)
    Button date_btn;
    @ViewById(R.id.tv_Topic)
    TextView tv_Topic;
    @ViewById(R.id.course_recycler_view)
    RecyclerView my_recycler_view;

    private List<ContentTable> boardList, langList, subjList, tabList, levelList;
    int levelPos = 0;
    String selectedBoardId, selectedLangId, selectedSubjectId, selectedTabId, selectedLevelId,selectedLangName;
    ContentTable selectedCourse;
    List<Model_CourseEnrollment> courseEnrolled;

    @AfterViews
    public void initialize() {
        presenter.setView(CourseEnrollmentActivity.this);
        date_btn.setVisibility(View.GONE);
        courseEnrolled = new ArrayList<>();
        setProfileName();
        showLoader();
        presenter.getEnrolledCourses();
    }

    @Override
    public void showNoData() {
        dismissLoadingDialog();
        rl_no_data.setVisibility(View.VISIBLE);
        my_recycler_view.setVisibility(View.GONE);
    }

    private boolean loaderVisible = false;
    private CustomLodingDialog myLoadingDialog;
    @UiThread
    public void showLoader() {
        if (!loaderVisible) {
            loaderVisible = true;
            myLoadingDialog = new CustomLodingDialog(this);
            myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(myLoadingDialog.getWindow()).
                    setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myLoadingDialog.setContentView(R.layout.loading_dialog);
            myLoadingDialog.setCanceledOnTouchOutside(false);
//        myLoadingDialog.setCancelable(false);
            myLoadingDialog.show();
        }
    }

    private boolean desFlag = false;

    @Override
    public void onDestroy() {
        desFlag = true;
        super.onDestroy();
    }

    @UiThread
    public void dismissLoadingDialog() {
        try {
            if (!desFlag) {
                loaderVisible = false;
                new Handler().postDelayed(() -> {
                    if (myLoadingDialog != null && myLoadingDialog.isShowing())
                        myLoadingDialog.dismiss();
                }, 150);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @UiThread
    public void setProfileName() {
        try {
            String profileName = "";
            if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(GROUP_MODE))
                profileName = AppDatabase.getDatabaseInstance(this).getGroupsDao().getGroupNameByGrpID(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            else if (!FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(QR_GROUP_MODE)) {
                profileName = AppDatabase.getDatabaseInstance(this).getStudentDao().getFullName(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            }

            if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(INDIVIDUAL_MODE))
                profileName = profileName.split(" ")[0];

            tv_Topic.setText(profileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void noCource() {
        Toast.makeText(this, "No course found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addContentToViewList(List<Model_CourseEnrollment> courseEnrolleda) {
        courseEnrolled.clear();
        courseEnrolled.addAll(courseEnrolleda);
    }

    private EnrolledCoursesAdapter enrolledCoursesAdapter;

    @Override
    public void notifyAdapter() {
//        private String parseDate(String date) {
//            String[] date_split = date.split(" ");
//            return date_split[1] + " " + date_split[2] + " " + date_split[3] + "," + date_split[6];
//        }
        dismissLoadingDialog();
        my_recycler_view.setVisibility(View.VISIBLE);
        rl_no_data.setVisibility(View.GONE);
        if (enrolledCoursesAdapter == null) {
            try {
                enrolledCoursesAdapter = new EnrolledCoursesAdapter(this, courseEnrolled);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
                my_recycler_view.setLayoutManager(mLayoutManager);
                my_recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(this), true));
                my_recycler_view.setItemAnimator(new DefaultItemAnimator());
                my_recycler_view.setAdapter(enrolledCoursesAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            enrolledCoursesAdapter.notifyDataSetChanged();
    }

    @UiThread
    @Override
    public void setBoardList(List<ContentTable> boardList) {
        try {
            this.boardList = boardList;
            List<String> prgrms = new ArrayList<>();
            for (ContentTable mp : boardList) {
                prgrms.add(mp.getNodeTitle());
            }
            closeProgressDialog();
            ArrayAdapter arrayStateAdapter = new ArrayAdapter(this, R.layout.custom_spinner, prgrms);
            arrayStateAdapter.setDropDownViewResource(R.layout.custom_spinner);
            board_spinner.setAdapter(arrayStateAdapter);
            board_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    disableSaveButton();
//                    if (position <= 0) {
//                        presenter.clearLists();
//                    } else {
                    selectedBoardId = boardList.get(position).getNodeId();
                    presenter.loadLanguages(selectedBoardId);
//                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void setLangList(List<ContentTable> langList) {
        try {
            this.langList = langList;
            List<String> prgrms = new ArrayList<>();
            for (ContentTable mp : langList) {
                prgrms.add(mp.getNodeTitle());
            }
            closeProgressDialog();
            ArrayAdapter arrayStateAdapter = new ArrayAdapter(this, R.layout.custom_spinner, prgrms);
            arrayStateAdapter.setDropDownViewResource(R.layout.custom_spinner);
            lang_spinner.setAdapter(arrayStateAdapter);
            lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    disableSaveButton();
//                    if (position <= 0) {
//                        presenter.clearLists();
//                    } else {
                    selectedLangName = langList.get(position).getNodeTitle();
                    selectedLangId = langList.get(position).getNodeId();
                    presenter.loadSubjects(selectedLangId,selectedLangName);
//                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void setSubjList(List<ContentTable> subjList) {
        try {
            this.subjList = subjList;
            List<String> prgrms = new ArrayList<>();
            for (ContentTable mp : subjList) {
                prgrms.add(mp.getNodeTitle());
            }
            closeProgressDialog();
            ArrayAdapter arrayStateAdapter = new ArrayAdapter(this, R.layout.custom_spinner, prgrms);
            arrayStateAdapter.setDropDownViewResource(R.layout.custom_spinner);
            subject_spinner.setAdapter(arrayStateAdapter);
            subject_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    disableSaveButton();
//                    if (position <= 0) {
//                        presenter.clearLists();
//                    } else {
                    selectedSubjectId = subjList.get(position).getNodeId();
                    presenter.loadTabs(selectedSubjectId);
//                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void setTabList(List<ContentTable> tabList) {
        try {
            this.tabList = tabList;
            List<String> prgrms = new ArrayList<>();
            for (ContentTable mp : tabList) {
                prgrms.add(mp.getNodeTitle());
            }
            closeProgressDialog();
            ArrayAdapter arrayStateAdapter = new ArrayAdapter(this, R.layout.custom_spinner, prgrms);
            arrayStateAdapter.setDropDownViewResource(R.layout.custom_spinner);
            learning_spinner.setAdapter(arrayStateAdapter);
            learning_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    disableSaveButton();
//                    if (position <= 0) {
//                        presenter.clearLists();
//                    } else {
                    selectedTabId = tabList.get(position).getNodeId();
                    presenter.loadLevels(selectedTabId);
//                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean courseSelected = false;
    @UiThread
    @Override
    public void setLevelList(List<ContentTable> levelList) {
        try {
            this.levelList = levelList;
            List<String> prgrms = new ArrayList<>();
            for (ContentTable mp : levelList) {
                prgrms.add(mp.getNodeTitle());
            }
            closeProgressDialog();
            ArrayAdapter arrayStateAdapter = new ArrayAdapter(this, R.layout.custom_spinner, prgrms);
            arrayStateAdapter.setDropDownViewResource(R.layout.custom_spinner);
            level_spinner.setAdapter(arrayStateAdapter);
            level_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    levelPos = position;
                    selectedLevelId = levelList.get(position).getNodeId();
                    selectedCourse = levelList.get(position);
                    date_btn.setVisibility(View.VISIBLE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void courseError() {
        Toast.makeText(this, "Error Enrolling Course ", Toast.LENGTH_SHORT).show();
    }

    private Calendar startDate;
    private Calendar endDate;

    @Click(R.id.date_btn)
    public void dateBtnClicked() {
        spinner_rl.setVisibility(View.GONE);
        date_btn.setVisibility(View.GONE);
        rl_calendar_view.setVisibility(View.VISIBLE);
        initializeCalendar();
    }

    @ViewById(R.id.rl_add_course)
    RelativeLayout rl_add_course;
    @ViewById(R.id.list_display)
    RelativeLayout list_display;

    @Click(R.id.add_course_btn)
    public void onAddNewCourse() {
        rl_add_course.setVisibility(View.VISIBLE);
        list_display.setVisibility(View.GONE);
        presenter.getRootData();
    }

    @Click(R.id.main_back2)
    public void onMain_back2() {
        rl_add_course.setVisibility(View.GONE);
        list_display.setVisibility(View.VISIBLE);
        presenter.getEnrolledCourses();
//        reload();
//        presenter.getRootData();
    }

    @Click(R.id.main_back)
    public void onMain_back() {
        finish();
    }

    @Click(R.id.btn_course_time_select)
    public void onCourseTimeSelected() {
        if (endDate == null || startDate == null) {
            Toast.makeText(this, "Please select the correct timeline.", Toast.LENGTH_SHORT).show();
        } else {
            presenter.addCourseToDb("WEEK_1", selectedCourse, startDate, endDate);
        }
    }

    @UiThread
    @Override
    public void courseAdded() {
        spinner_rl.setVisibility(View.VISIBLE);
        date_btn.setVisibility(View.VISIBLE);
        rl_calendar_view.setVisibility(View.GONE);
        Toast.makeText(this, "Course Added", Toast.LENGTH_SHORT).show();
    }

    @UiThread
    @Override
    public void courseAlreadySelected() {
        spinner_rl.setVisibility(View.VISIBLE);
        date_btn.setVisibility(View.VISIBLE);
        rl_calendar_view.setVisibility(View.GONE);
        Toast.makeText(this, "Course Already Selected", Toast.LENGTH_SHORT).show();
    }

    @UiThread
    @Click(R.id.iv_close_calendar)
    public void closeCalender() {
        spinner_rl.setVisibility(View.VISIBLE);
        date_btn.setVisibility(View.VISIBLE);
        rl_calendar_view.setVisibility(View.GONE);
    }

    @ViewById(R.id.course_date_picker)
    DateRangeCalendarView course_date_picker;

    private void initializeCalendar() {
        Calendar startSelectionDate = Calendar.getInstance();
        startSelectionDate.add(Calendar.MONTH, -1);
        Calendar endSelectionDate = (Calendar) startSelectionDate.clone();
        endSelectionDate.add(Calendar.DATE, 4);
        course_date_picker.setSelectedDateRange(startSelectionDate, endSelectionDate);
        course_date_picker.setCalendarListener(new DateRangeCalendarView.CalendarListener() {
            @Override
            public void onFirstDateSelected(Calendar startDate) {
            }

            @Override
            public void onDateRangeSelected(Calendar s_Date, Calendar e_Date) {
                Log.d("onDate", "onDateRangeSelected: " + s_Date + "   " + e_Date);
                startDate = s_Date;
                endDate = e_Date;
            }
        });
    }

    private void closeProgressDialog() {
    }
}