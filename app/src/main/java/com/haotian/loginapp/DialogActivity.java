package com.haotian.loginapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.haotian.setdbtblp.TblP;

import org.xutils.DbManager;
import org.xutils.x;

/**
 * Created by HAOTIAN on 2016/3/12.
 */
public class DialogActivity extends Activity {
    private EditText edt_usercode;
    private EditText edt_password;
    private Button btn_OK;
    private Button btn_Cancel;
    private String str_usercode;
    private String str_password;
    private int result = -1;
    private DbManager.DaoConfig dao;
    private DbManager dbxu3;
    private TblP tblP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogactivity);
        edt_password = (EditText) findViewById(R.id.passwordinput);
        edt_usercode = (EditText) findViewById(R.id.usercodeinput);
        tblP = new TblP();

        dao=new DbManager
                .DaoConfig()
                .setDbName("dbUser2")
                //.setDbDir()
                .setDbVersion(1);
        dbxu3 = x.getDb(dao);
    }

    public void okClick(View v){
        if (ifBlank()>0){
            tblP.setUsercode(str_usercode);
            tblP.setPassword(str_password);
            try {
                dbxu3.save(tblP);
                result = 1;
            }catch (Exception e){
                result = -1;
            }finally {
                showResult(result);
            }
        }
        finish();
    }

    private void showResult(int result) {
        if (result == 1){
            Toast.makeText(this,"成功添加用户",Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(this,"添加用户失败",Toast.LENGTH_LONG).show();
        }

    }

    public void cancelClick(View v){

        finish();
    }
    private  int ifBlank(){
        str_usercode = edt_usercode.getText().toString().trim();
        str_password = edt_password.getText().toString().trim();
        if ("".equals(str_usercode)||"".equals(str_password)){

            result = -1;
        }else
            result = 1;

        return result;
    }
}
