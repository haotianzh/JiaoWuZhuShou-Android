package com.haotian.loginapp;

import android.content.Intent;
import android.os.Looper;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.sql.SQLOutput;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    private final static String TAG="zhtLoginAPP";
    private String COOKIE;
    private String DOMAIN;
    private String NAME;
    private RadioGroup radioGroup;
    private WebView webView;
    private LinearLayout linearLayout;
    private ImageView imageView;
    private TextView textView;
    private ProgressBar progressBar;
    private String testBug;
    /**
     *  具体实现的功能有： 查询成绩 查询课表 查询选课
     *  webview 上添加 setJAVASCRIPTENABLE
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // MyApplication ap = MyApplication.getInstance();
      //  ap.addActivity(this);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        COOKIE = intent.getStringExtra("COOKIE");
        DOMAIN = intent.getStringExtra("DOMAIN");
        NAME = intent.getStringExtra("NAME");
        System.out.println(COOKIE);
        System.out.println(DOMAIN);
        System.out.println(NAME);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(this);
        webView = (WebView) findViewById(R.id.mwebview);
        linearLayout = (LinearLayout) findViewById(R.id.mycontentlinearlayout);
        textView = (TextView) findViewById(R.id.mycontentTextView);
        imageView = (ImageView) findViewById(R.id.mycontentImageView);
        progressBar = (ProgressBar) findViewById(R.id.mprogressbar);


    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
          switch (checkedId){
              case R.id.kebiao:
                  getKebiao();
                  break;
              case R.id.chengji:
                  getChengji();
                  break;
              case R.id.xuanke:
                  getXuanke();
                  break;
              case R.id.cet:
                  getCet();
                  break;

              default:
                  break;
          }
    }
    public void showWebView(){
        webView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
    }
    public void getKebiao(){
        showWebView();
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        String cookiestring = NAME+"="+COOKIE+";domain="+DOMAIN;
        cookieManager.setCookie("http://jwc.hrbmu.edu.cn/bkjw/kbcx/queryXszkb?xnxq=2015-20162",cookiestring);
        CookieSyncManager.getInstance().sync();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100){
                       progressBar.setVisibility(View.GONE);
                }else{
                    if (View.GONE == progressBar.getVisibility()) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                      progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        webView.loadUrl("http://jwc.hrbmu.edu.cn/bkjw/kbcx/queryXszkb?xnxq=2015-20162");
    }
    public void getChengji(){
        showWebView();
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        String cookiestring = NAME+"="+COOKIE+";domain="+DOMAIN;
        cookieManager.setCookie("http://jwc.hrbmu.edu.cn/bkjw/cjcx/queryQmcj?pageXnxq=2015-20162",cookiestring);
        CookieSyncManager.getInstance().sync();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
               // webView.scrollTo(0,1000);
                webView.pageDown(true);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                }else{
                    if (View.GONE == progressBar.getVisibility()) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        webView.loadUrl("http://jwc.hrbmu.edu.cn/bkjw/cjcx/queryQmcj?pageXnxq=2015-20162");
    }
    public void getXuanke(){
        showWebView();
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        String cookiestring = NAME+"="+COOKIE+";domain="+DOMAIN;
        cookieManager.setCookie("http://jwc.hrbmu.edu.cn/bkjw/xsxk/queryYxkc?pageXklb=xx&pageXnxq=2016-20171",cookiestring);
        CookieSyncManager.getInstance().sync();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
               // webView.scrollTo(0,1000);
                webView.pageDown(true);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                }else{
                    if (View.GONE == progressBar.getVisibility()) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        webView.loadUrl("http://jwc.hrbmu.edu.cn/bkjw/xsxk/queryYxkc?pageXklb=xx&pageXnxq=2016-20171");
    }
    public void getCet(){
//         new AlertDialog.Builder(this)
//                  .setTitle("Sorry")
//                  .setMessage("本功能还未开放,敬请期待！")
//                  .setPositiveButton("确定",null)
//                 .show();
      new Thread(new Runnable() {
          @Override
          public void run() {
              Looper.prepare();
              try {

                  HttpClient httpclient = new DefaultHttpClient();
                  HttpGet httpGet = new HttpGet("http://jwc.hrbmu.edu.cn/bkjw/cjcx/queryQmcj?ageXnxq=&pageBkcxbj=&pageSfjg=1&pageKcmc=");
                  String cookiestring = NAME+"="+COOKIE;
                  httpGet.addHeader("cookie",cookiestring);
                  HttpResponse httpResponse = httpclient.execute(httpGet);
                  String strResult= EntityUtils.toString(httpResponse.getEntity());
                  Log.i("zhtLoginAPP", "getCet: "+strResult);
                  System.out.println(strResult);
                  String pageCount = strResult.split("<input type=\"hidden\" id=\"pageCount\" name=\"pageCount\" value=\"")[1].split("\"")[0];
                  Log.i(TAG, "run: "+pageCount);
                  String[] course ;

                  String[] mCourses = new String[50];
                  Double[] mCoursesScore = new Double[50];
                  int index = 0;
                  for (int i = 0;i<50;i++){
                       mCourses[i] = "";
                       mCoursesScore[i] = 0.0;
                  }

                  for (int page =1;page<=Integer.valueOf(pageCount);page++){
                      httpGet = new HttpGet("http://jwc.hrbmu.edu.cn/bkjw/cjcx/queryQmcj?pageCount="+pageCount+"&pageXnxq=&pageBkcxbj=&pageSfjg=1&pageKcmc=&pageSize=20&pageNo="+page);
                      httpGet.addHeader("cookie",cookiestring);
                      httpResponse = httpclient.execute(httpGet);
                      strResult= EntityUtils.toString(httpResponse.getEntity());
                      course = strResult.split("<td>选修</td>");
                      int length = course.length;
                      String[] strs;
                      for (int i=0;i<(length-1);i++){
                          index ++;
                          strs = course[i].split("<td>");
                          mCourses[index] = strs[strs.length-1].split("</td>")[0];
                          Log.i(TAG, "run: "+mCourses[index]);
                          strs = course[i+1].split("</td>");
                          mCoursesScore[index] = Double.valueOf(strs[1].split("<td>")[1]);
                          Log.i(TAG, "run: "+mCoursesScore[index]);
                      }

                  }
                  String content = "";
                  double total = 0.0;
                  for (int i=0;i<index;i++){
                             String string = mCourses[i+1];
                             int long1 = 10 - string.length();
                           Log.i(TAG, "run: long1"+long1);
                             for (int j = 0;j< (long1);j++){
                                 string += "    ";
                             }

                      content += string + ":" +mCoursesScore[i+1]+"\n";
                      total += mCoursesScore[i+1];
                  }
                  content += String.format("%-20s","选修课总学分为") + ":" + total + "分";
                  new AlertDialog.Builder(MainActivity.this)
                          .setTitle("结果")
                          .setMessage(content)
                          .setPositiveButton("确定",null)
                          .show();
                  Looper.loop();

              } catch (Exception e) {
                  Log.i("zhtloginapp", "getCet: "+e);
                  e.printStackTrace();
              }
          }
      }).start();


    }


}
