package com.joting.exercise.drysister;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.joting.exercise.drysister.Helper.DiskCacheHelper;
import com.joting.exercise.drysister.Helper.MemoryCacheHelper;
import com.joting.exercise.drysister.Helper.NetworkHelper;
import com.joting.exercise.drysister.utils.NetworkUtils;
import com.joting.exercise.drysister.utils.SizeUtils;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by zhangguohuang on 2018/1/24.
 */

public class SisterLoader {

    private static final String TAG = "SisterLoader";

    public static final int MESSAGE_POST_RESULT = 1;
    private static final int TAG_KEY_URL = R.id.sister_loader_url;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();//CPU个数
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;//核心线程数
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;//最大线程池大小
    private static final long KEEP_ALIVE = 10L;//线程空闲时间

    private Context mContext;
    private MemoryCacheHelper mMemoryCacheHelper;
    private DiskCacheHelper mDiskCacheHelper;

    /** 线程工厂创建线程 */
    private static final ThreadFactory mFactory = new ThreadFactory() {
        private  final AtomicInteger count = new AtomicInteger(1);
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r,"SisterLoader#" + count.getAndIncrement());
        }
    };
    /** 线程池管理线程*/
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,KEEP_ALIVE, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(),mFactory);
    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message){
            LoaderResult result = (LoaderResult)message.obj;
            ImageView resultImageView = result.imageView;
            //设置图片大小，加载图片
            ViewGroup.LayoutParams params = resultImageView.getLayoutParams();
            params.width = SizeUtils.dp2px(mContext.getApplicationContext(),result.reqWidth);
            params.height = SizeUtils.dp2px(mContext.getApplicationContext(),result.reqHeight);
            resultImageView.setLayoutParams(params);
            resultImageView.setImageBitmap(result.bitmap);
            String url = (String )resultImageView.getTag(TAG_KEY_URL);
            if (url.equals(result.url)){
                resultImageView.setImageBitmap(result.bitmap);
            }else {
                Log.w(TAG,"URL发生改变，不设置图片");
            }
        }
    };
    public static SisterLoader getInstance(Context context){return new SisterLoader(context);}
    private SisterLoader(Context context){
        mContext = context.getApplicationContext();
        mMemoryCacheHelper = new MemoryCacheHelper(mContext);
        mDiskCacheHelper = new DiskCacheHelper(mContext);
    }
    /** 同步加载图片，该方法只能在主线程执行 */
    private Bitmap loadBitmap(String url,int reqWidth,int reqHeight){
        final String key = NetworkHelper.hashKeyFromUrl(url);
        //先在内存中找
        Bitmap bitmap = mMemoryCacheHelper.getBitmapFromMemoryCache(key);
        if (bitmap != null){
            return bitmap;
        }
        //在磁盘中找
        try{
            bitmap = mDiskCacheHelper.loadBitmapFromDiskCache(key,reqWidth,reqHeight);
            //如果磁盘中找到，往内存中塞
            if (bitmap != null) {
                mMemoryCacheHelper.addBitmapToMemoryCache(key, bitmap);
                return bitmap;
            }else {
                //磁盘中找不到，从网络加载
                if (NetworkUtils.isAvailable(mContext)){
                    bitmap = mDiskCacheHelper.saveImageByte(key,reqWidth,reqHeight,NetworkHelper.downloadUrlToStream(url));
                    Log.d(TAG,"加载网络上的图片,URL:" + url);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        if (bitmap == null && !mDiskCacheHelper.isDiskLruCacheCreated()){
            Log.w(TAG,"磁盘缓存未创建！");
            bitmap = NetworkHelper.downloadBitmapFromUrl(url);
        }
        return bitmap;
    }
    /** 异步加载图片 */
    public void bindBitmap(final String url, final ImageView imageView,
                           final int reqWidth, final int reqHeight) {
        if (url == null || url.isEmpty()) return;
        final String key = NetworkHelper.hashKeyFromUrl(url);
        imageView.setTag(TAG_KEY_URL, key);
        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(url,reqWidth,reqHeight);
                if(bitmap != null) {
                    LoaderResult result = new LoaderResult(imageView,url,bitmap,reqWidth,reqHeight);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT,result).sendToTarget();
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }
}
