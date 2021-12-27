package com.example.myapplication3.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
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
    }

    @Override
    protected void initData() {
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
    }
}