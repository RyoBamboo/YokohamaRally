package com.dr.yokohamarally.models;

public class Root {

    private String title;   // タイトル
    private String summary; // 概要
    private String imageUrl; // 画像URL

    public String getTitle() {
        return this.title;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
