package com.example.myapplication3.fragments;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.LoginActivity;
import com.example.myapplication3.R;
import com.example.myapplication3.adapter.PictureAdapter;
import com.example.myapplication3.entity.PictureEntity;
import com.example.myapplication3.entity.ResultResponse;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private PictureAdapter newsAdapter;
    private List<PictureEntity> datas = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private String username;
    private String token;
    private int pageNum = 1;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }


    @Override
    protected int initLayout() {
        return R.layout.fragment_image;
    }

    @Override
    protected void initView() {
        recyclerView=mRootView.findViewById(R.id.recycleView);
        refreshLayout=mRootView.findViewById(R.id.refreshLayout);
        token=GetStringFromSP("token");
        username=GetStringFromSP("username");
    }

    @Override
    protected void initData() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        newsAdapter = new PictureAdapter(getActivity());
        recyclerView.setAdapter(newsAdapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNum = 1;
                getNewsList(true);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                pageNum++;
                getNewsList(false);
            }
        });
        getNewsList(true);
    }


    private void getNewsList(final boolean isRefresh) {
        OkHttpClient okHttpClient=new OkHttpClient.Builder().build();
        FormBody formBody = new FormBody.Builder()
                .add("page", Integer.toString(pageNum))
                .add("pageSize", "3")
                .add("username",username)
                .add("token",token)
                .build();
        final Request request = new Request.Builder()
                .url("http://123.56.83.121:8080/allpicturesbypage")//请求的url
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

                    String res= response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isRefresh) {
                                refreshLayout.finishRefresh(true);
                            } else {
                                refreshLayout.finishLoadMore(true);
                            }
                            ResultResponse resresponse = new Gson().fromJson(res, ResultResponse.class);
                            if(resresponse!=null&&resresponse.getCode()==401)
                            {
                                RemoveStringFromSP();
                                navgateToWithFlag(LoginActivity.class,
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            System.out.println(resresponse);
                            if (resresponse != null && resresponse.getCode() == 200) {
                                List<PictureEntity> list = resresponse.getDatas();
                                if (list != null && list.size() > 0) {
                                    if (isRefresh) {
                                        datas = list;
                                    } else {
                                        datas.addAll(list);
                                    }
                                    newsAdapter.setDatas(datas);
                                    newsAdapter.notifyDataSetChanged();

                                }
                                if (isRefresh) {
                                    ShowToast("暂时无数据");
                                } else {
                                    ShowToast("没有更多数据");
                                }
                            }
                        }
                    });
                }

        });
    }
}