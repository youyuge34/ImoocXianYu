package com.example.yousheng.imoocxianyu.util;

import com.example.yousheng.imoocxianyu.module.recommand.RecommandBodyValue;

import java.util.ArrayList;

/**
 * Created by yousheng on 17/5/7.
 */

public class Util {

    /**
     * @function 将服务器拼接过的一组数据转拆分成多组数据的集合,供给viewpager
     * @return
     */
    public static ArrayList<RecommandBodyValue> handlePagerData(RecommandBodyValue value){
        ArrayList<RecommandBodyValue> values = new ArrayList<>();
        String[] titles = value.title.split("@");
        String[] infos = value.info.split("@");
        String[] prices = value.price.split("@");
        String[] texts = value.text.split("@");
        ArrayList<String> urls = value.url;

        int start = 0;
        for(int i=0;i<titles.length;i++){
            RecommandBodyValue tempValue= new RecommandBodyValue();
            tempValue.title = titles[i];
            tempValue.info = infos[i];
            tempValue.price = prices[i];
            tempValue.text = texts[i];

            //每个小page有三个图片url
            ArrayList<String> tempList = new ArrayList<>();
            for(int k=start;k<start+3;k++){
                tempList.add(urls.get(k));
            }
            tempValue.url = tempList;
            start+=3;

            values.add(tempValue);
        }

        return values;
    }
}
