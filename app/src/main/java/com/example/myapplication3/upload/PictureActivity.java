package com.example.myapplication3.upload;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication3.HomeActivity;
import com.example.myapplication3.LoginActivity;
import com.example.myapplication3.MainActivity;
import com.example.myapplication3.R;
import com.example.myapplication3.Utils.StringUtils;
import com.example.myapplication3.entity.LoginResponse;
import com.example.myapplication3.fragments.MyFragment;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PictureActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView photograph, album;
    private Intent intent;
    private final int CAMERA = 1;//事件枚举(可以自定义)
    private final int CHOOSE = 2;//事件枚举(可以自定义)
    private final String postUrl = "http://qingshanboke.com/Home/AndoridUploadFile";//接收上传图片的地址
    String photoPath = "";//要上传的图片路径
    private final int permissionCode = 100;//权限请求码
    private String Account;
    public Context mContext;
    public Button button_upload;
    public EditText pic_name;
    private Uri uri;
    //权限集合，对应在AndroidManifest.xml文件中添加配置
    //    <uses-permission android:name="android.permission.CAMERA" />
    //    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    //    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    //    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    //    <uses-permission android:name="android.permission.INTERNET"/>
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET
    };
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        mContext=this;
        //6.0才用动态权限
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }

//        photograph = findViewById(R.id.photograph);
        album = findViewById(R.id.album);
