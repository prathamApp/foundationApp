  package com.pratham.foundation.ui.contentPlayer.worddictionary;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.foundation.database.AppDatabase;
import com.pratham.foundation.database.BackupDatabase;
import com.pratham.foundation.database.domain.Assessment;
import com.pratham.foundation.database.domain.ContentProgress;
import com.pratham.foundation.database.domain.KeyWords;
import com.pratham.foundation.database.domain.Score;
import com.pratham.foundation.modalclasses.ModalParaMainMenu;
import com.pratham.foundation.modalclasses.ModalParaSubMenu;
import com.pratham.foundation.modalclasses.ModalParaWord;
import com.pratham.foundation.modalclasses.WordDictionaryModel;
import com.pratham.foundation.services.shared_preferences.FastSave;

import com.pratham.foundation.utility.FC_Constants;
import com.pratham.foundation.utility.FC_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.foundation.utility.FC_Constants.APP_SECTION;
import static com.pratham.foundation.utility.FC_Constants.CURRENT_FOLDER_NAME;
import static com.pratham.foundation.utility.FC_Constants.STT_REGEX;
import static com.pratham.foundation.utility.FC_Constants.sec_Test;


  @EBean
  public class Word_Dictionary_Presenter implements Word_Dictionary_Contract.Word_Dictionary_Presenter {

      public Word_Dictionary_Contract.Word_Dictionary_View readingView;

      public Context context;
      public List<ModalParaWord> modalParaList, questionList;
      public ModalParaMainMenu modalParaMainMenu;
      public ModalParaSubMenu modalParaSubMenu;
      public JSONArray WordParaJsonArray;
      public JSONArray LangParaJsonArray;
      public JSONArray CharParaJsonArray;
      public List<String> WordList;
      public List<WordDictionaryModel> CharList;
      public List<String> LanguageList;
      public List<KeyWords> learntWordsList;
      public KeyWords learntWords;
      public float perc = 0f, pagePerc;
      public int randomCategory, GLC, totalVocabSize, learntWordsCount;
      public String resId, resStartTime, resType, paraAudio;


      public Word_Dictionary_Presenter(Context context) {
          this.context = context;
      }

      @Override
      public void setView(Word_Dictionary_Contract.Word_Dictionary_View readingView) {
          this.readingView = readingView;
          learntWordsList = new ArrayList<>();
      }



      @Override
      public void setResId(String resourceId) {
          resId = resourceId;
          resStartTime = FC_Utility.getCurrentDateTime();
      }






      @Background
      @Override
      public void getDataList() {

          try {
              Gson gson = new Gson();
              WordList = gson.fromJson(WordParaJsonArray.toString(),new TypeToken<List<String>>(){}.getType());
              Log.i("SIZE-1",WordList.size()+"-");
              List<String> keyWordsList = AppDatabase.getDatabaseInstance(context).getKeyWordDao().getListKeyWords(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID,""),"WORD_DIC");
              WordList.removeAll(keyWordsList);
             Log.i("SIZE-2",WordList.size()+"-");
          } catch (Exception e) {
              e.printStackTrace();
          }



          readingView.setListData(WordList);
          readingView.initializeContent();

      }


      @Background
      @Override
      public void  getCahrDataList() {

          try {
              Gson gson = new Gson();
              CharList = gson.fromJson(CharParaJsonArray.toString(),new TypeToken<List<WordDictionaryModel>>(){}.getType());

              Log.i("SIZE-2",CharList.size()+"-");
          } catch (Exception e) {
              e.printStackTrace();
          }

          readingView.setCharListData(CharList);
          readingView.initializeContent();

      }

      @Background
      @Override
      public void getLangDataList() {

          try {
              Gson gson = new Gson();
              LanguageList = gson.fromJson(LangParaJsonArray.toString(),new TypeToken<List<String>>(){}.getType());
              Log.i("SIZE",LanguageList.size()+"-");
          } catch (Exception e) {
              e.printStackTrace();
          }

          readingView.setLangListData(LanguageList);
          readingView.initializeLangContent();

      }

      @Background
      @Override
      public void fetchJsonData(String json_datas, String resTypes) {

              resType = resTypes;


          try {
              Gson gson=new Gson();
              WordDictionaryModel selectd_data = gson.fromJson(json_datas,WordDictionaryModel.class);
              //selectd_data.getWd()

              String wordlists = gson.toJson(
                      selectd_data.getWd(),
                      new TypeToken<ArrayList<String>>() {}.getType());

              WordParaJsonArray = new JSONArray(wordlists);
          } catch (Exception e) {
              e.printStackTrace();
          }

  /*

              try {
                //  String filePathStr = FC_Utility.getStoragePath().toString()
                          + "/.FCAInternal/Games/";
                 // InputStream is = new FileInputStream(filePathStr + "word.json");
                  InputStream is = context.getAssets().open("Games/mr_word.json");
                  int size = is.available();
                  byte[] buffer = new byte[size];
                  is.read(buffer);
                  is.close();
                  String jsonStr = new String(buffer);
                  JSONObject jsonObj = new JSONObject(jsonStr);
                  Log.d("DATA",jsonObj.toString());

                  WordParaJsonArray = jsonObj.getJSONArray("words");



              } catch (Exception e) {
                  e.printStackTrace();
              }
              */
          getDataList();
             /* if (resType.equalsIgnoreCase(FC_Constants.PARA_VOCAB_ANDROID))
                  getRandomParaData();
              else
                 getDataList();*/

      }

      @Background
      @Override
      public void fetchCharData(String contentPath, String resTypes) {

          resType = resTypes;
          try {
                /*  String filePathStr = FC_Utility.getStoragePath().toString()
                          + "/.FCAInternal/Games/";
                  InputStream is = new FileInputStream(filePathStr + "word.json");*/
              InputStream is = context.getAssets().open("Games/mr_word_new.json");
              int size = is.available();
              byte[] buffer = new byte[size];
              is.read(buffer);
              is.close();
              String jsonStr = new String(buffer);
              JSONObject jsonObj = new JSONObject(jsonStr);
              Log.d("DATA",jsonObj.toString());

              CharParaJsonArray = jsonObj.getJSONArray("words");



          } catch (Exception e) {
              e.printStackTrace();
          }
          getCahrDataList();
             /* if (resType.equalsIgnoreCase(FC_Constants.PARA_VOCAB_ANDROID))
                  getRandomParaData();
              else
                 getDataList();*/

      }

      @Background
      @Override
      public void fetchLangJsonData(String contentPath, String resTypes) {

          resType = resTypes;
          try {
            /*  String filePathStr = FC_Utility.getStoragePath().toString()
                      + "/.FCAInternal/Games/";
              InputStream is = new FileInputStream(filePathStr + "lang.json");*/
              InputStream is = context.getAssets().open("Games/mr_lang.json");
              int size = is.available();
              byte[] buffer = new byte[size];
              is.read(buffer);
              is.close();
              String jsonStr = new String(buffer);
              JSONObject jsonObj = new JSONObject(jsonStr);
              Log.d("DATA",jsonObj.toString());

              LangParaJsonArray = jsonObj.getJSONArray("data");

          } catch (Exception e) {
              e.printStackTrace();
          }
          getLangDataList();
             /* if (resType.equalsIgnoreCase(FC_Constants.PARA_VOCAB_ANDROID))
                  getRandomParaData();
              else
                 getDataList();*/

      }











      @Background
      @Override
      public void exitDBEntry() {

      }



      @Background
      @Override
      public void addCompletion(String values,String words) {
          try {
              ContentProgress contentProgress = new ContentProgress();
              contentProgress.setProgressPercentage(values);
              contentProgress.setResourceId("WORD_DIC");
              contentProgress.setSessionId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_SESSION, ""));
              contentProgress.setStudentId("" + FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
              contentProgress.setUpdatedDateTime("" + FC_Utility.getCurrentDateTime());
              contentProgress.setLabel("villageword");
              contentProgress.setSentFlag(0);
              AppDatabase.getDatabaseInstance(context).getContentProgressDao().insert(contentProgress);




              KeyWords learntWordss = new KeyWords();
              learntWordss.setResourceId("WORD_DIC");
              learntWordss.setSentFlag(0);
              learntWordss.setStudentId(FastSave.getInstance().getString(FC_Constants.CURRENT_STUDENT_ID, ""));
              learntWordss.setKeyWord(words.toLowerCase());
              learntWordss.setTopic("" + "villageword");
              learntWordss.setWordType("word");
              AppDatabase.getDatabaseInstance(context).getKeyWordDao().insert(learntWordss);


              BackupDatabase.backup(context);
              readingView.nextbuttonaction();

          } catch (Exception e) {
              e.printStackTrace();
          }
      }

  }