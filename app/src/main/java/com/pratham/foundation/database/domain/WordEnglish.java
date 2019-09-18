package com.pratham.foundation.database.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Anki on 10/8/2018.
 */

@Entity
public class WordEnglish implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int wordId;

    //common fields
    private String word;
    private int size;
    private String start;
    private String type;
    private String meaning;

	
  /*  //English
    private String vowelCnt;
    @TypeConverters(JSONArrayToString.class)
    private String[] vowels;
    @TypeConverters(JSONArrayToString.class)
    private String[] vowelTogether;
    private String blendCnt;
    @TypeConverters(JSONArrayToString.class)
    private String[] blends;

   */

    @SerializedName("label")
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @SerializedName("vowelTogether")
    @TypeConverters(JSONArrayToString.class)
    private String[] vowelTogether;

    @SerializedName("blends")
    @TypeConverters(JSONArrayToString.class)
    private String[] blends;

    @SerializedName("vowels")
    @TypeConverters(JSONArrayToString.class)
    private String[] vowels;

    @SerializedName("blendCnt")
    private int blendCnt;

    @SerializedName("vowelCnt")
    private int vowelCnt;

    @SerializedName("id")
    private String uuid;

    @SerializedName("synid")
    private String synid;

    @NonNull
    public int getWordId() {
        return wordId;
    }

    public void setWordId(@NonNull int wordId) {
        this.wordId = wordId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSynid() {
        return synid;
    }

    public void setSynid(String synid) {
        this.synid = synid;
    }

    public String[] getVowelTogether() {
        return vowelTogether;
    }

    public void setVowelTogether(String[] vowelTogether) {
        this.vowelTogether = vowelTogether;
    }

    public String[] getBlends() {
        return blends;
    }

    public void setBlends(String[] blends) {
        this.blends = blends;
    }

    public String[] getVowels() {
        return vowels;
    }

    public void setVowels(String[] vowels) {
        this.vowels = vowels;
    }

    public int getBlendCnt() {
        return blendCnt;
    }

    public void setBlendCnt(int blendCnt) {
        this.blendCnt = blendCnt;
    }

    public int getVowelCnt() {
        return vowelCnt;
    }

    public void setVowelCnt(int vowelCnt) {
        this.vowelCnt = vowelCnt;
    }
}
