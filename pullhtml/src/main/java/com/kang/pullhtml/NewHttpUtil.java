package com.kang.pullhtml;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewHttpUtil {
    private static NewHttpUtil httpUtil;
    private static OkHttpClient okHttpClient;

    public static NewHttpUtil get() {
        if (httpUtil == null) {
            httpUtil = new NewHttpUtil();
        }
        return httpUtil;
    }
    static final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }


                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }
    };

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {

            SSLContext sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            ssfFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    public static OkHttpClient getOkHttpClient() {

        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.sslSocketFactory(createSSLSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) ->
                    true);


            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    public static void sendGetRequest_okHttp(final String address, final HttpCallback callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = getOkHttpClient();
                    Request request = new Request.Builder()
                            .url(address)
                            .build();
                    Response response = client.newCall(request).execute();
                    callback.onFinish(response.body().byteStream());
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(e);
                }
            }
        }).start();

    }
}
