package com.pratham.foundation.ui.test.certificate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ezydev.bigscreenshot.BigScreenshot;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.GridSpacingItemDecoration;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.fontsview.SansButton;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.Student;
import com.pratham.foundation.modalclasses.CertificateModelClass;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.app_home.test_fragment.CertificateClicked;
import com.pratham.foundation.ui.contentPlayer.web_view.WebViewActivity_;
import com.pratham.foundation.utility.FC_Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pratham.foundation.ui.contentPlayer.web_view.WebViewActivity.gameLevel;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_STUDENT_ID;
import static com.pratham.foundation.utility.FC_Constants.HINDI;
import static com.pratham.foundation.utility.FC_Constants.activityPDFPath;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Utility.dpToPx;
import static com.pratham.foundation.utility.FC_Utility.getLevelWiseJson;

@EActivity(R.layout.activity_certificate)
public class CertificateActivity extends BaseActivity implements CertificateContract.CertificateView,
        CertificateClicked, AdapterView.OnItemSelectedListener,
        BigScreenshot.ProcessScreenshot {

    @Bean(CertificatePresenter.class)
    CertificateContract.CertificatePresenter presenter;

    @ViewById(R.id.lang_certi_spinner)
    Spinner lang_certi_spinner;
    @ViewById(R.id.iv_certificate)
    ImageView iv_certificate;
    @ViewById(R.id.main_certi_layout)
    RelativeLayout main_certi_layout;

    @ViewById(R.id.tv_studentName)
    TextView tv_studentName;
    @ViewById(R.id.tv_level)
    TextView tv_certi_level;
    @ViewById(R.id.pdf_page_btn)
    Button pdf_page_btn;
    @ViewById(R.id.tv_supervisor_name)
    TextView tv_supervisor_name;
    @ViewById(R.id.assessment_recycler)
    RecyclerView assessment_recycler;

    public static Assessment assessmentProfile;
    CertificateAdapter certificateAdapter;
    List<CertificateModelClass> ContentTableList;
    int clicked_Pos = 0;
    public static String certificateLanguage;
    String level_lbl = "", certificate_lbl = "", supervisorName_lbl, supervisorPhoto;
    String[] allCodes;
    String nodeId, CertiTitle, CertiCode, certiMode, timeStamp, cTitle, pdfName,cLevel,cSubject;
    Context context;

    @AfterViews
    protected void initiate() {

        context = CertificateActivity.this;
        nodeId = getIntent().getStringExtra("nodeId");
        CertiCode = getIntent().getStringExtra("CertiCode");
        CertiTitle = getIntent().getStringExtra("CertiTitle");
        cTitle = getIntent().getStringExtra("cTitle");
        cLevel = getIntent().getStringExtra("cLevel");
        cSubject = getIntent().getStringExtra("cSubject");
        timeStamp = getIntent().getStringExtra("TimeStamp");
        certiMode = getIntent().getStringExtra("display");
        assessmentProfile = (Assessment) getIntent().getSerializableExtra("assessment");

        presenter.setView(CertificateActivity.this);

        certificateLanguage = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, HINDI);

        ContentTableList = new ArrayList<>();

        Student student;
        String sId = assessmentProfile.getStudentIDa();
        student = AppDatabase.getDatabaseInstance(context).getStudentDao().getStudent(sId);
        String studName = "" + student.getFullName();

        tv_certi_level.setText(Html.fromHtml("<b><i><font color=\"#00c853\">"
                +studName+"</font></i></b> has completed<br><b>"+cTitle+"</b> successfully"));
        String dateStamp = timeStamp.split(" ")[0];
        tv_studentName.setText(Html.fromHtml("on "+dateStamp+" using<br>'"+getResources().getString(R.string.app_name)+"' app<br>"
                +"by <i>Pratham Education Foundation</i>."));
        pdfName = sId+"_"+cTitle+"_"+timeStamp+".pdf";
        String jsonName = getLevelWiseJson(Integer.parseInt(cLevel));
        String[] testData = presenter.getTestData(jsonName);

        lang_certi_spinner.setOnItemSelectedListener(this);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, testData);
