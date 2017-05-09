package widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.yousheng.imoocsdk.R;
import com.example.yousheng.imoocsdk.constant.SDKConstant;

/**
 * Created by yousheng on 17/5/9.
 * @function 负责广告播放，暂停，事件触发的自定义布局
 */

public class CostumeVideoView extends RelativeLayout implements View.OnClickListener
        //surfaceView的三个生命周期回调接口
        ,TextureView.SurfaceTextureListener
        //MediaPlayer的4个状态接口
        ,MediaPlayer.OnPreparedListener,MediaPlayer.OnBufferingUpdateListener
        ,MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener{


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
    private boolean mIsRealPause;
    private boolean mIsComplete;
    private int mCurrentCount;
    private int playerState = STATE_IDLE;  //默认空闲状态

    private MediaPlayer mediaPlayer;    //核心的播放类
    private ADVideoPlayerListener listener;  //监听回调
    private ScreenEventReceiver mScreenReceiver;  //监听是否锁屏广播，锁屏了要暂停播放，解锁后继续播放

    //复用主线程的Handler，每隔一秒发送播放的进度，每隔一段时间就告诉服务器,Timer会导致内存泄漏所以不用
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == TIME_MSG){
                if(isPlaying()){
                    //还可以在这里更新progressbar
                    //LogUtils.i(TAG, "TIME_MSG");
                    listener.onBufferUpdate(getCurrentPosition());
                    //每隔1s循环发送延迟消息
                    sendEmptyMessageDelayed(TIME_MSG,TIME_INVAL);
                }
            }
        }
    };

    public CostumeVideoView(Context context,ViewGroup parentContainer) {
        super(context);
        mParentContainer = parentContainer;
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        initData();
        initView();
        registeBroadcast();

    }

    private void initData() {
        //初始化播放器宽度
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mDestationHeight = (int)(mScreenWidth * SDKConstant.VIDEO_HEIGHT_PERCENT);
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        //初始化此自定义布局
        mPlayerView = (RelativeLayout) inflater.inflate(R.layout.xadsdk_video_player,this);
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

    private void registeBroadcast() {

    }

    private boolean isPlaying() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            return true;
        }
        return false;
    }

    /**
     * @return the current position in milliseconds
     */
    private int getCurrentPosition() {
        if(mediaPlayer != null){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onClick(View v) {

    }


    public interface ADVideoPlayerListener{
        void onBufferUpdate(int time);
    }

    /**
     *  @function 监听锁屏事件的广播接收器
     */
    private class ScreenEventReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
