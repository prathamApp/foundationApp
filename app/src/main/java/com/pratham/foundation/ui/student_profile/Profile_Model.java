package com.pratham.foundation.ui.student_profile;

public class Profile_Model {
    private String tittle;
    private String count;
    private String type;
    private String node;
    private int progress;

    public Profile_Model(String tittle, String count, String type, String node, int progress) {
        this.tittle = tittle;
        this.count = count;
        this.type = type;
        this.node = node;
        this.progress = progress;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }
}
