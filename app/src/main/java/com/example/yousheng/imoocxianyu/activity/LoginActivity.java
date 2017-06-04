package com.example.yousheng.imoocxianyu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yousheng.imoocsdk.constant.LogUtils;
import com.example.yousheng.imoocsdk.okhttp.listener.DisposeDataListener;
import com.example.yousheng.imoocxianyu.R;
import com.example.yousheng.imoocxianyu.activity.base.BaseActivity;
import com.example.yousheng.imoocxianyu.manager.DialogManager;
import com.example.yousheng.imoocxianyu.manager.UserManager;
import com.example.yousheng.imoocxianyu.module.PushMessage;
import com.example.yousheng.imoocxianyu.module.user.User;
import com.example.yousheng.imoocxianyu.network.http.RequestCenter;
import com.example.yousheng.imoocxianyu.view.associatemail.MailBoxAssociateTokenizer;
import com.example.yousheng.imoocxianyu.view.associatemail.MailBoxAssociateView;

/**
 * Created by yousheng on 17/6/3.
 *
 * @function 登陆界面
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    //自定义登陆广播Action
    public static final String LOGIN_ACTION = "com.imooc.action.LOGIN_ACTION";
    /**
     * UI
     */
    private MailBoxAssociateView mUserNameAssociateView;
    private EditText mPasswordView;
    private TextView mLoginView;
    private ImageView mQQLoginView; //用来实现QQ登陆
    /**
     * data
     */
    private PushMessage mPushMessage; // 推送过来的消息
    private boolean fromPush; // 是否从推送到此页面


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);

        initView();
    }

    private void initView() {
        changeStatusBarColor(R.color.white);
        mUserNameAssociateView = (MailBoxAssociateView) findViewById(R.id.associate_email_input);
        mPasswordView = (EditText) findViewById(R.id.login_input_password);
        mLoginView = (TextView) findViewById(R.id.login_button);
        mLoginView.setOnClickListener(this);

        //设置自动补全匹配
        String[] recommendArray = getResources().getStringArray(R.array.recommend_mailbox);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, recommendArray);
        mUserNameAssociateView.setAdapter(adapter);
        mUserNameAssociateView.setTokenizer(new MailBoxAssociateTokenizer());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                loginIn();
                break;
        }
    }

    private void loginIn() {

        String mUser = mUserNameAssociateView.getText().toString().trim();
        String mPassword = mPasswordView.getText().toString().trim();

        if (TextUtils.isEmpty(mUser) || TextUtils.isEmpty(mPassword)) {
            Toast.makeText(this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        //显示进度圈旋转
        DialogManager.getInstnce().showProgressDialog(this);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                DialogManager.getInstnce().dismissProgressDialog();
//            }
//        }).start();
        //发送登陆的网络请求
        RequestCenter.loginIn(mUser, mPassword, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                //关闭进度圈
                DialogManager.getInstnce().dismissProgressDialog();

                //储存用户信息
                User mUser = (User) responseObj;
                UserManager.getInstance().setUser(mUser);

                //发送本地广播
                sendLoginBroadcast();
                LogUtils.d(TAG,"loginIn()-->"+mUser.data.name);
                Toast.makeText(LoginActivity.this,"欢迎回来！ "+mUser.data.name,Toast.LENGTH_SHORT);

                finish();
            }

            @Override
            public void onFailure(Object reasonObj) {
                Toast.makeText(LoginActivity.this, "登陆失败！", Toast.LENGTH_SHORT);
                //关闭进度圈
                DialogManager.getInstnce().dismissProgressDialog();
            }
        });


    }

    /**
     * 发送登陆成功的本地广播，通知许多页面进行刷新
     */
    private void sendLoginBroadcast() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(LOGIN_ACTION));
    }
}
