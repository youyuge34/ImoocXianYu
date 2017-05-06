package com.example.yousheng.imoocxianyu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.yousheng.imoocsdk.imageloader.ImageLoaderManger;
import com.example.yousheng.imoocxianyu.module.recommand.RecommandBodyValue;

import java.util.ArrayList;

/**
 * Created by yousheng on 17/5/6.
 */

public class CourseAdapter extends BaseAdapter {

    /**
     * @function 不同类型的item标识，根据json中的标识，在getview()中加载不同类型的item布局
     */
    private static final int CARD_COUNT = 4;
    private static final int VIDOE_TYPE = 0x00;
    private static final int CARD_TYPE_ONE = 0x01;
    private static final int CARD_TYPE_TWO = 0x02;
    private static final int CARD_TYPE_THREE = 0x03;

    private LayoutInflater mInflate;
    private Context mContext;
    private ArrayList<RecommandBodyValue> mData;

    private ImageLoaderManger mImageLoaderManger;

    public CourseAdapter(Context mContext, ArrayList<RecommandBodyValue> mData) {
        this.mContext = mContext;
        this.mData = mData;
        mInflate = LayoutInflater.from(mContext);
        mImageLoaderManger = ImageLoaderManger.getInstance(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @function Returns the number of types of Views that will be created by
     * {@link #getView}. Each type represents a set of views that can be
     * converted in {@link #getView}.
     */
    @Override
    public int getViewTypeCount() {
        return CARD_COUNT;
    }

    /**
     * @param position
     * @return
     * @function Get the type of View that will be created by {@link #getView} for the specified item.
     * 根据服务器端的返回值type，决定子item是哪种类型，然后在getview()中填充不同的布局
     */
    @Override
    public int getItemViewType(int position) {
        RecommandBodyValue value = (RecommandBodyValue) getItem(position);
        return value.type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
