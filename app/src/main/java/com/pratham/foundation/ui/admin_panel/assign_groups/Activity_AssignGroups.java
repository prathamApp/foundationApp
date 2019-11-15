package com.pratham.foundation.ui.admin_panel.assign_groups;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.SansCheckBox;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.Village;
import com.pratham.foundation.ui.admin_panel.AdminControlsActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;


@EActivity(R.layout.activity_assign_groups)
public class Activity_AssignGroups extends BaseActivity implements AssignGroupsContract.AssignGroupsView {

    @Bean(AssignGroupsPresenter.class)
    AssignGroupsContract.AssignGroupsPresenter presenter;

    @ViewById(R.id.spinner_SelectState)
    Spinner spinner_SelectState;
    @ViewById(R.id.spinner_SelectBlock)
    Spinner spinner_SelectBlock;
    @ViewById(R.id.spinner_selectVillage)
    Spinner spinner_selectVillage;
    @ViewById(R.id.assignGroup1)
    LinearLayout assignGroup1;
    @ViewById(R.id.assignGroup2)
    LinearLayout assignGroup2;
    @ViewById(R.id.LinearLayoutGroups)
    LinearLayout LinearLayoutGroups;
    @ViewById(R.id.allocateGroups)
    Button allocateGroups;

    Boolean isAssigned = false;

    List<String> blocksList;
    List<Village> blocksVillagesList;
    private List<Groups> dbgroupList;
    private int vilID, cnt = 0;
    public String[] checkBoxIds;
    public String group1 = "0";
    public String group2 = "0";
    public String group3 = "0";
    public String group4 = "0";
    public String group5 = "0";
    private ProgressDialog progress;

    @AfterViews
    public void initialize() {
        presenter.setView(Activity_AssignGroups.this);
        presenter.initializeStatesSpinner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getProgramWiseSpinners();
    }

    String programID;

    @UiThread
    @Override
    public void showProgramwiseSpinners(String programID) {

        this.programID = programID;

        if (programID.equals("1") || programID.equals("3") || programID.equals("10")) {
            spinner_selectVillage.setVisibility(View.VISIBLE);
        } else if (programID.equals("2")) // RI
        {
            spinner_selectVillage.setVisibility(View.GONE);
        } else {
            spinner_selectVillage.setVisibility(View.VISIBLE);
        }

    }

    @UiThread
    @Override
    public void showStateSpinner(List<String> statesList) {

        ArrayAdapter<String> StateAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, statesList);
        spinner_SelectState.setPrompt("Select State");
        spinner_SelectState.setAdapter(StateAdapter);

