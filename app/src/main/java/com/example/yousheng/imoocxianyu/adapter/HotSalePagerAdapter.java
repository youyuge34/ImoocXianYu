package com.example.yousheng.imoocxianyu.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yousheng.imoocsdk.imageloader.ImageLoaderManger;
import com.example.yousheng.imoocxianyu.R;
import com.example.yousheng.imoocxianyu.module.recommand.RecommandBodyValue;

import java.util.ArrayList;

/**
 * Created by yousheng on 17/5/7.
 */

public class HotSalePagerAdapter extends PagerAdapter {

    Context mContext;
    ArrayList<RecommandBodyValue> mList;

    ImageLoaderManger mLoaderManger;
    LayoutInflater mInflater;

    public HotSalePagerAdapter(Context mContext, ArrayList<RecommandBodyValue> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mLoaderManger = ImageLoaderManger.getInstance(mContext);
        mInflater = LayoutInflater.from(mContext);
    }

    //为了无限循环设置成最大的整数
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    /**
     * * Determines whether a page View is associated with a specific key object
     * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate(ViewGroup)}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position  The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //为了无限循环，position取模
        final RecommandBodyValue value = mList.get(position % mList.size());
        //初始化控件
        View rootView = mInflater.inflate(R.layout.item_hot_product_pager_layout, null);
        TextView titleView = (TextView) rootView.findViewById(R.id.title_view);
        TextView infoView = (TextView) rootView.findViewById(R.id.info_view);
        TextView gonggaoView = (TextView) rootView.findViewById(R.id.gonggao_view);
        TextView saleView = (TextView) rootView.findViewById(R.id.sale_num_view);
        ImageView[] imageViews = new ImageView[3];
        imageViews[0] = (ImageView) rootView.findViewById(R.id.image_one);
        imageViews[1] = (ImageView) rootView.findViewById(R.id.image_two);
        imageViews[2] = (ImageView) rootView.findViewById(R.id.image_three);

        //绑定数据
        titleView.setText(value.title);
        infoView.setText(value.price);
        gonggaoView.setText(value.info);
        saleView.setText(value.text);
        for (int i = 0; i < imageViews.length; i++) {
            mLoaderManger.displayImage(imageViews[i], value.url.get(i));
        }

        //添加rootView进container
        // The adapter is responsible for adding the view to the container given here
        container.addView(rootView);

        return rootView;
    }
}
