package com.example.yousheng.imoocxianyu.view.fragment.home;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yousheng.imoocsdk.okhttp.listener.DisposeDataListener;
import com.example.yousheng.imoocxianyu.R;
import com.example.yousheng.imoocxianyu.adapter.CourseAdapter;
import com.example.yousheng.imoocxianyu.module.recommand.BaseRecommandModel;
import com.example.yousheng.imoocxianyu.network.http.RequestCenter;
import com.example.yousheng.imoocxianyu.view.fragment.BaseFragment;
import com.example.yousheng.imoocxianyu.view.home.HomeHeaderLayout;

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
        }
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
