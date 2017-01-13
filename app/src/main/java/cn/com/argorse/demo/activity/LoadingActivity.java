package cn.com.argorse.demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.Timer;

import cn.com.argorse.common.utils.SharedPreUtils;
import cn.com.argorse.demo.BaseApplication;
import cn.com.argorse.demo.R;

public class LoadingActivity extends Activity {
    private BaseApplication mApplication;
    private Activity mActivity;
    private int recLen = 1;//秒
    Timer timer = new Timer();
    String isFirstInstall = "01";//01是，02 不是

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);// 主文件布局
        mActivity = this;

        mApplication = (BaseApplication) getApplication();
        //是否是第一次运行
        if (SharedPreUtils.getInstance(mActivity).getString("isFirstInstall", "01").equals("01")) {//第一次安装
            isFirstInstall = "01";
           SharedPreUtils.getInstance(mActivity).saveKeyObjValue("isFirstInstall", "02");

        } else {//不是第一次运行
            isFirstInstall = "02";

        }

        goMainActivity();



    }

    private void goMainActivity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (timer != null) timer.cancel();
                if (recLen > 0) {
                    try {
                        Thread.sleep(recLen * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Uri uri = getIntent().getData();
                String type = "";
                if (uri != null) {
                    type = uri.getQueryParameter("type");
                }
                Intent intent = new Intent(mActivity, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).start();
    }






}