//        photograph.setOnClickListener(this);
        album.setOnClickListener(this);
        Account=GetStringFromSP("username");
        button_upload=findViewById(R.id.bottom_upload);
        button_upload.setOnClickListener(this);
        pic_name=findViewById(R.id.EditTitle);
        button_upload.setEnabled(false);
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
    public void onClick(View v) {
        switch (v.getId()) {

//            //拍照按钮事件
//            case R.id.photograph:
//                //方法一：这样拍照只能取到缩略图（不清晰）
//                intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, CAMERA);
//
//
//                //方法二：指定加载路径图片路径（保存原图，清晰）
//                String SD_PATH = Environment.getExternalStorageDirectory().getPath() + "/拍照上传示例/";
//                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//                String fileName = format.format(new Date(System.currentTimeMillis())) + ".JPEG";
//                photoPath = SD_PATH + fileName;
//                File file = new File(photoPath);
//                if (!file.getParentFile().exists()) {
//                    file.getParentFile().mkdirs();
//                }
//
//                //兼容7.0以上的版本
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    try {
//                        ContentValues values = new ContentValues(1);
//                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
//                        values.put(MediaStore.Images.Media.DATA, photoPath);
//                        Uri tempuri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                        if (tempuri != null) {
//                            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempuri);
//                            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//                        }
//                        startActivityForResult(intent, CAMERA);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    Uri uri = Uri.fromFile(file);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri); //指定拍照后的存储路径，保存原图
//                    startActivityForResult(intent, CAMERA);
//                }
//                break;

            //选择按钮事件
            case R.id.album:
                //打开系统的选择图片界面，ACTION_PICK是系统变量
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, CHOOSE);
                break;


            case R.id.bottom_upload:
                String name=pic_name.getText().toString();
                if(StringUtils.IsEmpty(name))
                {
                    ShowToast("请输入标题！");
                    return;
                }
                button_upload.setEnabled(false);
                if(uri==null)
                {
                    return;
                }
                photoPath = PathHelper.getRealPathFromUri(PictureActivity.this, uri);
                File file = new File(photoPath);
                if (file.exists()) {

                    final RequestBody requestBody= RequestBody.create(MediaType.parse("image/jpg"),file);
                    final MultipartBody multipartBody=new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("uploadFile",file.getName(),requestBody)
                            .addFormDataPart("username",Account)
                            .addFormDataPart("picturename",name)
                            .build();
                    OkHttpClient okHttpClient=new OkHttpClient.Builder().build();
                    final Request request = new Request.Builder()
                            .url("http://123.56.83.121:8080/upload")//请求的url
                            .post(multipartBody)
                            .build();
                    Call call = okHttpClient.newCall(request);
                    //加入队列 异步操作
                    call.enqueue(new Callback() {
                        //请求错误回调方法
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            System.out.println("上传：连接服务器失败");
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
                                    runOnUiThread(() -> {
                                        ShowToast("上传成功！");
                                        finish();
                                    });
                                }
                                else
                                {
                                    runOnUiThread(() -> ShowToast("上传失败！code:"+loginResponse.getCode()));
                                }
                            }
                            else
                            {
                                runOnUiThread(() -> ShowToast("上传失败！"));
                            }
                        }
                    });
                }
                break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//requestcode用于鉴别哪一次跳转
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
//            // 调用照相机拍照
//            case CAMERA:
//                if (resultCode == RESULT_OK) {
//                    //对应方法一：图片未保存，需保存文件到本地
//                    Bundle bundle = data.getExtras();
//                    Bitmap bitmap = (Bitmap) bundle.get("data");
//                    String savePath;
//                    String SD_PATH = Environment.getExternalStorageDirectory().getPath() + "/拍照上传示例/";
//                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//                    String fileName = format.format(new Date(System.currentTimeMillis())) + ".JPEG";
//                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                        savePath = SD_PATH;
//                    } else {
//                        Toast.makeText(PictureActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    photoPath = savePath + fileName;
//                    File file = new File(photoPath);
//                    try {
//                        if (!file.exists()) {
//                            file.getParentFile().mkdirs();
//                            file.createNewFile();
//                        }
//                        FileOutputStream stream = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                        Toast.makeText(PictureActivity.this, "保存成功,位置:" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    //对应方法二：图片已保存，只需读取就行了
//                    try {
//                        FileInputStream stream = new FileInputStream(photoPath);
//                        Bitmap bitmaps = BitmapFactory.decodeStream(stream);
//
//                        //预览图片
//                        ImageView image = findViewById(R.id.imageView);
//                        image.setImageBitmap(bitmaps);
//
//                        //上传图片（Android 4.0 之后不能在主线程中请求HTTP请求）
//                        File files = new File(photoPath);
//                        if (files.exists()) {
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //文本字段（用于验证用户身份）
//                                    HashMap<String, String> form = new HashMap<String, String>();
//                                    form.put("username", "zhangqs");
//                                    form.put("password", "123456");
//
//                                    //图片字段
//                                    HashMap<String, String> files = new HashMap<String, String>();
//                                    files.put(PathHelper.getFileNameFromPath(photoPath), photoPath);
//                                    formUpload(postUrl, form, files);
//                                }
//                            }).start();
//                        }
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
            // 选择图片库的图片
            case CHOOSE:
                if (resultCode == RESULT_OK) {

                        uri = data.getData();//系统选择图片界面返回的路径
                        photoPath = PathHelper.getRealPathFromUri(PictureActivity.this, uri);//转换成绝对路径
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);//通过路径获取到图片
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                        //压缩图片
                        bitmap = scaleBitmap(bitmap, (float) 0.5);

                        //预览图片
                        ImageView image = findViewById(R.id.imageView);
                        image.setImageBitmap(bitmap);
                        button_upload.setEnabled(true);
                }
                break;
        }
    }

    //压缩图片
    public Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        return newBM;
    }

    //POST 表单提交
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formUpload(String posturl, Map<String, String> textMap, Map<String, String> fileMap) {
        String res = "";
        HttpURLConnection conn = null;
        String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符
        try {
            URL url = new URL(posturl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            OutputStream out = new DataOutputStream(conn.getOutputStream());

            // text
            if (textMap != null) {
                StringBuffer buffer = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    buffer.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    buffer.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
                    buffer.append(inputValue);
                }
                out.write(buffer.toString().getBytes());
            }

            // file
            if (fileMap != null) {
                Iterator iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    File file = new File(inputValue);
                    String filename = file.getName();
                    String contentType = "";
                    if (filename.endsWith(".jpg")) {
                        contentType = "image/jpg";
                    } else if (filename.endsWith(".png")) {
                        contentType = "image/png";
                    } else if (contentType == null || contentType.equals("")) {
                        contentType = "application/octet-stream";
                    }

                    StringBuffer buffer = new StringBuffer();
                    buffer.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    buffer.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");
                    buffer.append("Content-Type:" + contentType + "\r\n\r\n");

                    out.write(buffer.toString().getBytes());

                    DataInputStream in = new DataInputStream(new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }
            }

            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // 读取返回数据
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            res = buffer.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            System.out.println("发送POST请求出错。" + posturl);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }
    protected String GetStringFromSP(String key)
    {
        SharedPreferences sp= getSharedPreferences("sp_sjj", MODE_PRIVATE);
        return sp.getString(key,"");
    }
    public void ShowToast(String msg)
    {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

}
