package com.example.yousheng.imoocsdk.core.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.yousheng.imoocsdk.activity.AdBrowserActivity;
import com.example.yousheng.imoocsdk.constant.LogUtils;
import com.example.yousheng.imoocsdk.constant.SDKConstant;
import com.example.yousheng.imoocsdk.core.AdParameters;
import com.example.yousheng.imoocsdk.module.AdValue;
import com.example.yousheng.imoocsdk.report.ReportManager;
import com.example.yousheng.imoocsdk.util.Utils;
import com.example.yousheng.imoocsdk.widget.CostumeVideoView;
import com.example.yousheng.imoocsdk.widget.VideoFullDialog;

/**
 * Created by yousheng on 17/5/14.
 *
 * @function: 广告业务逻辑层
 */

public class VideoAdSlot implements CostumeVideoView.ADVideoPlayerListener {
    private Context mContext;
    /**
     * UI
     */
    private CostumeVideoView mVideoView;
    private ViewGroup mParentView;   //要添加到的父容器中
    /**
     * Data
     */
    private AdValue mXAdInstance;
    private AdSDKSlotListener mSlotListener;
    private boolean canPause = false; //是否可自动暂停标志位
    private int lastArea = 0; //防止将要滑入滑出时播放器的状态改变
    private static final String TAG = "VideoAdSlot";

    public VideoAdSlot(AdValue adInstance, AdSDKSlotListener slotListener, CostumeVideoView.ADFrameImageLoadListener frameLoadListener) {
        mXAdInstance = adInstance;
        mSlotListener = slotListener;
        mParentView = slotListener.getAdParent();
        mContext = mParentView.getContext();
        initVideoView(frameLoadListener);
    }

    private void initVideoView(CostumeVideoView.ADFrameImageLoadListener frameImageLoadListener) {
        mVideoView = new CostumeVideoView(mContext, mParentView);
        if (mXAdInstance != null) {
            mVideoView.setDataSource(mXAdInstance.resource);
            mVideoView.setFrameURI(mXAdInstance.thumb);
            mVideoView.setFrameLoadListener(frameImageLoadListener);
            mVideoView.setVideoPlayListener(this);
        }
        RelativeLayout paddingView = new RelativeLayout(mContext);
        paddingView.setBackgroundColor(mContext.getResources().getColor(android.R.color.black));
        paddingView.setLayoutParams(mVideoView.getLayoutParams());
        mParentView.addView(paddingView);
        mParentView.addView(mVideoView);
    }

    private boolean isPlaying() {
        if (mVideoView != null) {
            return mVideoView.isPlaying();
        }
        return false;
    }

    private boolean isRealPause() {
        if (mVideoView != null) {
            return mVideoView.isRealPause();
        }
        return false;
    }

    private boolean isComplete() {
        if (mVideoView != null) {
            return mVideoView.isComplete();
        }
        return false;
    }

