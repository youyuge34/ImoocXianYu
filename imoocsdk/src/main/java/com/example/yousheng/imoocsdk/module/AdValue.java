package com.example.yousheng.imoocsdk.module;


import com.example.yousheng.imoocsdk.module.monitor.Monitor;
import com.example.yousheng.imoocsdk.module.monitor.emevent.EMEvent;

import java.util.ArrayList;

/**
 * @author: qndroid
 * @function: 广告json value节点， 节点名字记得修改一下,视频信息实体类
 * @date: 16/6/13
 */
public class AdValue {

    public String resourceID;
    public String adid;
    public String resource;
    public String thumb;
    public ArrayList<Monitor> startMonitor;
    public ArrayList<Monitor> middleMonitor;
    public ArrayList<Monitor> endMonitor;
    public String clickUrl;
    public ArrayList<Monitor> clickMonitor;
    public EMEvent event;
    public String type;
}
