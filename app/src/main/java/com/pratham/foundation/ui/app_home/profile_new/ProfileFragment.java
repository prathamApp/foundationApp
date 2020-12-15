package com.pratham.foundation.ui.app_home.profile_new;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.async.PushDataToServer_New;
import com.pratham.foundation.customView.BlurPopupDialog.BlurPopupWindow;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.showcaseviewlib.GuideView;
import com.pratham.foundation.customView.showcaseviewlib.config.DismissType;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.ModalTopCertificates;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.admin_panel.MenuActivity_;
import com.pratham.foundation.ui.app_home.profile_new.certificate_display.CertificateDisplayActivity_;
import com.pratham.foundation.ui.app_home.profile_new.chat_display_list.DisplayChatActivity_;
import com.pratham.foundation.ui.app_home.profile_new.display_image_ques_list.DisplayImageQuesActivity_;
import com.pratham.foundation.ui.bottom_fragment.BottomStudentsFragment;
import com.pratham.foundation.ui.bottom_fragment.BottomStudentsFragment_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.io.FileUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ApplicationClass.App_Thumbs_Path;
import static com.pratham.foundation.ApplicationClass.ButtonClickSound;
import static com.pratham.foundation.ui.app_home.HomeActivity.header_rl;
import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.GROUP_MODE;
import static com.pratham.foundation.utility.FC_Constants.INDIVIDUAL_MODE;
import static com.pratham.foundation.utility.FC_Constants.LOGIN_MODE;
import static com.pratham.foundation.utility.FC_Constants.PROFILE_FRAGMENT_SHOWCASE;
import static com.pratham.foundation.utility.FC_Constants.SPLASH_OPEN;
import static com.pratham.foundation.utility.FC_Constants.StudentPhotoPath;
import static com.pratham.foundation.utility.FC_Constants.progressArray;
import static com.pratham.foundation.utility.FC_Constants.sec_Profile;
import static com.pratham.foundation.utility.FC_Utility.decimalFormat;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;
import static com.pratham.foundation.utility.FC_Utility.folderSize;
import static com.pratham.foundation.utility.FC_Utility.getRandomFemaleAvatar;
import static com.pratham.foundation.utility.FC_Utility.getRandomMaleAvatar;


@EFragment(R.layout.fragment_profile)
public class ProfileFragment extends Fragment implements ProfileContract.ProfileView, ProfileContract.ProfileItemClicked {

    @Bean(ProfilePresenter.class)
    ProfileContract.ProfilePresenter presenter;

    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;
    @ViewById(R.id.tv_studentName)
    TextView tv_studentName;
    @ViewById(R.id.tv_usage)
    TextView tv_usage;
    @ViewById(R.id.tv_days)
    TextView tv_days;
    @ViewById(R.id.certi1_perc)
    TextView certi1_perc;
    @ViewById(R.id.certi1_subj)
    TextView certi1_subj;
    @ViewById(R.id.certi2_perc)
    TextView certi2_perc;
    @ViewById(R.id.certi2_subj)
    TextView certi2_subj;
    @ViewById(R.id.certi3_perc)
    TextView certi3_perc;
    @ViewById(R.id.certi3_subj)
    TextView certi3_subj;
    @ViewById(R.id.rl_certi1)
    RelativeLayout rl_certi1;
    @ViewById(R.id.rl_certi2)
    RelativeLayout rl_certi2;
    @ViewById(R.id.rl_certi3)
    RelativeLayout rl_certi3;
    @ViewById(R.id.ib_langChange)
    ImageButton ib_langChange;
    @ViewById(R.id.card_img)
    SimpleDraweeView card_img;

    //    String[] progressArray = {"Progress", "Share"};
    private ProfileOuterDataAdapter adapterParent;
    Context context;
    private GuideView mGuideView;
    private GuideView.Builder builder;

