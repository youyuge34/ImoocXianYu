package com.example.yousheng.imoocxianyu.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yousheng.imoocsdk.util.Utils;
import com.example.yousheng.imoocxianyu.R;
import com.example.yousheng.imoocxianyu.activity.base.BaseActivity;
import com.example.yousheng.imoocxianyu.adapter.PhotoPageAdapter;
import com.example.yousheng.imoocxianyu.util.Util;

import java.util.ArrayList;

/**
 * Created by yousheng on 17/6/5.
 *
 * @function 点击图片，滑动显示大图的活动
 */

public class PhotoViewActivity extends BaseActivity implements View.OnClickListener {
    private static final String PHOTO_LIST = "photo_list";
    private static final String CURRENT_POSITION = "current_position";
    /**
     * UI
     */
    private ViewPager mPager;
    private TextView mIndictorView;
    private ImageView mShareView;
    /**
     * Data
     */
    private PhotoPageAdapter mAdapter;
    private ArrayList<String> mPhotoLists;
    private int mLenght;
    private int currentPos;


    public static void newInstance(Context context, ArrayList<String> mPhotoLists) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra(PHOTO_LIST, mPhotoLists);
        context.startActivity(intent);
    }

    public static void newInstance(Context context, ArrayList<String> mPhotoLists, int position) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra(PHOTO_LIST, mPhotoLists);
        intent.putExtra(CURRENT_POSITION, position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view_layout);

        initData();
        initView();
    }

    private void initData() {
        mPhotoLists = getIntent().getStringArrayListExtra(PHOTO_LIST);
        mLenght = mPhotoLists.size();
        currentPos=getIntent().getIntExtra(CURRENT_POSITION, 0);
    }

    private void initView() {
        mIndictorView = (TextView) findViewById(R.id.indictor_view);
        mIndictorView.setText((currentPos + 1) + "/" + mLenght);
        mShareView = (ImageView) findViewById(R.id.share_view);
        mShareView.setOnClickListener(this);
        mPager = (ViewPager) findViewById(R.id.photo_pager);
        //false->使用photoview，true->使用imageview
        mAdapter = new PhotoPageAdapter(this, mPhotoLists, false);
        mPager.setPageMargin(Utils.dip2px(this, 30));
        mPager.setAdapter(mAdapter);

        //设置点击后第一个显示的pager位置
        mPager.setCurrentItem(currentPos);
        //设置pager滑动后顶部数字变化
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndictorView.setText((position + 1) + "/" + mLenght);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Util.hideSoftInputMethod(this, mIndictorView);
    }

    @Override
    public void onClick(View v) {

    }
}
