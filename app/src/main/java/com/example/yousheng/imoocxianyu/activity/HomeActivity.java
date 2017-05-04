package com.example.yousheng.imoocxianyu.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yousheng.imoocxianyu.R;
import com.example.yousheng.imoocxianyu.activity.base.BaseActivity;
import com.example.yousheng.imoocxianyu.view.fragment.home.HomeFragment;
import com.example.yousheng.imoocxianyu.view.fragment.home.MessageFragment;
import com.example.yousheng.imoocxianyu.view.fragment.home.MineFragment;

public class HomeActivity extends BaseActivity  implements View.OnClickListener{
    private FragmentManager fm;
    private HomeFragment mHomeFragment;
    private Fragment mCommonFragmentOne;
    private MessageFragment mMessageFragment;
    private MineFragment mMineFragment;
    private Fragment mCurrent;

    private RelativeLayout mHomeLayout;
    private RelativeLayout mPondLayout;
    private RelativeLayout mMessageLayout;
    private RelativeLayout mMineLayout;
    private TextView mHomeView;
    private TextView mPondView;
    private TextView mMessageView;
    private TextView mMineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout);

        initView();

        //添加默认要显示的fragment,一般先写好commit方法，再去写过程，怕忘记
        mHomeFragment = new HomeFragment();
        fm=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fm.beginTransaction();
        /**
         * fragment进行动态加载三种方式：
         * 1、replace：将碎片栈中所有碎片remove后，add进新的fragment。即我们栈中只有一个fragment，别的都会被移除掉（实例也会被回收）。
         * 2、hide和show只是把原来的隐藏掉，占用比较大的内存，但是最常用，因为不会销毁实例！
         * 3、attach和detach鸡肋，基本不会用到，不会销毁fragment，但是会把view销毁，内存上没有变少，反而每次attach的时候还要重绘view。
         */
        fragmentTransaction.replace(R.id.content_layout,mHomeFragment);
        fragmentTransaction.commit();
    }

    private void initView() {
        mHomeLayout = (RelativeLayout) findViewById(R.id.home_layout_view);
        mHomeLayout.setOnClickListener(this);
        mPondLayout = (RelativeLayout) findViewById(R.id.pond_layout_view);
        mPondLayout.setOnClickListener(this);
        mMessageLayout = (RelativeLayout) findViewById(R.id.message_layout_view);
        mMessageLayout.setOnClickListener(this);
        mMineLayout = (RelativeLayout) findViewById(R.id.mine_layout_view);
        mMineLayout.setOnClickListener(this);

        mHomeView = (TextView) findViewById(R.id.home_image_view);
        mPondView = (TextView) findViewById(R.id.fish_image_view);
        mMessageView = (TextView) findViewById(R.id.message_image_view);
        mMineView = (TextView) findViewById(R.id.mine_image_view);
        mHomeView.setBackgroundResource(R.drawable.comui_tab_home_selected);
    }

    /**
     * @function 点击底部不同按钮，切换按钮图标，切换填充的fragment
     * @param v
     */
    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction=fm.beginTransaction();
        switch (v.getId()){
            case R.id.home_layout_view:
                mHomeView.setBackgroundResource(R.drawable.comui_tab_home_selected);
                mPondView.setBackgroundResource(R.drawable.comui_tab_pond);
                mMessageView.setBackgroundResource(R.drawable.comui_tab_message);
                mMineView.setBackgroundResource(R.drawable.comui_tab_person);

                hideFragment(mMessageFragment,fragmentTransaction);
                hideFragment(mMineFragment,fragmentTransaction);
                hideFragment(mCommonFragmentOne,fragmentTransaction);

                //若为空，则创建新的实例并添加，若实例已存在则show
                if(mHomeFragment==null){
                    mHomeFragment=new HomeFragment();
                    fragmentTransaction.add(R.id.content_layout,mHomeFragment);
                }else {
                    fragmentTransaction.show(mHomeFragment);
                }
                break;

            case R.id.message_layout_view:
                mMessageView.setBackgroundResource(R.drawable.comui_tab_message_selected);
                mHomeView.setBackgroundResource(R.drawable.comui_tab_home);
                mPondView.setBackgroundResource(R.drawable.comui_tab_pond);
                mMineView.setBackgroundResource(R.drawable.comui_tab_person);

                hideFragment(mCommonFragmentOne, fragmentTransaction);
                hideFragment(mHomeFragment, fragmentTransaction);
                hideFragment(mMineFragment, fragmentTransaction);
                if (mMessageFragment == null) {
                    mMessageFragment = new MessageFragment();
                    fragmentTransaction.add(R.id.content_layout, mMessageFragment);
                } else {
                    mCurrent = mMessageFragment;
                    fragmentTransaction.show(mMessageFragment);
                }
                break;

            case R.id.mine_layout_view:
                mMineView.setBackgroundResource(R.drawable.comui_tab_person_selected);
                mHomeView.setBackgroundResource(R.drawable.comui_tab_home);
                mPondView.setBackgroundResource(R.drawable.comui_tab_pond);
                mMessageView.setBackgroundResource(R.drawable.comui_tab_message);
                hideFragment(mCommonFragmentOne, fragmentTransaction);
                hideFragment(mMessageFragment, fragmentTransaction);
                hideFragment(mHomeFragment, fragmentTransaction);
                if (mMineFragment == null) {
                    mMineFragment = new MineFragment();
                    fragmentTransaction.add(R.id.content_layout, mMineFragment);
                } else {
                    mCurrent = mMineFragment;
                    fragmentTransaction.show(mMineFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideFragment(Fragment fragment,FragmentTransaction fragmentTransaction){
        if(fragment!=null){
            fragmentTransaction.hide(fragment);
        }
    }
}
