package com.tq;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.tq.adapter.RefreshAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.easydone.swiperefreshendless.EndlessRecyclerOnScrollListener;
import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.toolBar)
    Toolbar toolBar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private List<String> mList;
    private HeaderViewRecyclerAdapter stringAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolBar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GridActivity.class));
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                Color.BLUE,
                Color.GREEN,
                Color.RED, Color.YELLOW);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        setDada();//初始化数据
        RefreshAdapter refreshAdapter = new RefreshAdapter(mList, this);
        stringAdapter = new HeaderViewRecyclerAdapter(refreshAdapter);
        recyclerView.setAdapter(stringAdapter);

        //加载footer布局
        createLoadMoreView();

        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                simulateLoadMoreData();
            }
        });

    }

    private void simulateLoadMoreData() {
        Observable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        loadMoreData();
                        stringAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Load Finished！", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }).subscribe();

    }

    /**
     * 下拉刷新，加载更多数据
     */
    private void loadMoreData() {

        List<String> moreList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            moreList.add("加载更多的数据");
        }
        mList.addAll(moreList);

    }

    /**
     * 加载footer布局
     */
    private void createLoadMoreView() {
        View loadMoreView = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.view_load_more, recyclerView, false);
        stringAdapter.addFooterView(loadMoreView);
    }

    private void setDada() {
        mList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mList.add("第" + i + "个");
        }

    }

    @Override
    public void onRefresh() {
        Observable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        fetchingNewData();
                        swipeRefreshLayout.setRefreshing(false);
                        stringAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Refresh finished", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }).subscribe();
    }

    private void fetchingNewData() {
        mList.add(0, "下拉刷新出来的数据");

    }
}
