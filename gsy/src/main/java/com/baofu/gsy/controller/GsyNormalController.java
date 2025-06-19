package com.baofu.gsy.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baofu.gsy.R;
import com.baofu.gsy.controller.constants.SpeedInterface;
import com.baofu.gsy.controller.listener.GSYSimpleListener;
import com.baofu.gsy.controller.listener.OnSpeedClickListener;
import com.baofu.gsy.controller.utils.AvSharePreference;
import com.baofu.gsy.controller.widget.SpeedDialog;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.Map;

public class GsyNormalController extends StandardGSYVideoPlayer {

    ViewGroup mBottomToolsLayout;
    TextView tv_speed;
    TextView tv_av_scale;
    ImageView fullscreen;
    public OrientationUtils orientationUtils;
    public GSYSimpleListener listener;
    //记住切换数据源类型
    private int mType = 0;
    protected OnSpeedClickListener onSpeedClickListener;
    //上一次选择的播放速度
    String lastSpeed;
    //记住播放速度
    boolean remindSpeed = true;
    //是否长按状态
    boolean isLongPress;
    /**
     * 是否可以长按
     */
    private boolean mCanLongPress = true;
    ViewGroup mLongSpeed;
    ImageView mIvQuick;
    AnimationDrawable mAnimationDrawable;

    public GsyNormalController(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public GsyNormalController(Context context) {
        super(context);
    }

    public GsyNormalController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListener(GSYSimpleListener listener) {
        this.listener = listener;
    }

    public void setOnSpeedClickListener(OnSpeedClickListener onSpeedClickListener) {
        this.onSpeedClickListener = onSpeedClickListener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.gsy_video_normal;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mBottomToolsLayout = findViewById(R.id.bottom_tools_layout);
        tv_speed = findViewById(R.id.tv_speed);
        tv_av_scale = findViewById(R.id.tv_av_scale);
        fullscreen = findViewById(R.id.fullscreen);
        mLongSpeed = findViewById(R.id.long_speed);
        mIvQuick = findViewById(R.id.iv_quick);
        lastSpeed = AvSharePreference.getLastPlaySpeed(context);
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils((Activity) getActivityContext(), this);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(true);
        tv_av_scale.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType == 0) {
                    mType = 1;
                } else if (mType == 1) {
                    mType = 2;
                } else if (mType == 2) {
                    mType = 3;
                } else if (mType == 3) {
                    mType = 4;
                } else if (mType == 4) {
                    mType = 0;
                }
                resolveTypeUI();
            }
        });
        tv_speed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new SpeedDialog(mContext, getSpeed() + "", new OnSpeedClickListener() {
                    @Override
                    public void onSpeedClick(String speed) {
                        lastSpeed = speed;
                        AvSharePreference.saveLastPlaySpeed(mContext, speed);
                        setMySpeed(speed);
                    }
                }).show();

            }
        });
        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
                // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
                orientationUtils.resolveByClick();

                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                startWindowFullscreen(getActivityContext(), false, true);
            }
        });
        setVideoAllCallBack(new GSYSampleCallBack() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                if (listener != null) {
                    listener.onPrepared(url, objects);
                }
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
                if (listener != null) {
                    listener.onEnterFullscreen(url, objects);
                }
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
                if (listener != null) {
                    listener.onAutoComplete(url, objects);
                }
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                super.onClickStartError(url, objects);
                if (listener != null) {
                    listener.onClickStartError(url, objects);
                }
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);

                // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
                // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
                if (orientationUtils != null) {
                    orientationUtils.backToProtVideo();
                }
                lastSpeed = AvSharePreference.getLastPlaySpeed(context);
                setMySpeed(lastSpeed);

                if (listener != null) {
                    listener.onQuitFullscreen(url, objects);
                }
            }
        });

    }



    public void setTvSpeed(String speed) {
        if (tv_speed != null) {
            tv_speed.setText(speed);
        }
    }

    public void setMySpeed(String speed) {
        Log.e("asdf","speed:"+speed);
        // 转为小写处理
        switch (speed.toLowerCase()) {

            case SpeedInterface.sp0_75:
                setSpeed(0.75f);
                setTvSpeed(getResources().getString(R.string.gsy_speed_0_75));
                break;
//            case SpeedInterface.sp1_0:
//                mControlWrapper.setSpeed(1f);
//                setTvSpeed(getResources().getString(R.string.av_speed_1_0));
//                break;
            case SpeedInterface.sp1_25:
                setSpeed(1.25f);
                setTvSpeed(getResources().getString(R.string.gsy_speed_1_25));
                break;
            case SpeedInterface.sp1_50:
                setSpeed(1.5f);
                setTvSpeed(getResources().getString(R.string.gsy_speed_1_5));
                break;
            case SpeedInterface.sp1_75:
                setSpeed(1.75f);
                setTvSpeed(getResources().getString(R.string.gsy_speed_1_75));
                break;
            case SpeedInterface.sp2_0:
                setSpeed(2f);
                setTvSpeed(getResources().getString(R.string.gsy_speed_2_0));
                break;
            case SpeedInterface.sp3_0:
                setSpeed(3f);
                setTvSpeed(getResources().getString(R.string.gsy_speed_3_0));
                break;
            case SpeedInterface.sp4_0:
                setSpeed(4f);
                setTvSpeed(getResources().getString(R.string.gsy_speed_4_0));
                break;
            default:
                setSpeed(1f);
                setTvSpeed(getResources().getString(R.string.gsy_speed_1_0));
                break;
        }
    }

    /**
     * 显示比例
     * 注意，GSYVideoType.setShowType是全局静态生效，除非重启APP。
     */
    private void resolveTypeUI() {
        if (!mHadPlay) {
            return;
        }
        if (mType == 1) {
            tv_av_scale.setText("16:9");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9);
        } else if (mType == 2) {
            tv_av_scale.setText("4:3");
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_4_3);
        } else if (mType == 3) {
            tv_av_scale.setText(R.string.gsy_full);
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        } else if (mType == 4) {
            tv_av_scale.setText(R.string.gsy_fill);
            GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL);
        } else if (mType == 0) {
            tv_av_scale.setText(R.string.gsy_video_default);
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        }
        changeTextureViewShowType();
        if (mTextureView != null)
            mTextureView.requestLayout();
    }


