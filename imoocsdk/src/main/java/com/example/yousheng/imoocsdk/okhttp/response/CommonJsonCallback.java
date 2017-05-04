package com.example.yousheng.imoocsdk.okhttp.response;

import android.os.Handler;
import android.os.Looper;

import com.example.yousheng.imoocsdk.okhttp.exception.OkHttpException;
import com.example.yousheng.imoocsdk.okhttp.listener.DisposeDataHandle;
import com.example.yousheng.imoocsdk.okhttp.listener.DisposeDataListener;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by yousheng on 17/5/4.
 * @function 专门处理JSON的回调,实现了callback接口，相当于封装了一次，
 *           这样在实际调用时候只需传入CommonJsonCallback这个callback,
 *           并且需要实现DisposeDataHandle里的DisposeDataListener接口，
 *           这个接口里的方法实现与okhttp的api无关，所以以后okhttp的api改了也没事
 */

public class CommonJsonCallback implements Callback {

    /**
     * the logic layer exception, may alter in different app
     * @function 与服务器返回的字段的一个对应关系
     */
    protected final String RESULT_CODE = "ecode"; // 有返回则对于http请求来说是成功的，但还有可能是业务逻辑上的错误
    protected final int RESULT_CODE_VALUE = 0;
    protected final String ERROR_MSG = "emsg";
    protected final String EMPTY_MSG = "";

    /**
     * the java layer exception, do not same to the logic error
     * @function 自定义异常类型
     */
    protected final int NETWORK_ERROR = -1; // the network relative error
    protected final int JSON_ERROR = -2; // the JSON relative error
    protected final int OTHER_ERROR = -3; // the unknow error

    /**
     * 将其它线程的数据转发到UI线程
     */
    private Handler mDeliveryHandler;
    private DisposeDataListener mListener;
    private Class<?> mClass;

    public CommonJsonCallback(DisposeDataHandle handle) {
        this.mListener = handle.mListener;
        this.mClass = handle.mClass;
        //初始化handler
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }


    @Override
    public void onFailure(Call call, final IOException ioexception) {
        /**
         * 此时还在非UI线程，因此要转发消息到主线程
         */
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                //应用层会拿到异常，根据错误code的种类去做处理
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, ioexception));
            }
        });
    }

    /**
     * 真正的响应方法
     * @param call
     * @param response
     * @throws IOException
     */
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String result = response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    /**
     * 处理服务器返回的响应数据
     * @param responseObj
     */
    private void handleResponse(Object responseObj) {
        //为了保证代码的健壮性,response为空则返回一个网络异常
        if (responseObj == null || responseObj.toString().trim().equals("")) {
            mListener.onFailure(new OkHttpException(NETWORK_ERROR, EMPTY_MSG));
            return;
        }

        //开始数据解析
        try {
            /**
             * 与服务器的协议确定后看这里如何修改
             */
            String result = responseObj.toString();
            //class为空，表示无需解析成实体，直接返回给应用层
            if (mClass == null) {
                mListener.onSuccess(result);
            } else {
                //需要把json转换成实体
                Object obj = new Gson().fromJson(result,mClass);
                //表明正确转换成了实体对象
                if (obj != null) {
                    mListener.onSuccess(obj);
                } else {
                    //返回的是不合法的json
                    mListener.onFailure(new OkHttpException(JSON_ERROR, EMPTY_MSG));
                }
            }
        } catch (Exception e) {
            //捕获到任何异常时
            mListener.onFailure(new OkHttpException(OTHER_ERROR, e.getMessage()));
            e.printStackTrace();
        }
    }
}
