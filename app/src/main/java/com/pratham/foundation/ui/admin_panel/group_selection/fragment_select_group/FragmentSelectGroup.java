package com.pratham.foundation.ui.admin_panel.group_selection.fragment_select_group;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.admin_panel.group_selection.fragment_child_attendance.FragmentChildAttendance;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;


@EFragment(R.layout.fragment_select_group)
public class FragmentSelectGroup extends Fragment implements ContractGroup {

    @ViewById(R.id.rv_group)
    RecyclerView rv_group;
    @ViewById(R.id.main_layout)
    RelativeLayout main_layout;

    GroupAdapter groupAdapter;
    ArrayList<Groups> groups;
    Groups groupSelected;
    Context context;

    @AfterViews
    public void initialize() {
        context = getActivity();
        showGrps();
    }

    private void showGrps() {
        try {
            ArrayList<String> present_groups = new ArrayList<>();
            String groupId1 = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue(FC_Constants.GROUPID1);

            if (!groupId1.equalsIgnoreCase("0")) present_groups.add(groupId1);
            String groupId2 = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue(FC_Constants.GROUPID2);

            if (!groupId2.equalsIgnoreCase("0")) present_groups.add(groupId2);
            String groupId3 = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue(FC_Constants.GROUPID3);

            if (!groupId3.equalsIgnoreCase("0")) present_groups.add(groupId3);
            String groupId4 = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue(FC_Constants.GROUPID4);

            if (!groupId4.equalsIgnoreCase("0")) present_groups.add(groupId4);
            String groupId5 = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue(FC_Constants.GROUPID5);

            if (!groupId5.equalsIgnoreCase("0")) present_groups.add(groupId5);

            if (getArguments().getBoolean(FC_Constants.GROUP_AGE_BELOW_7)) {
                get3to6Groups(present_groups);
            } else {
                get8to14Groups(present_groups);
            }
            setGroups(groups);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void get3to6Groups(ArrayList<String> allGroups) {
        groups = new ArrayList<>();
        for (String grID : allGroups) {
            // ArrayList<Student> students = (ArrayList<Student>) BaseActivity.studentDao.getGroupwiseStudents(grID);
            ArrayList<Student> students = (ArrayList<Student>) AppDatabase.getDatabaseInstance(context).getStudentDao().getGroupwiseStudents(grID);
            for (Student stu : students) {
                if (stu.getAge() < 7) {
                    //Groups group = BaseActivity.groupDao.getGroupByGrpID(grID);
                    Groups group = AppDatabase.getDatabaseInstance(context).getGroupsDao().getGroupByGrpID(grID);
                    groups.add(group);
                    break;
                }
            }
        }
    }

    private void get8to14Groups(ArrayList<String> allGroups) {
        groups = new ArrayList<>();
        for (String grID : allGroups) {
            //ArrayList<Student> students = (ArrayList<Student>) BaseActivity.studentDao.getGroupwiseStudents(grID);
            ArrayList<Student> students = (ArrayList<Student>) AppDatabase.getDatabaseInstance(context).getStudentDao().getGroupwiseStudents(grID);

            for (Student stu : students) {
                if (stu.getAge() >= 7) {
                    // group = BaseActivity.groupDao.getGroupByGrpID(grID);
                    Groups group = AppDatabase.getDatabaseInstance(context).getGroupsDao().getGroupByGrpID(grID);
                    groups.add(group);
                    break;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ArrayList<String> present_groups = new ArrayList<>();
            String groupId1 = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue(FC_Constants.GROUPID1);

            if (!groupId1.equalsIgnoreCase("0")) present_groups.add(groupId1);
            String groupId2 = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue(FC_Constants.GROUPID2);

            if (!groupId2.equalsIgnoreCase("0")) present_groups.add(groupId2);
            String groupId3 = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue(FC_Constants.GROUPID3);

            if (!groupId3.equalsIgnoreCase("0")) present_groups.add(groupId3);
            String groupId4 = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue(FC_Constants.GROUPID4);

            if (!groupId4.equalsIgnoreCase("0")) present_groups.add(groupId4);
            String groupId5 = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue(FC_Constants.GROUPID5);

            if (!groupId5.equalsIgnoreCase("0")) present_groups.add(groupId5);

            if (getArguments().getBoolean(FC_Constants.GROUP_AGE_BELOW_7)) {
                get3to6Groups(present_groups);
            } else {
                get8to14Groups(present_groups);
            }
            setGroups(groups);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setGroups(ArrayList<Groups> groups) {
        if (groupAdapter == null) {
            groupAdapter = new GroupAdapter(getActivity(), groups, FragmentSelectGroup.this);
            rv_group.setHasFixedSize(true);
            // rv_group.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            rv_group.setLayoutManager(mLayoutManager);
            rv_group.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(getActivity(), 10), true));
            rv_group.setItemAnimator(new DefaultItemAnimator());
            //recyclerView.setAdapter(adapter);

            rv_group.setAdapter(groupAdapter);
        } else {
            groupAdapter.updateGroupItems(groups);
        }
    }

    @Click(R.id.btn_group_next)
    public void setNext(View v) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (groupSelected != null) {
            //todo remove comment
            //  ApplicationClass.bubble_mp.start();
            ArrayList<Student> students = new ArrayList<>();
            String currentStudName = groupSelected.GroupName;
            FastSave.getInstance().saveString(FC_Constants.CURRENT_STUDENT_NAME, currentStudName);
            students.addAll(AppDatabase.getDatabaseInstance(context).getStudentDao().getGroupwiseStudents(groupSelected.getGroupId()));
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(FC_Constants.STUDENT_LIST, students);
            bundle.putString(FC_Constants.GROUPID, groupSelected.getGroupId());
            FC_Utility.showFragment(getActivity(), new FragmentChildAttendance(), R.id.frame_group,
                    bundle, FragmentChildAttendance.class.getSimpleName());
        } else {
            Toast.makeText(getContext(), "Please select Group !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void groupItemClicked(Groups modalGroup, int position) {
        groupSelected = modalGroup;
        for (Groups gr : groups) {
            if (gr.getGroupId().equalsIgnoreCase(modalGroup.getGroupId())) {
                gr.setSelected(true);
            } else
                gr.setSelected(false);
        }
        setGroups(groups);
    }

    @Click(R.id.btn_back)
    public void pressedBackButton() {
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

}
