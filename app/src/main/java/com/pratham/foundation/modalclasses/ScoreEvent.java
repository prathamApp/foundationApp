package com.pratham.foundation.modalclasses;

public class ScoreEvent {
    String message;
    int totalCount;
    int scoredMarks;

    public ScoreEvent(String message, int totalCount, int scoredMarks) {
        this.message = message;
        this.totalCount = totalCount;
        this.scoredMarks = scoredMarks;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getScoredMarks() {
        return scoredMarks;
    }

    public void setScoredMarks(int scoredMarks) {
        this.scoredMarks = scoredMarks;
    }
}
