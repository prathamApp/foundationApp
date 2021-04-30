package com.pratham.foundation.async;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.FilePushResponse;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@EBean
public class PushDataBaseZipToServer {

    private Context context;
    private boolean pushSuccessfull = false, pushImageSuccessfull = false, showUi = false;
    CustomLodingDialog pushDialog;
    LottieAnimationView push_lottie;
    TextView txt_push_dialog_msg;
    TextView txt_push_error;
    RelativeLayout rl_btn;
    Button ok_btn, eject_btn;
    private int BUFFER = 10000;


    public PushDataBaseZipToServer(Context context) {
        this.context = context;
        showUi = false;
    }

    /**
     * This method begins the process of pushing data to server.
     * Locally stored data is collected and added to its respective JsonArray defined globally above.
     *
     * @param showUi is used to show the push Dialog.
     */
    @Background
    public void startDataPush(Context context, boolean showUi) {
        if (FC_Utility.isDataConnectionAvailable(context)) {
            this.context = context;
            this.showUi = showUi;
            //Show Dialog
            if (showUi)
                showPushDialog(context);
            //Here data is fetched from local database and added to a list and then passed to JsonArray.
            try {
                setMainTextToDialog(context.getResources().getString(R.string.Collecting_Data));
                pushSuccessfull = false;
                //Checks if device is connected to wifi
                pushDataToServer(context, FC_Constants.DB_ZIP_PUSH_API);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "No Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    //Set heading text of Dialog
    @SuppressLint("SetTextI18n")
    @UiThread
    public void setMainTextToDialog(String dialogMsg) {
        if (showUi)
            txt_push_dialog_msg.setText("" + dialogMsg);
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    public void setSyncLottieToDialog() {
        if (showUi) {
            push_lottie.setAnimation("cloud_sync.json");
            push_lottie.playAnimation();
        }
    }

    //Set sub text of dialog
    @SuppressLint("SetTextI18n")
    @UiThread
    public void setSubTextToDialog(String dialogMsg) {
        if (showUi) {
            txt_push_error.setVisibility(View.VISIBLE);
            txt_push_error.setText("" + dialogMsg);
        }
    }

    //Custom loading dialog is shown
    @UiThread
    public void showPushDialog(Context context) {
        if (showUi) {
            pushDialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
            pushDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pushDialog.setContentView(R.layout.app_send_success_dialog);
            Objects.requireNonNull(pushDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pushDialog.setCancelable(false);
            pushDialog.setCanceledOnTouchOutside(false);
            pushDialog.show();

            push_lottie = pushDialog.findViewById(R.id.push_lottie);
            txt_push_dialog_msg = pushDialog.findViewById(R.id.txt_push_dialog_msg);
            txt_push_error = pushDialog.findViewById(R.id.txt_push_error);
            rl_btn = pushDialog.findViewById(R.id.rl_btn);
            ok_btn = pushDialog.findViewById(R.id.ok_btn);
            eject_btn = pushDialog.findViewById(R.id.eject_btn);

            txt_push_error.setText("");
            txt_push_error.setVisibility(View.GONE);
            ok_btn.setVisibility(View.GONE);
            eject_btn.setVisibility(View.GONE);

            ok_btn.setOnClickListener(v -> {
                pushDialog.dismiss();
            });
        }
    }

    public void zip(List<String> _files, String zipFileName, File filepath) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte[] data = new byte[BUFFER];
            for (int i = 0; i < _files.size(); i++) {
                Log.v("Compress", "Adding: " + _files.get(i));
                FileInputStream fi = new FileInputStream(_files.get(i));
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(_files.get(i).substring(_files.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
//            new File(zipFileName).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Call only this method, do the data collection before
    public void pushDataToServer(Context context, String... url) {
        try {
//            String newdata = compress(String.valueOf(data));
            BackupDatabase.backup(context);
            String fielName = "" + FC_Utility.getUUID();
            String filePathStr = Environment.getExternalStorageDirectory().toString()
                    + "/PrathamBackups/" + AppDatabase.DB_NAME; // file path to save
            // Type the path of the files in here
            File dir = new File(Environment.getExternalStorageDirectory().toString() + "/PrathamBackups/");
            File[] db_files = dir.listFiles();
            if (db_files != null) {
                List<String> fileNameListStrings = new ArrayList<>();
                for (int i = 0; i < db_files.length; i++)
                    if (db_files[i].exists() && db_files[i].isFile() && db_files[i].getName().contains("foundation"))
                        fileNameListStrings.add(db_files[i].getAbsolutePath());
                zip(fileNameListStrings, filePathStr + ".zip", new File(filePathStr));

                AndroidNetworking.upload(url[0])
                        .addHeaders("Content-Type", "file/zip")
                        .addMultipartFile("" + fielName, new File(filePathStr + ".zip"))
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("PushData", "DB ZIP PUSH " + response);
                                Gson gson = new Gson();
                                FilePushResponse pushResponse = gson.fromJson(response, FilePushResponse.class);

                                new File(filePathStr + ".zip").delete();
                                if (pushResponse.isSuccess()/*equalsIgnoreCase("success")*/) {
                                    Log.d("PushData", "DB ZIP PUSH SUCCESS");
                                    pushSuccessfull = true;
                                    setDataPushSuccessfull();
                                } else {
                                    Log.d("PushData", "Failed DB ZIP PUSH");
                                    pushSuccessfull = false;
                                    setDataPushFailed();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                //Fail - Show dialog with failure message.
                                Log.d("PushData", "Data push FAIL");
                                Log.d("PushData", "ERROR  " + anError);
                                pushSuccessfull = false;
                                setDataPushFailed();
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method shows success dialog
    @UiThread
    public void setDataPushSuccessfull() {
        if (showUi) {
            setMainTextToDialog(context.getResources().getString(R.string.DB_Zip_pushed_successfully));
            ok_btn.setText(context.getResources().getString(R.string.Okay));
            ok_btn.setVisibility(View.VISIBLE);
        }
    }

    @UiThread
    public void hideOKBtn() {
        if (showUi) {
            ok_btn.setText(context.getResources().getString(R.string.Okay));
            ok_btn.setVisibility(View.GONE);
        }
    }

    //Method shows failure dialog
    @UiThread
    public void setDataPushFailed() {
        if (showUi) {
            setMainTextToDialog(context.getResources().getString(R.string.OOPS));
            setSubTextToDialog(context.getResources().getString(R.string.DB_Zip_pushed_failed));
            push_lottie.setAnimation("error_cross.json");
            push_lottie.playAnimation();
            ok_btn.setText(context.getResources().getString(R.string.Okay));
            ok_btn.setVisibility(View.VISIBLE);
        }
    }

}