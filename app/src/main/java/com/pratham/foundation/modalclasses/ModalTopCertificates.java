
package com.pratham.foundation.modalclasses;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class ModalTopCertificates implements Serializable {


    @SerializedName("certiName")
    @Expose
    private String certiName;

    @SerializedName("totalPerc")
    @Expose
    private String totalPerc;


    public String getCertiName() { return certiName; }

    public void setCertiName(String certiName) {
        this.certiName = certiName;
    }

    public String getTotalPerc() {
        return totalPerc;
    }

    public void setTotalPerc(String totalPerc) {
        this.totalPerc = totalPerc;
    }
}