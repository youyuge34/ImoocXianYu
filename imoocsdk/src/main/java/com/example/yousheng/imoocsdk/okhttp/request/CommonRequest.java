package com.example.yousheng.imoocsdk.okhttp.request;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * Created by yousheng on 17/5/4.
 *
 * @function 接受url和请求参数params，为我们生成request对象
 */

public class CommonRequest {

    /**
     * 因为是工具类，所以用静态方法
     *
     * @param url
     * @param params
     * @return post请求返回一个创建好的request对象
     */
    public static Request createPostRequest(String url, RequestParams params) {

        //三部曲
        //1、只是一个请求构建类,okhttp用的是构建者模式
        FormBody.Builder mFormBodyBuilder = new FormBody.Builder();

        //将post请求参数遍历添加进入我们的请求构建类中
        for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
            mFormBodyBuilder.add(entry.getKey(), entry.getValue());
        }

        //2、通过请求构建类的build方法，获取到真正的请求体对象
        FormBody mFormBody = mFormBodyBuilder.build();

        //3、通过formbody请求体 创建请求request
        return new Request.Builder().url(url).post(mFormBody).build();
    }

    /**
     * @param url
     * @param params
     * @return get请求返回一个创建好的request对象
     */
    public static Request createGetRequest(String url, RequestParams params) {

        //因为get方式的请求是在url后添加？，再添加参数，用&分隔
        StringBuilder urlBuilder = new StringBuilder(url).append("?");

        if (params != null) {
            //url字符串的拼接
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=")
                        .append(entry.getValue()).append("&");
            }
        }

        //substring是为了去除最后多余的一个&
        return new Request.Builder().url(urlBuilder.substring(0, urlBuilder.length() - 1))
                .get().build();
    }
}
