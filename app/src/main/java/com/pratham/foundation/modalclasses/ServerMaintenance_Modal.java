package com.pratham.foundation.modalclasses;

/**
 * Created by Anki on 12/11/2018.
 */

public class ServerMaintenance_Modal {


    String app_version;
    String status_code;
    String status;
    String maintenance_closing_time;
    String message;
    String last_updated;


    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMaintenance_closing_time() {
        return maintenance_closing_time;
    }

    public void setMaintenance_closing_time(String maintenance_closing_time) {
        this.maintenance_closing_time = maintenance_closing_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }
}
