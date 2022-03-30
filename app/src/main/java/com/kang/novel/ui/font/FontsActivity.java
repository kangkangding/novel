package com.kang.novel.ui.font;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kang.novel.R;
import com.kang.novel.base.BaseActivity;
import com.kang.novel.databinding.ActivityFontsBinding;


public class FontsActivity extends BaseActivity {



    private FontsPresenter mFontsPresenter;
    private ActivityFontsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFontsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setStatusBar(R.color.sys_line);
        mFontsPresenter = new FontsPresenter(this);
        mFontsPresenter.enable();

    }

    public ProgressBar getPbLoading() {
        return binding.pbLoading;
    }

    public LinearLayout getLlTitleBack() {
        return binding.title.llTitleBack;
    }

    public TextView getTvTitleText() {
        return binding.title.tvTitleText;
    }

    public ListView getLvFonts() {
        return binding.lvFonts;
    }

    public ActivityFontsBinding getBinding() {
        return binding;
    }
}
