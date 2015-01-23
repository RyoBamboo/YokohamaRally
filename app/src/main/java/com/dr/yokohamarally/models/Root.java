package com.dr.yokohamarally.models;

import android.graphics.Bitmap;

public class Root {

    private String title;   // タイトル
    private String summary; // 概要
    private String imageUrl; // 画像URL
    private Bitmap imageBitmap; // 画像URL
    private int    rate;
    private int    id;

    public String getTitle() {
        return this.title;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public int getRate() {
        return this.rate;
    }

    public int getId() {
        return this.id;
    }

    public Bitmap getImageBitmap() {
        return this.imageBitmap;
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

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
