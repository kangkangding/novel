package com.kang.novel.application;

import android.app.Activity;

import java.util.ArrayList;

public class ActivityManage {

    private static ArrayList<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity ){
        activities.remove(activity);
    }

    public static void finishAllActivites() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static Activity getActivityByCurrenlyRun(){
        if(activities.size() <= 0){
            return null;
        }
        return activities.get(activities.size() - 1);
    }



}
