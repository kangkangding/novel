package com.kang.pullhtml;

public interface ResultCallback {

    void onFinish(Object o, int code);

    void onError(Exception e);

}
