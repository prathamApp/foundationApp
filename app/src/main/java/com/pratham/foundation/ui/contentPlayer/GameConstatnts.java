package com.pratham.foundation.ui.contentPlayer;

import android.app.Activity;
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

import com.google.gson.Gson;
import com.pratham.foundation.R;
import com.pratham.foundation.customView.display_image_dialog.CustomLodingDialog;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.interfaces.OnGameClose;
import com.pratham.foundation.interfaces.ShowInstruction;
import com.pratham.foundation.modalclasses.ImageJsonObject;
import com.pratham.foundation.modalclasses.ScoreEvent;
import com.pratham.foundation.services.shared_preferences.FastSave;
import com.pratham.foundation.ui.contentPlayer.chit_chat.level_1.ConversationFragment_1_;
import com.pratham.foundation.ui.contentPlayer.chit_chat.level_2.ConversationFragment_2_;
import com.pratham.foundation.ui.contentPlayer.chit_chat.level_3.ConversationFragment_3_;
import com.pratham.foundation.ui.contentPlayer.dialogs.InstructionDialog;
import com.pratham.foundation.ui.contentPlayer.doing.DoingFragment_;
import com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment.FactRetrieval_;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.Fact_Retrieval_Fragment_;
import com.pratham.foundation.ui.contentPlayer.fillInTheBlanks.FillInTheBlanksFragment;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.KeywordsIdentificationFragment_;
import com.pratham.foundation.ui.contentPlayer.keywords_mapping.KeywordMappingFragment_;
import com.pratham.foundation.ui.contentPlayer.listenAndWritting.ListeningAndWritting_;
import com.pratham.foundation.ui.contentPlayer.morphin.Hive_game_;
import com.pratham.foundation.ui.contentPlayer.multipleChoiceQuetion.multipleChoiceFragment_;
import com.pratham.foundation.ui.contentPlayer.new_reading_fragment.ContentReadingFragment;
import com.pratham.foundation.ui.contentPlayer.new_reading_fragment.ContentReadingFragment_;
import com.pratham.foundation.ui.contentPlayer.new_vocab_reading.VocabReadingFragment;
import com.pratham.foundation.ui.contentPlayer.new_vocab_reading.VocabReadingFragment_;
import com.pratham.foundation.ui.contentPlayer.paragraph_stt.ParaSttReadingFragment;
import com.pratham.foundation.ui.contentPlayer.paragraph_stt.ParaSttReadingFragment_;
import com.pratham.foundation.ui.contentPlayer.paragraph_stt.STTQuestionsFragment;
import com.pratham.foundation.ui.contentPlayer.paragraph_stt.STTQuestionsFragment_;
import com.pratham.foundation.ui.contentPlayer.paragraph_stt.STTSummaryFragment;
import com.pratham.foundation.ui.contentPlayer.paragraph_stt.STTSummaryFragment_;
import com.pratham.foundation.ui.contentPlayer.paragraph_writing.ParagraphWritingFragment_;
import com.pratham.foundation.ui.contentPlayer.pictionary.pictionaryFragment;
import com.pratham.foundation.ui.contentPlayer.pictionary.pictionaryFragment_;
import com.pratham.foundation.ui.contentPlayer.reading.ReadingFragment_;
import com.pratham.foundation.ui.contentPlayer.sequenceLayout.SequenceLayout_;
import com.pratham.foundation.ui.contentPlayer.trueFalse.TrueFalseFragment;
import com.pratham.foundation.ui.contentPlayer.video_view.ActivityVideoView_;
import com.pratham.foundation.ui.contentPlayer.word_writting.WordWritingFragment_;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;

public class GameConstatnts implements ShowInstruction {
    public static final String KEYWORD_IDENTIFICATION = "IKWAndroid";
    public static final String KEYWORD_MAPPING = "chKeywords";
    public static final String PARAGRAPH_WRITING = "CopyWriting";
    public static final String LISTNING_AND_WRITTING = "Dictation";
    //   public static final String READING = "reading";
//    public static final String MULTIPLE_CHOICE_QUE = "multiple_choice_que";
    public static final String FILL_IN_THE_BLANKS = "fill_in_the_blanks";
    public static final String TRUE_FALSE = "true_false";
    public static final String READING_ANDROID = "ReadingAndroid";
    public static final String DOING = "doing_act";
    public static final String FACT_RETRIAL_CLICK = "fact_retrieval_click";
    public static final String FACTRETRIEVAL = "factRetrieval";
    public static final String SHOW_ME_ANDROID = "ShowMeAndroid";
    public static final String THINKANDWRITE = "TAW";
    public static final String DOING_ACT_READ = "doing_act";
    public static final String LetterWriting = "letter";
    // public static final String DOING_ACT_VIDEO = "doing_act_video";
    public static final String READ_VOCAB_ANDROID = "ReadVocabAndroid";
    public static final String MULTIPLE_CHOICE = "multiple_choice";
    public static final String READING_STT = "reading_stt";
    public static final String WATCHING_VIDEO = "watching_video";
    public static final String NEW_CHIT_CHAT_1 = "new_chit_chat_1";
    public static final String NEW_CHIT_CHAT_2 = "new_chit_chat_2";
    public static final String NEW_CHIT_CHAT_3 = "new_chit_chat_3";
    public static final String HIVELAYOUT_GAME = "hivelayout_game";
    public static final String PARAREADQUES = "ParaReadQues";
    public static final String PARAQA = "paraqa";
    public static final String PARASUMMARY = "parasummary";
    public static final boolean TRUE = true;
    public static final boolean FALSE = false;
    public boolean onSdCard = false;
    public static boolean onSdCard2 = false;
    public static final String START = "start";
    public static final String END = "end";
    public static final String VIDEO = "Video";

