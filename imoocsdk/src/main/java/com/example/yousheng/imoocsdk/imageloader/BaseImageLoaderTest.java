package com.example.yousheng.imoocsdk.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by yousheng on 17/5/6.
 *
 * @function 演示universalimageloader的api接口使用
 */

public class BaseImageLoaderTest {

    //测试原生的api
    private void testApi() {


        /**
         * 参数配置
         */
        Context context = null;
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context).build();


        /**
         *  先获取imageLoader的一个实例
         */
        ImageLoader mImageLoader = ImageLoader.getInstance();


        /**
         * 显示图片的参数配置
         */
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().build();

        /**
         *  使用displayImage方法去加载图片
         */
        ImageView imageView = null;
        mImageLoader.displayImage("url", imageView, displayImageOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    //测试我们自己封装的api
    void testOwnApi() {
        ImageView imageView = null;
        Context context = null;
        ImageLoaderManger.getInstance(context).displayImage(imageView,"url");
    }

}
