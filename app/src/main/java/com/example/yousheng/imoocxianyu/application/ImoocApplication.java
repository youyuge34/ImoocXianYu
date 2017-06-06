package com.example.yousheng.imoocxianyu.application;

import android.app.Application;

import com.example.yousheng.imoocxianyu.share.ShareManager;

/**
 * Created by yousheng on 17/5/4.
 * @function 1.整个程序的入口
 *           2.初始化工作,如第三方组件
 *           3.提供上下文
 */

public class ImoocApplication extends Application {

    private static ImoocApplication mApplication=null;

    /**
     *  @description 因为onCreate()只会执行一次，所以用单例模式
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication=this;
        ShareManager.initSDK(this);
    }

    public static ImoocApplication getInstance(){
        return mApplication;
    }
}
