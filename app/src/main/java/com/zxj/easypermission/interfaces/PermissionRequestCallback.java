package com.zxj.easypermission.interfaces;

public interface PermissionRequestCallback {

    /**
     * 申请权限成功
     */
    void permissionSuccess();

    /**
     * 申请权限失败，用户点击了拒绝
     */
    void permissionCanceled();

    /**
     * 申请权限失败，用户点击了不再询问
     */
    void permissionDenied();
}
