package com.pratham.foundation.modalclasses;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class MatchThePair implements Serializable
{
    private String paraTitle;

    private String langText;

    private String paraText;

    private String paraLang;

    @NonNull
    @PrimaryKey
    private String id;

    public String getParaTitle ()
    {
        return paraTitle;
    }

    public void setParaTitle (String paraTitle)
    {
        this.paraTitle = paraTitle;
    }

    public String getLangText ()
    {
        return langText;
    }

    public void setLangText (String langText)
    {
        this.langText = langText;
    }

    public String getParaText ()
    {
        return paraText;
    }

    public void setParaText (String paraText)
    {
        this.paraText = paraText;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getParaLang() {
        return paraLang;
    }

    public void setParaLang(String paraLang) {
        this.paraLang = paraLang;
    }
}

