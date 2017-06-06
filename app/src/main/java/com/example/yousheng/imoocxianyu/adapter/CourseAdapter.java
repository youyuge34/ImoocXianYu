package com.example.yousheng.imoocxianyu.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yousheng.imoocsdk.activity.AdBrowserActivity;
import com.example.yousheng.imoocsdk.constant.LogUtils;
import com.example.yousheng.imoocsdk.core.AdContextInterface;
import com.example.yousheng.imoocsdk.core.video.VideoAdContext;
import com.example.yousheng.imoocsdk.imageloader.ImageLoaderManger;
import com.example.yousheng.imoocsdk.util.Utils;
import com.example.yousheng.imoocxianyu.R;
import com.example.yousheng.imoocxianyu.activity.PhotoViewActivity;
import com.example.yousheng.imoocxianyu.module.recommand.RecommandBodyValue;
import com.example.yousheng.imoocxianyu.util.Util;
import com.google.gson.Gson;

import java.util.ArrayList;

import cn.sharesdk.onekeyshare.OnekeyShare;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yousheng on 17/5/6.
 */

public class CourseAdapter extends BaseAdapter {

    /**
     * @function 不同类型的item标识，根据json中的标识，在getview()中加载不同类型的item布局
     * TYPE_ONE 为图片列表
     * TYPE_TWO 为单图
     * TYPE_THREE 为无限viewPager
     */
    private static final int CARD_COUNT = 4;
    private static final int VIDOE_TYPE = 0x00;
    private static final int CARD_TYPE_ONE = 0x01;
    private static final int CARD_TYPE_TWO = 0x02;
    private static final int CARD_TYPE_THREE = 0x03;

    private LayoutInflater mInflate;
    private Context mContext;
    private ArrayList<RecommandBodyValue> mData;
    private ViewHolder mViewHolder;
    private ImageLoaderManger mImageLoader;
    private VideoAdContext mAdsdkContext;

    public CourseAdapter(Context mContext, ArrayList<RecommandBodyValue> mData) {
        this.mContext = mContext;
        this.mData = mData;
        mInflate = LayoutInflater.from(mContext);
        mImageLoader = ImageLoaderManger.getInstance(mContext);
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
        //获取item种类
        int type = getItemViewType(position);
        //获取当前位置item的服务器数据
        final RecommandBodyValue value = mData.get(position);

        //若缓存布局为空，则开始新建
        if (convertView == null) {
            switch (type) {
                case CARD_TYPE_ONE:
                    mViewHolder = new ViewHolder();
                    convertView = mInflate.inflate(R.layout.item_product_card_one_layout, parent, false);
                    mViewHolder.mLogoView = (CircleImageView) convertView.findViewById(R.id.item_logo_view);
                    mViewHolder.mTitleView = (TextView) convertView.findViewById(R.id.item_title_view);
                    mViewHolder.mInfoView = (TextView) convertView.findViewById(R.id.item_info_view);
                    mViewHolder.mFooterView = (TextView) convertView.findViewById(R.id.item_footer_view);
                    mViewHolder.mPriceView = (TextView) convertView.findViewById(R.id.item_price_view);
                    mViewHolder.mFromView = (TextView) convertView.findViewById(R.id.item_from_view);
                    mViewHolder.mZanView = (TextView) convertView.findViewById(R.id.item_zan_view);
                    mViewHolder.mProductLayout = (LinearLayout) convertView.findViewById(R.id.product_photo_layout);

                    break;
                case CARD_TYPE_TWO:
                    mViewHolder = new ViewHolder();
                    convertView = mInflate.inflate(R.layout.item_product_card_two_layout, parent, false);
                    mViewHolder.mLogoView = (CircleImageView) convertView.findViewById(R.id.item_logo_view);
                    mViewHolder.mTitleView = (TextView) convertView.findViewById(R.id.item_title_view);
                    mViewHolder.mInfoView = (TextView) convertView.findViewById(R.id.item_info_view);
                    mViewHolder.mFooterView = (TextView) convertView.findViewById(R.id.item_footer_view);
                    mViewHolder.mProductView = (ImageView) convertView.findViewById(R.id.product_photo_view);
                    mViewHolder.mPriceView = (TextView) convertView.findViewById(R.id.item_price_view);
                    mViewHolder.mFromView = (TextView) convertView.findViewById(R.id.item_from_view);
                    mViewHolder.mZanView = (TextView) convertView.findViewById(R.id.item_zan_view);
                    break;
                case CARD_TYPE_THREE:
                    mViewHolder = new ViewHolder();
                    convertView = mInflate.inflate(R.layout.item_product_card_three_layout, parent, false);
                    mViewHolder.mViewPager = (ViewPager) convertView.findViewById(R.id.pager);

                    break;

                case VIDOE_TYPE:
                    //显示video卡片
                    mViewHolder = new ViewHolder();
                    convertView = mInflate.inflate(R.layout.item_video_layout, parent, false);
                    mViewHolder.mVieoContentLayout = (RelativeLayout)
                            convertView.findViewById(R.id.video_ad_layout);
                    mViewHolder.mLogoView = (CircleImageView) convertView.findViewById(R.id.item_logo_view);
                    mViewHolder.mTitleView = (TextView) convertView.findViewById(R.id.item_title_view);
                    mViewHolder.mInfoView = (TextView) convertView.findViewById(R.id.item_info_view);
                    mViewHolder.mFooterView = (TextView) convertView.findViewById(R.id.item_footer_view);
                    mViewHolder.mShareView = (ImageView) convertView.findViewById(R.id.item_share_view);



                    break;

            }
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        //开始绑定数据到view层
        switch (type) {
            case VIDOE_TYPE:
                mImageLoader.displayImage(mViewHolder.mLogoView, value.logo);
                mViewHolder.mTitleView.setText(value.title);
                mViewHolder.mInfoView.setText(value.info.concat(mContext.getString(R.string.tian_qian)));
                mViewHolder.mFooterView.setText(value.text);

                //视频api第一步：为对应布局创建播放器api层
                mAdsdkContext = new VideoAdContext(mViewHolder.mVieoContentLayout,
                        new Gson().toJson(value), null);
                LogUtils.d("MraidVideoView", value.resource);
                mAdsdkContext.setAdResultListener(new AdContextInterface() {
                    @Override
                    public void onAdSuccess() {
                        LogUtils.d("test1", "mAdsdkContext回调成功");
                    }

                    @Override
                    public void onAdFailed() {
                        LogUtils.d("test1", "mAdsdkContext回调失败");
                    }

                    @Override
                    public void onClickVideo(String url) {
                        Intent intent = new Intent(mContext, AdBrowserActivity.class);
                        intent.putExtra(AdBrowserActivity.KEY_URL, url);
                        mContext.startActivity(intent);
                    }
                });

                mViewHolder.mShareView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showShare();
                    }
                });

