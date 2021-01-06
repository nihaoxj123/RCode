package com.rcode.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//弹屏，模仿电影评论弹幕
public class PopScreenView extends FrameLayout {

    private int textHeight = 0;

    public PopScreenView(Context context) {
        super(context);
    }

    public PopScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public final synchronized void addView(final View view) {
        if (view == null) return;
        int count = getChildCount();
        int cen = getCent();
        view.setTag(cen);
        view.setTranslationX(getWidth());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0,cen * textHeight,0,0);
        super.addView(view,params);

        if (count==0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(50);
                        if (textHeight == 0) textHeight = view.getHeight();

                        while (getChildCount() > 0){
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<View> list = new ArrayList<>();
                                    for (int i = 0; i < getChildCount(); i++) {
                                        final View v = getChildAt(i);
                                        if (v.getTranslationX() < - v.getWidth()){
                                            list.add(v);
                                            continue;
                                        }
                                        float f = v.getTranslationX() - 2;
                                        v.setTranslationX(f);
                                    }
                                    for (View view1 : list) {
                                        removeView(view1);
                                    }
                                }
                            });
                            Thread.sleep(10);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public  void addText(String text){
        if (TextUtils.isEmpty(text)) return;

        final TextView tv = new TextView(getContext());
        tv.setTextSize(12);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setText(text);

        addView(tv);
    }

    //计算第几次有位置显示
    private int getCent(){
        if (getChildCount() == 0) return 0;
        Map<Integer, List<View>> cenMap = new HashMap<>();
        for (int i =0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            List<View> list = cenMap.get(v.getTag());
            if (list == null){
                list = new ArrayList<>();
                cenMap.put(i,list);
            }
            list.add(v);
        }
        for (int i = 0; i < cenMap.size(); i++) {
            List<View> list = cenMap.get(i);
            if (list == null) continue;
            boolean b = false;//同一行是否还要位置
            for (int i1 = 0; i1 < list.size(); i1++) {
                View v = list.get(i1);
                b = v.getTranslationX() < getWidth() - v.getWidth();
            }
            if (b) return i;
        }
        return cenMap.size();
    }

}