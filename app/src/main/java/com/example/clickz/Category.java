package com.example.clickz;

public class Category {
    private String title;
    private  int imageRes;

    public Category(String title, int imageRes) {
        this.title = title;
        this.imageRes = imageRes;
    }

    public Category() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }
}
