package com.baofu.gsydemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.baofu.gsy.controller.GsyNormalController;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.ArrayList;
import java.util.List;

public class SlideActivity extends AppCompatActivity {

    private ViewPager2 mViewPager;
    private SlideVideoAdapter mAdapter;
    private List<VideoItem> mVideoItems = new ArrayList<>();
    private int mCurrentPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        mViewPager = findViewById(R.id.viewPager2);
        initVideoData();
        mAdapter = new SlideVideoAdapter(this, mVideoItems);
        mViewPager.setAdapter(mAdapter);

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (mCurrentPosition != -1) {
                    GSYVideoManager.onPause();
                }
                mViewPager.postDelayed(() -> startPlay(position), 80);
                mCurrentPosition = position;
            }
        });

        mViewPager.post(() -> startPlay(0));
    }

    private void initVideoData() {
        mVideoItems.add(new VideoItem("https://play.modujx10.com/20230816/4ioQdi8e/1546kb/hls/index.m3u8", "名侦探柯南1"));
        mVideoItems.add(new VideoItem("https://play.modujx10.com/20230816/BwDSrrJh/1582kb/hls/index.m3u8", "名侦探柯南2"));
        mVideoItems.add(new VideoItem("https://play.modujx10.com/20230816/MOsdWHYZ/1572kb/hls/index.m3u8", "名侦探柯南3"));
        mVideoItems.add(new VideoItem("https://play.modujx10.com/20230816/kgap2oQ6/1633kb/hls/index.m3u8", "名侦探柯南4"));
        mVideoItems.add(new VideoItem("https://play.modujx10.com/20230816/JhIS1qbV/1637kb/hls/index.m3u8", "名侦探柯南5"));
    }

    private void startPlay(int position) {
        RecyclerView.ViewHolder viewHolder = ((RecyclerView) mViewPager.getChildAt(0)).findViewHolderForAdapterPosition(position);
        if (viewHolder instanceof SlideVideoAdapter.VideoViewHolder) {
            GsyNormalController player = ((SlideVideoAdapter.VideoViewHolder) viewHolder).player;
            player.startPlayLogic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GSYVideoManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }
}