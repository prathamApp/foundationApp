package com.pratham.foundation.ui.admin_panel.fragment_admin_panel.PushOrAssign;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.ui.admin_panel.AdminControlsActivity_;
import com.pratham.foundation.utility.BaseActivity;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import static com.pratham.foundation.utility.SplashSupportActivity.ButtonClickSound;

@EActivity(R.layout.activity_push_data)
public class PushDataActivity extends BaseActivity implements PushDataContract.PushDataView {

    @Bean(PushDataPresenter.class)
    PushDataContract.PushDataPresenter presenter;

    boolean isConnectedToRasp = false;

    @AfterViews
    public void initialize() {
        checkConnectivity();
        presenter.createJsonForTransfer();

    }

    @UiThread
    @Override
    public void showDialog(boolean statusFlg) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        if (statusFlg)
            dia_title.setText("Data pushed successfully");
        else
            dia_title.setText("Data push failed");

        dia_btn_green.setText("Ok");
        dia_btn_red.setVisibility(View.GONE);
        dia_btn_yellow.setVisibility(View.GONE);
        dialog.show();

        dia_btn_green.setOnClickListener(v -> {
            ButtonClickSound.start();
            dialog.dismiss();
            finishActivity();
        });
    }

    @UiThread
    @Override
    public void finishActivity() {
        Intent intent = new Intent(this, AdminControlsActivity_.class);
        startActivity(intent);
    }

    ProgressDialog dialog;
    boolean loaderShownFlg = false;

    @UiThread
    @Override
    public void showLoaderDialog() {
        if (!loaderShownFlg) {
            loaderShownFlg = true;
            dialog = new ProgressDialog(this);
            FC_Utility.showDialogInApiCalling(dialog, this, "Uploading Data..");
        }
    }

    @UiThread
    @Override
    public void dismissLoaderDialog() {
        if (loaderShownFlg) {
            loaderShownFlg = false;
            FC_Utility.dismissDialog(dialog);
        }
    }

    public void checkConnectivity() {
        if (ApplicationClass.wiseF.isDeviceConnectedToMobileNetwork()) {
            //callOnlineContentAPI(contentList, parentId);
        } else if (ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_KOLIBRI_HOTSPOT)) {
                //  if (FastSave.getInstance().getString(PD_Constant.FACILITY_ID, "").isEmpty())
                isConnectedToRasp = checkConnectionForRaspberry();
                //callKolibriAPI(contentList, parentId);
            } else {
                isConnectedToRasp = false;
                //callOnlineContentAPI(contentList, parentId);
            }
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkConnectionForRaspberry() {
        boolean isRaspberry = false;
        if (ApplicationClass.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (ApplicationClass.wiseF.isDeviceConnectedToSSID(FC_Constants.PRATHAM_KOLIBRI_HOTSPOT)) {
                try {
                    isRaspberry = true;
                    /*JSONObject object = new JSONObject();
                    object.put("username", "pratham");
                    object.put("password", "pratham");
                    new PD_ApiRequest(context, ContentPresenterImpl.this)
                            .getacilityIdfromRaspberry(PD_Constant.FACILITY_ID, PD_Constant.RASP_IP + "/api/session/", object);*/
                } catch (Exception e) {
                    isRaspberry = false;
                    e.printStackTrace();
                }
            }
        } else isRaspberry = false;
        return isRaspberry;
    }
}
