package com.joting.exercise.drysister.Helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhangguohuang on 2018/1/24.
 */

public class NetworkHelper {

    private static final String TAG = "NetworkHelper";

    private static final int IO_BUFFER_SIZE = 8 * 1024;

    /** 根据URL下载图片的方法 */
    public static Bitmap downloadBitmapFromUrl(String picUrl){
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(picUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        }catch (final IOException e){
            Log.e(TAG,"下载图片出错: " + e);
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            try {
                in.close();
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        return  bitmap;
    }

    /**根据url下载图片的方法*/
    public static byte[] downloadUrlToStream(String picUrl){
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try{
            URL url = new URL(picUrl);
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            if (connection.getResponseCode() == 200){
                inputStream = connection.getInputStream();
                outputStream = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int length;
                while ((length = inputStream.read(bytes)) != -1){
                    outputStream.write(bytes,0,length);
                }
                return outputStream.toByteArray();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (inputStream != null && outputStream != null){
                    inputStream.close();
                    outputStream.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }
    /** URL转md5*/
    public static String hashKeyFromUrl(String url){
        String cacheKey;
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(url.getBytes());
            cacheKey = bytesToHexString(messageDigest.digest());
        }catch (NoSuchAlgorithmException e){
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }
    /** byte 转 md5*/
    public static String bytesToHexString(byte[] bytes){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1){
                stringBuilder.append('0');
            }
            stringBuilder.append(hex);

        }
        return stringBuilder.toString();
    }
}
