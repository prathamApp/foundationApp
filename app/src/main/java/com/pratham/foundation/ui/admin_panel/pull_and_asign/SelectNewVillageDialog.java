package com.pratham.foundation.ui.admin_panel.pull_and_asign;

import android.content.Context;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.fontsview.SansCheckBox;
import com.pratham.foundation.database.domain.Village;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SelectNewVillageDialog extends CustomLodingDialog {

    @BindView(R.id.txt_message)
    TextView txt_message_village;
    @BindView(R.id.flowLayout)
    GridLayout flowLayout;

    Context context;
    List<Village> villageList;
    List<SansCheckBox> checkBoxes = new ArrayList<>();
    PullAndAssign_Contract.VillageSelectListener villageSelectListener;

    public SelectNewVillageDialog(@NonNull Context context,
                                  PullAndAssign_Contract.VillageSelectListener villageSelectListener,
                                  List tempList) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen/*Theme_Black_NoTitleBar_Fullscreen*//*Theme_DeviceDefault_Light_NoActionBar*/);
        this.villageList = tempList;
        this.context = context;
        this.villageSelectListener = villageSelectListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_village_dialog);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        txt_message_village.setText("Select Village");
        for (int i = 0; i < villageList.size(); i++) {
            SansCheckBox checkBox = new SansCheckBox(context);
            checkBox.setText(villageList.get(i).getVillageName());
            checkBox.setTextSize(context.getResources().getDimension(R.dimen._8sdp));
            checkBox.setTag(villageList.get(i).getVillageId());
            flowLayout.addView(checkBox);
            checkBoxes.add(checkBox);
        }
    }

    @OnClick(R.id.btn_back)
    public void closeDialog() {
        dismiss();
    }

    @OnClick(R.id.txt_clear_changes)
    public void clearChanges() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            checkBoxes.get(i).setChecked(false);
        }
    }

    @OnClick(R.id.txt_ok)
    public void ok() {
        ArrayList<String> villageIDList = new ArrayList();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isChecked()) {
                villageIDList.add(checkBoxes.get(i).getTag().toString());
            }
        }
        villageSelectListener.getSelectedItems(villageIDList);
        dismiss();
    }
}

