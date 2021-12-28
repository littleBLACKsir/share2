package com.example.myapplication3.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dueeeke.videocontroller.component.PrepareView;
import com.example.myapplication3.LoginActivity;
import com.example.myapplication3.R;
import com.example.myapplication3.entity.DownloadResponse;
import com.example.myapplication3.entity.PictureEntity;
import com.example.myapplication3.entity.ResultResponse;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PictureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<PictureEntity> datas;
    private static final int SAVE_SUCCESS = 0;//保存图片成功
    private static final int SAVE_FAILURE = 1;//保存图片失败
    private static final int SAVE_BEGIN = 2;//开始保存图片
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SAVE_BEGIN:
                    ShowToast("开始保存，请稍后......");
                    break;
                case SAVE_SUCCESS:
                    ShowToast("图片保存成功,请到相册查找");
                    break;
                case SAVE_FAILURE:
                    ShowToast("图片保存失败,请稍后再试...");
                    break;
            }
        }
    };

    private SharedPreferences getLikeSp;
    private SharedPreferences downLoadSp;

    public void setDatas(List<PictureEntity> datas) {
        this.datas = datas;
    }
    public PictureAdapter(Context context) {
        this.mContext = context;
        getLikeSp = mContext.getSharedPreferences("getlike", MODE_PRIVATE);
        downLoadSp = mContext.getSharedPreferences("download", MODE_PRIVATE);
    }

    @NonNull
    @Override
    //创建item，展示图片
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_picture, parent, false);
        PictureHolder viewHolder = new PictureHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,int position)
    {
        PictureHolder vh = (PictureHolder) holder;
        PictureEntity PictureEntity = datas.get(position);
        System.out.println(PictureEntity);
        vh.tvTitle.setText(PictureEntity.getPicturename());
        vh.tvAuthor.setText(PictureEntity.getUsername());
        vh.tvDianzan.setText(String.valueOf(PictureEntity.getLiked()));
        boolean islike=PictureEntity.isCollect();   //从后端获取数据，是否点赞的状态
        System.out.println(String.valueOf(position)+islike+"");
        //请求之后绑定的状态
        if(islike)//设置点赞的效果
        {
            vh.image_Dianzan.setImageResource(R.mipmap.dianzan_select);
        }
        else
        {
            vh.image_Dianzan.setImageResource(R.mipmap.dianzan);
        }
        vh.islikeflag=islike;
        vh.username=GetStringFromSP("username");
        vh.token=GetStringFromSP("token");
        vh.id=PictureEntity.getPid();
        Picasso.with(mContext)
                .load(PictureEntity.getUrl())
                .into(vh.Cover);

    }

    @Override
    public int getItemCount() {
        if (datas != null && datas.size() > 0) {
            return datas.size();   //返回item的值
        } else
            return 0;

    }

    public class PictureHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvAuthor;
        private TextView tvDianzan;
        private TextView tvDownload;
        private TextView tvCollect;
        private ImageView img_header;
        public ImageView Cover;
        public int mPosition;
        public PrepareView mPrepareView;
        public FrameLayout mPlayerContainer;
        private ImageView image_Dianzan;
        private ImageView image_Download;
        private boolean islikeflag;
        private  String username;
        private String token;
        private int id;
        public PictureHolder(@NonNull View view) {
            super(view);
            tvTitle = view.findViewById(R.id.title);
            tvAuthor = view.findViewById(R.id.author);
            tvDianzan = view.findViewById(R.id.dianzan);
//            tvCollect = view.findViewById(R.id.collect);
            tvDownload = view.findViewById(R.id.download);
            img_header = view.findViewById(R.id.img_header);
            Cover = view.findViewById(R.id.img_cover);
            image_Dianzan=view.findViewById(R.id.img_dianzan);
            image_Download=view.findViewById(R.id.img_download);
            image_Dianzan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int dianzancount=Integer.parseInt(tvDianzan.getText().toString());
                    int totalNum = getLikeSp.getInt("num", 0);
                    SharedPreferences.Editor editor = getLikeSp.edit();
                    //前端中点击按钮的状态绑定
                    if(islikeflag)
                    {
                        SubDianzan(id,username,token);
                        image_Dianzan.setImageResource(R.mipmap.dianzan);
                        tvDianzan.setText(String.valueOf(--dianzancount));
                        editor.putInt("num", --totalNum);
                    }
                    else
                    {
                        AddDianzan(id,username,token);
                        image_Dianzan.setImageResource(R.mipmap.dianzan_select);
                        tvDianzan.setText(String.valueOf(++dianzancount));
                        editor.putInt("num", ++totalNum);
                    }
                    islikeflag=!islikeflag;
                    editor.apply();
                }
            });
            //下载按钮
            image_Download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int downloadNum = Integer.parseInt(tvDownload.getText().toString());
                    tvDownload.setText(String.valueOf(++downloadNum));
                    GetPictureUrl(id,username,token);
                }
            });
        }

    }
    public void AddDianzan(int id,String username,String token)
    {
        OkHttpClient okHttpClient=new OkHttpClient.Builder().build();
        FormBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("token", token)
                .add("id",Integer.toString(id))
                .build();
        final Request request = new Request.Builder()
                .url("http://123.56.83.121:8080/adddianzan")//请求的url
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
                    ResultResponse resultResponse=gson.fromJson(res,ResultResponse.class);
                    if(resultResponse.getCode()==200)
                    {
                        System.out.println("点赞成功！");
                    }
                    else if(resultResponse.getCode()==401)
                    {
                        RemoveStringFromSP();
                        navgateToWithFlag(LoginActivity.class,
                                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    }

                }
            }
        });
    }
    public void GetPictureUrl(int id,String username,String token)
    {
        int totalNum = downLoadSp.getInt("num", 0);
        SharedPreferences.Editor editor = downLoadSp.edit();
        editor.putInt("num", ++totalNum);
        editor.apply();

        OkHttpClient okHttpClient=new OkHttpClient.Builder().build();
        FormBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("token", token)
                .add("id",Integer.toString(id))
                .build();
        final Request request = new Request.Builder()
                .url("http://123.56.83.121:8080/download")//请求的url
                .post(formBody)
                .build();
        //请求服务器响应
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
                    DownloadResponse resultResponse=gson.fromJson(res,DownloadResponse.class);
                    if(resultResponse.getCode()==200)  //拿到状态码
                    {
                        System.out.println("开始下载");
                        System.out.println(resultResponse.getData());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mHandler.obtainMessage(SAVE_BEGIN).sendToTarget();
                                Bitmap bp = returnBitmap(resultResponse.getData());  //获取图片下载地址
                                saveImageToPhotos(mContext, bp);
                            }
                        }).start();

                    }
                    else if(resultResponse.getCode()==401)
                    {
                        RemoveStringFromSP();
                        navgateToWithFlag(LoginActivity.class,
                                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    }

                }
            }
        });
    }
    public void SubDianzan(int id,String username,String token)
    {
        OkHttpClient okHttpClient=new OkHttpClient.Builder().build();
        FormBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("token", token)
                .add("id",Integer.toString(id))
                .build();
        final Request request = new Request.Builder()
                .url("http://123.56.83.121:8080/subdianzan")//请求的url
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
                    ResultResponse resultResponse=gson.fromJson(res,ResultResponse.class);
                    if(resultResponse.getCode()==200)
                    {
                        System.out.println("取消点赞成功！");
                    }
                    else if(resultResponse.getCode()==401)
                    {
                        RemoveStringFromSP();
                        navgateToWithFlag(LoginActivity.class,
                                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    }

                }
            }
        });
    }
    public void navgateToWithFlag(Class cls,int flags)
    {
        Intent it=new Intent(mContext,cls);
        it.setFlags(flags);
        mContext.startActivity(it);
    }
    public void ShowToast(String msg)
    {

        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

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
        SharedPreferences sp=mContext.getSharedPreferences("sp_sjj",MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key,val);
        editor.commit();
    }
    protected String GetStringFromSP(String key)
    {
        SharedPreferences sp= mContext.getSharedPreferences("sp_sjj", MODE_PRIVATE);
        return sp.getString(key,"");
    }
    protected  void RemoveStringFromSP()
    {
        SharedPreferences sp= mContext.getSharedPreferences("sp_sjj", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.clear();
        editor.commit();
    }
    private void saveImageToPhotos(Context context, Bitmap bitmap) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
           ShowToast("保存失败,没有读写sd卡权限");
        }
        // 首先保存图片
//        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Boohee");
//        if (!appDir.exists()) {
//            appDir.mkdir();
//        }
        String fileName = System.currentTimeMillis() + ".jpg";
//        File file = new File(appDir, fileName);
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, fileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        OutputStream outputStream;
        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.obtainMessage(SAVE_FAILURE).sendToTarget();
            return;
        }

        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    file.getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//
//            return;
//        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));
        mHandler.obtainMessage(SAVE_SUCCESS).sendToTarget();

    }

    /**
     * 通过地址下载图片
     *
     * @param url
     * @return bitmap type
     */
    public static Bitmap returnBitmap(String url) {
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(url);
            HttpURLConnection conn;
            conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}

