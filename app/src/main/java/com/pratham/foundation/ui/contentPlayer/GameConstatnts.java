package com.pratham.foundation.ui.contentPlayer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pratham.foundation.R;
import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.ui.contentPlayer.fact_retrieval_fragment.FactRetrieval_;
import com.pratham.foundation.ui.contentPlayer.fact_retrival_selection.Fact_Retrieval_Fragment_;
import com.pratham.foundation.ui.contentPlayer.fillInTheBlanks.FillInTheBlanksFragment;
import com.pratham.foundation.ui.contentPlayer.keywords_identification.KeywordsIdentificationFragment_;
import com.pratham.foundation.ui.contentPlayer.keywords_mapping.KeywordMappingFragment_;
import com.pratham.foundation.ui.contentPlayer.listenAndWritting.ListeningAndWritting_;
import com.pratham.foundation.ui.contentPlayer.multipleChoice.McqFillInTheBlanksFragment;
import com.pratham.foundation.ui.contentPlayer.paragraph_writing.ParagraphWritingFragment_;
import com.pratham.foundation.ui.contentPlayer.pictionary.pictionaryFragment;
import com.pratham.foundation.ui.contentPlayer.reading.ReadingFragment;
import com.pratham.foundation.ui.contentPlayer.sequenceLayout.SequenceLayout_;
import com.pratham.foundation.ui.contentPlayer.story_reading.StoryReadingFragment_;
import com.pratham.foundation.ui.contentPlayer.trueFalse.TrueFalseFragment;
import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.dialog_btn_cancel;

public class GameConstatnts {
    public static final String KEYWORD_IDENTIFICATION = "IKWAndroid";
    public static final String KEYWORD_MAPPING = "chKeywords";
    public static final String PARAGRAPH_WRITING = "CWiritng";
    public static final String LISTNING_AND_WRITTING = "Dict";
    public static final String READING = "reading";
    public static final String MULTIPLE_CHOICE_QUE = "multiple_choice_que";
    public static final String FILL_IN_THE_BLANKS = "fill_in_the_blanks";
    public static final String TRUE_FALSE = "true_false";
    public static final String PICTIONARYFRAGMENT = "PictAndroid";
    public static final String FACT_RETRIAL_SELECTION = "fact_retrial_selection";
    public static final String FACTRETRIEVAL = "factRetrival";
    public static final String READINGGAME = "ShowMeAndroid";

    public static List<ContentTable> gameList;
    public static int currentGameAdapterposition;

    public static Bundle findGameData(String resourceID) {
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
    }

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

    public static void playGameNext(Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fc_custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView dia_title = dialog.findViewById(R.id.dia_title);
        Button dia_btn_yellow = dialog.findViewById(R.id.dia_btn_yellow);
        Button dia_btn_green = dialog.findViewById(R.id.dia_btn_green);
        Button dia_btn_red = dialog.findViewById(R.id.dia_btn_red);

        dia_btn_green.setText("Yes");
        dia_btn_red.setText("Exit Game");
        dia_btn_yellow.setText("" + dialog_btn_cancel);
        dia_title.setText("Play next Game?");
        dialog.show();

        dia_btn_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plaGame(context);
                dialog.dismiss();
            }
        });

        dia_btn_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private static void plaGame(Context context) {

        ContentTable contentTable1 = null;
        Bundle bundle = null;

        if (gameList != null && (currentGameAdapterposition < (gameList.size() - 1))) {
            //currentGameAdapterposition is initialized in adapter when game is played
            currentGameAdapterposition++;
            contentTable1 = gameList.get(currentGameAdapterposition);
            bundle = new Bundle();
            bundle.putString("contentPath", contentTable1.getResourcePath());
            bundle.putString("StudentID", FC_Constants.currentStudentID);
            bundle.putString("resId", contentTable1.getResourceId());
            bundle.putString("contentName", contentTable1.getNodeTitle());
            bundle.putBoolean("onSdCard", true);
            if (contentTable1 != null)
                switch (contentTable1.getResourceType()) {
                    case GameConstatnts.FACTRETRIEVAL:
                        FC_Utility.showFragment((Activity) context, new FactRetrieval_(), R.id.RL_CPA,
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
                        FC_Utility.showFragment((Activity) context, new ParagraphWritingFragment_(), R.id.RL_CPA,
                                bundle, ParagraphWritingFragment_.class.getSimpleName());
                        break;
                    case "105":
                        FC_Utility.showFragment((Activity) context, new ListeningAndWritting_(), R.id.RL_CPA,
                                bundle, ListeningAndWritting_.class.getSimpleName());
                        break;
                    case "106":
                        FC_Utility.showFragment((Activity) context, new ReadingFragment(), R.id.RL_CPA,
                                bundle, ReadingFragment.class.getSimpleName());
                        break;
                    case "108":
                        FC_Utility.showFragment((Activity) context, new McqFillInTheBlanksFragment(), R.id.RL_CPA,
                                bundle, McqFillInTheBlanksFragment.class.getSimpleName());
                        break;
                    case "109":
                        FC_Utility.showFragment((Activity) context, new TrueFalseFragment(), R.id.RL_CPA,
                                bundle, TrueFalseFragment.class.getSimpleName());
                        break;
                    case "110":
                        FC_Utility.showFragment((Activity) context, new FillInTheBlanksFragment(), R.id.RL_CPA,
                                bundle, FillInTheBlanksFragment.class.getSimpleName());
                        break;
                    case GameConstatnts.READINGGAME:
                        FC_Utility.showFragment((Activity) context, new pictionaryFragment(), R.id.RL_CPA,
                                bundle, pictionaryFragment.class.getSimpleName());
                        break;
                    case "112":
                        FC_Utility.showFragment((Activity) context, new Fact_Retrieval_Fragment_(), R.id.RL_CPA,
                                bundle, Fact_Retrieval_Fragment_.class.getSimpleName());
                        break;
                    case GameConstatnts.PICTIONARYFRAGMENT:
                        FC_Utility.showFragment((Activity) context, new StoryReadingFragment_(), R.id.RL_CPA,
                                bundle, StoryReadingFragment_.class.getSimpleName());
                        break;
                }
      }else {
            ((ContentPlayerActivity) context).getSupportFragmentManager().popBackStack(SequenceLayout_.class.getSimpleName(), 0);
        }
    }
}