//                getResources().getStringArray(R.array.certificate_Languages));
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner);
        // attaching data adapter to spinner
        lang_certi_spinner.setAdapter(dataAdapter);

        presenter.fillAdapter(assessmentProfile, CertiTitle);
    }

    @Override
    public void setStudentName(String studName) {
    }

    @Override
    public void addContentToViewList(CertificateModelClass contentTable) {
        ContentTableList.add(contentTable);
    }

    @Override
    public void doubleQuestionCheck() {
        for (int i = 0; i < ContentTableList.size(); i++) {
            for (int j = i + 1; j < ContentTableList.size(); j++) {
                if (ContentTableList.get(i).getCertiCode().equalsIgnoreCase(ContentTableList.get(j).getCertiCode())) {
                    ContentTableList.get(i).setCodeCount(ContentTableList.get(i).getCodeCount() + 1);
                }
            }
        }
    }

    @Override
    public void initializeTheIndex() {
    }

    @Override
    public void notifyAdapter() {
        if(certificateAdapter==null){
            certificateAdapter = new CertificateAdapter(this, ContentTableList, this);
            certificateAdapter.initializeIndex();
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            assessment_recycler.setLayoutManager(mLayoutManager);
            assessment_recycler.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(this, 5), true));
            assessment_recycler.setItemAnimator(new DefaultItemAnimator());
            assessment_recycler.setAdapter(certificateAdapter);
        }else {
            certificateAdapter.initializeIndex();
            certificateAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCertificateUpdate() { }

    @Override
    public void openAssessmentApp() { }

    @Click(R.id.main_back)
    public void pressedBack() {
        onBackPressed();
    }

    boolean pdfPressed = false;
    BigScreenshot longScreenshot;

    @Click(R.id.pdf_page_btn)
    public void SharePDFClicked() {
        if (!pdfPressed) {
//        showLoader();
            pdfPressed = true;
            Log.d("SharePDF", "SharePDFClicked: AAAAAAAAAAAAAAAAAAAAAA");
            activityPDFPath = Environment.getExternalStorageDirectory().toString() + "/.FCAInternal/StudentPDFs/"
                    + FastSave.getInstance().getString(CURRENT_STUDENT_ID, "") + "/";
            if (!new File(activityPDFPath).exists())
                new File(activityPDFPath).mkdir();
            pdf_page_btn.setText("STOP");
//        takeSS(assessment_recycler);
            // Main container which screenshot is to be taken - main_certi_layout
            // assessment_recycler for scrolling screenshot.
            longScreenshot = new BigScreenshot(this, assessment_recycler, main_certi_layout);
            longScreenshot.startScreenshot();
            new Handler().postDelayed(() -> pdf_page_btn.performClick(),45);
        } else {
            pdfPressed = false;
            longScreenshot.stopScreenshot();
            pdf_page_btn.setText("PDF");
        }
    }

    private boolean loaderVisible = false;
    private CustomLodingDialog myLoadingDialog;

    public void showLoader() {
        if (!loaderVisible) {
            loaderVisible = true;
            myLoadingDialog = new CustomLodingDialog(context);
            myLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(myLoadingDialog.getWindow()).
                    setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myLoadingDialog.setContentView(R.layout.loading_dialog);
            myLoadingDialog.setCanceledOnTouchOutside(false);
//        myLoadingDialog.setCancelable(false);
            myLoadingDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        try {
            loaderVisible = false;
            new Handler().postDelayed(() -> {
                if (myLoadingDialog != null && myLoadingDialog.isShowing())
                    myLoadingDialog.dismiss();
            }, 150);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ShowPreviewDialog(Bitmap image) {
        Log.d("SharePDF", "ShowPreviewDialog: CCCCCCCCCCCCCCCCCCCCCCCCC");
        final CustomLodingDialog dialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_image_preview_dialog);
        dialog.setCanceledOnTouchOutside(false);
        ImageView iv_dia_preview = dialog.findViewById(R.id.iv_dia_preview);
        SansButton dia_btn_cross = dialog.findViewById(R.id.dia_btn_cross);
        ImageButton camera = dialog.findViewById(R.id.camera);

        dialog.show();

        iv_dia_preview.setImageBitmap(image);
        dia_btn_cross.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    public void getScreenshot(Bitmap bitmap) {
        Log.d("SharePDF", "getScreenshot: BBBBBBBBBBBBBBBBBBBBBBBBB");
/*
        ShowPreviewDialog(bitmap);
        try {
            Date now = new Date();
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
            String mPath = activityPDFPath + now + ".jpeg";
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(myPageInfo);

        page.getCanvas().drawBitmap(bitmap, 0, 0, null);
        pdfDocument.finishPage(page);
//        activityPDFPath = activityPDFPath + "" + pdfName;
        activityPDFPath = ApplicationClass.foundationPath + "" + pdfName;
        File myPDFFile = new File(activityPDFPath);
        try {
            pdfDocument.writeTo(new FileOutputStream(myPDFFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();

        if (myPDFFile.exists()) {
            Uri uri = Uri.fromFile(myPDFFile);
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share via"));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onCertificateClicked(int position, String nodeId) {
    }

    @Override
    public void onCertificateOpenGame(int position, String nodeId) {
        String resPath = null;
        clicked_Pos = position;
        String gameID = ContentTableList.get(position).getResourceId();
        gameLevel = ContentTableList.get(position).getNodeAge();
        if (!ApplicationClass.getAppMode())
            resPath = ApplicationClass.foundationPath + gameFolderPath + "/" +
                    ContentTableList.get(position).getResourcePath();
        else
            resPath = ApplicationClass.contentSDPath + gameFolderPath + "/" +
                    ContentTableList.get(position).getResourcePath();
        File file = new File(resPath);
        Uri path = Uri.fromFile(file);

        Intent intent = new Intent(this, WebViewActivity_.class);
        intent.putExtra("resPath", path.toString());
        intent.putExtra("resId", gameID);
        intent.putExtra("mode", "test");
        intent.putExtra("gameLevel", gameLevel);

        startActivityForResult(intent, 1461);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1461) {
            if (resultCode == Activity.RESULT_OK) {
                String cCode = data.getStringExtra("cCode");
                int tMarks = data.getIntExtra("tMarks", 0);
                int sMarks = data.getIntExtra("sMarks", 0);

                try {
                    if (cCode.equalsIgnoreCase(ContentTableList.get(clicked_Pos).getCertiCode())) {
                        ContentTableList.get(clicked_Pos).setAsessmentGiven(true);
                        ContentTableList.get(clicked_Pos).setTotalMarks(tMarks);
                        ContentTableList.get(clicked_Pos).setScoredMarks(sMarks);
                        float perc = ((float) sMarks / (float) tMarks) * 100;
                        ContentTableList.get(clicked_Pos).setStudentPercentage("" + perc);
                        ContentTableList.get(clicked_Pos).setCertificateRating(presenter.getStarRating(perc));

                        certificateAdapter.notifyItemChanged(clicked_Pos, ContentTableList.get(clicked_Pos));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        certificateLanguage = parent.getItemAtPosition(position).toString();
        level_lbl = "Level ";
        iv_certificate.setImageResource(R.drawable.certifcate_eng);
        certificateAdapter.initializeIndex();
        certificateAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        iv_certificate.setImageResource(R.drawable.certifcate_eng);
        certificateLanguage = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, HINDI);
        certificateAdapter.initializeIndex();
        certificateAdapter.notifyDataSetChanged();
    }
}