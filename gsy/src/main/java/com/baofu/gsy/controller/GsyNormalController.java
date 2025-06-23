package com.baofu.gsy.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baofu.gsy.R;
import com.baofu.gsy.controller.constants.SpeedInterface;
import com.baofu.gsy.controller.listener.GSYSimpleListener;
import com.baofu.gsy.controller.listener.OnSpeedClickListener;
import com.baofu.gsy.controller.utils.AvSharePreference;
import com.baofu.gsy.controller.widget.SpeedBottomSheetDialog;
import com.baofu.gsy.controller.widget.SpeedDialog;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

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

    protected View mVolumeView;
    protected ProgressBar mDialogVolumeProgressBar;
    protected View mBrightnessView;
    protected TextView mBrightnessDialogTv;

    private String mCurrentSpeed = SpeedInterface.sp1_0;

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
                OnSpeedClickListener speedClickListener = new OnSpeedClickListener() {
                    @Override
                    public void onSpeedClick(String speed) {
                        lastSpeed = speed;
                        AvSharePreference.saveLastPlaySpeed(mContext, speed);
                        setMySpeed(speed);
                    }
                };
                if (isIfCurrentIsFullscreen()) {
                    new SpeedDialog(mContext, getSpeed() + "", speedClickListener).show();
                } else {
                    new SpeedBottomSheetDialog(mContext, getSpeed() + "", speedClickListener).show();
                }
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

    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        updateSpeedUiOnly();
        updateRatioUiOnly();
    }

    @Override
    protected void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        updateSpeedUiOnly();
        updateRatioUiOnly();
    }

    @Override
    protected void changeUiToCompleteShow() {
        super.changeUiToCompleteShow();
        updateSpeedUiOnly();
        updateRatioUiOnly();
    }

    private void updateSpeedUiOnly() {
        float currentSpeed = getSpeed();
        String speedText;

        if (currentSpeed == 0.75f) {
            speedText = getResources().getString(R.string.gsy_speed_0_75);
        } else if (currentSpeed == 1.25f) {
            speedText = getResources().getString(R.string.gsy_speed_1_25);
        } else if (currentSpeed == 1.5f) {
            speedText = getResources().getString(R.string.gsy_speed_1_5);
        } else if (currentSpeed == 1.75f) {
            speedText = getResources().getString(R.string.gsy_speed_1_75);
        } else if (currentSpeed == 2.0f) {
            speedText = getResources().getString(R.string.gsy_speed_2_0);
        } else if (currentSpeed == 3.0f) {
            speedText = getResources().getString(R.string.gsy_speed_3_0);
        } else if (currentSpeed == 4.0f) {
            speedText = getResources().getString(R.string.gsy_speed_4_0);
        } else {
            speedText = getResources().getString(R.string.gsy_speed);
        }
        setTvSpeed(speedText);
    }

    private void updateRatioUiOnly() {
        int currentShowType = GSYVideoType.getShowType();
        if (currentShowType == GSYVideoType.SCREEN_TYPE_16_9) {
            mType = 1;
            tv_av_scale.setText("16:9");
        } else if (currentShowType == GSYVideoType.SCREEN_TYPE_4_3) {
            mType = 2;
            tv_av_scale.setText("4:3");
        } else if (currentShowType == GSYVideoType.SCREEN_TYPE_FULL) {
            mType = 3;
            tv_av_scale.setText(R.string.gsy_full);
        } else if (currentShowType == GSYVideoType.SCREEN_MATCH_FULL) {
            mType = 4;
            tv_av_scale.setText(R.string.gsy_fill);
        } else {
            mType = 0;
            tv_av_scale.setText(R.string.gsy_video_default);
        }
    }

    /**
     * 重写父类方法，亮度调节框
     */
    @Override
    protected void showBrightnessDialog(float percent) {
        if (mBrightnessView == null) {
            mBrightnessView = LayoutInflater.from(getActivityContext()).inflate(com.shuyu.gsyvideoplayer.R.layout.video_brightness, null);
            mBrightnessDialogTv = mBrightnessView.findViewById(com.shuyu.gsyvideoplayer.R.id.app_video_brightness);
            addView(mBrightnessView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        if (mBrightnessView.getVisibility() != VISIBLE) {
            mBrightnessView.setVisibility(VISIBLE);
        }
        if (mBrightnessDialogTv != null) {
            String text = ((int) (percent * 100)) + "%";
            mBrightnessDialogTv.setText(text);
        }
    }

    @Override
    protected void dismissBrightnessDialog() {
        super.dismissBrightnessDialog();
        if (mBrightnessView != null) {
            mBrightnessView.setVisibility(GONE);
        }
    }

    /**
     * 重写父类方法，音量调节框
     */
    @Override
    protected void showVolumeDialog(float deltaY, int volumePercent) {
        if (mVolumeView == null) {
            mVolumeView = LayoutInflater.from(getActivityContext()).inflate(com.shuyu.gsyvideoplayer.R.layout.video_volume_dialog, null);
            mDialogVolumeProgressBar = mVolumeView.findViewById(com.shuyu.gsyvideoplayer.R.id.volume_progressbar);
            addView(mVolumeView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        if (mVolumeView.getVisibility() != VISIBLE) {
            mVolumeView.setVisibility(VISIBLE);
        }
        if (mDialogVolumeProgressBar != null) {
            mDialogVolumeProgressBar.setProgress(volumePercent);
        }
    }

    @Override
    protected void dismissVolumeDialog() {
        super.dismissVolumeDialog();
        if (mVolumeView != null) {
            mVolumeView.setVisibility(GONE);
        }
    }

    public void setTvSpeed(String speed) {
        if (tv_speed != null) {
            tv_speed.setText(speed);
        }
    }

    public void setMySpeed(String speed) {
        Log.e("asdf", "speed:" + speed);
        if (speed == null) {
            speed = "1.0";
        }

        if (!isLongPress) {
            mCurrentSpeed = speed;
        }

        // 转为小写处理
        switch (speed.toLowerCase()) {

            case SpeedInterface.sp0_75:
                setSpeed(0.75f);
                setTvSpeed(getResources().getString(R.string.gsy_speed_0_75));
                break;
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
                setTvSpeed(getResources().getString(R.string.gsy_speed));
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

    @Override
    protected void touchSurfaceUp() {
        super.touchSurfaceUp();
        isLongPress = false;
        Log.e("asdf", "set long false");
        setMySpeed(mCurrentSpeed);
        mLongSpeed.setVisibility(View.GONE);
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
    }

    @Override
    protected void touchLongPress(MotionEvent e) {
        super.touchLongPress(e);
        if (!mCanLongPress)
            return;
        isLongPress = true;
        Log.e("asdf", "set long true");
        mLongSpeed.setVisibility(View.VISIBLE);
        mAnimationDrawable = (AnimationDrawable) mIvQuick.getDrawable();
        mAnimationDrawable.start();
        Log.e("asdf", "touchLongPress:" + 3.0);
        setMySpeed(SpeedInterface.sp3_0);
    }

    @Override
    protected void setTextAndProgress(int secProgress) {
        Log.e("asdf", "isLongPress:" + isLongPress);
        super.setTextAndProgress(secProgress);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
        dismissBrightnessDialog();
        dismissVolumeDialog();
    }

    public void togglePlay() {
        clickStartIcon();
    }

}
