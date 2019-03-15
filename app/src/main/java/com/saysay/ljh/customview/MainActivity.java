package com.saysay.ljh.customview;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.saysay.ljh.customview.dialog.GuideDialog;
import com.saysay.ljh.customview.fragment.LoopViewPageFragment;
import com.saysay.ljh.customview.utils.DisplayUtil;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, GuideDialog.OptionListener {

    private Button btnLoop;
    private int statusBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLoop = (Button) findViewById(R.id.btn_loop);
        btnLoop.setOnClickListener(this);
        statusBarHeight = DisplayUtil.getStatusBarHeight(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        btnLoop.post(new Runnable() {
            @Override
            public void run() {
                Rect rect = new Rect();
                int[] pos = new int[2];
                btnLoop.getLocationInWindow(pos);
                btnLoop.getLocalVisibleRect(rect);
                int left = pos[0];
                int top = pos[1] - statusBarHeight;
                int height = rect.height();
                int width = rect.width();

                GuideDialog guideDialog = new GuideDialog(MainActivity.this);
                guideDialog.setRectLocation(left, top, left + width, top + height);
//                guideDialog.setGuide(left, top - 100+GuideDialog.PADDING, 100, 100, R.mipmap.ic_launcher);
                guideDialog.setOptionListener(MainActivity.this);
                guideDialog.show();

            }
        });

        findViewById(R.id.btn_drag_loop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DragLoopViewActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_loop:
                goLoopFragment();
                break;
        }
    }

    private void goLoopFragment() {
        LoopViewPageFragment loopViewPageFragment = new LoopViewPageFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, loopViewPageFragment, loopViewPageFragment.getClass().getName());
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void onOption() {
        goLoopFragment();
    }

    @Override
    public void onKnown() {

    }

    @Override
    public void onEmpty() {

    }
}