    @SuppressLint("SetTextI18n")
    @AfterViews
    public void initialize() {
        context = getActivity();
        presenter.setView(ProfileFragment.this);
        fragmentSelected();
        if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Profile)) {
            if (!FastSave.getInstance().getBoolean(PROFILE_FRAGMENT_SHOWCASE, false))
                new Handler().postDelayed(this::setShowcaseView, 1200);
        }
    }

    @UiThread
    public void setShowcaseView() {
        builder = new GuideView.Builder(context)
                .setTitle(getResources().getString(R.string.Profile))
                .setContentText(getResources().getString(R.string.Switch_Profile) + "\n"
                        + getResources().getString(R.string.Clicking_icon))
                .setDismissType(DismissType.selfView) //optional - default dismissible by TargetView
                .setTargetView(card_img)
                .build()
                .show();
        FastSave.getInstance().saveBoolean(PROFILE_FRAGMENT_SHOWCASE, true);
//        updatingForDynamicLocationViews();
    }

    private void updatingForDynamicLocationViews() {
        card_img.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mGuideView.updateGuideViewLocation();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageReceived(EventMessage message) {
        if (FastSave.getInstance().getString(APP_SECTION, "").equalsIgnoreCase(sec_Profile)) {
            if (message.getMessage().equalsIgnoreCase(FC_Constants.FRAGMENT_SELECTED) ||
                    message.getMessage().equalsIgnoreCase(FC_Constants.FRAGMENT_RESELECTED) ||
                    message.getMessage().equalsIgnoreCase(FC_Constants.ACTIVITY_RESUMED)) {
                header_rl.setVisibility(View.GONE);
                fragmentSelected();
                if (!FastSave.getInstance().getBoolean(PROFILE_FRAGMENT_SHOWCASE, false))
                    new Handler().postDelayed(this::setShowcaseView, 1000);
            }
        }
    }

    private void fragmentSelected() {
        tv_studentName.setText("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_NAME, "Student"));
        if (adapterParent == null) {
            adapterParent = new ProfileOuterDataAdapter(context, progressArray, this);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
            my_recycler_view.setLayoutManager(mLayoutManager);
            my_recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(context), true));
            my_recycler_view.setItemAnimator(new DefaultItemAnimator());
            my_recycler_view.setAdapter(adapterParent);
        }
        setImage();
        ib_langChange.setVisibility(View.GONE);
        presenter.getCertificateCount();
        presenter.getActiveData();
    }

    @UiThread
    public void setImage() {
        String profileName;
        if (FastSave.getInstance().getString(LOGIN_MODE, "").equalsIgnoreCase(GROUP_MODE))
            card_img.setImageResource(R.drawable.ic_grp_btn);
        else if (FastSave.getInstance().getString(LOGIN_MODE, "").equalsIgnoreCase(INDIVIDUAL_MODE)) {
            File file;
            file = new File(StudentPhotoPath + "" + FastSave.getInstance()
                    .getString(FC_Constants.CURRENT_STUDENT_ID, "") + ".jpg");
            if (file.exists()) {
                card_img.setImageURI(Uri.fromFile(file));
            } else {
                if (!ApplicationClass.getAppMode()) {
                    String gender = AppDatabase.getDatabaseInstance(context).getStudentDao().
                            getStudentAvatar(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                    file = new File(ApplicationClass.foundationPath +
                            "" + App_Thumbs_Path + "" + gender);
                    if (file.exists())
                        card_img.setImageURI(Uri.fromFile(file));
                    else
                        card_img.setImageResource(getRandomFemaleAvatar(context));
                } else {
                    String gender = AppDatabase.getDatabaseInstance(context).getStudentDao().getStudentGender(
                            FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                    if (gender.equalsIgnoreCase("male"))
                        card_img.setImageResource(getRandomMaleAvatar(context));
                    else
                        card_img.setImageResource(getRandomFemaleAvatar(context));
                }
            }
        }
    }

    @Click(R.id.card_img)
    public void showBottomFragment() {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (!ApplicationClass.getAppMode()) {
            SPLASH_OPEN = false;
            BottomStudentsFragment_ bottomStudentsFragment = new BottomStudentsFragment_();
            bottomStudentsFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(),
                    BottomStudentsFragment.class.getSimpleName());
        } else {
            endSession();
            startActivity(new Intent(getActivity(), MenuActivity_.class));
            getActivity().finish();
        }
    }

    @Background
    public void endSession() {
        try {
            String curSession = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("CurrentSession");
            String toDateTemp = AppDatabase.getDatabaseInstance(context).getSessionDao().getToDate(curSession);
            if (toDateTemp.equalsIgnoreCase("na")) {
                AppDatabase.getDatabaseInstance(context).getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
            }
            BackupDatabase.backup(getActivity());
        } catch (Exception e) {
            String curSession = AppDatabase.getDatabaseInstance(context).getStatusDao().getValue("CurrentSession");
            AppDatabase.getDatabaseInstance(context).getSessionDao().UpdateToDate(curSession, FC_Utility.getCurrentDateTime());
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void setDays(int size) {
        try {
            tv_days.setText("" + size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void setCertificateCount(List<ModalTopCertificates> modalTopCertificatesList) {
        Collections.sort(modalTopCertificatesList, (o1, o2) -> o1.getTotalPerc().compareTo(o2.getTotalPerc()));
        Collections.reverse(modalTopCertificatesList);
        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        try {
            for (int i = 0; i < modalTopCertificatesList.size() && i < 3; i++) {
                float num = Float.parseFloat(modalTopCertificatesList.get(i).getTotalPerc());
                String numStr = decimalFormat.format(num) + "%";
                switch (i) {
                    case 0:
                        rl_certi1.setVisibility(View.VISIBLE);
                        certi1_subj.setText(modalTopCertificatesList.get(i).getCertiName());
                        certi1_perc.setText(numStr);
                        break;
                    case 1:
                        rl_certi2.setVisibility(View.VISIBLE);
                        certi2_subj.setText(modalTopCertificatesList.get(i).getCertiName());
                        certi2_perc.setText(numStr);
                        break;
                    case 2:
                        rl_certi3.setVisibility(View.VISIBLE);
                        certi3_subj.setText(modalTopCertificatesList.get(i).getCertiName());
                        certi3_perc.setText(numStr);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void itemClicked(String usage) {
        try {
            ButtonClickSound.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        switch (usage) {
            case "Sync Data":
                pushData();
                break;
            case "Certificate":
                showCertificates();
                break;
            case "Projects":
                break;
            case "Usage":
                showUsage();
                break;
            case "ImageQues":
                showImageQues();
                break;
            case "sync status":
                showSyncStatus();
                break;
            case "ChitChat":
                showChitChat();
                break;
            case "Share Content":
                break;
            case "Share App":
                break;
            case "free space":
                cleanStorage();
                break;
            case "Lang_ic":
                show_STT_Dialog();
                break;
        }
    }

    private void show_STT_Dialog() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.google.android.googlequicksearchbox",
                "com.google.android.voicesearch.greco3.languagepack.InstallActivity"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Bean(PushDataToServer_New.class)
    PushDataToServer_New pushDataToServer;

    private void pushData() {
        pushDataToServer.startDataPush(context, true);
    }

    private void cleanStorage() {
        File directory = new File(ApplicationClass.foundationPath + "/.FCA");
        long full_size = folderSize(directory);

        double sizeMB = (double) full_size / 1024 / 1024;
        String s = " MB";
        if (sizeMB < 1) {
            sizeMB = (double) full_size / 1024;
            s = " KB";
        }
        showDeletingDialog(sizeMB, s);
    }

    BlurPopupWindow exitDialog;
    TextView dia_title;
    LottieAnimationView push_lottie;
    TextView txt_push_dialog_msg;
    TextView txt_push_error;
    RelativeLayout rl_btn;
    Button ok_btn, eject_btn;
    BlurPopupWindow deleteDialog;

    @SuppressLint("SetTextI18n")
    @UiThread
    public void showDeletingDialog(double full_size, String unit) {
        deleteDialog = new BlurPopupWindow.Builder(context)
                .setContentView(R.layout.app_send_success_dialog)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(false)
                .setScaleRatio(0.2f)
                .bindClickListener(v -> {
                    push_lottie.setAnimation("loading_new.json");
                    push_lottie.playAnimation();
                    eject_btn.setVisibility(View.GONE);
                    ok_btn.setVisibility(View.GONE);
                    txt_push_dialog_msg.setText(getResources().getString(R.string.deleting_data));
//                    rl_btn.setVisibility(View.VISIBLE);
                    deleteAppData();
                }, R.id.ok_btn)
                .bindClickListener(v -> {
                    new Handler().postDelayed(() -> {
                        deleteDialog.dismiss();
                    }, 200);
                }, R.id.eject_btn)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();

        push_lottie = deleteDialog.findViewById(R.id.push_lottie);
        push_lottie.setAnimation("loading_new.json");
        push_lottie.playAnimation();
        txt_push_dialog_msg = deleteDialog.findViewById(R.id.txt_push_dialog_msg);
        txt_push_error = deleteDialog.findViewById(R.id.txt_push_error);
        rl_btn = deleteDialog.findViewById(R.id.rl_btn);
        ok_btn = deleteDialog.findViewById(R.id.ok_btn);
        eject_btn = deleteDialog.findViewById(R.id.eject_btn);
        txt_push_dialog_msg.setText(getResources().getString(R.string.free) + " " +
                decimalFormat.format(full_size) + unit + "\n"
                + getResources().getString(R.string.delete_data));
        rl_btn.setVisibility(View.VISIBLE);
        ok_btn.setText(getResources().getString(R.string.yes));
        eject_btn.setText(getResources().getString(R.string.no));
        deleteDialog.show();
    }


    private void deleteAppData() {
        File directory = new File(ApplicationClass.foundationPath + "/.FCA");
        File[] fileListArray = directory.listFiles();

        for (int index = 0; index < fileListArray.length; index++) {
            if (fileListArray[index].exists() && fileListArray[index].isDirectory()) {
                try {
                    FileUtils.deleteDirectory(fileListArray[index]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                fileListArray[index].delete();
        }

        AppDatabase.getDatabaseInstance(context).getContentTableDao().deleteAll();
        txt_push_dialog_msg.setText(getResources().getString(R.string.deleted_data));
        rl_btn.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            deleteDialog.dismiss();
        }, 5000);

    }


    @SuppressLint("SetTextI18n")
    @UiThread
    public void showSyncStatus() {
        exitDialog = new BlurPopupWindow.Builder(getActivity())
                .setContentView(R.layout.sync_status_dialog)
                .bindClickListener(v -> {
                    exitDialog.dismiss();
                }, R.id.dia_btn_ok)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(true)
                .setDismissOnClickBack(true)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();

        dia_title = exitDialog.findViewById(R.id.dia_title);

        dia_title.setText(getResources().getString(R.string.Sync_Time) + " " + FastSave.getInstance().getString(FC_Constants.SYNC_TIME, "NA")
                        + "\n" + getResources().getString(R.string.Data_synced) + " " + FastSave.getInstance().getString(FC_Constants.SYNC_DATA_LENGTH, "0")
                        + "\n" + getResources().getString(R.string.Certificate_synced) + " " + FastSave.getInstance().getString(FC_Constants.SYNC_CERTI_LENGTH, "0")
                        + "\n" + getResources().getString(R.string.Media_synced) + " " + FastSave.getInstance().getString(FC_Constants.SYNC_MEDIA_LENGTH, "0")
                /*+"Media failed : "+failed_ImageLength*/);
        exitDialog.show();
    }

    private void showChitChat() {
        startActivity(new Intent(context, DisplayChatActivity_.class));
    }

    private void showImageQues() {
        startActivity(new Intent(context, DisplayImageQuesActivity_.class));
    }

    private void showCertificates() {
        startActivity(new Intent(context, CertificateDisplayActivity_.class));
    }

    private void showUsage() {
        Toast.makeText(context, "Work In Progress", Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.ib_langChange)
    public void langChangeButtonClick() {
        showLanguageSelectionDialog();
    }

    @SuppressLint("SetTextI18n")
    private void showLanguageSelectionDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_language_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Spinner lang_spinner = dialog.findViewById(R.id.lang_spinner);
        dia_btn_green.setText(getResources().getString(R.string.Okay));
        dialog.show();
        String currLang = " " + FastSave.getInstance().getString(FC_Constants.LANGUAGE, "Hindi");
        dia_title.setText(getResources().getString(R.string.curr_lang) + currLang);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, R.layout.custom_spinner,
                context.getResources().getStringArray(R.array.app_Language));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang_spinner.setAdapter(dataAdapter);
        String[] languages = getResources().getStringArray(R.array.app_Language);
        for (int i = 0; i < languages.length; i++) {
            if (currLang.equalsIgnoreCase(languages[i])) {
                lang_spinner.setSelection(i);
                break;
            }
        }

        lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                FC_Constants.currentSelectedLanguage = lang_spinner.getSelectedItem().toString();
//                FastSave.getInstance().saveString(FC_Constants.LANGUAGE, FC_Constants.currentSelectedLanguage);
                FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dia_btn_green.setOnClickListener(v -> {
//            HomeActivity.languageChanged = true;
            FastSave.getInstance().saveBoolean(FC_Constants.LANGUAGE_SPLASH_DIALOG, true);
//            setAppLocal(context, FastSave.getInstance().
//                    getString(FC_Constants.LANGUAGE, FC_Constants.HINDI));
            dialog.dismiss();
        });
    }

//    @Click({R.id.rl_share_app, R.id.btn_share_app})
//    public void share_app() {
//        KotlinPermissions.with(context)
//                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE)
//                .onAccepted(permissionResult -> {
//                    try {
//                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
//                        PackageManager pm = ApplicationClass.getInstance().getPackageManager();
//                        ApplicationInfo ai = pm.getApplicationInfo(ApplicationClass.getInstance().getPackageName(), 0);
//                        File localFile = new File(ai.publicSourceDir);
//                        Uri uri = FileProvider.getUriForFile(context,
//                                BuildConfig.APPLICATION_ID + ".provider", localFile);
//                        intentShareFile.setType("*/*");
//                        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
//                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Please download apk from here...");
//                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.pratham.foundation");
//                        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        startActivity(Intent.createChooser(intentShareFile, "Share through"));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                })
//                .ask();
//    }
}