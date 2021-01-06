package com.rcode.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

/**
 * 视图翻转器
 *
 * 注意：建议不要在RecyclerView使用，如果非要使用请调用setAutoCancel(false); 再调试关闭Activity 此类的updateView方法是否还会执行
 */
public class SmartViewFlipper extends FrameLayout {

    private long delayed = 3000; //每隔多少毫秒滑动一次
    private long slidingTime = 1000; //滑动时间
    private boolean isCancel; //是否停止动画
    private boolean isAutoCancel = true; //控件销毁是否自动取消动画
    private int direction = DIRECTION_TOP; //方向 1 上 2 下 3 左 3 右
    private Adapter adapter;
    private ViewHolder vh1,vh2;
    private boolean isStart;  //是否在启动

    //滑动方向常量
    public static final int DIRECTION_TOP = 1;
    public static final int DIRECTION_BOTTOM = 2;
    public static final int DIRECTION_LEFT = 3;
    public static final int DIRECTION_RIGHT = 4;

    public SmartViewFlipper(Context context) {
        super(context);
    }

    public SmartViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //每隔多少毫秒滑动一次
    public void setDelayed(long delayed) {
        if (delayed > 0)
            this.delayed = delayed;
    }

    //滑动时间
    public void setSlidingTime(long slidingTime) {
        this.slidingTime = slidingTime;
    }

//    //设置背景透明度
//    public void setBackgroundAlpha(int alpha) {
//        if (getBackground() != null) {
//            if (alpha > 255) {
//                alpha = 255;
//            } else if (alpha < 0) {
//                alpha = 0;
//            }
//            getBackground().mutate().setAlpha(alpha);
//        }
//    }
//
//    @Override
//    public void setBackground(Drawable background) {
//        super.setBackground(background);
//        setBackgroundAlpha(175);
//    }

    //设置滚动方向 1 上 2 下 3 左 3 右
    public void setDirection(int direction) {
        if (direction > 4 || direction < 1) {
            return;
        }
        this.direction = direction;
    }

    //设置适配器
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        removeAllViews();

        vh1 = adapter.createView(getContext());
        vh2 = adapter.createView(getContext());

        vh1.itemView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        vh2.itemView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        addView(vh1.itemView);
        addView(vh2.itemView);
    }

//    public void setTextAdapter(List<String> data){
//        setAdapter(new TextAdapter(data));
//    }

    //控件销毁是否自动取消动画
    public void setAutoCancel(boolean autoCancel) {
        this.isAutoCancel = autoCancel;
    }

    //停止动画
    public void cancel() {
        isCancel = true;
        isStart = false;
    }

    //启动
    public void start() {
        if (adapter == null)  return;

        if (isStart){
            adapter.refresh();
            return;
        }
        isStart = true;
        isCancel = false;

        adapter.updateDataE(vh1, adapter.getData());
        adapter.updateDataE(vh2, adapter.getData());
        updateView();
    }

    //是否在启动
    public boolean isStart() {
        return isStart;
    }

    private void updateView() {
        if (isCancel)  return;

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isCancel)  return;

                adapter.addIndex();
                switch (direction) {
                    case DIRECTION_BOTTOM:
                        bottomAnimator();
                        break;
                    case DIRECTION_LEFT:
                        leftAnimator();
                        break;
                    case DIRECTION_RIGHT:
                        rightAnimator();
                        break;
                    default:
                        topAnimator();
                        break;
                }

            }
        }, delayed);
    }

    private void topAnimator() {
        ObjectAnimator animator = translationY(vh1.itemView, slidingTime, 0, -vh1.itemView.getHeight());
        translationY(vh2.itemView, slidingTime, vh1.itemView.getHeight(), 0);
        adapter.updateDataE(vh2, adapter.getData());

        animator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationEnd(Animator animation) {
                adapter.updateDataE(vh1, adapter.getData());
                vh1.itemView.setTranslationY(0);
                updateView();
            }
        });
    }

    private void bottomAnimator() {
        ObjectAnimator animator = translationY(vh1.itemView, slidingTime, 0, vh1.itemView.getHeight());
        translationY(vh2.itemView, slidingTime, -vh1.itemView.getHeight(), 0);
        adapter.updateDataE(vh2, adapter.getData());

        animator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationEnd(Animator animation) {
                adapter.updateDataE(vh1, adapter.getData());
                vh1.itemView.setTranslationY(0);
                updateView();
            }
        });
    }

    private void leftAnimator() {
        ObjectAnimator animator = translationX(vh1.itemView, slidingTime, 0, -getWidth());
        translationX(vh2.itemView, slidingTime, getWidth(), 0);
        adapter.updateDataE(vh2, adapter.getData());

        animator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationEnd(Animator animation) {
                adapter.updateDataE(vh1, adapter.getData());
                vh1.itemView.setTranslationX(0);
                updateView();
            }
        });
    }

    private void rightAnimator() {
        vh2.itemView.setVisibility(INVISIBLE);
        adapter.updateDataE(vh2, adapter.getData());
        vh2.itemView.post(new Runnable() {
            @Override
            public void run() {
                vh2.itemView.setVisibility(VISIBLE);
                ObjectAnimator animator = translationX(vh1.itemView, slidingTime, 0, getWidth());
                translationX(vh2.itemView, slidingTime, -getWidth(), 0);

                animator.addListener(new AnimatorListener() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        adapter.updateDataE(vh1, adapter.getData());
                        vh1.itemView.setTranslationX(0);
                        updateView();
                    }
                });
            }
        });
    }

    private ObjectAnimator translationY(View view, long duration, float... values) {
        ObjectAnimator translationYAni = ObjectAnimator.ofFloat(view, "translationY", values);
        translationYAni.setDuration(duration);
        translationYAni.start();
        return translationYAni;
    }
    private ObjectAnimator translationX(View view, long duration, float... values) {
        ObjectAnimator translationYAni = ObjectAnimator.ofFloat(view, "translationX", values);
        translationYAni.setDuration(duration);
        translationYAni.start();
        return translationYAni;
    }

