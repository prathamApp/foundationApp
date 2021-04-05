package com.pratham.foundation.modalclasses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Model_CourseExperience {
    @SerializedName("assignments")
    private List<Model_Assignment> assignments;
    @SerializedName("words_learnt")
    private String words_learnt;
    @SerializedName("assignments_completed")
    private String assignments_completed;
    @SerializedName("assignments_description")
    private String assignments_description;
    @SerializedName("coach_comments")
    private String coach_comments;
    @SerializedName("coach_verification_date")
    private String coach_verification_date;
    @SerializedName("coach_image")
    private String coach_image;
    @SerializedName("assignment_submission_date")
    private String assignment_submission_date;
    @SerializedName("status")
    private String status;
    @SerializedName("sessionId")
    private String sessionId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignment_submission_date() {
        return assignment_submission_date;
    }

    public void setAssignment_submission_date(String assignment_submission_date) {
        this.assignment_submission_date = assignment_submission_date;
    }

    public List<Model_Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Model_Assignment> assignments) {
        this.assignments = assignments;
    }

    public String getWords_learnt() {
        return words_learnt;
    }

    public void setWords_learnt(String words_learnt) {
        this.words_learnt = words_learnt;
    }

    public String getAssignments_completed() {
        return assignments_completed;
    }

    public void setAssignments_completed(String assignments_completed) {
        this.assignments_completed = assignments_completed;
    }

    public String getAssignments_description() {
        return assignments_description;
    }

    public void setAssignments_description(String assignments_description) {
        this.assignments_description = assignments_description;
    }

    public String getCoach_comments() {
        return coach_comments;
    }

    public void setCoach_comments(String coach_comments) {
        this.coach_comments = coach_comments;
    }

    public String getCoach_verification_date() {
        return coach_verification_date;
    }

    public void setCoach_verification_date(String coach_verification_date) {
        this.coach_verification_date = coach_verification_date;
    }

    public String getCoach_image() {
        return coach_image;
    }

    public void setCoach_image(String coach_image) {
        this.coach_image = coach_image;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
