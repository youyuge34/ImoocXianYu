package com.example.yousheng.imoocsdk.okhttp.exception;

/**********************************************************
 * @文件名称：LogicException.java
 * @文件作者：renzhiqiang
 * @创建时间：2015年8月19日 上午10:05:08
 * @文件描述：自定义异常类,返回ecode,emsg到业务层
 * @修改历史：2015年8月19日创建初始版本
 **********************************************************/
public class OkHttpException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * the server return code
     * 最重要的异常码
     */
    private int ecode;

    /**
     * the server return error message
     * 防止应用层需要
     */
    private Object emsg;

    public OkHttpException(int ecode, Object emsg) {
        this.ecode = ecode;
        this.emsg = emsg;
    }

    public int getEcode() {
        return ecode;
    }

    public Object getEmsg() {
        return emsg;
    }
}