package com.kang.novel.ui.home.bookcase;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.widget.AdapterView;

import androidx.core.content.ContextCompat;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.kang.novel.R;
import com.kang.novel.base.BasePresenter;
import com.kang.novel.callback.ResultCallback;

import com.kang.novel.custom.DragSortGridView;
import com.kang.novel.greendao.entity.Book;
import com.kang.novel.greendao.entity.Chapter;
import com.kang.novel.greendao.service.BookService;

import com.kang.novel.ui.home.MainActivity;

import com.kang.novel.ui.search.SearchBookActivity;

import com.kang.novel.util.VibratorUtil;
import com.kang.novel.webapi.CommonApi;

import java.util.ArrayList;

public class BookcasePresenter extends BasePresenter {

    private BookcaseFragment mBookcaseFragment;
    private ArrayList<Book> mBooks = new ArrayList<>();//书目数组
    private BookcaseDragAdapter mBookcaseAdapter;
    private BookService mBookService;
    private MainActivity mMainActivity;
//    private ChapterService mChapterService;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mBookcaseAdapter.notifyDataSetChanged();
                    mBookcaseFragment.getSrlContent().finishRefresh();
                    break;
                case 2:
                    mBookcaseFragment.getSrlContent().finishRefresh();
                    break;

            }
        }
    };

    BookcasePresenter(BookcaseFragment bookcaseFragment) {
        super(bookcaseFragment.getContext(),bookcaseFragment.getLifecycle());
        mBookcaseFragment = bookcaseFragment;
        mBookService = new BookService();

    }

    @Override
    public void start() {
        mMainActivity = ((MainActivity) (mBookcaseFragment.getContext()));
        mBookcaseFragment.getSrlContent().setEnableRefresh(false);
        mBookcaseFragment.getSrlContent().setEnableHeaderTranslationContent(false);
        mBookcaseFragment.getSrlContent().setEnableLoadMore(false);
        mBookcaseFragment.getSrlContent().setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initNoReadNum();
            }
        });


        mBookcaseFragment.getLlNoDataTips().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mBookcaseFragment.getContext(), SearchBookActivity.class);
                mBookcaseFragment.startActivity(intent);
            }
        });

        mBookcaseFragment.getGvBook().setOnItemLongClickListener((parent, view, position, id) -> {
            if (!mBookcaseAdapter.ismEditState()) {
                mBookcaseFragment.getSrlContent().setEnableRefresh(false);
                mBookcaseAdapter.setmEditState(true);
                mBookcaseFragment.getGvBook().setDragModel(DragSortGridView.DRAG_BY_LONG_CLICK);
                mBookcaseAdapter.notifyDataSetChanged();
                mMainActivity.getRlCommonTitle().setVisibility(View.GONE);
                VibratorUtil.Vibrate(mBookcaseFragment.getActivity(),200);
//                    mBookcaseFragment.getGvBook().setOnItemClickListener(null);
            }
            return true;
        });
    }

    private void init() {
        initBook();
        if (mBooks == null || mBooks.size() == 0) {
            mBookcaseFragment.getGvBook().setVisibility(View.GONE);
            mBookcaseFragment.getLlNoDataTips().setVisibility(View.VISIBLE);
        } else {
            if(mBookcaseAdapter == null) {
                mBookcaseAdapter = new BookcaseDragAdapter(mBookcaseFragment.getContext(), R.layout.gridview_book_item, mBooks, false);
                mBookcaseFragment.getGvBook().setDragModel(-1);
                mBookcaseFragment.getGvBook().setTouchClashparent(((MainActivity) (mBookcaseFragment.getContext())).getVpContent());
       /*     mBookcaseFragment.getGvBook().setDragModel(DragSortGridView.DRAG_BY_LONG_CLICK);
            ((MainActivity) (mBookcaseFragment.getActivity())).setViewPagerScroll(false);*/
                mBookcaseFragment.getGvBook().setAdapter(mBookcaseAdapter);
            }else {
                mBookcaseAdapter.notifyDataSetChanged();
            }
            mBookcaseFragment.getLlNoDataTips().setVisibility(View.GONE);
            mBookcaseFragment.getGvBook().setVisibility(View.VISIBLE);
        }
    }


    public void getData() {
        init();
        initNoReadNum();
    }

    private void initBook() {
        mBooks.clear();
        mBooks.addAll(mBookService.getAllBooks());
        for (int i = 0; i < mBooks.size(); i++) {
            if (mBooks.get(i).getSortCode() != i + 1) {
                mBooks.get(i).setSortCode(i + 1);
                mBookService.updateEntity(mBooks.get(i));
            }
        }
    }

    private void initNoReadNum() {
        for (final Book book : mBooks) {
            CommonApi.getBookChapters(book, new ResultCallback() {
                @Override
                public void onFinish(Object o, int code) {
                    final ArrayList<Chapter> chapters = (ArrayList<Chapter>) o;
                    int noReadNum = chapters.size() - book.getChapterTotalNum();
                    if (noReadNum > 0) {
                        book.setNoReadNum(noReadNum);
                        mHandler.sendMessage(mHandler.obtainMessage(1));
                    } else {
                        book.setNoReadNum(0);
                        mHandler.sendMessage(mHandler.obtainMessage(2));
                    }
                    mBookService.updateEntity(book);
                }

                @Override
                public void onError(Exception e) {
                    mHandler.sendMessage(mHandler.obtainMessage(1));
                }
            });
        }
    }

    private void setThemeColor(int colorPrimary, int colorPrimaryDark) {
//        mToolbar.setBackgroundResource(colorPrimary);
        mBookcaseFragment.getSrlContent().setPrimaryColorsId(colorPrimary, android.R.color.white);
        if (Build.VERSION.SDK_INT >= 21) {
            mBookcaseFragment.getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(mBookcaseFragment.getContext(), colorPrimaryDark));
        }
    }


    @Override
    public void resume(){
        getData();
    }

}
