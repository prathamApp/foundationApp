package com.pratham.foundation.ui.student_profile;

public interface Student_profile_contract {
    interface Student_profile_view {
        void displayStudentProfileNameAndImage();

       // public void displayStudentProgress(int totalWords, int learnedWords, int wordProgress, int toatalSentence, int learntSentence, int sentenceProgress);
    }

    interface Student_profile_presenter {
        String getStudentProfileName();

        String getStudentProfileImage();

        /*public void calculateStudentProgress();*/

        String getTittleName(String id);

        void setView(Student_profile_contract.Student_profile_view view);
    }
}
