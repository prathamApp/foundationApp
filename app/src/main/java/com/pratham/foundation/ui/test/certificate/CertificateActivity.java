package com.pratham.foundation.ui.test.certificate;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.R;
import com.pratham.foundation.ui.contentPlayer.web_view.WebViewActivity;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.modalclasses.CertificateModelClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.utility.FC_Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pratham.foundation.ui.contentPlayer.web_view.WebViewActivity.gameLevel;


public class CertificateActivity extends BaseActivity implements CertificateContract.CertificateView, CertificateClicked, AdapterView.OnItemSelectedListener {

    /*
        @BindView(R.id.btn_english)
        Button btn_english;
        @BindView(R.id.btn_hindi)
        Button btn_hindi;
        @BindView(R.id.btn_marathi)
        Button btn_marathi;
    */
    @BindView(R.id.lang_certi_spinner)
    Spinner lang_certi_spinner;
    @BindView(R.id.iv_photo)
    ImageView iv_photo;

    @BindView(R.id.iv_certificate)
    ImageView iv_certificate;
    @BindView(R.id.rl_supervisedby)
    RelativeLayout rl_supervisedby;

    @BindView(R.id.tv_studentName)
    TextView tv_studentName;
    @BindView(R.id.tv_level)
    TextView tv_certi_level;
    @BindView(R.id.tv_supervisor_name)
    TextView tv_supervisor_name;

    CertificateContract.CertificatePresenter presenter;
    public static Assessment assessmentProfile;
    CertificateAdapter certificateAdapter;
    private RecyclerView recyclerView;
    List<CertificateModelClass> ContentTableList;
    JSONArray certiData;
    int clicked_Pos = 0;
    static String certificateLanguage;
    String level_lbl = "", certificate_lbl = "", supervisorName_lbl, supervisorPhoto;
    String allCodes[], nodeId, CertiTitle, certiMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        nodeId = getIntent().getStringExtra("nodeId");
        CertiTitle = getIntent().getStringExtra("CertiTitle");
        certiMode = getIntent().getStringExtra("display");
        assessmentProfile = (Assessment) getIntent().getSerializableExtra("assessment");
        //  tv_certi_level.setText(CertiTitle);

        certificateLanguage = "English";

