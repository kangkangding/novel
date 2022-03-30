package com.kang.novel.webapi;

import com.google.gson.Gson;
import com.kang.novel.application.SysManager;
import com.kang.novel.callback.JsonCallback;
import com.kang.novel.callback.ResultCallback;
import com.kang.novel.common.ErrorCode;
import com.kang.novel.entity.JsonModel;
import com.kang.novel.source.HttpDataSource;
import com.kang.novel.util.HttpUtil;
import com.kang.novel.util.JsonArrayToObjectArray;
import com.kang.novel.util.TextHelper;

import java.util.Map;

public class BaseApi {

    /**
     * get通用返回字符串api
     * @param url
     * @param params
     * @param callback
     */
    protected static void getCommonReturnStringApi(String url, Map<String, Object> params, final ResultCallback callback) {
        HttpDataSource.httpGet(HttpUtil.makeURL(url,params), new JsonCallback() {
            @Override
            public void onFinish(JsonModel jsonModel) {
                if (jsonModel.isSuccess()) {
                    callback.onFinish(jsonModel.getResult(), jsonModel.getError());
                } else {
                    noSuccess(jsonModel,callback);
                }
            }
            @Override
            public void onError(Exception e) {
              error(e,callback);
            }
        });
    }

    /**
     * get通用返回Html字符串api
     * @param url
     * @param params
     * @param callback
     */
    protected static void getCommonReturnHtmlStringApi(String url, Map<String, Object> params, String charsetName, final ResultCallback callback) {
        HttpDataSource.httpGet_html(HttpUtil.makeURL(url, params), charsetName, new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(o,code);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
//                error(e,callback);
            }
        });
    }


    /**
     * get通用获取实体列表api
     * @param url
     * @param params
     * @param c 返回是列表实体类型
     * @param callback
     */
    protected static void getCommonListApi(String url, Map<String, Object> params, final Class c, final ResultCallback callback) {
        HttpDataSource.httpGet(HttpUtil.makeURL(url,params), new JsonCallback() {
            @Override
            public void onFinish(JsonModel jsonModel) {
                if (jsonModel.isSuccess()) {
                    try {
                        callback.onFinish(JsonArrayToObjectArray.getArray(jsonModel.getResult(),c), jsonModel.getError());
                    }catch (Exception e){
                        callback.onError(e);
                        e.printStackTrace();
                    }
                } else {
                    noSuccess(jsonModel,callback);
                }
            }

            @Override
            public void onError(Exception e) {
               error(e,callback);
            }
        });
    }

    /**
     * api异常处理
     * @param e
     * @param callback
     */
    private static void error(Exception e, final ResultCallback callback){
      /*  if (e.toString().contains("SocketTimeoutException") || e.toString().contains("UnknownHostException")) {
            TextHelper.showText("网络连接超时，请检查网络");
        }*/
        e.printStackTrace();
        callback.onError(e);
    }

    /**
     * api请求失败处理
     * @param jsonModel
     * @param callback
     */
    private static void noSuccess(JsonModel jsonModel, ResultCallback callback){
        if (!jsonModel.isSuccess()) {
            if (jsonModel.getError() == ErrorCode.no_security) {
                TextHelper.showText("登录过期，请重新登录");
                SysManager.logout();
            } else {
                if (jsonModel.getError() == 0) {
                    callback.onFinish(jsonModel.getResult(), -1);
                } else {
                    callback.onFinish(jsonModel.getResult(), jsonModel.getError());
                }
            }
        }
    }
}
