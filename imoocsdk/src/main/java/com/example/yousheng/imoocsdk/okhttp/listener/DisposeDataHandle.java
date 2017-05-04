package com.example.yousheng.imoocsdk.okhttp.listener;

/**
 * 因为需要json数据到实体的转化，所以需要转化成对象的字节码对象（class）,这样才知道转化成什么实体对象
 * 相当于对封装的callback的预处理，多了个json直接转化的方法
 * Created by yousheng on 17/5/4.
 */

public class DisposeDataHandle {
    public DisposeDataListener mListener = null;
    public Class<?> mClass = null;

    public DisposeDataHandle(DisposeDataListener mListener) {
        this.mListener = mListener;
    }

    //若参数有class则要在回调中转换成实体对象
    public DisposeDataHandle(DisposeDataListener mListener, Class<?> mClass) {
        this.mListener = mListener;
        this.mClass = mClass;
    }


}
