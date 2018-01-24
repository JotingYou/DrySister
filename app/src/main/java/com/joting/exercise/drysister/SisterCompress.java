package com.joting.exercise.drysister;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;

/**
 * Created by zhangguohuang on 2018/1/24.
 */

public class SisterCompress {

    private static final String TAG = "ImageCompress";

    public SisterCompress(){}

    /**压缩图片资源*/
    public Bitmap decodeBitmapFromResource(Resources resources,int resID,int reqWidth,int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources,resID,options);

        //计算缩放比例
        options.inSampleSize = computeSimpleSize(options,reqWidth,reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources,resID,options);
    }
    /**压缩图片文件*/
    public Bitmap decodeBitmapFromFileDescriptor(FileDescriptor fileDescriptor,int reqWidth,int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor,null,options);
        options.inSampleSize = computeSimpleSize(options,reqWidth,reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor,null,options);
    }

    /**计算缩放比例方法*/
    public int computeSimpleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        if (reqWidth == 0 || reqHeight == 0){
            return 1;
        }
        int inSampleSize = 1;
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.v(TAG,"图片大小为：" + width + "x" + height);
        if (height > reqHeight || width > reqHeight){
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.v(TAG,"inSampleSize = " + inSampleSize);
        return inSampleSize;
    }
}
