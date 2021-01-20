package com.zxj.easypermission;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.zxj.easypermission.annotation.Permission;
import com.zxj.easypermission.annotation.PermissionDenied;
import com.zxj.easypermission.annotation.PermissionFailed;
import com.zxj.easypermission.utils.PermissionUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSdRead();
            }
        });

        findViewById(R.id.btn_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSdWrite();
            }
        });

        findViewById(R.id.btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri packageURI = Uri.parse("package:" + "com.zxj.easypermission");
                Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
                startActivity(intent);
            }
        });
    }


    @Permission(value = "android.permission.READ_EXTERNAL_STORAGE",requestCode = 1)
    public void getSdRead(){
        Log.e("ZXJ-------->","getSdRead");
    }

    @Permission(value = "android.permission.CAMERA",requestCode = 2)
    public void getSdWrite(){
        Log.e("ZXJ-------->","getSdWrite");
    }

    @PermissionFailed(requestCode = 1)
    private void requestReadPermissionFailed() {
        Toast.makeText(this, "用户拒绝了读读读权限", Toast.LENGTH_SHORT).show();
        Log.e("ZXJ-------->","getSdRead requestReadPermissionFailed");
    }

    @PermissionDenied(requestCode = 1)
    private void requestReadPermissionDenied() {
        Toast.makeText(this, "权限申请读读读失败,不再询问", Toast.LENGTH_SHORT).show();
        //开发者可以根据自己的需求看是否需要跳转到设置页面去
        PermissionUtil.startAndroidSettings(this);
        Log.e("ZXJ-------->","getSdRead requestReadPermissionDenied");
    }


    @PermissionFailed(requestCode = 2)
    private void requestWritePermissionFailed() {
        Toast.makeText(this, "用户拒绝了写写权限", Toast.LENGTH_SHORT).show();
        Log.e("ZXJ-------->","getSdWrite  requestWritePermissionFailed");
    }

    @PermissionDenied(requestCode = 2)
    private void requestWritePermissionDenied() {
        Toast.makeText(this, "权限申请写写失败,不再询问", Toast.LENGTH_SHORT).show();
        //开发者可以根据自己的需求看是否需要跳转到设置页面去
        PermissionUtil.startAndroidSettings(this);
        Log.e("ZXJ-------->","getSdWrite  requestWritePermissionDenied");
    }
}