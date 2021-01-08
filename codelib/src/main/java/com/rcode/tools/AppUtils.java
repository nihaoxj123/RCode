package com.rCode.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

public class AppUtils {

    /**
     *  权限
     *     <!-- 往SDCard 读写入数据权限 -->
     *     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     *     <!--安装apk权限-->
     *     <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
     *
     *     WRITE_EXTERNAL_STORAGE 保存文件用到 7.0及以上 需要动态申请
     *     REQUEST_INSTALL_PACKAGES 安装用到
     *
     * provider
     *          <!--安装apk-->
     *         非Androidx 使用这个 android.support.v4.content.FileProvider"
     *         Androidx 使用这个 androidx.core.content.FileProvider
     *         <provider
     *             android:name="androidx.core.content.FileProvider"
     *             android:authorities="${applicationId}.fileProvider"
     *             android:exported="false"
     *             android:grantUriPermissions="true">
     *             <meta-data
     *                 android:name="android.support.FILE_PROVIDER_PATHS"
     *                 android:resource="@xml/file_paths" />
     *         </provider>
     *
     * file_paths.xml
     *      <?xml version="1.0" encoding="utf-8"?>
     *      <paths>
     *          <external-path path="." name="external_storage_root" />
     *      </paths>
     *
     * 安装apk 7.0及以上都要在AndroidManifest配置provider和安装权限
     * @param context
     * @param apkFile apk文件对象
     */
    public static void install(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// 判断版本大于等于7.0
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            uri = Uri.parse("file://" + apkFile);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
