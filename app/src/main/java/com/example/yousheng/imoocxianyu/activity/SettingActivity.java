package com.example.yousheng.imoocxianyu.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.yousheng.imoocsdk.constant.SDKConstant.AutoPlaySetting;
import com.example.yousheng.imoocsdk.core.AdParameters;
import com.example.yousheng.imoocxianyu.R;
import com.example.yousheng.imoocxianyu.db.SPManager;

/**
 * Created by yousheng on 17/6/1.
 *
 * @function 设置网络受限情况
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * UI
     */
    private RelativeLayout mAlwayLayout;
    private RelativeLayout mWifiLayout;
    private RelativeLayout mNeverLayout;
    private CheckBox mWifiBox, mAlwayBox, mNeverBox;
    private ImageView mBackView;

    private static int ALWAYS = 0;
    private static int WIFI = 1;
    private static int NEVER = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_layout);

        initView();
    }

    private void initView() {
        mBackView = (ImageView) findViewById(R.id.back_view);
        mWifiLayout = (RelativeLayout) findViewById(R.id.wifi_layout);
        mWifiBox = (CheckBox) findViewById(R.id.wifi_check_box);
        mAlwayLayout = (RelativeLayout) findViewById(R.id.alway_layout);
        mAlwayBox = (CheckBox) findViewById(R.id.alway_check_box);
        mNeverLayout = (RelativeLayout) findViewById(R.id.close_layout);
        mNeverBox = (CheckBox) findViewById(R.id.close_check_box);

        mBackView.setOnClickListener(this);
        mWifiLayout.setOnClickListener(this);
        mAlwayLayout.setOnClickListener(this);
        mNeverLayout.setOnClickListener(this);

        //sp取出值
        int currentSetting = SPManager.getInstance().getInt(SPManager.VIDEO_PLAY_SETTING, ALWAYS);

        //在对应选项上打钩
        switch (currentSetting) {
            case 0:
                //总是可用
                mAlwayBox.setBackgroundResource(R.drawable.setting_selected);
                mWifiBox.setBackgroundResource(0);
                mNeverBox.setBackgroundResource(0);
                break;

            case 1:
                mAlwayBox.setBackgroundResource(0);
                mWifiBox.setBackgroundResource(R.drawable.setting_selected);
                mNeverBox.setBackgroundResource(0);
                break;

            case 2:
                mAlwayBox.setBackgroundResource(0);
                mWifiBox.setBackgroundResource(0);
                mNeverBox.setBackgroundResource(R.drawable.setting_selected);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alway_layout:
                SPManager.getInstance().putInt(SPManager.VIDEO_PLAY_SETTING, 0);
                //通知当前设置到视频播放sdk
                AdParameters.setCurrentSetting(AutoPlaySetting.AUTO_PLAY_3G_4G_WIFI);
                mAlwayBox.setBackgroundResource(R.drawable.setting_selected);
                mWifiBox.setBackgroundResource(0);
                mNeverBox.setBackgroundResource(0);
                break;
            case R.id.close_layout:
                SPManager.getInstance().putInt(SPManager.VIDEO_PLAY_SETTING, 2);
                AdParameters.setCurrentSetting(AutoPlaySetting.AUTO_PLAY_NEVER);
                mAlwayBox.setBackgroundResource(0);
                mWifiBox.setBackgroundResource(0);
                mNeverBox.setBackgroundResource(R.drawable.setting_selected);
                break;
            case R.id.wifi_layout:
                SPManager.getInstance().putInt(SPManager.VIDEO_PLAY_SETTING, 1);
                AdParameters.setCurrentSetting(AutoPlaySetting.AUTO_PLAY_ONLY_WIFI);
                mAlwayBox.setBackgroundResource(0);
                mWifiBox.setBackgroundResource(R.drawable.setting_selected);
                mNeverBox.setBackgroundResource(0);
                break;
            case R.id.back_view:
                finish();
                break;
        }
    }
}
