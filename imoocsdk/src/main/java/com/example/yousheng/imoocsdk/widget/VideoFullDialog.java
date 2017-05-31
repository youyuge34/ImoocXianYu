package com.example.yousheng.imoocsdk.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.yousheng.imoocsdk.R;
import com.example.yousheng.imoocsdk.activity.AdBrowserActivity;
import com.example.yousheng.imoocsdk.constant.LogUtils;
import com.example.yousheng.imoocsdk.constant.SDKConstant;
import com.example.yousheng.imoocsdk.core.video.VideoAdSlot.AdSDKSlotListener;
import com.example.yousheng.imoocsdk.module.AdValue;
import com.example.yousheng.imoocsdk.report.ReportManager;
import com.example.yousheng.imoocsdk.util.Utils;
import com.example.yousheng.imoocsdk.widget.CostumeVideoView.ADVideoPlayerListener;


/**
 * @author: qndroid
 * @function: 全屏显示视频,全屏后将原先的video实例从view tree中移除，放入dialog的根布局中
 * @date: 16/6/7
 */
public class VideoFullDialog extends Dialog implements ADVideoPlayerListener {

    private static final String TAG = VideoFullDialog.class.getSimpleName();
    private CostumeVideoView mVideoView;

    private Context mContext;
    private RelativeLayout mRootView;
    private ViewGroup mParentView;
    private ImageView mBackButton;

    private AdValue mXAdInstance;
    private int mPosition; //小屏变全屏时的位置
    private FullToSmallListener mListener;
    private boolean isFirst = true;
    //动画要执行的平移值
    private int deltaY;
    private AdSDKSlotListener mSlotListener;
    private Bundle mStartBundle;
    private Bundle mEndBundle; //用于Dialog出入场动画

    public VideoFullDialog(Context context, CostumeVideoView mraidView, AdValue instance,
                           int position) {
        super(context, R.style.dialog_full_screen); //通过style设置，保证dialog全屏
        mContext = context;
        mXAdInstance = instance;
        mPosition = position;
        mVideoView = mraidView;
    }

    /**
     * 主要来初始化控件
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.xadsdk_dialog_video_layout);
        initVideoView();
    }

    public void setViewBundle(Bundle bundle) {
        mStartBundle = bundle;
    }

    public void setListener(FullToSmallListener listener) {
        this.mListener = listener;
    }

//    public void setFrameLoadListener(XADFrameImageLoadListener listener) {
//        this.mFrameLoadListener = listener;
//    }

    public void setSlotListener(AdSDKSlotListener slotListener) {
        this.mSlotListener = slotListener;
    }

    private void initVideoView() {
        mParentView = (RelativeLayout) findViewById(R.id.content_layout);
        mBackButton = (ImageView) findViewById(R.id.xadsdk_player_close_btn);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBackBtn();
            }
        });
        mRootView = (RelativeLayout) findViewById(R.id.root_view);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVideo();
            }
        });
        mRootView.setVisibility(View.INVISIBLE);

        //设置播放器的事件监听为当前对话框，非slot层
        mVideoView.setVideoPlayListener(this);
        mVideoView.mute(false);
        mParentView.addView(mVideoView);
        mParentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mParentView.getViewTreeObserver().removeOnPreDrawListener(this);
                prepareScene();
                runEnterAnimation();
                return true;
            }
        });
    }

    /**
     * 和view的onVisibilityChanged类似，焦点状态改变时回调
     * Called when the window containing this view gains or loses focus
     * 只有dialog就绪，获得焦点时候才能继续播放，太早调用resume会失效
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        LogUtils.i(TAG, "onWindowFocusChanged");
        mVideoView.isShowFullBtn(false); //防止第一次，有些手机仍显示全屏按钮
        if (!hasFocus) {
            //未获得焦点时候
            mPosition = mVideoView.getCurrentPosition();
            mVideoView.pauseForFullScreen();
        } else {
            //获得焦点时
            if (isFirst) {
                //为了适配某些手机不执行resume继续播放，若dialog首次创建且首次获得焦点
                mVideoView.seekAndResume(mPosition);
            } else {
                mVideoView.resume();
            }
        }
        //保证seekAndResume只会执行一次
        isFirst = false;
    }

    /**
     * 窗体消失时候调用
     */
    @Override
    public void dismiss() {
        LogUtils.e(TAG, "dismiss");
        //摆脱父布局，不然小屏时无法放入
        mParentView.removeView(mVideoView);
        super.dismiss();
    }

