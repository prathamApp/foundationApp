package com.pratham.foundation.ui.student_profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;


import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.customView.CircularImageView;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.ui.old_home.HomeActivity;
import com.pratham.foundation.ui.student_profile.discription_adapter.DiscriptionAdapter;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Student_profile_activity extends BaseActivity implements Student_profile_contract.Student_profile_view, DiscreteScrollView.ScrollStateChangeListener<ForecastAdapter.ViewHolder>,
        DiscreteScrollView.OnItemChangedListener<ForecastAdapter.ViewHolder> {

    @BindView(R.id.tv_studentName)
    TextView studentName;
    @BindView(R.id.ib_langChange)
    ImageButton ib_langChange;
    @BindView(R.id.profileImage)
    CircularImageView profileImage;
    @BindView(R.id.forecast_city_picker)
    DiscreteScrollView cityPicker;
    @BindView(R.id.discription)
    RecyclerView discriptionRecycler;
    Student_profile_contract.Student_profile_presenter student_profile_presenter;

    private List<Forecast> forecasts;
    private Context context;
    List maxScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = Student_profile_activity.this;
        student_profile_presenter = new Student_profile_presenterImpl(this);
        displayStudentProfileNameAndImage();
        HomeActivity.languageChanged = false;
        // student_profile_presenter.calculateStudentProgress();
        loadRecycler();
    }

    @SuppressLint("SetTextI18n")
    private void showLanguageSelectionDialog() {
        final Dialog dialog = new Dialog(context);
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner,
                getResources().getStringArray(R.array.app_Language));
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
            HomeActivity.languageChanged = true;
            dialog.dismiss();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void displayStudentProfileNameAndImage() {
        new AsyncTask<Object, Void, Object>() {
            String profileName;
            String sImage;

            @Override
            protected Object doInBackground(Object... objects) {
                profileName = student_profile_presenter.getStudentProfileName();
                sImage = student_profile_presenter.getStudentProfileImage();
                studentName.setText(profileName);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (sImage != null) {
                    if (sImage.equalsIgnoreCase("group_icon"))
                        profileImage.setImageResource(R.drawable.ic_grp_btn);
                    else if (!FC_Constants.GROUP_LOGIN && ApplicationClass.isTablet)
                        profileImage.setImageResource(R.drawable.b2);
                    else {
                        profileImage.setImageResource(R.drawable.b2);
                        switch (sImage) {
                            case "b1.png":
                                profileImage.setImageResource(R.drawable.b1);
                                break;
                            case "b2.png":
                                profileImage.setImageResource(R.drawable.b2);
                                break;
                            case "b3.png":
                                profileImage.setImageResource(R.drawable.b3);
                                break;
                            case "g1.png":
                                profileImage.setImageResource(R.drawable.g1);
                                break;
                            case "g2.png":
                                profileImage.setImageResource(R.drawable.g2);
                                break;
                            case "g3.png":
                                profileImage.setImageResource(R.drawable.g3);
                                break;
                        }
/*                        Glide.with(context).load(ApplicationClass.pradigiPath + "/.FCA/" +
                                FC_Constants.currentSelectedLanguage + "/App_Thumbs/" + sImage).into(profileImage);*/
                    }
                } else
                    profileImage.setImageResource(R.drawable.b2);
            }
        }.execute();
    }

    private void loadRecycler() {
        forecasts = WeatherStation.get(this).getForecasts();
        cityPicker.setSlideOnFling(true);
        cityPicker.setAdapter(new ForecastAdapter(forecasts));
        cityPicker.addOnItemChangedListener(this);
        cityPicker.addScrollStateChangeListener(this);
        cityPicker.setItemTransitionTimeMillis(100);
        cityPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        // forecastView.setForecast(forecasts.get(0));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        discriptionRecycler.setLayoutManager(layoutManager);
        loadDiscriptionData(context.getString(R.string.Summary));
        // discriptionRecycler.setAdapter(new DiscriptionAdapter(forecasts, this));
    }

    List<Profile_Model> adapterList;

    @SuppressLint("StaticFieldLeak")
    private void loadDiscriptionData(String type) {
        switch (type) {
            case "Summary":
                new AsyncTask<Object, Void, Object>() {
                    int learntSentence;
                    int learntWords;

                    @Override
                    protected Object doInBackground(Object... objects) {
                        learntSentence = AppDatabase.getDatabaseInstance(context).getKeyWordDao().getLearntSentenceCount(FC_Constants.currentStudentID);
                        learntWords = AppDatabase.getDatabaseInstance(context).getKeyWordDao().getLearntWordCount(FC_Constants.currentStudentID);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        List temp = Arrays.asList(
                                new Profile_Model("Learnt Words", "" + learntWords, context.getString(R.string.Summary), "", 0),
                                new Profile_Model("Learnt Sentences", "" + learntSentence, context.getString(R.string.Summary), "", 0));
                        discriptionRecycler.setAdapter(new DiscriptionAdapter(temp, context));
                    }
                }.execute();
                break;
            case "Certificates":
                List temp = Arrays.asList(
                        new Profile_Model("", "", context.getString(R.string.Certificates), "", 0));
                discriptionRecycler.setAdapter(new DiscriptionAdapter(temp, context));

                //getCertificates();
                break;
            case "Progress":
                adapterList = new ArrayList();
                getAllProgressForAdapter(0);
                break;
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void getAllProgressForAdapter(int pos) {
        new AsyncTask<Object, Void, Object>() {
            String sTitle = "";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (pos == 0)
                    showLoader();
            }

            @Override
            protected Object doInBackground(Object... objects) {
                try {
                    String rootNodeId = FC_Utility.getRootNode(FC_Constants.currentSelectedLanguage);
                    maxScore = new ArrayList();
                    switch (pos) {
                        case 0:
                            findMaxScore(""+AppDatabase.appDatabase.getContentTableDao()
                                    .getContentDataByTitle(rootNodeId,"Learning"));
                            break;
                        case 1:
                            findMaxScore(""+AppDatabase.appDatabase.getContentTableDao()
                                    .getContentDataByTitle(rootNodeId,"Practice"));
                            break;
                        case 2:
                            findMaxScore(""+AppDatabase.appDatabase.getContentTableDao()
                                    .getContentDataByTitle(rootNodeId,"Fun"));
                            break;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                new Handler().postDelayed(() -> {
                    int progress = getWholePercentage();
                    switch (pos) {
                        case 0:
                            sTitle = "Learning";
                            adapterList.add(new Profile_Model("" + sTitle, "", "" + context.getString(R.string.Progress), "", progress));
                            break;
                        case 1:
                            sTitle = "Practice";
                            adapterList.add(new Profile_Model("" + sTitle, "", "" + context.getString(R.string.Progress), "", progress));
                            break;
                        case 2:
                            sTitle = "Fun";
                            adapterList.add(new Profile_Model("" + sTitle, "", "" + context.getString(R.string.Progress), "", progress));
                            break;
                    }
                    maxScore = new ArrayList();
                    int cntr = pos + 1;
                    if (cntr < 3) {
                        getAllProgressForAdapter(cntr);
                    } else {
                        discriptionRecycler.setAdapter(new DiscriptionAdapter(adapterList, context));
                        dismissLoadingDialog();
                    }

                }, 1200);
            }
        }.execute();

    }

    public Dialog myLoadingDialog;

    private void showLoader() {
        myLoadingDialog = new Dialog(this);
        myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myLoadingDialog.setContentView(R.layout.loading_dialog);
        myLoadingDialog.setCanceledOnTouchOutside(false);
//        myLoadingDialog.setCancelable(false);
        myLoadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (myLoadingDialog != null) {
            myLoadingDialog.dismiss();
        }
    }

    private int getWholePercentage() {
        double totalScore = 0;
        for (int j = 0; maxScore.size() > j; j++) {
            totalScore = totalScore + Double.parseDouble(maxScore.get(j).toString());
        }
        if (maxScore.size() > 0) {
            int percent = (int) (totalScore / maxScore.size());
            return percent;
        } else {
            return 0;
        }
    }

    private void findMaxScore(String nodeId) {
        List<ContentTable> childList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getChildsOfParent(nodeId);
        for (int childCnt = 0; childList.size() > childCnt; childCnt++) {
//            if (!childList.get(childCnt).getNodeId().equalsIgnoreCase("4033")) {
            if (childList.get(childCnt).getNodeType().equals("Resource")) {
                double maxScoreTemp = 0.0;
                List<ContentProgress> contentProgressList = AppDatabase.getDatabaseInstance(context).getContentProgressDao().getContentNodeProgress(FC_Constants.currentStudentID, childList.get(childCnt).getResourceId(), "resourceProgress");
                for (int cnt = 0; cnt < contentProgressList.size(); cnt++) {
                    String d = contentProgressList.get(cnt).getProgressPercentage();
                    double scoreTemp = Double.parseDouble(d);
                    if (maxScoreTemp < scoreTemp) {
                        maxScoreTemp = scoreTemp;
                    }
                }
                maxScore.add(maxScoreTemp);
            } else {
                findMaxScore(childList.get(childCnt).getNodeId());
            }
        }
//        }
    }
/*    public void findMaxScore(String nodeId) {
        List<ContentTable> childList = AppDatabase.getDatabaseInstance(context).getContentTableDao().getChildsOfParent(nodeId);
        for (int childCnt = 0; childList.size() > childCnt; childCnt++) {
            if (childList.get(childCnt).getNodeType().equals("Resource")) {
                double maxScoreTemp = 0.0;
                List<ContentProgress> score = AppDatabase.getDatabaseInstance(context).getContentProgressDao().getProgressByStudIDAndResID(FC_Constants.currentStudentID, childList.get(childCnt).getResourceId(), "resourceProgress");
                for (int cnt = 0; cnt < score.size(); cnt++) {
                    String d = score.get(cnt).getProgressPercentage();
                    double scoreTemp = Double.parseDouble(d);
                    if (maxScoreTemp < scoreTemp) {
                        maxScoreTemp = scoreTemp;
                    }
                }
                maxScore.add(maxScoreTemp);
            } else {
                findMaxScore(childList.get(childCnt).getNodeId());
            }
        }
    }

    private int getWholePercentage(List maxScore) {
        double totalScore = 0;
        for (int j = 0; maxScore.size() > j; j++) {
            totalScore = totalScore + Double.parseDouble(maxScore.get(j).toString());
        }
        if (maxScore.size() > 0) {
            int percent = (int) (totalScore / maxScore.size());
            return percent;
        } else {
            return 0;
        }
    }*/

    @Override
    public void onCurrentItemChanged(@Nullable ForecastAdapter.ViewHolder holder, int position) {
        //viewHolder will never be null, because we never remove items from adapter's list
        if (holder != null) {
            // forecastView.setForecast(forecasts.get(position));
            holder.showText();
            loadDiscriptionData(forecasts.get(position).getCityName());
        }
    }

    @Override
    public void onScrollStart(@NonNull ForecastAdapter.ViewHolder holder, int position) {
        holder.hideText();
    }

    @Override
    public void onScroll(
            float position,
            int currentIndex, int newIndex,
            @Nullable ForecastAdapter.ViewHolder currentHolder,
            @Nullable ForecastAdapter.ViewHolder newHolder) {
        Forecast current = forecasts.get(currentIndex);
        if (newIndex >= 0 && newIndex < cityPicker.getAdapter().getItemCount()) {
            Forecast next = forecasts.get(newIndex);
            //  forecastView.onScroll(1f - Math.abs(position), current, next);
        }
    }

    @Override
    public void onScrollEnd(@NonNull ForecastAdapter.ViewHolder holder, int position) {

    }

    //todo remove#
   /* @OnClick(R.id.btn_share_receive)
    public void goto_share_receive() {
        startActivity(new Intent(this, ActivityShareReceive_.class));
    }*/

    @OnClick(R.id.ib_langChange)
    public void langChangeButtonClick() {
        showLanguageSelectionDialog();
    }

    //todo  remove#
//    @OnClick({R.id.rl_share_app, R.id.btn_share_app})
//    public void share_app() {
//        KotlinPermissions.with(Student_profile_activity.this)
//                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE)
//                .onAccepted(permissionResult -> {
//                    try {
//                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
//                        PackageManager pm = ApplicationClass.getInstance().getPackageManager();
//                        ApplicationInfo ai = pm.getApplicationInfo(ApplicationClass.getInstance().getPackageName(), 0);
//                        File localFile = new File(ai.publicSourceDir);
//                        Uri uri = FileProvider.getUriForFile(Student_profile_activity.this,
//                                BuildConfig.APPLICATION_ID + ".provider", localFile);
//                        intentShareFile.setType("*/*");
//                        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
//                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Please download apk from here...");
//                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.pratham.cityofstories");
//                        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        startActivity(Intent.createChooser(intentShareFile, "Share through"));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                })
//                .ask();
//    }
}
