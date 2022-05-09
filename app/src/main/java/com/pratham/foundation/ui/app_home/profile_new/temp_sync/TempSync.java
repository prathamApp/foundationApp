package com.pratham.foundation.ui.app_home.profile_new.temp_sync;


import static com.pratham.foundation.ApplicationClass.BUILD_DATE;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Modal_Log;
import com.pratham.foundation.modalclasses.SyncLog;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import java.io.File;
import java.util.Objects;

@EActivity(R.layout.activity_temp_sync)
public class TempSync extends BaseActivity {

    public static String TEST_SYNC_API = "http://prathamyouthnet.org/ssmspushdb/dbpush.php";
//    static String btn_1 = "0a6f4e3f-6669-45ed-92cc-66c34a1a2bdf";
    static String btn_1 = "FC_933552fb35f51447ce2f37e82dcf6267";
    static String btn_2 = "FC_c14bcab0f968a514c5cf80a92ee51bd9";
    static String btn_3 = "0a6f4e3f-6669-45ed-92cc-66c34a1a2bdf";
    static String btn_4 = "795dabc6-69a6-4c4d-802e-ba1942ab81a6";
    static String btn_5 = "61bd569f-7c85-453e-94c4-bb3672937b72";
    static String btn_6 = "6063a838-79e8-4b51-8f5c-1f785745d069";
    static String btn_7 = "60747e93-eccb-4534-8eec-0676737f5a95";
    static String btn_8 = "cda70be4-b1e5-47ab-a6be-f7edefba53ae";
    static String btn_9 = "d29d1568-aa9d-414c-8184-6bd696ecdd2f";
    static String btn_10 = "dbbcdf7f-b039-4d1d-8054-e06e7be91991";

    @AfterViews
    public void initiate() {

    }

    @Click(R.id.goto_results)
    public void gotoResults() {
        startActivity(new Intent(this, SyncResultActivity_.class));
    }

    @Click(R.id.button_1)
    public void button1Click() {
        showLoader();
        String url = ApplicationClass.getStoragePath().toString()+ "/FCAInternal/"+btn_1;
//        Toast.makeText(this, ""+url, Toast.LENGTH_SHORT).show();
        PushData(url);
    }
    @Click(R.id.button_2)
    public void button2Click() {
        showLoader();
        String url = ApplicationClass.getStoragePath().toString()+ "/FCAInternal/"+btn_2;
//        Toast.makeText(this, ""+url, Toast.LENGTH_SHORT).show();
        PushData(url);
    }
    @Click(R.id.button_3)
    public void button3Click() {
        showLoader();
        String url = ApplicationClass.getStoragePath().toString()+ "/FCAInternal/"+btn_3;
//        Toast.makeText(this, ""+url, Toast.LENGTH_SHORT).show();
        PushData(url);
    }
    @Click(R.id.button_4)
    public void button4Click() {
        showLoader();
        String url = ApplicationClass.getStoragePath().toString()+ "/FCAInternal/"+btn_4;
//        Toast.makeText(this, ""+url, Toast.LENGTH_SHORT).show();
        PushData(url);
    }
    @Click(R.id.button_5)
    public void button5Click() {
        showLoader();
        String url = ApplicationClass.getStoragePath().toString()+ "/FCAInternal/"+btn_5;
//        Toast.makeText(this, ""+url, Toast.LENGTH_SHORT).show();
        PushData(url);
    }
    @Click(R.id.button_6)
    public void button6Click() {
        showLoader();
        String url = ApplicationClass.getStoragePath().toString()+ "/FCAInternal/"+btn_6;
//        Toast.makeText(this, ""+url, Toast.LENGTH_SHORT).show();
        PushData(url);
    }
    @Click(R.id.button_7)
    public void button7Click() {
        showLoader();
        String url = ApplicationClass.getStoragePath().toString()+ "/FCAInternal/"+btn_7;
//        Toast.makeText(this, ""+url, Toast.LENGTH_SHORT).show();
        PushData(url);
    }
    @Click(R.id.button_8)
    public void button8Click() {
        showLoader();
        String url = ApplicationClass.getStoragePath().toString()+ "/FCAInternal/"+btn_8;
//        Toast.makeText(this, ""+url, Toast.LENGTH_SHORT).show();
        PushData(url);
    }
    @Click(R.id.button_9)
    public void button9Click() {
        showLoader();
        String url = ApplicationClass.getStoragePath().toString()+ "/FCAInternal/"+btn_9;
//        Toast.makeText(this, ""+url, Toast.LENGTH_SHORT).show();
        PushData(url);
    }
    @Click(R.id.button_10)
    public void button10Click() {
        showLoader();
        String url = ApplicationClass.getStoragePath().toString()+ "/FCAInternal/"+btn_10;
//        Toast.makeText(this, ""+url, Toast.LENGTH_SHORT).show();
        PushData(url);
    }

