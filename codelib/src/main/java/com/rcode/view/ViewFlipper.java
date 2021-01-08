package com.rCode.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.rCode.R;


public class ViewFlipper extends ViewGroup {

    private View v1;
    private View v2;
    private View bgV;
    private Adapter adapter;
    private float radius;
    private int mType;
    private boolean isCancel;

    public ViewFlipper(Context context) {
        this(context,null);
    }

    public ViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);

        obtainStyledAttributes(context,attrs);
    }

    private void obtainStyledAttributes(Context context, AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, com.rCode.R.styleable.ViewFlipper);
        mType = ta.getInt(com.rCode.R.styleable.ViewFlipper_slideType,1);
        int bgColor = ta.getColor(R.styleable.ViewFlipper_bgColor,0);
        radius = ta.getDimension(R.styleable.ViewFlipper_radius,0);

        if (bgColor != 0){
            bgV = new View(context);
            bgV.setBackgroundColor(bgColor);
        }
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        initView();
    }

    public void initView(){
        if (adapter == null || adapter.getCount() <= 0){
            return;
        }
        removeAllViews();

        if (bgV != null){
            addView(bgV);
        }
        if (adapter.getCount() > 0){
            v1 = adapter.update(this);
            addView(v1);
        }
        if (adapter.getCount() > 1){
            v2 = adapter.update(this);
            addView(v2);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (v1 != null && v2 != null){
            v1.measure(widthMeasureSpec,heightMeasureSpec);
            LayoutParams params1 = (LayoutParams) v1.getLayoutParams();
            params1.width = v1.getMeasuredWidth();
            params1.height = v1.getMeasuredHeight();

            v2.measure(widthMeasureSpec,heightMeasureSpec);
            LayoutParams params2 = (LayoutParams) v2.getLayoutParams();
            params2.width = v2.getMeasuredWidth();
            params2.height = v2.getMeasuredHeight();

            setMeasuredDimension(params1.width, params1.height);
            return;
        }
        super.onMeasure(0,0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (bgV != null){ //画背景
            bgV.layout(0,0,r,b);
        }

        if (v1 != null && v2 != null){
            LayoutParams params1 = (LayoutParams) v1.getLayoutParams();
            v1.layout(0,params1.top,params1.width,params1.getBottom());
        }
    }

    public void start(){
        if (isCancel){
            return;
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isCancel){
                    return;
                }
                if (mType == 1) {
                    changeTop();
                }else if (mType == 2){
                    changeBottom();
                }else if (mType == 3){
                    changeLeft();
                }else if (mType == 4){
                    changeRight();
                }
            }
        },3000);
    }

    public void stop(){
        isCancel = true;
    }

    //改变位置
    public void changeTop(){
        LayoutParams params1 = (LayoutParams) v1.getLayoutParams();
        LayoutParams params2 = (LayoutParams) v2.getLayoutParams();
        params1.top -= 2;
        if (params1.top <= -params1.height){
            params1.top = -params1.height;
            changeFinish();
            v1.layout(0,params1.top,params1.width,params1.getBottom());
            v2.layout(0,params1.getBottom(),params2.width,params1.getBottom() + params2.height);
            return;
        }
        v1.layout(0,params1.top,params1.width,params1.getBottom());
        v2.layout(0,params1.getBottom(),params2.width,params1.getBottom() + params2.height);

        delay(new Runnable() {
            @Override
            public void run() {
                changeTop();
            }
        });
    }

    //改变位置
    public void changeBottom(){
        LayoutParams params1 = (LayoutParams) v1.getLayoutParams();
        LayoutParams params2 = (LayoutParams) v2.getLayoutParams();
        params1.top += 2;
        if (params1.top >= params2.height){
            params1.top = params2.height;
            changeFinish();
            v1.layout(0,params1.top,params1.width,params1.getBottom());
            v2.layout(0,-params2.height+params1.top,params2.width, params1.top);
            return;
        }
        v1.layout(0,params1.top,params1.width,params1.getBottom());
        v2.layout(0,-params2.height+params1.top,params2.width, params1.top);

        delay(new Runnable() {
            @Override
            public void run() {
                changeBottom();
            }
        });
    }

    //改变位置
    public void changeLeft(){
        LayoutParams params1 = (LayoutParams) v1.getLayoutParams();
        LayoutParams params2 = (LayoutParams) v2.getLayoutParams();
        params1.left -= 2;
        if (params1.left <= -params1.width){
            params1.left = -params1.width;
            changeFinish();
            v1.layout(params1.left,0,params1.width,params1.height);
            v2.layout(params1.width+params1.left,0,params2.width, params2.height);
            return;
        }
        v1.layout(params1.left,0,params1.width-params1.left,params1.height);
        v2.layout(params1.width+params1.left,0,params1.width+params1.left+params2.width, params2.height);

        delay(new Runnable() {
            @Override
            public void run() {
                changeLeft();
            }
        });
    }

    //改变位置
    public void changeRight(){
        LayoutParams params1 = (LayoutParams) v1.getLayoutParams();
        LayoutParams params2 = (LayoutParams) v2.getLayoutParams();
        params1.left += 2;
        if (params1.left >= params2.width){
            params1.left = params2.width;
            changeFinish();
            v1.layout(params1.left,0,params1.width,params1.height);
            v2.layout(-(params2.width+params1.left),0,params2.width, params2.height);
            return;
        }
        v1.layout(params1.left,0,params1.width,params1.height);
        v2.layout(-params2.width+params1.left,0,params2.width, params2.height);

        delay(new Runnable() {
            @Override
            public void run() {
                changeRight();
            }
        });
    }

    private void delay(final Runnable runnable){
        if (isCancel){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(35);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(runnable);
            }
        }).start();
    }

    //改变完成 把控件的大小改变当前的子view大小，
    private void changeFinish(){
        if (isCancel){
            return;
        }
        adapter.index --;
        initView();
        start();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (radius > 0){
            //切圆角
            Path path = new Path();
            path.addRoundRect(new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight()), radius, radius, Path.Direction.CW);
            canvas.clipPath(path, Region.Op.REPLACE);
        }
        super.dispatchDraw(canvas);
    }

    //当checkLayoutParams返回false的时候就会执行到这里的generateLayoutParams
    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return (LayoutParams) super.generateLayoutParams(lp);
    }

    //当addView的时候没有设置LayoutParams的话就会默认执行这里的generateDefaultLayoutParams
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    //写在xml中属性的时候就会执行这里的generateLayoutParams
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    public static class LayoutParams extends ViewGroup.LayoutParams{
        int left = 0;
        int top = 0;

        public LayoutParams(int width, int height) {
            super(width,height);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public int getRight(){
            return left + width;
        }

        public int getBottom(){
            return top + height;
        }
    }

    public static abstract class Adapter{
        private int index = 0;
        public abstract View getView(ViewGroup parentView,int position);
        public abstract int getCount();
        private View update(ViewGroup parentView){
            if (index >= getCount()){
                index = 0;
            }
            View view = getView(parentView,index);
            index ++;
            return view;
        }
    }

}
