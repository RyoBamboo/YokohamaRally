package com.dr.yokohamarally.models;

import android.graphics.Bitmap;

public class Root {

    private String title;   // タイトル
    private String userImage;
    private String name;   //名前
    private String comments;   // コメント
    private String clearDate;   // 日付
    private String summary; // 概要
    private String imageUrl; // 画像URL
    private String clearRoot; //クリアしたルート
    private Bitmap imageBitmap; // 画像URL
    private int completedCount; // クリア人数
    private int    rate;
    private int    id;
    private int acceptFrag;
    private boolean checkedPoint;

    public String getTitle() {
        return this.title;
    }

    public String getClearDate() {
        return this.clearDate;
    }

    public String getName() {
        return this.name;
    }

    public int getAcceptFrag (){
        return this.acceptFrag;
    }

    public boolean getCheckedPoint() {
        return this.checkedPoint;
    }

    public String getUserImage() {
        return this.userImage;
    }

    public int getCompletedCount() {
        return this.completedCount;
    }

    public String getComments() {
        return this.comments;
    }

    public String getClearRoot() {
        return this.clearRoot;
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

    public void setClearRoot(String clearRoot) {
        this.clearRoot = clearRoot;
    }

    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    public void setUseImage(String userImage) {
        this.userImage = userImage;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAcceptFrag(int acceptFrag) {
        this.acceptFrag = acceptFrag;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClearDate(String clearDate) {
        this.clearDate = clearDate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCheckedPoint( boolean checkedPoint) {
        this.checkedPoint = checkedPoint;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
