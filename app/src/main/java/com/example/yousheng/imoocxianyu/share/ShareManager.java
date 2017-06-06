package com.example.yousheng.imoocxianyu.share;

import android.content.Context;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * @author yousheng
 * @function 分享功能统一入口，负责发送数据到指定平台,可以优化为一个单例模式
 */

public class ShareManager {
    private static ShareManager mShareManager = null;
    /**
     * 要分享到的平台
     */
    private Platform mCurrentPlatform;

    /**
     * 线程安全的单例模式
     */
    public static ShareManager getInstance() {
        if (mShareManager == null) {
            synchronized (ShareManager.class) {
                if (mShareManager == null) {
                    mShareManager = new ShareManager();
                }
            }
        }
        return mShareManager;
    }

    private ShareManager() {
    }

    /**
     * 第一个执行的方法,最好在程序入口入执行
     *
     * @param context
     */
    public static void initSDK(Context context) {
        ShareSDK.initSDK(context);
    }

    /**
     * shareSdk分享快速测试
     */
    public void showShare(Context context) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle("漫尤");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl("http://youyuge.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("漫尤官方网站——鱿鱼罐头，闲来无事写点博文~");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://youyuge.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("评论：漫尤最棒啦");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("漫尤官方网站");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://youyuge.cn");

// 启动分享GUI
        oks.show(context);
    }

}