    @UiThread
    public void PushData(String filePathStr) {
        try {
            if(new File(filePathStr + ".zip").exists()) {
                AndroidNetworking.upload(TEST_SYNC_API)
                        .addHeaders("Content-Type", "file/zip")
                        .addMultipartFile("fileJson", new File(filePathStr + ".zip"))
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("PushData", "DATA PUSH " + response);
                                Gson gson = new Gson();
                                SyncLog pushResponse = gson.fromJson(response, SyncLog.class);

                                if(pushResponse.getPushId()!=0) {
                                    Log.d("PushData", "DATA PUSH SUCCESS");
                                    ShowResponse(pushResponse, response);
                                }else{
                                    setDataPushFailed();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("PushData", "Data push FAIL");
                                Log.d("PushData", "ERROR  " + anError);
                                setDataPushFailed();
                            }
                        });
            }else{
                Toast.makeText(this, "File not Found", Toast.LENGTH_SHORT).show();
                dismissLoadingDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CustomLodingDialog sttDialog;
    @UiThread
    public void ShowResponse(SyncLog pushResponse, String response) {
        dismissLoadingDialog();
        AppDatabase.getDatabaseInstance(this).getSyncLogDao().insert(pushResponse);
        sttDialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
        sttDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(sttDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sttDialog.setContentView(R.layout.sync_new_test_dialog);
        sttDialog.setCanceledOnTouchOutside(true);

        TextView dia_title = sttDialog.findViewById(R.id.dia_title);
        Button dia_btn_ok = sttDialog.findViewById(R.id.dia_btn_ok);

        String err= "";
        if(!pushResponse.getError().equalsIgnoreCase("") && !pushResponse.getError().equalsIgnoreCase(" "))
            err = "ERROR : " +pushResponse.getError();


        dia_title.setText("PushID : " + pushResponse.getPushId() +"\n\n"
                + "UUID : " + pushResponse.getUuid() +"\n\n"
                + "Push Date : " + pushResponse.getPushDate() +"\n\n"
                + "Push Status : " + pushResponse.getStatus() +"\n\n"
                + err );

        dia_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(() -> {
                    sttDialog.dismiss();
                    addResponse(pushResponse, response);
                }, 100);

            }
        });
        sttDialog.show();
    }

    @UiThread
    public void addResponse(SyncLog pushResponse, String response) {
        Modal_Log log = new Modal_Log();

        log.setCurrentDateTime(pushResponse.getPushDate());
        log.setErrorType("");
        log.setExceptionMessage("TEMP_SYNC_RESPONSE");
        log.setMethodName(""+ pushResponse.getPushId());
        log.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
        log.setGroupId("");
        log.setLogDetail(""+response);
        log.setExceptionStackTrace("APK BUILD DATE : " + BUILD_DATE);
        log.setDeviceId("" + FC_Utility.getDeviceID());

        Log.d("PushData", "pushStatusJson JSON : " + pushResponse);
        AppDatabase.getDatabaseInstance(this).getLogsDao().insertLog(log);
        BackupDatabase.backup(this);
    }

    public void setDataPushSuccessfull(SyncLog pushResponse) {

    }

    public void setDataPushFailed() {
        dismissLoadingDialog();
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
            loaderVisible = true;
        }
    }

    @UiThread
    public void dismissLoadingDialog() {
        try {
            if (loaderVisible) {
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

}
