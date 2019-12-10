package com.pratham.foundation.ui.contentPlayer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.interfaces.ShowInstruction;
import com.pratham.foundation.modalclasses.EventMessage;
import com.pratham.foundation.ui.contentPlayer.dialogs.InstructionDialog;
import com.pratham.foundation.ui.contentPlayer.doing.DoingFragment;
import com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment.FactRetrieval_;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.Fact_Retrieval_Fragment_;
import com.pratham.foundation.ui.contentPlayer.fillInTheBlanks.FillInTheBlanksFragment;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.KeywordsIdentificationFragment_;
import com.pratham.foundation.ui.contentPlayer.keywords_mapping.KeywordMappingFragment_;
import com.pratham.foundation.ui.contentPlayer.listenAndWritting.ListeningAndWritting_;
import com.pratham.foundation.ui.contentPlayer.multipleChoiceQuetion.multipleChoiceFragment;
import com.pratham.foundation.ui.contentPlayer.new_reading_fragment.ContentReadingFragment;
import com.pratham.foundation.ui.contentPlayer.new_reading_fragment.ContentReadingFragment_;
import com.pratham.foundation.ui.contentPlayer.new_vocab_reading.VocabReadingFragment;
import com.pratham.foundation.ui.contentPlayer.new_vocab_reading.VocabReadingFragment_;
import com.pratham.foundation.ui.contentPlayer.paragraph_writing.ParagraphWritingFragment_;
import com.pratham.foundation.ui.contentPlayer.pictionary.pictionaryFragment;
import com.pratham.foundation.ui.contentPlayer.reading.ReadingFragment;
import com.pratham.foundation.ui.contentPlayer.sequenceLayout.SequenceLayout_;
import com.pratham.foundation.ui.contentPlayer.trueFalse.TrueFalseFragment;
import com.pratham.foundation.ui.contentPlayer.video_view.ActivityVideoView_;
import com.pratham.foundation.ui.contentPlayer.word_writting.WordWritingFragment_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.INFO_CLICKED;
import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;

public class GameConstatnts implements ShowInstruction {
    public static final String KEYWORD_IDENTIFICATION = "IKWAndroid";
    public static final String KEYWORD_MAPPING = "chKeywords";
    public static final String PARAGRAPH_WRITING = "CWiritng";
    public static final String LISTNING_AND_WRITTING = "Dict";
    //   public static final String READING = "reading";
//    public static final String MULTIPLE_CHOICE_QUE = "multiple_choice_que";
    public static final String FILL_IN_THE_BLANKS = "fill_in_the_blanks";
    public static final String TRUE_FALSE = "true_false";
    public static final String PICTIONARYFRAGMENT = "PictAndroid";
    public static final String DOING = "doing_act";
    public static final String FACT_RETRIAL_CLICK = "fact_retrial_click";
    public static final String FACTRETRIEVAL = "factRetrieval";
    public static final String SHOW_ME_ANDROID = "ShowMeAndroid";
    public static final String THINKANDWRITE = "TAW";
    public static final String DOING_ACT_READ = "doing_act_read";
    public static final String LetterWriting = "letter";
    public static final String DOING_ACT_VIDEO = "doing_act_video";
    public static final String READ_VOCAB_ANDROID = "ReadVocabAndroid";
    public static final String MULTIPLE_CHOICE = "multiple_choice";
    public static final String READING_STT = "reading_stt";
    public static final boolean TRUE = true;
    public static final boolean FALSE = false;
    public static final String START = "start";
    public static final String END = "end";
    public static final String VIDEO = "Video";

    public static boolean playInsequence = false;
    public static ContentTable contentTable1;
    public static InstructionsDialog instructionsDialog;
    private static GameConstatnts gameConstatnts;
    public static List<ContentTable> gameList;
    public static int currentGameAdapterposition;

