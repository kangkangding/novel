package com.kang.novel.ui.home.bookstore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;


import androidx.recyclerview.widget.LinearLayoutManager;


import com.kang.novel.base.BasePresenter;
import com.kang.novel.callback.ResultCallback;
import com.kang.novel.common.APPCONST;
import com.kang.novel.common.URLCONST;
import com.kang.novel.entity.bookstore.BookType;
import com.kang.novel.enums.BookSource;
import com.kang.novel.greendao.entity.Book;
import com.kang.novel.ui.bookinfo.BookInfoActivity;
import com.kang.novel.util.TextHelper;
import com.kang.novel.webapi.BookStoreApi;

import java.util.ArrayList;
import java.util.List;

public class BookStorePresenter extends BasePresenter {

    private BookStoreFragment mBookStoreFragment;
    private LinearLayoutManager mLinearLayoutManager;
    private BookStoreBookTypeAdapter mBookStoreBookTypeAdapter;
    private BookStoreBookAdapter mBookStoreBookAdapter;
    private List<BookType> mBookTypes;
    private List<Book> bookList;
    private BookType curType;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    initTypeList();
                    initBookList();
                    break;
                case 2:
                    initBookList();
                    break;
            }
        }
    };

     BookStorePresenter(BookStoreFragment bookStoreFragment){
         super(bookStoreFragment.getContext(),bookStoreFragment.getLifecycle());
         mBookStoreFragment = bookStoreFragment;
    }

    public void init() {
         //无需加载更多
         mBookStoreFragment.getSrlBookList().setEnableLoadMore(false);

         mBookStoreFragment.getSrlBookList().setEnableRefresh(false);

         //小说列表下拉刷新事件
//         mBookStoreFragment.getSrlBookList().setOnRefreshListener(refreshLayout -> {
//
//             getBooksData();
//         });
         getData();


    }


    /**
     * 获取页面数据
     */
    private void getData(){
        mBookStoreFragment.getBinding().pbLoading.setVisibility(View.VISIBLE);
         BookStoreApi.getBookRank(URLCONST.method_tl_rank, BookSource.tianlai,new ResultCallback() {
             @Override
             public void onFinish(Object o, int code) {
                 mBookTypes = (ArrayList<BookType>)o;
                 curType = mBookTypes.get(0);
                 bookList = mBookTypes.get(0).getBooks();
                 mHandler.sendMessage(mHandler.obtainMessage(1));
             }

             @Override
             public void onError(Exception e) {
                 TextHelper.showText(e.getMessage());

             }
         });


    }

    /**
     * 获取小数列表数据
     */
   /* private void getBooksData(){

        BookStoreApi.getBookRankList(URLCONST.nameSpace_biquge + curType.getUrl(), new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                bookList= (ArrayList<Book>)o;

                mHandler.sendMessage(mHandler.obtainMessage(2));
            }

            @Override
            public void onError(Exception e) {
                TextHelper.showText(e.getMessage());

            }
        });
    }*/





    /**
     * 初始化类别列表
     */
    private void initTypeList(){
        mBookStoreFragment.getBinding().pbLoading.setVisibility(View.GONE);
        //设置布局管理器
        mLinearLayoutManager = new LinearLayoutManager(mBookStoreFragment.getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBookStoreFragment.getRvTypeList().setLayoutManager(mLinearLayoutManager);
        mBookStoreBookTypeAdapter = new BookStoreBookTypeAdapter(mBookStoreFragment.getActivity(), mBookTypes);
        mBookStoreFragment.getRvTypeList().setAdapter(mBookStoreBookTypeAdapter);

        //点击事件
        mBookStoreBookTypeAdapter.setOnItemClickListener((pos, view) -> {
            curType = mBookTypes.get(pos);
            mBookStoreFragment.getBinding().pbLoading.setVisibility(View.VISIBLE);
            bookList = curType.getBooks();
            initBookList();
        });
    }

    /**
     * 初始化小说列表
     */
    private void initBookList(){
        mBookStoreFragment.getBinding().pbLoading.setVisibility(View.GONE);
        //设置布局管理器
        mLinearLayoutManager = new LinearLayoutManager(mBookStoreFragment.getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBookStoreFragment.getRvBookList().setLayoutManager(mLinearLayoutManager);
        mBookStoreBookAdapter = new BookStoreBookAdapter(mBookStoreFragment.getActivity(),bookList);
        mBookStoreFragment.getRvBookList().setAdapter(mBookStoreBookAdapter);

        //点击事件
        mBookStoreBookAdapter.setOnItemClickListener((pos, view) -> {

            Intent intent = new Intent(mBookStoreFragment.getActivity(), BookInfoActivity.class);
            intent.putExtra(APPCONST.BOOK, bookList.get(pos));
            mBookStoreFragment.getActivity().startActivity(intent);
        });

        //刷新动作完成
        mBookStoreFragment.getSrlBookList().finishRefresh();
    }


}
