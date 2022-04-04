package com.kang.novel.ui.home;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.kang.novel.R;

import com.kang.novel.common.APPCONST;
import com.kang.novel.databinding.ActivityMainBinding;
import com.kang.novel.util.SystemBarTintManager;
import com.kang.novel.util.TextHelper;


public class MainActivity extends FragmentActivity {

    private MainPrensenter mMainPrensenter;

    private ActivityMainBinding binding;
    AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setStatusBar(R.color.sys_line);
        mMainPrensenter = new MainPrensenter(this);
        mMainPrensenter.enable();
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.d("mAdView"," 广告 加载 onAdClicked");
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.d("mAdView"," 广告 加载 onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d("mAdView"," 广告 加载 onAdFailedToLoad" + loadAdError.toString());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.d("mAdView"," 广告 加载 onAdImpression");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d("mAdView"," 广告 加载 onAdLoaded");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.d("mAdView"," 广告 加载 onAdOpened");
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        if (System.currentTimeMillis() - APPCONST.exitTime > APPCONST.exitConfirmTime) {
//            TextHelper.showText("再按一次退出");
//            APPCONST.exitTime = System.currentTimeMillis();
//        } else {
//            finish();
//        }
//    }

    public TabLayout getTlTabMenu() {
        return binding.tlTabMenu;
    }

    public ImageView getIvSearch() {
        return binding.ivSearch;
    }

    public ViewPager getVpContent() {
        return binding.vpContent;
    }

    public RelativeLayout getRlCommonTitle() {
        return binding.rlCommonTitle;
    }

    @TargetApi(19)
    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 设置状态栏颜色
     *
     * @param colorId
     */
    public void setStatusBar(int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(colorId);//通知栏所需颜色ID
        }
    }

}
