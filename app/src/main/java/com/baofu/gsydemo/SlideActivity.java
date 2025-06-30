package com.baofu.gsydemo;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.baofu.gsy.controller.GsyNormalController;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;

public class SlideActivity extends AppCompatActivity {

    private ViewPager2 mViewPager;
    private MyAdapter mAdapter;
    private List<VideoItem> mVideoItems = new ArrayList<>();
    private StandardGSYVideoPlayer mCurrentPlayer;
    private boolean isMobileDataDialogShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_slide);

        mViewPager = findViewById(R.id.viewPager2);

        initVideoData();
        mAdapter = new MyAdapter(mVideoItems);
        mViewPager.setAdapter(mAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(mViewPager, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            private boolean isFirst = true;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (!isFirst) {
                    GSYVideoManager.releaseAllVideos();
                    mCurrentPlayer = null;
                }
                isFirst = false;
                mViewPager.post(() -> playVideoAtPosition(position));
            }
        });
    }

    private void playVideoAtPosition(int position) {
        MyAdapter.MyViewHolder viewHolder = null;
        try {
            viewHolder = (MyAdapter.MyViewHolder) ((RecyclerView) mViewPager.getChildAt(0)).findViewHolderForAdapterPosition(position);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (viewHolder == null || viewHolder.videoPlayer == null) {
            return;
        }

        GsyNormalController player = viewHolder.videoPlayer;
        VideoItem videoItem = (VideoItem) player.getTag();
        if (videoItem == null) return;

        buildPlayerOptions(player, videoItem, position);

        boolean isMobileNetwork = !CommonUtil.isWifiConnected(this);
        if (isMobileNetwork && !isMobileDataDialogShown) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("当前为移动网络，是否继续播放？")
                    .setPositiveButton("继续播放", (dialog, which) -> {
                        isMobileDataDialogShown = true;
                        player.startPlayLogic();
                        dialog.dismiss();
                    })
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .show();
        } else {
            player.startPlayLogic();
        }
    }

    private void buildPlayerOptions(GsyNormalController player, VideoItem item, int position) {
        new GSYVideoOptionBuilder()
                .setUrl(item.getUrl())
                .setVideoTitle(item.getTitle())
                .setNeedShowWifiTip(false)
                .setCacheWithPlay(true)
                .setRotateViewAuto(true)
                .setLockLand(true)
                .setPlayTag("slidePlay")
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setPlayPosition(position)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        if (objects.length > 1 && objects[1] instanceof StandardGSYVideoPlayer) {
                            mCurrentPlayer = (StandardGSYVideoPlayer) objects[1];
                        }
                    }
                })
                .build(player);
        player.getFullscreenButton().setOnClickListener(v -> {
            player.startWindowFullscreen(SlideActivity.this, true, true);
        });
    }

    private void initVideoData() {
        mVideoItems.add(new VideoItem("https://play.modujx10.com/20230816/4ioQdi8e/1546kb/hls/index.m3u8", "名侦探柯南1"));
        mVideoItems.add(new VideoItem("https://play.modujx10.com/20230816/BwDSrrJh/1582kb/hls/index.m3u8", "名侦探柯南2"));
        mVideoItems.add(new VideoItem("https://play.modujx10.com/20230816/MOsdWHYZ/1572kb/hls/index.m3u8", "名侦探柯南3"));
        mVideoItems.add(new VideoItem("https://play.modujx10.com/20230816/kgap2oQ6/1633kb/hls/index.m3u8", "名侦探柯南4"));
        mVideoItems.add(new VideoItem("https://play.modujx10.com/20230816/JhIS1qbV/1637kb/hls/index.m3u8", "名侦探柯南5"));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mCurrentPlayer != null) {
            mCurrentPlayer.onConfigurationChanged(this, newConfig, null, true, true);
        }
    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
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

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<VideoItem> list;

        public MyAdapter(List<VideoItem> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SlideActivity.this).inflate(R.layout.item_slide_video, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.bindData(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            GsyNormalController videoPlayer;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                videoPlayer = itemView.findViewById(R.id.video_player);
            }

            public void bindData(VideoItem item) {
                videoPlayer.setTag(item);
            }
        }
    }
}