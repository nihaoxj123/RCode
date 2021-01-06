package com.rcode.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import java.util.ArrayList;

/**
 * 防止重复弹出多个对话框，当先弹出的对话框关闭后，再弹出下一个对话框
 * https://www.jianshu.com/p/844e57dc891b
 */
public class NoRepeatDialog extends AlertDialog implements DialogInterface.OnDismissListener {

    private ArrayList<OnDialogListener> dialogList;
    private int layoutId;
    private Activity activity;
    private int showNextDialogDelay;

    public NoRepeatDialog(Activity activity) {
        super(activity, com.rcode.R.style.dialog);
        this.activity = activity;
        dialogList = new ArrayList<>();
        super.setOnDismissListener(this);
    }

    /**
     * 下个对话框延时多长时间再显示
     * @param showNextDialogDelay 时长（毫秒）
     */
    public void setShowNextDialogDelay(int showNextDialogDelay) {
        this.showNextDialogDelay = showNextDialogDelay;
    }

    /**
     * 显示对话框，强烈推荐使用此方法，不建议使用其他方法显示对话框
     * @param layoutId 布局
     * @param listener 监听（对话框：显示、关闭）
     */
    public final void show(int layoutId, OnDialogListener listener) {
        if (listener == null) {
            listener = new OnDialogListener() {
                @Override
                public void onClose() {}
                @Override
                public void onShow() {}
            };
        }
        if (dialogList.contains(listener))
            throw new RuntimeException("每个layoutId对应一个listener对象，一个listener对象不能使用多次");

        listener.layoutId = layoutId;
        listener.dialog = this;

        if (!isShowing()) {
            show();
            setContentView(layoutId);
            listener.onShow();
        }

        dialogList.add(listener);
    }

    @Deprecated
    @Override
    public void onDismiss(DialogInterface dialog) {
        for (int i = 0; i < dialogList.size(); i++) {
            OnDialogListener showDialog = dialogList.get(i);
            if (showDialog.layoutId == layoutId) {
                showDialog.onClose();
                dialogList.remove(showDialog);
                break;
            }
        }

        if (showNextDialogDelay > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(showNextDialogDelay);
                    } catch (InterruptedException e) {}
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showNext();
                        }
                    });
                }
            }).start();
        } else {
            showNext();
        }
    }

    private void showNext() {
        if (activity.isFinishing()) return;

        if (dialogList.size() > 0) {
            show();
            OnDialogListener d = dialogList.get(0);
            setContentView(d.layoutId);
            d.onShow();
        }
    }

    @Deprecated
    @Override
    public void setContentView(int layoutResID) {
        this.layoutId = layoutResID;
        super.setContentView(layoutResID);
    }

    public static abstract class OnDialogListener {
        private int layoutId;
        private AlertDialog dialog;

        public abstract void onClose();

        public abstract void onShow();

        public <T extends View> T findViewById(int id) {
            return dialog.findViewById(id);
        }
    }
}