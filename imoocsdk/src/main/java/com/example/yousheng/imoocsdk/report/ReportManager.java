package com.example.yousheng.imoocsdk.report;


import com.example.yousheng.imoocsdk.module.monitor.Monitor;
import com.example.yousheng.imoocsdk.okhttp.HttpConstant.Params;
import com.example.yousheng.imoocsdk.okhttp.listener.DisposeDataHandle;
import com.example.yousheng.imoocsdk.okhttp.listener.DisposeDataListener;
import com.example.yousheng.imoocsdk.okhttp.request.CommonOkHttpClient;
import com.example.yousheng.imoocsdk.okhttp.request.CommonRequest;
import com.example.yousheng.imoocsdk.okhttp.request.RequestParams;
import com.example.yousheng.imoocsdk.util.Utils;

import java.util.ArrayList;

import static com.example.yousheng.imoocsdk.okhttp.HttpConstant.ATM_MONITOR;
import static com.example.yousheng.imoocsdk.okhttp.HttpConstant.ATM_PRE;
import static com.example.yousheng.imoocsdk.okhttp.HttpConstant.AVS;
import static com.example.yousheng.imoocsdk.okhttp.HttpConstant.IE;
import static com.example.yousheng.imoocsdk.okhttp.HttpConstant.SID;
import static com.example.yousheng.imoocsdk.okhttp.HttpConstant.STEP_CD;

/**
 * @author: vision
 * @function: 负责所有监测请求的发送
 * @date: 16/6/13
 */
public class ReportManager {

    /**
     * 默认的事件回调处理
     */
    private static DisposeDataHandle handle = new DisposeDataHandle(
            new DisposeDataListener() {
                @Override
                public void onSuccess(Object responseObj) {
                }

                @Override
                public void onFailure(Object reasonObj) {
                }
            });

    /**
     * send the sus monitor
     */
    public static void susReport(ArrayList<Monitor> monitors, boolean isAuto) {
        if (monitors != null && monitors.size() > 0) {
            for (Monitor monitor : monitors) {
                RequestParams params = new RequestParams();
                if (Utils.containString(monitor.url, ATM_PRE)) {
                    params.put("ve", "0");
                    if (isAuto) {
                        params.put("auto", "1");
                    }
                }
                CommonOkHttpClient.get(
                        CommonRequest.createMonitorRequest(monitor.url, params), handle);
            }
        }
    }

    /**
     * send the sueReoprt
     */
    public static void sueReport(ArrayList<Monitor> monitors, boolean isFull, long playTime) {
        if (monitors != null && monitors.size() > 0) {
            for (Monitor monitor : monitors) {
                RequestParams params = new RequestParams();
                if (Utils.containString(monitor.url, ATM_PRE)) {
                    if (isFull) {
                        params.put("fu", "1");
                    }
                    params.put("ve", String.valueOf(playTime));
                }
                CommonOkHttpClient.get(
                        CommonRequest.createMonitorRequest(monitor.url, params), handle);
            }
        }
    }

    /**
     * send the su report
     */
    public static void suReport(ArrayList<Monitor> monitors, long playTime) {
        if (monitors != null && monitors.size() > 0) {
            for (Monitor monitor : monitors) {
                RequestParams params = new RequestParams();
                if (monitor.time == playTime) {
                    if (Utils.containString(monitor.url, ATM_PRE)) {
                        params.put("ve", String.valueOf(playTime));
                    }
                    CommonOkHttpClient.get(
                            CommonRequest.createMonitorRequest(monitor.url, params), handle);
                }
            }
        }
    }

    /**
     * send the clicl full btn monitor
     *
     * @param monitors urls
     * @param playTime player time
     */
    public static void fullScreenReport(ArrayList<Monitor> monitors, long playTime) {
        if (monitors != null && monitors.size() > 0) {
            for (Monitor monitor : monitors) {
                RequestParams params = new RequestParams();
                if (Utils.containString(monitor.url, ATM_PRE)) {
                    params.put("ve", String.valueOf(playTime));
                }
                CommonOkHttpClient.get(
                        CommonRequest.createMonitorRequest(monitor.url, params), handle);
            }
        }
    }

    /**
     * send the click back full btn monitor
     *
     * @param monitors urls
     * @param playTime player time
     */
    public static void exitfullScreenReport(ArrayList<Monitor> monitors, long playTime) {
        if (monitors != null && monitors.size() > 0) {
            for (Monitor monitor : monitors) {
                RequestParams params = new RequestParams();
                if (Utils.containString(monitor.url, ATM_PRE)) {
                    params.put("ve", String.valueOf(playTime));
                }
                CommonOkHttpClient.get(
                        CommonRequest.createMonitorRequest(monitor.url, params), handle);
            }
        }
    }


    /**
     * send the video pause monitor
     *
     * @param monitors urls
     * @param playTime player time
     */
    public static void pauseVideoReport(ArrayList<Monitor> monitors, long playTime) {
        if (monitors != null && monitors.size() > 0) {
            for (Monitor monitor : monitors) {
                RequestParams params = new RequestParams();
                if (Utils.containString(monitor.url, ATM_PRE)) {
                    params.put("ve", String.valueOf(playTime));
                }
                CommonOkHttpClient.get(
                        CommonRequest.createMonitorRequest(monitor.url, params), handle);
            }
        }
    }

    /**
     * 发送广告是否正常解析及展示监测
     */
    public static void sendAdMonitor(boolean isPad, String sid, String ie, String appVersion, Params step, String result) {
        RequestParams params = new RequestParams();
        params.put(Params.lvs.getKey(), Params.lvs.getValue());
        params.put(Params.st.getKey(), Params.st.getValue());
        params.put(Params.os.getKey(), Params.os.getValue());
        params.put(Params.p.getKey(), Params.p.getValue());
        params.put(Params.appid.getKey(), Params.appid.getValue());
        if (isPad) {
            params.put(Params.bt_pad.getKey(), Params.bt_pad.getValue());
        } else {
            params.put(Params.bt_phone.getKey(), Params.bt_phone.getValue());
        }
        params.put(step.getKey(),
                step.getValue());
        params.put(STEP_CD, result);
        params.put(SID, sid);
        params.put(IE, ie);
        params.put(AVS, appVersion);

        CommonOkHttpClient.get(CommonRequest.createGetRequest(ATM_MONITOR, params), handle);
    }
}
