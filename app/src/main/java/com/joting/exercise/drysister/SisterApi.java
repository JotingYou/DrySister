package com.joting.exercise.drysister;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by zhangguohuang on 2018/1/24.
 */

public class SisterApi {

    private static final String TAG = "Network";
    private static final String BASE_URL = "http://gank.io/api/data/福利/";

    /**
     * 查询妹子信息
     */
    public ArrayList<Sister> fetchSister(int count,int page){
        String fetchUrl = BASE_URL + count + "/" + page;
        ArrayList<Sister> sisters = new ArrayList<>();
        try {
            URL url = new URL(fetchUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            Log.v(TAG,"Server response:" + code);
            if (code == 200){
                InputStream in = conn.getInputStream();
                byte[] data = readFromStream(in);
                String result = new String(data,"UTF-8");
                sisters = parseSister(result);

            }else {
                Log.e(TAG,"请求失败:" + code);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sisters;
    }
    /**
     * 解析Json返回数据的方法
     */
    public  ArrayList<Sister> parseSister(String content) throws Exception{
        ArrayList<Sister> sisters = new ArrayList<>();
        JSONObject object = new JSONObject(content);
        JSONArray array = object.getJSONArray("results");
        for (int i = 0; i < array.length(); i++) {
            JSONObject results = (JSONObject)array.get(i);
            Sister sister = new Sister();
            sister._id = results.getString("_id");
            sister.createAt = results.getString("createdAt");
            sister.desc = results.getString("desc");
            sister.publishedAt = results.getString("publishedAt");
            sister.source = results.getString("source");
            sister.type = results.getString("type");
            sister.url = results.getString("url");
            sister.used = results.getBoolean("used");
            sister.who = results.getString("who");
            sisters.add(sister);
        }
        return sisters;
    }
    /**
     * 读取数据流的方法
     */
    public byte[] readFromStream(InputStream inputStream) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len ;
        while ((len = inputStream.read(buffer)) != -1){
            outputStream.write(buffer,0,len);
        }
        inputStream.close();
        return outputStream.toByteArray();
    }

}