    //pause the  video
    private void pauseVideo(boolean isAuto) {
        if (mVideoView != null) {
            if (isAuto) {
                //发自动暂停监测
                if (!isRealPause() && isPlaying()) {
                    try {
                        ReportManager.pauseVideoReport(mXAdInstance.event.pause.content, getPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            mVideoView.seekAndPause(0);
        }
    }

    //resume the video
    private void resumeVideo() {
        if (mVideoView != null) {
            mVideoView.resume();
            if (isPlaying()) {
//                sendSUSReport(true); //发自动播放监测
            }
        }
    }

    /**
     * 判断是否自动播放
     */
    public void updateAdInScrollView() {
        int currentArea = Utils.getVisiblePercent(mParentView);
        //小于0表示未出现在屏幕上，不做任何处理
        if (currentArea <= 0) {
            return;
        }
        //刚要滑入和滑出时，异常状态的处理
        if (Math.abs(currentArea - lastArea) >= 100) {
            return;
        }
        if (currentArea < SDKConstant.VIDEO_SCREEN_PERCENT) {
            //进入自动暂停状态
            if (canPause) {
                pauseVideo(true);
                canPause = false;
            }
            lastArea = 0;
            mVideoView.setIsComplete(false); // 滑动出50%后标记为从头开始播
            mVideoView.setIsRealPause(false); //以前叫setPauseButtonClick()
            return;
        }

        if (isRealPause() || isComplete()) {
            //进入手动暂停或者播放结束，播放结束和不满足自动播放条件都作为手动暂停
            pauseVideo(false);
            canPause = false;
            return;
        }

        //满足自动播放条件或者用户主动点击播放，开始播放
        if (Utils.canAutoPlay(mContext, AdParameters.getCurrentSetting())
                || isPlaying()) {
            lastArea = currentArea;
            resumeVideo();
            canPause = true;
            mVideoView.setIsRealPause(false);
        } else {
            pauseVideo(false);
            mVideoView.setIsRealPause(true); //不能自动播放则设置为手动暂停效果
        }
    }

    public void destroy() {
        mVideoView.destroy();
        mVideoView = null;
        mContext = null;
        mXAdInstance = null;
    }

    /**
     * 点击播放器的全屏按钮后会回调这个方法
     */
    @Override
    public void onClickFullScreenBtn() {
        //获取videoview在当前界面的属性
        Bundle bundle = Utils.getViewProperty(mParentView);
        //将播放器从view tree中移除
        mParentView.removeView(mVideoView);
        //创建全屏播放dialog
        VideoFullDialog dialog = new VideoFullDialog(mContext, mVideoView, mXAdInstance, mVideoView.getCurrentPosition());
        dialog.setListener(new VideoFullDialog.FullToSmallListener() {
            //dialog点击返回键之后的事件回调
            @Override
            public void getCurrentPlayPosition(int position) {
                backToSmallMode(position);
            }

            //dialog播放完成后的事件回调
            @Override
            public void playComplete() {
                bigPlayComplete();
            }
        });
        dialog.setViewBundle(bundle); //为Dialog设置播放器数据Bundle对象
        dialog.setSlotListener(mSlotListener);
        dialog.show();
        LogUtils.d(TAG,"onClickFullScreenBtn->show()");
    }

    private void backToSmallMode(int position) {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.setTranslationY(0); //防止动画导致偏离父容器
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true); //小屏静音
        mVideoView.setVideoPlayListener(this); //重新设监听
        mVideoView.seekAndResume(position);
        canPause = true; // 标为可自动暂停
    }

    private void bigPlayComplete() {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.setTranslationY(0); //防止动画导致偏离父容器
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setVideoPlayListener(this);
        mVideoView.seekAndPause(0);
        canPause = false;
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

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (mSlotListener != null) {
            mSlotListener.onAdVideoLoadSuccess();
        }

    }

    @Override
    public void onBufferUpdate(int time) {
        try {
//            ReportManager.suReport(mXAdInstance.middleMonitor, time / SDKConstant.MILLION_UNIT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAdVideoLoadFailed() {
        if (mSlotListener != null) {
            mSlotListener.onAdVideoLoadFailed();
        }
        //加载失败全部回到初始状态
        canPause = false;
    }

    @Override
    public void onAdVideoLoadComplete() {
        if (mSlotListener != null) {
            mSlotListener.onAdVideoLoadComplete();
        }

        //播放完成发送sue监测
        try {
            ReportManager.sueReport(mXAdInstance.endMonitor, false, getDuration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mVideoView.setIsRealPause(true);
    }

    //获取总时长
    private int getDuration() {
        return mVideoView.getDuration() / SDKConstant.MILLION_UNIT;
    }

    //获取位置
    private int getPosition() {
        return mVideoView.getCurrentPosition() / SDKConstant.MILLION_UNIT;
    }

    /**
     * 发送视频开始播放监测
     *
     * @param isAuto
     */
    private void sendSUSReport(boolean isAuto) {
        try {
//            ReportManager.susReport(mXAdInstance.startMonitor, isAuto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //传递消息到appcontext层,第二层接口回调
    public interface AdSDKSlotListener {

        ViewGroup getAdParent();

        void onAdVideoLoadSuccess();

        void onAdVideoLoadFailed();

        void onAdVideoLoadComplete();

        void onClickVideo(String url);
    }
}
