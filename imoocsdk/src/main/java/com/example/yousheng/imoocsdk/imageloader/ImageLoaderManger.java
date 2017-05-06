package com.example.yousheng.imoocsdk.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.example.yousheng.imoocsdk.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by yousheng on 17/5/6.
 *
 * @function 初始化ImageLoader, 并用来加载网络图片
 *           1、封装了默认的配置
 *           2、提供单例
 *           3、创建了自己的对外接口,万一下次想换用fresco等等，只要修改这个类，应用层不用修改
 */

public class ImageLoaderManger {

    //养成定义静态int的好习惯，不要在代码中直接用模式数
    private static final int THREAD_COUNT = 4; //标明我们的UIL最多的线程数
    private static final int PRIORITY = 2;  //标明图片加载降低的优先级
    private static final int MEMORY_CACHE_SIZE = 2 * 1024 * 1024;
    private static final int DISK_CACHE_SIZE = 50 * 1024;  //标明UIL最多可以缓存 50MB 图片
    private static final int CONNECTION_TIME_OUT = 5 * 1000;  //标明连接超时时间
    private static final int READ_TIME_OUT = 30 * 1000;  //读取的超时时间

    private static ImageLoaderManger mInstance = null;
    private static ImageLoader mLoader = null;

    //单例模式
    public static ImageLoaderManger getInstance(Context context) {
        if (mInstance == null) {
            //防止多线程中创建多个实例
            synchronized (ImageLoaderManger.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoaderManger(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * @param context
     * @function 私有构造方法完成初始化工作
     */
    private ImageLoaderManger(Context context) {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(context)
                .threadPoolSize(THREAD_COUNT) //配置图片下载线程的最大数量
                .threadPriority(Thread.NORM_PRIORITY - PRIORITY) //降低图片加载的优先级，因为每个系统的NORMAL优先级不同，所以用减法
                .denyCacheImageMultipleSizesInMemory() //防止缓存多套图片尺寸到内存中
                .memoryCache(new WeakMemoryCache()) //使用弱引用内存缓存，系统会在内存不足时回收图片，默认为一个当前应用可用内存的1/8大小的LruMemoryCache
                .diskCacheSize(DISK_CACHE_SIZE) //分配硬盘缓存大小
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO) //设置图片加载和显示队列处理的类型 默认为QueueProcessingType.FIFO
                .defaultDisplayImageOptions(getDisplayOptions())  //更改默认图片加载显示配置，实际调用时不指定则会用此
                .imageDownloader(new BaseImageDownloader(context, CONNECTION_TIME_OUT, READ_TIME_OUT))//设置图片下载器
                .writeDebugLogs() //debug时会输出日志
                .build();

        //用我们的配置去初始化imageLoader实例
        ImageLoader.getInstance().init(configuration);
        //获取实例
        mLoader = ImageLoader.getInstance();
    }

    /**
     * @function 默认的图片显示Options,可设置图片的缓存策略，编解码方式等，非常重要
     * @return
     */
    private DisplayImageOptions getDisplayOptions() {
        DisplayImageOptions options = new DisplayImageOptions
                .Builder()
                .showImageForEmptyUri(R.mipmap.ic_error) //地址为空的时候显示
                .showImageOnFail(R.mipmap.ic_error)  //下载失败的时候显示
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中, 重要，否则图片不会缓存到内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中, 重要，否则图片不会缓存到硬盘中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                .decodingOptions(new BitmapFactory.Options())//设置图片的解码配置
                .build();

        return options;
    }

    /**
     * @function 我们自己的加载图片api
     * @param imageView
     * @param path
     * @param listener
     * @param options
     */
    public void displayImage(ImageView imageView, String path,
                             ImageLoadingListener listener, DisplayImageOptions options) {
        if (mLoader != null) {
            mLoader.displayImage(path, imageView, options, listener);
        }
    }

    //load the image,使用我们上面默认配置的DisplayImageOptions
    public void displayImage(ImageView imageView, String path, ImageLoadingListener listener) {
        if (mLoader != null) {
            mLoader.displayImage(path, imageView, listener);
        }
    }

    public void displayImage(ImageView imageView, String path) {
        displayImage(imageView, path, null);
    }

}