        ContentTableList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.assessment_recycler);
        certificateAdapter = new CertificateAdapter(this, ContentTableList, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(certificateAdapter);
        presenter = new CertificatePresenter(CertificateActivity.this, this);

        presenter.getStudentName(certiMode);

        if (FC_Constants.supervisedAssessment)
            presenter.getSupervisorData(certiMode);
        else {
            rl_supervisedby.setVisibility(View.GONE);
        }

        certiData = presenter.fetchAssessmentList(CertiTitle);

        if (!certiMode.equalsIgnoreCase("display")) {
            presenter.proceed(certiData, nodeId);
        } else {

            if (CertiTitle.equalsIgnoreCase("0"))
                tv_certi_level.setText("Beginner");
            else if (CertiTitle.equalsIgnoreCase("1"))
                tv_certi_level.setText("Sub junior");
            else if (CertiTitle.equalsIgnoreCase("2"))
                tv_certi_level.setText("Junior");
            else if (CertiTitle.equalsIgnoreCase("3"))
                tv_certi_level.setText("Sub Senior");
            if (!assessmentProfile.getDeviceIDa().equalsIgnoreCase("na")) {
                rl_supervisedby.setVisibility(View.VISIBLE);
                presenter.getSupervisorData(certiMode);
            } else
                rl_supervisedby.setVisibility(View.GONE);

            presenter.fillAdapter(assessmentProfile, certiData);
        }
        if (FC_Constants.GROUP_LOGIN) {
        }

        lang_certi_spinner.setOnItemSelectedListener(this);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, getResources().getStringArray(R.array.certificate_Languages));
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        lang_certi_spinner.setAdapter(dataAdapter);

    }

    @Override
    public void setStudentName(String studName) {
        tv_studentName.setText("" + studName);
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
        certificateAdapter.initializeIndex();
    }

    @Override
    public void notifyAdapter() {
        certificateAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSupervisorData(String sName, String sImage) {
        supervisorName_lbl = sName;
        supervisorPhoto = sImage;
        try {
            Bitmap bmImg = BitmapFactory.decodeFile(sImage);
            BitmapFactory.decodeStream(new FileInputStream("" + sImage));
            iv_photo.setImageBitmap(bmImg);
            tv_supervisor_name.setText(supervisorName_lbl);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
/*    public float[] getAllPercentages() {
        float starRatings[];
        starRatings = new float[certificateModelClassList.size()];
        for (int i = 0; i < certificateModelClassList.size(); i++) {
            starRatings[i] = getStarRating(((float) certificateModelClassList.get(i).getScoredMarks() / (float) certificateModelClassList.get(i).getTotalMarks()) * 100);
        }
        return starRatings;
    }
    public String[] getAllCodes() {
        String allCodes[];
        allCodes = new String[certificateModelClassList.size()];
        for (int i = 0; i < certificateModelClassList.size(); i++) {
            allCodes[i] = certificateModelClassList.get(i).getCertiCode();
        }
        return allCodes;
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        boolean testGiven = true;

        if (!certiMode.equalsIgnoreCase("display")) {
            JSONObject jsonObjectAssessment = new JSONObject();
            for (int i = 0; i < ContentTableList.size(); i++) {
                try {
                    if (ContentTableList.get(i).isAsessmentGiven()) {
                        jsonObjectAssessment.put("CertCode" + i + "_" + ContentTableList.get(i).getCertiCode(), "" + ContentTableList.get(i).getStudentPercentage());
                    } else {
                        testGiven = false;
                        break;
                    }
                    //question
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (testGiven) {
                presenter.recordTestData(jsonObjectAssessment, CertiTitle);
            }
        }
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
        if (!ApplicationClass.isTablet)
            resPath = ApplicationClass.foundationPath + "/.LLA/English/Game/" +
                    ContentTableList.get(position).getResourcePath();
        else
            resPath = ApplicationClass.contentSDPath + "/.LLA/English/Game/" +
                    ContentTableList.get(position).getResourcePath();
        File file = new File(resPath);
        Uri path = Uri.fromFile(file);

        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("resPath", path.toString());
        intent.putExtra("resId", gameID);
        intent.putExtra("mode", "test");
        intent.putExtra("gameLevel", gameLevel);

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
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
        certificateLanguage = "English";
        certificateAdapter.initializeIndex();
        certificateAdapter.notifyDataSetChanged();
    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + avatar) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + avatar) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}



//beginner
/*,
        {
        "lang": "Malayalam",
        "questionList": {
        "1": "ഇതിൽ ശിശുഗാനം ഏതെന്നു തിരിച്ചറിയുക?",
        "2": "ചിത്രം തിരിച്ചറിയുക?",
        "3": "പറയുന്നത് ശ്രദ്ധിച്ചു ഉത്തരം നൽകാമോ",
        "4": "താങ്കൾക്ക് സല്ലപിക്കാൻ കഴിയുമോ?",
        "5": "Can you read alphabets?",
        "6": "വാക്കുകൾ കണ്ടെത്താൻ താങ്കൾക്ക് അറിയാമോ?",
        "7": "താങ്കൾക്ക് പ്രാസം കണ്ടെത്താൻ കഴിയുമോ",
        "8": "താങ്കൾക്ക് പ്രാസം ഉണ്ടാക്കാൻ കഴിയുമോ"
        },
        "answerList": {
        "1": "Can identify nursery rhymes.",
        "2": "Can identify pictures.",
        "3": "Can listen and answer.",
        "4": "Can chat.",
        "5": "Can read alphabets.",
        "6": "Can find words.",
        "7": "Can find rhyming words.",
        "8": "Can  make rhyming words."
        }
        },
        {
        "lang": "Punjabi",
        "questionList": {
        "1": "ਕੀ ਤੁਸੀਂ ਅੰਗਰੇਜ਼ੀ ਕਵਿਤਾਵਾਂ ਪਹਿਚਾਣ ਸਕਦੇ ਹੋ?",
        "2": "ਕੀ ਤੁਸੀਂ ਤਸਵੀਰਾਂ ਦੀ ਪਹਿਚਾਣ ਕਰ ਸਕਦੇ ਹੋ?",
        "3": "ਕੀ ਤੁਸੀਂ ਸੁਣ ਕੇ ਜਵਾਬ ਦੇ ਸਕਦੇ ਹੋ?",
        "4": "ਕੀ ਤੁਸੀਂ ਗੱਲਬਾਤ ਕਰ ਸਕਦੇ ਹੋ?",
        "5": "ਪ੍ਰਸ਼ਨ: ਕੀ ਤੁਸੀਂ ਅੱਖਰ ਪੜ੍ਹ ਸਕਦੇ ਹੋ?",
        "6": "ਕੀ ਤੁਸੀਂ ਸ਼ਬਦ ਲੱਭ ਸਕਦੇ ਹੋ?",
        "7": "ਕੀ ਤੁਸੀਂ ਲੈ ਬੱਧ ਲਿਖੇ ਸ਼ਬਦ ਲੱਭ ਸਕਦੇ ਹੋ?",
        "8": "ਕੀ ਤੁਸੀਂ ਲੈ ਬੱਧ ਸ਼ਬਦ ਬਣਾ ਸਕਦੇ ਹੋ?"
        },
        "answerList": {
        "1": "Can identify nursery rhymes.",
        "2": "Can identify pictures.",
        "3": "Can listen and answer.",
        "4": "Can chat.",
        "5": "Can read alphabets.",
        "6": "Can find words.",
        "7": "Can find rhyming words.",
        "8": "Can  make rhyming words."
        }
        }*/


/*
Junior
,
    {
      "lang": "Malayalam",
      "questionList": {
        "1": "താങ്കൾക്ക് ഒരു കഥ വായിക്കാൻ കഴിയുമോ?",
        "2": "താങ്കൾക്ക് മുൻപദം അറിയാമോ?",
        "3": "താങ്കൾക്ക് നാമ വിശേഷണങ്ങൾ അറിയാമോ?",
        "4": "താങ്കൾക്ക് സല്ലപിക്കാൻ കഴിയുമോ?",
        "5": "വാക്കുകൾ കണ്ടെത്താൻ താങ്കൾക്ക് അറിയാമോ?",
        "6": "താങ്കൾക്ക് വാചകങ്ങൾ വായിക്കാൻ കഴിയുമോ?",
        "7": "താങ്കൾക്ക് വാക്കുകൾ ഉണ്ടാക്കാൻ കഴിയുമോ?",
        "8": "താങ്കൾക്ക് വാചകങ്ങൾ ഉണ്ടാക്കാൻ കഴിയുമോ?",
        "9": "താങ്കൾക്ക് ഒരു ഖണ്ഡിക വായിക്കാൻ കഴിയുമോ?"
      },
      "answerList": {
        "1": "Can read a story.",
        "2": "know prepositions.",
        "3": "know adjectives.",
        "4": "Can chat.",
        "5": "Can find words.",
        "6": "Can read sentences.",
        "7": "Can make words.",
        "8": "Can  make sentences.",
        "9": "Can  read a simple paragraph."
      }
    },
    {
      "lang": "Punjabi",
      "questionList": {
        "1": "ਕੀ ਤੁਸੀਂ ਕੋਈ ਇੱਕ ਕਹਾਣੀ ਪੜ੍ਹ ਸਕਦੇ ਹੋ?",
        "2": "ਕੀ ਤੁਸੀਂ ਯੋਜਕ ਜਾਣਦੇ ਹੋ?",
        "3": "ਕੀ ਤੁਸੀਂ ਵਿਸ਼ੇਸ਼ਣ ਜਾਣਦੇ ਹੋ?",
        "4": "ਕੀ ਤੁਸੀਂ ਗੱਲਬਾਤ ਕਰ ਸਕਦੇ ਹੋ?",
        "5": "ਕੀ ਤੁਸੀਂ ਸ਼ਬਦ ਲੱਭ ਸਕਦੇ ਹੋ?",
        "6": "ਕੀ ਤੁਸੀਂ ਵਾਕ ਪੜ੍ਹ ਸਕਦੇ ਹੋ?",
        "7": "ਕੀ ਤੁਸੀਂ ਸ਼ਬਦ ਬਣਾ ਸਕਦੇ ਹੋ?",
        "8": "ਕੀ ਤੁਸੀਂ ਵਾਕ ਬਣਾ ਸਕਦੇ ਹੋ?",
        "9": "ਕੀ ਤੁਸੀਂ ਇੱਕ ਸਧਾਰਨ ਪੈਰਾ ਪੜ੍ਹ ਸਕਦੇ ਹੋ?"
      },
      "answerList": {
        "1": "Can read a story.",
        "2": "know prepositions.",
        "3": "know adjectives.",
        "4": "Can chat.",
        "5": "Can find words.",
        "6": "Can read sentences.",
        "7": "Can make words.",
        "8": "Can  make sentences.",
        "9": "Can  read a simple paragraph."
      }
    }*/
/*,{
        "lang": "Malayalam",
        "questionList": {
        "1": "Can you identify Nursery Rhymes?",
        "2": "Can you identify pictures?",
        "3": "Can you tell the date and time?",
        "4": "Can you listen and answer?",
        "5": "Can you chat?",
        "6": "Can you identify blends?",
        "7": "Can you find words?",
        "8": "Can you find rhyming words?",
        "9": "Can you make rhyming words?"
        },
        "answerList": {
        "1": "Can identify nursery rhymes.",
        "2": "Can identify pictures.",
        "3" :"Can tell the date and time",
        "4": "Can listen and answer.",
        "5": "Can chat.",
        "6": "Can identify blends.",
        "7": "Can find words.",
        "8": "Can find rhyming words.",
        "9": "Can make rhyming words."
        }
        },{
        "lang": "Punjabi",
        "questionList": {
        "1": "Can you identify Nursery Rhymes?",
        "2": "Can you identify pictures?",
        "3": "Can you tell the date and time?",
        "4": "Can you listen and answer?",
        "5": "Can you chat?",
        "6": "Can you identify blends?",
        "7": "Can you find words?",
        "8": "Can you find rhyming words?",
        "9": "Can you make rhyming words?"
        },
        "answerList": {
        "1": "Can identify nursery rhymes.",
        "2": "Can identify pictures.",
        "3" :"Can tell the date and time",
        "4": "Can listen and answer.",
        "5": "Can chat.",
        "6": "Can identify blends.",
        "7": "Can find words.",
        "8": "Can find rhyming words.",
        "9": "Can make rhyming words."
        }
        }*/

/*
,
        {
        "lang": "Malayalam",
        "questionList": {
        "1": "താങ്കൾക്ക് ഒരു കഥ വായിക്കാൻ കഴിയുമോ?",
        "2": "താങ്കൾക്ക് ചെറു കഥ മനസിലാക്കാൻ കഴിയുമോ",
        "3": "താങ്കൾക്ക് ചെറു കഥ വായിക്കാൻ കഴിയുമോ",
        "4": "താങ്കൾക്ക് സല്ലപിക്കാൻ കഴിയുമോ?"
        },
        "answerList": {
        "1": "Can read a story.",
        "2": "understands short story",
        "3": "Can read a short story.",
        "4": "Can chat."
        }
        },
        {
        "lang": "Punjabi",
        "questionList": {
        "1": "ਕੀ ਤੁਸੀਂ ਕੋਈ ਕਹਾਣੀ ਪੜ੍ਹ ਸਕਦੇ ਹੋ?",
        "2": "ਕੀ ਤੁਸੀਂ ਇੱਕ ਛੋਟੀ ਕਹਾਣੀ ਸਮਝ ਸਕਦੇ ਹੋ?",
        "3": "ਕੀ ਤੁਸੀਂ ਛੋਟੀ ਕਹਾਣੀ ਪੜ੍ਹ ਸਕਦੇ ਹੋ?",
        "4": "ਕੀ ਤੁਸੀਂ ਗੱਲਬਾਤ ਕਰ ਸਕਦੇ ਹੋ?"
        },
        "answerList": {
        "1": "Can read a story.",
        "2": "understands short story",
        "3": "Can read a short story.",
        "4": "Can chat."

        }
        }
*/
