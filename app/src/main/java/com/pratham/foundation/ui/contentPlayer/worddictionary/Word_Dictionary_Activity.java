package com.pratham.foundation.ui.contentPlayer.worddictionary;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.provider.Settings;
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
import android.widget.ImageView;
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
import com.pratham.foundation.modalclasses.ModalParaWord;
import com.pratham.foundation.modalclasses.WordDictionaryModel;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.contentPlayer.GameConstatnts;
import com.pratham.foundation.ui.contentPlayer.dialogs.InstructionDialog;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.gameFolderPath;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;


@EActivity(R.layout.word_dictionary_selection)
public  class Word_Dictionary_Activity extends BaseActivity
        implements
        Word_Dictionary_Contract.Word_Dictionary_View {


    @Bean(Word_Dictionary_Presenter.class)
    Word_Dictionary_Contract.Word_Dictionary_Presenter presenter;

    @ViewById(R.id.word)
    TextView word;
    @ViewById(R.id.title)
    TextView title;
    @ViewById(R.id.word_hint)
    TextView word_hint;
    @ViewById(R.id.language_hint)
    TextView language_hint;
    @ViewById(R.id.keyword_change)
    ImageView keyword_change;

    @ViewById(R.id.cancel_action)
    SansButton cancel_action;
    @ViewById(R.id.save_button)
    SansButton save_button;
    @ViewById(R.id.clear_selection)
    SansButton clear_selection;

    @ViewById(R.id.btn_next)
    ImageButton btn_next;
    @ViewById(R.id.next)
    Button next;

    @ViewById(R.id.et_word)
    EditText et_word;
    @ViewById(R.id.et_language)
    EditText et_language;

    @ViewById(R.id.spinner_language)
    Spinner spinner_language;

    public List<String> ModelWordList;
    public List<String> ModelLanguageList;

    RelativeLayout.LayoutParams viewParam;

    private boolean onSdCard;
    public int current_no = 0;
    private final boolean isKeyWordShowing = false;
    private int index = 0;
    String readingContentPath, contentPath, contentTitle, StudentID, resId, paraAudio,
            useText, englishWord, startPlayBack, resStartTime, certiCode, resType;

    Activity activity;
    int  no_word_answer = 0;
    public  MediaPlayer mediaPlayer = null;
    String json_data;

    @SuppressLint("SetTextI18n")
    @AfterViews
    public void initialize() {
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


        activity = this;
        presenter.setView(Word_Dictionary_Activity.this);
        et_language.setVisibility(View.GONE);
        Intent intent = getIntent();
        contentTitle = getIntent().getStringExtra("contentTitle");
        contentPath = intent.getStringExtra("contentPath");
        StudentID = intent.getStringExtra("StudentID");
        resId = intent.getStringExtra("resId");
        certiCode = intent.getStringExtra("certiCode");
        resType = intent.getStringExtra("resType");
        json_data = intent.getStringExtra("json_data");
        onSdCard = getIntent().getBooleanExtra("onSdCard", false);


        resStartTime = FC_Utility.getCurrentDateTime();
        presenter.setResId(resId);

        if (onSdCard)
            readingContentPath = ApplicationClass.contentSDPath + gameFolderPath + "/" + contentPath + "/";
        else
            readingContentPath = ApplicationClass.foundationPath + gameFolderPath + "/" + contentPath + "/";


        showPlayDialog("word_dictionary");






    }



    @UiThread
    @Override
    public void setListData(List<String> paraDataList) {
        ModelWordList = paraDataList;
        if(ModelWordList.size()>0)
        Collections.shuffle(ModelWordList);
        else {
            Toast.makeText(this, "Data not found", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @UiThread
    @Override
    public void setLangListData(List<String> paraDataList) {
        ModelLanguageList = paraDataList;

        if (!ModelLanguageList.get(0).equals(getResources().getString(R.string.word_dictionary_select_language)))
            ModelLanguageList.add(0, getResources().getString(R.string.word_dictionary_select_language));

        if (!ModelLanguageList.get(ModelLanguageList.size()-1).equals(getResources().getString(R.string.other_language)))
            ModelLanguageList.add(ModelLanguageList.size(), getResources().getString(R.string.other_language));
    }



    @UiThread
    @Override
    public void initializeContent() {

        word.setText(ModelWordList.get(current_no));
    }

    @UiThread
    @Override
    public void initializeLangContent() {


        ArrayAdapter ageAdapter = new ArrayAdapter(activity, R.layout.custom_spinner, ModelLanguageList);
        spinner_language.setAdapter(ageAdapter);

        spinner_language.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return false;
            }
        });

        spinner_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard(view);
                if(position == ModelLanguageList.size()-1)
                {
                    et_language.setVisibility(View.VISIBLE);
                    et_language.setText("");
                }
                else
                {
                    et_language.setVisibility(View.GONE);
                    et_language.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        });


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



    @Click(R.id.save_button)
    public void save_button() {
        if (et_word.getText().toString().equals("")) {
            Toast.makeText(activity, getResources().getString(R.string.enter_word), Toast.LENGTH_SHORT).show();
        } else if (spinner_language.getSelectedItemPosition() == 0) {
            Toast.makeText(activity, getResources().getString(R.string.select_language), Toast.LENGTH_SHORT).show();
        }else if (spinner_language.getSelectedItemPosition() == ModelLanguageList.size()-1 && et_language.getText().toString().trim().equals("")) {
            Toast.makeText(activity, getResources().getString(R.string.enter_language), Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("word", word.getText().toString());
                jsonObject.put("local_word", et_word.getText().toString());
                jsonObject.put("local_language",(spinner_language.getSelectedItemPosition() == ModelLanguageList.size()-1)? et_language.getText().toString().trim() : spinner_language.getSelectedItem().toString());
                presenter.addCompletion(jsonObject.toString(),word.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(activity,  word.getText().toString() + "-" + et_word.getText().toString()+"-"+spinner_language.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.keyword_change)
    public void keyword_change() {
        /*InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);

        imeManager.showInputMethodPicker();*/
        startActivity(new Intent(Settings.ACTION_HARD_KEYBOARD_SETTINGS));
        startActivity(new Intent("android.settings.INPUT_METHOD_SETTINGS"));
    }

    @Click(R.id.cancel_action)
    public void cancel_action() {
        //showExitDialog();
        onBackPressed();
    }

    @Click(R.id.clear_selection)
    public void resetdata() {
        et_word.setText("");
        spinner_language.setSelection(0);
    }

    @Click(R.id.btn_next)
    public void NextAction() {
        nextbuttonaction();

    }

    @Click(R.id.next)
    public void NextButonAction() {
        nextbuttonaction();

    }

    @UiThread
    @Override
    public void nextbuttonaction() {
        no_word_answer++;
        if(no_word_answer % 10 == 0)
        {
            showNextDialog();
        }
        else {
            next_method();
        }
    }

    public void next_method()
    {
        setAnimations(word);
        word.setText("");
        et_word.setText("");
        spinner_language.setSelection(0);
        current_no++;
        if(current_no < ModelWordList.size())
        word.setText(ModelWordList.get(current_no));
        else
            finish();
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

    @Override
    public void setCharListData(List<WordDictionaryModel> paraDataList) {

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

                presenter.fetchJsonData(json_data, resType);
                presenter.fetchLangJsonData(readingContentPath, resType);

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
