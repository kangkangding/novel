package com.kang.novel.ui.bookinfo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

import com.bumptech.glide.Glide;
import com.kang.novel.base.BasePresenter;
import com.kang.novel.callback.ResultCallback;
import com.kang.novel.common.APPCONST;
import com.kang.novel.enums.BookSource;
import com.kang.novel.greendao.entity.Book;
import com.kang.novel.greendao.service.BookService;
import com.kang.novel.ui.read.ReadActivity;
import com.kang.novel.util.StringHelper;
import com.kang.novel.util.TextHelper;
import com.kang.novel.util.crawler.BiQuGeReadUtil;
import com.kang.novel.webapi.BookStoreApi;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static androidx.lifecycle.Lifecycle.State.STARTED;

public class BookInfoPresenter extends  BasePresenter {

    private BookInfoActivity mBookInfoActivity;
    private Book mBook;
    private BookService mBookService;
    private MutableLiveData<Book> bookMutableLiveData = new MutableLiveData<>();

    public BookInfoPresenter(BookInfoActivity bookInfoActivity){
        super(bookInfoActivity,bookInfoActivity.getLifecycle());
        mBookInfoActivity  = bookInfoActivity;
        mBookService = new BookService();
        bookMutableLiveData.observe(mBookInfoActivity, book -> {

            mBook = book;
            init();
        });


    }

     @Override
     public void create() {
            mBook = (Book) mBookInfoActivity.getIntent().getSerializableExtra(APPCONST.BOOK);
         if (mBook != null) {
             init();
             getData();
         }
    }


    private void getData(){
        BookStoreApi.getBookInfo(mBook, new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {

                bookMutableLiveData.postValue((Book)o);

            }

            @Override
            public void onError(Exception e) {
                TextHelper.showText(e.getMessage());

            }
        });


    }

    private void init(){
        mBookInfoActivity.getTvTitleText().setText(mBook.getName());
        mBookInfoActivity.getTvBookAuthor().setText(mBook.getAuthor());
        mBookInfoActivity.getTvBookDesc().setText(mBook.getDesc());
        mBookInfoActivity.getTvBookType().setText(mBook.getType());
        mBookInfoActivity.getTvBookName().setText(mBook.getName());
        if (isBookCollected()){
            mBookInfoActivity.getBtnAddBookcase().setText("?????????");
        }else {
            mBookInfoActivity.getBtnAddBookcase().setText("????????????");
        }
        mBookInfoActivity.getLlTitleBack().setOnClickListener(view -> mBookInfoActivity.finish());
        mBookInfoActivity.getBtnAddBookcase().setOnClickListener(view -> {
            if (StringHelper.isEmpty(mBook.getId())){
                mBookService.addBook(mBook);
                TextHelper.showText("??????????????????");
                mBookInfoActivity.getBtnAddBookcase().setText("?????????");
            }else {
                mBookService.deleteBookById(mBook.getId());
                mBook.setId(null);
                TextHelper.showText("??????????????????");
                mBookInfoActivity.getBtnAddBookcase().setText("????????????");
            }

        });
        mBookInfoActivity.getBtnReadBook().setOnClickListener(view -> {
            Intent intent = new Intent(mBookInfoActivity, ReadActivity.class);
            intent.putExtra(APPCONST.BOOK,mBook);
            mBookInfoActivity.startActivity(intent);

        });
        Glide.with(mBookInfoActivity)
                .load(mBook.getImgUrl())
                .into(mBookInfoActivity.getIvBookImg());
    }

    private boolean isBookCollected(){
        Book book = mBookService.findBookByAuthorAndName(mBook.getName(),mBook.getAuthor(),mBook.getSource());
        if (book == null){
            return false;
        }else {
            mBook.setId(book.getId());
            return true;
        }
    }


}