   /* public static Bundle findGameData(String resourceID) {
        for (int i = 0; i < gameList.size(); i++) {
            if (resourceID.equalsIgnoreCase(gameList.get(i).getResourceId())) {
                if (i < (gameList.size() - 1)) {
                    i++;
                    ContentTable contentTable1 = gameList.get(i);
                    Bundle bundle = new Bundle();
                    bundle.putString("contentPath", contentTable1.getResourcePath());
                    bundle.putString("StudentID", FC_Constants.currentStudentID);
                    bundle.putString("resId", contentTable1.getResourceId());
                    bundle.putString("contentName", contentTable1.getNodeTitle());
                    bundle.putBoolean("onSdCard", true);
                    return bundle;
                }
            }
        }
        return null;
    }*/

    /*public static Bundle isContains(String resourceID) {
        for (int i = 0; i < gameList.size(); i++) {
            if (resourceID.equalsIgnoreCase(gameList.get(i).getResourceId())) {
                ContentTable contentTable1 = gameList.get(i);
                Bundle bundle = new Bundle();
                bundle.putString("contentPath", contentTable1.getResourcePath());
                bundle.putString("StudentID", FC_Constants.currentStudentID);
                bundle.putString("resId", contentTable1.getResourceId());
                bundle.putString("contentName", contentTable1.getNodeTitle());
                bundle.putBoolean("onSdCard", true);
                return bundle;
            }
        }
        return null;
    }*/

    public static int getInt(String resourceID) {
        return Integer.parseInt(resourceID);
    }

    public static void playGameNext(Context context, boolean flag, OnGameClose onGameClose) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText("Yes");
        dia_btn_red.setText("Exit");
        dia_btn_yellow.setText("" + dialog_btn_cancel);
        dia_title.setText("Next activity?");
        if (!flag) {
            dia_btn_yellow.setVisibility(View.GONE);
        }
        if (gameList != null && (currentGameAdapterposition == (gameList.size() - 1))) {
            dia_btn_red.setVisibility(View.GONE);
            if (!flag) {
                dia_title.setText("activity completed");
                dia_btn_green.setText("Ok");
            } else {
                dia_title.setText("Do you want to exit?");
            }
        } else {
            if (!playInsequence) {
                if (!flag) {
                    dia_title.setText("activity completed");
                    dia_btn_green.setText("Ok");
                } else {
                    dia_title.setText("Do you want to exit?");
                }
            }
        }


        dialog.show();

