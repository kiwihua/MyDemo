package com.ohoo.mydemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.ohoo.mydemo.R;
import com.ohoo.mydemo.bean.NewsList;

public class NewsDetailActivity extends AppCompatActivity {

    public static final String PARAM_NAME = "newsbean";

    private TextView mTvTime;
    private TextView mTvContent;
    private ImageView mIvImage;
    private ImageView mIvImage1;
    private ImageView mIvImage2;
    private ImageView mIvImage3;

    private NewsList.NewslistBean mNewSBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        init();
        bindData();
    }

    private void init() {
        mTvTime  = (TextView) findViewById(R.id.tv_time);
        mTvContent = (TextView) findViewById(R.id.tv_title);
        mIvImage = (ImageView) findViewById(R.id.iv_news_image);
        mIvImage1 = (ImageView) findViewById(R.id.iv_news_image1);
        mIvImage2 = (ImageView) findViewById(R.id.iv_news_image2);
        mIvImage3 = (ImageView) findViewById(R.id.iv_news_image3);

        mNewSBean = (NewsList.NewslistBean) getIntent().getSerializableExtra(PARAM_NAME);

    }

    private void bindData() {
        //绑定数据
        if(mNewSBean != null){
            mTvTime.setText(mNewSBean.getCtime());
            mTvContent.setText(mNewSBean.getTitle());
            //使用xml自定义的缩放动画效果
            Glide.with(this)
                    .load(mNewSBean.getPicUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .animate(R.animator.animator)
                    .into(mIvImage);
            //加载不同尺寸相同图片
            Glide.with(this)
                    .load(mNewSBean.getPicUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .animate( android.R.anim.slide_in_left ) //从左边滑入的动画效果
                    .priority(Priority.LOW)
                    .into(mIvImage1);
            //加载缩略图
            Glide.with(this)
                    .load(mNewSBean.getPicUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .priority(Priority.LOW)
                    .thumbnail(0.2f)
                    .into(mIvImage2);
            //简单使用Target回调得到Bitmap
            Target target = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    mIvImage3.setImageBitmap(resource);
                }
            };
            Glide.with(this)
                    .load(mNewSBean.getPicUrl())
                    .asBitmap()
                    .into(target);

        }
    }


    public static void actionStart(Context context,NewsList.NewslistBean bean){
        Intent intent = new Intent(context,NewsDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_NAME,bean);
        intent.putExtras(bundle);

        context.startActivity(intent);
    }

}
