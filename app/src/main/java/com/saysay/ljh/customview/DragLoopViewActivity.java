package com.saysay.ljh.customview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.saysay.ljh.customview.widgets.loop.DragLoopAdapter;
import com.saysay.ljh.customview.widgets.loop.DragLoopView;

import java.util.ArrayList;
import java.util.List;

public class DragLoopViewActivity extends AppCompatActivity {

    DragLoopView pagerView;
    DragLoopAdapter loopAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drag_loop_activity);


        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fl);
        pagerView = (DragLoopView)findViewById(R.id.lpv);
        loopAdapter = new DragLoopAdapter();

        final List<Integer> d = new ArrayList<>();
        d.add(1);
        d.add(2);
        d.add(4);
        d.add(5);
        d.add(6);
        d.add(7);


        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                loopAdapter.notifyDataSetChanged();
                d.add(12);
                loopAdapter.setData(d);
            }
        });


        loopAdapter.setData(d);

        pagerView.setAdapter(loopAdapter);


    }
}