package com.example.myapplication3.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication3.LoginActivity;
import com.example.myapplication3.MainActivity;
import com.example.myapplication3.R;
import com.example.myapplication3.upload.PictureActivity;


public class MyFragment extends BaseFragment {

    private RelativeLayout upload;
    private RelativeLayout logout;
    private String username;
    private TextView text_username;
    private TextView tv_get_like_num;
    private EditText text_signature;
    private SharedPreferences sp;

    public static MyFragment newInstance() {
        return new MyFragment();
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView() {
        upload = mRootView.findViewById(R.id.rl_upload);
        logout=mRootView.findViewById(R.id.rl_logout);
        text_username=mRootView.findViewById(R.id.text_username);
        username=GetStringFromSP("username");
        tv_get_like_num=mRootView.findViewById(R.id.tv_get_like_num);
        text_signature=mRootView.findViewById(R.id.text_signature);
    }

    @Override
    protected void initData() {
        sp = getContext().getSharedPreferences("getlike", MODE_PRIVATE);
        upload.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            navgateTo(PictureActivity.class);
                                        }
                                    }
        );
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoveStringFromSP();
                navgateToWithFlag(LoginActivity.class,
                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        });
        text_username.setText(username);
        text_signature.setOnKeyListener((view, keyCode, keyEvent) -> {
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                /*隐藏软键盘*/
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputMethodManager.isActive()){
                    inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                }
                text_signature.clearFocus();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        int totalNum = sp.getInt("num", 0);
        tv_get_like_num.setText(String.valueOf(totalNum));
    }
}