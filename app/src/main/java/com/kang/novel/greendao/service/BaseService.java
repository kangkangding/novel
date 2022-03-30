package com.kang.novel.greendao.service;

import android.database.Cursor;

import com.kang.novel.greendao.GreenDaoManager;
import com.kang.novel.greendao.gen.DaoMaster;
import com.kang.novel.greendao.gen.DaoSession;
import com.kang.novel.util.StringHelper;

import org.greenrobot.greendao.annotation.Entity;

import java.util.List;

public class BaseService {

    public void addEntity(Object entity){
        DaoSession daoSession  = GreenDaoManager.getInstance().getSession();
        daoSession.insert(entity);
    }

    public void updateEntity(Object entity){

        DaoSession daoSession  = GreenDaoManager.getInstance().getSession();
        daoSession.update(entity);
    }

    public void deleteEntity(Object entity){
        DaoSession daoSession  = GreenDaoManager.getInstance().getSession();
        daoSession.delete(entity);

    }

    /**
     * 通过SQL查找
     * @param sql
     * @param selectionArgs
     * @return
     */
    public Cursor selectBySql(String sql, String[] selectionArgs){

        Cursor cursor = null;
        try {
            DaoSession daoSession  = GreenDaoManager.getInstance().getSession();
            cursor = daoSession.getDatabase().rawQuery(sql, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return cursor;
    }

    /**
     * 执行SQL进行增删改
     * @param sql
     * @param selectionArgs
     */
    public void  rawQuery(String sql, String[] selectionArgs) {
        DaoSession daoSession  = GreenDaoManager.getInstance().getSession();
        Cursor cursor = daoSession.getDatabase().rawQuery(sql,selectionArgs);
    }




}
