package com.rcode.listener;

import android.view.View;

/**
 * 防止用户恶意疯狂点击
 * https://www.jianshu.com/p/ab7de1159f60
 */
public abstract class OnClickListener implements View.OnClickListener {

    private long delay;  //让用户延时多次时间点击，单位毫秒
    private long time;  //上次点击时间

    public OnClickListener() {
        delay = 500;
    }

    public OnClickListener(long delay) {
        this.delay = delay;
    }

    @Override
    public final void onClick(View v) {
        long nowTime = System.currentTimeMillis();
        if (nowTime >= time + delay){
            click(v);
            time = nowTime;
        }
    }

    public abstract void click(View v);
}