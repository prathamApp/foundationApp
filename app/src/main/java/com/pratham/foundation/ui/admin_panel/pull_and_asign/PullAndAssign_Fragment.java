package com.pratham.foundation.ui.admin_panel.pull_and_asign;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.fontsview.SansCheckBox;
import com.pratham.foundation.database.domain.Groups;
import com.pratham.foundation.database.domain.ModalProgram;
import com.pratham.foundation.database.domain.ModalStates;
import com.pratham.foundation.database.domain.Village;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@EFragment(R.layout.fragment_pull_and_assign)
public class PullAndAssign_Fragment extends Fragment implements PullAndAssign_Contract.PullAndAssignView,
        PullAndAssign_Contract.VillageSelectListener{

    @Bean(PullAndAssign_Presenter.class)
    PullAndAssign_Contract.PullAndAssignPresenter presenter;

    @ViewById(R.id.spinner_program)
    Spinner spinner_program;
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
    @ViewById(R.id.save_button)
    Button save_button;
    @ViewById(R.id.btn_back)
    ImageButton btn_back;

    ProgressDialog progressDialog;
    List<ModalProgram> prgrmList;
    List<Village> villageList;
    String selectedProgram;
    private int vilID, cnt = 0;
    private List<Groups> dbgroupList;
    public String[] checkBoxIds;
    public String group1 = "0";
    public String group2 = "0";
    public String group3 = "0";
    public String group4 = "0";
    public String group5 = "0";


    @AfterViews
    public void initialize() {
        presenter.setView(PullAndAssign_Fragment.this);
        showProgressDialog("Please wait");
        presenter.checkConnectivity();
    }

    @Click(R.id.btn_back)
    public void backBtnClicked(){
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    @Override
    public void showProgram(List<ModalProgram> prgrmList) {
        try {
            this.prgrmList = prgrmList;
            List<String> prgrms = new ArrayList<>();
            for (ModalProgram mp : prgrmList) {
                prgrms.add(mp.getProgramName());
            }
            closeProgressDialog();
            ArrayAdapter arrayStateAdapter = new ArrayAdapter(getActivity(), R.layout.custom_spinner, prgrms);
            arrayStateAdapter.setDropDownViewResource(R.layout.custom_spinner);
            spinner_program.setAdapter(arrayStateAdapter);
            spinner_program.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    disableSaveButton();
                    if (position <= 0) {
                        presenter.clearLists();
                    } else {
                        selectedProgram = prgrmList.get(position).getProgramId();
                        showProgressDialog("Please wait");
                        presenter.loadStateSpinner(selectedProgram);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showStatesSpinner(List<ModalStates> modalStates) {
        String[] statesArr = new String[modalStates.size()];
        for(int i=0;i<modalStates.size();i++)
            statesArr[i]=modalStates.get(i).getStateName();
        ArrayAdapter arrayStateAdapter = new ArrayAdapter(getActivity(), R.layout.custom_spinner, statesArr);
        arrayStateAdapter.setDropDownViewResource(R.layout.custom_spinner);
        spinner_SelectState.setAdapter(arrayStateAdapter);
        closeProgressDialog();
        spinner_SelectState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
//                disableSaveButton();
//                if (pos <= 0) {
//                    clearBlockSpinner();
//                } else {
                    showProgressDialog("Please wait");
                    presenter.loadBlockSpinner(pos, selectedProgram);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void clearBlockSpinner() {
        spinner_SelectBlock.setSelection(0);
        spinner_SelectBlock.setEnabled(false);
    }

    @Override
    public void showBlocksSpinner(List blocks) {
        spinner_SelectBlock.setEnabled(true);
        ArrayAdapter arrayBlockAdapter = new ArrayAdapter(getActivity(), R.layout.custom_spinner, blocks);
        arrayBlockAdapter.setDropDownViewResource(R.layout.custom_spinner);
        spinner_SelectBlock.setAdapter(arrayBlockAdapter);
        closeProgressDialog();
        spinner_SelectBlock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
//                disableSaveButton();
                if (pos > 0) {
                    //open Village Dialog
                    String block = adapterView.getSelectedItem().toString();
//                    showProgressDialog("Please wait");
                    presenter.proccessVillageData(block);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @UiThread
    @Override
    public void showVillageSpinner(List villageList) {
        this.villageList = villageList;
        ArrayAdapter VillagesAdapter = new ArrayAdapter(getActivity(), R.layout.custom_spinner, villageList);
        VillagesAdapter.setDropDownViewResource(R.layout.custom_spinner);
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

    // Assign Groups
    @Click(R.id.allocateGroups)
    public void assignButtonClick() {
        try {
            group1 = group2 = group3 = group4 = group5 = "0";
            cnt = 0;
            for (int i = 0; i < checkBoxIds.length; i++) {
                CheckBox checkBox = getActivity().findViewById(i);
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
                Toast.makeText(getActivity(), "Please Select atleast one Group !!!", Toast.LENGTH_SHORT).show();
            } else if (cnt >= 1 && cnt <= 5) {
                try {
                    showProgressDialog("Updating");
                    presenter.updateDBData(group1, group2, group3, group4, group5, vilID);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getActivity(), " You can select Maximum 5 Groups !!! ", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getSelectedItems(ArrayList<String> villageIDList) {
        presenter.downloadStudentAndGroup(villageIDList);
    }

    @UiThread
    @Override
    public void groupAssignSuccess() {
        closeProgressDialog();
        FastSave.getInstance().saveBoolean(FC_Constants.PRATHAM_LOGIN, true);
        Toast.makeText(getActivity(), " Groups Assigned Successfully !!!", Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }


    @UiThread
    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        progressDialog.setCancelable(false);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    @Override
    public void showVillageDialog(List villageList) {
        CustomLodingDialog villageDialog = new SelectNewVillageDialog(getActivity(), this, villageList);
        villageDialog.show();
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
            TableRow row = new TableRow(getActivity());
            checkBoxIds[i] = groupId;
            //dynamically create checkboxes. i.e no. of students in group = no. of checkboxes
            row.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            SansCheckBox checkBox = new SansCheckBox(getActivity());
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
        Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide);
        LinearLayoutGroups.startAnimation(animation1);
        allocateGroups.setVisibility(View.VISIBLE);
    }

    @UiThread
    @Override
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showErrorToast() {
        closeProgressDialog();
        Toast.makeText(getActivity(), "Unable to get data", Toast.LENGTH_SHORT).show();
    }

    @UiThread
    @Override
    public void showNoConnectivity() {
        closeProgressDialog();
        Toast.makeText(getActivity(), "No Internet Connection..", Toast.LENGTH_SHORT).show();
    }

}
