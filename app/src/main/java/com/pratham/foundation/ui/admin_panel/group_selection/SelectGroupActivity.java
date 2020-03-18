package com.pratham.foundation.ui.admin_panel.group_selection;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.ui.admin_panel.group_selection.fragment_select_group.FragmentSelectGroup;
import com.pratham.foundation.ui.admin_panel.group_selection.fragment_select_group.FragmentSelectGroup_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Objects;

@EActivity(R.layout.activity_select_group)
public class SelectGroupActivity extends BaseActivity {

    @ViewById(R.id.main_layout)
    RelativeLayout main_layout;
    @ViewById(R.id.frame_group)
    FrameLayout frame_group;

    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Bundle bundle = new Bundle();
        bundle.putBoolean(FC_Constants.GROUP_AGE_ABOVE_7, true);
        FC_Utility.showFragment(this, new FragmentSelectGroup_(), R.id.frame_group,
                bundle, FragmentSelectGroup.class.getSimpleName());
//        FC_Utility.showFragment(this, new MenuFragment_(), R.id.frame_group,
//                null, MenuFragment.class.getSimpleName());
    }

    @SuppressLint("SetTextI18n")
    private void showExitDialog() {
        CustomLodingDialog dialog = new CustomLodingDialog(this, R.style.ExitDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.lottie_exit_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

//        TextView dia_title = dialog.findViewById(R.id.dia_title);
//        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_no = dialog.findViewById(R.id.dia_btn_no);
        TextView dia_btn_yes = dialog.findViewById(R.id.dia_btn_yes);

//        dia_btn_green.setText (getResources().getString(R.string.Restart));
//        dia_btn_yes.setText   (getResources().getString(R.string.Exit));
//        dia_btn_no.setText(getResources().getString(R.string.Cancel));

//        dia_btn_green.setOnClickListener(v -> {
//            finishAffinity();
//            context.startActivity(new Intent(context, SplashActivity_.class));
//            dialog.dismiss();
//        });

        dia_btn_yes.setOnClickListener(v -> {
            finishAffinity();
            dialog.dismiss();
        });

        dia_btn_no.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    public void onBackPressed() {
//        Log.d("Grp_Log", "onBackPressed: ");
//        int fragments = getSupportFragmentManager().getBackStackEntryCount();
//        Log.d("Grp_Log", "onBackPressed: "+fragments);
        showExitDialog();
/*        if (fragments == 1) {
            Log.d("Grp_Log", "onBackPressed: "+fragments);
//            finish();
        } else {
            if (fragments > 1) {
                Log.d("Grp_Log", "(fragments>1): "+fragments);
                // FragmentManager.BackStackEntry first = getSupportFragmentManager().getBackStackEntryAt(0);
                getSupportFragmentManager().popBackStack();
                Log.d("Grp_Log", "onBackPressed: "+fragments);
            } else {
                Log.d("Grp_Log", "else : "+fragments);
                super.onBackPressed();
            }
        }*/
    }
}