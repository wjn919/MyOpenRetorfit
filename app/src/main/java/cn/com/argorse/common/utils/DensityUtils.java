/*
 * ProjectName:   AND-PinWeiCn
 * FileName:      DensityUtils.java
 * Description:
 * Create at:	2014-9-29 下午2:14:08
 * @author:		roc.hu(roc.hu@msn.com)
 * @version:	V1.0.0
 * Copyright:   Copyright (c) 2013 Beijing Argorse Technology Development Co., LTD
 * Website：	http://www.argorse.com.cn 
 */
package cn.com.argorse.common.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Description:	系统屏幕的一些操作
 * @author        roc.hu
 * @version       1.0
 *
 * Modification History:
 * DateTime			-*#*-[Author]-*-[Version]	###	Description
 * ------------------------------------------------------------------
 * 2014-9-29 下午2:14:08	-*#*-[roc.hu]-*-[1.0]		###	des
 */
public final class DensityUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     */
    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取dialog宽度
     */
    public static int getDialogW(Activity mActivity) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = mActivity.getResources().getDisplayMetrics();
        int w = dm.widthPixels - 100;
        // int w = aty.getWindowManager().getDefaultDisplay().getWidth() - 100;
        return w;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenW(Activity mActivity) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = mActivity.getResources().getDisplayMetrics();
        int w = dm.widthPixels;
        // int w = aty.getWindowManager().getDefaultDisplay().getWidth();
        return w;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenH(Activity mActivity) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = mActivity.getResources().getDisplayMetrics();
        int h = dm.heightPixels;
        // int h = aty.getWindowManager().getDefaultDisplay().getHeight();
        return h;
    }
    
    /**
     * 获取屏幕密度 比
     */
    public static float getDensity(Activity mActivity) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = mActivity.getResources().getDisplayMetrics();
        float density = dm.density;
        return density;
    }
    /**
     * 获取屏幕密度
     */
    public static int getDensityDpi(Activity mActivity) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = mActivity.getResources().getDisplayMetrics();
        int density = dm.densityDpi;
        return density;
    }
 }
