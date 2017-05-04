package com.example.yousheng.imoocsdk.okhttp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yousheng on 17/5/4.
 * @function 仅仅为了演示okhttp的使用,实际应用中必须进行封装
 */

public class BaseOkhttpTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void sendRequest(){

        //创建client对象
        OkHttpClient client=new OkHttpClient();

        //构建者模式创造一个请求Requset
        final Request request=new Request.Builder()
                .url("http://www.baidu.com")
                .build();

        //发送我们的请求,获得异步对象call
        Call call=client.newCall(request);

        //请求加入调度,注意回调方法还在异步线程
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
}
