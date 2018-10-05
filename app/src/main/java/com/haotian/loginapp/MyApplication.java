package com.haotian.loginapp;

import android.app.Activity;
import android.app.Application;

import com.haotian.crashpackage.CrashHandler;

import org.xutils.x;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by HAOTIAN on 2016/3/12.
 */
public class MyApplication extends Application {
    private static MyApplication instance;
    private List<Activity> mList = new LinkedList<Activity>();
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        x.Ext.init(this);
        //设置日志
        x.Ext.setDebug(true);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
//    private MyApplication(){
//
//    }
//    public synchronized static MyApplication getInstance() {
//        if (null == instance) {
//            instance = new MyApplication();
//        }
//        return instance;
//    }
//    // add Activity
//      public void addActivity(Activity activity) {
//            mList.add(activity);
//          }
//       //关闭每一个list内的activity
//             public void exit() {
//             try {
//                    for (Activity activity:mList) {
//                             if (activity != null)
//                                     activity.finish();
//                         }
//                  } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                      System.exit(0);
//                  }
//          }
//
//       //杀进程
//              public void onLowMemory() {
//               super.onLowMemory();
//          }

}
