package com.example.myapplication3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    public Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        setContentView(initLayout());
        initView();
        initData();
    }
    protected abstract int initLayout();
    protected abstract void initView();
    protected abstract void initData();
    public void ShowToast(String msg)
    {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
    //跳转
    public void navgateTo(Class cls)
    {
        Intent it=new Intent(mContext,cls);
        startActivity(it);
    }
    //跳转杀死前面的
    public void navgateToWithFlag(Class cls,int flags)
    {
        Intent it=new Intent(mContext,cls);
        it.setFlags(flags);
        startActivity(it);
    }
    //
    public void ShowToastAsyn(String msg)
    {
        Looper.prepare();
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
    protected void SaveToSP(String key,String val)
    {
        SharedPreferences sp=getSharedPreferences("sp_sjj",MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key,val);
        editor.commit();
    }
    protected String GetStringFromSP(String key)
    {
        SharedPreferences sp= getSharedPreferences("sp_sjj", MODE_PRIVATE);
        return sp.getString(key,"");
    }
}
