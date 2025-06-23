package com.baofu.gsydemo;

import android.app.Application;

import com.shuyu.gsyvideoplayer.player.PlayerFactory;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
    }
}
