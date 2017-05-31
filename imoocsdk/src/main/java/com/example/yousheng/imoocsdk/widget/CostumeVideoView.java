package com.example.yousheng.imoocsdk.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.yousheng.imoocsdk.R;
import com.example.yousheng.imoocsdk.constant.LogUtils;
import com.example.yousheng.imoocsdk.constant.SDKConstant;
import com.example.yousheng.imoocsdk.util.Utils;

/**
 * Created by yousheng on 17/5/9.
 *
 * @function 负责广告播放，暂停，事件触发的自定义布局
 */

public class CostumeVideoView extends RelativeLayout implements View.OnClickListener
        //surfaceView的三个生命周期回调接口
        , TextureView.SurfaceTextureListener
        //MediaPlayer的4个状态接口
        , MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener
        , MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {


    /**
     * Constant
     */
    private static final String TAG = "MraidVideoView";
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INVAL = 1000;

    //播放器生命周期状态码
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;     //空闲状态
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSING = 2;

    //自动重新初始化次数，防止初始化一次失败
    private static final int LOAD_TOTAL_COUNT = 3;

    /**
     * UI
     */
    private ViewGroup mParentContainer;
    //就是此自定义布局
    private RelativeLayout mPlayerView;
    private TextureView mVideoView;
    private Button mMiniPlayBtn;
    private ImageView mFullBtn;
    private ImageView mLoadingBar;
    private ImageView mFrameView;
    private AudioManager audioManager;  //音量控制器
    private Surface videoSurface;   //真正显示帧数据的类

    /**
     * Data
     */
    private String mUrl;  //要加载的数据地址
    private String mFrameURI;
    private boolean isMute;  //是否静音
    private int mScreenWidth, mDestationHeight;  //默认宽高，宽是屏幕，高是按照16：9计算而来

    /**
     * Status状态保护
     */
    private boolean canPlay = true;
    private boolean mIsRealPause;  //因为还有划出屏幕的假暂停，划进屏幕要继续播放，而真暂停就是暂停了
    private boolean mIsComplete;
    private int mCurrentCount;
    private int playerState = STATE_IDLE;  //默认空闲状态

    private MediaPlayer mediaPlayer;    //核心的播放类
    private ADVideoPlayerListener listener;  //监听回调
    private ScreenEventReceiver mScreenReceiver;  //监听是否锁屏广播，锁屏了要暂停播放，解锁后继续播放

    //复用主线程的Handler，每隔一秒发送播放的进度，每隔一段时间就告诉服务器,Timer会导致内存泄漏所以不用
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TIME_MSG) {
                if (isPlaying()) {
                    //还可以在这里更新progressbar
                    //LogUtils.i(TAG, "TIME_MSG");
                    listener.onBufferUpdate(getCurrentPosition());
                    //每隔1s循环发送延迟消息
                    sendEmptyMessageDelayed(TIME_MSG, TIME_INVAL);
                }
            }
        }
    };

    private ADFrameImageLoadListener mFrameLoadListener;

    public CostumeVideoView(Context context, ViewGroup parentContainer) {
        super(context);
        mParentContainer = parentContainer;
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        initData();
        initView();
        registerBroadcastReceiver();
        LogUtils.d(TAG,"1.构造器初始化完毕");
    }

    private void initData() {
        //初始化播放器宽度
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mDestationHeight = (int) (mScreenWidth * SDKConstant.VIDEO_HEIGHT_PERCENT);
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        //初始化此自定义布局
        mPlayerView = (RelativeLayout) inflater.inflate(R.layout.xadsdk_video_player, this);
        mVideoView = (TextureView) mPlayerView.findViewById(R.id.xadsdk_player_video_textureView);
        mVideoView.setOnClickListener(this);
        mVideoView.setKeepScreenOn(true);
        mVideoView.setSurfaceTextureListener(this);
        initSmallLayoutMode(); //init the small mode
    }

    private void initSmallLayoutMode() {
        LayoutParams params = new LayoutParams(mScreenWidth, mDestationHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayerView.setLayoutParams(params);

        //主界面时候的播放按钮
        mMiniPlayBtn = (Button) mPlayerView.findViewById(R.id.xadsdk_small_play_btn);
        //全屏按钮
        mFullBtn = (ImageView) mPlayerView.findViewById(R.id.xadsdk_to_full_view);
        //主界面时候的三个点，读取帧动画
        mLoadingBar = (ImageView) mPlayerView.findViewById(R.id.loading_bar);
        //一条鱼，主界面没有滑到视频时显示的图片
        mFrameView = (ImageView) mPlayerView.findViewById(R.id.framing_view);
        mMiniPlayBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);
    }


    /**
     * 每次view可见性改变时调用，visibility返回新的view可见性
     *
     * @param changedView 发生可见性改变的view
     * @param visibility  Gone或者Visible
     */
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        LogUtils.e(TAG, "onVisibilityChanged: " + visibility);
        super.onVisibilityChanged(changedView, visibility);

        //播放器正在播放，而点击打开了另一个活动，必须暂停
        if (visibility == VISIBLE && playerState == STATE_PAUSING) {
            if (mIsRealPause || mIsComplete) {
                pause();
            } else {
                decideCanPlay();
            }
        } else {
            pause();
        }
    }

    //设置监听的回调接口
    public void setVideoPlayListener(ADVideoPlayerListener listener1) {
        listener = listener1;
    }

    public void setFrameLoadListener(ADFrameImageLoadListener frameLoadListener) {
        this.mFrameLoadListener = frameLoadListener;
    }

    //点击后消耗掉事件，父容器就不会执行onTouchEvent，防止与父容器发生冲突
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


    //-------------各种接口回调-------------
    //-------------各种接口回调-------------
    //-------------各种接口回调-------------


    /**
     * 播放器准备就绪后回调,就绪后两种情况，若是在屏幕里面积>50%就播放，否则暂停
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        LogUtils.i(TAG, "7.播放器onPrepared ");
        showPlayView();
        mediaPlayer = mp;
        if (mediaPlayer != null) {
            mediaPlayer.setOnBufferingUpdateListener(this);
            //重试次数清零
            mCurrentCount = 0;
            if (listener != null) {
                //接口通知外部，加载成功了
                listener.onAdVideoLoadSuccess();
            }
            //加载成功后不一定马上播放，只有显示了50%在当前屏幕里，才播放
            decideCanPlay();
        }
    }

    //屏幕里的显示面积>50%，就播放
    private void decideCanPlay() {
        LogUtils.d(TAG, "decideCanPlay");
        if (Utils.getVisiblePercent(mParentContainer) > SDKConstant.VIDEO_SCREEN_PERCENT)
        //来回切换页面时，只有 >50,且满足自动播放条件才自动播放
        {
            setCurrentPlayState(STATE_PAUSING);
            LogUtils.d(TAG, "8.decideCanPlay->resume()");
            resume();
        } else {
            LogUtils.d(TAG, "decideCanPlay->pause()");
            pause();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    /**
     * @param mp
     * @function 在播放器完成后回调
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (listener != null) {
            listener.onAdVideoLoadComplete();
        }
        setIsComplete(true);
        setIsRealPause(true);
        playBack();//进度回到0
    }

    /**
     * 播放器异常的方法回调
     *
     * @param mp
     * @param what
     * @param extra
     * @return true表示我们会自己处理error，FALSE表示让系统处理，默认是false
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        this.playerState = STATE_ERROR;
        //次数没到3就去stop里重新加载
        if (mCurrentCount >= LOAD_TOTAL_COUNT) {
            if (listener != null) {
                listener.onAdVideoLoadFailed();
            }
            showPauseView(false);
        }
        stop();//不管重试次数到顶了没，都清空重置播放器，有重试次数就继续重试
        return true;
    }

    /**
     * 最重要的方法，标明SurfaceTexture处于就绪状态，只有available以后才能去加载帧数据，不然会黑屏
     *
     * @param surface
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        LogUtils.i(TAG, "2.onSurfaceTextureAvailable");
        videoSurface = new Surface(surface);
        LogUtils.d(TAG,"3.SurfaceTexture创建完毕");
        checkMediaPlayer();
        LogUtils.d(TAG,"5.mediaPlayer加载surface");
        mediaPlayer.setSurface(videoSurface);
        load();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        LogUtils.i(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    //---------下面是几个功能性方法---------------
    //---------下面是几个功能性方法---------------
    //---------下面是几个功能性方法---------------

    //在slot逻辑层设定
    public void setDataSource(String url) {
        this.mUrl = url;
    }

    public void setFrameURI(String url) {
        mFrameURI = url;
    }

    /**
     * 点击onclick()与textureView的available回调中调用，构造方法中不一定textureView初始化好了
     * 加载我们的视频url，有异常就stop()循环重试3次，没异常就告诉播放器去准备，播放器回调onPrepare()
     */
    public void load() {
        LogUtils.e(TAG, "6.load()");
        //不空闲则不load
        if (this.playerState != STATE_IDLE) {
            return;
        }
        LogUtils.d(TAG, "do play url = " + this.mUrl);
        //显示读取的三个小圆点动画
        showLoadingView();
        try {

            setCurrentPlayState(STATE_IDLE);
            checkMediaPlayer(); //检查播放器是否新建（texture的available回调中已调用过）
//            LogUtils.d(TAG, "checked");
            mute(true);
            //设置播放源
            mediaPlayer.setDataSource(this.mUrl);
            //播放器开始异步加载,回调onprepared
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            LogUtils.e(TAG, "load() fail: " + e.getMessage());
            stop(); //error以后重新调用stop加载
        }
    }

    //暂停视频
    public void pause() {
        if (this.playerState != STATE_PLAYING) {
            return;
        }
        LogUtils.d(TAG, "do pause");
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
            if (!this.canPlay) {
                this.mediaPlayer.seekTo(0);
            }
        }
        this.showPauseView(false);
        mHandler.removeCallbacksAndMessages(null);
    }

    //全屏不显示暂停状态,后续可以整合，不必单独出一个方法
    public void pauseForFullScreen() {
        if (playerState != STATE_PLAYING) {
            return;
        }
        LogUtils.d(TAG, "do full pause");
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
            if (!this.canPlay) {
                mediaPlayer.seekTo(0);
            }
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    //恢复播放,之后的情况是播放完成或者失败，分别进入回调方法编写
    public void resume() {
        if (playerState != STATE_PAUSING) {
            return;
        }
        LogUtils.d(TAG, "10.resume()");
        if (!isPlaying()) {
            //让状态值变为播放中的
            entryResumeState();
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.start();
            LogUtils.d(TAG,"11.resume()--->mediaPlayer.start();");
            mHandler.sendEmptyMessage(TIME_MSG);
            showPauseView(true);
        } else {
            showPauseView(false);
        }
    }

    /**
     * 进入播放状态时的状态更新
     */
    private void entryResumeState() {
        canPlay = true;
        setCurrentPlayState(STATE_PLAYING);
        setIsRealPause(false);
        setIsComplete(false);
    }

    //播放完成后回到初始状态，逻辑是播放完后让播放流回到0并且暂停，这样下次播放就不用重新耗费流量下载
    public void playBack() {
        setCurrentPlayState(STATE_PAUSING);
        mHandler.removeCallbacks(null);
        if (mediaPlayer != null) {
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
        }
        //显示暂停界面
        showPauseView(false);
    }

    //初始化失败会调用stop(),stop会重启播放器3此，若还是初始化失败，则显示暂停界面
    public void stop() {
        LogUtils.d(TAG, " do stop");
        if (this.mediaPlayer != null) {
            this.mediaPlayer.reset();
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        setCurrentPlayState(STATE_IDLE);
        if (mCurrentCount < LOAD_TOTAL_COUNT) { //满足重新加载的条件
            mCurrentCount += 1;
            load();
        } else {
            showPauseView(false); //显示暂停状态
        }
    }

    //销毁自定义view和里面一些消耗内存的东西如监听
    public void destroy() {
        LogUtils.d(TAG, " do destroy");
        if (this.mediaPlayer != null) {
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        setCurrentPlayState(STATE_IDLE);
        mCurrentCount = 0;
        setIsComplete(false);
        setIsRealPause(false);
        unRegisterBroadcastReceiver();
        mHandler.removeCallbacksAndMessages(null); //release all message and runnable
        showPauseView(false); //除了播放和loading外其余任何状态都显示pause
    }

    //跳到指定点播放，用于小屏转大屏时续播用
    public void seekAndResume(int position) {
        LogUtils.d(TAG,"seekAndResume---->");
        if (mediaPlayer != null) {
            showPauseView(true);
            entryResumeState();
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    LogUtils.d(TAG, "do seek and resume");
                    mediaPlayer.start();
//                    resume();
                    mHandler.sendEmptyMessage(TIME_MSG);
                }
            });
        }
    }

    //跳到指定点暂停
    public void seekAndPause(int position) {
        if (this.playerState != STATE_PLAYING) {
            return;
        }
        showPauseView(false);
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.seekTo(position);
            //因为seekTo是耗时操作，不知道什么时候跳转完毕，所以用OnSeekComplete接口
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    LogUtils.d(TAG, "do seek and pause");
                    mediaPlayer.pause();
                    mHandler.removeCallbacksAndMessages(null);
                }
            });
        }
    }

    public synchronized void checkMediaPlayer() {
        if (mediaPlayer == null) {
            LogUtils.d(TAG, "4.createMediaPlayer");
            mediaPlayer = createMediaPlayer(); //每次都创建一个新的mediaPlayer
        }
    }

    private MediaPlayer createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        LogUtils.d(TAG, "new MediaPlayer()");

        mediaPlayer.reset();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        if (videoSurface != null && videoSurface.isValid()) {
            mediaPlayer.setSurface(videoSurface);
        } else {
            stop();
        }

        return mediaPlayer;
    }

    public void isShowFullBtn(boolean isShow) {
        mFullBtn.setImageResource(isShow ? R.drawable.xadsdk_ad_mini : R.drawable.xadsdk_ad_mini_null);
        mFullBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void setIsComplete(boolean isComplete) {
        mIsComplete = isComplete;
    }

    //是否真的暂停（可能是移出屏幕外或者锁屏的假暂停）
    public void setIsRealPause(boolean isRealPause) {
        this.mIsRealPause = isRealPause;
    }

    /**
     * 显示或者不显示暂停界面
     *
     * @param show true表示不暂停，FALSE表示暂停
     */
    private void showPauseView(boolean show) {
        mFullBtn.setVisibility(show ? View.VISIBLE : View.GONE);
        mMiniPlayBtn.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        if (!show) {
            mFrameView.setVisibility(View.VISIBLE);
            loadFrameImage();
        } else {
            mFrameView.setVisibility(View.GONE);
        }
    }

    //显示三个小圆点的读取动画
    private void showLoadingView() {
        LogUtils.d(TAG, "showLoadingView");
        mFullBtn.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.VISIBLE);
        AnimationDrawable anim = (AnimationDrawable) mLoadingBar.getBackground();
        anim.start();
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
        loadFrameImage();
    }

    private void showPlayView() {
        LogUtils.d(TAG, "showPlayView");
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
    }


    /**
     * 异步加载定帧图
     */
    private void loadFrameImage() {

    }

    private void setCurrentPlayState(int state) {
        playerState = state;
    }

    /**
     * true is no voice
     *
     * @param mute
     */
    public void mute(boolean mute) {
        LogUtils.d(TAG, "mute");
        isMute = mute;
        if (mediaPlayer != null && this.audioManager != null) {
            float volume = isMute ? 0.0f : 1.0f;
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    public boolean isRealPause() {
        return mIsRealPause;
    }

    public boolean isComplete() {
        return mIsComplete;
    }

    public boolean isFrameHidden() {
        return mFrameView.getVisibility() == View.VISIBLE ? false : true;
    }


    /**
     * @return the current position in milliseconds
     */
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }


    @Override
    public void onClick(View v) {
        if (v == this.mMiniPlayBtn) {
            if (this.playerState == STATE_PAUSING) {
                if (Utils.getVisiblePercent(mParentContainer)
                        > SDKConstant.VIDEO_SCREEN_PERCENT) {
                    resume();
                    this.listener.onClickPlay();
                }
            } else {
                load();
            }
        } else if (v == this.mFullBtn) {
            this.listener.onClickFullScreenBtn();
        } else if (v == mVideoView) {
            this.listener.onClickVideo();
        }
    }

    private void registerBroadcastReceiver() {
        if (mScreenReceiver == null) {
            mScreenReceiver = new ScreenEventReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getContext().registerReceiver(mScreenReceiver, filter);
        }
    }

    private void unRegisterBroadcastReceiver() {
        if (mScreenReceiver != null) {
            getContext().unregisterReceiver(mScreenReceiver);
        }
    }

    /**
     * 监听锁屏事件的广播接收器
     */
    private class ScreenEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //主动锁屏时 pause, 主动解锁屏幕时，resume
            switch (intent.getAction()) {
                case Intent.ACTION_USER_PRESENT:
                    if (playerState == STATE_PAUSING) {
                        if (mIsRealPause) {
                            //手动点的暂停，回来后还暂停
                            pause();
                        } else {
                            decideCanPlay();
                        }
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    if (playerState == STATE_PLAYING) {
                        pause();
                    }
                    break;
            }
        }
    }

    /**
     * @function 供slot层来实现具体点击逻辑, 具体逻辑还会变，
     * 如果对UI的点击没有具体监测的话可以不回调
     */
    public interface ADVideoPlayerListener {
        void onBufferUpdate(int time);

        void onClickFullScreenBtn();

        void onClickVideo();

        void onClickBackBtn();

        void onClickPlay();

        void onAdVideoLoadSuccess();

        void onAdVideoLoadFailed();

        void onAdVideoLoadComplete();
    }

    public interface ADFrameImageLoadListener {

        void onStartFrameLoad(String url, ImageLoaderListener listener);
    }


    public interface ImageLoaderListener {
        /**
         * 如果图片下载不成功，传null
         *
         * @param loadedImage
         */
        void onLoadingComplete(Bitmap loadedImage);
    }

}
