package com.example.yousheng.imoocxianyu.view.fragment.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yousheng.imoocsdk.constant.LogUtils;
import com.example.yousheng.imoocsdk.okhttp.listener.DisposeDataListener;
import com.example.yousheng.imoocxianyu.R;
import com.example.yousheng.imoocxianyu.activity.PhotoViewActivity;
import com.example.yousheng.imoocxianyu.adapter.CourseAdapter;
import com.example.yousheng.imoocxianyu.constant.Constant;
import com.example.yousheng.imoocxianyu.module.recommand.BaseRecommandModel;
import com.example.yousheng.imoocxianyu.module.recommand.RecommandBodyValue;
import com.example.yousheng.imoocxianyu.network.http.RequestCenter;
import com.example.yousheng.imoocxianyu.view.fragment.BaseFragment;
import com.example.yousheng.imoocxianyu.view.home.HomeHeaderLayout;
import com.example.yousheng.imoocxianyu.zxing.app.CaptureActivity;

/**
 * Created by yousheng on 17/5/4.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener,AdapterView.OnItemClickListener{
    private static final int REQUEST_QRCODE = 0x01;
    private static final String TAG = "HomeFragment";
    /**
     * UI
     */
    private View mContentView;
    private ListView mListView;
    private TextView mQRCodeView;
    private TextView mCategoryView;
    private TextView mSearchView;
    private ImageView mLoadingView;

    /**
     * data
     */
    private CourseAdapter mAdapter;
    private BaseRecommandModel mRecommandData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //发送推荐产品的请求
        requestRecommandData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext=getActivity();
        mContentView = inflater.inflate(R.layout.fragment_home_layout,container,false);
        initView();
        return mContentView;
    }

    private void initView() {
        mQRCodeView = (TextView) mContentView.findViewById(R.id.qrcode_view);
        mQRCodeView.setOnClickListener(this);
        mCategoryView = (TextView) mContentView.findViewById(R.id.category_view);
        mCategoryView.setOnClickListener(this);
        mSearchView = (TextView) mContentView.findViewById(R.id.search_view);
        mSearchView.setOnClickListener(this);
        mListView = (ListView) mContentView.findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);
        //开始loadingview的帧动画
        mLoadingView = (ImageView) mContentView.findViewById(R.id.loading_view);
        AnimationDrawable anim = (AnimationDrawable) mLoadingView.getDrawable();
        anim.start();
    }

    /**
     * @function 发送首页列表数据的请求
     */
    private void requestRecommandData() {
        RequestCenter.requestRecommandData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                Log.e(TAG, "onSuccess: "+responseObj.toString());
                mRecommandData = (BaseRecommandModel) responseObj;
                //更新UI
                showSuccessView();
            }

            @Override
            public void onFailure(Object reasonObj) {
                Log.e(TAG, "onFailure: "+reasonObj.toString());
            }
        });

    }

    /**
     * 请求成功，显示listview主页
     */
    private void showSuccessView() {

        //健壮性判断
        if(mRecommandData != null && mRecommandData.data.list.size()>0){
            mLoadingView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);

            //添加头布局
            mListView.addHeaderView(new HomeHeaderLayout(mContext,mRecommandData.data.head));

            //创建adapter
            mAdapter = new CourseAdapter(mContext, mRecommandData.data.list);
            mListView.setAdapter(mAdapter);

            //滑动事件监听，为了能自动播放视频
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    mAdapter.updateAdInScrollView();
                }
            });



        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qrcode_view:
                if (hasPermission(Constant.HARDWEAR_CAMERA_PERMISSION)) {
                    doOpenCamera();
                } else {
                    requestPermission(Constant.HARDWEAR_CAMERA_CODE, Constant.HARDWEAR_CAMERA_PERMISSION);
                }
                break;
        }
    }

    /**
     * 点击二维码图标，若是请求允许则调用CaptureActivity活动
     */
    @Override
    public void doOpenCamera() {
        super.doOpenCamera();
        Intent intent = new Intent(mContext, CaptureActivity.class);
        startActivityForResult(intent,REQUEST_QRCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_QRCODE:
                if (resultCode == Activity.RESULT_OK) {
                    String code = data.getStringExtra("SCAN_RESULT");
                    if (code.contains("http") || code.contains("https")) {
                        Toast.makeText(mContext, code, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, code, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //要减去列表头部个数
        RecommandBodyValue value = (RecommandBodyValue) mAdapter.getItem(position - mListView.getHeaderViewsCount());
        if (value.type != 0) {
            //若非视频，则启动滑动大图活动
            LogUtils.d(TAG,"onItemClick--->"+value.url.size());
            PhotoViewActivity.newInstance(mContext,value.url);
        }
    }

    @Override
    public void onDestroy() {
        if(mAdapter!=null){
            mAdapter.destroy();
        }
        super.onDestroy();
    }
}
