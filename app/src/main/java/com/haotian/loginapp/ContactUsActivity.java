package com.haotian.loginapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by HAOTIAN on 2016/3/13.
 */
public class ContactUsActivity extends Activity {
    private final static int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private final static String TAG_OURCONTACTINFO = "APP仅是提供一个查询的快捷手段，并无其他功能" +
            "\n" +
            "软件处于测试阶段，可能有很多BUG" +
            "\n" +
            "希望大家能及时反馈，谢谢！！" +
            "\n" +
            "邮箱：perryre@163.com";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);
        textView = (TextView) findViewById(R.id.ourContactInfoText);
        textView.setText(TAG_OURCONTACTINFO);
    }

    public void ensureClick(View v) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            callPhone();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone();
            } else {
                // Permission Denied
                Toast.makeText(ContactUsActivity.this, "没有获取到手机拨打电话的权限", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void callPhone() {
        String phonenumber = getString(R.string.phone_number);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phonenumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    public void docancelClick(View v){
          finish();
    }
}


