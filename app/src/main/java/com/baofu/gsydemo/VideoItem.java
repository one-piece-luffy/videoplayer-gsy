package com.baofu.gsydemo;

public class VideoItem {
    private String url;
    private String title;

    public VideoItem(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }
}