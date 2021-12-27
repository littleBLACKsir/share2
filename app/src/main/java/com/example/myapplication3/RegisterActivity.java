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

public class RegisterActivity extends BaseActivity {

    private Button bt_Register;
    private EditText et_Account;
    private EditText et_Pwd;
    private EditText et_Pwd2;

    @Override
    protected int initLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        bt_Register = findViewById(R.id.bt_log);
        et_Account = findViewById(R.id.et_account);
        et_Pwd = findViewById(R.id.et_pwd);
        et_Pwd2 = findViewById(R.id.et_pwd2);
    }

    @Override
    protected void initData() {
        bt_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwd1 = et_Pwd.getText().toString();
                String pwd2 = et_Pwd2.getText().toString();
                if (pwd1.equals(pwd2)) {
                    Register(et_Account.getText().toString().trim(), et_Pwd.getText().toString().trim(), et_Pwd2.getText().toString().trim());
                    ShowToast("注册成功！");
                } else
                    ShowToast("两次密码不一致！");
            }
        });
    }

    public void Register(String Account,String psw,String psw2)
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
        else if(StringUtils.IsEmpty(psw2))
        {
            ShowToast("请确认密码！");
            return;
        }
        OkHttpClient okHttpClient=new OkHttpClient.Builder().build();
        HashMap m=new HashMap<String,Object>();
        m.put("mobile",Account);
        m.put("password",psw);
        FormBody formBody = new FormBody.Builder()
                .add("username", Account)
                .add("password", psw)
                .build();
        final Request request = new Request.Builder()
                .url("http://123.56.83.121:8080/register")//请求的url
                .post(formBody)
                .build();
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200) {
                    String res=response.body().string();
                    Gson gson=new Gson();
                    LoginResponse loginResponse=gson.fromJson(res,LoginResponse.class);
                    System.out.println(loginResponse);
                    if(loginResponse.getCode()==200)
                    {
                        navgateToWithFlag(LoginActivity.class,
                                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        ShowToastAsyn("注册成功！");

                    }
                    else
                    {
                        ShowToastAsyn("账号已存在，请重新注册！");
                    }

                }
                else
                {
                    ShowToastAsyn("注册失败！");
                }
            }
        });
    }
}