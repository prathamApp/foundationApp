package com.pratham.foundation.ui.group_selection.fragment_child_attendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.dao.StatusDao;
import com.pratham.foundation.database.domain.Attendance;
import com.pratham.foundation.database.domain.Session;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.selectSubject.SelectSubject_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.utility.FC_Constants.GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.StudentPhotoPath;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;

@EFragment(R.layout.fragment_child_attendance)
public class FragmentChildAttendance extends Fragment implements ContractChildAttendance.attendanceView {

    @ViewById(R.id.rv_child)
    RecyclerView rv_child;
    @ViewById(R.id.btn_attendance_next)
    Button btn_attendance_next;
    @ViewById(R.id.main_layout)
    RelativeLayout main_layout;
    ChildAdapter childAdapter;
    ArrayList<Student> students;
    ArrayList<Integer> avatarsMale;
    ArrayList<Integer> avatarsFemale;
    private String groupID = "";
    private Uri capturedImageUri;
    String imageName = "NA";
    private static final int CAMERA_REQUEST = 1;

    @AfterViews
    public void onFragCreate() {
        students = Objects.requireNonNull(getArguments()).getParcelableArrayList(FC_Constants.STUDENT_LIST);
        avatarsFemale = new ArrayList<>();
        avatarsMale = new ArrayList<>();
        if (ApplicationClass.getAppMode()) {
            btn_attendance_next.setVisibility(View.VISIBLE);
            //add_child.setVisibility(View.GONE);
            groupID = getArguments().getString(FC_Constants.GROUPID);
            FC_Constants.GROUP_LOGIN = true;
            FC_Constants.currentGroup = "" + groupID;
            if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(GROUP_MODE))
                FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_ID, "" + groupID);
            for (Student stu : students) {
                avatarsMale.add(FC_Utility.getRandomMaleAvatar(getActivity()));
                avatarsFemale.add(FC_Utility.getRandomFemaleAvatar(getActivity()));
            }
        } else {
            btn_attendance_next.setVisibility(View.GONE);
            // add_child.setVisibility(View.VISIBLE);
            groupID = "SmartPhone";
            /*for (Student stu : students)
                avatars.add(stu.getAvatarName());*/
        }
        setChilds();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setChilds() {
        if (childAdapter == null) {
            childAdapter = new ChildAdapter(getActivity(), students, avatarsFemale, avatarsMale, FragmentChildAttendance.this);
            rv_child.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            rv_child.setLayoutManager(mLayoutManager);
            rv_child.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(getActivity(), 15), true));
            rv_child.setItemAnimator(new DefaultItemAnimator());
            // rv_child.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            rv_child.setAdapter(childAdapter);
        } else {
            childAdapter.notifyDataSetChanged();
        }
    }

    int childItemPos;

    @Override
    public void clickPhoto(String studentID, int pos) {
        childItemPos = pos;
        imageName = studentID + ".jpg";
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(StudentPhotoPath);
        if (!imagesFolder.exists()) imagesFolder.mkdirs();
        File image = new File(imagesFolder, imageName);
        capturedImageUri = Uri.fromFile(image);
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, capturedImageUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("codes", String.valueOf(requestCode) + resultCode);
        try {
            if (requestCode == CAMERA_REQUEST) {
                if (resultCode == RESULT_OK) {
                    childAdapter.notifyItemChanged(childItemPos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void childItemClicked(Student student, int position) {
        Log.d("ooo", "" + position);
        if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(GROUP_MODE)) {
            if (student.isChecked())
                students.get(position).setChecked(false);
            else
                students.get(position).setChecked(true);
            childAdapter.notifyItemChanged(position);
        } else {
            for (int i = 0; i < students.size(); i++) {
                students.get(i).setChecked(false);
            }
            students.get(position).setChecked(true);
            childAdapter.notifyDataSetChanged();
        }
        // setChilds(students);
    }

    @Click(R.id.btn_attendance_next)
    public void setNext(View v) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        ArrayList<Student> checkedStds = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).isChecked())
                checkedStds.add(students.get(i));
        }
        if (checkedStds.size() > 0) {
            //todo remove comment
            //   ApplicationClass.bubble_mp.start();
            startSession(checkedStds);
            //todo remove#
            //startActivity(new Intent(getActivity(), HomeActivity_.class));
//            startActivity(new Intent(getActivity(), SelectSubject_.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            startActivity(new Intent(getActivity(), SelectSubject_.class));
            getActivity().finish();
        } else {
            Toast.makeText(getContext(), "Please Select Students !", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void startSession(final ArrayList<Student> stud) {
        new AsyncTask<Object, Void, Object>() {
            String newCurrentSession;

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    StatusDao statusDao = AppDatabase.getDatabaseInstance(getContext()).getStatusDao();
                    newCurrentSession = "" + UUID.randomUUID().toString();
                    String currentSession = newCurrentSession;
                    FastSave.getInstance().saveString(FC_Constants.CURRENT_SESSION, currentSession);
                    statusDao.updateValue("CurrentSession", "" + currentSession);

                    Session startSesion = new Session();
                    startSesion.setSessionID("" + currentSession);
                    String timerTime = FC_Utility.getCurrentDateTime();
                    Log.d("doInBackground", "--------------------------------------------doInBackground : " + timerTime);
                    startSesion.setFromDate(timerTime);
                    startSesion.setToDate("NA");
                    startSesion.setSentFlag(0);
                    AppDatabase.getDatabaseInstance(getContext()).getSessionDao().insert(startSesion);
                    Log.d("ChildAttendence", "Student Count: " + stud.size());

                    Attendance attendance = new Attendance();
                    for (int i = 0; i < stud.size(); i++) {
                        FastSave.getInstance().saveString(FC_Constants.CURRENT_API_STUDENT_ID, "" + stud.get(i).getStudentID());
                        attendance.setSessionID("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
                        attendance.setStudentID("" + stud.get(i).getStudentID());
                        attendance.setDate(FC_Utility.getCurrentDateTime());
                        attendance.setGroupID(groupID);
                        attendance.setSentFlag(0);
                        AppDatabase.getDatabaseInstance(getContext()).getAttendanceDao().insert(attendance);
                        Log.d("ChildAttendence", "currentSession : " + FastSave.getInstance().getString(
                                FC_Constants.CURRENT_SESSION, "") + "  StudentId: " + stud.get(i).getStudentID());
                    }

                    String currentStudentID = "";
                    if (FastSave.getInstance().getString(FC_Constants.LOGIN_MODE, FC_Constants.GROUP_MODE).equalsIgnoreCase(GROUP_MODE))
                        currentStudentID = groupID;
                    else {
                        currentStudentID = stud.get(0).getStudentID();
                        String currentStudName = stud.get(0).getFullName();
                        FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_NAME, currentStudName);
                    }
                    FastSave.getInstance().saveString(FC_Constants.CURRENT_API_STUDENT_ID, "" + stud.get(0).getStudentID());
                    FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_ID, currentStudentID);
                    BackupDatabase.backup(getContext());
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }.execute();
    }

    public void markAttendance(ArrayList<Student> stud) {
        FastSave.getInstance().saveString(FC_Constants.SESSIONID, FC_Utility.getUUID().toString());
        ArrayList<Attendance> attendances = new ArrayList<>();
        for (Student stu : stud) {
            Attendance attendance = new Attendance();
            attendance.setSessionID(FastSave.getInstance().getString(FC_Constants.SESSIONID, ""));
            attendance.setStudentID(stu.getStudentID());
            attendance.setDate(FC_Utility.getCurrentDateTime());
            attendance.GroupID = groupID;
            attendance.setSentFlag(0);
            FastSave.getInstance().saveString(FC_Constants.GROUPID, groupID);
            attendances.add(attendance);
            FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_ID, groupID);
        }
        AppDatabase.getDatabaseInstance(getActivity()).getAttendanceDao().insertAll(attendances);
        Session s = new Session();
        s.setSessionID(FastSave.getInstance().getString(FC_Constants.SESSIONID, ""));
        s.setFromDate(FC_Utility.getCurrentDateTime());
        s.setToDate("NA");
        AppDatabase.getDatabaseInstance(getActivity()).getSessionDao().insert(s);
    }

    @Click(R.id.btn_back)
    public void pressedBackButton() {
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

}
