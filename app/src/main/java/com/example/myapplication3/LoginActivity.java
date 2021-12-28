package com.example.myapplication3;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication3.Utils.StringUtils;
import com.example.myapplication3.entity.LoginResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {
    private Button bt_Login;
    private EditText et_Account;
    private EditText et_Pwd;
    private Button bt_register;

    @Override
    protected int initLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        bt_Login=findViewById(R.id.bt_log);
        et_Account=findViewById(R.id.et_account);
        et_Pwd=findViewById(R.id.et_pwd);
        bt_register = findViewById(R.id.bt_register);
    }

    @Override
    protected void initData() {
        bt_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login(et_Account.getText().toString().trim(),et_Pwd.getText().toString().trim());
            }
        });
        bt_register.setOnClickListener(view -> {
            navgateTo(RegisterActivity.class);
        });
    }

    public void Login(String Account,String psw)
    {
        if(StringUtils.IsEmpty(Account))
        {
            ShowToast("请输入账号！");
            return;
        }
        else if(StringUtils.IsEmpty(psw))
        {
            ShowToast("请输入密码！");
            return;
        }
        //创建OKhttp的客户端，通过builder创建
        OkHttpClient okHttpClient=new OkHttpClient.Builder().build();
        HashMap m=new HashMap<String,Object>();
        m.put("username",Account);
        m.put("password",psw);
        //构建body
        FormBody formBody = new FormBody.Builder()
                .add("username", Account)
                .add("password", psw)
                .build();
        //封装
        final Request request = new Request.Builder()
                .url("http://123.56.83.121:8080/login")//请求的url
                .post(formBody)
                .build();
        //发送
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {    //服务器返回结果
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200) {   //返回状态码正确才有效
                    String res=response.body().string();
                    Gson gson=new Gson();
                    LoginResponse loginResponse=gson.fromJson(res,LoginResponse.class);//解析json
                    System.out.println(loginResponse);
                    if(loginResponse.getCode()==200)
                    {
                        String token=loginResponse.getToken();
                        SaveToSP("token",token);
                        SaveToSP("username",Account);
                        navgateTo(HomeActivity.class);
                        ShowToastAsyn("登陆成功！");
                        finish();
                    }
                    else
                    {
                        ShowToastAsyn("账号或者密码错误！");
                    }

                }
            }
        });
    }
}