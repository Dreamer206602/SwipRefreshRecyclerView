package com.tq;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tq.adapter.RefreshAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.easydone.swiperefreshendless.EndlessRecyclerOnScrollListener;
import cn.easydone.swiperefreshendless.GridSpacingItemDecoration;
import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class GridActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.toolBar)
    Toolbar toolBar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private HeaderViewRecyclerAdapter stringAdapter;
    private GridLayoutManager gridLayoutManager;
    private List<String>mList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolBar);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fab.setVisibility(View.GONE);

        swipeRefreshLayout.setColorSchemeColors(
                Color.BLUE,Color.GREEN,Color.RED,Color.YELLOW
        );

        gridLayoutManager=new GridLayoutManager(this,2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //TODO ?????
        recyclerView.setHasFixedSize(true);//TODO
        recyclerView.setLayoutManager(gridLayoutManager);


        int spacingInpixels=26;
        //TODO   ?????
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,spacingInpixels,true,0));


        setData();
        RefreshAdapter refreshAdapter=new RefreshAdapter(mList,this);
        stringAdapter=new HeaderViewRecyclerAdapter(refreshAdapter);
        createLoadMoreView();
        recyclerView.setAdapter(stringAdapter);



        //TODO ？？？？？？
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return ((stringAdapter.getItemCount()-1)==position)?gridLayoutManager.getSpanCount():1;

            }
        });

        //上拉刷新
        swipeRefreshLayout.setOnRefreshListener(this);

        //下拉加载
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(gridLayoutManager) {
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
                        Toast.makeText(GridActivity.this,"Load finished",Toast.LENGTH_SHORT).show();
                        return null;
                    }
                }).subscribe();

    }

    //下拉加载数据
    private void loadMoreData() {

        List<String>moreList=new ArrayList<>();
        for (int i = 0; i <4 ; i++) {
            moreList.add("加载更多的数据");
        }
        mList.addAll(moreList);

    }

    //加载footer布局
    private void createLoadMoreView() {

        View loadMoreView= LayoutInflater.from(GridActivity.this)
                .inflate(R.layout.view_load_more,recyclerView,false);
        stringAdapter.addFooterView(loadMoreView);

    }

    private void setData() {
        mList=new ArrayList<>();
        for (int i = 0; i <40 ; i++) {
            mList.add("第"+i+"个");
        }
    }

    @Override
    public void onRefresh() {
            Observable.timer(2,TimeUnit.SECONDS,AndroidSchedulers.mainThread())
                    .map(new Func1<Long, Object>() {
                        @Override
                        public Object call(Long aLong) {
                            fetchingNewData();
                            swipeRefreshLayout.setRefreshing(false);
                            stringAdapter.notifyDataSetChanged();
                            Toast.makeText(GridActivity.this,"Refresh finished",Toast.LENGTH_SHORT).show();
                            return null;
                        }
                    }).subscribe();
    }

    private void fetchingNewData() {
        mList.add(0,"下拉刷新出来的数据");
        mList.add(0,"下拉刷新出来的数据");

    }
}
