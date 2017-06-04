package com.example.yousheng.imoocxianyu.activity.base;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.yousheng.imoocxianyu.util.StatusBarUtil;

/**
 * Created by yousheng on 17/5/4.
 * @function 为所有activity提供公共的方法和事件
 */

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * @function 输出logcat日志所需的TAG，这样子类就能直接继承使用了
     */
    public String TAG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG=getComponentName().getShortClassName();
    }



    /**
     * 改变状态栏颜色
     *
     * @param color
     */
    public void changeStatusBarColor(@ColorRes int color) {
        StatusBarUtil.setStatusBarColor(this, color);
    }

}
