package com.joting.exercise.drysister;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import android.os.Handler;


/**
 * Created by zhangguohuang on 2018/1/23.
 */

public class PictureLoader {
    private ImageView loadImage;
    private  String imageUrl;
    private byte[] picByte;

    Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x123) if (picByte != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(picByte, 0, picByte.length);
                    loadImage.setImageBitmap(bitmap);
                }
            }

    };

    public void load(ImageView loadImage,String imageUrl){
        this.loadImage = loadImage;
        this.imageUrl = imageUrl;
        Drawable drawable = loadImage.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()){
                bitmap.recycle();
            }
        }
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                if (conn.getResponseCode() == 200){
                    InputStream in = conn.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int length = -1;
                    while ((length = in.read(bytes)) != -1){
                        out.write(bytes,0,length);
                    }
                    picByte = out.toByteArray();
                    in.close();
                    out.close();
                    handler.sendEmptyMessage(0x123);
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    };
}
