package com.zxj.easypermission.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zxj.easypermission.interfaces.PermissionRequestCallback;
import com.zxj.easypermission.utils.PermissionUtil;

public class ApplyPermissionActivity extends AppCompatActivity {

    /**
     * 请求的权限
     */
    public static final String REQUEST_PERMISSIONS = "request_permissions";

    /**
     * 请求码
     */
    public static final String REQUEST_CODE = "request_code";
    public static final int REQUEST_CODE_DEFAULT = -1;

    /**
     * 请求的回调
     */
    public static PermissionRequestCallback requestCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            //获取到意图传过来的权限数组
            String[] permissionArr = intent.getStringArrayExtra(REQUEST_PERMISSIONS);
            int request_code = intent.getIntExtra(REQUEST_CODE, REQUEST_CODE_DEFAULT);

            //如果没有请求权限 或者没有请求码，或者没有请求处理的回调都直接关闭透明Activity
            if (permissionArr == null || permissionArr.length == 0 || request_code == -1 || requestCallback == null) {
                this.finish();
                return;
            }

            //判断是否已经授权过了
            if (PermissionUtil.hasPermissionRequest(this, permissionArr)) {
                requestCallback.permissionSuccess();
                this.finish();
                return;
            }

            //开始申请权限
            ActivityCompat.requestPermissions(this, permissionArr, request_code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //再次判断是否权限真正申请成功
        if (PermissionUtil.requestPermissionSuccess(grantResults)) {
            requestCallback.permissionSuccess();
            this.finish();
            return;
        }

        //权限拒绝，并且用户勾选了不在提示
        if (PermissionUtil.shouldShowRequestPermissionRationale(this, permissions)) {
            requestCallback.permissionDenied();
            this.finish();
            return;
        }

        //权限拒绝
        requestCallback.permissionCanceled();
        this.finish();
    }

    public static void launchActivity(Context context, String[] permissions, int requestCode, PermissionRequestCallback callback) {
        requestCallback = callback;
        Bundle bundle = new Bundle();
        bundle.putStringArray(REQUEST_PERMISSIONS, permissions);
        bundle.putInt(REQUEST_CODE, requestCode);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(context, ApplyPermissionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(0, 0);
    }
}