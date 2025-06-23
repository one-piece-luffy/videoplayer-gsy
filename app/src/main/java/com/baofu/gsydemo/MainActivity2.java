package com.baofu.gsydemo;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.media3.exoplayer.SeekParameters;

import com.baofu.gsy.controller.listener.GSYSimpleListener;
import com.baofu.gsydemo.databinding.ActivityMainBinding;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

public class MainActivity2 extends AppCompatActivity {

    ActivityMainBinding binding;
    private boolean isPlay;
    private boolean isPause;


    @Override
    protected void onStart() {
        super.onStart();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.detailPlayer.togglePlay();
            }
        });



        String url = "https://bfikuncdn.com/20240702/gUjZmh3D/index.m3u8";
//        String url = "https://m8t.vboku.com/20230325/NA3vw31V/index.m3u8?sign=S7cRpZ10IZtUDxDIRr%252FFeRRnyDIsH1TZ9IZe2hRv2OM%253D";
//        String url = "https://s10-e1.etcbbh.xyz/ppot/_definst_/mp4:s10/kvod/dhp-jscd2j-no-transyouke-006B409EFqpu5.mp4/chunklist.m3u8?vendtime=1743319171&vhash=WwHcpchsDu8UQWTkkIqZZd4xukWRpa1GfBlgfaF0OMY=&vCustomParameter=0_103.169.127.152_HK_1_0&lb=7fb27a2dd8a388ee580c4103717386bb&us=1&proxy=Sp4mBMKnBcLqOs9YQ2vuUNfyEPYO5hAObpAwCR4nD9Sy7bwV7CnC2rbCIvbT6DYOcekU7bwV7CnC2rbCIvqPc9dOM5XBcDlRNnpCJ0jPJ4kT6PYPs9YOYvZRsD&vv=38fa9409a68d91c6369dee4bb8e91cb7&pub=CJSqCp4qDZCrDYupDJTVI4jVCJ0pBZ4sEIunCZSkCJKoNsKoDZWmOZWnCc8vDZHcP38vOJTbEJ0vOpHaCJapDs4nNs8sD68tEJGuEJGoDcOmC3CrDJ4qE3GtDJDYOJbaP31Y";

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.ic_launcher);




        Map<String, String> header = new HashMap<>();
        header.put("ee", "33");
        header.put("allowCrossProtocolRedirects", "true");
        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
        gsyVideoOption.setThumbImageView(imageView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setUrl(url)
                .setMapHeadData(header)
                .setCacheWithPlay(false)
                .setVideoTitle("测试视频")
                .setLockClickListener(new LockClickListener() {
                    @Override
                    public void onClick(View view, boolean lock) {
                        if (binding.detailPlayer.orientationUtils != null) {
                            //配合下方的onConfigurationChanged
                            binding.detailPlayer.orientationUtils.setEnable(!lock);
                        }
                    }
                })
                .setGSYVideoProgressListener(new GSYVideoProgressListener() {
                    @Override
                    public void onProgress(long progress, long secProgress, long currentPosition, long duration) {
                        Debuger.printfLog(" progress " + progress + " secProgress " + secProgress + " currentPosition " + currentPosition + " duration " + duration);
                    }
                })
                .build(binding.detailPlayer);

        binding.detailPlayer.setListener(new GSYSimpleListener(){
            @Override
            public void onPrepared(String url, Object... objects) {
                Debuger.printfError("***** onPrepared **** " + objects[0]);
                Debuger.printfError("***** onPrepared **** " + objects[1]);
                super.onPrepared(url, objects);
                //开始播放了才能旋转和全屏
//                        orientationUtils.setEnable(binding.detailPlayer.isRotateWithSystem());
                isPlay = true;

                //设置 seek 的临近帧。
                if (binding.detailPlayer.getGSYVideoManager().getPlayer() instanceof Exo2PlayerManager) {
                    ((Exo2PlayerManager) binding.detailPlayer.getGSYVideoManager().getPlayer()).setSeekParameter(SeekParameters.NEXT_SYNC);
                    Debuger.printfError("***** setSeekParameter **** ");
                }
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
                Debuger.printfError("***** onEnterFullscreen **** " + objects[0]);//title
                Debuger.printfError("***** onEnterFullscreen **** " + objects[1]);//当前全屏player
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                super.onClickStartError(url, objects);
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                Debuger.printfError("***** onQuitFullscreen **** " + objects[0]);//title
                Debuger.printfError("***** onQuitFullscreen **** " + objects[1]);//当前非全屏player

            }
        });

    }



    @Override
    public void onBackPressed() {

        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
//        if (orientationUtils != null) {
//            orientationUtils.backToProtVideo();
//        }

        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume(false);
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            getCurPlay().release();
        }
        //GSYPreViewManager.instance().releaseMediaPlayer();

    }


    /**
     * orientationUtils 和  binding.detailPlayer.onConfigurationChanged 方法是用于触发屏幕旋转的
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
//        if (isPlay && !isPause) {
//            binding.detailPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
//        }
    }


    private void resolveNormalVideoUI() {
        //增加title
        binding.detailPlayer.getTitleTextView().setVisibility(View.GONE);
        binding.detailPlayer.getBackButton().setVisibility(View.GONE);
    }

    private GSYVideoPlayer getCurPlay() {
        if (binding.detailPlayer.getFullWindowPlayer() != null) {
            return binding.detailPlayer.getFullWindowPlayer();
        }
        return binding.detailPlayer;
    }


}