package com.example.yousheng.imoocxianyu.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.yousheng.imoocsdk.imageloader.ImageLoaderManger;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by yousheng on 17/5/7.
 * @function 首页轮播器的adapter
 */

public class PhotoPageAdapter extends PagerAdapter {
    Context mContext;
    ArrayList<String> mData;
    boolean mIsMatch;
    ImageLoaderManger mLoader;

    public PhotoPageAdapter(Context context, ArrayList<String> list, boolean isMatch) {
        mData = list;
        mContext = context;
        mIsMatch = isMatch;
        mLoader = ImageLoaderManger.getInstance(mContext);
    }

    /**
     * @param container
     * @param position
     * @return
     * @function 用代码初始化轮播器里的图片
     * 若是isMatch为true，则用imageview，否则用photoview可放大
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView photoView;
        if (mIsMatch) {
            photoView = new ImageView(mContext);
            photoView.setScaleType(ImageView.ScaleType.FIT_XY);

        } else {
            photoView = new PhotoView(mContext);
        }
        mLoader.displayImage(photoView, mData.get(position));
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