    /**
     * back键按下的事件监听
     */
    @Override
    public void onBackPressed() {
        onClickBackBtn();
        //super.onBackPressed(); 禁止掉返回键本身的关闭功能,转为自己的关闭效果
    }

    @Override
    public void onClickFullScreenBtn() {
        onClickVideo();
    }

    @Override
    public void onClickVideo() {
        String desationUrl = mXAdInstance.clickUrl;
        if (mSlotListener != null) {
            if (mVideoView.isFrameHidden() && !TextUtils.isEmpty(desationUrl)) {
                mSlotListener.onClickVideo(desationUrl);
                try {
                    ReportManager.pauseVideoReport(mXAdInstance.clickMonitor, mVideoView.getCurrentPosition()
                            / SDKConstant.MILLION_UNIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            //走默认样式
            if (mVideoView.isFrameHidden() && !TextUtils.isEmpty(desationUrl)) {
                Intent intent = new Intent(mContext, AdBrowserActivity.class);
                intent.putExtra(AdBrowserActivity.KEY_URL, mXAdInstance.clickUrl);
                mContext.startActivity(intent);
                try {
                    ReportManager.pauseVideoReport(mXAdInstance.clickMonitor, mVideoView.getCurrentPosition()
                            / SDKConstant.MILLION_UNIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClickBackBtn() {
        runExitAnimator();
    }

    //准备动画所需数据
    private void prepareScene() {
        mEndBundle = Utils.getViewProperty(mVideoView);
        /**
         * 将desationview移到originalview位置处
         */
        deltaY = (mStartBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP)
                - mEndBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP));
        mVideoView.setTranslationY(deltaY);
    }

    //准备入场动画
    private void runEnterAnimation() {
        mVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(0)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        mRootView.setVisibility(View.VISIBLE);
                    }
                })
                .start();
    }

    //准备出场动画
    private void runExitAnimator() {
        //ViewPropertyAnimator类的属性动画
        mVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(deltaY)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        try {
                            ReportManager.exitfullScreenReport(mXAdInstance.event.exitFull.content, mVideoView.getCurrentPosition()
                                    / SDKConstant.MILLION_UNIT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (mListener != null) {
                            //回调slot层处理
                            mListener.getCurrentPlayPosition(mVideoView.getCurrentPosition());
                        }
                    }
                }).start();
    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    @Override
    public void onAdVideoLoadFailed() {
    }

    /**
     * 与小屏播放时处理不一样，单独处理
     */
    @Override
    public void onAdVideoLoadComplete() {
        try {
            int position = mVideoView.getDuration() / SDKConstant.MILLION_UNIT;
            ReportManager.sueReport(mXAdInstance.endMonitor, true, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dismiss();
        if (mListener != null) {
            //通知业务逻辑层slot
            mListener.playComplete();
        }
    }

    @Override
    public void onBufferUpdate(int time) {
        try {
            if (mXAdInstance != null) {
                ReportManager.suReport(mXAdInstance.middleMonitor, time / SDKConstant.MILLION_UNIT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClickPlay() {

    }

    /**
     * @function 与业务逻辑层（Slot）层进行通信
     */
    public interface FullToSmallListener {
        //全屏播放中，点击关闭按钮或者back时回调
        void getCurrentPlayPosition(int position);

        void playComplete();//全屏播放结束时回调
    }
}
