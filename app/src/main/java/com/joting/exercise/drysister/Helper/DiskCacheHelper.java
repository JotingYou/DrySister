package com.joting.exercise.drysister.Helper;

import android.content.Context;

import com.joting.exercise.drysister.Sister;
import com.joting.exercise.drysister.SisterCompress;

/**
 * Created by zhangguohuang on 2018/1/24.
 */

public class DiskCacheHelper {
    private static final String TAG = "DiskCacheHelper";
    private static final long DISK_CACHE_SIZE = 1024*1024*50;//磁盘缓存大小
    private static final int DISK_CACHE_INDEX = 0;

    private Context mContext;
    private DiskLruCache mDiskLruCache;
    private SisterCompress mCompress;
    private boolean mIsDiskLruCacheCreated = false;//磁盘缓存是否创建
}
