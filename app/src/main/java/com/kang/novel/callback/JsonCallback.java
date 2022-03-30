package com.kang.novel.callback;


import com.kang.novel.entity.JsonModel;

public interface JsonCallback {

    void onFinish(JsonModel jsonModel);

    void onError(Exception e);

}
