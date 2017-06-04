package com.example.yousheng.imoocxianyu.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.yousheng.imoocxianyu.R;

/**
 * Created by yousheng on 17/6/4.
 */

public class LoadingDialog extends ProgressDialog {
    private Context mContext;
    ImageView imageView;

    public LoadingDialog(Context context) {
        super(context,R.style.LoadingDialog);
        mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_loading_layout);
        imageView = (ImageView) findViewById(R.id.loading_view);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();


        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
