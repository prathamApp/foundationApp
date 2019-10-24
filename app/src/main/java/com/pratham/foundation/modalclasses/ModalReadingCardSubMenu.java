
package com.pratham.foundation.modalclasses;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ModalReadingCardSubMenu implements Serializable {


    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("cardImage")
    @Expose
    private String cardImage;

    @SerializedName("cardAudio")
    @Expose
    private String cardAudio;

    @SerializedName("type")
    @Expose
    private String type;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCardImage() {
        return cardImage;
    }

    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }

    public String getCardAudio() {
        return cardAudio;
    }

    public void setCardAudio(String cardAudio) {
        this.cardAudio = cardAudio;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}