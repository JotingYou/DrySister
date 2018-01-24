package com.joting.exercise.drysister;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button showButton;
    private Button refreshButton;
    private ImageView showImage;

    private ArrayList<Sister> datas;
    private int curPos = 0;//当前显示哪一张
    private int page = 1;//当前页数
    private PictureLoader loader;
    private SisterApi sisterApi;
    private SisterTask sisterTask;
    private SisterLoader mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sisterApi = new SisterApi();
        loader = new PictureLoader();
        mLoader = SisterLoader.getInstance(MainActivity.this);
        initData();
        initUI();
    }

    private  void initData(){
        datas = new ArrayList<>();
    }

    private  void initUI(){
        showButton = (Button) findViewById(R.id.button_show);
        refreshButton = (Button) findViewById(R.id.button_refresh);
        showImage = (ImageView)findViewById(R.id.image_show);

        showButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_show:
                if (datas != null && !datas.isEmpty()){
                    if (curPos > datas.size()-1){
                        curPos = 0;
                    }
                    //loader.load(showImage,datas.get(curPos).url);
                    mLoader.bindBitmap(datas.get(curPos).url,showImage,400,400);
                    curPos++;
                }

                break;
            case R.id.button_refresh:
                sisterTask = new SisterTask();
                sisterTask.execute();
                curPos = 0;
                //mLoader.bindBitmap(datas.get(curPos).url,showImage,400,400);
                break;
        }
    }
    private  class SisterTask extends AsyncTask<Void,Void,ArrayList<Sister>>{
        public SisterTask() {}
        @Override
        protected ArrayList<Sister> doInBackground(Void... Params){
            return sisterApi.fetchSister(10,page);
        }
        @Override
        protected  void onPostExecute(ArrayList<Sister> sisters){
            super.onPostExecute(sisters);
            datas.clear();
            datas.addAll(sisters);
            page++;
        }
        @Override
        protected void onCancelled(){
            super.onCancelled();
            sisterTask = null;
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        sisterTask.cancel(true);
    }
}
