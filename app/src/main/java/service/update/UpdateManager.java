package service.update;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**********************************************************
 * @文件名称：UpdateManager.java
 * @文件作者：renzhiqiang
 * @创建时间：2015年8月26日 下午10:58:20
 * @文件描述：下载调度管理器,线程池调用我们的UpdateDownloadRequest
 * @修改历史：2015年8月26日创建初始版本
 **********************************************************/
public class UpdateManager {
    private static UpdateManager manager;
    private ThreadPoolExecutor threadPool;
    private UpdateDownloadRequest downloadRequest;

    private UpdateManager() {
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    static {
        manager = new UpdateManager();
    }

    //静态单例模式构造
    public static UpdateManager getInstance() {
        return manager;
    }

    public void startDownload(String downloadUrl, String localFilePath,
                              UpdateDownloadListener downloadListener) {
        if (downloadRequest != null && downloadRequest.isDownloading()) {
            return;
        }
        //检查文件
        checkLocalFilePath(localFilePath);

        //创建request
        downloadRequest = new UpdateDownloadRequest(downloadUrl, localFilePath,
                downloadListener);
        Future<?> request = threadPool.submit(downloadRequest);
        new WeakReference<Future<?>>(request);
    }

    private void checkLocalFilePath(String localFilePath) {
        //获取文件夹位置
        File path = new File(localFilePath.substring(0,
                localFilePath.lastIndexOf("/") + 1));
        File file = new File(localFilePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}