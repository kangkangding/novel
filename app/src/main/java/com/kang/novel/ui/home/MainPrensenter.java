package com.kang.novel.ui.home;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.kang.novel.base.BasePresenter;
import com.kang.novel.ui.home.bbs.NovelUpdateFragment;
import com.kang.novel.ui.home.bookcase.BookcaseFragment;
import com.kang.novel.ui.home.bookstore.BookStoreFragment;
import com.kang.novel.ui.search.SearchBookActivity;

import java.util.ArrayList;

public class MainPrensenter extends BasePresenter {

    private MainActivity mMainActivity;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private String[] tabTitle = {"最热", "书城", "书架"};

    public MainPrensenter(MainActivity mainActivity) {
        super(mainActivity, mainActivity.getLifecycle());
        mMainActivity = mainActivity;
    }

    @Override
    public void create() {
        init();
        mMainActivity.getIvSearch().setOnClickListener(view -> {
            Intent intent = new Intent(mMainActivity, SearchBookActivity.class);
            mMainActivity.startActivity(intent);
        });

    }

    /**
     * 初始化
     */
    private void init() {
        mFragments.clear();
        mFragments.add(new NovelUpdateFragment());
        mFragments.add(new BookStoreFragment());
        mFragments.add(new BookcaseFragment());

        mMainActivity.getVpContent().setAdapter(new FragmentPagerAdapter(mMainActivity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

            @Override
            public Fragment getItem(int position) {

                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position >= tabTitle.length) return null;
                return tabTitle[position];
            }

        });
        mMainActivity.getTlTabMenu().setupWithViewPager(mMainActivity.getVpContent());
        mMainActivity.getVpContent().setCurrentItem(0);
        mMainActivity.getTlTabMenu().setTabMode(TabLayout.MODE_FIXED);
        mMainActivity.getTlTabMenu().setTabGravity(TabLayout.GRAVITY_FILL);
    }
}
