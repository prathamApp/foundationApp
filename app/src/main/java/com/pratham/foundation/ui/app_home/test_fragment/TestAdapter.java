package com.pratham.foundation.ui.app_home.test_fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.modalclasses.CertificateModelClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.currentLevel;


public class TestAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private int lastPos = -1;
    //private List<ContentView> gamesViewList;
    private List<CertificateModelClass> testList;
    CertificateClicked certificateClicked;
    TestContract.LanguageSpinnerListner languageSpinnerListner;
    public int quesIndex = 0;
    private final String TYPE_HEADER = "Header";
    private final String TYPE_SPINNER = "Spinner";
    private final String TYPE_UPDATE = "Update";
    private final String TYPE_FOOTER = "Footer";
    ArrayAdapter<String> dataAdapter;

    public void initializeIndex() {
        quesIndex = 0;
    }

    public TestAdapter(Context mContext,
                       List<CertificateModelClass> testList,
                       CertificateClicked certificateClicked,
                       TestFragment languageSpinnerListner) {

        this.mContext = mContext;
        this.testList = testList;
        this.certificateClicked = certificateClicked;
        this.languageSpinnerListner = languageSpinnerListner;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View view;
        switch (viewtype) {
            case 0:
                LayoutInflater header = LayoutInflater.from(viewGroup.getContext());
                view = header.inflate(R.layout.list_header, viewGroup, false);
                return new EmptyitemRowHolder(view);
            case 4:
                LayoutInflater footer = LayoutInflater.from(viewGroup.getContext());
                view = footer.inflate(R.layout.test_list_footer, viewGroup, false);
                return new EmptyitemRowHolder(view);
            case 1:
                LayoutInflater folder = LayoutInflater.from(viewGroup.getContext());
                view = folder.inflate(R.layout.certi_row, viewGroup, false);
                return new TestItemRowHolder(view);
            case 2:
                LayoutInflater spinner_layout = LayoutInflater.from(viewGroup.getContext());
                view = spinner_layout.inflate(R.layout.certi_spinner, viewGroup, false);
                return new TestSpinnerHolder(view);
            case 3:
                LayoutInflater update_layout = LayoutInflater.from(viewGroup.getContext());
                view = update_layout.inflate(R.layout.certi_update, viewGroup, false);
                return new TestUpdateHolder(view);
            default:
                return null;

        }
    }

    @Override
    public int getItemCount() {
        return (null != testList ? testList.size() : 0);
    }

    public class EmptyitemRowHolder extends RecyclerView.ViewHolder {

        public EmptyitemRowHolder(View view) {
            super(view);
        }
    }

    public class TestItemRowHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public RelativeLayout certificate_card;
        public RatingBar ratingStars;

        TestItemRowHolder(View view) {
            super(view);
            certificate_card = view.findViewById(R.id.certificate_card_view);
            title = view.findViewById(R.id.assess_data);
            ratingStars = view.findViewById(R.id.ratingStars);
        }
    }

    public class TestSpinnerHolder extends RecyclerView.ViewHolder {
        Spinner spinner;
        TestSpinnerHolder(View view) {
            super(view);
            spinner = view.findViewById(R.id.test_lang_spinner);
        }
    }

    public class TestUpdateHolder extends RecyclerView.ViewHolder {
        Button btn_test_update;
        TestUpdateHolder(View view) {
            super(view);
            btn_test_update = view.findViewById(R.id.btn_test_update);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (testList.get(position).getContentType() != null) {
            switch (testList.get(position).getContentType()) {
                case TYPE_HEADER:
                    return 0;
                case TYPE_SPINNER:
                    return 2;
                case TYPE_UPDATE:
                    return 3;
                case TYPE_FOOTER:
                    return 4;
                default:
                    return 1;
            }
        } else
            return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewitemRowHolder, int position) {

        switch (viewitemRowHolder.getItemViewType()) {
            case 1:
                TestItemRowHolder itemRowHolder = (TestItemRowHolder) viewitemRowHolder;

//                itemRowHolder.certificate_card.setBackground(mContext.getResources().getDrawable(getRandomCardColor()));
                //final ContentView gamesList = gamesViewList.get(position);
                String ques = "";

                if (testList.get(position).getCodeCount() > 1) {
                    quesIndex += 1;
                    ques = "" + quesIndex + ". ";
                } else {
                    //quesIndex = 0;
                    ques = "";
                }

//                itemRowHolder.title.setTypeface(null, Typeface.NORMAL);

                if (!testList.get(position).isAsessmentGiven()) {
                    itemRowHolder.certificate_card.setClickable(true);
                    itemRowHolder.ratingStars.setRating(0);
                    if (TestFragment.language.equalsIgnoreCase("English"))
                        itemRowHolder.title.setText(ques + testList.get(position).getEnglishQues());
                    if (TestFragment.language.equalsIgnoreCase("Hindi"))
                        itemRowHolder.title.setText(ques + testList.get(position).getHindiQues());
                    if (TestFragment.language.equalsIgnoreCase("Marathi"))
                        itemRowHolder.title.setText(ques + testList.get(position).getMarathiQues());
                    if (TestFragment.language.equalsIgnoreCase("Gujarati")) {
                        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/muktavaani_gujarati.ttf");
                        itemRowHolder.title.setTypeface(face);
                        itemRowHolder.title.setText(ques + testList.get(position).getGujaratiQues());
                    }
                    if (TestFragment.language.equalsIgnoreCase("Kannada"))
                        itemRowHolder.title.setText(ques + testList.get(position).getKannadaQues());
                    if (TestFragment.language.equalsIgnoreCase("Bengali"))
                        itemRowHolder.title.setText(ques + testList.get(position).getBengaliQues());
                    if (TestFragment.language.equalsIgnoreCase("Assamese")) {
                        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/lohit_oriya.ttf");
                        itemRowHolder.title.setTypeface(face);
                        itemRowHolder.title.setText(ques + testList.get(position).getAssameseQues());
                    }
                    if (TestFragment.language.equalsIgnoreCase("Telugu"))
                        itemRowHolder.title.setText(ques + testList.get(position).getTeluguQues());
                    if (TestFragment.language.equalsIgnoreCase("Tamil"))
                        itemRowHolder.title.setText(ques + testList.get(position).getTamilQues());
                    if (TestFragment.language.equalsIgnoreCase("Odiya")) {
                        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/lohit_oriya.ttf");
                        itemRowHolder.title.setTypeface(face);
                        itemRowHolder.title.setText(ques + testList.get(position).getOdiaQues());
                    }
                    if (TestFragment.language.equalsIgnoreCase("Malayalam"))
                        itemRowHolder.title.setText(ques + testList.get(position).getUrduQues());
                    if (TestFragment.language.equalsIgnoreCase("Punjabi")) {
                        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/raavi_punjabi.ttf");
                        itemRowHolder.title.setTypeface(face);
                        itemRowHolder.title.setText(ques + testList.get(position).getPunjabiQues());
                    }
                    itemRowHolder.certificate_card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            certificateClicked.onCertificateOpenGame(position, testList.get(position).getNodeId());
                        }
                    });
                } else {
                    itemRowHolder.certificate_card.setClickable(false);
                    itemRowHolder.ratingStars.setVisibility(View.VISIBLE);
                    if (TestFragment.language.equalsIgnoreCase("English"))
                        itemRowHolder.title.setText(ques + testList.get(position).getEnglishAnsw());
                    if (TestFragment.language.equalsIgnoreCase("Hindi"))
                        itemRowHolder.title.setText(ques + testList.get(position).getHindiAnsw());
                    if (TestFragment.language.equalsIgnoreCase("Marathi"))
                        itemRowHolder.title.setText(ques + testList.get(position).getMarathiAnsw());
                    if (TestFragment.language.equalsIgnoreCase("Gujarati")) {
                        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/muktavaani_gujarati.ttf");
                        itemRowHolder.title.setTypeface(face);
                        itemRowHolder.title.setText(ques + testList.get(position).getGujaratiAnsw());
                    }
                    if (TestFragment.language.equalsIgnoreCase("Kannada"))
                        itemRowHolder.title.setText(ques + testList.get(position).getKannadaAnsw());
                    if (TestFragment.language.equalsIgnoreCase("Bengali"))
                        itemRowHolder.title.setText(ques + testList.get(position).getBengaliAnsw());
                    if (TestFragment.language.equalsIgnoreCase("Assamese")) {
                        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/geetl_assamese.ttf");
                        itemRowHolder.title.setTypeface(face);
                        itemRowHolder.title.setText(ques + testList.get(position).getAssameseAnsw());
                    }
                    if (TestFragment.language.equalsIgnoreCase("Telugu"))
                        itemRowHolder.title.setText(ques + testList.get(position).getTeluguAnsw());
                    if (TestFragment.language.equalsIgnoreCase("Tamil"))
                        itemRowHolder.title.setText(ques + testList.get(position).getTamilAnsw());
                    if (TestFragment.language.equalsIgnoreCase("Odia")) {
                        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/lohit_oriya.ttf");
                        itemRowHolder.title.setTypeface(face);
                        itemRowHolder.title.setText(ques + testList.get(position).getOdiaAnsw());
                    }
                    if (TestFragment.language.equalsIgnoreCase("Malayalam"))
                        itemRowHolder.title.setText(ques + testList.get(position).getUrduAnsw());
                    if (TestFragment.language.equalsIgnoreCase("Punjabi")) {
                        Typeface face = Typeface.createFromAsset(mContext.getAssets(), "fonts/raavi_punjabi.ttf");
                        itemRowHolder.title.setTypeface(face);
                        itemRowHolder.title.setText(ques + testList.get(position).getPunjabiAnsw());
                    }
                    itemRowHolder.ratingStars.setRating(testList.get(position).getCertificateRating());
                }
                setAnimations(itemRowHolder.certificate_card);
                break;
            case 2:
                TestSpinnerHolder testSpinnerHolder = (TestSpinnerHolder) viewitemRowHolder;
                if(dataAdapter == null) {
                    dataAdapter = new ArrayAdapter<>(mContext,
                            R.layout.custom_spinner, mContext.getResources().getStringArray(R.array.certificate_Languages));
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    testSpinnerHolder.spinner.setAdapter(dataAdapter);
                }
                testSpinnerHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        languageSpinnerListner.onSpinnerLanguageChanged(testSpinnerHolder.spinner.
                                getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                break;
            case 3:
                TestUpdateHolder testUpdateHolder = (TestUpdateHolder) viewitemRowHolder;
                testUpdateHolder.btn_test_update.setText("UPDATE "+testList.get(position).getEnglishQues());
                testUpdateHolder.btn_test_update.setOnClickListener(v ->certificateClicked.onCertificateUpdate() );
                break;
        }

    }

    private JSONArray getTestData(String jsonName) {
        JSONArray returnCodeList = null;
        try {
            InputStream is = mContext.getAssets().open(jsonName);
//            InputStream is = new FileInputStream(ApplicationClass.pradigiPath + "/.FCA/"+FastSave.getInstance().getString(FC_Constants.LANGUAGE, FC_Constants.HINDI)+"/Game/CertificateData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonStr = new String(buffer);
            JSONObject jsonObj = new JSONObject(jsonStr);
            returnCodeList = jsonObj.getJSONArray("CodeList");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return returnCodeList;
    }

    private String getLevelWiseJson() {
        String jsonName = "TestBeginnerJson.json";
        switch (currentLevel) {
            case 0:
                jsonName = "TestBeginnerJson.json";
                break;
            case 1:
                jsonName = "TestSubJuniorJson.json";
                break;
            case 2:
                jsonName = "TestJuniorJson.json";
                break;
            case 3:
                jsonName = "TestSubSeniorJson.json";
                break;
        }
        return jsonName;
    }

    private void setAnimations(final View content_card_view) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.item_fall_down);
        animation.setDuration(500);
            content_card_view.setVisibility(View.VISIBLE);
            content_card_view.setAnimation(animation);
    }

}