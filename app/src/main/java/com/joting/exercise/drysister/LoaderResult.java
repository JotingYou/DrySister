package com.joting.exercise.drysister;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by zhangguohuang on 2018/1/24.
 */

public class LoaderResult {
    public ImageView imageView;
    public String url;
    public Bitmap bitmap;
    public int reqWidth;
    public int reqHeight;

    public LoaderResult(ImageView imageView,String url,Bitmap bitmap,int reqWidth,int reqHeight){
        this.imageView = imageView;
        this.url = url;
        this.bitmap = bitmap;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }
}
