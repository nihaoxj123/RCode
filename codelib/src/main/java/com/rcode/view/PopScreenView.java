package com.rCode.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.rCode.R;

import java.util.HashMap;
import java.util.Set;

public class PopScreenView extends ViewGroup {

    private boolean isCancel;
    private int speed;

    public PopScreenView(Context context) {
        super(context);
    }

    public PopScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);

        obtainStyledAttributes(context,attrs);
    }

    private void obtainStyledAttributes(Context context, AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PopScreenView);
        speed = ta.getInt(R.styleable.PopScreenView_speed,35);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            measureChild(view,widthMeasureSpec,heightMeasureSpec);
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            if (params.isInit == false){
                params.width = view.getMeasuredWidth();
                params.height = view.getMeasuredHeight();
                params.top = getMinTop();
                params.left = getWidth();
                if (params.getMaxTop() > getHeight()){
                    continue;
                }
                params.isInit = true;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

//    @Override
//    public void addView(View child) {
//        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.top = getMinTop();
//        if (params.top >= getHeight()){
//            return;
//        }
//        addView(child,params);
//    }

    private int getMinTop(){
        HashMap<Integer,Integer> map = new HashMap<>();
        int maxTop = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            LayoutParams params = (LayoutParams) getChildAt(i).getLayoutParams();
            if (params.isInit){
                maxTop = Math.max(maxTop,params.getMaxTop());
                Integer l = map.get(params.top);
                if (l == null){
                    map.put(params.top,params.getMaxLeft());
                }else {
                    map.put(params.top,Math.max(l,params.getMaxLeft()));
                }
            }
        }

        boolean notFind = true;
        int minTop = 999999;
        Set<Integer> top = map.keySet();
        for (Integer key : top) {
            Integer maxLeft = map.get(key);
            if (maxLeft < getWidth()){
                minTop = Math.min(minTop,key);
                notFind = false;
            }
        }
        if (notFind){
            return maxTop;
        }
        return minTop;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        myLayout();
    }

    private void myLayout(){
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View v = getChildAt(i);
            if (v != null){
                LayoutParams params = (LayoutParams) v.getLayoutParams();
                if (params.isInit){
                    v.layout(params.left,params.top,params.getMaxLeft(),params.getMaxTop());
                    if (params.left <= -params.width){
                        removeView(v);
                    }
                    //刷新之前没有位置显示的view
                    if (params.getMaxLeft() < getWidth()){
                        for (int j = 0; j < count; j++) {
                            View v1 = getChildAt(j);
                            if (v1 != null){
                                LayoutParams p = (LayoutParams) v1.getLayoutParams();
                                if (p.isInit == false){
                                    requestLayout();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void start(){
        if (isCancel){
            return;
        }

        isCancel = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isCancel){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            int count = getChildCount();
                            isCancel = count > 0;
                            for (int i = 0; i < count; i++) {
                                View v = getChildAt(i);
                                if (v !=null){
                                    LayoutParams params = (LayoutParams) v.getLayoutParams();
                                    params.left -= 2;
                                }
                            }
                            myLayout();
                        }
                    });

                    try {
                        Thread.sleep(speed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stop(){
        isCancel = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return (LayoutParams) super.generateLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(),attrs);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        int top;
        int left;
        boolean isInit;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public int getMaxLeft(){
            return left + width;
        }

        public int getMaxTop(){
            return top + height;
        }
    }
}
