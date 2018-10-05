package com.haotian.loginapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.haotian.myview.LoadingDialog;
import com.haotian.setdbtblp.TblP;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.spec.ECField;
import java.util.List;


/**
 * Created by HAOTIAN on 2016/3/12.
 */
public class FirstLoginActivity extends Activity implements View.OnClickListener{
    private final static String TAG="zhtLoginAPP";
    private final static String TAG_SUCCESS = "<title>哈尔滨医科大学本科教学管理与服务平台";
    private final static String TAG_FAIL = "<title>用户登录";
    private List<TblP> infoList;
    private Boolean FLAG_INTERNET_NOT_AVAILABLE = false;
    private DbManager.DaoConfig dao;
    private DbManager dbxu3;
    private Spinner spinner_USER;
    private LoadingDialog loadingDialog;
    private EditText edt_stringyanzhengma;
    private ArrayAdapter<String> arrayAdapter;
    private String[] infoStringArray;
    private String[] pswStringArray;
    private ImageView yanzhengImg;
    private String COOKIE;
    private String DOMAIN;
    private String NAME;
    private Button btn_login;
    private String currentUserId = null;
    private String currentUserPsw = null;
    private String code;
    private int currentPosition;
    private long firstTime = 0;
    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123){
                Bundle bundle = msg.getData();
                byte[] mbyte = bundle.getByteArray("img");
                Bitmap bitmap = BitmapFactory.decodeByteArray(mbyte, 0, mbyte.length);
                yanzhengImg.setImageBitmap(bitmap);

                System.out.println("hahah   "+COOKIE);
                System.out.println("hahaa   "+NAME);
                System.out.println("hahah   "+DOMAIN);
                //System.out.println("hahah   "+code);

            }
            if (msg.what == 0x110){
                //登陆成功，进行页面的跳转，同时携带COOKIE,DOMAIN,NAME 3个参数
                Intent intent = new Intent(FirstLoginActivity.this,MainActivity.class);
                intent.putExtra("COOKIE",COOKIE);
                intent.putExtra("DOMAIN",DOMAIN);
                intent.putExtra("NAME",NAME);
                loadingDialog.dismiss();
                startActivity(intent);

            }
            if (msg.what == 0x111){
                //登陆失败，验证码改变，验证码输入框清空。同时提示重新输入验证码进行登陆
                edt_stringyanzhengma.setText("");
                getYanzhengImg();
                loadingDialog.dismiss();
                Toast.makeText(FirstLoginActivity.this, "登录失败，请重新登录（验证码错误）", Toast.LENGTH_SHORT).show();

            }
            if(msg.what == 0x001){
                //此时获取验证码失败，网络连接不可用
                FLAG_INTERNET_NOT_AVAILABLE = true;
                Toast.makeText(FirstLoginActivity.this,"            网络连接不可用\n请确保网络正常情况下重新打开",Toast.LENGTH_SHORT).show();
            }

            if(msg.what == 0x010){
                //不需更新
                new AlertDialog.Builder(FirstLoginActivity.this)
                        .setTitle("来自网络")
                        .setMessage("         已经是最新版本！")
                        .setPositiveButton("确定",null)
                        .show();
            }
            if(msg.what == 0x011){
                //需要更新
                new AlertDialog.Builder(FirstLoginActivity.this)
                        .setTitle("!")
                        .setMessage("有最新版本！！请进入网站下载！\n 网址为：www.hmu614.wang")
                        .setPositiveButton("确定",null)
                        .show();
            }

        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {                                                    //两次按键小于2秒时，退出应用
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_firstlogin);
       // MyApplication ap = MyApplication.getInstance();
       // ap.addActivity(this);
        //System.out.println(4/0);
        spinner_USER = (Spinner) findViewById(R.id.UserInfoSpinner);
        yanzhengImg = (ImageView) findViewById(R.id.img_yanzhengma);
        btn_login = (Button) findViewById(R.id.Login);
        btn_login.setOnClickListener(this);
        edt_stringyanzhengma = (EditText) findViewById(R.id.stringyanzhengma);
        dao=new DbManager
                .DaoConfig()
                .setDbName("dbUser2")
                //.setDbDir()
                .setDbVersion(1);
        dbxu3 = x.getDb(dao);

        freshListInfo();
        getYanzhengImg();
        spinner_USER.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 判断是否为第一个项目  也就是“请先登录”
                if (position ==  0){
                    /**
                     *  记得添加 第一次进入应用时 取消该步
                     */
                       Intent intent = new Intent(FirstLoginActivity.this,DialogActivity.class);
                       startActivityForResult(intent,0);
                       return;
                }
                System.out.println(position);
                currentPosition = position;
                currentUserId = infoStringArray[position];
                currentUserPsw = pswStringArray[position];
                edt_stringyanzhengma.setText("");
                getYanzhengImg();
                System.out.println(currentUserId);
                System.out.println(currentUserPsw);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_USER.setSelection(-1);

    }
    public void changeImg(View view){
         getYanzhengImg();
    }
    @Override
    protected void onResume() {
        super.onResume();
        freshListInfo();
    }

    public void freshListInfo(){
        try {
            infoList = dbxu3.findAll(TblP.class);
            if (infoList == null){
                TblP tblP = new TblP();
                tblP.setPassword("");
                tblP.setUsercode("请先登录");
                try {
                    dbxu3.save(tblP);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                freshListInfo();
                return;
            }
            int length = infoList.size();
            int beginFlag = 0;
            infoStringArray = new String[length];
            pswStringArray = new String[length];
           for (TblP tblP:infoList){
               infoStringArray[beginFlag] = tblP.getUsercode();
               pswStringArray[beginFlag] = tblP.getPassword();
               beginFlag++;
           }
          //  currentUserId = infoStringArray[beginFlag-1];
            arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,infoStringArray);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner_USER.setAdapter(arrayAdapter);

            spinner_USER.setSelection(beginFlag-1);

           // spinner_USER.setSelection(beginFlag-1);
        } catch (DbException e) {
            e.printStackTrace();
        }


    }

    public void getYanzhengImg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet("http://jwc.hrbmu.edu.cn/bkjw/");
                    HttpResponse httpResponse = httpclient.execute(httpGet);
                    COOKIE = ((AbstractHttpClient) httpclient).getCookieStore().getCookies().get(0).getValue();
                    NAME=((AbstractHttpClient) httpclient).getCookieStore().getCookies().get(0).getName();
                    DOMAIN=((AbstractHttpClient) httpclient).getCookieStore().getCookies().get(0).getDomain();
                    HttpGet httpGetImg = new HttpGet("http://jwc.hrbmu.edu.cn/bkjw/captchaImage?id=3333");
                    String cookiestring = NAME+"="+COOKIE;
                    Log.i(TAG, "run: "+cookiestring);
                    httpGetImg.addHeader("cookie",cookiestring);
                    httpGetImg.addHeader("Host","jwc.hrbmu.edu.cn");
                    httpGetImg.addHeader("Referer","http://jwc.hrbmu.edu.cn/bkjw/");
                    HttpResponse httpResponseImg = httpclient.execute(httpGetImg);
                    byte[] bytes = EntityUtils.toByteArray(httpResponseImg.getEntity());

                    Bundle bundle = new Bundle();
                    bundle.putByteArray("img",bytes);
                    Message message = new Message();
                    message.setData(bundle);
                    message.what= 0x123;
                    mhandler.sendMessage(message);

                }catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "run: ",e );
                    Message msg = new Message();
                    msg.what = 0x001;
                    mhandler.sendMessage(msg);
                }

            }
        }).start();
    }
    public void notLogin(View v){
        Toast.makeText(this,"please input your id and password",Toast.LENGTH_LONG).show();
         Intent intent = new Intent(FirstLoginActivity.this,DialogActivity.class);
         startActivityForResult(intent,0);
    }
    public void ifsuccess(String string){
        if(TAG_SUCCESS.equals(string)){
            System.out.println("Login----------success");
            Message msg = new Message();
            msg.what = 0x110;
            mhandler.sendMessage(msg);
        }
        if(TAG_FAIL.equals(string)){
            System.out.println("Login----------failed");
            Message msg = new Message();
            msg.what = 0x111;
            mhandler.sendMessage(msg);
        }
    }
    public void contactUs(View v){
       Intent intent = new Intent(FirstLoginActivity.this,ContactUsActivity.class);
        startActivity(intent);
    }
    public void checkforupdateClick(View v){
        System.out.println("check");
        if(FLAG_INTERNET_NOT_AVAILABLE){
            Message msg= new Message();
            msg.what = 0x001;
            mhandler.sendMessage(msg);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
               try{
                   URL url = new URL("http://www.hmu614.wang/checkforupdate");
                   HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                   conn.setRequestMethod("GET");
                   conn.connect();
                   InputStream inputStream = conn.getInputStream();
                   InputStreamReader isr = new InputStreamReader(inputStream);
                   BufferedReader reader = new BufferedReader(isr);
                   String line;
                   String result="";

                   while((line=reader.readLine())!=null){
                        result+=line;
                   }
                   System.out.println(result);
                   //联网检查更新
                   checkForUpdate(result);
               }catch (Exception e){

               }

            }
        }).start();
    }
    public void checkForUpdate(String LastestVersion){
        String currentVersion = getString(R.string.app_version);

        if(currentVersion.equals(LastestVersion)){
            Message msg = new Message();
            msg.what = 0x010;  //不需要更新
            mhandler.sendMessage(msg);
        }else{
            Message msg = new Message();
            msg.what = 0x011; //需要更新
            mhandler.sendMessage(msg);
        }
    }
    @Override
    public void onClick(View v) {
        if(FLAG_INTERNET_NOT_AVAILABLE){
            Message msg= new Message();
            msg.what = 0x001;
            mhandler.sendMessage(msg);
            return;
        }
        code = null;
        code = edt_stringyanzhengma.getText().toString().trim();
        loadingDialog = new LoadingDialog(FirstLoginActivity.this);
        loadingDialog.show();
        if ("".equals(code)){
            Toast.makeText(FirstLoginActivity.this, "验证码不应该为空！", Toast.LENGTH_LONG).show();
            return;
        }
        if(currentUserId!= null  && currentUserPsw!=null ){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet("http://jwc.hrbmu.edu.cn/bkjw/login?usercode="+currentUserId+"&password="+currentUserPsw+"&code="+code);

                        httpGet.setHeader("cookie","JSESSIONID="+COOKIE);
                        HttpResponse response = httpclient.execute(httpGet);
                        HttpEntity entity = response.getEntity();
                        StringBuilder result = new StringBuilder();
                        if (entity != null) {
                            System.out.println(getFilesDir());
                            InputStream instream = entity.getContent();
                            BufferedReader br = new BufferedReader(new InputStreamReader(instream));
                            String temp = "";
                            while ((temp = br.readLine()) != null) {
                                String str = new String(temp.getBytes(), "utf-8");
                                result.append(str);
                            }
                            instream.close();
                        }
                        /**
                         *  缺少一个有关于判断是否已连接到互联网的判断。  就是是否有网络连接 而不是全部去判断
                         *  返回的response是否与正常登陆页面不一致。
                         */
                        System.out.println("wocao!!!!!  "+result.toString());
                        String judgeString = result.toString();
                        judgeString = judgeString.substring(judgeString.indexOf("<title>"),judgeString.indexOf("</title>"));
                        System.out.println(judgeString);
                        ifsuccess(judgeString);
                    }catch(Exception e){
                        System.out.println(e);
                    }
                }
            }).start();
        }else{
            Toast.makeText(FirstLoginActivity.this, "用户名或者密码为空，请添加用户", Toast.LENGTH_LONG).show();
        }
    }
}
