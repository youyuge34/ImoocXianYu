package com.example.yousheng.imoocxianyu.manager;

import android.content.Context;

import com.example.yousheng.imoocxianyu.view.LoadingDialog;


/**
 * Created by renzhiqiang on 16/8/15.
 */
public class DialogManager {

    private static DialogManager mInstnce = null;
    private LoadingDialog mDialog;

    public static DialogManager getInstnce() {

        if (mInstnce == null) {

            synchronized (DialogManager.class) {

                if (mInstnce == null) {

                    mInstnce = new DialogManager();
                }
            }
        }
        return mInstnce;
    }

    public void showProgressDialog(Context context) {

        if (mDialog == null) {
            mDialog = new LoadingDialog(context);
//            mDialog.setMessage(context.getResources().getString(R.string.please_wait));
            mDialog.setCanceledOnTouchOutside(false);
        }
        if (!mDialog.isShowing()){
            mDialog.show();
        }
    }

    public void dismissProgressDialog() {

        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = null;
    }
}
