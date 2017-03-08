package com.ohoo.mydemo.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ohoo.mydemo.R;
import com.ohoo.mydemo.bean.NewsList;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder>{

    private List<NewsList.NewslistBean> mNewslist;
    private LayoutInflater mInflater;
    private Context mContext;

    private OnItemClickListener mListener;

    public NewsAdapter(Context context, List<NewsList.NewslistBean> newslist){
        mNewslist = newslist;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建ViewHolder
        View itemView = mInflater.inflate(R.layout.news_item,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //拿到相应数据源
        final NewsList.NewslistBean newslistBean = mNewslist.get(position);
        //为视图绑定数据
        holder.tvTitle.setText(newslistBean.getTitle());
        holder.tvTime.setText(newslistBean.getCtime());
        Glide.with(mContext)
                .load(newslistBean.getPicUrl())
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivNewsImage);

        //如果设置了回调，则设置点击事件
        if (mListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mListener.onItemClick(holder.itemView,position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mListener.onItemLongClick(holder.itemView,position);
                    return true;
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mNewslist.size();
    }

    public void setOnItemClickLitener(OnItemClickListener listener){
        mListener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        TextView tvTime;
        ImageView ivNewsImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            ivNewsImage = (ImageView) itemView.findViewById(R.id.iv_news_image);
        }
    }

    //定义点击，长按事件监听接口
    public interface OnItemClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

}
