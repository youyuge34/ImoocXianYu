package com.example.yousheng.imoocxianyu.view.fragment.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yousheng.imoocsdk.constant.LogUtils;
import com.example.yousheng.imoocsdk.imageloader.ImageLoaderManger;
import com.example.yousheng.imoocsdk.okhttp.listener.DisposeDataListener;
import com.example.yousheng.imoocxianyu.R;
import com.example.yousheng.imoocxianyu.activity.LoginActivity;
import com.example.yousheng.imoocxianyu.activity.SettingActivity;
import com.example.yousheng.imoocxianyu.manager.UserManager;
import com.example.yousheng.imoocxianyu.module.update.UpdateModel;
import com.example.yousheng.imoocxianyu.network.http.RequestCenter;
import com.example.yousheng.imoocxianyu.service.update.UpdateService;
import com.example.yousheng.imoocxianyu.util.Util;
import com.example.yousheng.imoocxianyu.view.MyQrCodeDialog;
import com.example.yousheng.imoocxianyu.view.fragment.BaseFragment;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

/**
 * Created by yousheng on 17/5/4.
 */

public class MineFragment extends BaseFragment implements OnClickListener {

    /**
     * UI
     */
    private View mContentView;
    private RelativeLayout mLoginLayout;
    private CircleImageView mPhotoView;
    private CircleImageView mUserPhotoView;
    private TextView mLoginInfoView;
    private TextView mLoginView;
    private RelativeLayout mLoginedLayout;
    private TextView mUserNameView;
    private TextView mTickView;
    private TextView mVideoPlayerView;
    private TextView mShareView;
    private TextView mQrCodeView;
    private TextView mUpdateView;

    //登陆广播的广播接收器
    private LoginBroadcastReceiver loginBroadcastReceiver =
            new LoginBroadcastReceiver();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        registerLoginBroadcast();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_mine_layout, container, false);
        initView();
        return mContentView;
    }

    private void initView() {
        mLoginLayout = (RelativeLayout) mContentView.findViewById(R.id.login_layout);
        mLoginLayout.setOnClickListener(this);
        mLoginedLayout = (RelativeLayout) mContentView.findViewById(R.id.logined_layout);
        mLoginedLayout.setOnClickListener(this);

        //默认头像
        mPhotoView = (CircleImageView) mContentView.findViewById(R.id.photo_view);
        mPhotoView.setOnClickListener(this);
        //登陆后的头像
        mUserPhotoView = (CircleImageView) mContentView.findViewById(R.id.user_photo_view);
        mUserPhotoView.setOnClickListener(this);
        //马上登陆按钮
        mLoginView = (TextView) mContentView.findViewById(R.id.login_view);
        mLoginView.setOnClickListener(this);
        mVideoPlayerView = (TextView) mContentView.findViewById(R.id.video_setting_view);
        mVideoPlayerView.setOnClickListener(this);
        mShareView = (TextView) mContentView.findViewById(R.id.share_imooc_view);
        mShareView.setOnClickListener(this);
        mQrCodeView = (TextView) mContentView.findViewById(R.id.my_qrcode_view);
        mQrCodeView.setOnClickListener(this);
        mLoginInfoView = (TextView) mContentView.findViewById(R.id.login_info_view);
        mUserNameView = (TextView) mContentView.findViewById(R.id.username_view);
        mTickView = (TextView) mContentView.findViewById(R.id.tick_view);

        mUpdateView = (TextView) mContentView.findViewById(R.id.update_view);
        mUpdateView.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterLoginBroadcast();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_setting_view:
                mContext.startActivity(new Intent(mContext, SettingActivity.class));
                break;

            case R.id.update_view:
                checkVersion();
                break;

            case R.id.login_view:
                //未登陆，则跳轉到登陸页面
                if (!UserManager.getInstance().hasLogined()) {
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;

            case R.id.my_qrcode_view:
                if (!UserManager.getInstance().hasLogined()) {
                    Toast.makeText(mContext,"请先登陆!",Toast.LENGTH_SHORT).show();
                } else {
                    //已登陆根据用户ID生成二维码显示
                    MyQrCodeDialog dialog = new MyQrCodeDialog(mContext);
                    dialog.show();
                }
                break;
        }
    }

    //发送版本检查请求
    private void checkVersion() {
        RequestCenter.checkUpdate(new DisposeDataListener() {

            //若是指定了class，则object是对应的class，否则为string
            @Override
            public void onSuccess(Object responseObj) {
                final UpdateModel updateModel = (UpdateModel) responseObj;
                LogUtils.d("MineFragment", "version online is--->" + updateModel.data.currentVersion);
                LogUtils.d("MineFragment", "version local is--->" + Util.getVersionCode(mContext));
                if (Util.getVersionCode(mContext) < updateModel.data.currentVersion) {

                    //说明有新版本，需要弹出对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("当前版本号：" + Util.getVersionName(mContext) + "  您有新版本:")
                            .setMessage(updateModel.data.whatsNew)
                            .setPositiveButton("安装", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //点击安装后的回调事件,实际上就是启动更新服务
                                    mContext.startService(new Intent(mContext, UpdateService.class));
                                    Toast.makeText(mContext, "正在后台下载", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("下次再说", null)
                            .show();

                } else {
                    //没有新版本
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("已经是最新版本！")
                            .setMessage("当前版本： v" + Util.getVersionName(mContext))
                            .setPositiveButton("好的", null)
                            .show();

                }
            }

            @Override
            public void onFailure(Object reasonObj) {
                LogUtils.e("MineFragment", "更新请求失败");
                Toast.makeText(mContext, "请求失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerLoginBroadcast() {
        IntentFilter filter = new IntentFilter(LoginActivity.LOGIN_ACTION);
        LocalBroadcastManager.getInstance(mContext).
                registerReceiver(loginBroadcastReceiver, filter);
    }

    private void unRegisterLoginBroadcast() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(loginBroadcastReceiver);
    }

    /**
     * 接收登陆成功后发送来的消息，并更新UI
     */
    private class LoginBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UserManager.getInstance().hasLogined()) {
                Toast.makeText(mContext,"欢迎回来！ "+UserManager.getInstance().getUser().data.name,Toast.LENGTH_SHORT);
                //更新我们的fragment
                if (mLoginedLayout.getVisibility() == View.GONE) {
                    mLoginLayout.setVisibility(View.GONE);
                    mLoginedLayout.setVisibility(View.VISIBLE);
                    mUserNameView.setText(UserManager.getInstance().getUser().data.name);
                    mTickView.setText(UserManager.getInstance().getUser().data.tick);
                    Log.d(TAG, "onReceive: " + UserManager.getInstance().getUser().data.photoUrl);
                    ImageLoaderManger.getInstance(mContext).displayImage(mUserPhotoView, UserManager.getInstance().getUser().data.photoUrl);
                }
            }
        }
    }
}