//    @Override
//    public GSYVideoViewBridge getGSYVideoManager() {
//        GSYVideoManager.instance().initContext(getContext().getApplicationContext());
//        return GSYVideoManager.instance();
//    }


    @Override
    protected void touchSurfaceUp() {
        super.touchSurfaceUp();
            isLongPress=false;
        Log.e("asdf","set long false");

        mLongSpeed.setVisibility(View.GONE);
            if(mAnimationDrawable!=null){
                mAnimationDrawable.stop();
            }
    }

    @Override
    protected void touchLongPress(MotionEvent e) {
        super.touchLongPress(e);
        if(!mCanLongPress)
            return;
        isLongPress = true;
        Log.e("asdf","set long true");
        mLongSpeed.setVisibility(View.VISIBLE);
        mAnimationDrawable = (AnimationDrawable) mIvQuick.getDrawable();
        mAnimationDrawable.start();
        Log.e("asdf","touchLongPress:"+3.0);
        setMySpeed(SpeedInterface.sp3_0);
    }

    @Override
    protected void setTextAndProgress(int secProgress) {
        Log.e("asdf","isLongPress:"+isLongPress);
        super.setTextAndProgress(secProgress);
        if (remindSpeed && !isLongPress && !TextUtils.isEmpty(lastSpeed) && !lastSpeed.equals(String.valueOf(getSpeed()))) {
            Log.e("asdf","setTextAndProgress:"+lastSpeed);
            setMySpeed(lastSpeed);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    public void togglePlay() {
        clickStartIcon();
    }

}
