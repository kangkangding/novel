package com.kang.novel.webapi;

import com.kang.novel.callback.ResultCallback;
import com.kang.novel.common.URLCONST;
import com.kang.novel.greendao.entity.Book;
import com.kang.novel.util.crawler.BiQuGeReadUtil;
import com.kang.novel.util.crawler.DingDianReadUtil;
import com.kang.novel.util.crawler.TianLaiReadUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 *  2017/7/24.
 */

public class CommonApi extends BaseApi{


    /**
     * 获取章节列表
     * @param book
     * @param callback
     */
    public static void getBookChapters(Book book, final ResultCallback callback){

        getCommonReturnHtmlStringApi(book.getChapterUrl(), null, "GBK", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(TianLaiReadUtil.getChaptersFromHtml((String) o,book),0);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);

            }
        });
    }

    /**
     * 获取章节正文
     * @param url
     * @param callback
     */
    public static void getChapterContent(String url, final ResultCallback callback){
        int tem = url.indexOf("\"");
        if (tem != -1){
            url = url.substring(0,tem);
        }
        if (!url.contains("http")){
            url = URLCONST.nameSpace_tianlai + url;
        }

        getCommonReturnHtmlStringApi(url, null, "GBK", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(TianLaiReadUtil.getContentFormHtml((String)o),0);

            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * 搜索小说（天籁）
     * @param key
     * @param callback
     */
    public static void searchTl(String key, final ResultCallback callback){
        Map<String,Object> params = new HashMap<>();
        try {
            params.put("searchkey", URLEncoder.encode(key,"GB2312"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getCommonReturnHtmlStringApi(URLCONST.method_tl_search, params, "gbk", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(TianLaiReadUtil.getBooksFromSearchHtml((String)o),code);

            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);

            }
        });
    }


    /**
     * 搜索小说（笔趣阁）
     * @param key
     * @param callback
     */
    public static void searchBqg(String key, final ResultCallback callback){
        Map<String,Object> params = new HashMap<>();
        try {
            params.put("searchkey", URLEncoder.encode(key,"GB2312"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getCommonReturnHtmlStringApi(URLCONST.method_bqg_search, params, "GBK", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(BiQuGeReadUtil.getBooksFromSearchHtml((String)o),code);

            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);

            }
        });
    }

    /**
     * 搜索小说（顶点小说）
     * @param key
     * @param callback
     */
    public static void searchDdxs(String key, final ResultCallback callback){
        Map<String,Object> params = new HashMap<>();
        try {
            params.put("searchkey", URLEncoder.encode(key,"GB2312"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        params.put("searchtype", "articlename");
        getCommonReturnHtmlStringApi(URLCONST.method_dd_search, params, "GBK", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(DingDianReadUtil.getBooksFromSearchHtml((String)o),code);

            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);

            }
        });
    }

    /**
     * 获取天籁热门小说列表

     * @param callback
     */
    public static void getTlHotBookList( final ResultCallback callback){

        getCommonReturnHtmlStringApi(URLCONST.nameSpace_tianlai, null, "GBK", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(TianLaiReadUtil.getHotBookList((String)o),0);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);

            }
        });
    }


}
