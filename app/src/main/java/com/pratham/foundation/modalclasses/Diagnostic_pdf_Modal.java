
package com.pratham.foundation.modalclasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Diagnostic_pdf_Modal {

    @SerializedName("enrollment_id")
    @Expose
    private String enrollment_id;
    @SerializedName("file_path")
    @Expose
    private String file_path;
    @SerializedName("student_name")
    @Expose
    private String student_name;
    @SerializedName("subject_name")
    @Expose
    private String subject_name;

    public String getEnrollment_id() {
        return enrollment_id;
    }

    public void setEnrollment_id(String enrollment_id) {
        this.enrollment_id = enrollment_id;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }
}
