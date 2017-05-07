package com.example.yousheng.imoocxianyu.view.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yousheng.imoocsdk.imageloader.ImageLoaderManger;
import com.example.yousheng.imoocxianyu.R;
import com.example.yousheng.imoocxianyu.adapter.PhotoPageAdapter;
import com.example.yousheng.imoocxianyu.module.recommand.RecommandHeadValue;
import com.example.yousheng.imoocxianyu.view.viewpagerindictor.CirclePageIndicator;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by yousheng on 17/5/7.
 *
 * @function 自定义布局：主界面的头布局，由ads,middle,footer三部分组成
 */

public class HomeHeaderLayout extends RelativeLayout {

    /**
     * UI
     */
    private RelativeLayout mRootView;
    private AutoScrollViewPager mViewPager;
    private CirclePageIndicator mPagerIndicator;
    private TextView mHotView;
    private PhotoPageAdapter mAdapter;
    private ImageView[] mImageViews = new ImageView[4];
    private LinearLayout mFootLayout;

    /**
     * data
     */
    RecommandHeadValue mHeadValue;
    ImageLoaderManger mImageLoader;
    LayoutInflater mInflater;
    Context mContext;

    public HomeHeaderLayout(Context context, RecommandHeadValue headValue) {
        this(context, null, headValue);
    }

    public HomeHeaderLayout(Context context, AttributeSet attrs, RecommandHeadValue headValue) {
        super(context, attrs);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = ImageLoaderManger.getInstance(mContext);
        mHeadValue = headValue;

        initView();
    }

    /**
     * @function 初始化头布局，从上到下里面有
     * ads：一个图片轮播器和指示器
     * hotView："今日最热"的分隔符
     * middle：4张图
     * footer：几个HomeBottomItem，又是一个自定义布局
     */
    private void initView() {
        mRootView = (RelativeLayout) mInflater.inflate(R.layout.listview_home_head_layout, this);

        //ads部分是个viewpager轮播器
        mViewPager = (AutoScrollViewPager) mRootView.findViewById(R.id.pager);

        //指示器
        mPagerIndicator = (CirclePageIndicator) mRootView.findViewById(R.id.pager_indictor_view);

        //今日最新的分隔栏，在轮播器下方
        mHotView = (TextView) mRootView.findViewById(R.id.zuixing_view);
        mHotView.setText(mContext.getString(R.string.today_zuixing));

        //middle 4图片的初始化
        mImageViews[0] = (ImageView) mRootView.findViewById(R.id.head_image_one);
        mImageViews[1] = (ImageView) mRootView.findViewById(R.id.head_image_two);
        mImageViews[2] = (ImageView) mRootView.findViewById(R.id.head_image_three);
        mImageViews[3] = (ImageView) mRootView.findViewById(R.id.head_image_four);
        mFootLayout = (LinearLayout) mRootView.findViewById(R.id.content_layout);

        //ads部分:viewpager加载adapter,true表示用imageview而非photoview
        setAdsViewPager(true);

        //middle部分
        setMiddlePictures();

        //footer部分
        setFooterButton();
    }

    /**
     * @funciton ads部分viewpager加载adapter
     * @param isMatch true表示用imageview而非photoview
     */
    private void setAdsViewPager(boolean isMatch) {
        mAdapter = new PhotoPageAdapter(mContext, mHeadValue.ads, isMatch);
        mViewPager.setAdapter(mAdapter);
        mViewPager.startAutoScroll(3000);
        mPagerIndicator.setViewPager(mViewPager);
    }

    /**
     *  @function middle部分就是四张图片
     */
    private void setMiddlePictures() {
        for(int i=0;i<mHeadValue.middle.size();i++){
            mImageLoader.displayImage(mImageViews[i],mHeadValue.middle.get(i));
        }
    }

    /**
     *  @function footer部分是一组自定义布局：HomeFooterButton
     */
    private void setFooterButton() {
        for(int i=0;i<mHeadValue.footer.size();i++){
            HomeFooterButton footerButton= new HomeFooterButton(mContext,mHeadValue.footer.get(i));
            //因为HomeFooterButton自己有布局文件，所以不需要用代码的方式来添加布局参数
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//            footerButton.setLayoutParams(params);
            mFootLayout.addView(footerButton);
        }
    }


}
