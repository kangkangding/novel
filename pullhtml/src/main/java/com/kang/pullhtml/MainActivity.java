package com.kang.pullhtml;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tv_html_content;
    Handler handler = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            tv_html_content.setText(msg.obj.toString());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_html_content = findViewById(R.id.tv_html_content);
    }

    public void getHtml(View view) {
        HttpDataSource.httpGet_html("https://www.tiatiatoutiao.com/", "utf-8", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                Message message = new Message();
                message.obj = o;
                message.what = 1;
                handler.sendMessage(message);

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}