//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//
//        float roundSize = getMeasuredWidth() /2;
//        //切圆角
//        Path path = new Path();
//        path.addRoundRect(new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight()), roundSize, roundSize, Path.Direction.CW);
//        canvas.clipPath(path, Region.Op.REPLACE);
//        super.dispatchDraw(canvas);
//    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isAutoCancel) {
            cancel();
        }
    }

    public abstract static class Adapter<DATA, VH extends ViewHolder> {

        private List<DATA> data;
        private int index = 0;

        public Adapter(List<DATA> data) {
            this.data = data;
        }
        public Adapter() {

        }

        public void refresh() {
            index = 0;
        }

        public void addIndex() {
            index++;
            if (index >= getSize()) {
                index = 0;
            }
        }

        public void setData(List<DATA> data) {
            this.data = data;
        }

        public DATA getData() {
            if (data == null) {
                return null;
            }
            return data.get(index);
        }

        public int getSelectIndex() {
            return index;
        }

        public int getSize() {
            return data == null ? 0 : data.size();
        }

        //修改控件数据
        public abstract void updateData(VH vh, DATA data);

        public final void updateDataE(VH vh, DATA data){
            try {
                updateData(vh,data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //创建显示的View
        public abstract VH createView(Context context);

    }

    public static class ViewHolder {
        View itemView;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new RuntimeException("不能传入一个null的view");
            }
            this.itemView = itemView;
        }

        public <T extends View> T findViewById(int id) {
            return itemView.findViewById(id);
        }

    }

//    public static class TextAdapter extends Adapter<String, TextAdapter.VH> {
//
//        private String textColor = "#FFFFFF";
//        private int textSize = 14;
//
//        public TextAdapter(List<String> strings) {
//            super(strings);
//        }
//
//        public TextAdapter(List<String> strings, String textColor) {
//            super(strings);
//            this.textColor = textColor;
//        }
//
//        public TextAdapter(List<String> strings, int textSize) {
//            super(strings);
//            this.textSize = textSize;
//        }
//
//        public TextAdapter(List<String> strings, String textColor, int textSize) {
//            super(strings);
//            this.textColor = textColor;
//            this.textSize = textSize;
//        }
//
//        @Override
//        public void updateData(VH vh, String s) {
//            TextView tv = (TextView) vh.itemView;
//            tv.setText(s);
//        }
//
//        @Override
//        public VH createView(Context context) {
//            TextView tv = new TextView(context);
//            tv.setTextColor(Color.parseColor(textColor));
//            tv.setGravity(Gravity.CENTER);
//            tv.setTextSize(textSize);
//            tv.setMaxLines(1);
//            VH vh = new VH(tv);
//            return vh;
//        }
//
//        class VH extends ViewHolder {
//            public VH(View itemView) {
//                super(itemView);
//            }
//        }
//    }

    private abstract class AnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}