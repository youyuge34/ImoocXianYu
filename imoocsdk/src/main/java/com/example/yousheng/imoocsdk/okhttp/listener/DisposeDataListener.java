package com.example.yousheng.imoocsdk.okhttp.listener;

/**
 * @function 业务逻辑层真正处理的地方，包括java层异常和业务层异常
 *           这个接口里的方法会在实际工作中的commonjsoncall里去实现，
 *           与okhttp的api无关，所以以后okhttp的api改了也没事
 * Created by yousheng on 17/5/4.
 */

public interface DisposeDataListener {
    /**
     * 请求成功回调事件处理
     */
    public void onSuccess(Object responseObj);

    /**
     * 请求失败回调事件处理
     * @param reasonObj 实际会接受一个OkHttpException
     */
    public void onFailure(Object reasonObj);
}
