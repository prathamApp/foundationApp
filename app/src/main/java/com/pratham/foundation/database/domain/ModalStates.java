package com.pratham.foundation.database.domain;

import com.google.gson.annotations.SerializedName;


public class ModalStates {
    @SerializedName("StateName")
    private
    String StateName;
    @SerializedName("StateCode")
    private
    String StateCode;
    @SerializedName("ProgramId")
    private
    int ProgramId;

    public String getStateName() {
        return StateName;
    }

    public void setStateName(String stateName) {
        StateName = stateName;
    }

    public String getStateCode() {
        return StateCode;
    }

    public void setStateCode(String stateCode) {
        StateCode = stateCode;
    }

    public int getProgramId() {
        return ProgramId;
    }

    public void setProgramId(int programId) {
        ProgramId = programId;
    }
}