        dia_btn_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onGameClose.gameClose();
                if (playInsequence) {
                    plaGame(context);
                } else {
                    ((ContentPlayerActivity) context).getSupportFragmentManager().popBackStack(SequenceLayout_.class.getSimpleName(), 0);
                }
                //play next game

            }
        });

        dia_btn_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Exit game
                onGameClose.gameClose();
                ((ContentPlayerActivity) context).getSupportFragmentManager().popBackStack(SequenceLayout_.class.getSimpleName(), 0);
                dialog.dismiss();
            }
        });

        dia_btn_yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public static void plaGame(Context context) {

        contentTable1 = null;

        if (gameList != null && (currentGameAdapterposition < (gameList.size() - 1))) {
            //currentGameAdapterposition is initialized in adapter when game is played
            currentGameAdapterposition++;
            contentTable1 = gameList.get(currentGameAdapterposition);
            if (contentTable1 != null) {
                gameSelector(context, contentTable1);
            }
        } else {
            ((ContentPlayerActivity) context).getSupportFragmentManager().popBackStack(SequenceLayout_.class.getSimpleName(), 0);
        }
    }

    public static void gameSelector(Context context, ContentTable contentTable) {
        contentTable1 = contentTable;
        instructionsDialog = new InstructionsDialog(getGameConstantInstance(), context, contentTable1.getResourceType());
        instructionsDialog.show();

        Bundle bundle = null;
        bundle = new Bundle();
        bundle.putString("contentPath", contentTable1.getResourcePath());
        bundle.putString("StudentID", FC_Constants.currentStudentID);
        bundle.putString("resId", contentTable1.getResourceId());
        bundle.putString("contentName", contentTable1.getNodeTitle());
        bundle.putString("sttLang", contentTable1.getContentLanguage());
        bundle.putBoolean("onSdCard", true);
        bundle.putString("jsonName", contentTable1.getResourceType());

        switch (contentTable1.getResourceType()) {
            case GameConstatnts.FACTRETRIEVAL:
                FC_Utility.showFragment((Activity) context, new FactRetrieval_(), R.id.RL_CPA,
                        bundle, FactRetrieval_.class.getSimpleName());
                break;
            case GameConstatnts.
                    FACT_RETRIAL_CLICK:
                FC_Utility.showFragment((Activity) context, new Fact_Retrieval_Fragment_(), R.id.RL_CPA,
                        bundle, FactRetrieval_.class.getSimpleName());
                break;
            case GameConstatnts.KEYWORD_IDENTIFICATION:
                FC_Utility.showFragment((Activity) context, new KeywordsIdentificationFragment_(), R.id.RL_CPA,
                        bundle, KeywordsIdentificationFragment_.class.getSimpleName());
                break;
            case GameConstatnts.KEYWORD_MAPPING:
                FC_Utility.showFragment((Activity) context, new KeywordMappingFragment_(), R.id.RL_CPA,
                        bundle, KeywordMappingFragment_.class.getSimpleName());
                break;
            case GameConstatnts.PARAGRAPH_WRITING:
                if (FC_Constants.currentSubject.equalsIgnoreCase("English")) {
                    FC_Utility.showFragment((Activity) context, new WordWritingFragment_(), R.id.RL_CPA,
                            bundle, WordWritingFragment_.class.getSimpleName());
                } else if (FC_Constants.currentSubject.equalsIgnoreCase("Science")) {
                    FC_Utility.showFragment((Activity) context, new ParagraphWritingFragment_(), R.id.RL_CPA,
                            bundle, ParagraphWritingFragment_.class.getSimpleName());
                } else {
                    if (FC_Constants.currentLevel <= 2) {
                        FC_Utility.showFragment((Activity) context, new WordWritingFragment_(), R.id.RL_CPA,
                                bundle, WordWritingFragment_.class.getSimpleName());
                    } else {
                        FC_Utility.showFragment((Activity) context, new ParagraphWritingFragment_(), R.id.RL_CPA,
                                bundle, ParagraphWritingFragment_.class.getSimpleName());
                    }
                }
                break;
            case GameConstatnts.LISTNING_AND_WRITTING:
                FC_Utility.showFragment((Activity) context, new ListeningAndWritting_(), R.id.RL_CPA,
                        bundle, ListeningAndWritting_.class.getSimpleName());
                break;
            case GameConstatnts.MULTIPLE_CHOICE:
                FC_Utility.showFragment((Activity) context, new multipleChoiceFragment(), R.id.RL_CPA,
                        bundle, multipleChoiceFragment.class.getSimpleName());
                break;

          /*  case GameConstatnts.READING_STT:
                FC_Utility.showFragment((Activity) context, new ReadingFragment(), R.id.RL_CPA,
                        bundle, ReadingFragment.class.getSimpleName());
                break;*/

            case "109":
                FC_Utility.showFragment((Activity) context, new TrueFalseFragment(), R.id.RL_CPA,
                        bundle, TrueFalseFragment.class.getSimpleName());
                break;
            case "110":
                FC_Utility.showFragment((Activity) context, new FillInTheBlanksFragment(), R.id.RL_CPA,
                        bundle, FillInTheBlanksFragment.class.getSimpleName());
                break;
            case GameConstatnts.SHOW_ME_ANDROID:
                FC_Utility.showFragment((Activity) context, new pictionaryFragment(), R.id.RL_CPA,
                        bundle, pictionaryFragment.class.getSimpleName());
                break;


            case GameConstatnts.THINKANDWRITE:
            case GameConstatnts.DOING_ACT_READ:
            case GameConstatnts.DOING_ACT_VIDEO:
            case GameConstatnts.LetterWriting:
            case GameConstatnts.READING_STT:
                FC_Utility.showFragment((Activity) context, new DoingFragment(), R.id.RL_CPA,
                        bundle, DoingFragment.class.getSimpleName());
                break;
            case GameConstatnts.VIDEO:
                Intent intent = new Intent(context, ActivityVideoView_.class);
                intent.putExtra("contentPath", contentTable1.getResourcePath());
                intent.putExtra("StudentID", FC_Constants.currentStudentID);
                intent.putExtra("resId", contentTable1.getResourceId());
                intent.putExtra("contentName", contentTable1.getNodeTitle());
                intent.putExtra("onSdCard", true);
                context.startActivity(intent);
//                FC_Utility.showFragment((Activity) context, new ActivityVideoView_(), R.id.RL_CPA,
//                bundle, ActivityVideoView.class.getSimpleName());
                break;
        }


    }

    @Override
    public void play(Context context) {
        if (contentTable1 != null) {
            Bundle bundle = new Bundle();
            bundle.putString("contentPath", contentTable1.getResourcePath());
            bundle.putString("StudentID", FC_Constants.currentStudentID);
            bundle.putString("resId", contentTable1.getResourceId());
            bundle.putString("contentName", contentTable1.getNodeTitle());
            bundle.putString("sttLang", contentTable1.getContentLanguage());
            bundle.putBoolean("onSdCard", true);
            bundle.putString("jsonName", contentTable1.getResourceType());
            final Handler handler = new Handler();
            switch (contentTable1.getResourceType()) {
                case GameConstatnts.READ_VOCAB_ANDROID:
                    FC_Utility.showFragment((Activity) context, new VocabReadingFragment_(), R.id.RL_CPA,
                            bundle, VocabReadingFragment.class.getSimpleName());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            instructionsDialog.dismiss();
                            //Do something after 100ms
                        }
                    }, 100);
                    break;
                case GameConstatnts.PICTIONARYFRAGMENT:
                    FC_Utility.showFragment((Activity) context, new ContentReadingFragment_(), R.id.RL_CPA,
                            bundle, ContentReadingFragment.class.getSimpleName());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            instructionsDialog.dismiss();
                            //Do something after 100ms
                        }
                    }, 100);
                    break;
                default:
                    instructionsDialog.dismiss();
                    EventMessage eventMessage = new EventMessage();
                    eventMessage.setMessage(INFO_CLICKED);
                    EventBus.getDefault().post(eventMessage);
                    break;
            }


        }
    }

    public static int getCount() {
        if (FC_Constants.currentSubject.equalsIgnoreCase("English")) {
            if (FC_Constants.currentLevel <= 2) {
                return 5;
            } else {
                return 1;
            }

        } else if (FC_Constants.currentSubject.equalsIgnoreCase("Science")) {
            if (FC_Constants.currentLevel <= 2) {
                return 5;
            } else {
                return 1;
            }

        } else {
            if (FC_Constants.currentLevel < 2) {
                return 5;
            } else {
                return 1;
            }
        }
    }

/*    public static void showInstructionDialog(ShowInstruction showInstruction, Activity context, String resorcetype) {
        InstructionsDialog instructionsDialog = new InstructionsDialog(context, resorcetype);
        instructionsDialog.show();

    }*/

    public static void showGameInfo(Context context, String info, String infoPath) {
        InstructionDialog instructionDialog = new InstructionDialog(context, info, infoPath);
        instructionDialog.show();
      /*  final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_info_dialog);
        SansTextView infoText = dialog.findViewById(R.id.info);
        if (info != null)
            infoText.setText(info);
        dialog.show();*/

    }


    @Override
    public void exit() {

    }

    private static GameConstatnts getGameConstantInstance() {
        if (gameConstatnts == null) {
            gameConstatnts = new GameConstatnts();
        }
        return gameConstatnts;
    }
}
