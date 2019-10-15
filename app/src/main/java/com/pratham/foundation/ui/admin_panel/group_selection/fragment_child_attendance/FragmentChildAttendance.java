package com.pratham.foundation.ui.admin_panel.group_selection.fragment_child_attendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.dao.StatusDao;

import com.pratham.foundation.database.domain.Attendance;
import com.pratham.foundation.database.domain.Session;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.ui.selectSubject.SelectSubject_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;
import java.util.ArrayList;
import java.util.UUID;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.foundation.utility.FC_Utility.dpToPx;

public class FragmentChildAttendance extends Fragment implements ContractChildAttendance.attendanceView {

    @BindView(R.id.rv_child)
    RecyclerView rv_child;
    @BindView(R.id.btn_attendance_next)
    Button btn_attendance_next;
    @BindView(R.id.main_layout)
    RelativeLayout main_layout;

    ChildAdapter childAdapter;
    ArrayList<Student> students;
    ArrayList<Integer> avatarsMale;
    ArrayList<Integer> avatarsFemale;
    private String groupID = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_child_attendance, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        students = getArguments().getParcelableArrayList(FC_Constants.STUDENT_LIST);
        avatarsFemale = new ArrayList<>();
        avatarsMale = new ArrayList<>();
        if (ApplicationClass.isTablet) {
            btn_attendance_next.setVisibility(View.VISIBLE);
            //add_child.setVisibility(View.GONE);
            groupID = getArguments().getString(FC_Constants.GROUPID);
            FC_Constants.GROUP_LOGIN = true;
            FC_Constants.currentGroup = ""+groupID;
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
        setChilds(students);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setChilds(ArrayList<Student> childs) {
        if (childAdapter == null) {
            childAdapter = new ChildAdapter(getActivity(), childs, avatarsFemale, avatarsMale, FragmentChildAttendance.this);
            rv_child.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            rv_child.setLayoutManager(mLayoutManager);
            rv_child.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(getActivity(),15), true));
            rv_child.setItemAnimator(new DefaultItemAnimator());
            // rv_child.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            rv_child.setAdapter(childAdapter);
        } else {
            childAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void childItemClicked(Student student, int position) {
        Log.d("ooo", "" + position);
        for (Student stu : students) {
            if (stu.getStudentID().equalsIgnoreCase(student.getStudentID())) {
                if (stu.isChecked()) {
                    stu.setChecked(false);
                } else {
                    stu.setChecked(true);
                }
                break;
            }
        }
        childAdapter.notifyDataSetChanged();
        // setChilds(students);
    }

/*    @Override
    public void moveToDashboardOnChildClick(Student student, int position, View v) {
        ApplicationClass.bubble_mp.start();
        FastSave.getInstance().saveString(FC_Constants.AVATAR, student.getAvatarName());
        ArrayList<Student> s = new ArrayList<>();
        s.add(student);
        markAttendance(s);
    }*/

    @OnClick(R.id.btn_attendance_next)
    public void setNext(View v) {
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
            startActivity(new Intent(getActivity(), SelectSubject_.class));
            getActivity().finish();
        } else {
            Toast.makeText(getContext(), "Please Select Students !", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("StaticFieldLeak")
    private void startSession(final ArrayList<Student> stud) {
        new AsyncTask<Object, Void, Object>() {
            String currentSession;

            @Override
            protected Object doInBackground(Object[] objects) {
                try {

                    StatusDao statusDao = AppDatabase.getDatabaseInstance(getContext()).getStatusDao();
                    currentSession = "" + UUID.randomUUID().toString();
                    FC_Constants.currentSession = currentSession;
                    statusDao.updateValue("CurrentSession", "" + currentSession);

                    Session startSesion = new Session();
                    startSesion.setSessionID("" + currentSession);
                    String timerTime = FC_Utility.getCurrentDateTime();

                    Log.d("doInBackground", "--------------------------------------------doInBackground : " + timerTime);
                    startSesion.setFromDate(timerTime);
                    startSesion.setToDate("NA");
                    startSesion.setSentFlag(0);
                    AppDatabase.getDatabaseInstance(getContext()).getSessionDao().insert(startSesion);

                    Log.d("ChildAttendence","Student Count: " + stud.size());

                    Attendance attendance = new Attendance();
                    for (int i = 0; i < stud.size(); i++) {
                        attendance.setSessionID("" + currentSession);
                        attendance.setStudentID("" + stud.get(i).getStudentID());
                        attendance.setDate(FC_Utility.getCurrentDateTime());
                        attendance.setGroupID(groupID);
                        attendance.setSentFlag(0);
                        AppDatabase.getDatabaseInstance(getContext()).getAttendanceDao().insert(attendance);
                        Log.d("ChildAttendence", "currentSession : " + currentSession + "  StudentId: " + stud.get(i).getStudentID());
                    }

                    FC_Constants.currentStudentID = groupID;
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

            FC_Constants.currentStudentID = groupID;
        }
        AppDatabase.getDatabaseInstance(getActivity()).getAttendanceDao().insertAll(attendances);
        Session s = new Session();
        s.setSessionID(FastSave.getInstance().getString(FC_Constants.SESSIONID, ""));
        s.setFromDate(FC_Utility.getCurrentDateTime());
        s.setToDate("NA");
        AppDatabase.getDatabaseInstance(getActivity()).getSessionDao().insert(s);
    }

}
