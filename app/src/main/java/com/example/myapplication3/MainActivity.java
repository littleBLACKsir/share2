package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication3.Utils.StringUtils;
import com.example.myapplication3.fragments.MyFragment;
import com.example.myapplication3.upload.PictureActivity;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private TextView mainText;
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET};
    AlertDialog alertDialog;
    private final int permissionCode = 100;//权限请求码
    @Override
    //加载布局
    protected int initLayout() {
        return R.layout.activity_main;
        //6.0才用动态权限

    }
    //检查权限
    private void checkPermission() {
        List<String> permissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permissions[i]);
            }
        }
        if (permissionList.size() <= 0) {
            //说明权限都已经通过，可以做你想做的事情去

        } else {
            //存在未允许的权限
            ActivityCompat.requestPermissions(this, permissions, permissionCode);
        }
    }

    //授权后回调函数
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean haspermission = false;
        if (permissionCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    haspermission = true;
                }
            }
            if (haspermission) {
                //跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
                permissionDialog();
            } else {
                //全部权限通过，可以进行下一步操作
            }
        }
    }

    //打开手动设置应用权限
    private void permissionDialog() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle("提示信息")
                    .setMessage("当前应用缺少必要权限，该功能暂时无法使用。如若需要，请单击【确定】按钮前往设置中心进行权限授权。")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                            Uri packageURI = Uri.parse("package:" + getPackageName());
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                        }
                    })
                    .create();
        }
        alertDialog.show();
    }

    //用户取消授权
    private void cancelPermissionDialog() {
        alertDialog.cancel();
    }
    @Override
    //找到组件
    protected void initView() {
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }
        mainText = findViewById(R.id.main_text);
        Typeface fontFace = Typeface.createFromAsset(getAssets(),
                "hkww.ttf");
        mainText.setTypeface(fontFace);
    }

    @Override
    //绑定事件
    protected void initData() {
        String token = GetStringFromSP("token");
        Integer time = 2000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navgateTo(LoginActivity.class);
                finish();
            }
        }, time);


    }
}