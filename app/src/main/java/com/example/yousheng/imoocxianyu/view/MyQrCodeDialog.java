package com.example.yousheng.imoocxianyu.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yousheng.imoocsdk.imageloader.ImageLoaderManger;
import com.example.yousheng.imoocsdk.util.Utils;
import com.example.yousheng.imoocxianyu.R;
import com.example.yousheng.imoocxianyu.manager.UserManager;
import com.example.yousheng.imoocxianyu.util.Util;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by renzhiqiang on 16/8/19.
 */
public class MyQrCodeDialog extends Dialog {

    private Context mContext;

    /**
     * UI
     */
    private ImageView mQrCodeView;
    private CircleImageView mPhotoView;
    private TextView mTickView;
    private TextView mCloseView;

    public MyQrCodeDialog(Context context) {
        super(context,R.style.dialog_no_title);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_mycode_layout);
        initView();
    }

    private void initView() {

        mQrCodeView = (ImageView) findViewById(R.id.qrcode_view);
        mPhotoView = (CircleImageView) findViewById(R.id.photo_view);
        mTickView = (TextView) findViewById(R.id.tick_view);
        mCloseView = (TextView) findViewById(R.id.close_view);


        //给关闭按钮设置动画
        RotateAnimation rotateAnimation = new RotateAnimation(0,90f, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setStartOffset(500);
        rotateAnimation.setDuration(600);
        rotateAnimation.setInterpolator(mContext,android.R.anim.bounce_interpolator);
//        rotateAnimation.setRepeatCount(2);
        rotateAnimation.setFillAfter(false);
//        rotateAnimation.setRepeatMode(Animation.REVERSE);
        mCloseView.startAnimation(rotateAnimation);

        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

//        Window window = getWindow();
//        window.setWindowAnimations(R.style.Anim_Dialog1);


        String name = UserManager.getInstance().getUser().data.name;
        String photoUrl = UserManager.getInstance().getUser().data.photoUrl;
        mQrCodeView.setImageBitmap(Util.createQRCode(
            Utils.dip2px(mContext, 200),
            Utils.dip2px(mContext, 200),
            name));
        mTickView.setText(name + mContext.getString(R.string.personal_info));
        ImageLoaderManger.getInstance(mContext).displayImage(mPhotoView,photoUrl);
    }
}
