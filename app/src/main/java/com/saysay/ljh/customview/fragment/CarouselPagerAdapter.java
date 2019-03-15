package com.saysay.ljh.customview.fragment;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.saysay.ljh.customview.R;
import com.saysay.ljh.customview.model.Carousel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/31 0031.
 */
public class CarouselPagerAdapter extends PagerAdapter {


    private LayoutInflater mInflater;
    private Context mContext;
    private List<Carousel> carousels;

    public CarouselPagerAdapter(Context ctx) {
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
        carousels=new ArrayList<>();

    }
    public void setData(List<Carousel> data){
        if (null!=data){
            carousels=data;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return carousels.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = (ImageView) mInflater.inflate(R.layout.vp_item, container, false);
        imageView.setImageResource(R.mipmap.ic_launcher);
        Glide.with(mContext)
                .load(carousels.get(position).path)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(imageView);
        container.addView(imageView, 0);
        return imageView;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
