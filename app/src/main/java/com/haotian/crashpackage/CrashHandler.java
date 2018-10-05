package com.haotian.crashpackage;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.haotian.loginapp.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

/**
 * Created by HAOTIAN on 2016/3/16.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static CrashHandler INSTANCE = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    private Map<String,String> infos = new HashMap<String,String>();
    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");


    private CrashHandler(){

    }
    public static CrashHandler getInstance(){
        return INSTANCE;
    }

    /**
     *  init my default handler
     * @param context
     */
    public void  init(Context context){
         mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
    private boolean handleException(Throwable throwable){
          if (throwable == null){
               return  false;
          }
        new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext,"很抱歉，程序出现错误，正在发送错误日志",Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
//         TODO: 2016/3/16  收集错误信息
           collectDeviceInfo(mContext);
//         TODO: 2016/3/16  保存信息到本地文件
           saveCrashInfo2File(throwable);
        return  true;
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
          if (!handleException(ex) && mDefaultHandler!= null){
                mDefaultHandler.uncaughtException(thread, ex);
          }else
          {
              try {
                  Thread.sleep(1000);
              }catch (InterruptedException e){
                  Log.e(TAG,"error"+e);

              }
             //MyApplication ap = MyApplication.getInstance();
             // ap.exit();
              android.os.Process.killProcess(android.os.Process.myPid());
              System.exit(1);

          }
    }

    public void collectDeviceInfo(Context context){
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo  pi = pm.getPackageInfo(context.getPackageName(),PackageManager.GET_ACTIVITIES);
                if (pi != null){
                    String versionName = pi.versionName == null? "null":pi.versionName;
                    String versionCode = pi.versionCode +"";
                    infos.put("versionName",versionName);
                    infos.put("versionCode",versionCode);
                }
            }catch (Exception e){
                Log.e(TAG, "collectDeviceInfo: ");
            }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields){
              try {
                  field.setAccessible(true);
                  infos.put(field.getName(),field.get(null).toString());
                  Log.d(TAG, "collectDeviceInfo: "+field.getName()+"  "+field.get(null));

              }catch (Exception e){

              }

        }
    }

    private String saveCrashInfo2File(Throwable ex){
           StringBuffer sb = new StringBuffer();
          for(Map.Entry<String,String> entry : infos.entrySet()){
              String key = entry.getKey();
              String value = entry.getValue();
              sb.append(key+"="+value+"\n");


          }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        while (cause!= null){
              cause.printStackTrace(pw);
            cause = cause.getCause();

        }
        pw.close();
        String result = writer.toString();
        sb.append(result);
        try {
                 long timestamp = System.currentTimeMillis();
                 String time = format.format(new Date());
                    String fileName = "crash-" + time + "-" + timestamp + ".log";
                     if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                         String path = Environment.getExternalStorageDirectory() + "/crash/";
                          File dir = new File(path);
                          if (!dir.exists()) {
                                dir.mkdirs();
                            }
                         FileOutputStream fos = new FileOutputStream(path + fileName);
                         fos.write(sb.toString().getBytes());
                     }
                   return fileName;
                 } catch (Exception e) {
                   Log.e(TAG, "an error occured while writing file...", e);
               }
         return null;

    }
}
