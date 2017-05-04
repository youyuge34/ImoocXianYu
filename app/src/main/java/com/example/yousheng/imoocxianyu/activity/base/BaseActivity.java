package com.example.yousheng.imoocxianyu.activity.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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
}
