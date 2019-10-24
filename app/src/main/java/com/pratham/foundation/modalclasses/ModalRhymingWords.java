
package com.pratham.foundation.modalclasses;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ModalRhymingWords implements Serializable {


    @SerializedName("questionId")
    @Expose
    private String questionId;

    @SerializedName("wordCategory")
    @Expose
    private String wordCategory;

    @SerializedName("word")
    @Expose
    private String word;

    @SerializedName("wordAudio")
    @Expose
    private String wordAudio;

    @SerializedName("wordList")
    @Expose
    private List<ModalRhymingWords> wordList;

    @SerializedName("correctRead")
    @Expose
    private boolean correctRead;

    @SerializedName("readOut")
    @Expose
    private boolean readOut;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getWordCategory() {
        return wordCategory;
    }

    public void setWordCategory(String wordCategory) {
        this.wordCategory = wordCategory;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWordAudio() {
        return wordAudio;
    }

    public void setWordAudio(String wordAudio) {
        this.wordAudio = wordAudio;
    }

    public List<ModalRhymingWords> getWordList() {
        return wordList;
    }

    public void setWordList(List<ModalRhymingWords> wordList) {
        this.wordList = wordList;
    }

    public boolean isCorrectRead() {
        return correctRead;
    }

    public void setCorrectRead(boolean correctRead) {
        this.correctRead = correctRead;
    }

    public boolean isReadOut() {
        return readOut;
    }

    public void setReadOut(boolean readOut) {
        this.readOut = readOut;
    }
}