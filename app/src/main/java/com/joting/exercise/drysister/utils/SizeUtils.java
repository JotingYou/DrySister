package com.joting.exercise.drysister.utils;

import android.content.Context;

/**
 * Created by zhangguohuang on 2018/1/24.
 */

public class SizeUtils {
    /**dp to px*/
    public static int dp2px(Context context,float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }
    /**px to dp*/
    public static int px2dp(Context context,float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
}
