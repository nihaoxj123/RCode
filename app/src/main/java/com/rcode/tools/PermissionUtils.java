package com.rcode.tools;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * 申请权限工具类
 *      通过调用getInstance()方法获取实例，传OnPermissionCallback的接口实现类，
 *          void onGranted(int requestCode, String permission) 已授权的权限，通过setPermissions的第三个参数传入的权限遍历，每遍历一次判断一次，如果遍历某个权限已授权的，则回调这个方法
 *          void onDenied(int requestCode, String permission) 未授权的权限，通过setPermissions的第三个参数传入的权限遍历，每遍历一次判断一次，如果遍历某个权限未授权的，则回调这个方法
 *          void onFinish(int requestCode, boolean grantedAll) 遍历permissions完成，permissions中的所有权限都授权，grantedAll = trues 否则 = false
 *      使用方法
 *          在onCreate中调用
 *              PermissionUtils permission = PermissionUtils.getInstance().setPermissions;
 *          在onRequestPermissionsResult中调用
 *              permission.onRequestPermissionsResult
 */
public class PermissionUtils {

    private OnPermissionCallback onPermissionCallback;
    private int requestCode;
    private Activity activity;
    private String[] permissions;


    /**
     * 获取实例
     * @param onPermissionCallback
     * @return
     */
    public static PermissionUtils getInstance(Activity activity,OnPermissionCallback onPermissionCallback) {
        return new PermissionUtils(activity,onPermissionCallback);
    }

    private PermissionUtils(Activity activity,OnPermissionCallback onPermissionCallback) {
        if (onPermissionCallback == null) throw new RuntimeException("onPermissionCallback 不能为 null");
        this.activity = activity;
        this.onPermissionCallback = onPermissionCallback;
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

    /**
     * 设置授权的权限
     * @param requestCode
     * @param permissions 授权的权限
     * @return
     */
    public PermissionUtils setPermissions(int requestCode,String ... permissions) {
        if (this.permissions != null){
            return this;
        }
        this.requestCode = requestCode;
        this.permissions = permissions;
        checkPermission();
        return this;
    }

    /**
     * 发起申请permissions中的权限
     * @return
     */
    public void permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                if (activity.checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) { //未授权
                    activity.requestPermissions(permissions, requestCode);  //申请权限
                    return;
                }
            }
        }
        onPermissionCallback.onFinish(requestCode,true);
    }

    /**
     * 检查是否已授权，如果未授权，则发起授权
     */
    private void checkPermission(){
        if (isPermission() == false){
            permission();
        }else {
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
