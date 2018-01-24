package com.joting.exercise.drysister.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by zhangguohuang on 2018/1/24.
 */

public class MemoryCacheHelper {

    private static final String TAG = "MemoryCacheHelper";

    private Context mContext;
    private LruCache<String,Bitmap> mMemoryCache;

    public MemoryCacheHelper(Context mContext){
        this.mContext = mContext;
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);//应用最大内存
        int cacheSize = maxMemory / 8;//缓存大小
        mMemoryCache = new LruCache<String ,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key,Bitmap bitmap){
                return bitmap.getRowBytes() * bitmap.getHeight()/1024;
            }
        };
    }

    /** 获取LruCache对象 */
    public LruCache<String,Bitmap> getmMemoryCache(){
        return mMemoryCache;
    }
    /** 根据key取出LruCache中的Bitmap*/
    public Bitmap getBitmapFromMemoryCache(String key){
        Log.v(TAG,"加载内存缓存中的图片。");
        return mMemoryCache.get(key);
    }
    /** 按照key往LruCache里写Bitmap */
    public void addBitmapToMemoryCache(String key,Bitmap bitmap){
        if (getBitmapFromMemoryCache(key) == null){
            Log.v(TAG,"addBitmapToMemoryCache");
            mMemoryCache.put(key,bitmap);
        }
    }
}
