package com.kang.novel.ui.font;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.lifecycle.LiveData;

import com.kang.novel.R;
import com.kang.novel.base.BasePresenter;
import com.kang.novel.enums.Font;

import java.util.ArrayList;

public class FontsPresenter extends BasePresenter {

    private FontsActivity mFontsActivity;
    private ArrayList<Font> mFonts;
    private FontsAdapter mFontsAdapter;



    public FontsPresenter(FontsActivity fontsActivity) {
        super(fontsActivity,fontsActivity.getLifecycle());
        mFontsActivity = fontsActivity;
    }


    @Override
    public void create() {
        mFontsActivity.getLlTitleBack().setOnClickListener(v -> mFontsActivity.finish());

        mFontsActivity.getTvTitleText().setText(mFontsActivity.getString(R.string.font));
        init();
    }

    private void init() {
        initFonts();
        mFontsAdapter = new FontsAdapter(mFontsActivity, R.layout.listview_font_item, mFonts);
        mFontsActivity.getLvFonts().setAdapter(mFontsAdapter);
        mFontsActivity.getPbLoading().setVisibility(View.GONE);
    }



    private void initFonts() {
        mFonts = new ArrayList<>();
        mFonts.add(Font.默认字体);
        mFonts.add(Font.方正楷体);
        mFonts.add(Font.经典宋体);
        mFonts.add(Font.方正行楷);
        mFonts.add(Font.迷你隶书);
        mFonts.add(Font.方正黄草);
        mFonts.add(Font.书体安景臣钢笔行书);
    }
}
