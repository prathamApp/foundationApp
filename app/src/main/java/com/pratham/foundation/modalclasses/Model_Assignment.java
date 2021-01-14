package com.pratham.foundation.modalclasses;

import java.util.ArrayList;

public class Model_Assignment {
    String assignment_desc;
    ArrayList<String> assignment_files;

    public String getAssignment_desc() {
        return assignment_desc;
    }

    public void setAssignment_desc(String assignment_desc) {
        this.assignment_desc = assignment_desc;
    }

    public ArrayList<String> getAssignment_files() {
        return assignment_files;
    }

    public void setAssignment_files(ArrayList<String> assignment_files) {
        this.assignment_files = assignment_files;
    }
}
