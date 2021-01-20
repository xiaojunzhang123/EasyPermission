package com.zxj.easypermission.aspect;

import android.content.Context;
import android.util.Log;


import androidx.fragment.app.Fragment;

import com.zxj.easypermission.activity.ApplyPermissionActivity;
import com.zxj.easypermission.annotation.Permission;
import com.zxj.easypermission.annotation.PermissionDenied;
import com.zxj.easypermission.annotation.PermissionFailed;
import com.zxj.easypermission.interfaces.PermissionRequestCallback;
import com.zxj.easypermission.utils.PermissionUtil;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class PermissionAspect {

    /**
     * 声明切入点是所有的Permission注解标记了的方法
     * @param permissions
     */
    @Pointcut("execution(@com.zxj.easypermission.annotation.Permission * *(..)) && @annotation(permissions)")
    public void getPermission(Permission permissions){}


    /**
     * 根据切入点注入代码
     * @param proceedingJoinPoint
     * @param permissions
     */
    @Around("getPermission(permissions)")
    public void getPermissionPoint(ProceedingJoinPoint proceedingJoinPoint,Permission permissions){
        //获取上下文
        Context context = null;
        //获取到注解所在的类的对象
        final Object aThis = proceedingJoinPoint.getThis();
        //判断aThis是否是Context的子类，如果是就进行赋值
        if (aThis instanceof Context){
            context =(Context) aThis;
        }else  if (aThis instanceof Fragment){
            context = ((Fragment) aThis).getActivity();
        }

        //如果不是在Context的子类，或者是在Context的子类但是申请的权限没有或者有但是长度为0就返回不处理
        if (context == null || permissions == null || permissions.value() == null || permissions.value().length == 0){
            return;
        }

        //获取到注解携带的权限数据
        String[] value = permissions.value();
        int requestCode = permissions.requestCode();
        //由于权限的回调的方法只有Activity才有，因此我们创建一个透明的Activity来申请权限
        ApplyPermissionActivity.launchActivity(context, value, requestCode, new PermissionRequestCallback() {
            @Override
            public void permissionSuccess() {
                Log.e("ZXJ-------->","权限申请结果：成功");
              //权限获取成功，继续执行
                try {
                    proceedingJoinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void permissionCanceled() {
                Log.e("ZXJ-------->","权限申请结果：取消");
                PermissionUtil.invokeAnnotation(aThis, PermissionFailed.class,requestCode);
            }

            @Override
            public void permissionDenied() {
                Log.e("ZXJ-------->","权限申请结果：被永久拒绝");
                PermissionUtil.invokeAnnotation(aThis, PermissionDenied.class,requestCode);
            }
        });
    }
}
