package com.rCode.tools;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * 申请权限工具类
 *      https://www.jianshu.com/p/13140041eeab
 */
public class PermissionUtils {

    private OnPermissionCallback onPermissionCallback;
    private int requestCode;
    private Activity activity;
    private String[] permissions;

    public PermissionUtils(Activity activity,int requestCode,String ... permissions) {
        this.activity = activity;
        this.requestCode = requestCode;
        this.permissions = permissions;
    }

    /**
     * 设置授权的权限
     * @param onPermissionCallback 授权的权限
     * @return
     */
    public PermissionUtils checkPermission(OnPermissionCallback onPermissionCallback) {
        this.onPermissionCallback = onPermissionCallback;
        check();
        return this;
    }

    /**
     * 检查是否已授权，如果未授权，则发起授权
     */
    private void check(){
        if (isPermission() == false){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (int i = 0; i < permissions.length; i++) {
                    if (activity.checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) { //未授权
                        activity.requestPermissions(permissions, requestCode);  //申请权限
                        return;
                    }
                }
            }
        }
        if (onPermissionCallback != null) {
            onPermissionCallback.onFinish(requestCode,true);
        }
    }

    /**
     * 是否已授权
     * @return
     */
    public boolean isPermission(){
        return isPermission(activity,permissions);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (onPermissionCallback == null) {
            return;
        }
        if (this.requestCode == requestCode){
            boolean isGrantedAll = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    onPermissionCallback.onGranted(requestCode,permissions[i]);
                }else {
                    isGrantedAll = false;
                    onPermissionCallback.onDenied(requestCode,permissions[i]);
                }
            }
            onPermissionCallback.onFinish(requestCode,isGrantedAll);
        }
    }

    /**
     * 是否授权permissions中的权限
     * @param activity
     * @param permissions
     * @return true 已授权所有，false 最少一个未授权
     */
    public static boolean isPermission(Activity activity,String ... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                if (activity.checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) { //未授权
                    return false;
                }
            }
        }
        return true;
    }

    public interface OnPermissionCallback{

        /**
         * 已授权的权限
         * @param requestCode
         * @param permission
         */
        void onGranted(int requestCode, String permission);

        /**
         * 未授权的权限
         * @param requestCode
         * @param permission
         */
        void onDenied(int requestCode, String permission);

        /**
         * 检查完成是否已授权所有
         * @param requestCode
         * @param grantedAll false 没有授权所有，true 已授权所有
         */
        void onFinish(int requestCode, boolean grantedAll);
    }
}
