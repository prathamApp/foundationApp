package com.pratham.foundation.ui.home_screen.profile_new;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.modalclasses.ModalTopCertificates;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.home_screen.profile_new.certificate_display.CertificateDisplayActivity_;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.pratham.foundation.utility.FC_Utility.dpToPx;
import static com.pratham.foundation.utility.FC_Utility.setAppLocal;


@EFragment(R.layout.fragment_profile)
public class ProfileFragment extends Fragment implements ProfileContract.ProfileView, ProfileContract.ProfileItemClicked{

    @Bean(ProfilePresenter.class)
    ProfileContract.ProfilePresenter presenter;

    @ViewById(R.id.my_recycler_view)
    RecyclerView my_recycler_view;
    @ViewById(R.id.tv_studentName)
    TextView tv_studentName;
    @ViewById(R.id.tv_usage)
    TextView tv_usage;
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


    String[] progressArray = {"Progress", "Share"};
    private ProfileOuterDataAdapter adapterParent;

    @AfterViews
    public void initialize() {
        FC_Constants.isTest = false;
        FC_Constants.isPractice = false;
        tv_studentName.setText(""+ FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_NAME,"Student"));
        presenter.setView(ProfileFragment.this);
        if (adapterParent == null) {
            adapterParent = new ProfileOuterDataAdapter(getActivity(), progressArray, this);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
            my_recycler_view.setLayoutManager(mLayoutManager);
            my_recycler_view.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(getActivity()), true));
            my_recycler_view.setItemAnimator(new DefaultItemAnimator());
            my_recycler_view.setAdapter(adapterParent);
        }
        presenter.getCertificateCount();
    }

    @Click(R.id.tv_studentName)
    public void getCertiData(){
        presenter.getCertificateCount(); }

    @Override
    public void setCertificateCount(List<ModalTopCertificates> modalTopCertificatesList) {
        Collections.sort(modalTopCertificatesList, new Comparator<ModalTopCertificates>() {
            @Override
            public int compare(ModalTopCertificates o1, ModalTopCertificates o2) {
                return o1.getTotalPerc().compareTo(o2.getTotalPerc());
            }
        });

        Collections.reverse(modalTopCertificatesList);

        for(int i=0 ; i<modalTopCertificatesList.size() && i<3; i++){
            switch (i) {
                case 0:
                    rl_certi1.setVisibility(View.VISIBLE);
                    certi1_subj.setText(modalTopCertificatesList.get(i).getCertiName());
                    certi1_perc.setText(modalTopCertificatesList.get(i).getTotalPerc());
                    break;
                case 1:
                    rl_certi2.setVisibility(View.VISIBLE);
                    certi2_subj.setText(modalTopCertificatesList.get(i).getCertiName());
                    certi2_perc.setText(modalTopCertificatesList.get(i).getTotalPerc());
                    break;
                case 2:
                    rl_certi3.setVisibility(View.VISIBLE);
                    certi3_subj.setText(modalTopCertificatesList.get(i).getCertiName());
                    certi3_perc.setText(modalTopCertificatesList.get(i).getTotalPerc());
                    break;
            }
        }
//        tv_usage.setText(" Name: "+modalTopCertificatesList.get(0).getCertiName()+" Perc: "+modalTopCertificatesList.get(0).getTotalPerc());
    }

    @Override
    public void itemClicked(String usage) {
        switch (usage){
            case "Certificate":
                showCertificates();
                break;
            case "Projects":
                break;
            case "Usage":
                showUsage();
                break;
            case "Badges":
                break;
            case "Share Content":
                break;
            case "Share App":
                break;
        }
    }

    private void showCertificates() {
        startActivity(new Intent(getActivity(), CertificateDisplayActivity_.class));
    }

    private void showUsage() {
        Toast.makeText(getActivity(), "Work In Progress", Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.ib_langChange)
    public void langChangeButtonClick() {
        showLanguageSelectionDialog();
    }

    private void showLanguageSelectionDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_language_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Spinner lang_spinner = dialog.findViewById(R.id.lang_spinner);
        dia_btn_green.setText("OK");
        dialog.show();
        String currLang = "" + FastSave.getInstance().getString(FC_Constants.LANGUAGE,"Hindi");
        dia_title.setText("Current Language : "+currLang);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), R.layout.custom_spinner,
                getActivity().getResources().getStringArray(R.array.app_Language));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang_spinner.setAdapter(dataAdapter);
        String[] languages = getResources().getStringArray(R.array.app_Language);
        for(int i = 0 ; i<languages.length ; i++) {
            if (currLang.equalsIgnoreCase(languages[i])) {
                lang_spinner.setSelection(i);
                break;
            }
        }

        lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FC_Constants.currentSelectedLanguage = lang_spinner.getSelectedItem().toString();
                FastSave.getInstance().saveString(FC_Constants.LANGUAGE, FC_Constants.currentSelectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dia_btn_green.setOnClickListener(v -> {
//            HomeActivity.languageChanged = true;
            FastSave.getInstance().saveBoolean(FC_Constants.LANGUAGE_SPLASH_DIALOG, true);
            setAppLocal(getActivity(), FC_Constants.currentSelectedLanguage);
            dialog.dismiss();
        });
    }

//    @Click({R.id.rl_share_app, R.id.btn_share_app})
//    public void share_app() {
//        KotlinPermissions.with(getActivity())
//                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE)
//                .onAccepted(permissionResult -> {
//                    try {
//                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
//                        PackageManager pm = ApplicationClass.getInstance().getPackageManager();
//                        ApplicationInfo ai = pm.getApplicationInfo(ApplicationClass.getInstance().getPackageName(), 0);
//                        File localFile = new File(ai.publicSourceDir);
//                        Uri uri = FileProvider.getUriForFile(getActivity(),
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