    public static String readingImgPath = "";
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
                    bundle.putString("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
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
                bundle.putString("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
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
        final CustomLodingDialog dialog = new CustomLodingDialog(context, R.style.FC_DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText(context.getResources().getString(R.string.Okay));
        dia_btn_red.setText(context.getResources().getString(R.string.Exit));
        dia_btn_yellow.setText(context.getResources().getString(R.string.Cancel));
        dia_title.setText(context.getResources().getString(R.string.Next_activity));
        if (!flag) {
            dia_btn_yellow.setVisibility(View.GONE);
        }
        if (gameList != null && (currentGameAdapterposition == (gameList.size() - 1))) {
            dia_btn_red.setVisibility(View.GONE);
            if (!flag) {
                dia_title.setText(context.getResources().getString(R.string.activity_completed));
                dia_btn_green.setText(context.getResources().getString(R.string.Okay));
            } else {
                dia_title.setText(context.getResources().getString(R.string.Do_you_want_to_exit));
            }
        } else {
            if (!playInsequence) {
                if (!flag) {
                    dia_title.setText(context.getResources().getString(R.string.activity_completed));
                    dia_btn_green.setText(context.getResources().getString(R.string.Okay));
                } else {
                    dia_title.setText(context.getResources().getString(R.string.Do_you_want_to_exit));
                }
            }
        }
        dialog.show();
        dia_btn_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onGameClose.gameClose();

                if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                    ((ContentPlayerActivity) context).finish();
                }
                if (playInsequence) {
                    plaGame(context,onSdCard2);
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
                if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                    ((ContentPlayerActivity) context).finish();
                }
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

    public static void plaGame(Context context,boolean onSdCard) {
        onSdCard2 = onSdCard;
        contentTable1 = null;
        if (gameList != null && (currentGameAdapterposition < (gameList.size() - 1))) {
            //currentGameAdapterposition is initialized in adapter when game is played
            currentGameAdapterposition++;
            contentTable1 = gameList.get(currentGameAdapterposition);
            if (contentTable1 != null) {
                gameSelector(context, contentTable1,onSdCard);
            }
        } else {
            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                ((ContentPlayerActivity) context).finish();
            }
            ((ContentPlayerActivity) context).getSupportFragmentManager().popBackStack(SequenceLayout_.class.getSimpleName(), 0);
        }
    }

    public static void plaPrevGame(Context context, boolean onSdCard) {
        contentTable1 = null;
        if (gameList != null && (currentGameAdapterposition > 0)) {
            //currentGameAdapterposition is initialized in adapter when game is played
            currentGameAdapterposition--;
            contentTable1 = gameList.get(currentGameAdapterposition);
            if (contentTable1 != null) {
                gameSelector(context, contentTable1,onSdCard);
            }
        } else {
            if (FastSave.getInstance().getString(APP_SECTION,"").equalsIgnoreCase(sec_Test)) {
                ((ContentPlayerActivity) context).finish();
            }
            ((ContentPlayerActivity) context).getSupportFragmentManager().popBackStack(SequenceLayout_.class.getSimpleName(), 0);
        }
    }

    public static void gameSelector(Context context, ContentTable contentTable, boolean onSdCard) {
        contentTable1 = contentTable;
        instructionsDialog = new InstructionsDialog(getGameConstantInstance(), context, contentTable1.getResourceType());
        instructionsDialog.show();
        Bundle bundle = null;
        bundle = new Bundle();
        bundle.putString("contentPath", contentTable1.getResourcePath());
        bundle.putString("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
        bundle.putString("resId", contentTable1.getResourceId());
        bundle.putString("contentName", contentTable1.getNodeTitle());
        bundle.putString("sttLang", contentTable1.getContentLanguage());
        bundle.putBoolean("onSdCard", onSdCard);
        bundle.putString("jsonName", contentTable1.getResourceType());

        switch (contentTable1.getResourceType()) {
            case GameConstatnts.FACTRETRIEVAL:
                FC_Utility.showFragment((Activity) context, new FactRetrieval_(), R.id.RL_CPA,
                        bundle, FactRetrieval_.class.getSimpleName());
                break;
            case GameConstatnts.FACT_RETRIAL_CLICK:
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
            case GameConstatnts.THINKANDWRITE:
                if (FastSave.getInstance().getString(FC_Constants.CURRENT_SUBJECT, "").equalsIgnoreCase("Science")) {
                    FC_Utility.showFragment((Activity) context, new ParagraphWritingFragment_(), R.id.RL_CPA,
                            bundle, ParagraphWritingFragment_.class.getSimpleName());
                } else {
                    FC_Utility.showFragment((Activity) context, new DoingFragment_(), R.id.RL_CPA,
                            bundle, DoingFragment_.class.getSimpleName());
                }
                break;
            case GameConstatnts.PARAGRAPH_WRITING:
                /*if (FastSave.getInstance().getString(FC_Constants.CURRENT_SUBJECT, "").equalsIgnoreCase("English")) {
                    FC_Utility.showFragment((Activity) context, new WordWritingFragment_(), R.id.RL_CPA,
                            bundle, WordWritingFragment_.class.getSimpleName());
                } else*/ if (FastSave.getInstance().getString(FC_Constants.CURRENT_SUBJECT, "").equalsIgnoreCase("Science")) {
                    FC_Utility.showFragment((Activity) context, new ParagraphWritingFragment_(), R.id.RL_CPA,
                            bundle, ParagraphWritingFragment_.class.getSimpleName());
                } else {
                    FC_Utility.showFragment((Activity) context, new WordWritingFragment_(), R.id.RL_CPA,
                            bundle, WordWritingFragment_.class.getSimpleName());

                  /*  if (FC_Constants.currentLevel <= 2) {
                        FC_Utility.showFragment((Activity) context, new WordWritingFragment_(), R.id.RL_CPA,
                                bundle, WordWritingFragment_.class.getSimpleName());
                    } else {
                        FC_Utility.showFragment((Activity) context, new ParagraphWritingFragment_(), R.id.RL_CPA,
                                bundle, ParagraphWritingFragment_.class.getSimpleName());
                    }*/
                }
                break;
            case GameConstatnts.LISTNING_AND_WRITTING:
                FC_Utility.showFragment((Activity) context, new ListeningAndWritting_(), R.id.RL_CPA,
                        bundle, ListeningAndWritting_.class.getSimpleName());
                break;
            case GameConstatnts.MULTIPLE_CHOICE:
                FC_Utility.showFragment((Activity) context, new multipleChoiceFragment_(), R.id.RL_CPA,
                        bundle, multipleChoiceFragment_.class.getSimpleName());
                break;

            case GameConstatnts.READING_STT:
                FC_Utility.showFragment((Activity) context, new ReadingFragment_(), R.id.RL_CPA,
                        bundle, ReadingFragment_.class.getSimpleName());
                break;

            case "109":
                FC_Utility.showFragment((Activity) context, new TrueFalseFragment(), R.id.RL_CPA,
                        bundle, TrueFalseFragment.class.getSimpleName());
                break;
            case "110":
                FC_Utility.showFragment((Activity) context, new FillInTheBlanksFragment(), R.id.RL_CPA,
                        bundle, FillInTheBlanksFragment.class.getSimpleName());
                break;
            case GameConstatnts.SHOW_ME_ANDROID:
                FC_Utility.showFragment((Activity) context, new pictionaryFragment_(), R.id.RL_CPA,
                        bundle, pictionaryFragment.class.getSimpleName());
                break;
            case GameConstatnts.DOING_ACT_READ:
            case GameConstatnts.LetterWriting:
            case GameConstatnts.WATCHING_VIDEO:
                FC_Utility.showFragment((Activity) context, new DoingFragment_(), R.id.RL_CPA,
                        bundle, DoingFragment_.class.getSimpleName());
                break;
            case GameConstatnts.HIVELAYOUT_GAME:
                FC_Utility.showFragment((Activity) context, new Hive_game_(), R.id.RL_CPA,
                        bundle, Hive_game_.class.getSimpleName());
                break;

        }
    }

    @Override
    public void play(Context context) {
        if (contentTable1 != null) {
            Bundle bundle = new Bundle();
            bundle.putString("contentPath", contentTable1.getResourcePath());
            bundle.putString("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
            bundle.putString("resId", contentTable1.getResourceId());
            bundle.putString("contentName", contentTable1.getNodeTitle());
            bundle.putString("sttLang", contentTable1.getContentLanguage());
            bundle.putBoolean("onSdCard", onSdCard);
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
                case GameConstatnts.READING_ANDROID:
//                    FC_Utility.showFragment((Activity) context, new ScrollReadingFragment_(), R.id.RL_CPA,
//                            bundle, ScrollReadingFragment.class.getSimpleName());
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
                case GameConstatnts.PARAQA:
                    FC_Utility.showFragment((Activity) context, new STTQuestionsFragment_(), R.id.RL_CPA,
                            bundle, STTQuestionsFragment.class.getSimpleName());
                    handler.postDelayed(() -> {
                        instructionsDialog.dismiss();
                    }, 100);
                    break;            // case GameConstatnts.DOING_ACT_VIDEO:
                case GameConstatnts.PARASUMMARY:
                    FC_Utility.showFragment((Activity) context, new STTSummaryFragment_(), R.id.RL_CPA,
                            bundle, STTSummaryFragment.class.getSimpleName());
                    handler.postDelayed(() -> {
                        instructionsDialog.dismiss();
                    }, 100);
                    break;            // case GameConstatnts.DOING_ACT_VIDEO:
                case GameConstatnts.PARAREADQUES:
                    FC_Utility.showFragment((Activity) context, new ParaSttReadingFragment_(), R.id.RL_CPA,
                            bundle, ParaSttReadingFragment.class.getSimpleName());
                    handler.postDelayed(() -> {
                        instructionsDialog.dismiss();
                    }, 100);
                    break;
                case GameConstatnts.VIDEO:
                    Intent intent = new Intent(context, ActivityVideoView_.class);
                    intent.putExtra("contentPath", contentTable1.getResourcePath());
                    intent.putExtra("StudentID", FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
                    intent.putExtra("resId", contentTable1.getResourceId());
                    intent.putExtra("contentName", contentTable1.getNodeTitle());
                    intent.putExtra("onSdCard", onSdCard);
                    context.startActivity(intent);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            instructionsDialog.dismiss();
                        }
                    }, 100);
//                FC_Utility.showFragment((Activity) context, new ActivityVideoView_(), R.id.RL_CPA,
//                bundle, ActivityVideoView.class.getSimpleName());
                    break;
                case GameConstatnts.NEW_CHIT_CHAT_1:
                    FC_Utility.showFragment((Activity) context, new ConversationFragment_1_(), R.id.RL_CPA,
                            bundle, ConversationFragment_1_.class.getSimpleName());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            instructionsDialog.dismiss();
                            //Do something after 100ms
                        }
                    }, 100);
                    break;
                case GameConstatnts.NEW_CHIT_CHAT_2:
                    FC_Utility.showFragment((Activity) context, new ConversationFragment_2_(), R.id.RL_CPA,
                            bundle, ConversationFragment_2_.class.getSimpleName());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            instructionsDialog.dismiss();
                            //Do something after 100ms
                        }
                    }, 100);
                    break;
                case GameConstatnts.NEW_CHIT_CHAT_3:
                    FC_Utility.showFragment((Activity) context, new ConversationFragment_3_(), R.id.RL_CPA,
                            bundle, ConversationFragment_3_.class.getSimpleName());
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
                    //  show instruction on game dstart uncomment bellow code
                    //EventMessage eventMessage = new EventMessage();
                   // eventMessage.setMessage(FC_Constants.DIALOG_CLOSED);
                    EventBus.getDefault().post(FC_Constants.DIALOG_CLOSED);
                    break;
            }
        }
    }

    public static int getCount() {
        if (FastSave.getInstance().getString(FC_Constants.CURRENT_SUBJECT, "").equalsIgnoreCase("English")) {
            if (FC_Constants.currentLevel <= 2) {
                return 5;
            } else {
                return 1;
            }

        } else if (FastSave.getInstance().getString(FC_Constants.CURRENT_SUBJECT, "").equalsIgnoreCase("Science")) {
            if (FC_Constants.currentLevel <= 2) {
                return 5;
            } else {
                return 1;
            }

        } else {
           /* if (FC_Constants.currentLevel < 2) {
                return 5;
            } else {
                return 1;
            }*/
           return 5;
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
    public void exit() { }

    public static void postScoreEvent(int totalMarks, int scoredMarks) {
        EventBus.getDefault().post(new ScoreEvent(FC_Constants.RETURNSCORE, totalMarks, scoredMarks));
    }

    private static GameConstatnts getGameConstantInstance() {
        if (gameConstatnts == null)
            gameConstatnts = new GameConstatnts();
        return gameConstatnts;
    }

    public static String getString(String resId, String gameName, String qid, String ansImageName, String question, String queImageName) {
        Gson gson=new Gson();
        ImageJsonObject imageJsonObject = new ImageJsonObject(resId, gameName, qid, ansImageName, question, queImageName);
        String image= gson.toJson(imageJsonObject);
        return image;
    }
}
