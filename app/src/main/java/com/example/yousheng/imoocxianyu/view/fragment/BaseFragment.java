package com.example.yousheng.imoocxianyu.view.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by yousheng on 17/5/4.
 *
 * @function 基类fragment,主要是为我们所有的fragment提供公共的行为或者事件
 */

public class BaseFragment extends Fragment {
    //这样在子类中初始化后，随时可以调用到上下文
    protected Activity mContext;

}