                break;

            case CARD_TYPE_ONE:
                mImageLoader.displayImage(mViewHolder.mLogoView, value.logo);
                mViewHolder.mTitleView.setText(value.title);
                mViewHolder.mInfoView.setText(value.info.concat(mContext.getString(R.string.tian_qian)));
                mViewHolder.mFooterView.setText(value.text);
                mViewHolder.mPriceView.setText(value.price);
                mViewHolder.mFromView.setText(value.from);
                mViewHolder.mZanView.setText(mContext.getString(R.string.dian_zan).concat(value.zan));

                //因为会布局回收复用，所以要先清空原先的图片布局
                mViewHolder.mProductLayout.removeAllViews();

                //动态遍历添加imageview进入水平scrollview中
                for (int i=0;i<value.url.size();i++) {
                    ImageView image = createImageView(value.url.get(i));
                    mViewHolder.mProductLayout.addView(image);

                    //为每张图添加点击事件，可以跳转到大图滑动浏览
                    final int finalI = i;
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PhotoViewActivity.newInstance(mContext,value.url, finalI);
                        }
                    });
                }



                break;
            case CARD_TYPE_TWO:
                mViewHolder.mTitleView.setText(value.title);
                mViewHolder.mInfoView.setText(value.info.concat(mContext.getString(R.string.tian_qian)));
                mViewHolder.mFooterView.setText(value.text);
                mViewHolder.mPriceView.setText(value.price);
                mViewHolder.mFromView.setText(value.from);
                mViewHolder.mZanView.setText(mContext.getString(R.string.dian_zan).concat(value.zan));
                //为单个ImageView加载远程图片
                mImageLoader.displayImage(mViewHolder.mLogoView, value.logo);
                mImageLoader.displayImage(mViewHolder.mProductView, value.url.get(0));
                break;

            case CARD_TYPE_THREE:
                //将服务器拼接过的一组数据转拆分成多组数据的集合,供给viewpager使用
                ArrayList<RecommandBodyValue> valueList = Util.handlePagerData(value);
                mViewHolder.mViewPager.setAdapter(new HotSalePagerAdapter(mContext, valueList));

                //为了向左也能无限滑动，让初始的item位置在中间
                mViewHolder.mViewPager.setCurrentItem(valueList.size() * 100);
                break;

        }
        return convertView;
    }

    //视频api第二步：自动播放方法
    public void updateAdInScrollView() {
        if (mAdsdkContext != null) {
            mAdsdkContext.updateAdInScrollView();
        }
//        else LogUtils.d("test1", "mAdsdkContext为null");
    }

    /**
     * @param url
     * @return
     * @function 动态代码创建imageView
     */
    private ImageView createImageView(String url) {
        ImageView imageView = new ImageView(mContext);

        //要与groupview的类型一致。这里是添加到LinearLayout中，所以要用LinearLayout的LayoutParams，若是
        //用别的layout的param，不会报错，但是特殊的param识别不出
        LinearLayout.LayoutParams params = new LinearLayout
                //LayoutParams接收的是a fixed size in pixels，所以需要把dp转成px传入
                .LayoutParams(Utils.dip2px(mContext, 100), ViewGroup.LayoutParams.MATCH_PARENT);

        params.leftMargin = Utils.dip2px(mContext, 5);
        imageView.setLayoutParams(params);
        mImageLoader.displayImage(imageView, url);
        return imageView;
    }

    /**
     * shareSdk分享快速测试
     */
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(mContext);
    }

    public void destroy() {
        if (mAdsdkContext != null) {
            mAdsdkContext.destroy();
        }
    }

    //用来缓存控件
    private static class ViewHolder {
        //所有Card共有属性
        private CircleImageView mLogoView;
        private TextView mTitleView;
        private TextView mInfoView;
        private TextView mFooterView;
        //Video Card特有属性
        private RelativeLayout mVieoContentLayout;
        private ImageView mShareView;

        //Video Card外所有Card具有属性
        private TextView mPriceView;
        private TextView mFromView;
        private TextView mZanView;
        //Card One特有属性
        private LinearLayout mProductLayout;
        //Card Two特有属性
        private ImageView mProductView;
        //Card Three特有属性
        private ViewPager mViewPager;
    }
}
