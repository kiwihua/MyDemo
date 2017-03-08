package com.ohoo.mydemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ohoo.mydemo.R;
import com.ohoo.mydemo.adapter.NewsAdapter;
import com.ohoo.mydemo.bean.NewsList;
import com.ohoo.mydemo.net.GsonRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private RequestQueue mQueue;

    private RecyclerView mRvNewsList;
    private NewsAdapter mAdapter;
    private List<NewsList.NewslistBean> mNewsList;

    private Button mBtnTop;
    private Button mBtnPartRefresh;

    private int mRefreshNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueue = Volley.newRequestQueue(this);
        initView();

        loadNewsData();


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            //用于设置拖拽和滑动的方向
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags=0,swipeFlags=0;
                if(recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager ||recyclerView.getLayoutManager() instanceof GridLayoutManager){
                    //网格式布局有4个方向
                    dragFlags=ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
                }else if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
                    //线性式布局有2个方向
                    dragFlags=ItemTouchHelper.UP|ItemTouchHelper.DOWN;

                    swipeFlags = ItemTouchHelper.START|ItemTouchHelper.END; //设置侧滑方向为从两个方向都可以
                }
                return makeMovementFlags(dragFlags,swipeFlags);//swipeFlags 为0的话item不滑动
            }

            //长摁item拖拽时回调
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int from=viewHolder.getAdapterPosition();
                int to=target.getAdapterPosition();
                Collections.swap(mNewsList,from,to);//交换数据链表中数据的位置
                mAdapter.notifyItemMoved(from,to);//更新适配器中item的位置
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //这里处理滑动删除
                mNewsList.remove(viewHolder.getAdapterPosition());
                mAdapter.notifyItemRemoved(viewHolder.getLayoutPosition());
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;//返回true则为所有item都设置可以拖拽
            }
        });

        itemTouchHelper.attachToRecyclerView(mRvNewsList);

    }


    private void initView() {
        mBtnTop = (Button) findViewById(R.id.btn_top);
        mBtnPartRefresh = (Button) findViewById(R.id.btn_part_refresh);
        mBtnPartRefresh.setOnClickListener(this);
        mBtnTop.setOnClickListener(this);

        mNewsList = new ArrayList<NewsList.NewslistBean>();
        mAdapter = new NewsAdapter(MainActivity.this,mNewsList);
        //设置item点击,长按事件
        mAdapter.setOnItemClickLitener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("hahaha","you clicked item : position--" + position);
                NewsDetailActivity.actionStart(MainActivity.this,mNewsList.get(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d("hahaha","you long clicked item : position--" + position);
            }
        });
        mRvNewsList = (RecyclerView) findViewById(R.id.rv_news_list);
        mRvNewsList.setLayoutManager(new LinearLayoutManager(this));
        mRvNewsList.setAdapter(mAdapter);
        //mRvNewsList.setItemAnimator(new DefaultItemAnimator());

    }

    //加载新闻数据
    private void loadNewsData() {

        GsonRequest<NewsList> gsonRequest = new GsonRequest<NewsList>(
                "http://apis.baidu.com/txapi/keji/keji?num=20", NewsList.class,
                new Response.Listener<NewsList>() {
                    @Override
                    public void onResponse(NewsList newsList) {

                        Log.d("hahaha",newsList.getCode()+""+newsList.getMsg());


                        if(newsList.getNewslist() != null){
                            for(NewsList.NewslistBean bean : newsList.getNewslist()){
                                mNewsList.add(bean);
                            }
                            mAdapter.notifyDataSetChanged();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }
        ){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> map = new HashMap<String, String>();
                map.put("apikey","43bdf049898acc365d53311d40f5ad54");
                return map;
            }

        };
        //加入请求队列
        mQueue.add(gsonRequest);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_top:
                //平滑返回顶部
                mRvNewsList.smoothScrollToPosition(0);
                break;

            case R.id.btn_part_refresh:
                //局部刷新RecyclerView
                if(mNewsList.size() > 0){
                    mNewsList.get(0).setTitle("局部刷新后的标题" + mRefreshNum);
                    mNewsList.get(0).setCtime("局部刷新后的时间" + mRefreshNum);
                    mAdapter.notifyItemChanged(0);
                    mRefreshNum ++;
                }
                break;

            default:
                break;
        }
    }
}
