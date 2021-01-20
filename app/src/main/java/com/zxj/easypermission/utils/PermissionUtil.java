package com.zxj.easypermission.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zxj.easypermission.annotation.PermissionDenied;
import com.zxj.easypermission.annotation.PermissionFailed;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class PermissionUtil {

    private static HashMap<String, Class<? extends ISetting>> permissionMenu = new HashMap<>();

    private static final String MANUFACTURER_DEFAULT = "Default";//默认

    public static final String MANUFACTURER_HUAWEI = "huawei";//华为
    public static final String MANUFACTURER_MEIZU = "meizu";//魅族
    public static final String MANUFACTURER_XIAOMI = "xiaomi";//小米
    public static final String MANUFACTURER_SONY = "sony";//索尼
    public static final String MANUFACTURER_OPPO = "oppo";
    public static final String MANUFACTURER_LG = "lg";
    public static final String MANUFACTURER_VIVO = "vivo";
    public static final String MANUFACTURER_SAMSUNG = "samsung";//三星
    public static final String MANUFACTURER_LETV = "letv";//乐视
    public static final String MANUFACTURER_ZTE = "zte";//中兴
    public static final String MANUFACTURER_YULONG = "yulong";//酷派
    public static final String MANUFACTURER_LENOVO = "lenovo";//联想

    static {
        permissionMenu.put(MANUFACTURER_DEFAULT, DefaultStartSettings.class);
        permissionMenu.put(MANUFACTURER_OPPO, OPPOStartSettings.class);
        permissionMenu.put(MANUFACTURER_VIVO, VIVOStartSettings.class);
    }


    /**
     * 判断所有权限是否都给了，如果有一个权限没给，就返回false
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermissionRequest(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 判断是否全部真正的成功
     *
     * @param gantedResult
     * @return
     */
    public static boolean requestPermissionSuccess(int... gantedResult) {
        if (gantedResult == null || gantedResult.length <= 0) {
            return false;
        }

        for (int permissionValue : gantedResult) {
            if (permissionValue != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    /**
     * 用户是否拒绝了并且点击了不再提示
     *
     * @return
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 专门去执行被注解了的方法
     *
     * @param object
     * @param annotationClass
     */
    public static void invokeAnnotation(Object object, Class annotationClass, int requestCode) {
        //获取object的class对象
        Class<?> aClass = object.getClass();

        //获取object的所有方法
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            //让虚拟机不去检测 private
            method.setAccessible(true);

            //判断方法是否有被annotationClass注解过
            boolean annotationPresent = method.isAnnotationPresent(annotationClass);
            if (annotationPresent) {
                try {
                    Annotation annotation = method.getAnnotation(annotationClass);
                    //请求码和处理请求的接收码一致才会调用函数，否则不调用
                    if (annotation instanceof PermissionDenied) {
                        PermissionDenied permissionDenied = (PermissionDenied) annotation;
                        if (permissionDenied.requestCode() == requestCode) {
                            method.invoke(object);
                        }
                    } else if (annotation instanceof PermissionFailed) {
                        PermissionFailed permissionFailed = (PermissionFailed) annotation;
                        if (permissionFailed.requestCode() == requestCode) {
                            method.invoke(object);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // TODO 专门去 跳转到 设置界面
    public static void startAndroidSettings(Context context) {
        // 拿到当前手机品牌制造商，来获取 具体细节
        Class aClass = permissionMenu.get(Build.MANUFACTURER.toLowerCase());

        if (aClass == null) {
            aClass = permissionMenu.get(MANUFACTURER_DEFAULT);
        }

        try {
            Object newInstance = aClass.newInstance(); // new OPPOStartSettings()

            ISetting iMenu = (ISetting) newInstance; // ISetting iMenu = (ISetting) oPPOStartSettings;

            // 高层 面向抽象，而不是具体细节
            Intent startActivityIntent = iMenu.getStartSettingsIntent(context);

            if (startActivityIntent != null) {
                context.startActivity(startActivityIntent);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}
