package com.pratham.foundation.ui.student_profile;

public interface Student_profile_contract {
    interface Student_profile_view {
        public void displayStudentProfileNameAndImage();

       // public void displayStudentProgress(int totalWords, int learnedWords, int wordProgress, int toatalSentence, int learntSentence, int sentenceProgress);
    }

    interface Student_profile_presenter {
        public String getStudentProfileName();

        public String getStudentProfileImage();

        /*public void calculateStudentProgress();*/

        public String getTittleName(String id);
    }
}
