package com.saysay.ljh.customview.widgets.loop;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DragLoopAdapter extends BaseDragLoopAdapter {
    List<Integer> mData = new ArrayList<>();
    List<Integer> color = new ArrayList<>();

    public void setData(List<Integer> data) {
        this.mData.clear();
        this.mData.addAll(data);
        color.add(Color.RED);
        color.add(Color.BLUE);
        color.add(Color.YELLOW);
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View onCreateView(int position, ViewGroup parent) {


        TextView ta = new TextView(parent.getContext());
        ta.setWidth(700);
        ta.setHeight(700);
        ta.setText("position" + position);
        ta.setBackgroundColor(color.get(position));
        return ta;


    }

    @Override
    void onBindView(int position, View convertView, ViewGroup parent) {

        TextView textView= (TextView) convertView;
        textView.setText("position="+position);
        Log.e("onBindView","onBindView "+position);
    }
}
