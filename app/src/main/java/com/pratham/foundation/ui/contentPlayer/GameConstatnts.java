package com.pratham.foundation.ui.contentPlayer;

import android.os.Bundle;

import com.pratham.foundation.database.domain.ContentTable;
import com.pratham.foundation.utility.FC_Constants;

import java.util.List;

public class GameConstatnts {
    public static final String KEYWORD_IDENTIFICATION = "keyword_identification";
    public static final String KEYWORD_MAPPING = "keyword_mapping";
    public static final String PARAGRAPH_WRITING = "paragraph_writing";
    public static final String READING = "reading";
    public static final String MULTIPLE_CHOICE_QUE = "multiple_choice_que";
    public static final String FILL_IN_THE_BLANKS = "fill_in_the_blanks";
    public static final String TRUE_FALSE = "true_false";
    public static final String PICTIONARYFRAGMENT = "pictionary_game";
    public static final String FACT_RETRIAL_SELECTION = "fact_retrial_selection";

    public static List<ContentTable> gameList;

    public static Bundle findGameData( String resourceID) {
        for (int i=0;i<gameList.size();i++){
            if(resourceID.equalsIgnoreCase(gameList.get(i).getResourceId())){
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
    }
    public static Bundle isContains( String resourceID) {
        for (int i=0;i<gameList.size();i++){
            if(resourceID.equalsIgnoreCase(gameList.get(i).getResourceId())){
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
    }
}
