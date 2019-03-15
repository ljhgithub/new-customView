package com.saysay.ljh.customview.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.saysay.ljh.customview.R;
import com.saysay.ljh.customview.model.Album;
import com.saysay.ljh.customview.model.Carousel;
import com.saysay.ljh.customview.model.Entry;
import com.saysay.ljh.customview.model.Module;
import com.saysay.ljh.customview.model.Slider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljh on 2015/9/11 0011.
 */
public class LoopViewPageFragment extends Fragment {


    private RecyclerView rlv;
    private RlvAdapter adapter;
    private List<Module> modules;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modules = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        rlv = (RecyclerView) view.findViewById(R.id.rlv);
        rlv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rlv.setHasFixedSize(true);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 2);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case RlvAdapter.TYPE_SLIDER:
                    case RlvAdapter.TYPE_ENTRY:
                        return 2;
                    case RlvAdapter.TYPE_ALBUM:
                        return 1;
                    default:
                        return -1;
                }
            }
        });
        rlv.setLayoutManager(glm);
        adapter = new RlvAdapter(getActivity());
        loadData();
        rlv.setAdapter(adapter);
        return view;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void loadData() {
        Slider slider = new Slider();
        List<Carousel> carousels = new ArrayList<>();
        Carousel carousel1 = new Carousel();
        carousel1.path = "https://avatars.githubusercontent.com/u/2503423?v=3";
        carousels.add(carousel1);

        Carousel carousel2 = new Carousel();
        carousel2.path = "http://avatar.csdn.net/E/5/B/1_w250shini11.jpg";
        carousels.add(carousel2);

        Carousel carousel3 = new Carousel();
        carousel3.path = "http://www.iteye.com/upload/logo/user/350518/06c4b0c1-71d4-3501-9983-a2ef60d72a38.jpg?1290048277";
        carousels.add(carousel3);
        slider.carousels = carousels;
        modules.add(slider);

        Entry entry = new Entry();
        entry.title = "English";
        modules.add(entry);
        Album album;
        for (int i = 0; i < 4; i++) {
            album = new Album();
            if (i % 2 == 0) {
                album.location = 0;
            } else {
                album.location = 1;
            }
            album.pic = "http://androidkickstartr.com/img/header.png";
            modules.add(album);
        }

        Entry entry1 = new Entry();
        entry1.title = "Chinese";
        modules.add(entry1);
        Album album1;
        for (int i = 0; i < 10; i++) {
            album1 = new Album();
            if (i % 2 == 0) {
                album1.location = 0;
            } else {
                album1.location = 1;
            }
            album1.pic = "https://ss0.baidu.com/73F1bjeh1BF3odCf/it/u=2359638651,1999296670&fm=96&s=A033C6341430763955C6054D030050FA";
            modules.add(album1);
        }
        adapter.addData(modules);
    }

}
