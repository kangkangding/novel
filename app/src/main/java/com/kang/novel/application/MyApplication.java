package com.kang.novel.application;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.scwang.smartrefresh.header.WaveSwipeHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.kang.novel.R;
import com.kang.novel.base.BaseActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {

    private static Handler handler = new Handler();
    private static MyApplication application;
    private ScheduledExecutorService mFixedThreadPool;

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.sys_book_type_bg, R.color.sys_refresh_main);//全局设置主题颜色
            return new WaveSwipeHeader(context);
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {

            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }



    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

                Log.d("initializationStatus", " initializationStatus = " + initializationStatus.getAdapterStatusMap());


            }
        });
        application = this;
//        HttpUtil.trustAllHosts();//信任所有证书


//        handleSSLHandshake();

        Fresco.initialize(this);

        mFixedThreadPool = Executors.newScheduledThreadPool(Math.min(Runtime.getRuntime().availableProcessors(), 2));//初始化线程池   最大线程2是为了限制瞬时访问量，以防止网站反爬虫识别

        BaseActivity.setCloseAntiHijacking(true);

    }

    public static Context getmContext() {
        return application;
    }


    public void newThread(Runnable runnable) {

        try {

            mFixedThreadPool.schedule(runnable,1,TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            mFixedThreadPool = Executors.newScheduledThreadPool(Math.min(Runtime.getRuntime().availableProcessors(), 3));//初始化线程池
            mFixedThreadPool.execute(runnable);
        }
    }

    public void shutdownThreadPool(){
        mFixedThreadPool.shutdownNow();
    }

    /**
     * 主线程执行
     *
     * @param runnable
     */
    public static void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public static MyApplication getApplication() {
        return application;
    }

}
