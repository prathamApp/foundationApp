
package com.pratham.foundation.modalclasses.RaspModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModalRaspContentNew {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("next")
    @Expose
    private String next;
    @SerializedName("previous")
    @Expose
    private String previous;
    @SerializedName("results")
    @Expose
    private List<Modal_RaspResult> modalRaspResults;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String  getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<Modal_RaspResult> getModalRaspResults() {
        return modalRaspResults;
    }

    public void setModalRaspResults(List<Modal_RaspResult> modalRaspResults) {
        this.modalRaspResults = modalRaspResults;
    }

}
