package com.example.yousheng.imoocxianyu.view.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yousheng.imoocsdk.imageloader.ImageLoaderManger;
import com.example.yousheng.imoocxianyu.R;
import com.example.yousheng.imoocxianyu.module.recommand.RecommandFooterValue;

/**
 * Created by yousheng on 17/5/7.
 * @function 自定义布局：头布局中的footer布局
 */

public class HomeFooterButton extends RelativeLayout {

    /**
     * UI
     */
    private RelativeLayout mRootView;
    private TextView mTitleView;
    private TextView mInfoView;
    private TextView mInterestingView;
    private ImageView mImageOneView;
    private ImageView mImageTwoView;

    /**
     * data
     */
    Context mContext;
    ImageLoaderManger mLoader;
    LayoutInflater mInflater;
    RecommandFooterValue mData;


    public HomeFooterButton(Context context, RecommandFooterValue values) {
        this(context, null, values);
    }

    public HomeFooterButton(Context context, AttributeSet attrs, RecommandFooterValue values) {
        super(context, attrs);
        mContext = context;
        mLoader = ImageLoaderManger.getInstance(context);
        mInflater = LayoutInflater.from(context);
        mData = values;

        initView();
    }

    private void initView() {
        mRootView = (RelativeLayout) mInflater.inflate(R.layout.item_home_recommand_layout, this);
        mTitleView = (TextView) mRootView.findViewById(R.id.title_view);
        mInfoView = (TextView) mRootView.findViewById(R.id.info_view);
        mInterestingView = (TextView) mRootView.findViewById(R.id.interesting_view);
        mImageOneView = (ImageView) mRootView.findViewById(R.id.icon_1);
        mImageTwoView = (ImageView) mRootView.findViewById(R.id.icon_2);

        mTitleView.setText(mData.title);
        mInfoView.setText(mData.info);
        mInterestingView.setText(mData.from);
        mLoader.displayImage(mImageOneView, mData.imageOne);
        mLoader.displayImage(mImageTwoView, mData.imageTwo);
    }


}
