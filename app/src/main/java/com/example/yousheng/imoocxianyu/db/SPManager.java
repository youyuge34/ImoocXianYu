package com.example.yousheng.imoocxianyu.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.yousheng.imoocxianyu.application.ImoocApplication;

/**
 * Created by yousheng on 17/6/1.
 * @function 配置文件工具类,SharedPrefence的使用演示和封装,单例模式
 */

public class SPManager {

    /**
     * 演示：普通SharedPrefence的用法范例
     */
    private void testSharedPrefence(){
        //获取sp对象
        SharedPreferences sp = ImoocApplication.getInstance().
                getSharedPreferences("setting", Context.MODE_PRIVATE);

        //sp取出数据
        sp.getString("network","");

        //sp存数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("network","1");
        editor.commit();
        //新api
//        editor.apply();
    }


    /**
     * sp变量
     */
    private static SharedPreferences sp = null;
    private static SPManager spManger = null;
    private static SharedPreferences.Editor editor = null;

    /**
     * Preference文件名
     */
    private static final String SHARE_PREFREENCE_NAME = "manyou1.pre";

    //sp的key值集合
    public static final String VIDEO_PLAY_SETTING = "video_play_setting";

    //单例模式
    private SPManager(){
        sp = ImoocApplication.getInstance().
                getSharedPreferences(SHARE_PREFREENCE_NAME,Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    //单例模式
    public static SPManager getInstance(){
        if(spManger == null|| sp == null || editor == null){
            spManger = new SPManager();
        }
        return spManger;
    }


    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public void putLong(String key, Long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLong(String key, int defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public boolean isKeyExist(String key) {
        return sp.contains(key);
    }

    public float getFloat(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }
}
