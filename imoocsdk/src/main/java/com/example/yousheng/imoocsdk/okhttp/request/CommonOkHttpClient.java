package com.example.yousheng.imoocsdk.okhttp.request;

import com.example.yousheng.imoocsdk.okhttp.https.HttpsUtils;
import com.example.yousheng.imoocsdk.okhttp.response.CommonJsonCallback;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by yousheng on 17/5/4.
 *
 * @function 1、请求request的发送
 *           2、请求参数的配置
 *           3、https的支持
 */

public class CommonOkHttpClient {
    private static final int TIME_OUT = 10;
    private static OkHttpClient mOkHttpClient;

    //为client配置各种参数
    static {

        //创建client对象的构建者builder
        OkHttpClient.Builder builder=new OkHttpClient.Builder();

        //为构建者填充超时时间
        builder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);

        //允许重定向
        builder.followRedirects(true);

        //添加https支持
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //无论是CA机构还是自定义的证书都可以通过
                return true;
            }
        });

        /**
         * Sets the socket factory and trust manager used to secure HTTPS connections. If unset, the
         * system defaults will be used.
         * 若是未设置，则自定义证书无法通过，如12306网站
         */
        builder.sslSocketFactory(HttpsUtils.initSSLSocketFactory(),HttpsUtils.initTrustManager());

        //生成真正的client对象
        mOkHttpClient = builder.build();
    }

    /**
     * @function 发送具体的http/https请求
     * @param request
     * @param commonJsonCallback
     * @return Call
     */
    public static Call sendRequest(Request request, CommonJsonCallback commonJsonCallback){

        Call call=mOkHttpClient.newCall(request);
        call.enqueue(commonJsonCallback);

        return call;
    }
}
