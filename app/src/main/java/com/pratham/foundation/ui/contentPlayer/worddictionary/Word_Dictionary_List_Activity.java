package com.pratham.foundation.ui.contentPlayer.worddictionary;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pratham.foundation.ApplicationClass;
import com.pratham.foundation.BaseActivity;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.customView.fontsview.SansButton;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.modalclasses.WordDictionaryModel;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.admin_panel.tab_usage.ContractOptions;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;


@EActivity(R.layout.word_dictionary_list)
public  class Word_Dictionary_List_Activity extends BaseActivity
        implements
        Word_Dictionary_Contract.Word_Dictionary_View, RecyclerClicked {

    @Bean(Word_Dictionary_Presenter.class)
    Word_Dictionary_Contract.Word_Dictionary_Presenter presenter;


    @ViewById(R.id.title)
    TextView title;


    @ViewById(R.id.rv_word)
    RecyclerView rv_word;






    public List<String> ModelWordList;
    public List<WordDictionaryModel> ModelCharList;
    public List<String> ModelLanguageList;

    RelativeLayout.LayoutParams viewParam;
    WordListAdapter wordListAdapter;

    private boolean onSdCard;
    public int current_no = 0;
    private final boolean isKeyWordShowing = false;
    private int index = 0;
    String readingContentPath, contentPath, contentTitle, StudentID, resId, paraAudio,
            useText, englishWord, startPlayBack, resStartTime, certiCode, resType;

    Activity activity;
    int  no_word_answer = 0;
    public  MediaPlayer mediaPlayer = null;

    @SuppressLint("SetTextI18n")
    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


        activity = this;
        presenter.setView(Word_Dictionary_List_Activity.this);

        Intent intent = getIntent();
        contentTitle = getIntent().getStringExtra("contentTitle");
        contentPath = intent.getStringExtra("contentPath");
        StudentID = intent.getStringExtra("StudentID");
        resId = intent.getStringExtra("resId");
        certiCode = intent.getStringExtra("certiCode");
        resType = intent.getStringExtra("resType");
        onSdCard = getIntent().getBooleanExtra("onSdCard", false);

        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.setResId(resId);

        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";


        //showPlayDialog("word_dictionary");

        try {

            presenter.fetchCharData(readingContentPath, resType);


        } catch (Exception e) {
            e.printStackTrace();
        }




    }



    @UiThread
    @Override
    public void setListData(List<String> paraDataList) {

    }


    @UiThread
    @Override
    public void setCharListData(List<WordDictionaryModel> paraDataList) {
        ModelCharList = paraDataList;

    }

    @UiThread
    @Override
    public void setLangListData(List<String> paraDataList) {

    }



    @UiThread
    @Override
    public void initializeContent() {

        if (wordListAdapter == null) {
            wordListAdapter = new WordListAdapter(Word_Dictionary_List_Activity.this,ModelCharList,this );
            rv_word.setLayoutManager(new GridLayoutManager(this, 5));
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL
//                    , false);

           //rv_word.setLayoutManager(linearLayoutManager);
            rv_word.setAdapter(wordListAdapter);
        } else
            wordListAdapter.updateItems(ModelCharList);
        if (ModelCharList.size() > 0) rv_word.smoothScrollToPosition(0);

    }

    @UiThread
    @Override
    public void initializeLangContent() {





    }




    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        showExitDialog();
    }


    @Override
    public void onStart() {
        super.onStart();

    }
    @Override
    public void onStop() {

        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {

        // GameConstatnts.showGameInfo(activity, questionModel.getInstruction(),readingContentPath+questionModel.getInstructionUrl());
        Toast.makeText(activity, event.getMessage()+"---", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnItemClcick(int position, Object object) {

        Gson gson=new Gson();
        WordDictionaryModel selectd_data = (WordDictionaryModel) object;
        Log.i("OnItemClcick",position+"-"+selectd_data.getCh());
        Intent mainNew1 = new Intent(Word_Dictionary_List_Activity.this, Word_Dictionary_Activity_.class);
        mainNew1.putExtra("sdStatus", "F");
        mainNew1.putExtra("json_data",gson.toJson(selectd_data));
        startActivity(mainNew1);
        /*InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);

        imeManager.showInputMethodPicker();*/
    }

    @UiThread
    @Override
    public void nextbuttonaction() {

    }

    public void next_method()
    {

    }

    private void setAnimations(final View v) {
        final Animation animation;
        animation = AnimationUtils.loadAnimation(this, R.anim.item_fall_down);
        animation.setDuration(500);
        v.setAnimation(animation);
    }

    private void showExitDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText(getResources().getString(R.string.yes));
        //dia_btn_red.setText("No");
        dia_btn_red.setVisibility(View.GONE);
        dia_btn_yellow.setText(getResources().getString(R.string.Cancel));
        dia_title.setText(getResources().getString(R.string.exit_dialog_msg));
        dialog.show();

        dia_btn_green.setOnClickListener(v -> {
            dialog.dismiss();
            finish();

        });

        dia_btn_red.setOnClickListener(v -> dialog.dismiss());

        dia_btn_yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
    private void showNextDialog() {
        final CustomLodingDialog dialog = new CustomLodingDialog(this, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);
        dia_title.setText(getResources().getString(R.string.Do_you_want_to_exit_continue));
        dia_btn_green.setText(getResources().getString(R.string.continues_txt));

        dia_btn_red.setVisibility(View.VISIBLE);
        dia_btn_yellow.setVisibility(View.GONE);
        Locale locale=new Locale("en");
        dia_btn_red.setText(getResources().getString(R.string.Exit));
        dia_btn_red.setTextLocale(locale);

        dialog.show();

        dia_btn_green.setOnClickListener(v -> {
            dialog.dismiss();
            next_method();

        });

        dia_btn_yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dia_btn_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

    }

    private void showPlayDialog(String resorcetype) {

        final CustomLodingDialog dialog = new CustomLodingDialog(this, R.style.FullScreenCustomDialogStyle);
        dialog.setContentView(R.layout.activity_instructions_dialog);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setCancelable(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);

        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);

        dia_btn_green.setOnClickListener(v -> {
            dialog.dismiss();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            try {



            } catch (Exception e) {
                e.printStackTrace();
            }
        });



        dialog.getWindow().setDimAmount(.7f);
        String def_lang = FastSave.getInstance().getString(FC_Constants.APP_LANGUAGE, FC_Constants.HINDI);
        Log.d("INSTRUCTIONFRAG4", "Select Subj: " + def_lang);
        FC_Utility.setAppLocal(activity, def_lang);


            dia_title.setText(getResources().getString(R.string.word_dictionary_title));

        dialog.show();
        if (def_lang.equalsIgnoreCase(FC_Constants.ASSAMESE))
            resorcetype = "as_" + resorcetype;
        else if (def_lang.equalsIgnoreCase(FC_Constants.BENGALI))
            resorcetype = "bn_" + resorcetype;
        else if (def_lang.equalsIgnoreCase(FC_Constants.GUJARATI))
            resorcetype = "gu_" + resorcetype;
        else if (def_lang.equalsIgnoreCase(FC_Constants.HINDI))
            resorcetype = "hi_" + resorcetype;
        else if (def_lang.equalsIgnoreCase(FC_Constants.KANNADA))
            resorcetype = "kn_" + resorcetype;
        else if (def_lang.equalsIgnoreCase(FC_Constants.MARATHI))
            resorcetype = "mr_" + resorcetype;
        else if (def_lang.equalsIgnoreCase(FC_Constants.ODIYA))
            resorcetype = "or_" + resorcetype;
        else if (def_lang.equalsIgnoreCase(FC_Constants.PUNJABI))
            resorcetype = "pa_" + resorcetype;
        else if (def_lang.equalsIgnoreCase(FC_Constants.TAMIL))
            resorcetype = "ta_" + resorcetype;
        else if (def_lang.equalsIgnoreCase(FC_Constants.TELUGU))
            resorcetype = "te_" + resorcetype;
        Log.d("INSTRUCTIONFRAG", "resorcetype: " + resorcetype);
        int rawID = activity.getResources().getIdentifier(resorcetype.toLowerCase(), "raw", activity.getPackageName());
        if (rawID != 0) {
            mediaPlayer = MediaPlayer.create(activity, rawID);
            mediaPlayer.start();
        } else {
            resorcetype = "hi_" + resorcetype;
            rawID = activity.getResources().getIdentifier(resorcetype.toLowerCase(), "raw", activity.getPackageName());
            if (rawID != 0) {
                mediaPlayer = MediaPlayer.create(activity, rawID);
                mediaPlayer.start();
            }
        }






    }



    private void hideKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(this).getSystemService(activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