        spinner_SelectState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = spinner_SelectState.getSelectedItem().toString();
                presenter.getBlockData(selectedState);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @UiThread
    @Override
    public void populateBlock(List<String> blocksList) {
        this.blocksList = blocksList;
        ArrayAdapter<String> BlockAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, blocksList);
        spinner_SelectBlock.setPrompt("Select Block");
        spinner_SelectBlock.setAdapter(BlockAdapter);
        spinner_SelectBlock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBlock = spinner_SelectBlock.getSelectedItem().toString();
                if (programID.equals("1") || programID.equals("3") || programID.equals("10"))
                    presenter.fetchVillageData(selectedBlock);
                else if (programID.equals("2"))
                    presenter.fetchRIVillage(selectedBlock);
                else
                    presenter.fetchVillageData(selectedBlock);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @UiThread
    @Override
    public void populateVillage(List<Village> blocksVillagesList) {

        this.blocksVillagesList = blocksVillagesList;
        ArrayAdapter<Village> VillagesAdapter = new ArrayAdapter<Village>(this, R.layout.custom_spinner, blocksVillagesList);
        spinner_selectVillage.setPrompt("Select Village");
        spinner_selectVillage.setAdapter(VillagesAdapter);
        spinner_selectVillage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Village village = (Village) parent.getItemAtPosition(position);
                vilID = village.getVillageId();
                try {
                    presenter.getAllGroups(vilID);//Populate groups According to JSON & DB in Checklist instead of using spinner
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void populateRIVillage(int vilID) {
        this.vilID = vilID;
    }

    @UiThread
    @Override
    public void populateGroups(List<Groups> dbgroupList) {
        checkBoxIds = null;
        this.dbgroupList = dbgroupList;

        assignGroup1.removeAllViews();
        assignGroup2.removeAllViews();

        checkBoxIds = new String[dbgroupList.size()];
        int half = Math.round(dbgroupList.size() / 2);

        for (int i = 0; i < dbgroupList.size(); i++) {
            Groups grp = dbgroupList.get(i);
            String groupName = grp.getGroupName();
            String groupId = grp.getGroupId();
            TableRow row = new TableRow(Activity_AssignGroups.this);
            checkBoxIds[i] = groupId;
            //dynamically create checkboxes. i.e no. of students in group = no. of checkboxes
            row.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            SansCheckBox checkBox = new SansCheckBox(Activity_AssignGroups.this);
            try {
                checkBox.setId(i);
                checkBox.setTag(groupId);
                checkBox.setText(groupName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            checkBox.setTextSize(20);
            checkBox.setTextColor(Color.BLACK);
            row.addView(checkBox);
            if (i >= half)
                assignGroup2.addView(row);
            else
                assignGroup1.addView(row);
        }

        // Animation Effect on Groups populate
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);
        LinearLayoutGroups.startAnimation(animation1);
        allocateGroups.setVisibility(View.VISIBLE);
    }

    // Assign Groups
    @Click(R.id.allocateGroups)
    public void assignButtonClick() {
        try {
            group1 = group2 = group3 = group4 = group5 = "0";
            cnt = 0;
            for (int i = 0; i < checkBoxIds.length; i++) {
                CheckBox checkBox = findViewById(i);
                if (checkBox.isChecked() && group1.equals("0")) {
                    group1 = (String) checkBox.getTag();
                    cnt++;
                } else if (checkBox.isChecked() && group2.equals("0")) {
                    cnt++;
                    group2 = (String) checkBox.getTag();
                } else if (checkBox.isChecked() && group3.equals("0")) {
                    cnt++;
                    group3 = (String) checkBox.getTag();
                } else if (checkBox.isChecked() && group4.equals("0")) {
                    cnt++;
                    group4 = (String) checkBox.getTag();
                } else if (checkBox.isChecked() && group5.equals("0")) {
                    cnt++;
                    group5 = (String) checkBox.getTag();
                } else if (checkBox.isChecked()) {
                    cnt++;
                }

            }

            if (cnt < 1) {
                Toast.makeText(Activity_AssignGroups.this, "Please Select atleast one Group !!!", Toast.LENGTH_SHORT).show();
            } else if (cnt >= 1 && cnt <= 5) {
                try {
                    showProgressDialog();
                    presenter.updateDBData(group1, group2, group3, group4, group5, vilID);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(Activity_AssignGroups.this, " You can select Maximum 5 Groups !!! ", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void showProgressDialog() {
        progress = new ProgressDialog(Activity_AssignGroups.this);
        progress.setMessage("Please Wait...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    @UiThread
    public void dismissProgressDialog() {
        if (progress != null)
            progress.dismiss();
    }

    @UiThread
    @Override
    public void groupAssignSuccess() {
        isAssigned = true;
        dismissProgressDialog();
        Toast.makeText(Activity_AssignGroups.this, " Groups Assigned Successfully !!!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Activity_AssignGroups.this, AdminControlsActivity_.class));
        onBackPressed();
    }

    @Click(R.id.btn_back)
    public void pressedBackButton(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (isAssigned) {
            Intent i = new Intent();
            setResult(Activity.RESULT_OK, i);
            finish();
        } else {
            super.onBackPressed();
            finish();
        }
    }
}
