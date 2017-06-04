package com.example.yousheng.imoocxianyu.network.http;

import com.example.yousheng.imoocsdk.okhttp.listener.DisposeDataHandle;
import com.example.yousheng.imoocsdk.okhttp.listener.DisposeDataListener;
import com.example.yousheng.imoocsdk.okhttp.request.CommonOkHttpClient;
import com.example.yousheng.imoocsdk.okhttp.request.CommonRequest;
import com.example.yousheng.imoocsdk.okhttp.request.RequestParams;
import com.example.yousheng.imoocxianyu.module.recommand.BaseRecommandModel;
import com.example.yousheng.imoocxianyu.module.update.UpdateModel;
import com.example.yousheng.imoocxianyu.module.user.User;

/**
 * Created by yousheng on 17/5/6.
 *
 * @function 集中封装了所有应用层的网络请求
 * 在sdk的基础上再集中封装，为了在修改时只要在这个集中类中寻找修改
 * 因为包含了业务层具体的参数，所以不能封装在sdk层
 */

public class RequestCenter {

    //根据参数发送所有post请求
    public static void postRequest(String url, RequestParams params, DisposeDataListener listener, Class<?> clazz) {

        //  老接口
        //  CommonOkHttpClient.sendRequest(CommonRequest.createGetRequest(url,params),new CommonJsonCallback(new DisposeDataHandle(listener,clazz)));

        //用新的接口，此接口默认使用CommonJsonCallback
        CommonOkHttpClient.get(CommonRequest.createGetRequest(url, params), new DisposeDataHandle(listener, clazz));
    }

    public static void requestRecommandData(DisposeDataListener listener) {
        RequestCenter.postRequest(HttpConstants.HOME_RECOMMAND, null, listener, BaseRecommandModel.class);
    }

    public static void checkUpdate(DisposeDataListener listener) {
        RequestCenter.postRequest(HttpConstants.CHECK_UPDATE, null, listener, UpdateModel.class);
    }

    public static void loginIn(String user, String password, DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        params.put("user", user);
        params.put("password", password);
        //本来的发送登陆方式
//        RequestCenter.postRequest(HttpConstants.LOGIN, params, listener, User.class);
        //因为没有服务器，模拟登陆方式，返回固定数据
        RequestCenter.postRequest(HttpConstants.LOGIN, null, listener, User.class);
    }

}
