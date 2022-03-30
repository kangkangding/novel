package com.kang.novel.webapi;

import com.kang.novel.callback.ResultCallback;
import com.kang.novel.common.URLCONST;
import com.kang.novel.enums.BookSource;
import com.kang.novel.greendao.entity.Book;
import com.kang.novel.util.crawler.BiQuGeReadUtil;
import com.kang.novel.util.crawler.DingDianReadUtil;
import com.kang.novel.util.crawler.TianLaiReadUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *  2017/7/24.
 */

public class BookStoreApi extends BaseApi{


    /**
     * 获取书城小说分类列表
     * @param url
     * @param callback
     */
    public static void getBookTypeList(String url, final ResultCallback callback){

        getCommonReturnHtmlStringApi(url, null, "GBK", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(BiQuGeReadUtil.getBookTypeList((String) o),0);



            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);

            }
        });
    }


    /**
     * 获取某一分类小说排行榜列表
     * @param url
     * @param callback
     */
    public static void getBookRankList(String url, final ResultCallback callback){

        getCommonReturnHtmlStringApi(url, null, "GBK", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(BiQuGeReadUtil.getBookRankList((String) o),0);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);

            }
        });
    }


    /**
     * 获取小说详细信息
     * @param book
     * @param callback
     */
    public static void getBookInfo(Book book, final ResultCallback callback){

        getCommonReturnHtmlStringApi(book.getChapterUrl(), null, "GBK", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                if (BookSource.dingdian.toString().equals(book.getSource())){
                    callback.onFinish(DingDianReadUtil.getBookInfo((String) o,book),0);

                }else if(BookSource.biquge.toString().equals(book.getSource())){

                    callback.onFinish(BiQuGeReadUtil.getBookInfo((String) o,book),0);
                }else if(BookSource.tianlai.toString().equals(book.getSource())){

                    callback.onFinish(TianLaiReadUtil.getBookInfo((String) o,book),0);
                }

            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);

            }
        });
    }


    /**
     * 获取书城小说排行榜
     * @param url
     * @param callback
     */
    public static void getBookRank(String url, BookSource bookSource, final ResultCallback callback){

        getCommonReturnHtmlStringApi(url, null, "GBK", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {

                if (BookSource.dingdian == bookSource){
                    callback.onFinish(DingDianReadUtil.getRank((String) o),0);

                }else if(BookSource.biquge == bookSource){


                }else if(BookSource.tianlai == bookSource){

                    callback.onFinish(TianLaiReadUtil.getRank((String) o),0);
                }


            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);

            }
        });
    }







}
