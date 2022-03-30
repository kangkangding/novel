package com.kang.novel.source;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.kang.novel.callback.HttpCallback;
import com.kang.novel.callback.JsonCallback;
import com.kang.novel.callback.ResultCallback;
import com.kang.novel.common.APPCONST;
import com.kang.novel.common.URLCONST;
import com.kang.novel.entity.JsonModel;
import com.kang.novel.util.HttpUtil;
import com.kang.novel.util.RSAUtilV2;
import com.kang.novel.util.StringHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class HttpDataSource {

    /**
     * http请求 (get) ps:获取html
     * @param url
     * @param callback
     */
    public static void httpGet_html(String url, final String charsetName, final ResultCallback callback){
        Log.d("HttpGet URl", url);
        HttpUtil.sendGetRequest_okHttp(url, new HttpCallback() {
            @Override
            public void onFinish(Bitmap bm) {

            }

            @Override
            public void onFinish(InputStream in) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, charsetName));
                    StringBuilder response = new StringBuilder();
                    String line = reader.readLine();
                    while (line != null) {
                        response.append(line);
                        line = reader.readLine();
                    }
                    if (callback != null) {
                        Log.d("Http", "read finish：" + response.toString());
                       callback.onFinish(response.toString(),0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(e);
                }
            }

            @Override
            public void onFinish(String response) {

            }

            @Override
            public void onError(Exception e) {

                if (callback != null) {
                    callback.onError(e);
                }
            }

        });
    }




    /**
     * http请求 (get)
     * @param url
     * @param callback
     */
    public static void httpGet(String url, final JsonCallback callback) {
        Log.d("HttpGet URl", url);
        HttpUtil.sendGetRequest(url, new HttpCallback() {
            @Override
            public void onFinish(Bitmap bm) {

            }

            @Override
            public void onFinish(InputStream in) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line = reader.readLine();
                    while (line != null) {
                        response.append(line);
                        line = reader.readLine();
                    }
                    if (callback != null) {
                        Log.d("Http", "read finish：" + response.toString());
                        // setResponse(response.toString());
                        JsonModel jsonModel = new Gson().fromJson(response.toString(), JsonModel.class);
//                        jsonModel.setResult(jsonModel.getResult().replace("\n",""));
//                        test(jsonModel.getResult());
//                        String str = new String(RSAUtilV2.decryptByPrivateKey(Base64.decode(jsonModel.getResult().replace("\n",""),Base64.DEFAULT),APPCONST.privateKey));
                        if (URLCONST.isRSA && !StringHelper.isEmpty(jsonModel.getResult())) {
                            jsonModel.setResult(StringHelper.decode(new String(RSAUtilV2.decryptByPrivateKey(Base64.decode(jsonModel.getResult().replace("\n", ""), Base64.DEFAULT), APPCONST.privateKey))));
                        }
                        callback.onFinish(jsonModel);
                        Log.d("Http", "RSA finish：" + new Gson().toJson(jsonModel));
                    }
                } catch (Exception e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onFinish(String response) {

            }

            @Override
            public void onError(Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }

        });
    }

    /**
     * http请求 (post)
     * @param url
     * @param output
     * @param callback
     */
    public static void httpPost(String url, String output, final JsonCallback callback) {
        Log.d("HttpPost:", url + "&" + output);
        HttpUtil.sendPostRequest(url, output, new HttpCallback() {
            @Override
            public void onFinish(Bitmap bm) {

            }

            @Override
            public void onFinish(InputStream in) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line = reader.readLine();
                    while (line != null) {
                        response.append(line);
                        line = reader.readLine();
                    }
                    if (callback != null) {
                        Log.d("Http", "read finish：" + response);
                        // setResponse(response.toString());
                        JsonModel jsonModel = new Gson().fromJson(response.toString(), JsonModel.class);
                        if (URLCONST.isRSA && !StringHelper.isEmpty(jsonModel.getResult())) {
                            jsonModel.setResult(StringHelper.decode(new String(RSAUtilV2.decryptByPrivateKey(Base64.decode(jsonModel.getResult().replace("\n", ""), Base64.DEFAULT), APPCONST.privateKey))));
                        }
                        callback.onFinish(jsonModel);
                        Log.d("Http", "RSA finish：" + new Gson().toJson(jsonModel));
                    }
                } catch (Exception e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onFinish(String response) {
                Log.e("http", response);
            }

            @Override
            public void onError(Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

}
