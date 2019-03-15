package com.saysay.ljh.customview.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.saysay.ljh.customview.R;
import com.saysay.ljh.customview.model.Album;
import com.saysay.ljh.customview.model.Module;
import com.saysay.ljh.customview.model.Slider;
import com.saysay.ljh.customview.widgets.LoopViewPager;
import com.saysay.ljh.customview.widgets.PointIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljh on 2015/9/11 0011.
 */
public class RlvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_SLIDER = 0;
    public static final int TYPE_ENTRY = 1;
    public static final int TYPE_ALBUM = 2;
    public static final int TYPE_UNKNOWN = -1;
    private List<Module> modules;
    private Context mContext;
    private LayoutInflater mInflater;

    public RlvAdapter(Context context) {
        this.mContext = context;
        modules = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }


    public void addData(List<Module> data) {
        if (null != data) {
            modules = data;
            notifyDataSetChanged();
        }
    }


    @Override
    public int getItemViewType(int position) {
        String typeStr = modules.get(position).type;
        int type;
        if (Module.SLIDER.equalsIgnoreCase(typeStr)) {
            type = TYPE_SLIDER;
        } else if (Module.ENTRY.equalsIgnoreCase(typeStr)) {
            type = TYPE_ENTRY;
        } else if (Module.ALBUM.equalsIgnoreCase(typeStr)) {
            type = TYPE_ALBUM;
        } else {
            type = TYPE_UNKNOWN;
        }

        return type;
    }

    @Override
    public int getItemCount() {
        return modules.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case TYPE_SLIDER:
                holder = new SliderHolder(mInflater.inflate(R.layout.rlv_slider_item, parent, false));
                break;
            case TYPE_ENTRY:
                holder = new EntryHolder(mInflater.inflate(R.layout.rlv_entry_item, parent, false));
                break;
            case TYPE_ALBUM:
                holder = new AlbumHolder(mInflater.inflate(R.layout.rlv_album_item, parent, false));
                break;
            default:
                holder = new UnknownHolder(mInflater.inflate(R.layout.empty_item, parent, false));
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_SLIDER: {
                SliderHolder adHolder = (SliderHolder) holder;
                Slider slider = (Slider) modules.get(position);
                adHolder.pagerAdapter.setData(slider.carousels);
                adHolder.pointIndicator.setIndicatorCount(slider.carousels.size());
                break;
            }
            case TYPE_ENTRY: {
                EntryHolder entryHolder = (EntryHolder) holder;
                break;
            }
            case TYPE_ALBUM: {
                AlbumHolder albumHolder = (AlbumHolder) holder;
                Album album = (Album) modules.get(position);
                Glide.with(mContext)
                        .load(album.pic)
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .crossFade()
                        .into(albumHolder.ivPic);
                if (album.location == 0) {
                    albumHolder.setPaddingRight(0);
                } else {
                    albumHolder.setPaddingRight(20);
                }
                break;
            }


        }
    }

    public class SliderHolder extends RecyclerView.ViewHolder {

        private LoopViewPager loopViewPager;
        public CarouselPagerAdapter pagerAdapter;
        public PointIndicator pointIndicator;

        public SliderHolder(View itemView) {
            super(itemView);
            loopViewPager = (LoopViewPager) itemView.findViewById(R.id.lvp_main);
            pointIndicator = (PointIndicator) itemView.findViewById(R.id.pi_point);
            pointIndicator.setIndicator(5,15, Color.RED,Color.WHITE);
            loopViewPager.setIndicator(pointIndicator);
            pagerAdapter = new CarouselPagerAdapter(mContext);
            loopViewPager.setAdapter(pagerAdapter);
            loopViewPager.startLoop(5000,5000);
        }
    }

    public class AlbumHolder extends RecyclerView.ViewHolder {

        public ImageView ivPic;

        public AlbumHolder(View itemView) {
            super(itemView);
            ivPic = (ImageView) itemView.findViewById(R.id.iv_image);
        }

        public void setPaddingRight(int paddingRight) {
            itemView.setPadding(20, 0, paddingRight, 20);
        }
    }

    public class EntryHolder extends RecyclerView.ViewHolder {

        public EntryHolder(View itemView) {
            super(itemView);
        }
    }

    public class UnknownHolder extends RecyclerView.ViewHolder {
        public UnknownHolder(View itemView) {
            super(itemView);
        }
    }
}
