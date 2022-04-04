package com.kang.pullhtml;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpDataSource {

    /**
     * http请求 (get) ps:获取html
     * @param url
     * @param callback
     */
    public static void httpGet_html(String url, final String charsetName, final ResultCallback callback){
        Log.d("HttpGet URl", url);
        NewHttpUtil.sendGetRequest_okHttp(url, new HttpCallback() {
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
}
